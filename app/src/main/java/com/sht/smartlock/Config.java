package com.sht.smartlock;

import android.content.SharedPreferences;
import android.os.Environment;

import com.sht.smartlock.util.DateUtil;


public class Config {
    public static final String APP = "smartlock";
    //私人
    //09b29fa4e386470f578339ffdb9e9e6e  微信MD5签名
    //测试key
//    public static final String APPID_WEIXIN = "wx102b99845171327e";
//    public static final String APPID_QZONE = "1104805211";
//    public static final String SECRET_WEIXIN = "21d2eedc1148497b456555d2f6fd128d";
//    public static final String SECRET_QZONE = "yaHeFXaEc87GflNY";

    //正式key
    public static final String APPID_WEIXIN = "wx58fb96f5bcbbb8d4";
    public static final String SECRET_WEIXIN = "c702c77a1745e9deedf9bd2ee1f665b1";
    public static final String APPID_QZONE = "1105780645";
    public static final String SECRET_QZONE = "ySqNMoi76qjJGebt";
    public static final String XINLANG_ID="1130678623";
    public static final String XINLANG_SERVICE="b699ecfbca33877f89ad3f2a12a06063";

    public static final String DESCRIPTOR = "com.umeng.share";
    public final static int PAGESIZE = 20;
    public final static int DEFAULT_LISTDIALOG_ID = 1000;

    public final static String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APP;
    public final static String UPDATE_APK_PAHT = BASE_DIR + "/" + "update";
    public final static String SELECT_IMAGE_SAVE_PAHT = BASE_DIR + "/" + "camera";
    public final static String IMAGE_SAVE_PAHT = BASE_DIR + "/" + "images";
    public final static String IMAGE = "/" + APP + "/" + "images/";
    public final static String CACHE_IMAGE_SAVE_PAHT = IMAGE_SAVE_PAHT + "/" + "cache/";
    public final static String LOG_PAHT = BASE_DIR + "/" + "log";

    public final static String HOTEL_TYPE = "HOTEL_TYPE";
    public static final String KEY_HOTEL_URL = "HOTEL_URL";
    public static final String KEY_HOTEL_ID = "hotelid";
    public static final String ORDER_PRICE = "ORDER_PRICE";
    public static final String KEY_HOTEL_CAPTION ="hotelcaption" ;
    public static final String KEY_HOUR_CHECKIN="key_hour_checkin";
    public static final String KEY_MAIN_CURRENT_PAGE="main_current_page";
    public static final String CACHE_HOTELID="cache_hotelid";
    public static final String CACHE_HOTELCAPTION="cache_hotelcaption";
    public static final String CACHE_HOTELURL="cache_hotelurl";


    public static final String RANKTYPE_PRICE = "price";
    public static final String RANKTYPE_DISTANCE = "distance";
    public static final String RANKORDER_ASC = "asc";
    public static final String RANKORDER_DESC = "desc";
    public static final String KEY_HOTEL_ROOMID = "roomid";
    public static final String KEY_CHECKIN_DATE = "BookingDateStart";
    public static final String KEY_CHECKOUT_DATE= "BookingDateEnd";
    public static final String KEY_HOTEL_ROOMCAPTION ="roomcaption" ;
    public static final String KEY_HOTEL_ROOMPRICE ="roomprice" ;
    public static final String KEY_ARRIVE_TIME = "arrive_time";
    public static final String KEY_ARRIVE_HOTELID="key_arrive_hotelid";
    public static final String KEY_ARRIVE_STARTTIME="key_arrive_starttime";
    public static final String KEY_BOOKING_BILLNUM ="booking_billnum" ;
    public static final String KEY_HOTEL_ROOMCOUNT ="hotel_roomcount" ;
    public static final String KEY_HOTEL_ROOMTOTALPRICE ="hotel_roomtotalprice" ;
    public static final String KEY_HOTEL_LIST ="hotel_list" ;
    public static final String KEY_HOUR_STARTTIME ="hour_starttime" ;
    public static final String KEY_HOUR_REAL_CURRENT ="key_hour_real_current" ;

    public static final String KEY_SHOW_ROOM_MODE="show_room_mode";
    public static final String KEY_HOUR_ROOM_SPAN_LIMIT ="hour_room_span_limit";
    public static final String KEY_HOUR_ROOM_SPAN ="hour_room_span";
    public static final String KEY_BILL_TYPE ="key_bill_type";
    public static final int BILL_TYPE_BOOK =1;
    public static final int BILL_TYPE_SERVER =2;


