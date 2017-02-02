package ds.test;

import ch.epfl.lamp.compiler.msil.util.Table;

import java.util.*;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SparkGBTModel  extends Model{

    protected List<Tree> treesModel = new ArrayList<Tree>();
    protected HashMap<Integer, Float> treeWeights = new HashMap<Integer, Float>();//每棵树都有一个变量
    private Set<Integer> availableFeatures;//spark gbt的特征下标是从0开始

    public SparkGBTModel(){
        this.modelName = "sparkGBT";
    }

    public SparkGBTModel(String modelName){
        this.modelName = modelName;
    }

    public SparkGBTModel(String modelName, String rawModel){
        this.modelName = modelName;
        parse(rawModel);
    }

    public Model parse(String rawModel) {
        /*if(rawModel == null || rawModel.length() == 0){
            return null;
        }
        Set<Integer> availableFeatureSet = new HashSet<Integer>();
        List<Tree> treesModel0 = new ArrayList<Tree>();
        HashMap<Integer, Float> treeWeights0 = new HashMap<Integer, Float>();
        String []treeArrStr = rawModel.trim().split("booster");
        if(treeArrStr.length > 1){
            //第0个是模型说明部分
            for(int index = 1; index < treeArrStr.length; index++){
                
            }
        }*/
        return null;
    }

    public double predict(Map<Integer, Double> point) {
        return 0;
    }

    public Set<Integer> getAvailableFeatureIndex() {
        return null;
    }

    public class Tree{
        public int nodeIndex;
        public int featureIndex;//leaf is -1
        public double value;//split value when branches, leaf value otherwise
        public int yes;//go left or right when satisfy condition
        public int no;//go left or right when not satisfy condition
        public int missing;//go left or right when missing value
        public Tree left;
        public Tree right;

        public Tree(int nodeIndex, int featureIndex, double value, int yes, int no, int missing, Tree left, Tree right){
            this.nodeIndex = nodeIndex;
            this.featureIndex = featureIndex;
            this.value = value;
            this.yes = yes;
            this.no = no;
            this.missing = missing;
            this.left = left;
            this.right = right;
        }
    }
}
