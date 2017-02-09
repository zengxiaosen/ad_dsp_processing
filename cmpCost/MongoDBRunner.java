import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/31.
 */
//操作mongodb的主类
public class MongoDBRunner {
    /*
    Mongodb转化为hadoop的一个bean对象
     */
    static class PersonMongoDBWritable implements MongoDBWritable{
        private String name;
        private Integer age;
        private String sex = "";
        private int count = 1;


        public void write(DBCollection dbCollection) {
            DBObject dbObject = BasicDBObjectBuilder.start().add("age", this.age).add("count", this.count).get();
            dbCollection.insert(dbObject);
        }

        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.name);
            out.writeUTF(this.sex);
            if(this.age == null){
                out.writeBoolean(false);
            }else{
                out.writeBoolean(true);
                out.writeInt(this.age);
            }
            out.writeInt(this.count);
        }

        public void readFields(DBObject dbObject) {
            //从mongodb中拿数据
            this.name = dbObject.get("name").toString();
            //this.sex = dbObject.get("sex").toString();
            if(dbObject.get("age") != null){
                this.age = Double.valueOf(dbObject.get("age").toString()).intValue();
            }

        }

        public void readFields(DataInput in) throws IOException {
            this.name = in.readUTF();
            this.sex = in.readUTF();
            if(in.readBoolean()){
                //有这个标志位
                this.age = in.readInt();
            }
            else{
                this.age = null;
            }
            this.count = in.readInt();
        }
    }


    //做一个合并
    public static  class MongoDBMapper extends Mapper<LongWritable, PersonMongoDBWritable, IntWritable, PersonMongoDBWritable> {
        protected void map(LongWritable key, PersonMongoDBWritable value,
                           Mapper<LongWritable, PersonMongoDBWritable, IntWritable, PersonMongoDBWritable>.Context context) throws IOException, InterruptedException{
            if(value.age == null){
                System.out.println("过滤数据" + value.name);
                return;
            }
            context.write(new IntWritable(value.age), value);

        }
    }
    //自定义reducer
    static class MongoDBReducer extends Reducer<IntWritable, PersonMongoDBWritable, NullWritable, PersonMongoDBWritable> {
        protected void reduce(IntWritable key, Iterable<PersonMongoDBWritable> values, Reducer<IntWritable, PersonMongoDBWritable, NullWritable, PersonMongoDBWritable>.Context context) throws IOException, InterruptedException{
            long sum = 0;
            for(PersonMongoDBWritable value : values){
                sum += value.count;
            }
            PersonMongoDBWritable personMongoDBWritable = new PersonMongoDBWritable();
            personMongoDBWritable.age = key.get();
            personMongoDBWritable.count = (int) sum;
            context.write(NullWritable.get(), personMongoDBWritable);

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        //设置inputformat的value类
        conf.setClass("mapreduce.mongo.split.value.class", PersonMongoDBWritable.class, MongoDBWritable.class);
        Job job = Job.getInstance(conf, "自定义input/outputformat");
        job.setJarByClass(MongoDBRunner.class);
        job.setMapperClass(MongoDBMapper.class);
        job.setReducerClass(MongoDBReducer.class);
        job.setMapOutputKeyClass(IntWritable.class);//mapper输出key
        job.setMapOutputValueClass(PersonMongoDBWritable.class);//mapper输出value
        job.setOutputKeyClass(NullWritable.class);//reducer输出key
        job.setOutputValueClass(PersonMongoDBWritable.class);//reducer输出value
        job.setInputFormatClass(MongoDBInputFormat.class); //计算inputformat
        job.setOutputFormatClass(MongoDBOutputFormat.class); // 计算outputformat

        job.waitForCompletion(true);

    }

}
