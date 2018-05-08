package com.sht.smartlock.ui.activity.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.helper.MySQLiteOpenHelper;
//import com.sht.smartlock.mqtt.PushService;
import com.sht.smartlock.mqtt.MQTTService;
import com.sht.smartlock.ui.activity.ChenkinByPhotoActivity;
import com.sht.smartlock.ui.activity.PayTestActivity;
import com.sht.smartlock.ui.activity.badgeview.BadgeUtils;
import com.sht.smartlock.ui.activity.badgeview.BadgeView;
import com.sht.smartlock.ui.activity.myview.MyGiftView;
import com.sht.smartlock.ui.adapter.DoorsAdapter;
import com.sht.smartlock.ui.entity.DoorsEntitys;
import com.sht.smartlock.ui.entity.RedPackgeEntity;
import com.sht.smartlock.util.LogUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;

import com.umeng.socialize.media.MailShareContent;

import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.helper.SceneAnimation;
import com.sht.smartlock.model.MyNetworkHelper;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.MyselfChenkinActivity;
import com.sht.smartlock.ui.activity.anim.RippleBackground;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationByTest;
import com.sht.smartlock.ui.activity.myview.MyDialog;
import com.sht.smartlock.ui.activity.myview.MyPopupWindow;
import com.sht.smartlock.ui.activity.myview.SharePopwindow;
import com.sht.smartlock.ui.adapter.SpinnerAdapter;
import com.sht.smartlock.ui.entity.ChenkOutEntity;
import com.sht.smartlock.ui.entity.LeaveState;
import com.sht.smartlock.ui.entity.LockInfo;
import com.sht.smartlock.ui.entity.MyLockEntity;
import com.sht.smartlock.ui.entity.OpenDoorEntity;
import com.sht.smartlock.ui.entity.PayChickoutEntity;
import com.sht.smartlock.ui.entity.PowerEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class NewDoorFragment extends Fragment implements View.OnClickListener {
    private ImageView ivOpenDoor;
//    private RippleBackground content;
    //    private static Spinner spLock;
    public static TextView tvLock;
    private TextView tvOpenLock;
    private ImageView ivElevator;
    private static ImageView ivPower;
    private static ImageView ivChinkIn;
    private ImageView ivChinkOut;
    private View reDoor;
    //    private static SpinnerAdapter adapter;
//    private List<String> mItems=new ArrayList<>();
    public static boolean noInHole = false;
    public static int pos;
    private int num = 0;
    private int n = 0;
    private Animation operatingAnim;
    //我的酒店全部信息
    public static List<MyLockEntity> list = new ArrayList<>();
    private Dialog dialog_power;
    private static int closepower = 101;
    private static ImageView ivRenPacket,ivBreakfast;

    // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
    NotificationManager manager;
    private static final int NOTIFICATION_FLAG = 1;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 12;
    private static final int WRITE_RESULT_CODE02 = 112;//刷脸
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String WRITE_PERMISSION03= Manifest.permission.CAMERA;
    private List<String> listPermission = new ArrayList<>();
    private MyPopupWindow myPopupWindow;
    private SharePopwindow sharePopwindow;
    View viewFragment;
    private static RedPackgeEntity redPackge;
    private static MySQLiteOpenHelper mydbHelper = null;

    private static List<DoorsEntitys> doors = new ArrayList<>();
    private  int doorNum=0;
    private MyGiftView gif1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    ivOpenDoor.setImageResource(R.drawable.opendoornew);
                    ivOpenDoor.setVisibility(View.VISIBLE);
                    tvOpenLock.setText(R.string.open_door_State);
                    break;
                case 10:
                    getMyLockInfo();
                    break;
                case 101://超出500米提示一分钟用户不处理就关闭电源
                    AppContext.setProperty(Config.ISBACKGROUD, null);
//                    if (AppContext.getProperty(Config.CHINCKIN_ROOMID) == null) {
//                        return;
//                    }
//                    if (AppContext.getProperty(Config.ISNOTCLOSEPOWER) != null && AppContext.getProperty(Config.ISNOTCLOSEPOWER).equals("true")) {
//                        AppContext.setProperty(Config.ISNOTCLOSEPOWER, null);
//                        return;
//                    }
                    LockInfo lockInfo = (LockInfo) msg.obj;
                    if (dialog_power != null && dialog_power.isShowing()) {
                        getPower(lockInfo.getRoomid(), lockInfo.getBook_id(), "2");
                        AppContext.setProperty(Config.CHINCKIN_ID, null);
                        AppContext.setProperty(Config.CHINCKIN_LAD, null);
                        AppContext.setProperty(Config.CHINCKIN_LON, null);
                        AppContext.setProperty(Config.CHINCKIN_ROOMID, null);
                        AppContext.setProperty(Config.CHINCKIN_HOTELNAME, null);
                        AppContext.setProperty(Config.CHINCKIN_BOOKID, null);
                        dialog_power.dismiss();
                    } else {
//                        if (AppContext.getProperty(Config.CHINCKIN_ROOMID) != null) {
                        getPower(lockInfo.getRoomid(), lockInfo.getBook_id(), "2");
                        AppContext.setProperty(Config.CHINCKIN_ID, null);
                        AppContext.setProperty(Config.CHINCKIN_LAD, null);
                        AppContext.setProperty(Config.CHINCKIN_LON, null);
                        AppContext.setProperty(Config.CHINCKIN_ROOMID, null);
                        AppContext.setProperty(Config.CHINCKIN_HOTELNAME, null);
                        AppContext.setProperty(Config.CHINCKIN_BOOKID, null);
//                        }
                    }
                    break;
            }
        }
    };
    private ListView lvDoors;
    private static BadgeView badgeView;
    private static RelativeLayout relOpenLock;
    private RelativeLayout relLogin;
    private ImageView ivWord01,ivWord02,ivWord03,ivWord07,ivWord04,ivWord05,ivWord06,ivWord10;
    private int numChick=0;

    @Override
    public void onStart() {
        super.onStart();
        Log.e("TAG", "-------------->onStart");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "-------------->onViewCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewFragment = LayoutInflater.from(getActivity()).inflate(R.layout.newdoorfragment, container, false);
        mPermissionHelper = new PermissionHelper(getActivity());
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);
        Log.e("TAG", "-------------->onCreateView");
        initView(viewFragment);
        setOnClickLener();
        return viewFragment;
    }

    private void initView(View view) {
//        content = (RippleBackground) view.findViewById(R.id.content);
        gif1 = (MyGiftView) view.findViewById(R.id.gif1);
        tvLock = (TextView) view.findViewById(R.id.tvLock);
        ivOpenDoor = (ImageView) view.findViewById(R.id.ivOpenDoor);
        tvOpenLock = (TextView) view.findViewById(R.id.tvOpenLock);
        ivElevator = (ImageView) view.findViewById(R.id.ivElevator);
        ivPower = (ImageView) view.findViewById(R.id.ivPower);
        ivChinkIn = (ImageView) view.findViewById(R.id.ivChinkIn);
        ivChinkOut = (ImageView) view.findViewById(R.id.ivChinkOut);
        reDoor = view.findViewById(R.id.reDoor);
        ivRenPacket = (ImageView) view.findViewById(R.id.ivRenPacket);
        ivBreakfast = (ImageView) view.findViewById(R.id.ivBreakfast);

        //指导
        relLogin = (RelativeLayout) view.findViewById(R.id.relLogin);
        relOpenLock = (RelativeLayout) view.findViewById(R.id.relOpenLock);

        ivWord01 = (ImageView) view.findViewById(R.id.ivWord01);
        ivWord02 = (ImageView) view.findViewById(R.id.ivWord02);
        ivWord03 = (ImageView) view.findViewById(R.id.ivWord03);
        ivWord07 = (ImageView) view.findViewById(R.id.ivWord07);
        ivWord04 = (ImageView) view.findViewById(R.id.ivWord04);
        ivWord05 = (ImageView) view.findViewById(R.id.ivWord05);
        ivWord06 = (ImageView) view.findViewById(R.id.ivWord06);
        ivWord10 = (ImageView) view.findViewById(R.id.ivWord10);


    }


    private void setOnClickLener() {
        ivOpenDoor.setOnClickListener(this);
        ivElevator.setOnClickListener(this);
        ivPower.setOnClickListener(this);
        ivChinkIn.setOnClickListener(this);
        ivChinkOut.setOnClickListener(this);
        tvLock.setOnClickListener(this);
        reDoor.setOnClickListener(this);
        ivRenPacket.setOnClickListener(this);
        ivBreakfast.setOnClickListener(this);

        ivWord03.setOnClickListener(this);
        ivWord06.setOnClickListener(this);
        ivWord02.setOnClickListener(this);
        relLogin.setOnClickListener(this);
        relOpenLock.setOnClickListener(this);

    }

    private void popGetLock(View view) {
        /*
                * 	选择当前酒店，
				*
				* */
        if (myPopupWindow == null || (!myPopupWindow.isShowing())) {
            myPopupWindow = new MyPopupWindow(getActivity(), list, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pos = (int) id;
                    tvLock.setText(list.get(pos).getHotel_caption());
                    if (list.get(pos).getCheckin_time().equals("null")) {
                        noInHole = false;
                    } else {
                        noInHole = true;
                    }
                    if (noInHole) {
                        ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                        ivChinkIn.setClickable(false);
                        if (list.get(pos).getBreakfast_remain()>0){
                            ivBreakfast.setVisibility(View.VISIBLE);
                            if (badgeView!=null){
                                badgeView.unbind();
                            }
                            badgeView= BadgeUtils.newRightBadgeView(AppContext.context(), ivBreakfast, list.get(pos).getBreakfast_remain());
                        }else {
                            if (badgeView!=null){
                                badgeView.unbind();
                            }
                            ivBreakfast.setVisibility(View.GONE);
                        }
                    } else {
                        if (badgeView!=null){
                            badgeView.unbind();
                        }
                        ivChinkIn.setImageResource(R.drawable.newcheckin);//未入住
                        ivChinkIn.setClickable(true);
                        ivBreakfast.setVisibility(View.GONE);
                    }

                    //判断该订单是否有多个门锁
                    if (!list.get(pos).getLock().equals("[]")) {
                        try {
                            JSONArray jsonArray = new JSONArray(list.get(pos).getLock());
                            doors.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                DoorsEntitys doorsEntitys = new DoorsEntitys();
                                doorsEntitys.setID(jsonArray.getJSONObject(i).getString("ID"));
                                doorsEntitys.setLock_name(jsonArray.getJSONObject(i).getString("lock_name"));
                                doors.add(doorsEntitys);
                            }
                            DoorsEntitys doorsEntitys1 = new DoorsEntitys();
                            doorsEntitys1.setID("-1");
                            doorsEntitys1.setLock_name(list.get(pos).getRoom_no() + "房门");
                            doors.add(doorsEntitys1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        doors.clear();
                    }






                    //是否展示红包分享按钮
                    if (list.get(pos).getBonus_id().equals("false")) {
                        //没有活动 ，不显示红包按钮
                        ivRenPacket.setVisibility(View.GONE);
                    } else {
                        ivRenPacket.setVisibility(View.VISIBLE);
                        getRedPackge(list.get(pos).getBonus_id());
                    }


                    myPopupWindow.dismiss();
                }
            });
            // 加了下面这行，onItemClick才好用
            myPopupWindow.setFocusable(true);
                    /*
                    * 	设置宽度等于上面控件的宽度
					* */
            myPopupWindow.setWidth(view.getWidth());
            myPopupWindow.showAsDropDown(view);
        } else {
            myPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tvLock:
                popGetLock(v);
                break;
            case R.id.ivOpenDoor:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    //开门
                    if (MyNetworkHelper.isNetworkAvailable(getActivity())) {//在有网络的情况下才执行
//                         DoorsEntitys doorsEntitys1 = new DoorsEntitys();
//                        doorsEntitys1.setID("-1");
//                        doorsEntitys1.setCaption(list.get(pos).getRoom_no() + "房门");
//                        doors.add(doorsEntitys1);
                        if (doors.size() > 1) {//出去房间锁
                            getDoorsDialog(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), list.get(pos).getNum() + "");
                        } else {
                            getDialog_OpenDoor();
                        }


                    } else {
                        BaseApplication.toast(R.string.Toast04);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast05);
                }
                break;
            case R.id.ivElevator:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    if (list.get(pos).getLift_config().equals("0")) {//配置电梯
                        getDialog_UnlockLift();
                    }else {// 1 未配置电梯锁
                        AppContext.toast("该酒店未配置电梯锁或解锁失败");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivPower:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    getDialog_Electric();
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivChinkIn:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    //判断是否入住
                    if (noInHole) {
                        BaseApplication.toast(R.string.Toast06);
                    } else {
                        ivChinkIn.setEnabled(false);
                        isNeedFace(list.get(pos).getID());
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }


                break;
            case R.id.ivChinkOut:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    //判断是否入住
                    if (noInHole) {
                        isGoToChinkOut();
                    } else {
                        AppContext.toast("您未入住，无房间可以退");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
//                startActivity(new Intent(getActivity(), PayTestActivity.class));

                break;
            case R.id.reDoor:
                MainActivity.idMenu.closeMenu();
                break;
            case R.id.ivRenPacket:
                getDialog();
//                cancelRedPackge(list.get(pos).getNum()+"", list.get(pos).getBook_id());
                break;
            case R.id.ivBreakfast:
//                BadgeUtils.newRightBadgeView(getActivity(), ivBreakfast, 2);
                showMyDialog(list.get(pos).getBreakfast_remain()+"",list.get(pos).getBreakfast());
                break;
            case R.id.ivWord03:
            case R.id.ivWord02:
                relLogin.setVisibility(View.GONE);
                MainActivity.relGuiTop.setVisibility(View.GONE);
                MainActivity.relGuiServicer.setVisibility(View.GONE);
                MainActivity.mTabBtnIndex.setClickable(true);
                MainActivity.mTabBtnMine.setClickable(true);
                AppContext.setProperty(Config.ISFALSEGUI, "true");
                break;
            case R.id.ivWord06:
                AppContext.setProperty(Config.ISFALSEGUIBYSKIT,"true");
                if (numChick==0){
                    numChick++;
                    ivWord05.setVisibility(View.VISIBLE);
                    ivWord07.setVisibility(View.GONE);
                    ivWord04.setImageResource(R.drawable.pic_word3);
                }else if (numChick==1){
                    numChick++;
                    ivWord05.setVisibility(View.GONE);
                    ivWord07.setVisibility(View.GONE);
                    ivWord04.setVisibility(View.GONE);
                    ivWord10.setVisibility(View.VISIBLE);
                    MainActivity.ivWord09.setVisibility(View.VISIBLE);
                }else if (numChick==2){
                    MainActivity.relGuiServicer.setVisibility(View.GONE);
                    MainActivity.relGuiTop.setVisibility(View.GONE);
                    relOpenLock.setVisibility(View.GONE);

                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "-------------->onResume");
        if (AppContext.getShareUserSessinid() == null) {
            noInHole = false;
            list.clear();
            tvLock.setText(getString(R.string.GoToOrderLock));
            ivBreakfast.setVisibility(View.GONE);
            if (badgeView!=null){
                badgeView.unbind();
            }
        } else {
            tvLock.setText("");
            getMyLockInfo();
        }
        if (noInHole) {
            ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
            ivChinkIn.setClickable(false);
        } else {
            ivChinkIn.setImageResource(R.drawable.newcheckin);//未入住
            ivChinkIn.setClickable(true);
        }
        LogUtil.logE(AppContext.getProperty(Config.ISFALSEGUI));
        if (AppContext.getProperty(Config.ISFALSEGUI)==null){
            MainActivity.relGuiTop.setVisibility(View.VISIBLE);
            MainActivity.relGuiServicer.setVisibility(View.VISIBLE);
            relLogin.setVisibility(View.VISIBLE);

        }

        if (AppContext.getProperty(Config.ISFALSEGUI)!=null&&AppContext.getProperty(Config.ISFALSEGUIBYSKIT)==null && AppContext.getShareUserSessinid()!=null){
            MainActivity.relGuiTop.setVisibility(View.VISIBLE);
            MainActivity.relGuiServicer.setVisibility(View.VISIBLE);
            MainActivity.ivWord08.setVisibility(View.VISIBLE);
            relOpenLock.setVisibility(View.VISIBLE);
        }

    }

    public static   View.OnClickListener onClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppContext.setProperty(Config.ISFALSEGUIBYSKIT,"true");
            MainActivity.relGuiTop.setVisibility(View.GONE);
            MainActivity.relGuiServicer.setVisibility(View.GONE);
            relOpenLock.setVisibility(View.GONE);
        }
    };

    private void animOpenDoor(ImageView view) {
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.opendoor);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        view.startAnimation(operatingAnim);
    }

    //获取用户订酒店的信息
    private void getMyLockInfo() {
        if (AppContext.getShareUserSessinid() == null) {
            tvLock.setText(getString(R.string.GoToOrderLock));
            return;
        }
        HttpClient.instance().get_room_checkin_user("", new HttpCallBack() {
            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                if (AppContext.getShareUserSessinid() == null) {
                } else {
                    if (num < 3) {//循环 5次，若是都失败 就结束加载
                        num++;
                        getMyLockInfo();
                    }
                }
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog("jiudian" + responseBean.toString());
                Log.e("jiudian", "----" + responseBean.toString());
                AppContext.setProperty(Config.MYLOCKORDER, responseBean.toString());
                num = 0;
                try {
                    JSONObject object = new JSONObject(responseBean.toString());
                    String result = object.getString("result");
                    if (result.equals("false")) {//没有入住
//                      BaseApplication.toast(false+"");
                        tvLock.setText("");
                        noInHole = false;
                        list.clear();
                        //没有订单 ，不显示红包按钮
                        ivRenPacket.setVisibility(View.GONE);
                        mydbHelper = MySQLiteOpenHelper.getInstance(getActivity());
                        mydbHelper.onUpgrade_ServiceTpye_DB(mydbHelper.getSQLiteDatabase());
                        ivPower.setImageResource(R.drawable.newpowering);//未取电状态
                        if (noInHole) {
                            ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                            ivChinkIn.setClickable(false);
                        } else {
                            ivChinkIn.setImageResource(R.drawable.newcheckin);//未入住
                            ivChinkIn.setClickable(true);
                        }
                        tvLock.setText(getString(R.string.GoToOrderLock));
                        if (badgeView!=null){
                            badgeView.unbind();
                        }
                    } else {
                        list.clear();
                        List<MyLockEntity> list_s = responseBean.getListData(MyLockEntity.class);
                        list.addAll(list_s);
                        AppContext.toLog("mylock" + responseBean.toString() + list.size() + list.toString());
                        if (pos < list.size()) {
                            tvLock.setText(list.get(pos).getHotel_caption());
                        } else {
                            tvLock.setText(list.get(0).getHotel_caption());
                        }
                        getMyLockData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tvLock.setText(getString(R.string.GoToOrderLock));
                }

            }
        });

    }

    //接收推送消息时，刷新酒店消息
    public static void refreshMyLock() {

        if (AppContext.getShareUserSessinid() == null) {
            return;
        }
        HttpClient.instance().get_room_checkin_user("", new HttpCallBack() {
            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                if (AppContext.getShareUserSessinid() == null) {
                } else {

                }
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog("jiudian" + responseBean.toString());

                try {
                    JSONObject object = new JSONObject(responseBean.toString());
                    String result = object.getString("result");
                    if (result.equals("false")) {//没有入住
//                      BaseApplication.toast(false+"");
                        tvLock.setText("");
                        noInHole = false;
                        list.clear();
                        //没有订单 ，不显示红包按钮

                        ivRenPacket.setVisibility(View.GONE);
                        ivPower.setImageResource(R.drawable.newpowering);//未取电状态
                        if (noInHole) {
                            ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                            ivChinkIn.setClickable(false);
                        } else {
                            ivChinkIn.setImageResource(R.drawable.newcheckin);//未入住
                            ivChinkIn.setClickable(true);
                        }
                        tvLock.setText("请预定房间");
                        if (badgeView!=null){
                            badgeView.unbind();
                        }
                    } else {
                        list.clear();
                        List<MyLockEntity> list_s = responseBean.getListData(MyLockEntity.class);
                        list.addAll(list_s);
                        AppContext.toLog("mylock" + responseBean.toString() + list.size() + list.toString());
                        tvLock.setText(list.get(pos).getHotel_caption());
                        getMyLockData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tvLock.setText("请预定房间");
                }
            }
        });
    }


    public static void getMyLockData() {
//		pos=0;
        if (list.size() > 0) {
            if (list.size() < pos || list.size() == pos) {//后台取消订单
                pos = 0;
            }
            if (list.get(pos).getCheckin_time().equals("null")) {
                noInHole = false;
            } else {
                noInHole = true;
            }
//            tvlock.setText(list.get(pos).getHotel_caption());
            tvLock.setText(list.get(pos).getHotel_caption());

            //是否展示红包分享按钮
            if (list.get(pos).getBonus_id().equals("false")) {
                //没有活动 ，不显示红包按钮
                ivRenPacket.setVisibility(View.GONE);
            } else {
                ivRenPacket.setVisibility(View.VISIBLE);
                getRedPackge(list.get(pos).getBonus_id());
            }
            //判断该订单是否有多个门锁
            if (!list.get(pos).getLock().equals("[]")) {
                try {
                    JSONArray jsonArray = new JSONArray(list.get(pos).getLock());
                    doors.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DoorsEntitys doorsEntitys = new DoorsEntitys();
                        doorsEntitys.setID(jsonArray.getJSONObject(i).getString("ID"));
                        doorsEntitys.setLock_name(jsonArray.getJSONObject(i).getString("lock_name"));
                        doors.add(doorsEntitys);
                    }
                        DoorsEntitys doorsEntitys1 = new DoorsEntitys();
                        doorsEntitys1.setID("-1");
                        doorsEntitys1.setLock_name(list.get(pos).getRoom_no() + "房门");
                        doors.add(doorsEntitys1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                doors.clear();
            }

            if (noInHole) {
                ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                ivChinkIn.setClickable(false);
                if (list.get(pos).getBreakfast_remain()>0){
                    ivBreakfast.setVisibility(View.VISIBLE);
                    if (badgeView!=null){
                        badgeView.unbind();
                    }
                   badgeView= BadgeUtils.newRightBadgeView(AppContext.context(), ivBreakfast, list.get(pos).getBreakfast_remain());
                }else {
                    if (badgeView!=null){
                        badgeView.unbind();
                    }
                    ivBreakfast.setVisibility(View.GONE);
                }
            } else {
                ivChinkIn.setImageResource(R.drawable.newcheckin);//未入住
                ivChinkIn.setClickable(true);
                ivBreakfast.setVisibility(View.GONE);
                if (badgeView!=null){
                    badgeView.unbind();
                }
            }
        } else {
            noInHole = false;
            ivBreakfast.setVisibility(View.GONE);
            if (badgeView!=null){
                badgeView.unbind();
            }
        }
    }


    //开门
    private void getDialog_OpenDoor() {
        final Dialog dialog_open = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.dialog_opendoor01, null);
        dialog_open.setContentView(view);
        //我知道了
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        final EditText ed_pdw = (EditText) view.findViewById(R.id.ed_pdw);
//        View dialog_Line = view.findViewById(R.id.dialog_Line);
//        View lin_SureOrNo = view.findViewById(R.id.lin_SureOrNo);

        ed_pdw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 4) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else if (s.length() > 4) {
                    BaseApplication.toast("请输入4位密码");
                }
            }
        });


        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_open.dismiss();
                n = 0;
            }
        });
        view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    //判断是否设置了开门密码
                    String istrue = AppContext.getProperty(Config.OPENDOOR_PWD_SAVE);
                    if (istrue != null) {
                        if (istrue.equals("true")) {
                            ed_pdw.setVisibility(View.VISIBLE);
                            tv_title.setText(R.string.dialog_opendoor_pdw);
                            n++;
                            if (n == 2) {
                                n = 0;
                                dialog_open.dismiss();
                                //设置动画
                                ivOpenDoor.setImageResource(R.drawable.opendoornew);
                                ivOpenDoor.setVisibility(View.GONE);
                                gif1.setVisibility(View.VISIBLE);
//                                animOpenDoor(ivOpenDoor);
//                                content.startRippleAnimation();
                                boolean paused = gif1.isPaused();
                                if (paused){
                                    gif1.setPaused(false);
                                }
                                gif1.setMovieResource(R.raw.img_opening2);
                                openDoor(list.get(pos).getRoom_id(), ed_pdw.getText().toString().trim(), list.get(pos).getBook_id(), list.get(pos).getNum() + "");

                            }
                        } else {
                            dialog_open.dismiss();
                            //设置动画
//                            ivOpenDoor.setImageResource(R.drawable.pic_opening2);
//                            animOpenDoor(ivOpenDoor);
//                            content.startRippleAnimation();
                            ivOpenDoor.setImageResource(R.drawable.opendoornew);
                            ivOpenDoor.setVisibility(View.GONE);
                            gif1.setVisibility(View.VISIBLE);
                            boolean paused = gif1.isPaused();
                            if (paused){
                                gif1.setPaused(false);
                            }
                            gif1.setMovieResource(R.raw.img_opening2);
                            openDoor(list.get(pos).getRoom_id(), "", list.get(pos).getBook_id(), list.get(pos).getNum() + "");
                        }
                    } else {
                        dialog_open.dismiss();
                        //设置动画
//                        ivOpenDoor.setImageResource(R.drawable.pic_opening2);
//                        animOpenDoor(ivOpenDoor);
//                        content.startRippleAnimation();
                        ivOpenDoor.setImageResource(R.drawable.opendoornew);
                        ivOpenDoor.setVisibility(View.GONE);
                        gif1.setVisibility(View.VISIBLE);
                        boolean paused = gif1.isPaused();
                        if (paused){
                            gif1.setPaused(false);
                        }
                        gif1.setMovieResource(R.raw.img_opening2);
                        openDoor(list.get(pos).getRoom_id(), "", list.get(pos).getBook_id(), list.get(pos).getNum() + "");

                    }
                } else {
                    BaseApplication.toast(R.string.Toast01);
                    dialog_open.dismiss();
                }
            }
        });
        dialog_open.show();

    }

    //开门
    private void openDoor(String roomid, String unlock_password, final String book_id, final String num) {

        HttpClient.instance().newUserUnlock(roomid, unlock_password, book_id, num, new HttpCallBack() {

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
//                ivOpenDoor.clearAnimation();
//                content.stopRippleAnimation();
                gif1.setPaused(true);
                gif1.setVisibility(View.GONE);
                ivOpenDoor.setImageResource(R.drawable.pic_opened);
                ivOpenDoor.setVisibility(View.VISIBLE);
                getDialog_OpenDoorFailure("1", "网络错误");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog(responseBean.toString());
//                ivOpenDoor.clearAnimation();
//                content.stopRippleAnimation();
                gif1.setPaused(true);
                gif1.setVisibility(View.GONE);
                try {
//                    //测试
//                    setServierByPower();
                    getMyLockInfo();
                    OpenDoorEntity data = responseBean.getData(OpenDoorEntity.class);
                    if (data.getCode().equals("1")) {

                        ivOpenDoor.setImageResource(R.drawable.pic_opened);
                        ivOpenDoor.setVisibility(View.VISIBLE);
                        tvOpenLock.setText(R.string.open_door_State_open);
//                        tv_Door_tile.setText(R.string.open_door_Title_open);
                        AppContext.setProperty(Config.POWER, "1");
                        if (data.getPower().equals("-1")) {
                            BaseApplication.toast("门已开,未配置取电开关");
                        } else if (data.getPower().equals("0")) {
                            BaseApplication.toast("门已开,硬件取电失败");
                        } else if (data.getPower().equals("1")) {
                            BaseApplication.toast("门已开,取电成功");
                            ivPower.setImageResource(R.drawable.newpower);//已取电状态
                            ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                        } else {
                            AppContext.toast(data.getMsg());
                        }
                        //存储服务监听需要值 并开启广播和服务
                        if (AppContext.getProperty(Config.CHINCKIN_TRUE) == null && !data.getPower().equals("-1")) {
                            setServierByPower();
                        }
                        //需要弹红包分享
                        if (list.get(pos).getAlert() == 1) {
                            getDialog();
                            cancelRedPackge(num, book_id);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(6000);
                                    //关门
                                    handler.sendEmptyMessage(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else if (data.getCode().equals("-1")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-2")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-3")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-4")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-5")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-6")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getDialog_OpenDoorFailure("1", "网络异常");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getDialog_OpenDoorFailure(final String phone, String errorMessage) {
        ivOpenDoor.setImageResource(R.drawable.opendoornew);
        ivOpenDoor.setVisibility(View.VISIBLE);
        final Dialog dialog = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.dialogopendoor_failure, null);
        dialog.setContentView(view);
        TextView tv_agin = (TextView) view.findViewById(R.id.tv_agin);
        TextView tv_ErrorMessage = (TextView) view.findViewById(R.id.tv_ErrorMessage);
        TextView tv_Contact = (TextView) view.findViewById(R.id.tv_Contact);//联系前台
        tv_agin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog_OpenDoor();
                dialog.dismiss();
            }
        });

        tv_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.toast(R.string.Toast02);
                //调用拨号面板：
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        tv_ErrorMessage.setText("原因：" + errorMessage);


        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void setServierByPower() {
        //存储服务监听需要值
        AppContext.setProperty(Config.CHINCKIN_ID, list.get(pos).getID() + Config.CHINCKIN_TRUE);
        AppContext.setProperty(Config.CHINCKIN_LAD, list.get(pos).getLatitude());
        AppContext.setProperty(Config.CHINCKIN_LON, list.get(pos).getLongitude());
        AppContext.setProperty(Config.CHINCKIN_ROOMID, list.get(pos).getRoom_id());
        AppContext.setProperty(Config.CHINCKIN_HOTELNAME, list.get(pos).getHotel_caption());
        AppContext.setProperty(Config.CHINCKIN_BOOKID, list.get(pos).getBook_id());//订单号
        if (AppContext.getProperty(Config.LEAVE_STATE02) != null && AppContext.getProperty(Config.LEAVE_STATE02).equals("1")) {
        } else {
            setChenkInfo(list.get(pos).getBook_id(), list.get(pos).getRoom_id(), "1", false);
        }
        //在店
        AppContext.setProperty(Config.CLOSEPOWER, null);
        //注册广播和开启服务
        openGPSSettings();
    }

    /*  state 离店状态
       *  isLeaver 是否离店
       *
       * */
    private void setChenkInfo(final String book_id, final String roomid, final String state, final boolean isLeaver) {
//        Log.e("TAG", "----------------->入住状态参数" + list.get(pos).getBook_id() + "::::+" + state);
        HttpClient.instance().book_leave_room(book_id, state, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    Log.e("TAG", "----------------->入住状态" + responseBean.toString());
                    LeaveState leaveState = responseBean.getData(LeaveState.class);
                    if (leaveState.getBooldata().equals("true")) {
                        AppContext.setProperty(Config.LEAVE_STATE02, state);
                        if (isLeaver) {//离开店500米开外
                            if (leaveState.getAuto_power().equals("1")) { //不自动断电//入住状态返回auto_power=null
                                //超过500米显示距离，请求是否可以关点
                                if (leaveState.getAll_leave().equals("false")) {//所以人已经离开店
                                    boolean background = isBackground(getActivity());
                                    if (background) {
                                        //后台
                                        AppContext.setProperty(Config.ISBACKGROUD, "true");
                                    } else {
                                        //前台
                                        AppContext.setProperty(Config.ISBACKGROUD, null);
                                        if (dialog_power != null && dialog_power.isShowing()) {
                                            dialog_power.dismiss();
                                            getDialog(book_id, roomid);
                                        } else {
                                            getDialog(book_id, roomid);
                                        }
                                        //客户离开店的状态
                                    }
                                    if (state.equals("0")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(60000);
                                                    Message msgMessage = Message.obtain();
                                                    LockInfo lockInfo = new LockInfo();
                                                    lockInfo.setBook_id(book_id);
                                                    lockInfo.setRoomid(roomid);
                                                    msgMessage.what = closepower;
                                                    msgMessage.obj = lockInfo;
//                                                handler.sendEmptyMessage(closepower);
                                                    handler.sendMessage(msgMessage);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }

                                } else {//还有人在房间不关电,或者回来

                                }
                            } else {
                                //自动断电,不用再处理
                            }
                        }
                    } else {//状态更新失败再次更新状态。
                        setChenkInfo(book_id, roomid, state, isLeaver);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    //判断是否入住了
    private void isCanClosePower(final String hotel_caption, final String bookid, final String roomid, final String m, final BroadcastReceiver receiver) {
        HttpClient.instance().is_room_chinckin_user_power(roomid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("1")) {//入住中
                        if (Float.parseFloat(m) > 500) {
                            //通知
                            Log.e("TAG", "----------->通知1");
                            setNotification(hotel_caption);
                            setChenkInfo(bookid, roomid, "0", true);//离开500米
                        } else {
                            setChenkInfo(bookid, roomid, "1", false);//500米内
                        }
                        //在店
                        AppContext.setProperty(Config.CLOSEPOWER, null);
                    } else {//已经离店
//                        AppContext.setProperty(Config.CHINCKIN_ID, null);
//                        AppContext.setProperty(Config.CHINCKIN_LAD, null);
//                        AppContext.setProperty(Config.CHINCKIN_LON, null);
//                        AppContext.setProperty(Config.CHINCKIN_ROOMID, null);
//                        AppContext.setProperty(Config.CHINCKIN_HOTELNAME, null);
//                        AppContext.setProperty(Config.CHINCKIN_BOOKID, null);
//                        AppContext.setProperty(Config.CLOSEPOWER, "true");
//                        getActivity().unregisterReceiver(receiver);// 不需要时注销

//                        AppContext.setProperty(Config.LEAVE_STATE, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /*
  *   提示关电Dialog
  *
  * */
    private void getDialog(final String bookid, final String roomid) {
        dialog_power = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.close_power, null);
        dialog_power.setContentView(view);
        //后台
        AppContext.setProperty(Config.ISBACKGROUD, null);
        //取消
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_power.isShowing()) {
                    dialog_power.dismiss();
                }
                AppContext.setProperty(Config.ISNOTCLOSEPOWER, "true");

            }
        });
        view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //后台

                if (dialog_power.isShowing()) {
                    dialog_power.dismiss();
                }
                if (roomid == null) {
                    return;
                }
                getPower(roomid, bookid, "2");
//                AppContext.setProperty(Config.CHINCKIN_ID, null);
//                AppContext.setProperty(Config.CHINCKIN_LAD, null);
//                AppContext.setProperty(Config.CHINCKIN_LON, null);
//                AppContext.setProperty(Config.CHINCKIN_ROOMID, null);
//                AppContext.setProperty(Config.CHINCKIN_HOTELNAME, null);
//                AppContext.setProperty(Config.CHINCKIN_BOOKID, null);
            }
        });
        dialog_power.show();
    }

    private void setNotification(String hotel_caption) {
        PendingIntent pendingIntent2 = PendingIntent.getActivity(getActivity(), 0,
                new Intent(getActivity(), MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify2 = new Notification.Builder(getActivity())
                .setSmallIcon(R.drawable.icon_small) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                        // icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setTicker("指尖宿:" + "您有新短消息，请注意查收！")// 设置在status
                        // bar上显示的提示文字
                .setContentTitle("出门通知")// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText("你已离开" + hotel_caption + "酒店，请注意关闭电源")// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        //设置默认声音
        notify2.defaults = Notification.DEFAULT_SOUND;
        manager.notify(NOTIFICATION_FLAG, notify2);

    }


    //取电
    private void getDialog_Electric() {
        final Dialog dialog = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.dialog_electric, null);
        dialog.setContentView(view);
        //我知道了
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        final TextView tv_pdw = (TextView) view.findViewById(R.id.tv_pdw);
        final TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        final TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final ImageView iv_true = (ImageView) view.findViewById(R.id.iv_true);
        final View dialog_Line = view.findViewById(R.id.dialog_Line);
        final View lin_SureOrNo = view.findViewById(R.id.lin_SureOrNo);
        if (AppContext.getProperty(Config.POWER) == null) {
            tv_title.setText(R.string.get_electric);
        } else {
            if (AppContext.getProperty(Config.POWER).equals("1")) {
                tv_title.setText(R.string.get_electric_02);
            } else if (AppContext.getProperty(Config.POWER).equals("2")) {
                tv_title.setText(R.string.get_electric);
            }
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (AppContext.getProperty(Config.POWER) == null) {
                    getPower(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), "1");
                } else {
                    if (AppContext.getProperty(Config.POWER).equals("1")) {
                        getPower(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), "2");
                    } else if (AppContext.getProperty(Config.POWER).equals("2")) {
                        getPower(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), "1");
                    }
                }
            }
        });
        dialog.show();
    }

    private void getPower(String room_id, String order_id, String operation) {
        HttpClient.instance().service_take_power(room_id, order_id, operation, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(getActivity(), "正在取电。。。");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                PowerEntity data = responseBean.getData(PowerEntity.class);
                try {
                    if (data.getCode().equals("-1")) {
                        BaseApplication.toast(data.getMsg());
                    } else if (data.getCode().equals("-2")) {
                        BaseApplication.toast(data.getMsg());
                    } else if (data.getCode().equals("-3")) {
                        BaseApplication.toast(data.getMsg());
                    } else if (data.getCode().equals("1")) {//取电成功
                        BaseApplication.toast(data.getMsg());
                        AppContext.setProperty(Config.POWER, "1");
                        ivPower.setImageResource(R.drawable.newpower);//已取电状态
                    } else if (data.getCode().equals("2")) {//关闭电源成功
                        AppContext.setProperty(Config.POWER, "2");
                        BaseApplication.toast(data.getMsg());
                        ivPower.setImageResource(R.drawable.newpowering);//取电状态
                    } else if (data.getCode().equals("3")) {//关闭电源成功
                        BaseApplication.toast("开关配置错误");
                        ivPower.setImageResource(R.drawable.newpowering);//取电状态
                    } else {
                        BaseApplication.toast(data.getMsg());
                        ivPower.setImageResource(R.drawable.newpowering);//取电状态
                    }
                } catch (Exception e) {

                }

            }
        });
    }


    //解锁电梯
    private void getDialog_UnlockLift() {
        final Dialog dialog = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.dialog_unlock_lift, null);
        dialog.setContentView(view);
        //我知道了
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        final TextView tv_pdw = (TextView) view.findViewById(R.id.tv_pdw);
        final TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        final TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final ImageView iv_true = (ImageView) view.findViewById(R.id.iv_true);
        final View dialog_Line = view.findViewById(R.id.dialog_Line);
        final View lin_SureOrNo = view.findViewById(R.id.lin_SureOrNo);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_pdw.setVisibility(View.VISIBLE);
                tv_sure.setVisibility(View.GONE);
                tv_title.setVisibility(View.GONE);
                tv_cancel.setVisibility(View.GONE);
                dialog_Line.setVisibility(View.GONE);
                lin_SureOrNo.setVisibility(View.GONE);
                tv_pdw.setText(R.string.dialog_Unlock_lift);
                if (list.size() > 0) {
                    service_Unlock_Lift(list.get(pos).getID(), iv_true, tv_pdw, dialog);
                }
            }
        });
        dialog.show();
    }

    //电梯
    private void service_Unlock_Lift(String hotel_id, final ImageView iv_true, final TextView tv_pdw, final Dialog dialog) {

        HttpClient.instance().service_Unlock_Lift(hotel_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
//                    boolean result = jsonObject.getBoolean("result");
                    String result = jsonObject.getString("result");
                    if (result.equals("true")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            iv_true.setVisibility(View.VISIBLE);
                                            tv_pdw.setText(R.string.dialog_Unlock_lift02);
                                            try {
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Thread.sleep(3000);
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }).start();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        BaseApplication.toast(R.string.Toast03);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BaseApplication.toast(R.string.Toast03);
                    dialog.dismiss();
                }

            }
        });
    }

    private void isGoToChinkOut() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("是否确定现在自动退房");
        dialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setCheckout();
                        ivChinkOut.setEnabled(false);
