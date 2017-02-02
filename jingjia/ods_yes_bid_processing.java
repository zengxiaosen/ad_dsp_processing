import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Administrator on 2017/1/14.
 */
public class ods_yes_bid_processing implements Algorithm {

    private String featureWords;
    private HashMap<String, Float> featureKV;

    private double threshold = -100;
    private double prob = 0.95;
    public void setProb(double prob) {
        this.prob = prob;
    }
    private double[] weights;

    public void init() throws Exception {

    }


    public static void main(String[] args) throws Exception {
        List<ods_yes_auction_invideo_dspinfo_log_d_right_data> e = null;
        List<ods_yes_auction_invideo_log_d_data> d = null;
        List<ods_yes_auction_vhtml_biding_log_data> c = null;
        List<ods_yes_auction_vhtml_dspinfo_log_d_data> a = null;
        List<ods_yes_aution_vhtml_log_d_data> b = null;
        DataLoader dataLoader = new DataLoader();
        //e = dataLoader.loadFile_ods_yes_auction_invideo_dspinfo_log_d_right("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_invideo_dspinfo_log_d_right.txt");
        //d = dataLoader.loadFile_ods_yes_auction_invideo_log_d("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_invideo_log_d.txt");
        c = dataLoader.loadFile_ods_yes_auction_vhtml_biding_log("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_biding_log_d.txt");
        //a = dataLoader.loadFile_ods_yes_auction_vhtml_dspinfo_log_d("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_dspinfo_log_d.txt");
        //b =dataLoader.loadFile_ods_yes_aution_vhtml_log_d_data("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_aution_vhtml_log_d.txt");
        for(int i=0;i<5; i++) {
            System.out.println();
        }
        Map<Double, Integer> bid_ = new HashMap<Double, Integer>(c.size());
        //Double[] BidPrice = new Double[c.size()];
        Map<String, Map<Double, Integer>> BidPrice = new HashMap<String, Map<Double, Integer>>();

        double bidprice_max = -1;
        ods_yes_auction_vhtml_biding_log_data object_max = null;

        for(int i=0; i<c.size(); i++){
            c.get(i).setBudget(500);
        }

        //<bidprice,ods_yes_auction_vhtml_biding_log_data>
        HashMap<Double, ods_yes_auction_vhtml_biding_log_data> kv = new HashMap<Double, ods_yes_auction_vhtml_biding_log_data>();
        //HashMap<String[],ods_yes_auction_vhtml_biding_log_data>,而这个string[]实际上就是<bidid,adxpid>
        HashMap<String, ods_yes_auction_vhtml_biding_log_data> kv1 = new HashMap<String, ods_yes_auction_vhtml_biding_log_data>();
        System.out.println("加载<bidprice,object>............");
        System.out.println("加载<adxpid,bidid>..........");
        for(int i=0; i< c.size(); i++){
            kv.put(Double.parseDouble(c.get(i).getBidprice()), c.get(i));
            String[] temp = new String[2];
            temp[0] = c.get(i).getAdxpid();
            temp[1] = c.get(i).getBidid();//竞价请求id
            String temp1 = temp[0]+","+temp[1];
            kv1.put(temp1, c.get(i));

        }
        System.out.println("加载相关Map完毕............");
        //对每个requestid,adxpid,impressid相同的分别处理，在这里头维护一个HashMap<String[],ods_yes_auction_vhtml_biding_log_data>,而这个string[]实际上就是<requestid,adxpid>
        //返回的map结构是：Map<String[], ArrayList<ods_yes_auction_vhtml_biding_log_data>>
        Map m = mapCombine(kv1);
        //System.out.println(m.toString());
        Iterator iter = m.entrySet().iterator();
        int count = 0;
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Object val =  entry.getValue();
            System.out.println("第"+count+"轮竞价：");
            System.out.println("adxpid,bidid: " + key);
            ArrayList<ods_yes_auction_vhtml_biding_log_data> blist = (ArrayList<ods_yes_auction_vhtml_biding_log_data>) val;
            System.out.println("这"+count+"轮竞价的所有dsp_id: ");
            for(int i=0; i< blist.size(); i++){
                ods_yes_auction_vhtml_biding_log_data bd = blist.get(i);
                System.out.print(bd.getDspid()+ ", ");
            }
            count ++;
            System.out.println();
            System.out.println();
        }

        System.out.println(m.size());


        for(int i=0; i< c.size(); i++){
            //日志里面分了若干轮竞价，分解每一轮

        }

