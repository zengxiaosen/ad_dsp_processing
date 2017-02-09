import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/1/31.
 */

/**
 * 自定义outputformat
 * @param <V>
 */
public class MongoDBOutputFormat<V extends MongoDBWritable> extends OutputFormat<NullWritable, V> {

    /**
     * 自定义mongodb outputformat
     * @param <V>
     */
    static class MongoDBRecordWriter<V extends MongoDBWritable> extends RecordWriter<NullWritable, V>{
        private DBCollection dbCollection = null;
        public MongoDBRecordWriter() {

        }

        public MongoDBRecordWriter(TaskAttemptContext context) throws UnknownHostException {
            DB db = Mongo.connect(new DBAddress("127.0.0.1", "hadoop"));
            dbCollection = db.getCollection("result");

        }

        public void write(NullWritable key, V value) throws IOException, InterruptedException {
            value.write(this.dbCollection);
        }

        public void close(TaskAttemptContext context) throws IOException, InterruptedException {

        }
    }


    public RecordWriter<NullWritable, V> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new MongoDBRecordWriter<V>(context);
    }

    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {

    }

    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new FileOutputCommitter(null, taskAttemptContext);
    }
}
