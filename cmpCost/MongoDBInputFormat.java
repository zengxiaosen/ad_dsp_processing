import com.mongodb.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/31.
 */
public class MongoDBInputFormat<V extends MongoDBWritable> extends InputFormat<LongWritable, V> {

    /*
    MongoDB自定义InputSplit
     */
    static class MongoDBInputSplit extends InputSplit implements Writable{
        private long start;//起始位置，包含
        private long end;//终止位置，不包含

        public MongoDBInputSplit(int i, long l){
            super();
        }

        public long getLength() throws IOException, InterruptedException {
            return end - start;
        }

        public String[] getLocations() throws IOException, InterruptedException {
            //没有数据本地化信息，所以返回一个长度为0的数组
            return new String[0];
        }

        public void write(DataOutput out) throws IOException {
            out.writeLong(this.start);
            out.writeLong(this.end);

        }

        public void readFields(DataInput in) throws IOException {
            this.start = in.readLong();
            this.end = in.readLong();
        }
    }


    /*
    获取分片信息
     */
    public List<InputSplit> getSplits(JobContext jobContext) throws IOException, InterruptedException {
        //获取mongo连接
        DB mongo = Mongo.connect(new DBAddress("127.0.0.1", "hadoop"));

        //获取mongo集合
        DBCollection dbCollection = mongo.getCollection("persons");

        //每两条数据一个mapper
        int chunkSize = 2;
        long size = dbCollection.count();//获取mongodb对于collection的数据条数
        long chunk = size / chunkSize; //计算mapper个数
        List<InputSplit> list = new ArrayList<InputSplit>();
        for(int i=0; i< chunk; i++){
            if(i+1 == chunk){
                list.add(new MongoDBInputSplit(i * chunkSize, size));
            }else{
                list.add(new MongoDBInputSplit(i * chunkSize, i*chunkSize + chunkSize));
            }
        }

        // 0 2 01
        // 2 4 23
        // 4 7 456

        return list;
    }

    /*
    获取具体的reader类
     */
    public RecordReader<LongWritable, V > createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

        return new MongoDBRecordReader((MongoDBInputSplit) inputSplit, taskAttemptContext);
    }

    /*
    一个空的mongodb自定义数据类型
     */
    static class NullMongoDBWritable implements MongoDBWritable{

        public void readFields(DBObject dbObject) {

        }

        public void write(DBCollection dbCollection) {

        }

        public void write(DataOutput dataOutput) throws IOException {

        }

        public void readFields(DataInput dataInput) throws IOException {

        }
    }

    /*
    自定义mongodbreader类
     */
    static class MongoDBRecordReader< V extends MongoDBWritable> extends RecordReader<LongWritable, V>{

        private MongoDBInputSplit split;
        private int index;

        public MongoDBRecordReader() {
            super();
        }

        private DBCursor dbCursor;

        public MongoDBRecordReader(MongoDBInputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            this.initialize(split, context);
        }

        private LongWritable key;
        private V value;

        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            this.split = (MongoDBInputSplit) inputSplit;
            Configuration conf = taskAttemptContext.getConfiguration();
            key =  new LongWritable();
            //第二个参数是找不到这个class的时候要传一个默认的值
            Class clz = conf.getClass("mapreduce.mongo.split.value.class", NullMongoDBWritable.class);
            value = (V) ReflectionUtils.newInstance(clz, conf);

        }

        public boolean nextKeyValue() throws IOException, InterruptedException {
            if(this.dbCursor == null){
                // 获取mongo连接
                DB mongo = Mongo.connect(new DBAddress("127.0.0.1", "hadoop"));
                // 获取mongo集合
                DBCollection dbCollection = mongo.getCollection("persons");
                //获取DBcurstor对象
                dbCursor = dbCollection.find().skip((int)this.split.start).limit((int) this.split.getLength());
            }
            boolean hasNext = this.dbCursor.hasNext();
            if(hasNext) {
                DBObject dbObject = this.dbCursor.curr();
                this.key.set(this.split.start + index);
                this.index ++ ;
                this.value.readFields(dbObject);
            }
            return hasNext;
        }

        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return this.key;
        }

        public V getCurrentValue() throws IOException, InterruptedException {
            return this.value;
        }

        public float getProgress() throws IOException, InterruptedException {
            return 0;
        }

        public void close() throws IOException {

        }
    }
}
