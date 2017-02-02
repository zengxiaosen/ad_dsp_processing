package ds.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/1/13.
 */
public class ModelTest {
    public static void main(String[] args){
        System.out.println("Test start!");
        //TestFormat();
        //TestReflect();
        //TestSplit();
        //TestOFEFeatures();
        //TestLRModel();
        //TestDate();
        TestFile();
        TestCBTModel();
        System.out.println("Test over!");
    }

    public static void TestFile(){
        File file = new File("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_biding_log_d.txt");
        System.out.println(file.getName());
    }

    public static void TestCBTModel(){
        SparkGBTModel model = new SparkGBTModel();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_biding_log_d.txt")));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