//
                    }
                });
        dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        dialog.show();
    }

    private void setCheckout() {
        HttpClient.instance().chenkout(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(getActivity(), "正在办理退费。。。");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                ivChinkOut.setEnabled(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Log.e("TAG", "---------------->" + responseBean.toString());
                ivChinkOut.setEnabled(true);
                ChenkOutEntity data = responseBean.getData(ChenkOutEntity.class);
                switch (data.getCode()) {
                    case "0":
                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            getMyLockInfo();
                        }
                        break;
                    case "1":
                        AppContext.toast(data.getMsg());
//                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            getMyLockInfo();
                        }
                        break;
                    case "2":
                        isGoToChenkOut(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), data.getFee(), data.getMsg());
                        break;
                    case "3":
                        isGoToChenkOut02(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), data.getAccount_id(), data.getPrice(), data.getMsg());
                        break;
                    case "-1":
                        AppContext.toast(data.getMsg());
                        break;
                    case "-2":
                        AppContext.toast(data.getMsg());
                        break;
                    case "-3":
                        AppContext.toast(data.getMsg());
                        break;
                    case "-4":
                        AppContext.toast(data.getMsg());

                        break;
                    default:
                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            getMyLockInfo();
                        }
                        break;
                }
            }
        });
    }

    //押金支付
    private void isGoToChenkOut(final String room_id, final String order_id, final String fee, String data) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(data);
        dialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // 转到手机设置界面，用户设置GPS
