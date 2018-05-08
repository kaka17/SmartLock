package com.sht.smartlock.api.base;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.util.LogUtil;
import com.thetransactioncompany.jsonrpc2.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.entity.StringEntity;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpClient extends BaseHttpClient {
    //     public static final String BASEPATH = "http://192.168.1.88/smartlock/server.php";
//    public http://192.168.1.84/smartlocktest_1/server.php  //测试服务器
        public static final String BASEPATH = "http://192.168.1.84/smartlocktest/server.php";
//       public static final String BASEPATH = "http://www.smarthoteltech.cn/smartlock2/server.php"; //测试服务器 //测试服务器
//      public static final String BASEPATH = "http://192.168.1.84/smartlock/server.php"; //测试服务器
//    public static final String BASEPATH = "http://www.smarthoteltech.cn/smartlock2/server.php";//主干
//   public static final String BASEPATH = "http://srv1.smarthoteltech.cn:8088/smartlock/server.php"; //运营外网

    public static final String IMAGE_BASE_PATH = "";
//    public static final String LOGIN = "test.subtract";

    //酒店预订
    public static final String BOOK_SEARCH = "book.search";
    public static final String BOOK_NEAR_HOTEL = "book.near_hotel";
    public static final String BOOK_ADD_HOTEL_FAVORITES = "user.addhotel_favorites";
    public static final String BOOK_DEL_HOTEL_FAVORITES = "user.delhotel_favorites";
    public static final String BOOK_GET_ROOM_TYPE_LIST = "book.get_room_type_list";
    public static final String BOOK_GET_HOURROOM_TYPE_LIST = "book.get_hourroom_type_list";
    public static final String BOOK_SUBMIT_BOOK = "book.submit_book";
    public static final String BOOK_SUBMIT_HOURBOOK = "book.submit_hourbook";//钟点房提交  旧
    private static final String BOOK_PAY_BILL = "pingpp.getCharge";
    public static final String BOOK_DESTINATION = "book.destination";
    public static final String BOOK_CITY = "book.city_hotel";
    public static final String BOOK_GET_ROOM_BOOKING = "book.get_room_booking_details";
    public static final String BOOK_GET_CHECKOUT_TIME = "book.get_checkout_time";

    public static final String HUANXIN_CREATEUSER = "huanxin.createUser";
    public static final String HUANXIN_REGISTERUSER = "huanxin.registerUser";
    public static final String HUANXIN_SEARCHHOTEL = "huanxin.searchHotel";//搜索聊天室
    public static final String SERVICE_UNLOCK = "service.unlock";//开门
    public static final String SERVICE_UNLOCK_LIFT = "service.unlock_lift";//电梯
    public static final String SERVICE_MYSELF_CHECKIN = "service.myself_checkin";//自助checkin
    public static final String SERVER_GETSETVICECATALOG = "service.getServiceCatalog";//获取订餐/购物的分类
    public static final String SERVICE_GETSERVICELIST = "service.getServiceList";//获取订餐/购物/--分类对应的list表
    public static final String SERVICE_ADDSERVICE = "service.addService";//添加服务订单，（下单）
    public static final String HUANXIN_CREATECHATROOM = "huanxin.createChatrooms";//创建聊天室
    //    public static final String MYLOCKINFO="book.get_room_booking_by_user_id";//根据use_id获取用户订了那些酒店，
    private static final String MODIFY_PAY_SERVICE = "service.modify_pay_service";//用户对订餐和购物订单支付状态进行修改。
    private static final String GET_MYROOM_UNLOCK_LIST = "service.get_myroom_unlock_list";//开门记录
    private static final String GET_MYSERVICEORDER_DETAIL = "user.get_myserviceorder_detail";//根据订单号，获取用户信息
    private static final String NEAR_HOTELCHATROOM = "user.near_hotelchatroom";//根据经纬度获取周边酒店的聊天室
    //我的
    public static final String GET_INVOICE_TITLE_LIST = "user.get_invoice_title_list";//获取发票抬头
    public static final String set_user_head_image = "user.set_user_head_image";//获取发票抬头
    public static final String MY_LOGIN = "user.login";//登录
    public static final String MY_REGIST = "user.register";//注册
    public static final String MY_INFO = "user.myinfo";//我的资料
    public static final String MY_ODERFORM = "user.myhotel_order_list";//我的酒店订单
    public static final String MY_DINCHAN = "user.mydinchan_list";//我的酒店订餐
    public static final String MY_SHOPPING = "user.myshopping_list";//我的酒店购物
    public static final String My_COLLECTIONACTIVITY = "user.myhotel_collection_list";//我的收藏
    public static final String ADDHOTEL_FAVORITES = "user.addhotel_favorites";//添加一个酒店到收藏列表
    public static final String DELHOTEL_FAVORITES = "user.delhotel_favorites";//删除一个酒店收藏
    public static final String MYHOTEL_COMMENT = "user.myhotel_comment_list";//我的点评
    public static final String ADDHOTEL_COMMENT = "user.addhotel_comment";//添加一个酒店点评
    public static final String GET_ROOM_CHECKIN_USER = "book.get_room_checkin_user";//返回入住的酒店房号信息，用于开门界面的显示。
    public static final String GET_USER_MYINFO = "user.myinfo";//获取我的资料
    public static final String LOGIN_OUT = "user.login_out";//退出登录
    public static final String MY_MODIF_USER_UNLOCK_PWD = "user.modify_user_unlock_pwd";//设置密码（原始密码为空，）修改密码（）
    public static final String RESET_USER_LOGIN_PWD = "user.reset_user_login_pwd";//忘记密码
    public static final String SET_USER_TRUE_NAME = "user.set_user_true_name";//修改用户名
    public static final String SET_USER_ID_NO = "user.set_user_id_no";//修改身份证号码
    public static final String MY_CANCLE_ORDER = "book.cancel_order";//取消订单
    public static final String MODIFY_USER_LOGIN_PWD = "user.modify_user_login_pwd";//修改用户密码
    public static final String GETALLSERVICELIST = "service.getAllServiceList";
    public static final String SET_USER_HEAD_IMAGE = "user.set_user_head_image";//设置用户头像
    public static final String GET_USER_HEAD_IMAGE = "user.get_user_head_image";//获取用户头像
    public static final String UPDATE_INVOICE_TITLE_LIST = "user.update_invoice_title_list";//上传发票抬头
    public static final String CANCEL_SERVICE_ORDER = "service.cancel_service_order";
    public static final String REQUEST_VERIFY_CODE = "user.request_verify_code";
//    public static final String REFUND_ORDER = "book.refund_order";//退订
    public static final String REFUND_ORDER = "book.new_refund_order";//新退订
    public static final String ORDER_EXPIRED = "book.order_expired";
    public static final String RESET_USER_UNLOCK_PWD = "user.reset_user_unlock_pwd";//重置开门密码
    public static final String RECHARGE_ACCOUNT_USER = "user.recharge_account_user";//我的余额列表
    public static final String RECHARGE_ACCOUNT_USER_DETAILS = "user.recharge_account_user_details";//余额明细
    public static final String RECHARGE_ACCOUNT_BALANCE="user.recharge_account_balance";
    public static final String SEARCH_FRIENDS="user.search_friends";

    public static final String SERVIC_TAKE_POWER="service.user_take_power";//取电
    public static final String MY_BILL_LIST="service.my_bill_list";//我的账单列表


    //钟点房
    public static final String BOOK_HOURSEARCH = "book.hoursearch";
    public static final String BOOK_NEARHOUR = "book.nearhour";
    public static final String SERVICE_SETFREE = "service.setroomtrouble";//设置免打扰
    public static final String SERVICE_GET_ROOM_TROUBLE = "service.get_room_trouble";//获取免打扰状态
    public static final String SERVICE_HOTELSERVICE = "service.addhotelService";//1房间清扫 2更换床上用品 3设置morningcall  4其他
    public static final String SERVICE_GET_ROOM_NO = "book.get_room_checkin_user";//获取用户入住酒店房间号，id
    public static final String MY_GET_ROOM_BOOKING_DETAILS = "book.get_room_booking_details";//获取订单状态信息


    public static final String CHECK_LATEST_APP_VERSION = "service.check_latest_app_version";//检查版本更新
    public static final String SET_USER_ID_IMAGE = "user.set_user_id_image";//设置身份证
    public static final String GET_MYHOTESERVICE_LIST = "service.get_myhotelService_list";

    public static final String RE_SUMBIT_SERVICE="service.re_sumbit_service";

    public static final String GET_OR_CREATE_RECHARGE_USER="book.get_or_create_recharge_user";//充值前调用
//    public static final String RECHARGE_CALLBACK="book.recharge_callback";//充值后回调
    public static final String IS_ROOM_CHINCKINUSER_POWER="book.is_room_checkin_user_poweroff";//判断是否入住了
    //环信好友列表
    public static final String USER_MY_FRIENFS_LIST="user.my_friends_list";

    public static final String RECHARGE_BEFORE="book.recharge_before";

    public static final String BOOK_LEAVE_ROOM="service.leave_room";//客户 距离酒店的状态

    public static final String WAITER="user.waiter";//聊天室显示前台用户为店小二

    public static final String CHECKOUT="service.checkout";//自助退房
    public static final String GETTOCHENKIN="service.pledge_checkout";//自助退房 余额支付或者押金支付
    public static final String NEWUSER_UNLOCK="service.user_unlock";
    public static final String MY_BILL_DETAIL="service.my_bill_detail";//我的账单详情接口

    public static final String IS_RECEPTIONIST="service.is_receptionist";
    public static final String JUDGE_APP_UPDATE="service.judge_app_update";//检查版本更新

    //一键注册登录接口
    public static final String USER_REGISTER="user.user_register";
    public static final String CHANFPWD="user.modify_user_password";
    public static final String JUDGEUSERPHONENO="user.judge_user_phone_no";
    public static final String FIRSTGETCODE="user.send_newphone_verify_code";
    public static final String FIRSTBINDPHONENO="user.modify_user_phone_no";
    public static final String PHONENOGETCODE="user.send_verify_code";
    public static final String ISTRUEPHONENOCODE="user.modify_user_bind_phone_no";//验证旧手机号码
    public static final String ISBINDPHONENO="user.judge_bind_phone_no";

    public static final String ROOMORDERDETAIL="book.get_room_order_detail";
    public static final String SERVICEORDERDETAIL="user.get_service_order_detail";

    public static final String ROOMORDERLIST="user.room_order_list";//11.4 房间订单列表
    public static final String BOOKDINNERLIST="user.book_dinner_list";//11.7 订餐/购物 订单列表

//    public static final String BOOKNEARHOTEL="book.near_hour_hotel";//附近钟点房
//    public static final String BOOKHOURLISTSEARSH="book.hotel_hour_search";//酒店钟点搜索
    public static final String BOOKNEARHOTEL="other.near_hour_hotel";//附近钟点房
    public static final String BOOKHOURLISTSEARSH="other.hotel_hour_search";//酒店钟点搜索

//    public static final String BOOKNEARHOTELLIST="book.near_allday_hotel";//附近酒店
//    public static final String BOOKNEARHOTELLISTSEARSH="book.hotel_search";//酒店搜索
    public static final String BOOKNEARHOTELLIST="other.near_allday_hotel";//附近酒店
    public static final String BOOKNEARHOTELLISTSEARSH="other.hotel_search";//酒店搜索
    public static final String BOOKNROOMSUBMIT="book.submit_room_book";//房间订单提交 新

    public static final String SERVICERACTIVEDETAIL="service.active_detail";//红包详情
    public static final String SERVICESENDPACKGE="service.send_red_bag";//红包详情
    public static final String SERVICESENDPACKGELIST="service.user_red_bag_list";//红包详情
    public static final String SUBMITHOURBOOK="book.submit_hour_book";//钟点房提交 新

    public static final String CANCELWINDOW="service.cancel_window";
    public static final String OPENDOORSS="service.unlock_all";//开锁用
    public static final String ChECKINBYPHOTO="service.checkin_by_photo";//刷脸自助入住
    public static final String CHECKINISNEEDFACE="service.need_face";//刷脸自助入住
    public static final String LOCKDETAILE="service.hotel_detail";//酒店详情页面
    public static final String LOCKDETAILECOMMENT="service.comment";//酒店详情评论页面

//    public static final String NEWONLINE_PAY="book.getCharge_before";//旧的（酒店）支付接口
//    public static final String SERVICERNEWONLINE_PAY="pingpp.newPay";//旧支付(服务和充值)新接口
    public static final String NEWONLINE_PAY="book.create_online_pay";//支付新接口
    public static final String SERVICERNEWONLINE_PAY="pingpp.newCreatePay";//支付新接口
    public static final String REGISTPHONE="user.phone_register_verify";
    public static final String REGISTNEWVERSION="user.user_register_new_version";
    public static final String SERVICERADDSERVICERORDER="service.add_service_order";//添加服务订单
    public static final String SERVICERMORNINGCALL="service.morning_call";//新的叫醒服务
    public static final String SERVICERADDTASK="service.add_service_task";//添加服务
    public static final String SERVICERCATALOG="service.task_catalog";//服务列表
    public static final String SERVICERCANCELTASK="service.task_cancel";//取消服务
    public static final String SERVICERSENDMSG="service.send_msg";//服务发送会话
    public static final String SERVICERMSGLIST="service.msg_search";//服务发送会话消息列表
    public static final String SERVICERCALLLIST="service.morning_call_list";//服务叫醒时间列表
    public static final String SERVICERCALLLISTTODELETE="service.morning_call_delete";//服务叫醒时间列表  -删除
    public static final String SERVICERISSUETASK="service.re_issue_task";//服务重新发布
    public static final String SERVICEOTHERLIST="other.consumption_list";//其他订单列表
    public static final String SERVICEOTHERLISTDETAIL="other.consumption_detail";//其他订单列表详情
    public static final String OTHERCONSUMPTION_AFFIRM="other.consumption_affirm";//确认订单
    public static final String BandGT="other.bind_gt";//绑定个推
    public static final String GTALERT="other.consumption_alert_detail";//绑定个推
    public static final String UserChangPwd="user.user_modify_password";//修改密码
    public static final String SERVIERCHARGE="service.hotel_service_charge";//商品服务费



    private static HttpClient instance = new HttpClient();

    public HttpClient() {
    }

    public static HttpClient instance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                instance = new HttpClient();
            }
        }
        return instance;
    }

    public static String getUrl(String url) {
        return BASEPATH + url;
    }

    public void login(String username, String password, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", username);
        params.put("password", password);
        send(BaseApplication.context(), MY_LOGIN, params, callBack);
    }

    public void getVerCode(String phone, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        send(BaseApplication.context(), "user.request_verify_code", params, callBack);
    }

    public void getRegisterVerCode(String phone, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        send(BaseApplication.context(), "user.register_request_verify_code", params, callBack);
    }

    public void register(String user_name, String password, String verCode, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", user_name);
        params.put("password", password);
        params.put("code", verCode);
        send(BaseApplication.context(), MY_REGIST, params, callBack);
    }

    public void test(int subtrahend, int minuend, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("subtrahend", subtrahend);
        params.put("minuend", minuend);
        send(BaseApplication.context(), MY_LOGIN, params, callBack);
    }

    public static void getImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(getUrl("/" + url), imageView);
    }

    /**
     * 搜索酒店
     *
     * @param city
     * @param destination
     * @param priceRangeStart
     * @param priceRangeEnd
     * @param checkInDate
     * @param checkOutDate
     * @param pageId
     * @param ranktype
     * @param rankorder
     * @param callBack
     */
    public void hotelSearch(String city, String destination, String priceRangeStart, String priceRangeEnd, String checkInDate, String checkOutDate, int pageId, String ranktype, String rankorder, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("destination", destination);
        Map<String, Object> price = new HashMap<>();
        price.put("start", priceRangeStart);
        price.put("end", priceRangeEnd);
        params.put("price", price);
        Map<String, Object> day = new HashMap<>();
        day.put("start", checkInDate);
        day.put("end", checkOutDate);
        params.put("day", day);
        params.put("pageid", pageId);
        params.put("ranktype", ranktype);
        params.put("rankorder", rankorder);
        send(BaseApplication.context(), BOOK_SEARCH, params, callBack);
    }

    /**
     * 获取附近酒店
     *
     * @param longitude
     * @param latitude
     * @param ranktype
     * @param rankorder
     * @param pageid
     * @param callBack
     */
    public void nearHotel(String longitude, String latitude, String checkInDate, String checkOutDate, String ranktype, String rankorder, int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        Map<String, Object> day = new HashMap<>();
        day.put("start", checkInDate);
        day.put("end", checkOutDate);
        params.put("day", day);
        params.put("ranktype", ranktype);
        params.put("rankorder", rankorder);
        params.put("pageid", pageid);
        send(BaseApplication.context(), BOOK_NEAR_HOTEL, params, callBack);
    }

    /**
     * 获取城市列表
     *
     * @param callBack
     */
    public void getHotelCity(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(), BOOK_CITY, params, callBack);
    }

    /**
     * 获取目的地提示
     *
     * @param key
     * @param callBack
     */
    public void GetHotelDestination(String key, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        send(BaseApplication.context(), BOOK_DESTINATION, params, callBack);
    }

    /**
     * 获取可用的房间
     *
     * @param hotelid
     * @param dateStart
     * @param dateEnd
     * @param callBack
     */
    public void getHoteRoomList(String hotelid, String dateStart, String dateEnd, String priceStart, String priceEnd, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotelid);
        params.put("startdate", dateStart);
        params.put("enddate", dateEnd);
        Map<String, Object> price = new HashMap<>();
        price.put("start", priceStart);
        price.put("end", priceEnd);
        params.put("price", price);
        send(BaseApplication.context(), BOOK_GET_ROOM_TYPE_LIST, params, callBack);
    }

    /**
     * 收藏酒店
     *
     * @param hotelid
     * @param callBack
     */
    public void addHotelFavorites(String hotelid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotelid);
        send(BaseApplication.context(), BOOK_ADD_HOTEL_FAVORITES, params, callBack);
    }

    public void delHotelFavorites(String hotelid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotelid);
        send(BaseApplication.context(), BOOK_DEL_HOTEL_FAVORITES, params, callBack);
    }

    /**
     * 提交订单
     *
     * @param roomtypeid
     * @param num
     * @param startdate
     * @param enddate
     * @param price
     * @param expectCheckinTime
     * @param checkinName
     * @param checkinPhone
     * @param callBack
     */
    public void submitBook(String roomtypeid, int num, String startdate, String enddate, String price, String expectCheckinTime, String checkinName, String checkinPhone, HttpCallBack callBack) {
        //   startdate="2011-09-11";
        // enddate="2015-10-25";
        Map<String, Object> params = new HashMap<>();
        params.put("room_type_id", roomtypeid);
        params.put("start_date", startdate);
        params.put("end_date", enddate);
        params.put("price", price);
        params.put("num", num);
        params.put("expect_checkin_time", expectCheckinTime);
        params.put("checkin_name", checkinName);
        params.put("checkin_phone", checkinPhone);
        send(BaseApplication.context(), BOOK_SUBMIT_BOOK, params, callBack);
    }

    /**
     * 付款
     *
     * @param billnum
     * @param ammount
     * @param channelid
     * @param callBack
     */
    public void payBill(String billnum, String ammount, String channelid, int billtype, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("billnum", billnum);
        params.put("amount", ammount);
        params.put("channel", channelid);
        params.put("billtype", billtype);
        send(BaseApplication.context(), BOOK_PAY_BILL, params, callBack);
    }

    public void hourSearch(String day, String starttime, String ranktype, String rankorder, int pageid, String destination, String city, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        params.put("starttime", starttime);
        params.put("ranktype", ranktype);
        params.put("rankorder", rankorder);
        params.put("pageid", pageid);
        params.put("destination", destination);
        params.put("city", city);
        send(BaseApplication.context(), BOOK_HOURSEARCH, params, callBack);
    }

    public void nearHour(String longitude, String latitude, String day, String starttime, String ranktype, String rankorder, int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("day", day);
        params.put("starttime", starttime);
        params.put("ranktype", ranktype);
        params.put("rankorder", rankorder);
        params.put("pageid", pageid);
        send(BaseApplication.context(), BOOK_NEARHOUR, params, callBack);
    }

    public void getHourRoomTypeList(String day, String starttime, String hotelid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        params.put("starttime", starttime);
        params.put("hotel_id", hotelid);
        send(BaseApplication.context(), BOOK_GET_HOURROOM_TYPE_LIST, params, callBack);
    }

    public void submitHourBook(String roomid, String day, String starttime, String price, int num, String checkinphone, String checkinname, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_type_id", roomid);
        params.put("day", day);
        params.put("starttime", starttime);
        params.put("price", price);
        params.put("num", num);
        params.put("checkin_phone", checkinphone);
        params.put("checkin_name", checkinname);
        send(BaseApplication.context(), SUBMITHOURBOOK, params, callBack);
    }

    public void getRoomBooking(String billnum, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("booking", billnum);
        send(BaseApplication.context(), BOOK_GET_ROOM_BOOKING, params, callBack);
    }
    public void getCheckOutTime(String hotelId,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotelId);
        send(BaseApplication.context(), BOOK_GET_CHECKOUT_TIME, params, callBack);
    }
    //获取环信账号：
    public void huanxin_CreateUser(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("key", key);
        send(BaseApplication.context(), HUANXIN_CREATEUSER, params, callBack);
    }

    //使用用户app账号注册环信账号
    public void huanxin_RegisterUser(String username, String password, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        send(BaseApplication.context(), HUANXIN_REGISTERUSER, params, callBack);
    }

    //通过酒店名称搜索酒店聊天室：
    public void huanxin_searchHotel(String hotelName, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelName", hotelName);
        send(BaseApplication.context(), HUANXIN_SEARCHHOTEL, params, callBack);
    }

    //创建聊天室：
    public void huanxin_createChatRoom(String descript, String roomname, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("descript", descript);
        params.put("roomname", roomname);
        send(BaseApplication.context(), HUANXIN_CREATECHATROOM, params, callBack);
    }
    /*//开门：
    * room_id 房间号
    * source
    * unlock_password 开锁密码，没有设置密码传空字 符串“”，有设置密码传密码
    * */

    public void service_Unlock(String room_id, String unlock_password,String order_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("source", "我");//开锁人（我/服务员）  用户开锁填：我
        params.put("success", "1");//开锁是否成功，传1
        params.put("unlock_password", unlock_password);
        params.put("id", "-1");
        params.put("user_id", "-1");
        params.put("order_id", order_id);
        send(BaseApplication.context(), SERVICE_UNLOCK, params, callBack);
    }

    //解锁电梯
    public void service_Unlock_Lift(String hotel_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
//        params.put("source", source);
        send(BaseApplication.context(), SERVICE_UNLOCK_LIFT, params, callBack);
    }
    //自助chenkin /*
    //  book_id 订单id
    //  room_id 房间id
    //
    // */

    public void service_Myself_Checkin(String book_id, String room_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("book_id", book_id);
        params.put("room_id", room_id);
        send(BaseApplication.context(), SERVICE_MYSELF_CHECKIN, params, callBack);
    }

    /*
        hotel_id 酒店id
        type  餐饮1 购物2
        获取订餐/购物分类
    */
    public void getServiceCatalog(String hotel_id, String type, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        params.put("type", type);
        send(BaseApplication.context(), SERVER_GETSETVICECATALOG, params, callBack);
    }

    /*

        caption  分类对应的id号
        获取订餐/购物分类 --对应的list列表
    */
    public void getServiceList(String catalog_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("catalog_id", catalog_id);
        send(BaseApplication.context(), SERVICE_GETSERVICELIST, params, callBack);
    }

    /*

       total 订单总价

       discount 折扣
       items json数据
       提交订餐/购物  订单
   */
    public void addService(String room_id, String note, String hotel_id, String total, String discount, String pay_type, String items,String booking_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("note", note);
        params.put("hotel_id", hotel_id);
        params.put("total", total);
        params.put("discount", discount);
        params.put("items", items);
        params.put("pay_type", pay_type);
        params.put("room_id", room_id);
        params.put("booking_id", booking_id);
        send(BaseApplication.context(), SERVICE_ADDSERVICE, params, callBack);
    }

    /*
      获取该用户订酒店的信息
   */
