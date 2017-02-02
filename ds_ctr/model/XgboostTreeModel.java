package model;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Administrator on 2016/12/11.
 */
public class XgboostTreeModel extends Model {
    protected List<Tree> treesModel = new ArrayList<Tree>();
    private int[] availableFeatures;

    public XgboostTreeModel(String rawModel){
        parse(rawModel);
    }

    public int[] getAvailableFeatureIndex() {
        return availableFeatures;
    }

    /**
     booster[0]:
     0:leaf=-1,cover=232896
     booster[55]:
     0:[f460<2.00001] yes=1,no=2,missing=2,gain=2.6407,cover=3171.42
     1:leaf=-0.773756,cover=3.40443
     2:[f499<2.00001] yes=3,no=4,missing=4,gain=7.30285,cover=3168.01
     3:[f572<0.0073521] yes=5,no=6,missing=5,gain=2.52225,cover=7.69159
     5:[f181<2.00001] yes=9,no=10,missing=10,gain=0.371298,cover=4.1853
     9:leaf=1,cover=2.94559
     10:leaf=0.217525,cover=1.23971
     6:[f455<2.00001] yes=11,no=12,missing=12,gain=1.73268,cover=3.50629
     11:leaf=0.650858,cover=1.75463
     12:leaf=-0.474161,cover=1.75167
     4:[f480<0.00264926] yes=7,no=8,missing=7,gain=3.21511,cover=3160.32
     7:[f974<2.00001] yes=13,no=14,missing=14,gain=2.88437,cover=3146.06
     13:leaf=-0.366439,cover=20.2434
     14:leaf=0.00329009,cover=3125.82
     8:[f152<0.00578699] yes=15,no=16,missing=15,gain=1.83736,cover=14.2601
     15:leaf=0.117939,cover=3.77123
     16:leaf=-0.659074,cover=10.4889
     * @param rawModel
     * @return
     */
    @Override
    public Model parse(String rawModel) {
        if(rawModel == null || rawModel.length() == 0){
            return null;
        }
        Set<Integer> availableFeatureSet = new HashSet<Integer>();
        String []treeArrStr = rawModel.trim().split("booster\\[\\d+\\]:");
        for(String treeStr : treeArrStr){
            Tree tree = parseTree(treeStr.trim(), availableFeatureSet);
            if(null != tree){
                treesModel.add(tree);
            }
        }

        availableFeatures = new int[availableFeatureSet.size()];
        int index = 0;
        Iterator<Integer>featureSet = availableFeatureSet.iterator();
        while(featureSet.hasNext()){
            Integer featureIndex = featureSet.next();
            availableFeatures[index++] = featureIndex.intValue();
        }
        return this;
    }

    private Tree parseTree(String treeStr, Set<Integer> availableFeatureSet){
        if(treeStr == null || treeStr.trim().contains("leaf") == false){
            return null;
        }
        return parseTreeRecursion(treeStr, availableFeatureSet);
    }

    private Tree parseTreeRecursion(String treeStr, Set<Integer> availableFeatureSet){
        String nodeStr = treeStr.trim().split("\n")[0];
        if(nodeStr.contains("leaf")){
            String value = nodeStr.split(",")[0].split("leaf=")[1];
            String nodeIndex = nodeStr.split(",")[0].split(":")[0].trim();
            return new Tree(Integer.parseInt(nodeIndex), -1, Double.parseDouble(value), -1, -1, -1, null, null);
        }
        //String nodeStr = treeStr.split("\n")[0];
        //System.out.println("nodeStr:\n" + nodeStr);
        String featureValues = nodeStr.split("\\]")[0];//featureValues=[f460<2.00001
        String []featuresValuesArr = featureValues.split("<");
        int featureIndex = Integer.parseInt(featuresValuesArr[0].substring(featuresValuesArr[0].indexOf("[f") + 2));
        availableFeatureSet.add(featureIndex);
        String nodeIndex = featuresValuesArr[0].split(":")[0].trim();

        String[] yesNoMissingStrArr = nodeStr.split("\\]")[1].trim().split(",");//" yes=9,no=10,missing=10,gain=1838.03,cover=8192.26"
        int yes = Integer.parseInt(yesNoMissingStrArr[0].split("=")[1]);
        int no = Integer.parseInt(yesNoMissingStrArr[1].split("=")[1]);
        int missing = Integer.parseInt(yesNoMissingStrArr[2].split("=")[1]);
        Tree tree = new Tree(Integer.parseInt(nodeIndex), featureIndex, Double.parseDouble(featuresValuesArr[1]),
                yes, no, missing, null, null);

        String sonStr = treeStr.substring(nodeStr.length() + 1);
        String tmpSonStr = sonStr.split("\n")[0];//tab num
        String startedTab = tmpSonStr.substring(0, tmpSonStr.indexOf(tmpSonStr.trim().charAt(0)));
        String leftRightSplitStr = "\n" + startedTab + "\\d+:";
        //System.out.println("leftRightSplitStr:" + leftRightSplitStr);

        //System.out.println("sonStr.trim():\n" + sonStr.trim());
        String []leftRightStr = sonStr.trim().split(leftRightSplitStr);//need trim() before split
        //System.out.println("***********leftRightStr[0]************");
        //System.out.println("left:\n" + leftRightStr[0]);
        //System.out.println("***********leftRightStr[1]************");
        //System.out.println("right:\n" + sonStr.substring(sonStr.indexOf(leftRightStr[1]) - 5));
        tree.left = parseTreeRecursion(leftRightStr[0].trim(), availableFeatureSet);
        tree.right = parseTreeRecursion(sonStr.trim().substring(leftRightStr[0].length() + 2).trim(), availableFeatureSet);//leftRightSplitStr.substring(1) +
        return tree;
    }

