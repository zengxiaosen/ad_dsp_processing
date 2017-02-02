/**
 * Created by Administrator on 2017/1/6.
 */
public class ods_yes_auction_invideo_dspinfo_log_d_right_data {
    private String ds;//时间
    private String bidid;//唯一竞价请求id
    private String dspid;//dspid，>0的值，=1表示ATM
    private String adxpid;//adx广告位
    private String reqdbdealid;//请求的PDB dealid列表，可能是多个
    private String ext;//dsp唯独的扩展字段，rt=xxx：dsp的响应时间，单位为毫秒

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public String getDspid() {
        return dspid;
    }

    public void setDspid(String dspid) {
        this.dspid = dspid;
    }

    public String getAdxpid() {
        return adxpid;
    }

    public void setAdxpid(String adxpid) {
        this.adxpid = adxpid;
    }

    public String getReqdealid() {
        return reqdealid;
    }

    public void setReqdealid(String reqdealid) {
        this.reqdealid = reqdealid;
    }

    public String getReqdbdealid() {
        return reqdbdealid;
    }

    public void setReqdbdealid(String reqdbdealid) {
        this.reqdbdealid = reqdbdealid;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    private String reqdealid;//请求的普通dealid列表，可能有多个

    @Override
    public String toString() {
        return "ods_yes_auction_invideo_dspinfo_log_d_right_data{" +
                "ds='" + ds + '\'' +
                ", bidid='" + bidid + '\'' +
                ", dspid='" + dspid + '\'' +
                ", adxpid='" + adxpid + '\'' +
                ", reqdealid='" + reqdealid + '\'' +
                ", reqdbdealid='" + reqdbdealid + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }


}
