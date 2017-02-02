package age.train;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import utils.sample.Sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

/**
 * Created by Administrator on 2017/1/17.
 */
//最终预测程序
public class MAgePredict extends Configured{
    private static final String separator = "\t";
    private static final double[] prior = {0.0008, 0.38, 0.68, 0.84, 0.91, 0.98, 1.0};
    private static final double[] balanceCoefficient = {0.001, 1.0, 1.8, 1.8, 1.8, 1.22, 0.001};
    private static final double[] bayesCoefficient = {0.0015, 0.3, 0.31, 0.17, 0.12, 0.09, 0.0085};

    private static final Random random = new Random(System.currentTimeMillis());

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println("args[] in run:" + Arrays.toString(args));
        String jobname = "MAgePredict";
        Job job = Job.getInstance(conf);
        job.setJarByClass(MAgePredict.class);
        job.setJobName(jobname);

        String inputpaths = conf.get("input");
        String outputPaths = conf.get("output");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(MAgePredict.MAgePredictMapper.class);
        job.setReducerClass(MAgePredict.MAgePredictReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        //采用文件压缩输出，减少磁盘消耗
        FileOutputFormat.setCompressOutput(job, true);
        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

        FileInputFormat.setInputPaths(job, inputpaths);
        FileOutputFormat.setOutputPath(job, new Path(outputPaths));

        return (job.waitForCompletion(true) ? 0 : 1);

    }