//    public void getMyLockInfo(HttpCallBack callBack){
//        Map<String, Object> params = new HashMap<>();
////        params.put("user_id", user_id);
//        send(BaseApplication.context(), MYLOCKINFO, params, callBack);
//    }
    /*
         用户对订餐和购物订单支付状态进行修改。
         id	 	订单id
         pay_type	支付方式
         pay_channel	结算通道
         pay_no	支付订单号
      */
    public void modify_pay_service(String id, String pay_type, String pay_channel, String pay_no, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("pay_type", pay_type);
        params.put("pay_channel", pay_channel);
        params.put("pay_no", pay_no);
        send(BaseApplication.context(), MODIFY_PAY_SERVICE, params, callBack);
    }

    /*
         用户开门记录。
         room_id	 	房号

      */
    public void get_myroom_unlock_list(String room_id,int pageid,String order_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("pageid", pageid);
        params.put("order_id", order_id);
        send(BaseApplication.context(), GET_MYROOM_UNLOCK_LIST, params, callBack);
    }

    /*
         用户服务订单信息。
         service_id	 	订单号"

      */
    public void get_myserviceorder_detail(String room_id, String service_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("service_id", service_id);
        send(BaseApplication.context(), GET_MYSERVICEORDER_DETAIL, params, callBack);
    }

    /* 获取周边聊天室
    * longitude 经度
    * latitude		纬度
    * */
    public void near_hotelchatroom(String longitude, String latitude,int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("pageid",pageid);
        send(BaseApplication.context(), NEAR_HOTELCHATROOM, params, callBack);
    }

    /* 据user_id返回入住的酒店房号信息，用于开门界面的显示。

    * */
    public void get_room_checkin_user(String user_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);

        send(BaseApplication.context(), GET_ROOM_CHECKIN_USER, params, callBack);
    }


    public void send(Context context, String method, Map<String, Object> params, HttpCallBack callBack) {
        try {
            JSONRPC2Request reqOut = new JSONRPC2Request(method, params, "123");
            reqOut.appendNonStdAttribute("auth", AppContext.getShareUserSessinid());
            StringEntity entity = new StringEntity(reqOut.toString(), HTTP.UTF_8);
            LogUtil.log("paramss:" + reqOut);
              Log.e("myparamss: ", "" + reqOut);
            BaseHttpClient.post(context, BASEPATH, entity, "application/json-rpc", new ResponseHandler(context, callBack));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    //我的酒店订单
    public void myhotel_order_list(int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        send(BaseApplication.context(), MY_ODERFORM, params, callBack);
    }

    //我的订餐订单
    public void mydinchan_list(int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        send(BaseApplication.context(), MY_DINCHAN, params, callBack);
    }

    //我的购物订单
    public void myshopping_list(int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        send(BaseApplication.context(), MY_SHOPPING, params, callBack);
    }

    //我的收藏
    public void myhotel_collection_list(int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        send(BaseApplication.context(), My_COLLECTIONACTIVITY, params, callBack);
    }

    //我的点评
    public void myhotel_comment_list(int pageid, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        send(BaseApplication.context(), MYHOTEL_COMMENT, params, callBack);
    }

    //我的点评
    public void getUser_Myinfo(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("user_id", user_id);
        send(BaseApplication.context(), GET_USER_MYINFO, params, callBack);
    }

    //设置免打扰
    public void setroomtrouble(String id, String distrub, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("distrub", distrub);
        send(BaseApplication.context(), SERVICE_SETFREE, params, callBack);
    }

    //获取我的资料
    public void myinfo(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("user_id", user_id);
//        params.put("room_id", room_id);
//        params.put("content", content);
//        params.put("service_type", service_type);
//        params.put("content_type", content_type);
        send(BaseApplication.context(), MY_INFO, params, callBack);
    }

    //设置morningcall  房间清扫  更换床上用品  其他content_type1 文本1，音乐2；
    public void addhotelService(String room_id, String content, String service_type, String content_type, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("user_id", user_id);
        params.put("room_id", room_id);
        params.put("content", content);
        params.put("service_type", service_type);
        params.put("content_type", content_type);
        send(BaseApplication.context(), SERVICE_HOTELSERVICE, params, callBack);
    }

    //退出登录
    public void login_out(String user_name, String password, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", user_name);
//        params.put("password", password);
        send(BaseApplication.context(), LOGIN_OUT, params, callBack);
    }

    //获取免打扰状态
    public void get_room_trouble(String room_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        send(BaseApplication.context(), SERVICE_GET_ROOM_TROUBLE, params, callBack);
    }

    //获取免打扰状态
    public void get_room_checkin_user(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("room_id", room_id);
        send(BaseApplication.context(), SERVICE_GET_ROOM_NO, params, callBack);
    }


    //获取订单状态信息
    public void get_room_booking_details(String booking, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("booking", booking);
        send(BaseApplication.context(), MY_GET_ROOM_BOOKING_DETAILS, params, callBack);
    }

    //设置修改密码
    public void modify_user_unlock_pwd(String unlock_password, String new_unlock_password, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("unlock_password", unlock_password);
        params.put("new_unlock_password", new_unlock_password);
        send(BaseApplication.context(), MY_MODIF_USER_UNLOCK_PWD, params, callBack);
    }

    //忘记密码
    public void reset_user_login_pwd(String phone_no, String new_password, String code, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone_no", phone_no);
        params.put("new_password", new_password);
        params.put("code", code);
        send(BaseApplication.context(), RESET_USER_LOGIN_PWD, params, callBack);
    }

    /*
    *  上传身份证图片
    * */
    public void set_User_Id_Image(String id_image, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("id_image", id_image);
        send(BaseApplication.context(), SET_USER_ID_IMAGE, params, callBack);
    }

    //修改用户名
    public void set_user_true_name(String true_name, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("true_name", true_name);
//        params.put("new_password", new_password);
        send(BaseApplication.context(), SET_USER_TRUE_NAME, params, callBack);
    }

    //修改身份证号码
    public void set_user_id_no(String id_no, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("id_no", id_no);
//        params.put("new_password", new_password);
        send(BaseApplication.context(), SET_USER_ID_NO, params, callBack);
    }

    //取消订单
    public void cancel_order(String book_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("book_id", book_id);
//        params.put("new_password", new_password);
        send(BaseApplication.context(), MY_CANCLE_ORDER, params, callBack);
    }

    //修改用户密码
    public void modify_user_login_pwd(String new_password, String code, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
//        params.put("password", password);
        params.put("new_password", new_password);
        params.put("code", code);
        send(BaseApplication.context(), MODIFY_USER_LOGIN_PWD, params, callBack);
    }


    /*
    * room_id 房间id
    * service_type
    * 服务类型
    *   1 – 房间清扫
    *   2 – 更换床上用品
    *   3 - Morning Call
    *   4 - 其它
    *
    * */
    public void get_myhotelService_list(String room_id, String service_type, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("service_type", service_type);
        send(BaseApplication.context(), GET_MYHOTESERVICE_LIST, params, callBack);
    }

    /*
    *   hotel_id 酒店id
    *
    *   type 分类类型
    *   1 – 餐饮
    *   2 - 购物
    * */
    public void getAllServiceList(String hotel_id, String type, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        params.put("type", type);
        send(BaseApplication.context(), GETALLSERVICELIST, params, callBack);
    }


    //设置用户头像
    public void set_user_head_image(String head_image, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("head_image", head_image);
        send(BaseApplication.context(), SET_USER_HEAD_IMAGE, params, callBack);
    }

    //获取用户头像
    public void get_user_head_image(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(), GET_USER_HEAD_IMAGE, params, callBack);
    }


    //上传发票抬头
    public void update_invoice_title_list(String title_list, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("title_list", title_list);
        send(BaseApplication.context(), UPDATE_INVOICE_TITLE_LIST, params, callBack);
    }

    //获取发票抬头
    public void get_invoice_title_list(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(), GET_INVOICE_TITLE_LIST, params, callBack);
    }


    //检查版本更新
    public void check_latest_app_version(String os, String app, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("os", os);
        params.put("app", app);
        send(BaseApplication.context(), CHECK_LATEST_APP_VERSION, params, callBack);
    }
   public void getBookPrice(String roomType,String startTime,String endTime,String roomNum,HttpCallBack callBack){
       Map<String, Object> params = new HashMap<>();
       params.put("room_type_id", roomType);
       params.put("start_date", startTime);
       params.put("end_date", endTime);
       params.put("num", roomNum);
       send(BaseApplication.context(), "book.total_price", params, callBack);
   }

    /*
    * service_id 服务id
    *
    * */
    public void cancel_service_order(String service_id, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("service_id", service_id);
        send(BaseApplication.context(), CANCEL_SERVICE_ORDER, params, callBack);
    }

    //发表评论 user.addhotel_comment
    public void addhotel_comment(String hotel_id, String content, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        params.put("content", content);
        send(BaseApplication.context(), ADDHOTEL_COMMENT, params, callBack);
    }

    //获取验证码 user.addhotel_comment
    public void request_verify_code(String phone, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        send(BaseApplication.context(), REQUEST_VERIFY_CODE, params, callBack);
    }

    /*
   * order_id 订单id
   * pay_channel 支付通过 ，3，房费，4 在线支付
   * */
    public void re_sumbit_service(String order_id,String pay_channel, HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", order_id);
        params.put("pay_channel", pay_channel);
        send(BaseApplication.context(), RE_SUMBIT_SERVICE, params, callBack);
    }

    //退订
    public void refund_order(String book_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("book_id", book_id);
        send(BaseApplication.context(), REFUND_ORDER, params, callBack);
    }

    //判断是否过期订单
    public void order_expired(String book_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("book_id", book_id);
        send(BaseApplication.context(), ORDER_EXPIRED, params, callBack);
    }

    //重置用户开门密码
    public void reset_user_unlock_pwd(String login_pass,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("login_pass", login_pass);
        send(BaseApplication.context(), RESET_USER_UNLOCK_PWD, params, callBack);
    }

    //取电
    public void service_take_power(String room_id,String order_id,String operation,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("order_id", order_id);
        params.put("operation", operation);
        params.put("apply", "1");
        send(BaseApplication.context(), SERVIC_TAKE_POWER, params, callBack);
    }
    //充值前，获取充值id:
    /*
    *   recharge_account_id 酒店账户id
    * */
    public void get_or_create_recharge_user(String recharge_account_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("recharge_account_id", recharge_account_id);
        send(BaseApplication.context(), GET_OR_CREATE_RECHARGE_USER, params, callBack);
    }

    public void recharge_account_user(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();

        send(BaseApplication.context(), RECHARGE_ACCOUNT_USER, params, callBack);
    }

    public void recharge_account_user_details(String recharge_account_user_id,int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("recharge_account_user_id", recharge_account_user_id);
        params.put("pageid", pageid);
        send(BaseApplication.context(), RECHARGE_ACCOUNT_USER_DETAILS, params, callBack);
    }

    public void getMyAvaliableBalance(String hotel_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);

        send(BaseApplication.context(), "book.get_my_available_balance", params, callBack);
    }
    /*
    *   IS_ROOM_CHINCKINUSER_POWER
    * */
    public void is_room_chinckin_user_power(String room_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        send(BaseApplication.context(), IS_ROOM_CHINCKINUSER_POWER, params, callBack);
    }


    //当前余额值
    public void recharge_account_balance(String recharge_account_user_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("recharge_account_user_id", recharge_account_user_id);
        send(BaseApplication.context(), RECHARGE_ACCOUNT_BALANCE, params, callBack);
    }//当前余额值
    public void myfriends_list(HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
//        params.put("recharge_account_user_id", recharge_account_user_id);
        send(BaseApplication.context(), USER_MY_FRIENFS_LIST, params, callBack);
    }



    //当前余额值
    public void search_friends(String search_name,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("search_name", search_name);
        send(BaseApplication.context(), SEARCH_FRIENDS, params, callBack);
    }

    //
    public void rechare_before(String recharge_account_user_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("recharge_account_user_id", recharge_account_user_id);
        send(BaseApplication.context(), RECHARGE_BEFORE, params, callBack);
    }

    //客人是否离开酒店状态
    public void book_leave_room(String booking_id,String bool,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("booking_id", booking_id);
        params.put("bool", bool);
        send(BaseApplication.context(), BOOK_LEAVE_ROOM, params, callBack);
    }

    //聊天室显示前台用户为店小二
    public void waiter(String emid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("emid", emid);
        send(BaseApplication.context(), WAITER, params, callBack);
    }
    //自助chenkout
    public void chenkout(String room_id,String order_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("order_id", order_id);
//        params.put("hotel_id", hotel_id);
        send(BaseApplication.context(), CHECKOUT, params, callBack);
    }
