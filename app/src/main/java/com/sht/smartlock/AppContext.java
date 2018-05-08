package com.sht.smartlock;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.design.widget.Snackbar;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChatManager;
//import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.nostra13.universalimageloader.utils.StorageUtils;
//import com.sht.smartlock.mqtt.PushService;
import com.sht.smartlock.mqtt.MQTTService;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.OtherOrderInfoActivity;
import com.sht.smartlock.ui.activity.chatlocation.LocationUtil;
import com.sht.smartlock.util.NotificationUtil;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.User;

import com.sht.smartlock.model.booking.Hotel;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.ECPreferenceSettings;
import com.sht.smartlock.phone.common.utils.ECPreferences;
import com.sht.smartlock.phone.common.utils.FileAccessor;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.util.MyPathUtil;
import com.sht.smartlock.util.StringUtil;
import com.sht.smartlock.util.cache.DataCache;
import com.sht.smartlock.widget.dialog.CommonDialog;
import com.sht.smartlock.widget.dialog.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InvalidClassException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class AppContext extends BaseApplication {
    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    private boolean mIsDownload;

    private static AppContext instance;
    public static Context applicationContext;
    private DataCache mDataCache;
    public static String TAG="TAG";
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
    private static DemoHandler handler;
    private Context mContext;
    public static MainActivity mainActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        instance = this;
        applicationContext = this;
        MyPathUtil.getInstance().initDirs("kaiser","myMp3",this);
        mDataCache = DataCache.get(this);
        initImageLoader(this);
        LocationUtil instances = LocationUtil.getInstance(this);
        /*设置ImageLoader默认参数
        创建默认的ImageLoader配置参数
        */
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
//        //Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(configuration);

        initImageLoader(applicationContext);
//        MultiDex.install(this);

        if (handler == null) {
            handler = new DemoHandler();
        }
//
//        EMChat.getInstance().init(applicationContext);
//
//    /**
//     * debugMode == true 时为打开，sdk 会在log里输入调试信息
//     * @param debugMode
//     * 在做代码混淆的时候需要设置成false
//     */
//        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题

//       CrashHandler crashHandler = CrashHandler.getInstance(); //程序出错处理
//       crashHandler.init(getApplicationContext());


        //MQTT 存储deviceID
//        mqttStart();


        CCPAppManager.setContext(instance);
        FileAccessor.initFileAccess();
        setChattingContactId();
        initImageLoader();
//        CrashHandler.getInstance().init(this);
        SDKInitializer.initialize(instance);
        UMShareAPI.get(this);
//        SpeechUtility.createUtility(instance, "appid=" + getString(R.string.app_id));
        //-------------
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase("com.sht.smartlock")) {
            Log.e("TAG", "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

            hxSDKHelper.onInit(applicationContext);

            //自动登录
            EMChat.getInstance().setAutoLogin(true);
            EMChatManager.getInstance().getChatOptions().setUseRoster(true);
        Log.e("MQTT", "------------->MQTTService");
//        startService(new Intent(this, MQTTService.class));


    }

    {
        PlatformConfig.setWeixin(Config.APPID_WEIXIN, Config.SECRET_WEIXIN);//公司酒店 千万鼎key值
//        PlatformConfig.setWeixin("wx0393601e41a8b392", "d1dcdfa9b5d4ff0e8334ea1dd258a05a");//自己申请的指尖宿酒店 key值
//        PlatformConfig.setWeixin("wx58fb96f5bcbbb8d4", "c702c77a1745e9deedf9bd2ee1f665b1");// key值
        PlatformConfig.setQQZone(Config.APPID_QZONE, Config.SECRET_QZONE);
        PlatformConfig.setSinaWeibo(Config.XINLANG_ID,Config.XINLANG_SERVICE);
    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://个推推送过来的消息
//                    if (MainActivity != null) {
//                        payloadData.append((String) msg.obj);
//                        payloadData.append("\n");
//                        if (GetuiSdkDemoActivity.tLogView != null) {
//                            GetuiSdkDemoActivity.tLogView.append(msg.obj + "\n");
//                        }
//                    }
//                    MainActivity.showMyDialog("娱乐消费","你的消费金额：8000");
                    if (mainActivity!=null){
                        MainActivity.idMenu.closeMenu();
                    }
                    try {
                        JSONObject jsonObject=new JSONObject((String) msg.obj);
                        JSONObject result = jsonObject.getJSONObject("result");
                        String type = result.getString("type");

                        if (type.equals("1")){
                            String order_id = result.getString("order_id");
                            String confirm = result.getString("confirm");//2 需要确认 3 不需要确认
                            String booking_id = result.getString("booking_id");//房费的订单id
                            boolean background = isAppForeground(applicationContext);//是否在后台
                            Log.e("IiBack","--------true=后台--->"+background);
                            if (background){
                                NotificationUtil notiUtil = new NotificationUtil(applicationContext);
                                notiUtil.postNotification(order_id,booking_id);
                                AppContext.setProperty(Config.GTCIDBYORDERID, order_id);
                                AppContext.setProperty(Config.GTCIDBYISTRUEIN,"true");
                                AppContext.setProperty(Config.GTCIDBYCONFIRM,confirm);
                                AppContext.setProperty(Config.GTBOOKINGIDBySAVE,booking_id);
                            }else {
                                if (confirm.equals("1")){//在APP中需要确认
                                    Intent intent = new Intent();
                                    intent.setClass(applicationContext, OtherOrderInfoActivity.class);
            //                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Config.GTCIDBYISTRUE, true);
                                    intent.putExtra(Config.GTCIDBYJSOn, order_id);
                                    intent.putExtra(Config.GTBOOKINGID, booking_id);
                                    AppContext.getInstance().startActivity(intent);
                                }else {//不需要确认
//                                    Snackbar snackbar = Snackbar.make( LayoutInflater.from(applicationContext).inflate(R.layout.activity_main, null), "您有1笔消费计入客房账单，可在\"我的订单\"中查看", Snackbar.LENGTH_INDEFINITE);
//                                    snackbar.show();
                                    AppContext.toastLong("您有1笔消费计入客房账单，可在\"我的订单\"中查看");
                                    NotificationUtil notiUtil = new NotificationUtil(applicationContext);
                                    notiUtil.postNotification(order_id,booking_id);
//                                    displayWindow(applicationContext,R.drawable.dialogbygay);
//                                    inDialog();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 1://个推cid
//                    if (demoActivity != null) {
//                        if (GetuiSdkDemoActivity.tLogView != null) {
//                            GetuiSdkDemoActivity.tView.setText((String) msg.obj);
//                        }
//                    }
                    break;
            }
        }
    }





    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "ECSDK_Demo/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .threadPoolSize(1)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                        // .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(CCPAppManager.md5FileNameGenerator)
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir ,null ,CCPAppManager.md5FileNameGenerator))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);
    }

    /**
     * 保存当前的聊天界面所对应的联系人、方便来消息屏蔽通知
     */
    private void setChattingContactId() {
        try {
            ECPreferences.savePreference(ECPreferenceSettings.SETTING_CHATTING_CONTACTID, "", true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


    public static void initImageLoader(Context context) {
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(R.drawable.load_image_fail)
                .showImageOnFail(R.drawable.load_image_fail)
                .showImageOnLoading(R.drawable.load_image_fail)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(displayOptions).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtil.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


    public static void clearLoginInfo() {
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = mDataCache.getAsString(CONF_APP_UNIQUEID);
        if (StringUtil.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            mDataCache.put(CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    public void showDialog(Context context, String title, String message, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(context);
        if (!title.equals("")) {
            dialog.setTitle(title);
        }
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.ok, positiveClickListener);
        dialog.setNegativeButton(R.string.cancel, negativeClickListener);
        dialog.show();
    }

//    public void showSimpleDialog

    public static void setProperty(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }
    public static void setPropertyBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }
    public static void clearPropertyData() {
        getPreferences().edit().clear().commit();
    }

    public static String getProperty(String key) {
        return getPreferences().getString(key, null);
    }
    public static boolean getPropertyBoolean(String key) {
        return getPreferences().getBoolean(key, false);
    }

    public static void removeProperty(String... keys) {
        for (String key : keys) {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString(key, null);
            apply(editor);
        }
    }

    public User getUser() {
        return null;
    }

    public int getUserId() {
        return 0;
    }

    public void setUser(User user) {

    }
    public boolean isAliasd(){
        String isAlias = getProperty(CacheKey.ALIASD);
        return isAlias != null && isAlias.equals(CacheKey.ALIASD_YES);
    }

    public void setAliasd(){
        setProperty(CacheKey.ALIASD, CacheKey.ALIASD_YES);
    }

    public void cleanTag(){
        removeProperty(CacheKey.TAGD);
    }

    public boolean isTagd(){
        String isAlias = getProperty(CacheKey.TAGD);
        return isAlias != null && isAlias.equals(CacheKey.TAGD_YES);
    }

    public void setTagd(){
        setProperty(CacheKey.TAGD, CacheKey.TAGD_YES);
    }

    public void cleanAliasd(){
        removeProperty(CacheKey.ALIASD);
    }

    public static void apply(SharedPreferences.Editor editor) {
        editor.apply();
    }

    public static AppContext instance() {
        return instance;
    }

    public void setDownload(boolean isDownload) {
        this.mIsDownload = isDownload;
    }

    public boolean isDownload() {
        return mIsDownload;
    }


    public static AppContext getInstance() {
        return instance;
    }
    public static int compareDistance(Hotel h1,Hotel h2){
       double d1,d2;
        d1=getLocalDistance(Double.parseDouble(h1.getLongitude()), Double.parseDouble(h1.getLatitude()));
        d2=getLocalDistance(Double.parseDouble(h2.getLongitude()),Double.parseDouble(h2.getLatitude()));
        if(d1>d2){
            return 1;
        }else if(d1<d2){
            return -1;
        }
        return 0;
    }
    public static double getLocalDistance( double long2,
                                          double lat2){
        double long1= Double.parseDouble(getProperty(Config.LONGITUDE));
        double lat1= Double.parseDouble(getProperty(Config.LATITUDE));
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1
     *            第一点经度
     * @param lat1
     *            第一点纬度
     * @param long2
     *            第二点经度
     * @param lat2
     *            第二点纬度
     * @return 返回距离 单位：米
     */
    public static String getDistanceStr(double long1, double lat1, double long2,
                                        double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
       String distance=(int)d+context().getString(com.sht.smartlock.R.string.meter);
        if(d>1000){
         //   d=Math.round(d / 1000, 1);
            DecimalFormat dr=new DecimalFormat("#0.0");
            if (d/1000<99){
                distance=dr.format(d/1000)+getInstance().getString(com.sht.smartlock.R.string.kilometer);
            }else {
                distance=">99"+getInstance().getString(com.sht.smartlock.R.string.kilometer);
            }
        }
        return distance;
    }
    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     *
     * @param username
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final boolean isGCM,final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(isGCM, emCallBack);
    }

    /**
     2  * 获取版本号
     3  * @return 当前应用的版本号
     4  */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本错误";
        }
    }


    public static  void toLog(String ss){
        Log.i(TAG, "-------->" + ss);
    }

    //获取登录的用户名
    public static String getShareUserName() {

        return getProperty(Config.SHARE_USER_ACCOUNT);
    }

    //获取登录的密码
    public static String getShareUserPwd() {
        String password =getProperty(Config.SHARE_USERPWD);
        //解密操作
//        try
//        {
//            // 从字符串中得到私钥
//            PrivateKey privateKey = RSAUtils.loadPrivateKey(Config.PRIVATE_KEY);
//            // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
//            byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(password), privateKey);
//            String   decryptStr = new String(decryptByte);
//            return decryptStr;
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }


        return password;
    }

    //获取登录的sessionid
    public static String getShareUserSessinid() {
        return getProperty(Config.SHARE_USERSESSIONID);
    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    Log.e("TAG", "--后台--"+appProcess.processName);
//                    return true;
//                } else {
//                    Log.e("TAG", "--前台--" + appProcess.processName);
//                    return false;
//                }
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    Log.e("TAG", "--后台--" + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    Log.e("TAG", "--前台--" + appProcess.processName);
                    return false;
                }

            }
        }
        return false;
    }


    /**
     * 弹窗--新手指引
     * @param cxt
     * @param id 资源编号
     * @create_time 2011-7-27 下午05:12:49
     */
    public static void displayWindow(Context cxt, int id) {
        final TextView imgTV = new TextView(cxt.getApplicationContext());
        imgTV.setBackgroundDrawable(cxt.getResources().getDrawable(id));//设置背景
        final WindowManager wm = (WindowManager) cxt.getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.format = 1;
        wmParams.flags = 40;
        imgTV.setText("您有1笔消费计入客房账单，可在\"我的订单\"中查看");
        imgTV.setPadding(10,10,10,10);
        imgTV.setTextColor(applicationContext.getResources().getColor(R.color.TextBlack054));
        wmParams.width = 600;
        wmParams.height = 540;
        wm.addView(imgTV, wmParams);
        imgTV.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(imgTV);//点击，将该窗口消失掉
            }
        });
    }
    private static void inDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setMessage("您有1笔消费计入客房账单，可在\"我的订单\"中查看").setCancelable(false).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    /**
     * 判断程序是否在前台运行
     * @param context
     * @return false在前台，true在后台
     */
    private static boolean isAppForeground(Context context) {
        boolean isForground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String pkgName : processInfo.pkgList) {
                        if (pkgName.equals(context.getPackageName())) {
                            isForground = false;
                        }
                    }
                }
            }
        } else {
            //@deprecated As of {@link android.os.Build.VERSION_CODES#LOLLIPOP}, 从Android5.0开始不能使用getRunningTask函数
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isForground = false;
            }
        }

        return isForground;
    }

}
