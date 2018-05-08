package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.helper.PwdGenHelper;
import com.sht.smartlock.mqtt.MQTTService;
import com.sht.smartlock.receiver.DemoIntentService;
import com.sht.smartlock.receiver.DemoPushService;
//import com.sht.smartlock.receiver.PushReceiver;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.fragment.NewLockFragment;
import com.sht.smartlock.ui.activity.fragment.NewServiceFragment;
import com.sht.smartlock.ui.activity.mine.GuideActivity;
import com.sht.smartlock.ui.activity.mine.MyBalanceActivity;
import com.sht.smartlock.ui.activity.mine.MyBillActivity;
import com.sht.smartlock.ui.activity.mine.MyCollectionActivity;
import com.sht.smartlock.ui.activity.mine.MyCommentsActivity;
import com.sht.smartlock.ui.activity.mine.MyInformationActivity;
import com.sht.smartlock.ui.activity.mine.MyOrderActivity;
import com.sht.smartlock.ui.activity.mine.MySettingsActivity;
import com.sht.smartlock.ui.activity.slidingmenu.SlidingMenu;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.InviteMessgeDao;
import com.sht.smartlock.ui.chat.applib.db.UserDao;
import com.sht.smartlock.ui.chat.applib.domain.InviteMessage;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.uidemo.Constant;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.HuanXinUserEntity;
import com.sht.smartlock.util.DeviceUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.util.NotificationUtil;
import com.sht.smartlock.util.VersionUpdateUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.sht.smartlock.zxing.CaptureActivity;
import com.yuntongxun.ecsdk.ECDevice;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends BaseActivity implements View.OnClickListener, EMEventListener {

    /**
     * ************************************************************************
     * **                              _oo0oo_                               **
     * **                             o8888888o                              **
     * **                             88" . "88                              **
     * **                             (| -_- |)                              **
     * **                             0\  =  /0                              **
     * **                           ___/'---'\___                            **
     * **                        .' \\\|     |// '.                          **
     * **                       / \\\|||  :  |||// \\                        **
     * **                      / _ ||||| -:- |||||- \\                       **
     * **                      | |  \\\\  -  /// |   |                       **
     * **                      | \_|  ''\---/''  |_/ |                       **
     * **                      \  .-\__  '-'  __/-.  /                       **
     * **                    ___'. .'  /--.--\  '. .'___                     **
     * **                 ."" '<  '.___\_<|>_/___.' >'  "".                  **
     * **                | | : '-  \'.;'\ _ /';.'/ - ' : | |                 **
     * **                \  \ '_.   \_ __\ /__ _/   .-' /  /                 **
     * **            ====='-.____'.___ \_____/___.-'____.-'=====             **
     * **                              '=---='                               **
     * ************************************************************************
     * **                        佛祖保佑      镇类之宝                         **
     * ************************************************************************
     *
     */
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private TextView mTitleBar;
    // 账号在别处登录
    public static boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;
    protected static final String TAG = "MainActivity";
    private MyConnectionListener connectionListener = null;
    private MyGroupChangeListener groupChangeListener = null;
    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;
//    private String user_id = "41";
//    public static List<MyLockEntity> list;

    private TextView tvTabBooking, tvTabIndex, tvTabMine;
    private ImageButton ibTabBooking, ibTabIndex, ibTabMine;
    private ImageView ivScan;

    /**
     * 底部四个按钮
     */
    private LinearLayout mTabBtnBooking;
    public static LinearLayout mTabBtnIndex;
    public static  LinearLayout mTabBtnMine;
    private ImageView iv_MyChat_History;
    public TextView tv_MyChat_Num;
    //个推------------------------------------------------------------
    /**
     * 第三方应用Master Secret，修改为正确的值  个推
     */
    private static final String MASTERSECRET = "LpLLsiIyjB6ZL6kcoYSr37";
    /**
     * 透传测试
     */
    private Button transmissionBtn;

    /**
     * 通知测试
     */
    private Button notifactionBtn;

    /**
     * SDK服务是否启动
     */
    private boolean isServiceRunning = true;
    private Context context;
    private SimpleDateFormat formatter;
    private Date curDate;

    // SDK参数，会自动从Manifest文件中读取，第三方无需修改下列变量，请修改AndroidManifest.xml文件中相应的meta-data信息。
    // 修改方式参见个推SDK文档
    private String appkey = "";
    private String appsecret = "";
    private String appid = "";
    private ImageView ivOpenDoorRecord;

    private String useName;
    private String pdw;
    public static int ISSCAN = 100;
    private int num = 0;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 102;
    private static final int WRITE_RESULT_CODE02 = 103;//定位
    private static final int WRITE_RESULT_CODE04 = 104;//个推
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.CAMERA;
    //    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    //权限名称
    private static final String  WRITE_PERMISSION22 = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String  WRITE_PERMISSION03 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String WRITE_PERMISSION04 = Manifest.permission.RECORD_AUDIO;//录音权限
    private static final String WRITE_PERMISSION05 = Manifest.permission.READ_PHONE_STATE;//  read phone state用于获取 imei 设备信息
    private List<String> listPermission = new ArrayList<>();
    private double latitude;
    private double longitude;
    private MySQLiteOpenHelper mydbHelper = null;
    private InputMethodManager inputMethodManager;
    private LocationBroadcastReceiver  receiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    if (DemoHXSDKHelper.getInstance().isLogined()) {
//                        refreshUI();
//                    init();
//                        Log.e("TAAG","------->"+"handler");
//                    }
                    break;

            }
        }
    };
    public static SlidingMenu idMenu;
    private ImageView ivMe;
    private ImageView ivMePic;
    private TextView tvName;
    private ImageView ivSetting, ivBalance, ivBindPhoneNotify, ivDot, ivCoupons;
    private LinearLayout linOrder, linBill, linFriends, linPwd, linMyInfo, linTakePhone, linComment, linLoginOut, llColl;

    private NewServiceFragment serviceFragment;
    private LinearLayout linRgChecked;
    private RadioGroup rgTab;
    public static ImageView ivServiceMsg;
    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = DemoPushService.class;
    // Preferences instance
    private SharedPreferences mPrefs;
    public static RelativeLayout relGuiTop,relGuiServicer;
    public static ImageView ivWord08,ivWord09;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppContext.getProperty(Config.ISFIRSTLOGIN) == null) {//ISFIRSTLOGIN
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
            AppContext.setProperty(Config.ISFIRSTLOGIN, "true");
            finish();
            return;
        }
        AppContext.mainActivity=this;
        check_versionUpdate();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        LogUtil.log("aaaq=" + DeviceUtil.versionCode());
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION22);
        listPermission.add(WRITE_PERMISSION02);
        listPermission.add(WRITE_PERMISSION03);
        listPermission.add(WRITE_PERMISSION04);
        listPermission.add(WRITE_PERMISSION05);
        if (AppContext.getProperty(getString(R.string.booking_cityname)) == null) {
            openGPSSettings();
        }
