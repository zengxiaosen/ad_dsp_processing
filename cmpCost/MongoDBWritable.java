import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/31.
 */
/*
mongodb自定义数据类型
 */
public interface MongoDBWritable extends Writable {
    /*
    从mongodb中读数据
     */
    public void readFields(DBObject dbObject);
    /*
    往mongodb中写入数据
     */
    public void write(DBCollection dbCollection);


}
