package FileUtil;

import Learning.common.EncodedItem;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/13.
 */
public class LibSVMWriter {
    private BufferedWriter _writer;
    private String _spliter;
    public LibSVMWriter(BufferedWriter writer, String spliter){
        _writer = writer;
        _spliter = spliter;
    }

    public void writeInstance(EncodedItem item) throws IOException {
        _writer.write(item.TargetValue);
        _writer.write(_spliter);
        for(int i=0; i<item.Features.size(); i++){
            _writer.write(_spliter);
            _writer.write(item.Features.get(i).FeatureId);
            _writer.write(":");
            _writer.write(item.Features.get(i).FeatureValue);
            _writer.write('\n');
        }
    }

    public void Close() throws IOException{
        if(_writer != null){
            _writer.close();
        }
    }
}