//                        depositPay(room_id,order_id,fee);
                        getToChenkin(list.get(pos).getRoom_id(), list.get(pos).getBook_id());
                        arg0.dismiss();
                    }
                });
        dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        dialog.show();
    }

    //余额支付
    private void isGoToChenkOut02(final String room_id, final String order_id, final String account_id, final String price, String data) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(data);
        dialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // 转到手机设置界面，用户设置GPS
//                        balancePay(room_id, order_id, account_id, price);
                        getToChenkin(list.get(pos).getRoom_id(), list.get(pos).getBook_id());
                        arg0.dismiss();
                    }
                });
        dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        dialog.show();
    }

    private void getToChenkin(String room_id, String order_id) {
        HttpClient.instance().getTo_checkout(room_id, order_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                PayChickoutEntity data = responseBean.getData(PayChickoutEntity.class);
                switch (data.getCode()) {
                    case "0":
                        AppContext.toast("房内消费未支付，请到前台办理");
                        break;
                    case "1":
                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            getMyLockInfo();
                        }
                        break;
                    case "-1":
                        AppContext.toast("操作异常，请到前台办理");
                        break;
                    case "-2":
                        AppContext.toast("该酒店未启用自助退房功能，请到前台办理");
                        break;
                    case "-3":
                        AppContext.toast("该酒店暂时无法自助退房，请到前台办理");
                        break;
                    case "-4":
                        AppContext.toast("余额不足，请到前台办理");
                        break;
                    default:
                        AppContext.toast(data.getMsg());
                        break;
                }
            }
        });
    }

    private void isGoToChinkIn() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("是否确定现在自助入住房间");
        dialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        service_Myself_Checkin(list.get(pos).getBook_id(), list.get(pos).getRoom_id());
