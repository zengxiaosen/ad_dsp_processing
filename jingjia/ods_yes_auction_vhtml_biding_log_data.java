/**
 * Created by Administrator on 2017/1/12.
 */
public class ods_yes_auction_vhtml_biding_log_data {
    private String ds;
    private String bidid;//唯一竞价请求id
    private String dspid;//dsp, >0的值，=1表示ATM
    private String adxpid;//adx广告位
    private String bidprice;//对一个位置的出价
    private String status;//一个位置的出价状态，包含ATM对该位置的出价状态
    private String iswinnerdsp;//是否在这个位置胜出，值为0,1,1位胜出
    private String winnercost;//胜出的花费
    private String advertiserid;//广告主id，原来这个位置是保量出价
    private String ideaid;//该DSP在该位置的返回素材
    private String adposition;//ATM相应的广告位id
    private String resdealid;//相应dealid，一个位置只能响应一个ideaid，如果没有相应记为0
    private String ideatype;//素材类型 0：普通 1：trueview
    private String idealength;//素材时长 秒
    private String ideabottomprice;//这个素材参与竞价使用的底板价，2015-10-16增加 15是使用15秒底板价，30秒使用30底板价，45秒使用45底板价，以此类推
    private String orderid;//ATM参与竞价时所对应的订单id
    private String castid;//ATM参与竞价时的素材所对应的投放id
    private String pdbdealid;//如果一个PDB请求，响应的是普通的deal。记录这个普通的deal对应的pdb的dealid
    private String cardid;//返回广告素材所使用的广告刊例id，不同行业刊例不同
    private String positioncount;//素材占用标准广告个数，普通素材根据时长折算，特殊素材计算实际占用

    @Override
    public String toString() {
        return "ods_yes_auction_vhtml_biding_log_data{" +
                "ds='" + ds + '\'' +
                ", bidid='" + bidid + '\'' +
                ", dspid='" + dspid + '\'' +
                ", adxpid='" + adxpid + '\'' +
                ", bidprice='" + bidprice + '\'' +
                ", status='" + status + '\'' +
                ", iswinnerdsp='" + iswinnerdsp + '\'' +
                ", winnercost='" + winnercost + '\'' +
                ", advertiserid='" + advertiserid + '\'' +
                ", ideaid='" + ideaid + '\'' +
                ", adposition='" + adposition + '\'' +
                ", resdealid='" + resdealid + '\'' +
                ", ideatype='" + ideatype + '\'' +
                ", idealength='" + idealength + '\'' +
                ", ideabottomprice='" + ideabottomprice + '\'' +
                ", orderid='" + orderid + '\'' +
                ", castid='" + castid + '\'' +
                ", pdbdealid='" + pdbdealid + '\'' +
                ", cardid='" + cardid + '\'' +
                ", positioncount='" + positioncount + '\'' +
                '}';
    }

    public boolean isEnough(Double bidprice){
        //从长远角度来看这次bidprice是否足够去投，要投还是不投
        return true;
    }

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

    public String getBidprice() {
        return bidprice;
    }

    public void setBidprice(String bidprice) {
        this.bidprice = bidprice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIswinnerdsp() {
        return iswinnerdsp;
    }

    public void setIswinnerdsp(String iswinnerdsp) {
        this.iswinnerdsp = iswinnerdsp;
    }

    public String getWinnercost() {
        return winnercost;
    }

    public void setWinnercost(String winnercost) {
        this.winnercost = winnercost;
    }

    public String getAdvertiserid() {
        return advertiserid;
    }

    public void setAdvertiserid(String advertiserid) {
        this.advertiserid = advertiserid;
    }

    public String getIdeaid() {
        return ideaid;
    }

    public void setIdeaid(String ideaid) {
        this.ideaid = ideaid;
    }

    public String getAdposition() {
        return adposition;
    }

    public void setAdposition(String adposition) {
        this.adposition = adposition;
    }

    public String getResdealid() {
        return resdealid;
    }

    public void setResdealid(String resdealid) {
        this.resdealid = resdealid;
    }

    public String getIdeatype() {
        return ideatype;
    }

    public void setIdeatype(String ideatype) {
        this.ideatype = ideatype;
    }

    public String getIdealength() {
        return idealength;
    }

    public void setIdealength(String idealength) {
        this.idealength = idealength;
    }

    public String getIdeabottomprice() {
        return ideabottomprice;
    }

    public void setIdeabottomprice(String ideabottomprice) {
        this.ideabottomprice = ideabottomprice;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getCastid() {
        return castid;
    }

    public void setCastid(String castid) {
        this.castid = castid;
    }

    public String getPdbdealid() {
        return pdbdealid;
    }

    public void setPdbdealid(String pdbdealid) {
        this.pdbdealid = pdbdealid;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getPositioncount() {
        return positioncount;
    }

    public void setPositioncount(String positioncount) {
        this.positioncount = positioncount;
    }

    private int bidstatus_123;

    public int getBidstatus_123() {
        return bidstatus_123;
    }

    public void setBidstatus_123(int bidstatus_123) {
        this.bidstatus_123 = bidstatus_123;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    double budget;//预算
}
