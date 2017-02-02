/**
 * Created by Administrator on 2017/1/7.
 */
public interface Algorithm {

    void init() throws Exception;

    int getBidPrice(BidRequest bidRequest);

}