    public static final String ROOM_MODE_DAY="room_mode_day";
    public static final String ROOM_MODE_HOUR="room_mode_hour";
    public static final String ROOM_MODE_ALL="room_mode_all";


  //  public static final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
    public static final String IS_HOUR_ROOM ="is_hour_room" ;
    public static final String KEY_HOTEL_IS_COLLECT = "key_hotel_is_collect";
    public static final String KEY_IS_PAY_FIRST ="key_is_pay_first" ;
    public static String SHARE_USER_Name = "SHARE_USER_ACCOUNT_UserName";
    public static String SHARE_USER_ACCOUNT = "SHARE_USER_ACCOUNT";
    public static final String SHARE_USERPWD = "SHARE_PASSWORD";
    public static String SHARE_USERSESSIONID = "SHARE_SESSIONID";
    public static String KEY_USER_NICKNAME="key_user_nickname";
    public static String KEY_USER_NAME="key_user_name";
    public static String SHARE_VOIP_ACCOUNT = "SHARE_VOIP_ACCOUNT";
    public static final String SHARE_VOIP_PED = "SHARE_VOIP_PED";

    public static final String MY_PREFERENCES = "MY_PREFERENCES";    //Preferences文件的名称
    public static final String MY_ACCOUNT = "MY_ACCOUNT";            //
    public static final String MY_PASSWORD = "MY_PASSWORD";
    public static final String ORDER_NUMVER="result";//订餐 购物订单号
    public static final String ORDER_TAB="tab";//区别 是购物 或者是订餐 1 购物 2 订餐
    public static final String REMARKS="Remarks";//备注
    public static final String SOUND_AND_VIBRATTON="sound_and_vibration";//震动
    public static final String CLOSE_SOUND="close_sound";//声音、开启或者关闭
    public static final String VIBRATION="Vibration";//震动
    public static final String OPENDOOR_PWD="opendoor_pwd";
    public static final String OPENDOOR_PWD_SAVE="opendoor_pwd_save";
    public static final String LOGIN_UNLOCK_PWD="set_unlock_pwd";
    public static final String OPENDOOR_MYOPENPWD="opendoor_myopenpwd";
    public static final String SERVICE_TYPE="service_type";//moringCall，清扫，其他，换床单

    public static final String PAYACTION="payAction";
    public static final String ORDERING="ordering";
    public static final String BOOKING="booking";
    public static final String HUANXINChATTIME="huanxinchattime";
    public static final String ROOM_ID="room_id";
    public static final String MAINORORDER="mainororder";
    public static final String MAINGOBACK="maingoback";

    public static final String ISFIRSTLOGIN="ISFIRSTLOGIN";//判断是否第一次登录

    public static final String ISBLACK="isBlack";

    //游客的环信账号
    public static final String HUANXINUSER="huanxinuser";
    public static final String HUANXINPWD="huanxinpwd";
    //我的环信密码
    public static final String EMID="emid";//环 信账号
    public static final String MYHUANXINPWD="myhuanxinpwd";

    public static final String BOOK_ID="book_id";

    public static final String POWER="POWER";
    public static final String ISFIRST="ISFIRST";

    public static final String MYFRIENDSTR="myfriendstr";




    public static final String LATITUDE="latitude";
    public static final String LONGITUDE="longitude";
    public static final String ADDRESS="address";

