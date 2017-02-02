import java.util.*;

/**
 * Created by Administrator on 2017/1/19.
 */
public class test {
    private String featureWords;
    private HashMap<String, Float> featureKV;
    private double threshold = -100;
    private double prob = 0.95;
    public void setProb(double prob) {
        this.prob = prob;
    }
    private double[] weights;

    public static void main(String[] args) throws Exception{
        //数据加载和预处理
        DataLoader dataLoader = new DataLoader();
        List<ods_yes_auction_vhtml_biding_log_data> c = c = dataLoader.loadFile_ods_yes_auction_vhtml_biding_log("C:\\Users\\Administrator\\Desktop\\yes_日志\\ods_yes_auction_vhtml_biding_log_d.txt");
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
        //返回的map结构是：Map<String[], ArrayList<ods_yes_auction_vhtml_biding_log_data>>
        Map m = mapCombine(kv1);
        Iterator iter = m.entrySet().iterator();
        //每一轮竞价有关初始信息输出
        int count = 0;
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Object val =  entry.getValue();
            System.out.println("第"+count+"轮竞价：");
            System.out.println("adxpid,bidid: " + key);
            ArrayList<ods_yes_auction_vhtml_biding_log_data> blist = (ArrayList<ods_yes_auction_vhtml_biding_log_data>) val;
            System.out.println("第"+count+"轮竞价:"+"\n"+"所有dsp_id: ");
            for(int i=0; i< blist.size(); i++){
                ods_yes_auction_vhtml_biding_log_data bd = blist.get(i);
                System.out.print(bd.getDspid()+ ", ");
            }
            //对于每轮竞价中的所有dsp，查看状态iswinnerprice，来如果是，就动态调整dsp端的budget,得到调整budget后的那个object
            System.out.println("最高的bidprice，以及对应的广义第二高价winprice：");
            ods_yes_auction_vhtml_biding_log_data bb = GetBiggestBidAndUpdate(blist);
            if(bb == null){
                System.out.println("maxObject对应的dsp没有足够的budget");
            }
            System.out.println("maxObject调整价格后的budget:" + bb.getBudget());

            count ++;
            System.out.println();
            System.out.println();
        }
        System.out.println(m.size());

    }



    //<object,<bidprice,winprice>>
    public static ods_yes_auction_vhtml_biding_log_data GetBiggestBidAndUpdate(ArrayList<ods_yes_auction_vhtml_biding_log_data> b){
        double bidprice_max = -1;
        double winprice_max = -1;
        ods_yes_auction_vhtml_biding_log_data object_max = null;
        ArrayList<Double> d = new ArrayList<Double>();
        HashMap<ods_yes_auction_vhtml_biding_log_data, ArrayList> bb = new HashMap<ods_yes_auction_vhtml_biding_log_data, ArrayList>();

        for(int i=0; i<b.size(); i++){
            if(Double.parseDouble(b.get(i).getBidprice()) > bidprice_max ){
                bidprice_max = Double.parseDouble(b.get(i).getBidprice());
                winprice_max = Double.parseDouble(b.get(i).getWinnercost());
                object_max = b.get(i);
            }
        }
        System.out.println("<" + bidprice_max + "," + object_max.getWinnercost() + ">");
        d.add(bidprice_max);
        d.add(winprice_max);
        bb.put(object_max, d);
        if(object_max.getBudget() > winprice_max) {
            object_max.setBudget(object_max.getBudget() - winprice_max);
            return object_max;
        }else{
            return null;
        }

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

}
