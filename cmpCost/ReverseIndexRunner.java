import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/30.
 */
public class ReverseIndexRunner {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.100.120");
        Job job = Job.getInstance(conf, "reverse_index");

        FileInputFormat.setInputPaths(job, "/a/input");
        job.setJarByClass(ReverseIndexRunner.class);
        job.setMapperClass(ReverseIndexMapper.class);
        job.setReducerClass(ReverseIndexReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, new Path("/a/index/1"));
        job.waitForCompletion(true);
    }
}
