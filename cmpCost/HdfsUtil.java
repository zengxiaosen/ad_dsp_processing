import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/27.
 */
public class HdfsUtil {
    public static boolean deleteFile(String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.100.120");
        FileSystem fs = null;
        try{
            fs = FileSystem.get(conf);
            return fs.delete(new Path(path), true);//true是递归删除的意思
        }finally {
            fs.close();
        }
    }
}
