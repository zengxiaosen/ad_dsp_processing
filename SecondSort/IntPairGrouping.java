import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * Created by Administrator on 2017/2/8.
 */

/**
 * 自定义分组类
 */
public class IntPairGrouping extends WritableComparator {

    public IntPairGrouping(){
        super(IntPair.class, true);
    }

    //分组的时候只根据第一个字符进行分组
    public int compare(WritableComparable a, WritableComparable b){
        IntPair key1 = (IntPair) a;
        IntPair key2 = (IntPair) b;
        return Integer.valueOf(key1.getFirst()).compareTo(Integer.valueOf(key2.getFirst()));
    }
}
