import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import util.HBaseUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/8.
 */
public class TestHBaseAdmin {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseUtil.getHBaseConfiguration();
        HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);
        try {
            testCreateTable(hBaseAdmin);
            testGetTableDescribe(hBaseAdmin);
            testDeleteTable(hBaseAdmin);
        }finally {
            hBaseAdmin.close();
        }
    }

    /**
     * 测试创建table
     */
    static void testCreateTable(HBaseAdmin hbAdmin) throws IOException {
        TableName tableName = TableName.valueOf("users");
        if(hbAdmin.tableExists(tableName)){
            HTableDescriptor htb = new HTableDescriptor(tableName);
            htb.addFamily(new HColumnDescriptor("f"));
            htb.setMaxFileSize(10000L);
            hbAdmin.createTable(htb);
            System.out.println("创建表成功");
        }else{
            System.out.println("表存在");
        }

    }

    static void testGetTableDescribe(HBaseAdmin hbAdmin) throws IOException{
        //hbAdmin.createNamespace(NamespaceDescriptor.create("dd").build());
        TableName name = TableName.valueOf("users");
        if(hbAdmin.tableExists(name)){
            HTableDescriptor htd = hbAdmin.getTableDescriptor(name);
            System.out.println(htd);
        }else{
            System.out.println("表不存在");
        }

    }

    /**
     * 测试删除
     */
    static void testDeleteTable(HBaseAdmin hbAdmin) throws IOException{
        TableName name = TableName.valueOf("users");
        if(hbAdmin.tableExists(name)){
            if(hbAdmin.isTableDisabled(name)){
                hbAdmin.disableTable(name);
            }

            hbAdmin.deleteTable(name);
            System.out.println("删除成功");
        }else{
            System.out.println("表不存在");
        }


    }
}