    public static class MAgePredictReducer extends Reducer<Text, Text, Text, Text>{
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Text i: values){
                context.write(key, i);
            }
        }
    }

    public static class MAgePredictMapper extends Mapper<LongWritable, Text, Text, Text>{
        private int num_of_splits = 7;
        private HashMap<Integer, double[]> age_offset_map;
        //private double[] channel_age_ratio;
        //private double[] ad_age_ratio;

        public void setup(Context context) throws IOException{
            String age_device_info_file = "age_device_info";
            String age_channel_info_file = "age_channel_info";
            String age_ad_info_file = "age_ad_info";
            //String gender_dsp_IDF_file = "age_dsp_info";
            loadAgeInfo(age_device_info_file, age_ad_info_file, age_channel_info_file);
            //String age_channel_total = "age_channel_total";
            //String age_channel_total = "age_channel_total";
            //loadAgeTotal(age_channel_total);
        }
        private void loadValFile(String file, int key, int value, HashMap<Integer, double[]> featureMap) throws IOException{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                String[] components = line.split(separator);
                if(components.length != 10){
                    continue;
                }
                double[] feature_age_info = new double[num_of_splits];
                for(int i = value; i< components.length; i++){
                    double v  = Double.parseDouble(components[i]);
                    feature_age_info[i - value] = v;
                }
                //feature, featureValue
                int feature = Integer.parseInt(components[key]);
                featureMap.put(feature, feature_age_info);
            }
            reader.close();
        }

        //load feature with its name and ratio
        private void loadAgeInfo(String file1, String file2, String file3) throws IOException{
            age_offset_map = new HashMap<Integer, double[]>();
            loadValFile(file1, 0, 3, age_offset_map);
            loadValFile(file2, 0, 3,age_offset_map);
            loadValFile(file3, 0, 3, age_offset_map);

        }

        private ArrayList<Integer> findTop(List<Map.Entry<Integer, Double>> toplist, int top){
            ArrayList<Integer> topArray = new ArrayList<Integer>();
            for(Map.Entry<Integer, Double> entry : toplist){
                topArray.add(entry.getKey());
            }
            if(top < topArray.size()){
                for(int i = top; i < topArray.size(); i++){
                    topArray.remove(i);
                    i--;
                }
            }
            return topArray;
        }

        private ArrayList<Integer> orderAgePrediction(HashMap<Integer, Double> voteTopMap, int top){
            //排序
            List<Map.Entry<Integer, Double>> toplist = new ArrayList<Map.Entry<Integer, Double>>();
            Collections.sort(toplist, new Comparator<Map.Entry<Integer, Double>>() {
                public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            ArrayList<Integer> t = findTop(toplist, top);
            return t;
        }

        private int getIndex(ArrayList<Integer> b, ArrayList<Integer> v, ArrayList<Integer> l){
            int idx = -1;
            if(b.get(0).equals(1)||b.get(0).equals(2)||b.get(0).equals(3)){
                for(int i=0; i< b.size(); i++){
                    if(v.contains(b.get(i))){
                        idx = b.get(i);
                        if((idx == 1) && (!v.get(0).equals(1))){
                            //idx = (int)makePredictionByPrior();
                            idx = l.get(0);
                        }else{
                            idx = v.get(0);
                        }
                    }
                    break;
                }
            }else{
                idx = b.get(0);
            }
            return idx + 1;
        }

        private double makePredictionByPrior(){
            double value = random.nextDouble();
            for(int i=0; i< prior.length -1; i++){
                if(value > prior[i] && value < prior[i + 1]){
                    return i+1;
                }
            }
            return prior.length;
        }

        //print score of each age
        private StringBuffer predictionByBayes(Sample sample){
            double[] votes = new double[num_of_splits];
            StringBuffer result = new StringBuffer();
            try{
                HashMap<Integer, Double> features = sample.features;
                for(Map.Entry<Integer, Double> entry : features.entrySet()){
                    int key = entry.getKey();
                    if(age_offset_map.containsKey(key)){
                        double[] feature_distribution = age_offset_map.get(key);
                        for(int i=0; i< num_of_splits; i++){
                            votes[i] += feature_distribution[i] * entry.getValue();
                        }
                    }
                }
                //P(C)
                for(int i=0; i< votes.length; i++){
                    votes[i] = votes[i] + Math.log(bayesCoefficient[i]);
                }

                for(double vote : votes){
                    result.append(vote);
                    result.append(" ");
                }
                return result;
            }catch (Exception e){
                result.append(makePredictionByPrior());
                return result;
            }
        }

        //score of each age
        private StringBuffer predictByVote(Sample sample){
            double[] votes = new double[num_of_splits];
            StringBuffer result = new StringBuffer();
            try{
                HashMap<Integer, Double> features = sample.features;
                for(Map.Entry<Integer, Double> entry : features.entrySet()){
                    int key = entry.getKey();
                    if(age_offset_map.containsKey(key)){
                        double[] feature_distribution = age_offset_map.get(key);
                        for(int i=0; i< num_of_splits; i++){
                            votes[i] += feature_distribution[i] * entry.getValue();
                        }
                    }
                }

                for(double vote : votes){
                    result.append(vote);
                    result.append(" ");
                }
                return result;
            }catch (Exception e){
                result.append(makePredictionByPrior());
                return result;
            }
        }

        ////P(F1|C)P(F2|C)P(F3|C)P(C)
        private double bayesPrediction(Sample sample){
            double[] votes = new double[num_of_splits];
            double[] bayes = new double[num_of_splits];
            double[] balance = new double[num_of_splits];
            HashMap<Integer, Double> voteToMap = new HashMap<Integer, Double>();
            HashMap<Integer, Double> bayesToMap = new HashMap<Integer, Double>();
            HashMap<Integer, Double> balanceMap = new HashMap<Integer, Double>();

            try{
                HashMap<Integer, Double> features = sample.features;
                //P(F1|C)P(F2|C)P(F3|C)
                for(Map.Entry<Integer, Double> entry : features.entrySet()){
                    int key = entry.getKey();
                    if(age_offset_map.containsKey(key)){
                        double[] feature_distribution = age_offset_map.get(key);
                        for(int i=0; i< num_of_splits; i++){
                            votes[i] += feature_distribution[i] * entry.getValue();
                        }
                    }
                }
                // multiply by P(C)
                for(int i=0; i< votes.length; i++){
                    bayes[i] = votes[i] + Math.log(bayesCoefficient[i]);
                }
                // multiply by Random(C) to balance
                for(int i=0; i< votes.length; i++){
                    balance[i] = votes[i] + Math.log(balanceCoefficient[i]);
                }
                //copy votes to hashmap
                for(int i=0; i< votes.length; i++){
                    voteToMap.put(i, votes[i]);
                }
                //copy bayes to hashmap
                for(int i=0; i< bayes.length; i++){
                    bayesToMap.put(i, bayes[i]);
                }
                //copy balance to hashmap
                for (int i =0; i< balance.length; i++){
                    balanceMap.put(i,balance[i]);
                }

                ArrayList<Integer> voteP = orderAgePrediction(voteToMap, 3);
                ArrayList<Integer> bayesP = orderAgePrediction(bayesToMap, 7);
                ArrayList<Integer> balanceP = orderAgePrediction(balanceMap, 7);

                int idx = getIndex(bayesP, voteP, balanceP);
                return (double) idx;
            }catch (Exception e){
                return makePredictionByPrior();
            }
        }

    }
}
