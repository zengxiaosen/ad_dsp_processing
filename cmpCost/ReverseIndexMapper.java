import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2017/1/27.
 */

public class ReverseIndexMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();
    private Text ovalue = new Text();
    private String filePath;

    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        FileSplit split = (FileSplit) context.getInputSplit();
        filePath = split.getPath().toString();
    }

    protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while(tokenizer.hasMoreTokens()){
            word.set(tokenizer.nextToken());
            ovalue.set(filePath + ":1");
            context.write(word, ovalue);
        }

    }
}
