package FileUtil;

import scala.collection.mutable.HashTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Administrator on 2017/1/13.
 */
public class HashSerializer {
    public static Hashtable<Integer, Integer> LoadHashtable(BufferedReader streamReader, String spliter, int keyIndex, int valueIndex) throws IOException {
        Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
        String textLine = null;
        while((textLine = streamReader.readLine()) != null){
            String[] fields = textLine.split(spliter);
            hashtable.put(Integer.parseInt(fields[keyIndex]), Integer.parseInt(fields[valueIndex]));
            textLine = streamReader.readLine();
        }
        return hashtable;
    }

    public static Hashtable<String, Integer> LoadStringHashtable(BufferedReader streamReader, String spliter, int keyIndex, int valueIndex) throws IOException {
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        String textLine = null;
        while((textLine = streamReader.readLine()) != null){
            String[] fields = textLine.split(spliter);
            hashtable.put(fields[keyIndex], Integer.parseInt(fields[valueIndex]));
            textLine = streamReader.readLine();
        }
        return hashtable;
    }

    public static Hashtable<Integer, Integer> LoadSeqHashtable(BufferedReader streamReader, String spliter, int keyIndex) throws IOException {
        Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
        int index = 1;
        String textLine = null;
        while((textLine = streamReader.readLine()) != null){
            String[] fields = textLine.split(spliter);
            hashtable.put(Integer.parseInt(fields[keyIndex]),index);
            textLine = streamReader.readLine();
            index ++;
        }
        return hashtable;
    }

    public static Hashtable<String, Integer> LoadStrSeqHashtable(BufferedReader streamReader, String spliter, int keyIndex) throws IOException {
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        int index = 1;
        String textLine = null;
        while((textLine = streamReader.readLine()) != null){
            String[] fields = textLine.split(spliter);
            hashtable.put(fields[keyIndex], index);
            textLine = streamReader.readLine();
            index ++;
        }
        return hashtable;
    }
}
