import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dmlc.xgboost4j.DMatrix;
import org.dmlc.xgboost4j.IEvaluation;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Main {


    public static void main(String[] args) throws IOException {
        DataLoader dataLoader = new DataLoader();
        //List<ods_yes_auction_invideo_dspinfo_log_d_right_data> e = dataLoader.loadFile_ods_yes_auction_invideo_dspinfo_log_d_right("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_invideo_dspinfo_log_d_right.txt");
        //List<ods_yes_auction_invideo_log_d_data> d = dataLoader.loadFile_ods_yes_auction_invideo_log_d("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_invideo_log_d.txt");
        List<ods_yes_auction_vhtml_biding_log_data> c = dataLoader.loadFile_ods_yes_auction_vhtml_biding_log("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_biding_log_d.txt");
        //List<ods_yes_auction_vhtml_dspinfo_log_d_data> a = dataLoader.loadFile_ods_yes_auction_vhtml_dspinfo_log_d("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_dspinfo_log_d.txt");
        // List<ods_yes_aution_vhtml_log_d_data> b =dataLoader.loadFile_ods_yes_aution_vhtml_log_d_data("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_aution_vhtml_log_d.txt");


    }

}
