package test;

import com.google.common.base.Optional;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */
public class App {
    public static void main(String[] args){
        SparkConf conf = new SparkConf().setAppName("Simple Application").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //convert from other RDD
        JavaRDD<String> line1 = sc.parallelize(Arrays.asList("1 aa", "2 bb", "4 cc", " 3 dd"));
        JavaPairRDD<String, String> prdd = line1.mapToPair(new PairFunction<String, String, String>() {
            public Tuple2<String, String> call(String x) throws Exception {
                return new Tuple2(x.split(" ")[0], x);
            }
        });
        System.out.println("111111111111mapToPair:");
        prdd.foreach(new VoidFunction<Tuple2<String, String>>() {
            public void call(Tuple2<String, String> x) throws Exception {
                System.out.println(x);
            }
        });
        // parallelizePairs
        Tuple2 t1 = new Tuple2(1, 2);
        Tuple2 t2 = new Tuple2(3, 4);
        Tuple2 t3 = new Tuple2(3, 6);

        List list1 = new ArrayList<Tuple2>();
        list1.add(t1);
        list1.add(t2);
        list1.add(t3);

        JavaPairRDD<Integer, Integer> line2 = sc.parallelizePairs(list1);
        line2.persist(StorageLevel.MEMORY_ONLY());

        Tuple2 t4 = new Tuple2(3, 9);
        List list2 = new ArrayList<Tuple2>();
        list2.add(t4);
        JavaPairRDD<Integer, Integer> line3 = sc.parallelizePairs(list2);
        line3.persist(StorageLevel.MEMORY_ONLY());

        // subtractByKey
        JavaPairRDD<Integer, Integer> line4 = line2.subtractByKey(line3);
        System.out.println("22222222222222subtractByKey:");
        line4.foreach(new VoidFunction<Tuple2<Integer, Integer>>() {
            public void call(Tuple2<Integer, Integer> x) throws Exception {
                System.out.println(x);
            }
        });
        //join
        JavaPairRDD<Integer, Tuple2<Integer, Integer>> line5 = line2.join(line3);
        System.out.println("33333333333333join:");
        line5.foreach(new VoidFunction<Tuple2<Integer, Tuple2<Integer, Integer>>>() {
            public void call(Tuple2<Integer, Tuple2<Integer, Integer>> x) throws Exception {
                System.out.println(x);
            }
        });
        // rightOutputJoin
        JavaPairRDD<Integer, Tuple2<Integer, Optional<Integer>>> line6 = line2.leftOuterJoin(line3);
        System.out.println("444444444444444444rightOuterJoin:");
        line6.foreach(new VoidFunction<Tuple2<Integer, Tuple2<Integer, Optional<Integer>>>>() {
            public void call(Tuple2<Integer, Tuple2<Integer, Optional<Integer>>> x) throws Exception {
                System.out.println(x);
            }
        });

        // leftOuterJoin
        JavaPairRDD<Integer, Tuple2<Integer, Optional<Integer>>> line7 = line2.leftOuterJoin(line3);
        System.out.println("555555555555555leftOuterJoin:");
        line7.foreach(new VoidFunction<Tuple2<Integer, Tuple2<Integer, Optional<Integer>>>>() {
            public void call(Tuple2<Integer, Tuple2<Integer, Optional<Integer>>> x) throws Exception {
                System.out.println(x);
            }
        });

        //cogroup
        JavaPairRDD<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> line8 = line2.cogroup(line3);
        System.out.println("66666666666666666cogroup:");
        line8.foreach(new VoidFunction<Tuple2<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>>>() {
            public void call(Tuple2<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> x) throws Exception {
                System.out.println(x);
            }
        });



    }
}
