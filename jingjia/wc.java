import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.WrappedMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2017/1/27.
 */
public class wc extends Mapper<Object, Text, Text, LongWritable>{
        private Text word = new Text();
        private LongWritable one = new LongWritable(1);
        protected void setup(WrappedMapper.Context context) throws IOException, InterruptedException{
            super.setup(context);
            System.out.println("setup");
        }
        protected void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException{
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while(tokenizer.hasMoreTokens()){
                //String word = tokenizer.nextToken();
                word.set(tokenizer.nextToken());
                context.write(word, one);
            }

        }

}

class wc_reducer extends Reducer<Text, LongWritable, Text, LongWritable>{
        private LongWritable count = new LongWritable();
        protected void setup(Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            super.setup(context);
            System.out.println("setup");
        }
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for(LongWritable value : values){
                sum += value.get();
            }
            count.set(sum);
            context.write(key, count);
        }

}

class WordCountRunner implements Tool {
    private Configuration conf = null;
    public int run(String[] strings) throws Exception {
        Configuration conf  = this.getConf();
        Job job = Job.getInstance(conf, "wordcount");
        //1, 输入
        FileInputFormat.addInputPath(job, new Path("/a/input/"));
        //2.map
        job.setMapperClass(wc.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        //3 shuffle
        //4 reduce
        job.setReducerClass(wc_reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        //5 output
        HdfsUtil.deleteFile("/a/wc/1");
        FileOutputFormat.setOutputPath(job, new Path("/a/output"));
        //到这里一个job就已经创建完了
        //提交(等待是否完成）
        return job.waitForCompletion(true) ? 0 : 1;

    }

    public void setConf(Configuration that) {
        this.conf = that;
        //指定我们的集群在哪
        this.conf.set("fs.defaultFS", "hdfs://192.168.100.120:8020");
    }

    public Configuration getConf() {
        return this.conf;
    }

    public static void main(String[] args) throws Exception {
        //运行
        ToolRunner.run(new WordCountRunner(), args);

    }

}
