package test;

import java.util.Arrays;
import java.util.Collections;

import com.mongodb.hadoop.MongoOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import scala.Tuple2;


/**
 * Created by Administrator on 2017/2/7.
 */
public class JavaWordCount {
    public static void main(String[] args){
        JavaSparkContext sc = new JavaSparkContext("local", "Java Word Count");
        Configuration config = new Configuration();
        config.set("mongo.input.uri","mongodb://127.0.0.1:27017/beowulf.input");
        config.set("mongo.output.uri","mongodb://127.0.0.1:27017/beowulf.output");



        // Input contains tuples of (ObjectId, BSONObject)
        JavaPairRDD<Object, BSONObject> mongoRDD = sc.newAPIHadoopRDD(config, com.mongodb.hadoop.MongoInputFormat.class, Object.class, BSONObject.class);

        // Input contains tuples of (ObjectId, BSONObject)
        JavaRDD<String> words = mongoRDD.flatMap(new FlatMapFunction<Tuple2<Object, BSONObject>, String>() {

            public Iterable<String> call(Tuple2<Object, BSONObject> arg) {
                Object o = arg._2.get("text");
                if (o instanceof String) {
                    String str = (String) o;
                    str = str.toLowerCase().replaceAll("[.,!?\n]", " ");
                    return Arrays.asList(str.split(" "));
                } else {
                    return Collections.emptyList();
                }
            }
        });

    }
}
