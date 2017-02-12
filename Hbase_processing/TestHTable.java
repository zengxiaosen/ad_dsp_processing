import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import scala.util.control.Exception;
import util.HBaseUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/2/9.
 */
public class TestHTable {
    static byte[] family = Bytes.toBytes("f");
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseUtil.getHBaseConfiguration();
        testUseHbaseConnectionPool(conf);
        /*HTable hTable = new HTable(conf, "users");
        try{
            testPut(hTable);
            testGet(hTable);
            testDelete(hTable);
        }finally {
            hTable.close();
        }*/
    }

    static void testUseHbaseConnectionPool(Configuration conf) throws IOException {
        //创建两个线程进行操作
        ExecutorService threads = Executors.newFixedThreadPool(2);
        HConnection pool = HConnectionManager.createConnection(conf, threads);
        HTableInterface hTable = pool.getTable("users");
        try{
            testPut(hTable);
            testGet(hTable);
            testDelete(hTable);
            testScan(hTable);
        }finally {
            hTable.close(); // 每次htable操作完关闭，其实是放到pool中
            pool.close(); // 最终时候的关闭
        }

    }

    /**
     * 测试put操作
     * @param hTable
     */
    static void testPut(HTableInterface hTable) throws IOException {
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

    /**
     * 测试get命令
     */
    static void testGet(HTableInterface htTable) throws IOException{
        Get get = new Get(Bytes.toBytes("row1"));
        Result result = htTable.get(get);
        byte[] buf = result.getValue(family, Bytes.toBytes("id"));
        System.out.println("id: " + Bytes.toString(buf));
        buf = result.getValue(family, Bytes.toBytes("age"));
        System.out.println("age: " +  Bytes.toInt(buf));
        buf = result.getValue(family, Bytes.toBytes("name"));
        System.out.println("name: " + Bytes.toString(buf));
        buf = result.getRow();
        System.out.println("row: " +  Bytes.toString(buf));
    }

    static void testDelete(HTableInterface hTable) throws IOException{
        Delete delete = new Delete(Bytes.toBytes("row3"));
        //删除列
        delete = delete.deleteColumn(family, Bytes.toBytes("id"));
        //delete.deleteColumn(family, Bytes.toBytes("name"));
        //delete.deleteFamily(family);
        hTable.delete(delete);
        System.out.println("删除成功");
    }

    static void testScan(HTableInterface hTable) throws IOException{
        Scan scan = new Scan();
        // 增加起始row key
        scan.setStartRow(Bytes.toBytes("row1"));
        scan.setStopRow(Bytes.toBytes("row5"));
        // 增加过滤filter
        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        byte[][] prefixes = new byte[2][];
        prefixes[0] = Bytes.toBytes("id");
        prefixes[1] = Bytes.toBytes("name");
        MultipleColumnPrefixFilter mcpf = new MultipleColumnPrefixFilter(prefixes);
        list.addFilter(mcpf);

        ResultScanner rs = hTable.getScanner(scan);
        Iterator<Result> iter = rs.iterator();
        while(iter.hasNext()){
            Result result = iter.next();
        }
    }


}