//    //押金支付chenkout
//    public void pledge_checkout(String room_id,String order_id,String fee,HttpCallBack callBack) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("room_id", room_id);
//        params.put("order_id", order_id);
//        params.put("fee", fee);
//        send(BaseApplication.context(), PLEDGE_CHECKOUT, params, callBack);
//    }
//    //余额支付chenkout
//    public void balance_checkout(String room_id,String order_id,String hotel_id,String price,HttpCallBack callBack) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("room_id", room_id);
//        params.put("order_id", order_id);
//        params.put("account_id", hotel_id);
//        params.put("price", price);
//        send(BaseApplication.context(), BALANCE_CHECKOUT, params, callBack);
//    }
    //余额支付chenkout
    public void getTo_checkout(String room_id,String order_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("order_id", order_id);
        send(BaseApplication.context(), GETTOCHENKIN, params, callBack);
    }
    //新开门接口
    public void newUserUnlock(String room_id,String unlock_password,String order_id,String num,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("unlock_password", unlock_password);
        params.put("user_id", "-1");
        params.put("order_id", order_id);
        params.put("port", "1");//安卓
        params.put("num", num);//安卓
        send(BaseApplication.context(), NEWUSER_UNLOCK, params, callBack);
    }

    //我的账单列表
    public void  my_bill_list(String action,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("action", action);
        send(BaseApplication.context(), MY_BILL_LIST, params, callBack);
    }

    //我的账单详情
    public void  my_bill_detail(String booking_id,String user_service_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("booking_id", booking_id);
        params.put("user_service_id", user_service_id);
        send(BaseApplication.context(), MY_BILL_DETAIL, params, callBack);
    }
    //判断是否是店小二
    public void  is_Receptionist(String emid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("emid", emid);
        send(BaseApplication.context(), IS_RECEPTIONIST, params, callBack);
    }


    //检查app版本更新
    public void  judge_app_update(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("app", "1");
        params.put("system", "1");
        send(BaseApplication.context(), JUDGE_APP_UPDATE, params, callBack);
    }
    //一键注册
    public void  userRegister(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(), USER_REGISTER, params, callBack);
    }
    //修改过 密码
    public void  changpwd(String password,String new_password,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("password",password);
        params.put("new_password",new_password);
        send(BaseApplication.context(),CHANFPWD, params, callBack);
    }
     //扫一扫注册和入住
    public void  scanUserRegister(String url,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        BaseHttpClient.get(url, new ResponseHandler(BaseApplication.context(), callBack));
//        send(BaseApplication.context(), USER_REGISTER, params, callBack);
    }
    //判断手机是否绑定
    public void  judgeuserPhoneNo(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(), JUDGEUSERPHONENO, params, callBack);
    }
    //首次绑定手机号获取验证码
    public void  firstGetPhoneCode(String phone_no,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone_no",phone_no);
        send(BaseApplication.context(),FIRSTGETCODE, params, callBack);
    }
    //首次绑定手机号
    public void  firstBindPhoneNo(String phone_no,String code,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone_no",phone_no);
        params.put("code",code);
        send(BaseApplication.context(),FIRSTBINDPHONENO, params, callBack);
    }
    //获取绑定手机号验证码
    public void  PhoneNogetCode(HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        send(BaseApplication.context(),PHONENOGETCODE, params, callBack);
    }
    //验证旧手机号
    public void  OldPhoneCodeIsTrue(String code,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
        send(BaseApplication.context(),ISTRUEPHONENOCODE, params, callBack);
    }
    //先判断手机号是否绑定了
    public void  isbindphoneNo(String code,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone",code);
        send(BaseApplication.context(),ISBINDPHONENO, params, callBack);
    }
    //酒店订单详情  新
    public void  roomOrderDetail(String booking,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("booking",booking);
        send(BaseApplication.context(),ROOMORDERDETAIL, params, callBack);
    }
    //服务订单详情  新
    public void  servicerOrderDetail(String service_id,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("service_id",service_id);
        send(BaseApplication.context(),SERVICEORDERDETAIL, params, callBack);
    }
    //酒店订单列表 新
    public void  roomOrderList(int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        send(BaseApplication.context(),ROOMORDERLIST, params, callBack);
    }
    //购物、订餐订单列表 新
    public void  bookDinnerList(int pageid,String type,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        params.put("type",type);
        send(BaseApplication.context(),BOOKDINNERLIST, params, callBack);
    }
    //附近钟点房  新
    public void  bookNearHotel(String longitude, String latitude, String day, String start_time, String sort, int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        params.put("day",day);
        params.put("longitude",longitude);
        params.put("latitude",latitude);
        params.put("start_time",start_time);
        params.put("sort",sort);
        send(BaseApplication.context(),BOOKNEARHOTEL, params, callBack);
    }
    //附近酒店  新
    public void  bookNearHotelList(String longitude, String latitude, String start_day,String end_day, String sort, int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        params.put("longitude",longitude);
        params.put("latitude",latitude);
        params.put("start_day",start_day);
        params.put("end_day",end_day);
        params.put("sort",sort);
        send(BaseApplication.context(),BOOKNEARHOTELLIST, params, callBack);
    }
    //搜索酒店  新
    public void  bookNearHotelListSearsh(String city, String destination,String min_price,String max_price, String start_day,String end_day, String sort, int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        params.put("start_day",start_day);
        params.put("end_day",end_day);
        params.put("city",city);
        params.put("destination",destination);
        params.put("min_price",min_price);
        params.put("max_price",max_price);
        params.put("sort",sort);
        send(BaseApplication.context(),BOOKNEARHOTELLISTSEARSH, params, callBack);
    }
    //搜索钟点房酒店  新
    public void  bookHourHotelListSearsh(String city, String day,String start_time,String destination, String sort, int pageid,HttpCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageid",pageid);
        params.put("day",day);
        params.put("start_time",start_time);
        params.put("city",city);
        params.put("destination",destination);
        params.put("min_price","0");
        params.put("max_price","100000");
        params.put("sort",sort);
        send(BaseApplication.context(),BOOKHOURLISTSEARSH, params, callBack);
    }

    public void bookRoomSubmit(String room_type_id,  String start_date,String end_date, String price,String expect_checkin_time,String checkin_name,String checkin_phone, String num,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("room_type_id",room_type_id);
        params.put("start_date",start_date);
        params.put("end_date",end_date);
        params.put("price",price);
        params.put("expect_checkin_time",expect_checkin_time);
        params.put("checkin_name",checkin_name);
        params.put("checkin_phone",checkin_phone);
        params.put("num", num);
        send(BaseApplication.context(),BOOKNROOMSUBMIT, params, callBack);

    }

    public void activeDetail( String bonus_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("bonus_id", bonus_id);
        send(BaseApplication.context(),SERVICERACTIVEDETAIL, params, callBack);

    }
    public void sendRedPackge( String bonus_id,String num,String book_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("bonus_id", bonus_id);
        params.put("num", num);
        params.put("book_id", book_id);
        send(BaseApplication.context(),SERVICESENDPACKGE, params, callBack);

    }
    //红包列表
    public void redPackgelist( String pageid,String search,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("pageid", pageid);
        params.put("search", search);
        send(BaseApplication.context(),SERVICESENDPACKGELIST, params, callBack);

    }
    //取消红包
    public void cancelWindowRedPackge( String num,String book_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("num", num);
        params.put("book_id", book_id);
        send(BaseApplication.context(),CANCELWINDOW, params, callBack);
    }
    //开锁通用
    public void openDoorss( String room_id,String lock_id,String unlock_password,String order_id,String num,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("lock_id", lock_id);
        params.put("port", "1");//安装端
        params.put("user_id", "-1");
        params.put("boss_id", "-1");
        params.put("id", "-1");
        params.put("unlock_password", unlock_password);
        params.put("order_id", order_id);
        params.put("num", num);
        send(BaseApplication.context(),OPENDOORSS, params, callBack);
    }
    //刷脸入住
    public void checkinByPhonto( String book_id,String room_id,String photo,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("book_id", book_id);
        params.put("room_id", room_id);
        params.put("photo", photo);
        send(BaseApplication.context(),ChECKINBYPHOTO, params, callBack);
    }
    //是否需要
    public void checkinIsNeedFace( String hotel_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        send(BaseApplication.context(),CHECKINISNEEDFACE, params, callBack);
    }
    //酒店详情 新
    public void lockDetail( String hotel_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        send(BaseApplication.context(),LOCKDETAILE, params, callBack);
    }
    //酒店详情评论 新
    public void lockDetailComment( String hotel_id,String pageid,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        params.put("pageid", pageid);
        send(BaseApplication.context(),LOCKDETAILECOMMENT, params, callBack);
    }
    //注册获取电话验证码 新
    public void registGetPhoneCode( String phone_no,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("phone_no", phone_no);
        send(BaseApplication.context(),REGISTPHONE, params, callBack);
    }
    //注册新
    public void registNewVersion( String name,String password,String phone_no,String code,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("password", password);
        params.put("phone_no", phone_no);
        params.put("code", code);
        send(BaseApplication.context(),REGISTNEWVERSION, params, callBack);
    }

    public void addServicerOrderByNew(String booking_id,String pay_way,String remark,String goods,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("booking_id", booking_id);
        params.put("pay_way", pay_way);
        params.put("remark", remark);
        params.put("goods", goods);
        send(BaseApplication.context(),SERVICERADDSERVICERORDER, params, callBack);
    }
    //
    public void servicerMoringCall(String room_id,String morning_call_time,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("morning_call_time", morning_call_time);
        send(BaseApplication.context(),SERVICERMORNINGCALL, params, callBack);
    }