    //point: [1.0, 0, 0, 1.0, 1.0, 0.56....] not include label
    @Override
    public double predict(double[] point, double label) {
        double score = 0.0;
        for(Tree tree: treesModel){
            score += predict(tree, point);
        }
        score = 1.0/(1+Math.exp(-score));
        return score;
    }

    private double predict(Tree tree, double []point){
        //leaf
        if(tree.featureIndex == -1 || tree.left == null || tree.right == null){
            return tree.value;
        }
        if(point.length >= tree.featureIndex){
            double featureValue = point[tree.featureIndex - 1];//start from 1
            if(featureValue < 0.0){//missing
                if(tree.missing == tree.no){
                    return  predict(tree.right, point);
                }else if(tree.missing == tree.yes){
                    return predict(tree.left, point);
                }
            }else if(featureValue < tree.value && tree.yes < tree.no){
                return predict(tree.left, point);
            }else if(featureValue >= tree.value && tree.yes < tree.no){
                return predict(tree.right, point);
            }
            /*if(featureValue == 0.0 && tree.value == 2.00001){ //discrete feature, go right tree when 0
                return predict(tree.right, point);
            }else if(featureValue < tree.value){
                return predict(tree.left, point);
            }else if(featureValue >= tree.value){
                return predict(tree.right, point);
            }*/
        }
        return 0.0;
    }

    public String predictLeafFeatures(double[] point, double label){
        StringBuffer leafFeaturesStr = new StringBuffer();
        leafFeaturesStr.append(label);
        leafFeaturesStr.append(" ");
        int index = 1;
        for(Tree tree: treesModel.subList(1,treesModel.size())){//leave first tree out
            leafFeaturesStr.append((index + predictLeafFeatures(tree, point)) + ":1.0");
            leafFeaturesStr.append(" ");
            index += 31;
        }
        return leafFeaturesStr.toString().trim();
    }

    private int predictLeafFeatures(Tree tree, double []point){
        //leaf
        if(tree.featureIndex == -1 || tree.left == null || tree.right == null){
            return tree.nodeIndex;
        }
        if(point.length >= tree.featureIndex){
            double featureValue = point[tree.featureIndex - 1];//start from 1
            if(featureValue == 0.0 && tree.value == 2.00001){ //discrete feature, go right tree when 0
                return predictLeafFeatures(tree.right, point);
            }else if(featureValue < tree.value){
                return predictLeafFeatures(tree.left, point);
            }else if(featureValue >= tree.value){
                return predictLeafFeatures(tree.right, point);
            }
        }
        return 0;
    }


    public class Tree{
        public int nodeIndex;//
        public int featureIndex;//leaf is -1
        public double value;//spilt value when branches, leaf value otherwise
        public int yes; //go left or right when satisfy condition
        public int no; //go left or right when do not satisfy condition
        public int missing; //go left or right when missing value
        public Tree left;
        public Tree right;

        public Tree(int nodeIndex, int featureIndex, double value,
                    int yes, int no, int missing, Tree left, Tree right){
            this.nodeIndex = nodeIndex;
            this.featureIndex = featureIndex;
            this.value = value;
            this.yes = yes;
            this.no = no;
            this.missing = no;
            this.left = left;
            this.right = right;
        }
    }

