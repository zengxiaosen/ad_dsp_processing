package utils.model.xgboost;

import utils.sample.Sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/17.
 */
public class XgBoostModel implements Model {
    private ArrayList<Tree> model;

    public void loadModel(String rawModel){
        try{
            this.model = new ArrayList<Tree>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rawModel)));
            String line;
            ArrayList<String> treeContent = new ArrayList<String>();
            while((line = reader.readLine()) != null){
                if(line.startsWith("booster")){
                    //we 've a new tree
                    if(treeContent.size() > 0){
                        //push the old tree into forest
                        Tree tree = loadOneTree(treeContent);
                        if(tree != null){
                            model.add(tree);
                        }
                    }
                    //clear tree content
                    treeContent = new ArrayList<String>();
                } else{
                    //part of an old tree
                    String[] contents = line.split("\t");
                    for(String content : contents){
                        String s = content.trim();
                        if(s.length() > 0){
                            treeContent.add(s);
                        }
                    }
                }
            }
            reader.close();
            //last check
            if(treeContent.size() > 0){
                //push the last tree into forest
                Tree tree = loadOneTree(treeContent);
                if(tree != null){
                    model.add(tree);
                }
            }

            System.out.printf("reading trees done, there are %d trees\n", model.size());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double predict(Sample sample) {
        double value = 0.0;
        for(int i=0, len=model.size(); i< len; i++){
            double offset = model.get(i).predict(sample);
;            System.out.println(offset);
            value += offset;
        }
        return logistic(value);
    }

    private Tree loadOneTree(ArrayList<String> treeContent){
        if(treeContent.size() == 0){
            return  null;
        }
        return Tree.loadTreeFromString(treeContent);
    }

    private double logistic(double value) {
        return 1.0/(1.0 + Math.exp(-value));
    }
}