//        listPermission.add(WRITE_PERMISSION02);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        getMyGeTui();
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        getView();
        initView();
        //开启MQTT服务
        //
//        MQTTService.publish("");


//        getMyLockInfo();
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        iv_MyChat_History.setVisibility(View.GONE);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int currentIndex;

            @Override
            public void onPageSelected(int position) {
                resetTabBtn();
                switch (position) {
                    case 0:
                        mTitleBar.setText(R.string.openDoor);
                        mTitleBar.setVisibility(View.VISIBLE);
                        tvTabBooking.setTextColor(getResources().getColor(R.color.text_selected));
                        ibTabBooking.setImageResource(R.drawable.btn_opendoor_choosed);
                        iv_MyChat_History.setVisibility(View.GONE);
                        tv_MyChat_Num.setVisibility(View.GONE);
                        ivScan.setVisibility(View.VISIBLE);
                        ivOpenDoorRecord.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mTitleBar.setText(R.string.ServiceLock);
                        mTitleBar.setVisibility(View.VISIBLE);
                        tvTabIndex.setTextColor(getResources().getColor(R.color.text_selected));
                        ibTabIndex.setImageResource(R.drawable.btn_services_choosed);
                        iv_MyChat_History.setVisibility(View.GONE);
                        tv_MyChat_Num.setVisibility(View.GONE);
                        ivScan.setVisibility(View.VISIBLE);
                        ivOpenDoorRecord.setVisibility(View.GONE);

                        break;
                    case 2:
                        mTitleBar.setText(R.string.tab_booking);
                        mTitleBar.setVisibility(View.VISIBLE);
                        tvTabMine.setTextColor(getResources().getColor(R.color.text_selected));
                        ibTabMine.setImageResource(R.drawable.btn_hotel_choosed);
                        iv_MyChat_History.setVisibility(View.GONE);
                        ivScan.setVisibility(View.VISIBLE);
                        ivOpenDoorRecord.setVisibility(View.GONE);
//                        int n = Integer.parseInt(tv_MyChat_Num.getText().toString());
//                        if (n > 0) {
//                            tv_MyChat_Num.setVisibility(View.GONE);
//                        } else {
//                            tv_MyChat_Num.setVisibility(View.GONE);
//                        }
                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case 1:

                        break;
                    default:

                }
            }
        });
//        mViewPager.setCurrentItem(1);
//        if(!AppContext.getShareUserSessinid().equals("")){
        mViewPager.setOffscreenPageLimit(2);
