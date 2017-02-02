package utils.tools;

/**
 * Created by Administrator on 2017/1/17.
 */
public class DeviceConstants {
    public static final int UNKNOWN = -1;
    public static final int COOKIE = 1;
    public static final int IMEI = 2;
    public static final int IDFA = 3;
    public static final int MAC = 4;
    public static final String cookiePattern = null;
    public static final String imeiPattern = null;
    public static final String idfaPattern = null;
    public static final String macPattern = null;

    public static int checkDeviceType(String cookie){
        if(cookie.matches(cookiePattern)){
            return COOKIE;
        }else if(cookie.matches(imeiPattern)){
            return IMEI;
        }else if(cookie.matches(idfaPattern)){
            return IDFA;
        }else if(cookie.matches(macPattern)){
            return MAC;
        }else{
            return UNKNOWN;
        }
    }
}
