package utils.model.xgboost;

/**
 * Created by Administrator on 2017/1/17.
 */
public class Node {
    public int nodeId;
    public int featureId;
    public double featureThreshold;
    public double predictValue;
    public int left;
    public int right;
    public int missing;

    private Node(int nodeId){
        this.nodeId = nodeId;
        this.featureId = -1;
        this.featureThreshold = 0;
        this.predictValue = 0;
        this.left = -1;
        this.right = -1;
        this.missing = -1;
    }

    public boolean isLeaf(){
        return featureId < 0;
    }

    public static Node parseNode(String line){
        //each string is like

        try{
            if(line.contains("leaf")){
                //it is a leaf , each String is like
                //63:leaf=0.439061, cover=510.653
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                Node node = new Node(id);
                double pValue = Double.parseDouble(parts[1].split(",")[0].split("=")[1]);
                node.predictValue = pValue;

                return node;
            }else{
                //it is not a leaf , each string is like
                //0:[f62<0.666677] yes=1,no=2,missing=2,gain=254317,cover=5.34334e+06
                String[] parts = line.split(" ");
                //parts[0] is like 0:[f62<0.666677]
                //we first find node id and feature id and feature threshold
                String[] idAndFeatureIds = parts[0].split(":");
                int id = Integer.parseInt(idAndFeatureIds[0]);
                Node node = new Node(id);
                String[] featureIdAndValue = idAndFeatureIds[1].substring(1,idAndFeatureIds[1].length() -1).split("<");
                int fid = Integer.parseInt(featureIdAndValue[0].substring(1));
                double fvalue = Double.parseDouble(featureIdAndValue[1]);
                node.featureId = fid;
                node.featureThreshold = fvalue;
                //parts[1] is like yes=1,no=2,missing=2,gain=254317,cover=5.34334e+06
                //we then find left child and right child
                String[] components = parts[1].split(",");
                int lid = Integer.parseInt(components[0].split("=")[1]);
                int rid = Integer.parseInt(components[1].split("=")[1]);
                int missing = Integer.parseInt(components[2].split("=")[1]);
                node.left = lid;
                node.right = rid;
                node.missing = missing;

                return node;
            }
        }catch (Exception e){
            return null;
        }
    }

    public String toString(){
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("nodeId=").append(nodeId);
        sb.append(", featureId=").append(featureId);
        sb.append(", featureThreshold=").append(featureThreshold);
        sb.append(", predictValue=").append(predictValue);
        sb.append(", left=").append(left);
        sb.append(", right=").append(right);
        sb.append(", missing=").append(missing);
        sb.append(", isLeaf=").append(isLeaf());
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args){
        /*String line1 = "63:leaf=0.439061,cover=510.653";
        Node node1 = Node.parseNode(line1);
        String s1 = node1.toString() == null ? " " : node1.toString();
        System.out.println(s1);*/

        String line2 = "0:[f62<0.666677] yes=1,no=2,missing=2,gain=254317,cover=5.34334e+06";
        Node node2 = Node.parseNode(line2);
        System.out.println(node2.toString());
    }
}
