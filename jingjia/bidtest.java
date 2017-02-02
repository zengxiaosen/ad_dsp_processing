import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */
public class bidtest {
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
        List<bid_log_data> c = dataLoader.loadFile_bid_log_data_list("C:\\Users\\Administrator\\Desktop\\yes_日志\\bid_log.txt");
        //把ad_dsp_info字段数据load到本class的dspinfo对象字段
        for(int i=0; i<c.size(); i++){
            //每次竞价维护的多家dsp数组
            String[] dspitems = c.get(i).getAd_dsp_info().trim().split("#");
            if(dspitems.length == 0){
                continue;
            }
            for(int j=0; j< dspitems.length; j++){
                //把每家dsp信息打到dspinfo对象里面
                String[] dspObjects = dspitems[j].trim().split("^");
                String dspid = dspObjects[0];
                //String biding = dspObjects[1];
                String adxPid = dspObjects[2];
                String reqDealId = dspObjects[3];
                String reqPdbDealId = dspObjects[4];
                String ext = dspObjects[5];

                bid_dspinfo_data b = new bid_dspinfo_data();
                b.setDspid(dspid);
                //b.setBiding(biding);
                b.setAdxPid(adxPid);
                b.setReqDealId(reqDealId);
                b.setReqPdbDealId(reqPdbDealId);
                b.setExt(ext);

                c.get(i).addToDspinfodata(b);
            }
            System.out.println("第"+i+"次竞价:维护的各个dsp数据加载完成");
        }

        //拿到budget,日志中没有budget，所以设为500


    }
}
