package utils.model.xgboost;

import utils.sample.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/17.
 */
public class Tree {
    private Node root;
    private HashMap<Integer, Node> nodeMap;

    public static Tree loadTreeFromString(ArrayList<String> treeContent){
        try{
            Tree tree = new Tree();
            tree.nodeMap = new HashMap<Integer, Node>();

            String rootLine = treeContent.get(0);
            System.out.println(rootLine);
            Node rootNode = Node.parseNode(rootLine);
            System.out.println(rootNode.toString());
            tree.root = rootNode;

            for(int i=1, len = treeContent.size(); i< len; i++){
                Node node = Node.parseNode(treeContent.get(i));
                tree.nodeMap.put(node.nodeId, node);
            }

            if(tree.checkData()){
                return tree;
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

    private boolean checkData(){
        //check whether all the data are in the map
        for(Map.Entry<Integer, Node> entry : nodeMap.entrySet()){
            Node node = entry.getValue();
            if(node.isLeaf()){
                continue;
            }
            if(!nodeMap.containsKey(node.left) || nodeMap.containsKey(node.right)){
                return false;
            }
        }
        return true;
    }

    public double predict(Sample sample){
        return predictByNode(root, sample);
    }

    private double predictByNode(Node aNode, Sample sample){
        if(aNode.isLeaf()){
            return aNode.predictValue;
        }
        if(sample.features.containsKey(aNode.featureId)){
            //命中特征
            if(sample.features.get(aNode.featureId) < aNode.featureThreshold){
                //特征值小于阈值
                return predictByNode(nodeMap.get(aNode.left), sample);
            }else{
                //特征值大于阈值
                return predictByNode(nodeMap.get(aNode.right), sample);
            }
        }else{
            //未命中特征
            return predictByNode(nodeMap.get(aNode.missing), sample);
        }
    }
}