    public static final String PAY_TYPE="paytype";
    public static  String PUCLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDE0xU6rmxNjHE9Dr5dJ9QDx4vK"+"\r"
            +"58GwpiSAX57TNxlhLGylOKXiQXop8j7p6r6m4LJfGZM6ZxbNb9a83fP1x1NjnxyW"+"\r"
            +"YsVcbOYrnGqU9Cm5A8em938qOnY8V3wTDweoUPBjeZeuBE9LaPW7QNEZwwQSQNYs"+"\r"
            +"Jx9MemWTYyPLiEnkeQIDAQAB";
    public static  String PRIVATE_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMTTFTqubE2McT0O"+"\r"
            +"vl0n1APHi8rnwbCmJIBfntM3GWEsbKU4peJBeinyPunqvqbgsl8ZkzpnFs1v1rzd"+"\r"
            +"8/XHU2OfHJZixVxs5iucapT0KbkDx6b3fyo6djxXfBMPB6hQ8GN5l64ET0to9btA"+"\r"
            +"0RnDBBJA1iwnH0x6ZZNjI8uISeR5AgMBAAECgYAYTiy382Qia25saM6vOEThNpoX"+"\r"
            +"s2jixJGLXMODN7Glo1PDT2yEtSs91NFaF4f1mZfNUJ4yvxo8wkvVRN9kgXnVgs8i"+"\r"
            +"uf2JREOJTSQZI8pqfE5FslTvCT3ozTexRrQoeIwMaCBDzOVpKznUmMRhdJUbxlha"+"\r"
            +"xeN+wXNkl7Hq7BcOEQJBAPg6mv8S6Hqft+8XBffyo5BLgbZCJ0hpbyAQmGpxJ9lM"+"\r"
            +"0jtStKFXvuofWMSj34dEWdTiIGWYm0iyPUpIaBWpt10CQQDK/IEoiVKk2HruWYc1"+"\r"
            +"CnEhV/VH/NmAbbi2+igaDnIlseXy7oaGUz+sElVe0pgOTp+rwGL9dfhOIhx11vjY"+"\r"
            +"xFvNAkATzEDOdUibdx+pbxGny+9ls8/9pFsTuG0HhqtRWgMOnsTpvFfxwljUvXlL"+"\r"
            +"/bH3TOlJRFvHHpvL+YNP6GR8zdN1AkBFvc3OlKftdWd/PwliYu0NrL/cdPZx1sDx"+"\r"
            +"NS/UDASSCPZdqjBUNIQa5c2pH6gYulDH3Pjl5sNibPvkTPEA4S49AkAY8boqnfPh"+"\r"
            +"MthrIDDDbExwvZy3SqDhGpiDqSctZQZHzmlD16vNMpIACDK5XMtfDMRj7FHf36bl"+"\r"
            +"YYLbonTa6krV"+"\r";

    public static  final String TOG_STATE_TYPE="TOG_STATE_TYPE";
    public static  final String TOG_OPENDOOR_TYPE="TOG_OPENDOOR_TYPE";

    //b72c56129f88     26f487ef172e4a244801e5aebc1ba961     abb952b1b444   9536f66208f5c624df6a9b61245ca771
    //b78b5ea8e27a     172ebca0280b6e12539055c3f6108798
    public static String APPKEY = "b72c56129f88";//463db7238681  27fe7909f8e8
    // 填写从短信SDK应用后台注册得到的APPSECRET
    public static String APPSECRET = "26f487ef172e4a244801e5aebc1ba961";//9cd46d6cf9908a297fbafeb75b7b19d3

    public static String getLogName() {
        long timestamp = System.currentTimeMillis();
        String time = DateUtil.getCurrentDateStr(DateUtil.LOG_PATTERN);
        return "error-" + time + "-" + timestamp + ".log";
    }

    public static String VOIPappKey="8a48b5515438446d01543847d1480001";
    public static String VOIPtoken="30dbe749b04c39f3290a53c0c6ed1dbc";


    public static String INVOICETITLE = "INVOICETITLE";

    public static String AMOUNT_ID = "AMOUNT_ID";//酒店账号id
    public static String BALANCE_ID = "BALANCE_ID";//余额id
    public static String BALANCE = "BALANCE";//余额
    public static String HOTELNAME = "HOTELNAME";//酒店名称
    public static String COMMENT = "COMMENT";//充值有礼提示文字
    public static String HOTELCHAIN = "HOTELCHAIN";//连锁酒店
    public static String ITEMSLIST = "HOTELCHAIN";//连锁酒店列表

    public static String CHINCKIN_ID="chinckin_id";
    public static String CHINCKIN_TRUE="chinckin_true";
    public static String CHINCKIN_LAD="chinckin_Latitude";//纬度
    public static String CHINCKIN_LON="chinckin_Longitude";//经度
    public static String CHINCKIN_ROOMID="chinckin_roomid";
    public static String CHINCKIN_HOTELNAME="chinckin_hotelcaption";
    public static String DISTANCE="distance";
    public static String CHINCKIN_BOOKID="chinkin_book_id";

    public static String SERVICECHAT="SERVICECHAT";

