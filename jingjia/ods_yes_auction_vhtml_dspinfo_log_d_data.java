/**
 * Created by Administrator on 2017/1/13.
 */
public class ods_yes_auction_vhtml_dspinfo_log_d_data {
    private String ds;
    private String bidid;//唯一竞价请求id
    private String dspid;//dspid , >0的值，=1表示ATM
    private String adxpid;//adx广告位
    private String reqdealid;//请求的普通dealid列表，可能有多个
    private String reqdbdealid;//请求的PDB dealid列表，可能是多个。如果没有PDB请求记录为0
    private String ext;//dsp维度的扩展字段。 rt=xxx:DSP的响应时间，单位为毫秒

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

    @Override
    public String toString() {
        return "ods_yes_auction_vhtml_dspinfo_log_d_data{" +
                "ds='" + ds + '\'' +
                ", bidid='" + bidid + '\'' +
                ", dspid='" + dspid + '\'' +
                ", adxpid='" + adxpid + '\'' +
                ", reqdealid='" + reqdealid + '\'' +
                ", reqdbdealid='" + reqdbdealid + '\'' +
                ", ext='" + ext + '\'' +
                '}';
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


}
