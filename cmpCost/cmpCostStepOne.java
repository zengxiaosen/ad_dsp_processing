import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/1/15.
 */
public class cmpCostStepOne extends Configured implements Tool{
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println("args[] in run:" + Arrays.toString(args));
        String jobname = "cmpCost_step_1";
        Job job = Job.getInstance(conf);
        job.setJarByClass(cmpCostStepOne.class);
        job.setJobName(jobname);

        String inputpaths = conf.get("input");
        String outputPaths = conf.get("output");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(cmpCostMapper.class);
        job.setReducerClass(cmpCostReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, inputpaths);
        FileOutputFormat.setOutputPath(job, new Path(outputPaths));

        return (job.waitForCompletion(true) ? 0 : 1);

    }

    public static void main(String[] args) throws Exception{
        System.out.println("args[] in main:" + Arrays.toString(args));
        int exitCode = ToolRunner.run(new cmpCostStepOne(), args);
        System.exit(exitCode);
    }

    //cmp是一种展示付费
    enum CheatRecords{
        SHOW,
        SHOWCPV,
        SHOWCPC,
        SHOWCPM
    }

    enum BadRecords{
        FEE,
        SHOW,
        RANK,
        INRANK,
        RANKCASTLEN,
        CHEATBUTFEE
    }

    enum LogRecords{
        FEE,
        SHOW,
        RANKCASTS,
        SHOWCPV,
        SHOWCPC,
        SHOWCPM,
        //prv是广告类高清视频
        PRVRANK
    }

    public static class cmpCostMapper extends Mapper<LongWritable, Text, Text, Text>{
        private static Pattern p = Pattern.compile("/source/ad/dsp/(.*?)(/(invideo|vhtml))?/");
        private String logtype;

        public void setup(Context context){
            FileSplit fsp = (FileSplit) context.getInputSplit();
            Path inputfile = fsp.getPath();
            String fname = inputfile.toString();
            Matcher m = p.matcher(fname);
            m.find();
            logtype = m.group(1);
        }

        // input value: logtype i field[i] count:
        public void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] inputRecord = line.split("\t");
            String requestID = null;
            String impressID = null;
            String castID = null;
            String adxPid = null;
            String costType = null;
            String dspPrice = null;//dsp实际扣费（在fee日志中的winPrice)
            String yesPrice = null;//yes对dsp的扣费
            String new_ecpm = null;//实际竞价出价
            String suggested_ecpm = null;//建议出价
            String market = "-";

            //String price = null; // 广告主最高定价
            String ectr = null;//rank回填的ctr,用来计算扣费

            if(logtype.equals("fee")){
                context.getCounter(LogRecords.FEE).increment(1);
                if(inputRecord.length < 20 || !inputRecord[17].matches("\\d+\\*\\d+")){
                    context.getCounter(BadRecords.FEE).increment(1);
                    return;
                }

                requestID = inputRecord[8];
                impressID = inputRecord[9];
                castID = inputRecord[11];
                //costType = inputRecord[12];
                dspPrice = inputRecord[19];//dsp实际扣费winPrice
                context.write(
                        new Text(requestID+"\t"+impressID+"\t"+castID),
                        new Text(logtype+"\t"+dspPrice)
                );

            }else if(logtype.equals("show")){
                context.getCounter(LogRecords.SHOW).increment(1);
                if(inputRecord.length < 43 || !inputRecord[35].matches("\\d+\\*\\d+")){
                    context.getCounter(BadRecords.SHOW).increment(1);
                    return;
                }
                costType = inputRecord[29-1];
                String isCheat = "0";

                if(costType.equals("0")) context.getCounter(LogRecords.SHOWCPM).increment(1);
                else if(costType.equals("1")) context.getCounter(LogRecords.SHOWCPC).increment(1);
                else if(costType.equals("3")) context.getCounter(LogRecords.SHOWCPV).increment(1);

                if(!inputRecord[42-1].equals("0")){
                    context.getCounter(CheatRecords.SHOW).increment(1);
                    if(costType.equals("0")) context.getCounter(CheatRecords.SHOWCPM).increment(1);
                    else if(costType.equals("1")) context.getCounter(CheatRecords.SHOWCPC).increment(1);
                    else if(costType.equals("3")) context.getCounter(CheatRecords.SHOWCPV).increment(1);
                    isCheat = inputRecord[42-1];
                }

                requestID = inputRecord[22];
                impressID = inputRecord[23];
                castID = inputRecord[27];
                //costType = inputRecord[28];
                yesPrice = inputRecord[37];//yes实际扣费winPrice

                market = inputRecord[43-1];
                context.write(
                        new Text(requestID+"\t"+impressID+"\t"+castID),
                        new Text(logtype+"\t"+yesPrice+"\t"+isCheat+"\t"+market)
                );
            }else if(logtype.equals("rank")){
                context.getCounter(LogRecords.PRVRANK).increment(1);
                if(inputRecord.length < 6){
                    context.getCounter(BadRecords.RANK).increment(1);
                    return;
                }
                requestID = inputRecord[2];
                impressID = inputRecord[3];
                adxPid = inputRecord[5];

                if(adxPid.equals("106")){
                    if(inputRecord.length < 21){
                        context.getCounter(BadRecords.RANK).increment(1);
                        return;
                    }
                    String[] filterdCasts = inputRecord[20-1].split("#");

                    for(int i=0; i<filterdCasts.length; i++){
                        context.getCounter(LogRecords.RANKCASTS).increment(1);
                        String[] filteredCast = filterdCasts[i].split(":");
                        if(filterdCasts.length < 10){
                            context.getCounter(BadRecords.RANKCASTLEN).increment(1);
                            continue;
                        }

                        try{
                            castID = filteredCast[1-1];//castID
                            costType = "1";
                            ectr = filterdCasts[7-1];
                            new_ecpm = filterdCasts[9-1];
                            suggested_ecpm = filterdCasts[10-1];
                            //new_ecpm : //实际竞价出价
                            if(Double.parseDouble(suggested_ecpm) > Double.parseDouble(new_ecpm)){
                                suggested_ecpm = new_ecpm;
                            }
                            if(costType.matches("[0123]")){
                                context.write(
                                        new Text(requestID+"\t"+impressID+"\t"+castID),
                                        new Text(logtype + "\t" + costType + "\t" + ectr + "\t" + suggested_ecpm + "\t" + adxPid)
                                );
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.err.println(Arrays.toString(filterdCasts));
                            context.getCounter(BadRecords.INRANK).increment(1);
                        }

                    }
                }

                else{
                    if(inputRecord.length < 12){
                        context.getCounter(BadRecords.RANK).increment(1);
                        return;
                    }
                    String[] filteredCasts = inputRecord[11].split("#");

                    for(int i=0; i< filteredCasts.length; i++){
                        context.getCounter(LogRecords.RANKCASTS).increment(1);
                        String[] filteredCast = filteredCasts[i].split(":");
                        if(filteredCast.length < 12){
                            context.getCounter(BadRecords.RANKCASTLEN).increment(1);
                            continue;
                        }

                        try{
                            castID = filteredCast[0]; // castID
                            costType = filteredCast[2];
                            ectr = filteredCast[7];
                            new_ecpm = filteredCast[9];
                            suggested_ecpm = filteredCast[11];

                            if(Double.parseDouble(suggested_ecpm) > Double.parseDouble(new_ecpm))
                                suggested_ecpm = new_ecpm;
                            if(costType.matches("[0123]")){
                                context.write(
                                        new Text(requestID + "\t" + impressID + "\t" + castID),
                                        new Text(logtype + "\t" + costType + "\t" + ectr + "\t" + suggested_ecpm + "\t" + adxPid)
                                );
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.err.println(Arrays.toString(filteredCast));
                            context.getCounter(BadRecords.INRANK).increment(1);
                        }

                    }
                }
            }
            else{
                context.setStatus("Detected possibly unnormal record.");
                System.err.format("unexpected line, logtype:%s, length:%s",logtype,inputRecord.length);
            }
        }

    }

    public static class cmpCostReducer extends Reducer<Text, Text, Text, Text>{
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            boolean flagFee = false, flagShow = false, flagRank = false;
            String castID = null;
            String costType = null;
            String logType = null;
            String adxPid = null;
            double dspPrice = 0;//dsp实际扣费（在fee日志中的winPrice）
            double yesPrice = 0;//yes对dsp的扣费
            double suggest_ecpm = 0;//建议出价
            double ectr = 0;//rank回填的ctr,用来计算扣费
            double priceA = 0;//扣费策略C的扣费(按suggested_ecpm扣费）
            double priceC = 0;//扣费策略C的扣费(按suggested_ecpm扣费）

            String val;
            String[] val_items;
            String[] keyss = key.toString().split("\t");
            castID = keyss[2];

            String isCheat = "-1";
            String market = "-";

            for(Text value : values){
                val = value.toString();
                val_items = val.split("\t");
                logType = val_items[0];
                if(logType.equals("fee")){
                    //实际扣费
                    try{
                        dspPrice += Double.parseDouble(val_items[1]);
                        flagFee = true;
                    }catch (Exception e){
                        e.printStackTrace();
                        flagFee = false;
                    }
                }
                else if(logType.equals("show")){
                    //yes扣费
                    try{
                        yesPrice = Double.parseDouble(val_items[1]);
                        isCheat = val_items[2];
                        market = val_items[3];
                        flagShow = true;
                    }catch (Exception e){
                        e.printStackTrace();
                        flagShow = false;
                    }
                }
                else if(logType.equals("rank")){
                    //按suggested_ecpm扣费（策略C）
                    //val(logtype+"\t"+ costType + "\t" + ectr + "\t" + suggested_ecpm + "\t" + adxPid)
                    costType = val_items[1];
                    adxPid = val_items[4];

                    try{
                        ectr = Double.parseDouble(val_items[2]);
                        suggest_ecpm = Double.parseDouble(val_items[3]);
                        flagRank = true;
                    }catch (Exception e){
                        e.printStackTrace();
                        flagRank = false;
                    }
                }
                else{
                    context.setStatus("Detected possibly unnormal record.");
                    System.err.println("unexpected logType:" + logType);
                }
            }
            if(flagShow && flagRank){
                //yes扣过费的，rank日志中也记录的一次竞价
                //if(!flagFee && !costType.equals("0")) { //Fee日志中未扣费的CPC或CPV类型广告
                if(!flagFee){
                    //Fee日志中未扣费的广告不计算DSP的扣费
                    dspPrice = 0;
                    priceA = 0;
                    priceC = 0;
                    //					if(costType.equals("0")) {
                    //						yesPrice = 0;
                    //						context.getCounter(BadRecords.CPMNOFEE).increment(1);
                    //					}
                }
                else if(!isCheat.equals("0")) context.getCounter(BadRecords.CHEATBUTFEE).increment(1);
                else if(costType.equals("0")){
                    //CPM
                    priceA = yesPrice;
                    priceC = suggest_ecpm/1000;
                }
                else if(costType.equals("1")){
                    //CPC
                    priceA = yesPrice/ectr;
                    priceC = suggest_ecpm/(ectr*1000);
                }
                else if(costType.equals("2")){
                    //CPA
                    priceC = 0;
                    context.setStatus("Detected possibly unnormal record.");
                    System.err.println("unexpected costType:2:CPC");
                    return;
                }
                else if(costType.equals("3")){
                    //CPV
                    priceA = yesPrice/ectr;
                    priceC = suggest_ecpm/(ectr*1000);
                }
                else{
                    context.setStatus("Detected possibly unnormak record.");
                    System.err.println("unexpected costType: unknown type: " + costType);
                }

                context.write(
                        new Text(castID+"\t"+costType+"\t"+adxPid+"\t"+isCheat+"\t"+market),
                        new Text(dspPrice+"\t"+yesPrice+"\t"+priceA+"\t"+priceC)
                );

            }
        }
    }





}
