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
import utils.tools.DeviceConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/1/15.
 */
public class AgeTrainingSampleMaker extends Configured{
    //生成年龄预估训练样本
    private static final String seperator = "\t";
    private static final String hive_separator = "\\001";
    private static final String MissingDataSymbol = "-1";
    private static final int num_age_splits = 7;

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println("args[] in run:" + Arrays.toString(args));
        String jobname = "AgeTrainingSampleMaker";
        Job job = Job.getInstance(conf);
        job.setJarByClass(AgeTrainingSampleMaker.class);
        job.setJobName(jobname);

        String inputpaths = conf.get("input");
        String outputPaths = conf.get("output");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(AgeTrainingSampleMaker.AgeTrainingSampleMakerMapper.class);
        job.setReducerClass(AgeTrainingSampleMaker.AgeTrainingSampleMakeReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        //采用文件压缩输出，减少磁盘消耗
        FileOutputFormat.setCompressOutput(job, true);
        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

        FileInputFormat.setInputPaths(job, inputpaths);
        FileOutputFormat.setOutputPath(job, new Path(outputPaths));

        return (job.waitForCompletion(true) ? 0 : 1);

    }

    public static class AgeTrainingSampleMakerMapper extends Mapper<LongWritable, Text, Text, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.getCounter(Counters.TOTAL_LINE).increment(1L);
            if(value == null || value.toString() == null){
                context.getCounter(Counters.INVALID_LINE).increment(1L);
                return;
            }
            String line = value.toString();
            String filePath = ((FileSplit)context.getInputSplit()).getPath().toString();
            try{
                if(filePath.contains("ali_merge")){
                    //ali data
                    String[] components = line.split(seperator);
                    if(components.length != 3){
                        context.getCounter(Counters.INVALID_LINE).increment(1L);
                        return;
                    }
                    String id = components[0];
                    String age= components[2];
                    if(age.equals("0")){
                        context.getCounter(Counters.ALI_UNKNOWN).increment(1L);
                        return;
                    }
                    context.write(new Text(id), new Text("ali:" + age));
                    context.getCounter(Counters.VALID_LINE).increment(1L);
                }else{
                    String[] components = line.split(hive_separator);
                    String cookie = components[0];
                    int deviceCode = DeviceConstants.checkDeviceType(cookie);
                    if(deviceCode == DeviceConstants.UNKNOWN){
                        context.getCounter(Counters.UNKNOWN_DEVICE).increment(1L);
                        return;
                    }
                    //获取时间+频道
                    String channel = components[2].length() < 1 ? MissingDataSymbol : components[2];
                    String time_channel = components[1] + "_" + channel;
                    context.write(new Text(cookie), new Text("channel" + seperator + time_channel + seperator + components[3]));
                    context.getCounter(Counters.VALID_LINE).increment(1L);
                }
            }catch (Exception e){
                e.printStackTrace();
                context.getCounter(Counters.INVALID_LINE).increment(1L);
                return;
            }
        }

        static enum Counters{
            ALI_UNKNOWN,
            VALID_LINE,
            INVALID_LINE,
            UNKNOWN_DEVICE,
            TOTAL_LINE
        }

    }



    public static class AgeTrainingSampleMakeReducer extends Reducer<Text, Text, Text, Text>{
        private double totalUserCount;
        private HashMap<String, Double> channel_idf_map;
        private HashMap<String, double[]> age_offset_map;
        private HashMap<String, Integer> featureIndexMap;

        public void setup(Context context) throws IOException{
            String totalUserCountFile = "unique_users";//统计用户人数
            totalUserCount = loadTotalUserCount(totalUserCountFile);

            //将hour_channel映射为feature id
            String gender_features_ids_file = "age_features_ids";
            loadFeatureIds(gender_features_ids_file);

            String gender_feature_stats_file = "age_feature_stats";
            loadGenderStats(gender_feature_stats_file);

            String gender_feature_count_file = "age_feature_count";
            loadGenderCounts(gender_feature_count_file, totalUserCount);

        }



        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            double totalCnt = 0.0;
            TreeMap<Integer, Double> featureMap = new TreeMap<Integer, Double>();
            HashMap<String, Double> dataMap = new HashMap<String, Double>();
            String gender = null;
            for(Text text : values){
                String value = text.toString();
                if(value.startsWith("ali")){
                    gender = value.split(":")[1];
                }else if(value.startsWith("channel")){
                    String[] pair = value.split(":")[1].split(seperator);//hour_channel, count
                    String hour_channel = pair[0];
                    double cnt = Double.parseDouble(pair[1]);
                    dataMap.put(hour_channel, cnt);
                    totalCnt += cnt;
                }
            }
            //output see more than 5 features
            if (gender != null && dataMap.size() >= 5 * num_age_splits) {
                //转化为feature
                for(Map.Entry<String, Double> entry : dataMap.entrySet()){
                    String entry_key = entry.getKey();

                    double idf = channel_idf_map.containsKey(entry_key) ? channel_idf_map.get(entry_key) : 1.0;
                    double tf_idf = entry.getValue() / totalCnt * idf;
                    double[] offset_distribution = age_offset_map.containsKey(entry_key) ? age_offset_map.get(entry_key) : new double[num_age_splits];
                    double[] featureValue = new double[num_age_splits];
                    for(int i=0; i<featureValue.length; i++){
                        featureValue[i] = offset_distribution[i] * tf_idf;
                    }

                    int featureIndex = featureIndexMap.containsKey(entry_key) ? featureIndexMap.get(entry_key) : -1;
                    if(featureIndex < 0){
                        continue;
                    }
                    for(int i=0; i<featureValue.length; i++){
                        featureMap.put((featureIndex-1) * num_age_splits + i + 1, featureValue[i]);
                    }
                }
                //normalize
                double sum = 0.0;
                for(Map.Entry<Integer, Double> entry : featureMap.entrySet()){
                    sum += entry.getValue();
                }
                for(Map.Entry<Integer, Double> entry : featureMap.entrySet()){
                    entry.setValue(entry.getValue() / sum);
                }
                //write down
                StringBuilder sbd = new StringBuilder();
                for(Map.Entry<Integer, Double> entry : featureMap.entrySet()){
                    sbd.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
                }
                if(sbd.length() > 0){
                    sbd.deleteCharAt(sbd.length() - 1);
                }
                context.write(new Text(gender), new Text(sbd.toString()));
                context.getCounter(Counters.VALID_REDUCE).increment(1L);
            }
        }

        static enum Counters{
            VALID_REDUCE;
        }


        //load channel view count
        private void loadGenderCounts(String file, double totalUserCount) throws IOException{
            channel_idf_map = new HashMap<String, Double>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null){
                String[] parts = line.split(seperator);
                String key = parts[0];//hour_channel
                double userCount = Double.parseDouble(parts[1]);
                double idf = Math.log(totalUserCount / userCount + 1e-12);
                channel_idf_map.put(key, idf);
            }
            reader.close();
        }


        //load age offset map
        private void loadGenderStats(String file) throws IOException{
            double[] all_age_distribution = new double[7];
            age_offset_map = new HashMap<String, double[]>();
            //hour_channel, age1,age2,age3...age7
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                String[] components = line.split(seperator);
                String key = components[0];//hour_channel
                double[] age_distribution = new double[num_age_splits];
                double total = 0.0;
                for(int i=1; i< components.length; i++){
                    double value = Double.parseDouble(components[i]);
                    age_distribution[i-1] = value;
                    all_age_distribution[i-1] += value;
                    total += value;
                }
                for(int i=0; i< age_distribution.length; i++){
                    age_distribution[i] /= total;
                }
                age_offset_map.put(key, age_distribution);
            }
            reader.close();
            double all_total = 0.0;
            for(double value: all_age_distribution){
                all_total += value;
            }
            for(int i=0; i<all_age_distribution.length; i++){
                all_age_distribution[i] /= all_total;
            }
            for(Map.Entry<String, double[]> entry : age_offset_map.entrySet()){
                double[] offset_distribution = entry.getValue();
                for(int i=0; i< offset_distribution.length; i++){
                    offset_distribution[i] = offset_distribution[i] / all_age_distribution[i];
                }
                entry.setValue(offset_distribution);
            }
        }



        //load features with its name and id
        private void loadFeatureIds(String file) throws IOException{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            featureIndexMap = new HashMap<String, Integer>();
            String line;
            while((line = reader.readLine()) != null){
                String[] components = line.split(seperator);
                if(components.length != 2){
                    continue;
                }
                //hour_channel, id
                String feature = components[0];
                int idx = Integer.parseInt(components[1]);
                featureIndexMap.put(feature, idx);
            }
            reader.close();
        }

        //load total user count
        private double loadTotalUserCount(String file) throws IOException{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            return Double.parseDouble(line.trim());
        }




    }

}