//        }
        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);

        /*
        *   聊天未读数
        *
        * */
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            int unreadMsgCountTotal = getUnreadMsgCountTotal();
            if (unreadMsgCountTotal > 0) {
//                tv_MyChat_Num.setText(unreadMsgCountTotal + "");
//                tv_MyChat_Num.setVisibility(View.GONE);
            } else {
//                tv_MyChat_Num.setVisibility(View.GONE);
//                tv_MyChat_Num.setText(0 + "");
            }
        }

        /*
        *   環信
        * */
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (AppContext.getShareUserSessinid() != null) {
                init();
                refreshUI();
                Log.e("TAAG", "------->" + "oncreate");
                //账号没有登录
            } else {
            }
        } else {
            Log.e("TAAG", "------->" + "else__oncreate");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(6000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void getMyGeTui() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION,WRITE_PERMISSION22,WRITE_PERMISSION03,WRITE_PERMISSION04);
        if (b) {
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        } else {
            mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE04);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//        if (AppContext.payloadData != null) {
//            tLogView.append(DemoApplication.payloadData);
//        }

        // cpu 架构
        Log.d(TAG, "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

        // 检查 so 是否存在
//        File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
//        Log.e(TAG, "libgetuiext2.so exist = " + file.exists());


    }

    protected void resetTabBtn() {
        tvTabBooking.setTextColor(getResources().getColor(R.color.text_hint));
        tvTabIndex.setTextColor(getResources().getColor(R.color.text_hint));
        tvTabMine.setTextColor(getResources().getColor(R.color.text_hint));
        ibTabBooking.setImageResource(R.drawable.btn_opendoorboom);
        ibTabIndex.setImageResource(R.drawable.btn_services_n);
        ibTabMine.setImageResource(R.drawable.btn_hotel);
    }

    private void initView() {

        idMenu = (SlidingMenu) findViewById(R.id.idMenu);
        ivMe = (ImageView) findViewById(R.id.ivMe);
        ivOpenDoorRecord = (ImageView) findViewById(R.id.ivOpenDoorRecord);
        //我的消息
        iv_MyChat_History = (ImageView) findViewById(R.id.iv_MyChat_History);
        tv_MyChat_Num = (TextView) findViewById(R.id.tv_MyChat_Num);
//        radioButton_server = (RadioButton) findViewById(R.id.radioButton_server);

        mTabBtnBooking = (LinearLayout) findViewById(R.id.id_tab_bottom_booking);
        mTabBtnIndex = (LinearLayout) findViewById(R.id.id_tab_bottom_index);
        mTabBtnMine = (LinearLayout) findViewById(R.id.id_tab_bottom_mine);
        mTitleBar = (TextView) findViewById(R.id.tvTitleBar);
        tvTabBooking = (TextView) findViewById(R.id.tv_tab_bottom_booking);
        tvTabIndex = (TextView) findViewById(R.id.tv_tab_bottom_index);
        tvTabMine = (TextView) findViewById(R.id.tv_tab_bottom_mine);
        ibTabBooking = (ImageButton) findViewById(R.id.btn_tab_bottom_booking);
        ibTabIndex = (ImageButton) findViewById(R.id.btn_tab_bottom_index);
        ibTabMine = (ImageButton) findViewById(R.id.btn_tab_bottom_mine);
        ivScan = (ImageView) findViewById(R.id.ivScan);
        ivServiceMsg = (ImageView) findViewById(R.id.ivServiceMsg);

        linRgChecked = (LinearLayout) findViewById(R.id.linRgChecked);
        rgTab = (RadioGroup)findViewById(R.id.rgTab);

        //指导
        relGuiTop = (RelativeLayout) findViewById(R.id.relGuiTop);
        ivWord08 = (ImageView) findViewById(R.id.ivWord08);
        relGuiServicer = (RelativeLayout) findViewById(R.id.relGuiServicer);
        ivWord09 = (ImageView) findViewById(R.id.ivWord09);
        relGuiTop.setOnClickListener(this);
        relGuiServicer.setOnClickListener(this);

        mTabBtnBooking.setOnClickListener(this);
        mTabBtnIndex.setOnClickListener(this);
        mTabBtnMine.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivWord08.setOnClickListener(NewDoorFragment.onClickListener);
        ivMe.setOnClickListener(this);
        ivOpenDoorRecord.setOnClickListener(this);

//        MainTabBookingFragment tab01 = new MainTabBookingFragment();
//        MainTabIndexFragment tab02 = new MainTabIndexFragment();
//        MainTabMineFragment tab03 = new MainTabMineFragment();

        NewDoorFragment doorFragment = new NewDoorFragment();
        serviceFragment = new NewServiceFragment();
        NewLockFragment lockFragment = new NewLockFragment();

        mFragments.add(doorFragment);
        mFragments.add(serviceFragment);
        mFragments.add(lockFragment);


        rgTab.setOnCheckedChangeListener(NewServiceFragment.onCheckedChangeListener);
//        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rbService:
//                        NewServiceFragment.linLeft.setVisibility(View.VISIBLE);
//                        NewServiceFragment.linRight.setVisibility(View.GONE);
//                        break;
//                    case R.id.rbCall:
//                        NewServiceFragment.linLeft.setVisibility(View.GONE);
//                        NewServiceFragment.linRight.setVisibility(View.VISIBLE);
////                        todo();
//                        break;
//                }
//            }
//        });


        //我的信息
        iv_MyChat_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), MyChatHistoryActivity.class);
                    startActivity(intent);
                } else {
                    String myuseName = AppContext.getProperty(Config.SHARE_USER_ACCOUNT);
                    if (myuseName == null) {
                        //账号未登陆
                        LoginHuanXin();
                    } else {//app已经登录
                        if (AppContext.getProperty(Config.EMID) != null) {
                            useName = AppContext.getProperty(Config.EMID);
                        }
                        if (AppContext.getProperty(Config.SHARE_USERPWD) != null) {
                            pdw = AppContext.getProperty(Config.SHARE_USERPWD);
                            AppContext.toLog("pdw" + pdw);
                        }
                        if (useName == null) {
                            useName = myuseName;
                            //创建环信账号 ，通过app账号注册登录
                            registerHuanXin();

                        } else {
                            //登录账号绑定的环信账号
                            loginHuanXin(useName, pdw);
                        }

                    }
                    BaseApplication.toast("聊天账号未登陆，正在初始化");
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    protected boolean hasToolBar() {
        return false;
    }

    protected boolean hasFeatureTitle() {
        return false;
    }
    //获取用户订酒店的信息
//    private void getMyLockInfo(){
//        HttpClient.instance().getMyLockInfo( new HttpCallBack() {
//            @Override
//            public void onSuccess(ResponseBean responseBean) {
//                list=responseBean.getListData(MyLockEntity.class);
//                AppContext.toLog("mylock"+responseBean.toString()+list.size()+list.toString());
//
//
//            }
//
//            @Override
//            public void onFailure(String error, String message) {
//                super.onFailure(error, message);
//            }
//        });
//    }


    @Override
    public void onClick(View v) {
        idMenu.closeMenu();
        switch (v.getId()) {
            case R.id.id_tab_bottom_booking:
                resetTabBtn();
                mViewPager.setCurrentItem(0);
                mTitleBar.setText(R.string.openDoor);
                mTitleBar.setVisibility(View.VISIBLE);
                tvTabBooking.setTextColor(getResources().getColor(R.color.text_selected));
                ibTabBooking.setImageResource(R.drawable.btn_opendoor_choosed);
                iv_MyChat_History.setVisibility(View.GONE);
                tv_MyChat_Num.setVisibility(View.GONE);
                ivScan.setVisibility(View.VISIBLE);
                ivOpenDoorRecord.setVisibility(View.VISIBLE);
                linRgChecked.setVisibility(View.GONE);
                break;
            case R.id.id_tab_bottom_index:
                resetTabBtn();
                mViewPager.setCurrentItem(1);
                mTitleBar.setText(R.string.ServiceLock);
                mTitleBar.setVisibility(View.VISIBLE);
                tvTabIndex.setTextColor(getResources().getColor(R.color.text_selected));
                ibTabIndex.setImageResource(R.drawable.btn_services_p);
                iv_MyChat_History.setVisibility(View.GONE);
                tv_MyChat_Num.setVisibility(View.GONE);
                ivScan.setVisibility(View.VISIBLE);
                ivOpenDoorRecord.setVisibility(View.GONE);
                linRgChecked.setVisibility(View.GONE);
                break;
            case R.id.id_tab_bottom_mine:
                resetTabBtn();
                mViewPager.setCurrentItem(2);
                mTitleBar.setText(R.string.tab_booking);
                mTitleBar.setVisibility(View.VISIBLE);
                tvTabMine.setTextColor(getResources().getColor(R.color.text_selected));
                ibTabMine.setImageResource(R.drawable.btn_hotel_choosed);
                iv_MyChat_History.setVisibility(View.GONE);
                ivScan.setVisibility(View.VISIBLE);
                ivOpenDoorRecord.setVisibility(View.GONE);
                linRgChecked.setVisibility(View.GONE);
//                int n = Integer.parseInt(tv_MyChat_Num.getText().toString());
//                if (n > 0) {
//                    tv_MyChat_Num.setVisibility(View.VISIBLE);
//                } else {
//                    tv_MyChat_Num.setVisibility(View.GONE);
//
//                }
                break;
            case R.id.ivScan:
//                listPermission.clear();
//                listPermission.add(WRITE_PERMISSION);
                boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION,WRITE_PERMISSION22,WRITE_PERMISSION03,WRITE_PERMISSION04);
                if (b) {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, ISSCAN);
                } else {
                    mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                }


                break;
            case R.id.ivMe:
                idMenu.toggle();
                break;
            case R.id.ivOpenDoorRecord:
                //判断是否登录
                Intent intent = new Intent();
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getApplicationContext(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (NewDoorFragment.list.size() > 0) {
                    if (NewDoorFragment.noInHole) {
                        intent.setClass(getApplicationContext(), OpenDoorRecordActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("room_id", NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                        bundle.putString(Config.BOOK_ID, NewDoorFragment.list.get(NewDoorFragment.pos).getBook_id());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        AppContext.toast(R.string.Toast_Login_Chenkin);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;

        }
    }

    //以下是环信 部分代码

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    private void init() {
        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        EMChat.getInstance().setAppInited();
        Log.e("TAAG", "---------------------------->" + "开始监听好友变化");
        // 注册一个监听连接状态的listener
        connectionListener = new MyConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);
//        AppContext.toast("进来 init()");

        groupChangeListener = new MyGroupChangeListener();
        // 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);


    }


    public static void asyncFetchGroupsFromServer() {
        HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

            @Override
            public void onSuccess() {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

                if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
            }

            @Override
            public void onError(int code, String message) {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

        });
    }

    static void asyncFetchContactsFromServer() {
        HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>() {

            @Override
            public void onSuccess(List<String> usernames) {
                Context context = HXSDKHelper.getInstance().getAppContext();

                System.out.println("----------------" + usernames.toString());
                EMLog.d("roster", "contacts size: " + usernames.size());
                Map<String, User> userlist = new HashMap<String, User>();
                for (String username : usernames) {
                    User user = new User();
                    user.setUsername(username);
                    setUserHearder(username, user);
                    userlist.put(username, user);
                }
                // 添加user"申请与通知"
                User newFriends = new User();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                String strChat = context.getString(R.string.Application_and_notify);
                newFriends.setNick(strChat);

                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                // 添加"群聊"
                User groupUser = new User();
                String strGroup = context.getString(R.string.group_chat);
                groupUser.setUsername(Constant.GROUP_USERNAME);
                groupUser.setNick(strGroup);
                groupUser.setHeader("");
                userlist.put(Constant.GROUP_USERNAME, groupUser);

                // 添加"聊天室"
                User chatRoomItem = new User();
                String strChatRoom = context.getString(R.string.chat_room);
                chatRoomItem.setUsername(Constant.CHAT_ROOM);
                chatRoomItem.setNick(strChatRoom);
                chatRoomItem.setHeader("");
                userlist.put(Constant.CHAT_ROOM, chatRoomItem);

                // 添加"Robot"
                User robotUser = new User();
                String strRobot = context.getString(R.string.robot_chat);
                robotUser.setUsername(Constant.CHAT_ROBOT);
                robotUser.setNick(strRobot);
                robotUser.setHeader("");
                userlist.put(Constant.CHAT_ROBOT, robotUser);
                AppContext.toLog(userlist.size() + "存了");
                Log.e("TTTTTT", "------------------------>>>" + userlist.size() + "存了");
                // 存入内存
                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
                // 存入db
                UserDao dao = new UserDao(context);
                List<User> users = new ArrayList<User>(userlist.values());
                dao.saveContactList(users);
//                AppContext.toast(userlist.size()+"存了");
                HXSDKHelper.getInstance().notifyContactsSyncListener(true);

                if (HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }

                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new EMValueCallBack<List<User>>() {

                    @Override
                    public void onSuccess(List<User> uList) {
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).updateContactList(uList);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().notifyContactInfosSyncListener(true);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyContactsSyncListener(false);
            }

        });
    }

    static void asyncFetchBlackListFromServer() {
        HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>() {

            @Override
            public void onSuccess(List<String> value) {
                EMContactManager.getInstance().saveBlackList(value);
                HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
            }

        });
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }


    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();

                break;
            }

            case EventConversationListChanged: {
                refreshUI();

                break;
            }

            default:

                break;
        }
    }

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (serviceFragment != null) {
                    serviceFragment.refreshMain();
                }

            }
        });
    }


    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            for (String username : usernameList) {
                User user = setUserHead(username);
                // 添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            Log.e("TAG", "------------->保存增加的联系人" + usernameList.size());
            // 刷新ui
//            if (currentTabIndex == 1)
//                contactListFragment.refresh();
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null
                            && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
//                    updateUnreadLabel();
//                    // 刷新ui
//                    contactListFragment.refresh();
//                    chatHistoryFragment.refresh();
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            Log.e("TAG", "------------->" + username + "请求加你为好友,reason: " + reason);
        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {

            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 连接监听listener
     */
    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();
//            AppContext.toast(""+contactSynced);
            Log.e("TTTTTTTTTT", "------------------" + contactSynced);
            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if (groupSynced && contactSynced) {
                new Thread() {
                    @Override
                    public void run() {
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            } else {
                if (!groupSynced) {
                    asyncFetchGroupsFromServer();
                }

                if (!contactSynced) {
                    asyncFetchContactsFromServer();
                }

                if (!HXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
                    asyncFetchBlackListFromServer();
                }
            }

//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    chatHistoryFragment.errorItem.setVisibility(View.GONE);
//                }
//
//            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            Log.e("TTTTTTTTTT", "------------------" + error);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    BaseApplication.toast("error"+error);
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
//                        BaseApplication.toast("error------->"+error);
                        showConflictDialog();
                    } else {
                        AppContext.toLog("error==" + error);
//                        chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
//                        if (NetUtils.hasNetwork(MainActivity.this))
//                            chatHistoryFragment.errorText.setText(st1);
//                        else
//                            chatHistoryFragment.errorText.setText(st2);

                    }
                }

            });
        }
    }

    /**
     * MyGroupChangeListener
     */
    public class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + " " + st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