    public static String NICKNAME="myNickName";
    public static String MyImg="myimg";

    public static String CLOSEPOWER="CLOSEPOWER";//是否入住中
    public static String WAITERJSON="WAITERJSON";//酒店前台用户改为店小二


    public static String ISBACKGROUD="isbackgroud";//是否在后台

//    public static String LEAVE_STATE="leave_state";
    public static String LEAVE_STATE02="leave_state02";//离店状态
    public static String ISNOTCLOSEPOWER="isnotclosepower";

    public static String MYLOCKORDER="mylockorder";//我订的酒店json数据
    public static String MYLOCKBOOKID="mylockbookid";//客人离店状态
    public static String MYLOCKBOOKID_ROOMID="mylockbookid_roomid";//客人离店状态
    public static String ISCLOSE="isclose";

    public static String MYHOTEL_CAPTION="myhotel_caption";

    public static String RANDOMPWD="RANDOMPWD";
    public static String SCANNAME="SCANNAME";
    public static String SCANNAMEPWD="SCANNAMEPWD";
    public static String URLFORREANDIN="URLFORREGISTERANDIN";
    public static String OLDPHONE="OLDPHONE";

    public static String ISFIRSTLOGINTOGUIDE="ISFIRSTLOGINTOGUIDE";
    public static String USERPHONE="USERPHONE";

    public static String ISBINDPHONE="isbindphone";

    public static String PRICEORDISTANCE="1";//1降序，2升序，其他（3），距离最近
    public static String SORTFORNEARHOTEL="1";//1降序，2升序，其他（3），距离最近

    public static String PAYDATApayAction="PAYDATApayAction";
    public static String PAYDATAstrBillnum="PAYDATAstrBillnum";
    public static String PAYDATAroom_mode="PAYDATAroom_mode";
    public static String PAYDATAtab="PAYDATAtab";
    public static String PAYDATAroom_id="PAYDATAroom_id";

    public static final String PAYDATARechargeActivity="PAYDATARechargeActivity";

    public static String LOCKDETAILDATA="LOCKDETAILDATA";

    public static String LOCATIONCITY="LOCATIONCITY";

    public static String ISNEAR="ISNEAR";
    public static String LOCKPIC="LOCKPIC";

    public static String IDALLDAY="全日房";
    public static String IDHOUREDAY="钟点房";

    public static String MoreServicerOrderId="MoreServicerOrderId";
    public static String MoreServicerOrderPrice="MoreServicerOrderPrice";
    public static String ISMORESERVICER="ISMORESERVICER";

    public static String TASKTYPE="TASKTYPE";

    public static String TASKEntity="TASKENTITY";

    public static String SERVICERMESSAGE="SERVICERMESSAGE";
    public static String SERVICERMESSAGE_BYSRID="SERVICERMESSAGEBYSRID";

    public static String SERVICETYPEBy1="SERVICETYPEBy1";//清扫服务
    public static String SERVICETYPEBy2="SERVICETYPEBy2";//更换服务
    public static String SERVICETYPEBy4="SERVICETYPEBy3";//其他服务

    public static String SONSUMPTION_ORDERID="consumption_order_id";
    public static String SONSUMPTION_ORDERIDtotal="SONSUMPTION_ORDERIDtotal";
    public static String GTCID="GTCID";
//    public static String GTCIDBYBroadcast="GTCIDBYBroadcast";
    public static String GTCIDBYJSOn="GTCIDBYJSOn";
    public static String GTCIDBYISTRUE="GTCIDBYISTRUE";
    public static String GTCIDBYORDERID="GTCIDBYORDERID";
    public static String GTCIDBYISTRUEIN="GTCIDBYISTRUEIN";
    public static String GTCIDBYCONFIRM="GTCIDBYCONFIRM";
    public static String ISBINDGTCID="ISBINDGTCID";
    public static String GTBOOKINGID="GTBOOKINGID";
    public static String GTBOOKINGIDBySAVE="GTBOOKINGIDBySAVE";

    public static String ISFALSEGUI="ISFALSEGUI";
    public static String ISFALSEGUIBYSKIT="ISFALSEGUIBYSKIT";

    public static String ISChANGPWD="ISChANGPWD";
    public static String ISChANGPWDByFIRST="ISChANGPWDByFIRST";
    public static String CHANGPWDPHONE="CHANGPWDPHONE";



}
