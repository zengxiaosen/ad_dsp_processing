import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;



/**
 * Created by Administrator on 2017/2/8.
 */

/**
 * 自定义输出key数据类型
 */
public class IntPair implements WritableComparable<IntPair> {

    private int first;
    private int second;

    public IntPair(int first1, int second1){
        this.first = first1;
        this.second = second1;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int compareTo(IntPair o) {
        if(o == this){
            return 0;
        }
        //int tmp = Integer.compare(this.first, o.first);
        //先按照first排序
        int tmp = Integer.valueOf(this.first).compareTo(Integer.valueOf(o.first));
        if(tmp != 0){
            return tmp;
        }
        //tmp = Integer.compare(this.second, o.second);
        //再按照second排序
        tmp = Integer.valueOf(this.second).compareTo(Integer.valueOf(o.second));
        return tmp;
    }

    public void write(DataOutput out) throws IOException {
        //hadoop序列化的方法
        out.writeInt(this.first);
        out.writeInt(this.second);

    }

    public void readFields(DataInput in) throws IOException {
        //输出
        this.first = in.readInt();
        this.second = in.readInt();

    }
}
