import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/1/15.
 */
public class cmpCostStepTwo extends Configured implements Tool {

    public static class cmpCostMapper extends Mapper<LongWritable, Text, Text, Text>{

        public static int findNthIndex(String s, char c, int n){
            int i = 0;
            int find_n = 0;
            while(i < s.length()){
                if(s.charAt(i) == c) find_n++;
                if(find_n == n) return i;
                i++;
            }
            return -1;
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            int sepi = findNthIndex(line,'\t', 5);

            String outkey = line.substring(0, sepi);
            String outval = line.substring(sepi+1, line.length());//忽略tab本身
            context.write(
                    new Text(outkey),
                    new Text(outval)
            );

        }

    }

    public static class cmpCostReducer extends Reducer<Text, Text, Text, Text>{
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            double dspPrice = 0;//dsp实际扣费（在fee日志中的winPrice)
            double yesPrice = 0;//yes对dsp的扣费
            double priceA = 0;//扣费策略C扣费
            double priceC = 0;//扣费策略C扣费
            double priceMax = 0;

            double base = 1;

            //parse val;
            String val;
            String[] val_items;
            for(Text value: values){
                val = value.toString();
                val_items = val.split("\t");
                dspPrice += Double.parseDouble(val_items[0]);
                yesPrice += Double.parseDouble(val_items[1]);
                priceA += Double.parseDouble(val_items[2]);
                priceC += Double.parseDouble(val_items[3]);
                priceMax += Math.max(Double.parseDouble(val_items[2]), Double.parseDouble(val_items[3]));
            }

            context.write(
                    key,
                    new Text(yesPrice/base +"\t"+dspPrice/base +"\t"+priceA/base+"\t"+priceC/base+"\t"+ priceMax/base)
            );
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println("args[] in run:" + Arrays.toString(args));

        String jobname = "cmpCost_step_2";

        Job job = Job.getInstance(conf);
        job.setJarByClass(cmpCostStepOne.class);
        job.setJobName(jobname);

        String inputpaths = conf.get("input");
        String outputpath = conf.get("output");


        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(cmpCostMapper.class);
        //job.setCombinerClass(cmpCostMapper.class);
        job.setReducerClass(cmpCostReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, inputpaths);
        FileOutputFormat.setOutputPath(job, new Path(outputpath));

        return (job.waitForCompletion(true) ? 0 : 1);
    }


    public static void main(String[] args) throws Exception {
        System.out.println("args[] in main:" + Arrays.toString(args));
        int exitCode = ToolRunner.run(new cmpCostStepTwo(), args);
        System.exit(exitCode);
    }
}
