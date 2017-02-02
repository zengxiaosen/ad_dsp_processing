import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public class DataLoader {
    //ods_yes_auction_invideo_dspinfo_log_d_right_data对象列表
    private List<ods_yes_auction_invideo_dspinfo_log_d_right_data> ods_yes_auction_invideo_dspinfo_log_d_right_data_list = new ArrayList<ods_yes_auction_invideo_dspinfo_log_d_right_data>();
    private List<ods_yes_auction_invideo_log_d_data> ods_yes_auction_invideo_log_d_data_list = new ArrayList<ods_yes_auction_invideo_log_d_data>();
    private List<ods_yes_auction_vhtml_biding_log_data> ods_yes_auction_vhtml_biding_log_data_list = new ArrayList<ods_yes_auction_vhtml_biding_log_data>();
    private List<ods_yes_auction_vhtml_dspinfo_log_d_data> ods_yes_auction_vhtml_dspinfo_log_d_data_list = new ArrayList<ods_yes_auction_vhtml_dspinfo_log_d_data>();
    private List<ods_yes_aution_vhtml_log_d_data> ods_yes_aution_vhtml_log_d_data_list = new ArrayList<ods_yes_aution_vhtml_log_d_data>();
    private List<bid_log_data> bid_log_data_list = new ArrayList<bid_log_data>();

    public List<bid_log_data> loadFile_bid_log_data_list(String filePath)  throws FileNotFoundException, UnsupportedEncodingException, IOException{
        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split("!");
            if (items.length == 0) {
                continue;
            }
            bid_log_data o = new bid_log_data();
            o.setLog_version(items[0]);
            o.setLog_time(items[1]);
            o.setVisitor_ip(items[2]);
            o.setVisitor_cookie(items[3]);
            o.setAd_request_id(items[4]);
            o.setMedia_site_id(items[5]);
            o.setMedia_id(items[6]);
            o.setAd_dsp_info(items[7]);
            o.setAd_requst_count(items[8]);
            o.setExtension(items[9]);
            o.setAd_algo_info(items[10]);
            o.setAd_flow_mark(items[11]);
            o.setAd_request_duration(items[12]);

            bid_log_data_list.add(o);
            System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return bid_log_data_list;
    }

    public  List<ods_yes_auction_invideo_dspinfo_log_d_right_data> loadFile_ods_yes_auction_invideo_dspinfo_log_d_right(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {


        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split(",");
            if (items.length == 0) {
                continue;
            }
            ods_yes_auction_invideo_dspinfo_log_d_right_data o = new ods_yes_auction_invideo_dspinfo_log_d_right_data();
            o.setDs(items[0]);
            o.setBidid(items[1]);
            o.setDspid(items[2]);
            o.setAdxpid(items[3]);
            o.setReqdealid(items[4]);
            o.setReqdbdealid(items[5]);
            o.setExt(items[6]);

            ods_yes_auction_invideo_dspinfo_log_d_right_data_list.add(o);
            System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return ods_yes_auction_invideo_dspinfo_log_d_right_data_list;
    }

    public  List<ods_yes_auction_invideo_log_d_data> loadFile_ods_yes_auction_invideo_log_d(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split(",");
            if (items.length == 0) {
                continue;
            }
            ods_yes_auction_invideo_log_d_data o = new ods_yes_auction_invideo_log_d_data();
            o.setLogversion(items[0]);
            o.setTs(items[1]);
            o.setDs(items[2]);
            o.setIp(items[3]);
            o.setCookie(items[4]);
            o.setBidid(items[5]);
            o.setWebsite(items[6]);
            o.setMediaid(items[7]);
            o.setAdxpid(items[8]);
            o.setRequestcount(items[9]);
            o.setExt(items[10]);
            o.setAlgoinfo(items[11]);
            o.setFlowmark(items[12]);
            o.setReqadduration(items[13]);

            ods_yes_auction_invideo_log_d_data_list.add(o);
            System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return ods_yes_auction_invideo_log_d_data_list;
    }


    public  List<ods_yes_auction_vhtml_biding_log_data> loadFile_ods_yes_auction_vhtml_biding_log(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {


        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split(",");
            if (items.length == 0) {
                continue;
            }
            ods_yes_auction_vhtml_biding_log_data o = new ods_yes_auction_vhtml_biding_log_data();
            o.setDs(items[0]);
            o.setBidid(items[1]);
            o.setDspid(items[2]);
            o.setAdxpid(items[3]);
            o.setBidprice(items[4]);
            o.setStatus(items[5]);
            o.setIswinnerdsp(items[6]);
            o.setWinnercost(items[7]);
            o.setAdvertiserid(items[8]);
            o.setIdeaid(items[9]);
            o.setAdposition(items[10]);
            o.setResdealid(items[11]);
            o.setIdeatype(items[12]);
            o.setIdealength(items[13]);
            o.setIdeabottomprice(items[14]);
            o.setOrderid(items[15]);
            o.setCastid(items[16]);
            o.setPdbdealid(items[17]);
            o.setCardid(items[18]);
            o.setPositioncount(items[19]);

            ods_yes_auction_vhtml_biding_log_data_list.add(o);
            //System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return ods_yes_auction_vhtml_biding_log_data_list;
    }



    public  List<ods_yes_auction_vhtml_dspinfo_log_d_data> loadFile_ods_yes_auction_vhtml_dspinfo_log_d(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {


        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split(",");
            if (items.length == 0) {
                continue;
            }
            ods_yes_auction_vhtml_dspinfo_log_d_data o = new ods_yes_auction_vhtml_dspinfo_log_d_data();
           o.setDs(items[0]);
            o.setBidid(items[1]);
            o.setDspid(items[2]);
            o.setAdxpid(items[3]);
            o.setReqdbdealid(items[4]);
            o.setReqdbdealid(items[5]);
            o.setExt(items[6]);

            ods_yes_auction_vhtml_dspinfo_log_d_data_list.add(o);
            System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return ods_yes_auction_vhtml_dspinfo_log_d_data_list;
    }

    public  List<ods_yes_aution_vhtml_log_d_data> loadFile_ods_yes_aution_vhtml_log_d_data(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {


        File f = new File(filePath);
        FileInputStream in = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


        String line;

        while ((line = reader.readLine()) != null) {
            String[] items = line.trim().split(",");
            if (items.length == 0) {
                continue;
            }
            ods_yes_aution_vhtml_log_d_data o = new ods_yes_aution_vhtml_log_d_data();
            o.setLogversion(items[0]);
            o.setTs(items[1]);
            o.setDs(items[2]);
            o.setIp(items[3]);
            o.setCookie(items[4]);
            o.setBidid(items[5]);
            o.setWebsite(items[6]);
            o.setMediaid(items[7]);
            o.setAdxpid(items[8]);
            o.setDspinfo(items[9]);
            o.setRequestcount(items[10]);
            o.setExt(items[11]);
            o.setAlgoinfo(items[12]);
            o.setFlowmark(items[13]);
            o.setReqadduration(items[14]);

            ods_yes_aution_vhtml_log_d_data_list.add(o);
            System.out.println(o.toString());


        }

        reader.close();
        in.close();


        return ods_yes_aution_vhtml_log_d_data_list;
    }



}