//
    public void servicerAddTask(String room_id,String content,String type,String content_type,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("content", content);
        params.put("type", type);
        params.put("content_type", content_type);
        send(BaseApplication.context(),SERVICERADDTASK, params, callBack);
    }
     public void servicerTaskCatalog(String room_id,String type,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_id", room_id);
            params.put("type", type);
            send(BaseApplication.context(),SERVICERCATALOG, params, callBack);
        }
    public void servicerTaskCancel(String room_service_id,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_service_id", room_service_id);
            send(BaseApplication.context(),SERVICERCANCELTASK, params, callBack);
        }
    public void servicerSendMsg(String room_service_id,String content,String content_type,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_service_id", room_service_id);
            params.put("content", content);
            params.put("content_type", content_type);
            send(BaseApplication.context(),SERVICERSENDMSG, params, callBack);
    }
    public void servicerMsgList(String room_service_id,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_service_id", room_service_id);
            send(BaseApplication.context(),SERVICERMSGLIST, params, callBack);
    }
    public void servicerCallList(String room_id,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_id", room_id);
            send(BaseApplication.context(),SERVICERCALLLIST, params, callBack);
    }
    public void servicerCallListbyDelete(String morning_call_id,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("morning_call_id", morning_call_id);
            send(BaseApplication.context(),SERVICERCALLLISTTODELETE, params, callBack);
    }
    public void servicerAgainTask(String room_service_id,HttpCallBack callBack){
            Map<String, Object> params = new HashMap<>();
            params.put("room_service_id", room_service_id);
            send(BaseApplication.context(),SERVICERISSUETASK, params, callBack);
    }
    public void servicerOtherOrderList(HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
//        params.put("room_service_id", room_service_id);
        send(BaseApplication.context(),SERVICEOTHERLIST, params, callBack);
    }
    public void servicerOtherOrderDetail(String consumption_order_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("consumption_order_id", consumption_order_id);
        send(BaseApplication.context(),SERVICEOTHERLISTDETAIL, params, callBack);
    }
    public void otherOrderDetailToSure(String consumption_order_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("consumption_order_id", consumption_order_id);
        send(BaseApplication.context(),OTHERCONSUMPTION_AFFIRM, params, callBack);
    }

    public void bandGT(String cid,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        send(BaseApplication.context(),BandGT, params, callBack);
    }
    public void gtAlert(String consumption_order_id,String booking_id,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("consumption_order_id", consumption_order_id);
        params.put("booking_id", booking_id);
        send(BaseApplication.context(),GTALERT, params, callBack);
    }
    public void changPwdNew(String new_password,String old_password,boolean isFistChang,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("new_password", new_password);
         if (!isFistChang){
            params.put("old_password", old_password);
         }
        send(BaseApplication.context(),UserChangPwd, params, callBack);
    }
    public void servicerCharge(String hotel_id,String num,String total_money,HttpCallBack callBack){
        Map<String, Object> params = new HashMap<>();
        params.put("hotel_id", hotel_id);
        params.put("num", num);
        params.put("total_money", total_money);
        send(BaseApplication.context(),SERVIERCHARGE, params, callBack);
    }

}