//
                    }
                });
        dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        dialog.show();
    }

    //自助 订单号 ；房间id
    private void service_Myself_Checkin(String book_id, String room_id) {
        HttpClient.instance().service_Myself_Checkin(book_id, room_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(getActivity(), "正在办理入住。。。");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                AppContext.toLog("chenkin" + responseBean.toString());
                try {
                    JSONObject object = new JSONObject(responseBean.toString());
                    String result = object.getString("result");
                    //自助chenkin成功
                    if (result.equals("1")) {
                        BaseApplication.toast(R.string.Toast_Success_Chenkin);
                        getMyLockInfo();
                    } else if (result.equals("-1")) {
                        BaseApplication.toast(R.string.Chenkin_failure01);
                    } else if (result.equals("-2")) {
                        BaseApplication.toast(R.string.Chenkin_failure02);
                    } else if (result.equals("-3")) {
                        BaseApplication.toast(R.string.Chenkin_failure03);
                    } else if (result.equals("-4")) {
                        BaseApplication.toast(R.string.Chenkin_failure04);
                    } else if (result.equals("-5")) {
                        BaseApplication.toast(R.string.Chenkin_failure05);
                    } else if (result.equals("-6")) {
                        BaseApplication.toast(R.string.Chenkin_failure06);
                    } else if (result.equals("-7")) {
                        BaseApplication.toast(R.string.Chenkin_failure07);
                    } else if (result.equals("-8")) {
                        BaseApplication.toast(R.string.Chenkin_failure08);
                    } else if (result.equals("2")) {
                        BaseApplication.toast("请到前台验证身份，办理入住");
                    } else {
                        BaseApplication.toast("自助chinkin失败,请到前台办理");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //---------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b) {
                getLocation();
            } else {
                mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
            }
            // 等待提示
            return;
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
            });
            dialog.show();
        }
    }

    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION_BYPOWER);
        getActivity().registerReceiver(new LocationBroadcastReceiver(), filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(getActivity(), LocationByTest.class);
        getActivity().startService(intent);

    }



    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION_BYPOWER)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            double latitude = bundle.getDouble(Config.LATITUDE);//纬度
            double longitude = bundle.getDouble(Config.LONGITUDE);//经度
            String isclose = bundle.getString(Config.ISCLOSE);
            //没有订单了 ，关闭广播
            if (isclose != null && isclose.equals("true")) {
                getActivity().unregisterReceiver(this);// 不需要时注销
                return;
            }
            String distance = bundle.getString(Config.DISTANCE);
            String bookid = bundle.getString(Config.MYLOCKBOOKID);
            String roomid = bundle.getString(Config.MYLOCKBOOKID_ROOMID);
            String hotel_caption = bundle.getString(Config.MYHOTEL_CAPTION);
