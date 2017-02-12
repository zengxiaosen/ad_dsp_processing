package processing_pcData;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2017/2/11.
 */

/**
 * mapreduce操作hbase
 */
public class HBaseTableDemo {
    /**
     * 转换字符串为map对象
     */


    static Map<String, String> transfoerContent2Map(String content){
        Map<String, String> map = new HashMap<String, String>();
        int i = 0;
        String key = "";
        StringTokenizer tokenizer = new StringTokenizer(content, "({|}|\"|:|,)");
        while(tokenizer.hasMoreTokens()){
            if(++i % 2 == 0){
                // 当前值是value
                map.put(key, tokenizer.nextToken());
            }else{
                // 当前值是 key
                key = tokenizer.nextToken();
            }
        }
        return map;
    }
    static class DemoMapper extends TableMapper<Text, ProductModel>{

        private Text outputKey = new Text();
        private ProductModel outputValue = new ProductModel();
        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            String content = Bytes.toString(value.getValue(Bytes.toBytes("f"), Bytes.toBytes("content")));
            if(content == null){
                System.err.println("数据格式错误" + content);
                return;
            }
            Map<String, String> map = transfoerContent2Map(content);
            if(map.containsKey("p_id")){
                // 产品id存在
                outputKey.set(map.get("p_id"));
            }else{
                System.err.println("数据格式错误" + content);
                return;
            }
            if(map.containsKey("p_name") && map.containsKey("price")){
                //数值正常，进行赋值
                outputValue.setId(outputKey.toString());
                outputValue.setName(map.get("p_name"));
                outputValue.setPrice(map.get("price"));
            }else{
                System.err.println("数据格式错误" + content);
                return;
            }
            //输出
            context.write(outputKey, outputValue);
        }
    }

    /**
     * 往hbase输出reducer
     */
    static class DemoReducer extends TableReducer<Text, ProductModel, ImmutableBytesWritable>{
        @Override
        protected void reduce(Text key, Iterable<ProductModel> values, Context context) throws IOException, InterruptedException {
            for(ProductModel value : values){
                // 我只拿一个，如果有多个产品id的话
                ImmutableBytesWritable outputKey = new ImmutableBytesWritable(Bytes.toBytes(key.toString()));
                Put put = new Put(Bytes.toBytes(key.toString()));
                put.add(Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes(value.getId()));
                put.add(Bytes.toBytes("f"), Bytes.toBytes("name"), Bytes.toBytes(value.getName()));
                put.add(Bytes.toBytes("f"), Bytes.toBytes("price"), Bytes.toBytes(value.getPrice()));
                context.write(outputKey, put);
            }
        }
    }

    /**
     * 执行入口
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("fs.defaultFS", "hdfs://192.168.100.120");//hadoop的环境
        conf.set("hbase.zookeeper.quorum", "192.168.100.120"); // hbase zk环境信息

        Job job = Job.getInstance(conf, "demo");
        job.setJarByClass(HBaseTableDemo.class);

        // 设置mapper相关，mapper从hbase输入
        // 本地环境，最后一个参数必须为false
        // 第一个参数是表名
        TableMapReduceUtil.initTableMapperJob("data",new Scan(), DemoMapper.class, Text.class, ProductModel.class, job, false);
        // 设置reducer相关，reducer在hbase输出
        // 本地环境，而且fs.defaultFS为集群模式的时候，需要设置addDependencyJars参数为false
        TableMapReduceUtil.initTableReducerJob("online_product", DemoReducer.class, job, null, null, null, null, false);

        int l = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("执行：" + l);
    }
}
