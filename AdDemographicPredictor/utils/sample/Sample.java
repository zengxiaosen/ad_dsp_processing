package utils.sample;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/17.
 */
//svmlight like data to sample
public class Sample {

    public double label;
    public double prediction;
    public HashMap<Integer, Double> features;

    private Sample(double label){
        this.label = label;
        this.prediction = 0.0;
        this.features = new HashMap<Integer, Double>();
    }

    public int size(){
        return features.size();
    }

    public static Sample loadSampleFromString(String str){
        try{
            String[] parts = str.split(" ");
            double label = Double.parseDouble(parts[0]);
            Sample sample = new Sample(label);
            if(parts.length > 1){
                for(int i=1; i< parts.length; i++){
                    String[] pair = parts[i].split(";");
                    sample.features.put(Integer.parseInt(pair[0]), Double.parseDouble(pair[1]));
                }
            }
            return sample;
        }catch (Exception e){
            return null;
        }
    }

}
