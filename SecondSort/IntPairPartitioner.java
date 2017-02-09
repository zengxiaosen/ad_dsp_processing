import org.apache.hadoop.io.IntWritable;
import org.apache.spark.Partitioner;

/**
 * Created by Administrator on 2017/2/8.
 */
public class IntPairPartitioner extends Partitioner {
    public int numPartitions(IntPair key, IntWritable value, int numPartitions) {
        if(numPartitions >= 2){
            int first = key.getFirst();
            if(first % 2 == 0){
                // 是偶数，需要第二个reducer进行处理
                return 1;
            }else{
                //是奇数，所以需要第一个reducer进行处理
                return 0;
            }
        }else{
            throw new IllegalArgumentException("reducer个数必须大于1");
        }
    }


    public int numPartitions() {
        return 0;
    }

    public int getPartition(Object key) {
        return 0;
    }
}