////            latitude=22.612130;
////            longitude=114.022295;
//            AppContext.toast(distance+"米");
            if (roomid == null || bookid == null || roomid.equals("null") || bookid.equals("null")) {
                return;
            }
            isCanClosePower(hotel_caption, bookid, roomid, distance, this);
            Log.e("TAAG", "--------------------------" + distance + "bookid=" + bookid + "roomid=" + roomid);

        }
    }


    /*
    *   分享红包Dialog
    *
    * */
    private void getDialog() {

        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);//,
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        final View view = mInflater.inflate(R.layout.redpackagedialog, null);
        dialog.setContentView(view);
        //关闭
        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ivShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
//                mController.openShare(getActivity(), false);
                sharePopWindow(view, dialog);
            }
        });
        dialog.show();

    }

    private void sharePopWindow(View view, final Dialog dialog) {
        if (sharePopwindow == null || !sharePopwindow.isShowing()) {
            sharePopwindow = new SharePopwindow(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePopwindow.dismiss();
                    switch (v.getId()) {
                        case R.id.ivWinXin:
                            toShare(SHARE_MEDIA.WEIXIN);
//                            UmengTool.checkWx(getActivity());
                            break;
                        case R.id.ivWinXinFriend:
                            toShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                            break;
                        case R.id.ivQQ:
                            toShare(SHARE_MEDIA.QQ);
                            break;
                        case R.id.ivQQSpace:
                            toShare(SHARE_MEDIA.QZONE);
                            break;

                    }
                }
            });
            // 加了下面这行，onItemClick才好用
            sharePopwindow.setFocusable(true);
            sharePopwindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else {
            sharePopwindow.dismiss();
        }
        sharePopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();
            }
        });
    }

    private void toShare(final SHARE_MEDIA qqtype) {

        HttpClient.instance().sendRedPackge(list.get(pos).getBonus_id(), list.get(pos).getNum() + "", list.get(pos).getBook_id(), new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(getActivity(), "正在领取红包...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("服务器错误，红包获取失败");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                if (responseBean.getData().equals("true")) {
                    UMImage image = new UMImage(getActivity(), R.drawable.sharered_packge);
                    if (redPackge != null) {
                        new ShareAction(getActivity()).setPlatform(qqtype).withText(redPackge.getContent())
                                .withTitle(redPackge.getCaption())
                                .withTargetUrl(redPackge.getContent_url())
                                .withMedia(image)
                                .setCallback(umShareListener).share();
                    } else {
                        AppContext.toast("红包领取失败，请稍后再试");
                    }
                } else {
                    AppContext.toast("红包获取失败，请稍后再试");
                }
            }
        });
    }

    private void cancelRedPackge(String num, String book_id) {
        HttpClient.instance().cancelWindowRedPackge(num, book_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                if (responseBean.getData().equals("true")) {
                    //成功
                } else {

                }
            }
        });
    }


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(getActivity(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    private static void getRedPackge(String bonus_id) {
        HttpClient.instance().activeDetail(bonus_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                redPackge = responseBean.getData(RedPackgeEntity.class);
            }
        });
    }

    /*
        *   开门多锁Dialog
        *
        * */
    private void getDoorsDialog(final String room_id,  final String order_id, final String num) {
        final Dialog dialog = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.doors_dialog, null);
        dialog.setContentView(view);

        lvDoors = (ListView) view.findViewById(R.id.lvDoors);
        final EditText pwd = (EditText) view.findViewById(R.id.etPwd);
        final TextView tvSure = (TextView) view.findViewById(R.id.tvSure);
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        DoorsAdapter doorsAdapter = new DoorsAdapter(getActivity(), doors);
        lvDoors.setAdapter(doorsAdapter);

        lvDoors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否设置了开门密码
                String istrue = AppContext.getProperty(Config.OPENDOOR_PWD_SAVE);
                if (istrue != null) {
                    if (istrue.equals("true")) {
//                        initOpenDoors(room_id, doors.get((int) id).getID(), unlock_password, order_id, num);
                        lvDoors.setVisibility(View.GONE);
                        pwd.setVisibility(View.VISIBLE);
                        tvSure.setVisibility(View.VISIBLE);
                        tvTitle.setText("请输入开门密码");
                        doorNum = (int) id;
                    } else {
                        dialog.dismiss();
                        //设置动画
//                        ivOpenDoor.setImageResource(R.drawable.pic_opening2);
//                        animOpenDoor(ivOpenDoor);
//                        content.startRippleAnimation();
                        ivOpenDoor.setImageResource(R.drawable.opendoornew);
                        ivOpenDoor.setVisibility(View.GONE);
                        gif1.setVisibility(View.VISIBLE);
                        boolean paused = gif1.isPaused();
                        if (paused){
                            gif1.setPaused(false);
                        }
                        gif1.setMovieResource(R.raw.img_opening2);
                        initOpenDoors(room_id, doors.get((int) id).getID(), "", order_id, num);
                    }
                } else {
                    dialog.dismiss();
                    //设置动画
//                    ivOpenDoor.setImageResource(R.drawable.pic_opening2);
//                    animOpenDoor(ivOpenDoor);
//                    content.startRippleAnimation();
                    ivOpenDoor.setImageResource(R.drawable.opendoornew);
                    ivOpenDoor.setVisibility(View.GONE);
                    gif1.setVisibility(View.VISIBLE);
                    boolean paused = gif1.isPaused();
                    if (paused){
                        gif1.setPaused(false);
                    }
                    gif1.setMovieResource(R.raw.img_opening2);
                    initOpenDoors(room_id, doors.get((int) id).getID(), "", order_id, num);
                }
            }
        });
        //取消
        view.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //设置动画
