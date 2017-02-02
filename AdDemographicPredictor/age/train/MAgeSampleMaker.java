package age.train;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import utils.tools.DeviceConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/1/19.
 */
//生成年龄预估的全部样本
public class MAgeSampleMaker {
    private static final String separator = "\t";
    private static final String MissingDataSymbol = "-1";

    public boolean run(){
        return false;
    }

    public static class MAgeSampleMakerReducer extends Reducer<Text, Text, Text, Text>{
        private HashMap<String, Integer> featureIndexMap;
        public void setup(Context context) throws IOException{
            String age_device_ids_file = "age_device_info";
            String age_channel_ids_file = "age_channel_info";
            String age_ad_ids_file = "age_ad_info";
            loadFeatureIds(age_device_ids_file, age_ad_ids_file, age_channel_ids_file);
        }

        private void loadIdxFiles(String file, int key, int value, HashMap<String, Integer> featureMap) throws IOException{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                String[] components = line.split(separator);
                if(components.length != 10){
                    continue;
                }
                //feature, featureID
                int idx = Integer.parseInt(components[value]);
                String feature = components[key];
                featureMap.put(feature, idx);
            }
            reader.close();
        }

        //load featues with its name and id
        private void loadFeatureIds(String file1, String file2, String file3) throws IOException{
            featureIndexMap = new HashMap<String, Integer>();
            loadIdxFiles(file1, 1, 0, featureIndexMap);
            loadIdxFiles(file2, 1, 0, featureIndexMap);
            loadIdxFiles(file3, 1, 0, featureIndexMap);
        }

