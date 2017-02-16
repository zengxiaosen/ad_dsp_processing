import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * Created by Administrator on 2017/2/15.
 */
public class UDFLowerOrUpperCase extends UDF{

    public Text evaluate(Text t){
        // 默认进行小写转换
        return this.evaluate(t, "lower");
    }

    /**
     * 对参数t进行大小写转换
     * @param t
     * @param lowerOrUpper
     *         如果该值为lower，则进行小写转换，如果该值为uppper则进行大小转换，其他情况不进行转换
     * @return
     */

    public Text evaluate(Text t, String lowerOrUpper){
        if(t == null){
            return t;
        }
        if("lower".equals(lowerOrUpper)){
            return new Text(t.toString().toLowerCase());
        }else if("upper".equals(lowerOrUpper)){
            return new Text(t.toString().toUpperCase());
        }
        return t;
    }
}

