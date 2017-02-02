package FileUtil;

import java.util.Hashtable;

/**
 * Created by Administrator on 2017/1/13.
 */
public class TSVLine {
    private Hashtable<String, Integer> _columnMapping = null;
    private String _spliter;
    String[] _fields;

    public TSVLine(String txtLine, String spliter, Hashtable<String,Integer> columnMapping){
        _columnMapping = columnMapping;
        _spliter = spliter;
        _fields = txtLine.split(_spliter);
    }

    private TSVLine(String spliter, Hashtable<String, Integer> columnMapping){
        _columnMapping = columnMapping;
        _spliter = spliter;
        _fields = new String[_columnMapping.size()];
    }
    public static TSVLine NewTSVLine(String spliter,Hashtable<String, Integer> columnMapping)
    {
        return new TSVLine(spliter,columnMapping);
    }
    public String GetColumnValue(String columnName)
    {
        return _columnMapping.contains(columnName) ? _fields[_columnMapping.get(columnName)] : null;
//		if(!_columnMapping.containsKey(columnName))
//		{
//			return null;
//		}
//		int columnIndex = _columnMapping.get(columnName);
//		return _fields[columnIndex];
    }
    public boolean SetColumnValue(String columnName, String columnValue)
    {
        if(!_columnMapping.containsKey(columnName))
        {
            return false;
        }
        int columnIndex = _columnMapping.get(columnName);
        _fields[columnIndex] = columnValue;
        return true;
    }

    public String ToString(){
        StringBuilder sbd = new StringBuilder(10000);
        sbd.append(_fields[0]);
        for(int i=1; i<_fields.length; i++){
            sbd.append(_spliter).append(_fields[i]);
        }
        return sbd.toString();
    }
}