        //feature key:value (value = tf * idf * offset)
        private void featureMaker(HashMap<String, Double> dataMap, TreeMap<Integer, Double> featureMap){
            if(dataMap.size() > 0){
                for(Map.Entry<String, Double> entry : dataMap.entrySet()){
                    String entry_key = entry.getKey();
                    double featureValue = entry.getValue();
                    int featureIndex = featureIndexMap.containsKey(entry_key) ? featureIndexMap.get(entry_key) : -1;
                    if(featureIndex < 0){
                        continue;
                    }
                    featureMap.put(featureIndex, featureValue);
                }
            }
        }
        //deviceFeature key:value (value = offset)
        private void deviceMaker(String device, StringBuilder deviceValue){
            if (device != null){
                double featureValue = featureIndexMap.containsKey(device) ? 1.0 : 0.0;
                int featureIndex = featureIndexMap.containsKey(device) ? featureIndexMap.get(device) : -1;
                if (featureIndex > 0) {
                    deviceValue.append(String.valueOf(featureIndex)).append(":").append(String.valueOf(featureValue));
                }
            }
        }

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            try{
                TreeMap<Integer, Double> featureMap = new TreeMap<Integer, Double>();
                TreeMap<Integer, Double> brandfeatureMap = new TreeMap<Integer, Double>();
                //TreeMap<Integer, Double> dspfeatureMap = new TreeMap<Integer, Double>();
                HashMap<String, Double> dataMap = new HashMap<String, Double>();
                HashMap<String, Double> branddataMap = new HashMap<String, Double>();
                //HashMap<String, Double> dspdataMap = new HashMap<String, Double>();
                StringBuilder modelValue = new StringBuilder();
                StringBuilder operatorValue = new StringBuilder();
                String gender = "-1";
                String model = null;
                String operator = null;
                for(Text text : values){
                    String value = text.toString();
                    if(value.startsWith("ali")){
                        gender = value.split(":")[1];
                    }else if(value.startsWith("channel")){
                        String[] pair = value.split(":")[1].split(separator); // hour_channel, count
                        String hour_channel = pair[0];
                        double vv = Double.parseDouble(pair[1]);
                        dataMap.put(hour_channel, vv);
                    }else if(value.startsWith("brand")){
                        String[] pair = value.split(":")[1].split(separator);//ad, score
                        String brand = pair[0];
                        double score = Double.parseDouble(pair[1]);
                        if(branddataMap.containsKey(brand)){
                            double val = branddataMap.get(brand);
                            branddataMap.put(brand, val + score);
                        }else {
                            branddataMap.put(brand, score);
                        }
                    }else if(value.startsWith("model")){
                        model = value.split(":")[1];//brand_model
                    }else if(value.startsWith("operator")){
                        operator = value.split(":")[1];//operator
                    }
                }

                //device feature
                deviceMaker(model, modelValue); //model
                deviceMaker(operator, operatorValue);//operator
                //channel ad dsp feature
                featureMaker(branddataMap, brandfeatureMap);//brand
                featureMaker(dataMap, featureMap);//perioud_channel
                //featureMaker(dspdataMap, dspfeatureMap, totalDSPScore); //DSP

                //write down
                StringBuilder sbd = new StringBuilder();
                if(operatorValue.length() > 0){
                    sbd.append(" ").append(operatorValue.toString());
                    context.getCounter(Counters.HAVE_OPERATOR).increment(1L);
                }

                if(modelValue.length() > 0){
                    sbd.append(" ").append(modelValue.toString());
                    context.getCounter(Counters.HAVE_MODEL).increment(1L);
                }

                if(modelValue.length() > 0){
                    sbd.append(" ").append(modelValue.toString());
                    context.getCounter(Counters.HAVE_MODEL).increment(1L);
                }

                //                for (Map.Entry<Integer, Double> dspentry : dspfeatureMap.entrySet()) {
                //                    sbd.append(" ").append(dspentry.getKey()).append(":").append(dspentry.getValue());
                //                    context.getCounter(Counters.HAVE_DSP_DATA).increment(1L);
                //                }
                for(Map.Entry<Integer, Double> brandentry : brandfeatureMap.entrySet()){
                    sbd.append(" ").append(brandentry.getKey()).append(":").append(brandentry.getValue());
                    context.getCounter(Counters.HAVE_BRAND_DATA).increment(1L);
                }
                for (Map.Entry<Integer, Double> entry : featureMap.entrySet()) {
                    sbd.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
                    context.getCounter(Counters.HAVE_CHANNEL_DATA).increment(1L);
                }
                if(sbd.length() > 0){
                    context.write(key, new Text(gender + sbd.toString()));
                    context.getCounter(Counters.VALID_USER).increment(1L);
                }else{
                    context.getCounter(Counters.NO_DATA_USER).increment(1L);
                    return;
                }

            }catch (Exception e){
                context.getCounter(Counters.ERROR_LINE).increment(1L);
            }
        }

        static enum Counters {
            HAVE_OPERATOR,
            HAVE_MODEL,
            HAVE_CHANNEL_DATA,
            HAVE_BRAND_DATA,
            HAVE_DSP_DATA,
            VALID_USER,
            NO_DATA_USER,
            ERROR_LINE
        }
    }
    public static class MAgeSampleMakerMapper extends Mapper<LongWritable, Text, Text, Text>{

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            context.getCounter(Counters.TOTAL_M_LINE).increment(1L);
            if (value == null || value.toString() == null) {
                context.getCounter(Counters.INVALID_LINE).increment(1L);
                return;
            }
            String line = value.toString();
            String filePath = ((FileSplit) context.getInputSplit()).getPath().toString();
            try{
                if(filePath.contains("ali_merge")){
                    //ali data
                    String[] components = line.split(separator);
                    if(components.length != 3){
                        context.getCounter(Counters.INVALID_LINE).increment(1L);
                        return;
                    }
                    String id = components[0];
                    String age = components[2];
                    int aliID = DeviceConstants.checkDeviceType(id);
                    if(aliID == DeviceConstants.IMEI || aliID == DeviceConstants.IDFA || aliID == DeviceConstants.MAC ){
                        if (age.equals("1")) {context.getCounter(Counters.ALI_PC_A).increment(1L);}
                        else if (age.equals("2")) {context.getCounter(Counters.ALI_PC_B).increment(1L);}
                        else if (age.equals("3")) {context.getCounter(Counters.ALI_PC_C).increment(1L);}
                        else if (age.equals("4")) {context.getCounter(Counters.ALI_PC_D).increment(1L);}
                        else if (age.equals("5")) {context.getCounter(Counters.ALI_PC_E).increment(1L);}
                        else if (age.equals("6")) {context.getCounter(Counters.ALI_PC_F).increment(1L);}
                        else if (age.equals("7")) {context.getCounter(Counters.ALI_PC_G).increment(1L);}
                        else {context.getCounter(Counters.ALI_PC_UNKNOWN).increment(1L); return;}
                        context.write(new Text(id), new Text("ali:" + age));
                    }else {
                        context.getCounter(Counters.ALI_OTHER_ID).increment(1L);
                        return;
                    }
                }else if(filePath.contains("mobile_log")){
                    String[] components = line.split(separator);
                    if(components.length != 4){
                        context.getCounter(Counters.INVALID_LINE).increment(1L);
                        return;
                    }
                    String cookie = components[0];
                    int deviceCode = DeviceConstants.checkDeviceType(cookie);
                    if (deviceCode == DeviceConstants.IMEI || deviceCode == DeviceConstants.IDFA ||
                            deviceCode == DeviceConstants.MAC ) {
                        //获取时间+频道
                        String time_channel = components[1] + "_" + components[2];
                        context.write(new Text(cookie), new Text("channel:" + time_channel + separator + components[3]));
                        context.getCounter(Counters.CHANNEL_VALID_ID).increment(1L);
                    }
                }else if(filePath.contains("device_stat")){
                    //device
                    String[] components = line.split(separator);
                    if (components.length != 5) {
                        context.getCounter(Counters.INVALID_LINE).increment(1L);
                        return;
                    }
                    String cookie = components[0];
                    int deviceCode = DeviceConstants.checkDeviceType(cookie);
                    if (deviceCode != DeviceConstants.UNKNOWN) {
                        if (!components[2].equals("-1")){
                            context.write(new Text(cookie), new Text("model:" + components[1] + "_" + components[2]));
                        }
                        if (!components[3].equals("-1")){
                            context.write(new Text(cookie), new Text("operator:" + components[3]));
                        }
                        context.getCounter(Counters.DEVICE_VALID_ID).increment(1L);
                    }

                }else if(filePath.contains("adpreferstat")){
                    //ad
                    String[] components = line.split(separator);
                    if(components.length != 6){
                        context.getCounter(Counters.INVALID_LINE).increment(1L);
                        return;
                    }
                    String cookie = components[5];
                    int deviceCode = DeviceConstants.checkDeviceType(cookie);
                    if(deviceCode == DeviceConstants.IMEI || deviceCode == DeviceConstants.IDFA || deviceCode == DeviceConstants.MAC){
                        context.write(new Text(cookie), new Text("brand:" + components[4] + separator + components[0]));
                        context.getCounter(Counters.AD_VALID_ID).increment(1L);
                    }
                }
            }catch (Exception e){
                context.getCounter(Counters.ERROR_LINE).increment(1L);
            }
        }

        static enum Counters{
            TOTAL_M_LINE,
            INVALID_LINE,
            ALI_PC_A,
            ALI_PC_B,
            ALI_PC_C,
            ALI_PC_D,
            ALI_PC_E,
            ALI_PC_F,
            ALI_PC_G,
            ALI_PC_UNKNOWN,
            ALI_MOBILE_ID,
            ALI_OTHER_ID,
            CHANNEL_VALID_ID,
            CHANNEL_INVALID_ID,
            DEVICE_VALID_ID,
            AD_VALID_ID,
            PC_VALID_DSP,
            PC_INVALID_DSP,
            UNKNOWN_DEVICE,
            ERROR_LINE
        }
    }
}