//            runOnUiThread(new Runnable() {
//                public void run() {
//                    updateUnreadLabel();
//                    // 刷新ui
//                    if (currentTabIndex == 0)
//                        chatHistoryFragment.refresh();
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
//                }
//            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

            // 提示用户被T了，demo省略此步骤
            // 刷新ui
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    try {
//                        updateUnreadLabel();
//                        if (currentTabIndex == 0)
//                            chatHistoryFragment.refresh();
//                        if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                            GroupsActivity.instance.onResume();
//                        }
//                    } catch (Exception e) {
//                        EMLog.e(TAG, "refresh exception " + e.getMessage());
//                    }
//                }
//            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {

            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    updateUnreadLabel();
//                    if (currentTabIndex == 0)
//                        chatHistoryFragment.refresh();
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
//                }
//            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + " " + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

//            runOnUiThread(new Runnable() {
//                public void run() {
//                    updateUnreadLabel();
//                    // 刷新ui
//                    if (currentTabIndex == 0)
//                        chatHistoryFragment.refresh();
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
//                }
//            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
//        updateUnreadAddressLable();
//        // 刷新好友页面ui
//        if (currentTabIndex == 1)
//            contactListFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(AppContext.instance());
        }
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
//        if (user.getUnreadMsgCount() == 0)
//            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
        if (user!=null){
             user.setUnreadMsgCount(1);
        }
    }

    /**
     * set head
     *
     * @param username
     * @return
     */
    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSoftKeyboard();

        num = 0;
        if (!(AppContext.getProperty(Config.SHARE_USER_Name) == null) && !(AppContext.getProperty(Config.SHARE_USER_Name).equals(""))) {
            tvName.setText(AppContext.getProperty(Config.SHARE_USER_Name));
        } else {
            if (AppContext.getShareUserSessinid() != null){
                clearSharePassword();
            }
            if (tvName!=null){
                tvName.setText("登录/注册");
            }
        }
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        if (AppContext.getShareUserSessinid() == null) {
            //登录环信
            // 如果用户名密码都有，不需要再次登录
            if (!DemoHXSDKHelper.getInstance().isLogined()) {
                Log.i("TAG", "环信没有：" + DemoHXSDKHelper.getInstance().isLogined() + "");
                LoginHuanXin();
            }
        } else {
            //登录绑定的环信账号 ，或者是注册
            makeHuanXinLogin();
        }
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
        /*
        *   環信
        * */
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            refreshUI();
//            init();
//            EMChatManager.getInstance().activityResumed();
            Log.e("TAAG", "------->" + "onResume");

            if (AppContext.getShareUserSessinid() != null) {
                get_user_head_image();
                initData();
            } else {
                if (ivMePic!=null){
                     ivMePic.setImageResource(R.drawable.pic_default_user);
                }
            }
        }


        if (AppContext.getProperty(Config.GTCIDBYISTRUEIN)!=null&&AppContext.getProperty(Config.GTCIDBYISTRUEIN).equals("true")){
            //有个推消息
            if (AppContext.getProperty(Config.GTCIDBYCONFIRM)!=null&&AppContext.getProperty(Config.GTCIDBYCONFIRM).equals("1")){//需要确认
                Intent intent = new Intent();
                intent.setClass(this, OtherOrderInfoActivity.class);
                //                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Config.GTCIDBYISTRUE, true);
                intent.putExtra(Config.GTCIDBYJSOn, AppContext.getProperty(Config.GTCIDBYORDERID));
                intent.putExtra(Config.GTBOOKINGID, AppContext.getProperty(Config.GTBOOKINGIDBySAVE));
                startActivity(intent);
                AppContext.setProperty(Config.GTCIDBYORDERID, null);
                AppContext.setProperty(Config.GTCIDBYISTRUEIN, null);
                AppContext.setProperty(Config.GTCIDBYCONFIRM, null);
                AppContext.setProperty(Config.GTBOOKINGIDBySAVE,null);
            }else {
                AppContext.toastLong("您有1笔消费计入客房账单，可在\"我的订单\"中查看");
                NotificationUtil notiUtil = new NotificationUtil(this);
                notiUtil.postNotification(AppContext.getProperty(Config.GTCIDBYORDERID),AppContext.getProperty(Config.GTBOOKINGIDBySAVE));
                AppContext.setProperty(Config.GTCIDBYORDERID, null);
                AppContext.setProperty(Config.GTCIDBYISTRUEIN, null);
                AppContext.setProperty(Config.GTCIDBYCONFIRM, null);
                AppContext.setProperty(Config.GTBOOKINGIDBySAVE,null);
            }
        }
        //个推未绑定就绑定
        if (AppContext.getShareUserSessinid() != null) {
            if (AppContext.getProperty(Config.ISBINDGTCID) == null || !AppContext.getProperty(Config.ISBINDGTCID).equals("true")) {
                if (AppContext.getProperty(Config.GTCID) != null) {
                    bangGT(AppContext.getProperty(Config.GTCID));
                } else {
                    Log.e("paramss", "--------个推id=" + AppContext.getProperty(Config.GTCID));
                    getMyGeTui();
                }
            }
        }
        if (AppContext.getProperty(Config.GTCID)==null){
            getMyGeTui();
        }
        if (AppContext.getProperty(Config.ISFALSEGUI)==null){
            //
            mTabBtnIndex.setClickable(false);
            mTabBtnMine.setClickable(false);
        }

        if (AppContext.getProperty(Config.ISFALSEGUI)!=null&&AppContext.getProperty(Config.ISFALSEGUIBYSKIT)==null && AppContext.getShareUserSessinid()!=null){
            //首次登陆指引    必须首页
            mViewPager.setCurrentItem(0);
        }

    }
    private void bangGT(String cid){
        HttpClient.instance().bandGT(cid,new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.setProperty(Config.ISBINDGTCID,"true");
                }else {
                    AppContext.setProperty(Config.ISBINDGTCID,"false");
                }
            }
        });
    }
    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }


    private void LoginHuanXin() {
//        DemoHXSDKHelper.getInstance().isLogined()
        if (AppContext.getProperty(Config.HUANXINUSER) != null) {
            //登录原来的环信账号
            loginHuanXin(AppContext.getProperty(Config.HUANXINUSER), AppContext.getProperty(Config.HUANXINPWD));
        } else {
            //创建新的环信账号。
            HttpClient.instance().huanxin_CreateUser(new HuanXinUser());
        }

    }

    class HuanXinUser extends HttpCallBack {

        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
//            ProgressDialog.disMiss();
            System.out.println("失败" + error + message);
            // 如果用户名密码都有，不需要再次登录
            if (!DemoHXSDKHelper.getInstance().isLogined()) {
                Log.i("TAG", DemoHXSDKHelper.getInstance().isLogined() + "");
                if (num < 3) {
                    LoginHuanXin();
                }
            }
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
//            ProgressDialog.disMiss();
            if (responseBean.isFailure()) {
                toastFail("失败----");
                AppContext.toLog(responseBean.toString() + "失败");
                return;
            }
            try {
                final HuanXinUserEntity user = responseBean.getData(HuanXinUserEntity.class);
                Log.i("TAG", "---------" + user.toString());
                AppContext.setProperty(Config.HUANXINUSER, user.getUsername());
                AppContext.setProperty(Config.HUANXINPWD, user.getUsername());

                //用户名 和密码相同user.getUsername()
                EMChatManager.getInstance().login(user.getUsername(), user.getUsername(), new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMGroupManager.getInstance().loadAllGroups();
                                EMChatManager.getInstance().loadAllConversations();
                                Log.d("main", "登陆聊天服务器成功！");
                                // 登陆成功，保存用户名密码
                                AppContext.getInstance().setUserName(user.getUsername());
                                AppContext.getInstance().setPassword(user.getUsername());
//                                init();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("main", "登陆聊天服务器失败！" + message);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                AppContext.toLog("huanxin==" + e.toString());
            }
        }
    }

    /*
    *   登录环 信
    *
    * */
    private void makeHuanXinLogin() {

        if (AppContext.getProperty(Config.EMID) != null) {
            useName = AppContext.getProperty(Config.EMID);
        }

        if (AppContext.getProperty(Config.MYHUANXINPWD) != null) {
            //返回的环信账号
            pdw = AppContext.getProperty(Config.MYHUANXINPWD);
        } else {
            //原始方式的环信账号
            if (AppContext.getProperty(Config.SHARE_USERPWD) != null) {
                pdw = AppContext.getProperty(Config.SHARE_USERPWD);
                AppContext.toLog("pdw" + pdw);
            }
        }


        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            //环信账号为空
            AppContext.toLog("emid" + AppContext.getProperty(Config.EMID) + ";;useName" + useName);
            /*
            *   判断账号有没有环信账号，没有就去注册
            *
            * */
            if (useName == null || useName.equals("null")) {
                //使用我的账号注册登录
                AppContext.toLog(useName);
                String myuseName = AppContext.getProperty(Config.SHARE_USER_ACCOUNT);
                /*
                *   判断用户是否登录。登录了就去注册账号
                * */
                if (myuseName != null) {
                    useName = myuseName;
                    registerHuanXin();
                }
            } else {//账号不为空时再判断是否相等
                if (!AppContext.getInstance().getUserName().equals(useName)) {
                    Log.i("TAG", DemoHXSDKHelper.getInstance().isLogined() + "aa");
                    //先退出登录
                    EMChatManager.getInstance().logout();//此方法为同步方法
                    if (AppContext.getProperty(Config.MYHUANXINPWD) != null && !AppContext.getProperty(Config.MYHUANXINPWD).equals("null")) {
                        loginHuanXin(useName, pdw);
                    } else {//只有环信账号没有密码
                        String b = String.valueOf((int) (Math.random() * 1000));//
                        useName = useName + b;
                        registerHuanXin();
                        AppContext.toLog("重新注册");
                    }
                }
            }

        } else {//环信没有登录
            AppContext.toLog("没有登录");
            if (useName == null || useName.equals("null")) {
                //使用我的账号注册登录
                String myuseName = AppContext.getProperty(Config.SHARE_USER_ACCOUNT);
                /*
                *   判断用户是否登录。登录了就去注册账号
                * */
                if (myuseName != null) {
                    useName = myuseName;
                    registerHuanXin();
                } else {
                    //创建环信账号
//                    CreateHuanXin();
                    //创建新的环信账号。
                    HttpClient.instance().huanxin_CreateUser(new HuanXinUser());
                }
            } else {//账号不为空时再判断是否相等
                if (AppContext.getProperty(Config.MYHUANXINPWD) != null && !AppContext.getProperty(Config.MYHUANXINPWD).equals("null")) {
                    loginHuanXin(useName, pdw);
                } else {//只有环信账号没有密码
                    String b = String.valueOf((int) (Math.random() * 100));//
                    useName = useName + b;
                    registerHuanXin();
                    AppContext.toLog("重新注册");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

//            // 创建退出对话框
//            AlertDialog isExit = new AlertDialog.Builder(this).create();
//            // 设置对话框标题
//            isExit.setTitle("系统提示");
//            // 设置对话框消息
//            isExit.setMessage("确定要退出吗");
//            // 添加选择按钮并注册监听
//            isExit.setButton("确定", listener);
//            isExit.setButton2("取消", listener);
//            // 显示对话框
//            isExit.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
    //    private Dialog conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoHXSDKHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        AppContext.toLog("账号在别处登录");
//        BaseApplication.toast("聊天账号在其他手机端登录");
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
//                if (conflictBuilder == null) {
//                }
//            handler.sendEmptyMessage(0);
//                clearSharePassword();
                final Dialog conflictBuilder = new Dialog(MainActivity.this, R.style.OrderDialog);
                LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
                View view = mInflater.inflate(R.layout.huanxin_login, null);
                conflictBuilder.setContentView(view);

                view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conflictBuilder.dismiss();
                    }
                });
                view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conflictBuilder.dismiss();
                        clearSharePassword();
                        startActivity(new Intent(MainActivity.this, LoginByNameActivity.class));
                    }
                });
                conflictBuilder.show();
                isConflict = true;
                BaseApplication.toast("聊天账号异端登录");

            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
