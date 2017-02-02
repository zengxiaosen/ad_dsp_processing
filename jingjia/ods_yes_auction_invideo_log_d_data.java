/**
 * Created by Administrator on 2017/1/7.
 */
public class ods_yes_auction_invideo_log_d_data {
    private String logversion;//日志版本
    private String ts;//时间戳（毫秒）
    private String ds;//日期
    private String ip;//访问用户ip
    private String cookie;//用户cookie标识
    private String bidid;//唯一竞价请求id
    private String website;//网站id
    private String mediaid;//媒体id
    private String adxpid;//adx广告位
    private String requestcount;//一个ADX能够填充的素材位置数（和acess日志的requestCount不同）
    private String ext;//扩展字段
    private String algoinfo;//算法扩展字段，格式由算法同学定义
    private String flowmark;//流量标记，格式：实验id1:策略id1|实验id2:策略id2|...（实验决策系统计划添加）
    private String reqadduration;//一次请求可以填充广告的总时长

    @Override
    public String toString() {
        return "ods_yes_auction_invideo_log_d_data{" +
                "logversion='" + logversion + '\'' +
                ", ts='" + ts + '\'' +
                ", ds='" + ds + '\'' +
                ", ip='" + ip + '\'' +
                ", cookie='" + cookie + '\'' +
                ", bidid='" + bidid + '\'' +
                ", website='" + website + '\'' +
                ", mediaid='" + mediaid + '\'' +
                ", adxpid='" + adxpid + '\'' +
                ", requestcount='" + requestcount + '\'' +
                ", ext='" + ext + '\'' +
                ", algoinfo='" + algoinfo + '\'' +
                ", flowmark='" + flowmark + '\'' +
                ", reqadduration='" + reqadduration + '\'' +
                '}';
    }

    public String getLogversion() {
        return logversion;
    }

    public void setLogversion(String logversion) {
        this.logversion = logversion;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getAdxpid() {
        return adxpid;
    }

    public void setAdxpid(String adxpid) {
        this.adxpid = adxpid;
    }

    public String getRequestcount() {
        return requestcount;
    }

    public void setRequestcount(String requestcount) {
        this.requestcount = requestcount;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getAlgoinfo() {
        return algoinfo;
    }

    public void setAlgoinfo(String algoinfo) {
        this.algoinfo = algoinfo;
    }

    public String getFlowmark() {
        return flowmark;
    }

    public void setFlowmark(String flowmark) {
        this.flowmark = flowmark;
    }

    public String getReqadduration() {
        return reqadduration;
    }

    public void setReqadduration(String reqadduration) {
        this.reqadduration = reqadduration;
    }


}
