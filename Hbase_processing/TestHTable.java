import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;
import util.HBaseUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */
public class TestHTable {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseUtil.getHBaseConfiguration();
        HTable hTable = new HTable(conf, "users");
        try{
            testPut(hTable);
        }finally {
            hTable.close();
        }
    }

    /**
     * 测试put操作
     * @param hTable
     */
    static void testPut(HTable hTable) throws IOException {
        Put put = new Put(Bytes.toBytes("row1"));
        put.add(Bytes.toBytes("f"),Bytes.toBytes("id"),Bytes.toBytes("1"));
        put.add(Bytes.toBytes("f"),Bytes.toBytes("name"),Bytes.toBytes("zhangsan"));
        put.add(Bytes.toBytes("f"),Bytes.toBytes("age"),Bytes.toBytes(27));
        put.add(Bytes.toBytes("f"),Bytes.toBytes("phone"),Bytes.toBytes("021-11111111"));
        put.add(Bytes.toBytes("f"),Bytes.toBytes("email"),Bytes.toBytes("zhangsan@qq.com"));
        hTable.put(put);

        Put put1 = new Put(Bytes.toBytes("row2"));
        put1.add(Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes("2"));
        put1.add(Bytes.toBytes("f"), Bytes.toBytes("name"), Bytes.toBytes("user2"));

        Put put2 = new Put(Bytes.toBytes("row3"));
        put2.add(Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes("3"));
        put2.add(Bytes.toBytes("f"), Bytes.toBytes("name"), Bytes.toBytes("user3"));

        Put put3 = new Put(Bytes.toBytes("row4"));
        put3.add(Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes("4"));
        put3.add(Bytes.toBytes("f"), Bytes.toBytes("name"), Bytes.toBytes("user4"));

        List<Put> list = new ArrayList<Put>();
        list.add(put1);
        list.add(put2);
        list.add(put3);
        hTable.put(list);

        // 检测put
        Put put4 = new Put(Bytes.toBytes("row5"));
        put4.add(Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes("5"));
        hTable.checkAndPut(Bytes.toBytes("row5"), Bytes.toBytes("f"), Bytes.toBytes("id"), Bytes.toBytes("4"), put4);
    }
}