//                BaseApplication.toast("异常了");
            }

        } else {
//            BaseApplication.toast("账号在别处登录");
        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        DemoHXSDKHelper.getInstance().logout(false, null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        AppContext.toLog("账号被移除");
        BaseApplication.toast("聊天账号已被移除");
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginByNameActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 清除密码
     */
    private void clearSharePassword() {
        try {
            NewDoorFragment.tvLock.setText("");
            ivMePic.setImageResource(R.drawable.pic_default_user);
            tvName.setText("登录/注册");
            AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
            AppContext.setProperty(Config.SHARE_USERPWD, null);
            AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
            AppContext.setProperty(Config.SHARE_USER_Name,null);
            //把选择的数据恢复
            //开门密码,
            AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(false));
            EMChatManager.getInstance().logout();//此方法为同步方法
            AppContext.setProperty(Config.EMID, null);
            AppContext.setProperty(Config.MYHUANXINPWD, null);
        } catch (Exception e) {
            AppContext.toLog(e.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        AppContext.toLog(getIntent().getBooleanExtra("conflict", false) + ";;" + isConflictDialogShow);
//        BaseApplication.toast(getIntent().getBooleanExtra("conflict", false) + ";;" + isConflictDialogShow);
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        if (unreadMsgCountTotal > 0) {
//            tv_MyChat_Num.setText(unreadMsgCountTotal + "");
//            tv_MyChat_Num.setVisibility(View.GONE);
        } else {
//            tv_MyChat_Num.setVisibility(View.GONE);
//            tv_MyChat_Num.setText(0 + "");
        }
//        return unreadMsgCountTotal-chatroomUnreadMsgCount;
        return unreadMsgCountTotal;
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
//            tv_MyChat_Num.setText(count + "");

//            if (mViewPager.getCurrentItem() == 2) {
//                tv_MyChat_Num.setVisibility(View.VISIBLE);
//            }
            if (serviceFragment.tvMyChatNum != null) {
                serviceFragment.tvMyChatNum.setVisibility(View.VISIBLE);
                serviceFragment.tvMyChatNum.setText(count + "");
            }
            //设置角标
            boolean b = ShortcutBadger.applyCount(MainActivity.this, num);
        } else {
//            tv_MyChat_Num.setVisibility(View.GONE);
//            tv_MyChat_Num.setText(0 + "");
            if (serviceFragment.tvMyChatNum != null) {
                serviceFragment.tvMyChatNum.setVisibility(View.GONE);
                serviceFragment.tvMyChatNum.setText(0 + "");
            }
            //取消角标
            ShortcutBadger.removeCount(MainActivity.this);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.context_tab_contact, menu);
    }

    private void loginHuanXin(final String userName, final String pwd) {

        if (userName == null || userName.equals("null")) {
            return;
        }
        if (pwd == null || pwd.equals("null")) {
            return;
        }
        //用户名 和密码相同
        EMChatManager.getInstance().login(userName, pwd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        //去获取所有会话, 获取单个会话
                        EMChatManager.getInstance().getConversation(userName);
//                        init();
                        Log.d("main", "登陆聊天服务器成功！");
                        // 登陆成功，保存用户名密码
                        AppContext.getInstance().setUserName(userName);
                        AppContext.getInstance().setPassword(pwd);
                        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                        initializeContacts();
                        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                        boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                                AppContext.getProperty(Config.SHARE_USER_Name));
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登陆聊天服务器失败！" + message);
            }
        });
    }

    private void registerHuanXin() {
        if (useName == null) {
            return;
        }
        //随机生成环信密码
        final PwdGenHelper pwdGenHelper = new PwdGenHelper();
        pdw = pwdGenHelper.getpwd(12, true, false, true);
        HttpClient.instance().huanxin_RegisterUser(useName, pdw, new HttpCallBack() {
            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
//            ProgressDialog.disMiss();
                System.out.println("失败" + error + message);
                // 如果用户名密码都有，不需要再次登录
                if (!DemoHXSDKHelper.getInstance().isLogined()) {
                    Log.i("TAG", DemoHXSDKHelper.getInstance().isLogined() + "");
                    BaseApplication.toast("聊天账号 注册失败");
                }
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                if (responseBean.isFailure()) {
                    toastFail("失败----");
                    AppContext.toLog(responseBean.toString() + "失败");
                    return;
                }
                try {
                    final HuanXinUserEntity user = responseBean.getData(HuanXinUserEntity.class);
                    Log.i("TAG", "---------" + user.toString());
                    //如果账号已经存在
                    if (user.getError() != null) {
                        if (user.getError().equals("null")) {
                        } else {
                            if (user.getError().equals("duplicate_unique_property_exists") || user.getError().equals("illegal_argument")) {
                                String b = String.valueOf((int) (Math.random() * 1000));//
                                pdw = pwdGenHelper.getpwd(9, true, false, true);
                                useName = pdw + b;
                                //使用我的账号注册登录
                                registerHuanXin();
                            }
                        }
                    }

                    //用户名 和密码相同
                    EMChatManager.getInstance().login(user.getUsername(), pdw, new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance().loadAllConversations();
                                    //去获取所有会话, 获取单个会话
                                    EMChatManager.getInstance().getConversation(user.getUsername());
                                    Log.d("main", "登陆聊天服务器成功！");
                                    // 登陆成功，保存用户名密码
                                    AppContext.getInstance().setUserName(user.getUsername());
                                    AppContext.getInstance().setPassword(pdw);
                                    AppContext.setProperty(Config.EMID, user.getUsername());
                                    AppContext.setProperty(Config.MYHUANXINPWD, pdw);
                                    initializeContacts();
                                    // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                                    boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                                            AppContext.getProperty(Config.SHARE_USER_Name));
//                                    init();
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d("main", "登陆聊天服务器失败！" + message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.toLog("huanxin==" + e.toString());
                }
            }
        });
    }
    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(MainActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }



    //---------------
    private void check_versionUpdate() {
        VersionUpdateUtil versionUpdateUtil = new VersionUpdateUtil(MainActivity.this);
        versionUpdateUtil.check(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("DATA", "resultCode" + resultCode);
        if (resultCode == ISSCAN) {
            Bundle extras = data.getExtras();
            Log.e("DATA", "-------------->" + extras.getString("result"));
            String url;
            if (AppContext.getShareUserSessinid() != null) {
                url = extras.getString("result") + "&session=" + AppContext.getShareUserSessinid();//扫一扫入住
                Intent intent = new Intent(getApplicationContext(), ScanOKActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.URLFORREANDIN, url);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                url = extras.getString("result") + "&reg=xxx";
                Intent intent = new Intent(getApplicationContext(), ScanSuccessActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.URLFORREANDIN, url);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        AppContext.toast("MainActivity requestCode=" + requestCode);
        switch (requestCode) {
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, ISSCAN);
                } else {
                    //如果请求失败
                    AppContext.toast("请手动相机权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
            case  WRITE_RESULT_CODE02://定位
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    openGPSSettings();
                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开定位权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
            case WRITE_RESULT_CODE04:
                //个推
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //-----------------------------------下面为侧滑菜单数据---------------------------
    private boolean isbinding = false;
    private String phone = "";

    private void getView() {
        ivMePic = (ImageView) findViewById(R.id.ivMePic);
        tvName = (TextView) findViewById(R.id.tvName);
        ivCoupons = (ImageView) findViewById(R.id.ivCoupons);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        ivBalance = (ImageView) findViewById(R.id.ivBalance);
        ivBindPhoneNotify = (ImageView) findViewById(R.id.ivBindPhoneNotify);
        ivDot = (ImageView) findViewById(R.id.ivDot);

        llColl = (LinearLayout) findViewById(R.id.llColl);
        linOrder = (LinearLayout) findViewById(R.id.linOrder);
        linBill = (LinearLayout) findViewById(R.id.linBill);
        linFriends = (LinearLayout) findViewById(R.id.linFriends);
        linPwd = (LinearLayout) findViewById(R.id.linPwd);
        linMyInfo = (LinearLayout) findViewById(R.id.linMyInfo);
        linTakePhone = (LinearLayout) findViewById(R.id.linTakePhone);
        linComment = (LinearLayout) findViewById(R.id.linComment);
        linLoginOut = (LinearLayout) findViewById(R.id.linLoginOut);

        setMeunListener();

    }

    private void setMeunListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivMePic:
                        if ((AppContext.getProperty(Config.SHARE_USER_ACCOUNT) == null)) {
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), MyInformationActivity.class));
                        }
                        break;
                    case R.id.ivSetting:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MySettingsActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.ivCoupons:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyCouponsActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.llColl:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyCollectionActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.ivBalance:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyBalanceActivity.class));
//					startActivity(new Intent(getActivity().getApplication(), RechargeActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linOrder:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyOrderActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linBill:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyBillActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linFriends:
                        if (AppContext.getShareUserSessinid() != null) {
                            if (DemoHXSDKHelper.getInstance().isLogined()) {
                                startActivity(new Intent(getApplicationContext(), ChatFriendsActivity.class));
                            } else {
                                //环信账号未登陆
                                BaseApplication.toast("聊天账号为初始化");
                            }
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linPwd:
                        if (AppContext.getShareUserSessinid() != null) {
                            Intent intentPwd=new Intent(getApplicationContext(), PwdInfoActivity.class);
                            intentPwd.putExtra(Config.CHANGPWDPHONE,phone);
                            startActivity(intentPwd);
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linMyInfo:
//                        if (AppContext.getShareUserSessinid() != null) {
//                            startActivity(new Intent(getApplicationContext(), MyInformationActivity.class));
//                        } else {
//                            BaseApplication.toast(R.string.pleaseLogin);
//                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
//                        }
                        break;
                    case R.id.linTakePhone:
                        if (AppContext.getShareUserSessinid() == null) {
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        } else {
                            if (isbinding) {
                                Intent intent = new Intent(getApplicationContext(), OldPhoneActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(Config.OLDPHONE, phone);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), BindingPhoneActivity.class);
                                startActivity(intent);
                            }
                        }
                        break;
                    case R.id.linComment:
                        if (AppContext.getShareUserSessinid() != null) {
                            startActivity(new Intent(getApplicationContext(), MyCommentsActivity.class));
                        } else {
                            BaseApplication.toast(R.string.pleaseLogin);
                            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
                        }
                        break;
                    case R.id.linLoginOut:

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setMessage("确定退出登录？");
                        dialog.setPositiveButton("确定",
                                new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        gotoLogout();
                                    }
                                });
                        dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        } );
                        dialog.show();

                        break;
                }
            }
        };

        ivMePic.setOnClickListener(clickListener);
        ivCoupons.setOnClickListener(clickListener);
        ivSetting.setOnClickListener(clickListener);
        llColl.setOnClickListener(clickListener);
        ivBalance.setOnClickListener(clickListener);
        linOrder.setOnClickListener(clickListener);
        linBill.setOnClickListener(clickListener);
        linFriends.setOnClickListener(clickListener);
        linMyInfo.setOnClickListener(clickListener);
        linTakePhone.setOnClickListener(clickListener);
        linComment.setOnClickListener(clickListener);
        linLoginOut.setOnClickListener(clickListener);
        linPwd.setOnClickListener(clickListener);
    }

    private void get_user_head_image() {
        HttpClient.instance().get_user_head_image(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                LogUtil.log(responseBean.toString());
                try {
                    JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    if (!jsonObject.getString("result").equals("")) {
//                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), igUserHead);
//                        BaseApplication.toast(jsonObject.getString("result"));
                        //显示图片的配置.cacheInMemory(true)
//                        .cacheOnDisk(true)
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
//								.showImageOnLoading(R.drawable.default_image)
                                .showImageOnFail(R.drawable.pic_default_user)
                                .cacheInMemory(false)
                                .cacheOnDisk(false)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .build();
//                        BaseApplication.toast(jsonObject.getString("result") + "；；；" + jsonObject.getString("result"));
                        AppContext.setProperty(Config.MyImg, jsonObject.getString("result"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), ivMePic, options);
                    } else {
//                        BaseApplication.toast(jsonObject.getString("result") + "图片地址");
                    }
                } catch (Exception e) {
                    LogUtil.log(e.toString());
                }
            }


        });
    }


    private void initData() {//手机号码
        HttpClient.instance().judgeuserPhoneNo(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (!result.equals("false")) {
                        isbinding = true;
                        phone = result;
                        AppContext.setProperty(Config.ISBINDPHONE, "true");
                        ivBindPhoneNotify.setVisibility(View.GONE);
                        ivDot.setVisibility(View.GONE);
                    } else {
                        isbinding = false;
                        ivBindPhoneNotify.setVisibility(View.VISIBLE);
                        ivDot.setVisibility(View.VISIBLE);
                        AppContext.setProperty(Config.ISBINDPHONE, "false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void gotoLogout() {
        if (AppContext.getShareUserSessinid() == null) {
            //

            clearSharePassword();
            startActivity(new Intent(getApplicationContext(), LoginByNameActivity.class));
            return;
        }
        HttpClient.instance().login_out(AppContext.getShareUserName(), AppContext.getShareUserPwd(), new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在退出...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();

            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                if (responseBean.isFailure()) {
                    BaseApplication.toast(responseBean.toString());
                }
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("true")) {
                        clearSharePassword();
                        String cidIn="";
                        boolean isfalseguibyskit=true;
                        if (AppContext.getProperty(Config.GTCID)!=null){
                            cidIn=AppContext.getProperty(Config.GTCID);
                        }

                        if (AppContext.getProperty(Config.ISFALSEGUIBYSKIT)==null){
                            isfalseguibyskit=false;
                        }
                        AppContext.clearPropertyData();
                        //把个推Id重新存储起来
                        if ( ! cidIn.equals("")){
                            AppContext.setProperty(Config.GTCID,cidIn);
                        }
                        AppContext.setProperty(Config.ISFIRSTLOGIN, "true");
                        AppContext.setProperty(Config.ISFALSEGUI, "true");
                        //已经登录指引过了
                        if (isfalseguibyskit){
                            AppContext.setProperty(Config.ISFALSEGUIBYSKIT,"true");
                        }
                        mydbHelper = MySQLiteOpenHelper.getInstance(MainActivity.this);
                        mydbHelper.onUpgrade_ServiceTpye_DB(mydbHelper.getSQLiteDatabase());
                        //把选择的数据恢复
                        //开门密码,
                        AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(false));
                        AppContext.setProperty(Config.EMID, null);
                        AppContext.setProperty(Config.MYHUANXINPWD, null);
                        AppContext.setProperty(Config.USERPHONE, null);
                        AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
                        AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
                        AppContext.setProperty(Config.SHARE_USERPWD, null);
                        AppContext.setProperty(Config.SHARE_USER_Name,null);
                        //
                        NewDoorFragment.tvLock.setText("");
                        ivMePic.setImageResource(R.drawable.pic_default_user);
                        tvName.setText("登录/注册");
                        startActivity(new Intent(MainActivity.this, LoginByNameActivity.class));
                        EMChatManager.getInstance().logout();//此方法为同步方法
                        voip_LoginOut();
                    } else {
                        Log.e("paramss", "-------------->" + responseBean.toString());
                        AppContext.toast("退出失败,请重新退出");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void voip_LoginOut() {
        ECDevice.logout(new ECDevice.OnLogoutListener() {
            @Override
            public void onLogout() {
                // SDK 回调通知当前登出成功
                // 这里可以做一些（与云通讯IM相关的）应用资源的释放工作
                // 如（关闭数据库，释放界面资源和跳转等）
//                                AppContext.toast("退出成功");
                ECDevice.unInitial();
            }
        });

    }


    //定位
    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION22, WRITE_PERMISSION02);
            if (b){
                getLocation();
            }else {
                mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE02);
            }
            // 等待提示
            return;
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                            arg0.dismiss();
                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            } );
            dialog.show();
        }
    }


    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
       receiver= new LocationBroadcastReceiver();
        this.registerReceiver(receiver, filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            latitude = bundle.getDouble(Config.LATITUDE);//纬度
            longitude = bundle.getDouble(Config.LONGITUDE);//经度
            String address = bundle.getString(Config.ADDRESS);
            if (address!=null){
                AppContext.setProperty(Config.LOCATIONCITY,address);
                DecimalFormat dr = new DecimalFormat("#0.000000");
                AppContext.setProperty(Config.LONGITUDE, dr.format(longitude));
                AppContext.setProperty(Config.LATITUDE, dr.format(latitude));
            }
            unregisterReceiver(this);// 不需要时注销
        }
    }

    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);// 不需要时注销
        }catch (Exception e){

        }
    }
}