    public static Model getModel(String modelPath) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(modelPath),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = reader.readLine()) != null){
            sb.append(line);
            sb.append("\n");
        }
        String rawModel = sb.toString();
        reader.close();
        Model model = new XgboostTreeModel(rawModel);
        return model;
    }

    public static void test(String modelPath, String dataPath)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(modelPath),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = reader.readLine()) != null){
            sb.append(line);
            sb.append("\n");
        }
        String rawModel = sb.toString();
        reader.close();
        Model model = new XgboostTreeModel(rawModel);

        /*reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath),"UTF-8"), 16*1024*1024);
        line = null;
        //0.0 6:1.0 91:1.0 152:0.002120222336828835 196:1.0 223:0.009521032584392684 224:1.0 228:1.0 231:1.0 248:1.0 267:1.0 274:0.002120222336828835 376:1.0 420:1.0

        while ((line = reader.readLine()) != null){
            String []lineArr = line.split(" ");
            double label = Double.parseDouble(lineArr[0]);
            double []points = new double[5481];
            for(int index = 1; index < lineArr.length; index++){
                String []arr = lineArr[index].split(":");
                points[Integer.parseInt(arr[0]) - 1] = Double.parseDouble(arr[1]);
            }
            double score = model.predict(points, label);
            System.out.println("(" + score + "," + label + ")");
        }*/
    }

    public static void main(String []args){
        /*String tmpSonStr  = "\t2:[f518<2.00001] yes=5,no=6,missing=6,gain=48.4849,cover=38260.9";
        System.out.println(tmpSonStr.indexOf(tmpSonStr.trim().charAt(0)));
        String startedTab = tmpSonStr.substring(0, tmpSonStr.indexOf(tmpSonStr.trim().charAt(0)));
        System.out.println("***" + startedTab + "***");
        System.out.println("***\t***");*/

        /*List<String> a = new ArrayList<>(3);
        a.add("5");
        a.add("4");
        a.add("6");
        System.out.println(a.subList(1,a.size()).get(1));*/

        /*if(args.length < 2){
            System.out.println("need args: modelPath, dataPath");
            System.exit(-1);
        }
        try {
            test(args[0], args[1]);
            //test("dump.raw.txt", "");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String rawModel = "booster[0]:\n" +
                "     0:leaf=-1,cover=232896\n" +
                "     booster[55]:\n" +
                "     0:[f460<2.00001] yes=1,no=2,missing=2,gain=2.6407,cover=3171.42\n" +
                "     \t1:leaf=-0.773756,cover=3.40443\n" +
                "     \t2:[f499<2.00001] yes=3,no=4,missing=4,gain=7.30285,cover=3168.01\n" +
                "     \t\t3:[f572<0.0073521] yes=5,no=6,missing=5,gain=2.52225,cover=7.69159\n" +
                "     \t\t\t5:[f181<2.00001] yes=9,no=10,missing=10,gain=0.371298,cover=4.1853\n" +
                "     \t\t\t\t9:leaf=1,cover=2.94559\n" +
                "     \t\t\t\t10:leaf=0.217525,cover=1.23971\n" +
                "     \t\t\t6:[f455<2.00001] yes=11,no=12,missing=12,gain=1.73268,cover=3.50629\n" +
                "     \t\t\t\t11:leaf=0.650858,cover=1.75463\n" +
                "     \t\t\t\t12:leaf=-0.474161,cover=1.75167\n" +
                "     \t\t4:[f480<0.00264926] yes=7,no=8,missing=7,gain=3.21511,cover=3160.32\n" +
                "     \t\t\t7:[f974<2.00001] yes=13,no=14,missing=14,gain=2.88437,cover=3146.06\n" +
                "     \t\t\t\t13:leaf=-0.366439,cover=20.2434\n" +
                "     \t\t\t\t14:leaf=0.00329009,cover=3125.82\n" +
                "     \t\t\t8:[f152<0.00578699] yes=15,no=16,missing=15,gain=1.83736,cover=14.2601\n" +
                "     \t\t\t\t15:leaf=0.117939,cover=3.77123\n" +
                "     \t\t\t\t16:leaf=-0.659074,cover=10.4889";
        Model model = new XgboostTreeModel(rawModel);

        /*System.out.println("**********************");
        System.out.println(rawModel.split("booster")[0]);
        System.out.println("**********************");
        System.out.println(rawModel.split("booster")[1]);
        System.out.println("**********************");
        System.out.println(rawModel.split("booster")[2]);
        System.out.println("**********************");*/
        /*System.out.println("**********************");
        System.out.println(treeArrStr[0].trim());
        System.out.println("**********************");
        System.out.println(treeArrStr[1].trim());
        System.out.println("**********************");
        System.out.println(treeArrStr[2].trim());
        System.out.println("**********************");*/
    }
}