//                ivOpenDoor.setImageResource(R.drawable.pic_opening2);
//                animOpenDoor(ivOpenDoor);
//                content.startRippleAnimation();
                ivOpenDoor.setImageResource(R.drawable.opendoornew);
                ivOpenDoor.setVisibility(View.GONE);
                boolean paused = gif1.isPaused();
                gif1.setVisibility(View.VISIBLE);
                if (paused){
                    gif1.setPaused(false);
                }
                gif1.setMovieResource(R.raw.img_opening2);
                initOpenDoors(room_id, doors.get(doorNum).getID(), pwd.getText().toString(), order_id, num);
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else if (s.length() > 4) {
                    BaseApplication.toast("请输入4位密码");
                }
            }
        });
        dialog.show();
    }

    //多房间开门
    private void initOpenDoors(String room_id, String lock_id, String unlock_password, final String order_id, final String num) {
        HttpClient.instance().openDoorss(room_id, lock_id, unlock_password, order_id, num, new HttpCallBack() {

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
//                ivOpenDoor.clearAnimation();
//                content.stopRippleAnimation();
                gif1.setPaused(true);
                gif1.setVisibility(View.GONE);
                ivOpenDoor.setImageResource(R.drawable.opendoornew);
                ivOpenDoor.setVisibility(View.VISIBLE);
                getDialog_OpenDoorFailure("1", "网络错误");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
//                ivOpenDoor.clearAnimation();
//                content.stopRippleAnimation();
                gif1.setPaused(true);
                gif1.setVisibility(View.GONE);

                try {
//                    //测试
//                    setServierByPower();
                    getMyLockInfo();
                    OpenDoorEntity data = responseBean.getData(OpenDoorEntity.class);
                    if (data.getCode().equals("1")) {

                        ivOpenDoor.setImageResource(R.drawable.opendoornew);
                        ivOpenDoor.setVisibility(View.VISIBLE);
                        tvOpenLock.setText(R.string.open_door_State_open);
//                        tv_Door_tile.setText(R.string.open_door_Title_open);
                        AppContext.setProperty(Config.POWER, "1");
                        if (data.getPower().equals("-1")) {
                            BaseApplication.toast("门已开,未配置取电开关");
                        } else if (data.getPower().equals("0")) {
                            BaseApplication.toast("门已开,硬件取电失败");
                        } else if (data.getPower().equals("1")) {
                            BaseApplication.toast("门已开,取电成功");
                            ivPower.setImageResource(R.drawable.newpower);//已取电状态
                            ivChinkIn.setImageResource(R.drawable.icon_sleep);//已入住
                        } else {
                            AppContext.toast(data.getMsg());
                        }
                        //存储服务监听需要值 并开启广播和服务
                        if (AppContext.getProperty(Config.CHINCKIN_TRUE) == null && !data.getPower().equals("-1")) {
                            setServierByPower();
                        }
                        //需要弹红包分享
                        if (list.get(pos).getAlert() == 1) {
                            getDialog();
                            cancelRedPackge(num, order_id);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(6000);
                                    //关门
                                    handler.sendEmptyMessage(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else if (data.getCode().equals("-1")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-2")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-3")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-4")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-5")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else if (data.getCode().equals("-6")) {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    } else {
                        getDialog_OpenDoorFailure(data.getReturn_value(), data.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getDialog_OpenDoorFailure("1", "网络异常");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        AppContext.toast("去拍照requestCode=" + requestCode);//在Activity获取权限返回值，因为用的是ActivityCompat.requestPermissions, 而不是Fragment的requestPermissions方法
        switch(requestCode){
            case WRITE_RESULT_CODE02:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    Intent intent1=new Intent(getActivity(), ChenkinByPhotoActivity.class);
                    intent1.putExtra("book_id",list.get(pos).getBook_id());
                    intent1.putExtra("room_id",list.get(pos).getRoom_id());
                    startActivity(intent1);
                } else {
                    //如果请求失败
                    AppContext.toast("请手动相机权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    getLocation();
                } else {
                    //如果请求失败
                    AppContext.toast("请手动定位权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }


    //判断是否需要刷脸自助入住
    private void isNeedFace(String hotel_id){
        HttpClient.instance().checkinIsNeedFace(hotel_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(getActivity(), "正在加载...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ivChinkIn.setEnabled(true);
                ProgressDialog.disMiss();
                AppContext.toast("网络错误");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                try {
                    ivChinkIn.setEnabled(true);
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("0")) {//需要
                        boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION03);
                        if (b) {
//                            AppContext.toast("不用权限");
                            Intent intent1 = new Intent(getActivity(), ChenkinByPhotoActivity.class);
                            intent1.putExtra("book_id", list.get(pos).getBook_id());
                            intent1.putExtra("room_id", list.get(pos).getRoom_id());
                            startActivity(intent1);
                        } else {
                            List<String> listPermission01 = new ArrayList<>();
                            listPermission01.add(WRITE_PERMISSION03);
                            mPermissionHelper.permissionsCheck(listPermission01, WRITE_RESULT_CODE02);
                        }
                    } else if (result.equals("1")) {
                        isGoToChinkIn();
                    } else {
                        AppContext.toast(result.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showMyDialog(String title,String message){
        final Dialog dialog = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.bf_dialog, null);
        dialog.setContentView(view);
        //
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        tvTitle.setText("您目前还有"+title+"张早餐券可用");
        tvMessage.setText(message);

        view.findViewById(R.id.tvKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}