        //一轮竞价
        for(int i=0; i< c.size(); i++){
            /*
               等处理完，把这段加进每轮竞价的数理统计，这里是整体的日志数理统计
               1，数理统计
                  对每个对象的bidprice进行排序，max（出价，胜出的花费，出价状态)
                  bidprice维护一个睿士内部第二高价
                  winprice是胜出价格，是每个dsp去竞争，胜出的dsp才有winprice，它是广义第二高价，是要去扣的。
               2, 补充一个topN
            */
            /*if(Double.parseDouble(c.get(i).getBidprice()) > bidprice_max && Integer.parseInt(c.get(i).getIswinnerdsp()) != 0){
                //最大bidprice的dsp的（bidprice，dsp对象）
                bidprice_max = Double.parseDouble(c.get(i).getBidprice());
                object_max = c.get(i);
            }*/



            //topN大bidprice的dsp的（bidprice，dsp对象）
            int N = 10;
            //List<Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data>>  TopN_bidprice = getTopN_bidprice(kv, N);//存bidprice的topN

            // 2，dsp在yes中参与竞价，判断是否竞价成功
            /*
            首先对于一个广告位：就是requestid, adxpid, （impressionid）相同
            对于一轮竞价，这三个字段是相同的
            由于atm参与竞价，所以看状态时要看status（包含Atm），同时也看iswinnerdsp
            对于赢得竞价的，我们在budget里面扣winprice（广义第二高价）
             */




            /*String bidId = c.get(i).getBidid();
            float ctr = 0;//待取
            double winningPrice = Double.parseDouble(c.get(i).getWinnercost());
            double bidPrice = Double.parseDouble(c.get(i).getBidprice());//理论上这个bidprice应该由ctr得出
            String bidid = c.get(i).getBidid();
            //double budget = 500;//预算
            //c.get(i).setBudget(500);
            int status = -1;
            c.get(i).setBidstatus_123(-1);
            //这是一个智能的判断是否有足够的bidprice
            if(c.get(i).isEnough(bidPrice)){
                status = (bidPrice >= winningPrice) ? 1 : 0;
                c.get(i).setBidstatus_123(status);
            }else{
                status = 2;
                c.get(i).setBidstatus_123(status);
            }
            bid_.put(bidPrice, status);
            //[bidid,[bidprice,status]]
            BidPrice.put(bidid,bid_);
            if(bid_.get(bidPrice) == 1){
                //重新调整预算，这里准确来讲应该不是扣bidPrice，而是扣广义第二高价GSP
                //c.get(i).setBudget(c.get(i).getBudget() - bidPrice);
                c.get(i).setBudget(c.get(i).getBudget() - gsp);
            }*/

            // 3，对于下一次bid，我要投多少，横向来看跟每次的bidprice,budget有关，跟自己的一些例如ip，adxpid等feature也有关，纵向来看跟其他dsp的bidprice有关


        }
        //System.out.println(object_max.toString());

        /*
        2，预测
        根据ods_yes_aution_vhtml_log_d_data的ip,cookie,bidid,website,mediaid,adxpid预测bidprice
         */


    }



    public static Map mapCombine(HashMap<String, ods_yes_auction_vhtml_biding_log_data> kv){
        Map<String, ArrayList<ods_yes_auction_vhtml_biding_log_data>> map2 = new HashMap<String, ArrayList<ods_yes_auction_vhtml_biding_log_data>>();

        List<Map.Entry<String, ods_yes_auction_vhtml_biding_log_data>> infoIds = new ArrayList<Map.Entry<String, ods_yes_auction_vhtml_biding_log_data>>(kv.entrySet());
        ArrayList<ods_yes_auction_vhtml_biding_log_data> tmpValue = new ArrayList<ods_yes_auction_vhtml_biding_log_data>();
        ArrayList<ods_yes_auction_vhtml_biding_log_data> tmpMap2Value = new ArrayList<ods_yes_auction_vhtml_biding_log_data>();
        for(Map.Entry<String, ods_yes_auction_vhtml_biding_log_data> entry : infoIds){
            tmpMap2Value.clear();
            tmpValue.clear();
            String entryKey = entry.getKey();
            ods_yes_auction_vhtml_biding_log_data entryValue = entry.getValue();
            if(map2.keySet().contains(entryKey)){
                tmpMap2Value = map2.get(entryKey);
                tmpMap2Value.add(entryValue);
                map2.put(entryKey, (ArrayList<ods_yes_auction_vhtml_biding_log_data>) tmpMap2Value.clone());
            }else{
                tmpValue.add(entryValue);
                map2.put(entryKey, (ArrayList<ods_yes_auction_vhtml_biding_log_data>) tmpValue.clone());
            }

        }
        System.out.println(map2.size());
        return map2;
    }

    public static List<Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data>>  getTopN_bidprice(HashMap<Double, ods_yes_auction_vhtml_biding_log_data> kv, int N){
        List<Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data>> infoIds = new ArrayList<Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data>>(kv.entrySet());
        //对hashmap中的key进行排序
        Collections.sort(infoIds, new Comparator<Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data>>() {
            public int compare(Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data> o1, Map.Entry<Double, ods_yes_auction_vhtml_biding_log_data> o2) {
                return (o2.getKey().toString()).compareTo(o1.getKey().toString());
            }
        });
        //对hashmap中的key，进行排序后，显示排序结果
        //for(int k=0; k< infoIds.size(); k++)
        for(int k=0; k< N; k++){
            String id = infoIds.get(k).toString();
            Double bidprice_forid = infoIds.get(k).getKey();
            System.out.println(bidprice_forid + " : " + id);
        }

        return infoIds.subList(0, N);
    }




    public int getBidPrice(BidRequest bidRequest) {
        return 0;
    }

    public void SvmBidingFeatureExtract(){

    }

    public void loadFeatureWord(String filePath){
        featureWords = null;
        List<String> featureWordsList = new ArrayList<String>();
        featureWordsList.add("bidid");
        featureWordsList.add("dspid");
        featureWordsList.add("adxpid");

    }

    public void writeToFile(String resultFile) throws IOException {
        FileWriter fw = new FileWriter(resultFile, false);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw, true);

        for(String k : featureKV.keySet()){
            pw.print(k);
            pw.print("::");
            pw.print(featureKV.get(k));
            pw.println();
        }

        pw.flush();
        pw.close();
        System.out.println("success");

    }


}
