package com.sht.smartlock.ui.activity.fragment;

import android.Manifest;
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
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.sht.smartlock.ui.activity.BusinessCentreActivity;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationByTest;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.ui.activity.MyLockGroupsActivity;
import com.sht.smartlock.ui.activity.MyselfChenkinActivity;
import com.sht.smartlock.ui.activity.OpenDoorRecordActivity;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.activity.mine.SetFreeActivity;
import com.sht.smartlock.ui.activity.SettingMorningCallActivity;
import com.sht.smartlock.ui.activity.myview.HomeButtonView;
import com.sht.smartlock.ui.activity.myview.MyPopupWindow;
import com.sht.smartlock.ui.entity.ChenkOutEntity;
import com.sht.smartlock.ui.entity.LeaveState;
import com.sht.smartlock.ui.entity.LockInfo;
import com.sht.smartlock.ui.entity.MyLockEntity;
import com.sht.smartlock.ui.entity.OpenDoorEntity;
import com.sht.smartlock.ui.entity.PayChickoutEntity;
import com.sht.smartlock.ui.entity.PowerEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class
        MainTabIndexFragment extends Fragment implements View.OnClickListener, HomeButtonView.HomeBtnOnClickListener {
    //	public static MyViewPager myViewPager;
//	private List<Fragment> mFragments = new ArrayList<Fragment>();
//	private FragmentPagerAdapter mAdapter;
//开门页面-----------------------------------------
    private TextView tv_Lock;
    private ImageView imageView_Go;
    //    private FrameLayout frlay;
    private ImageView iv_door;
    private ImageView iv_DoorCloseAndOpen;
    private LinearLayout lin_OpenDoor_recore;
    private TextView tv_DoorState;
    private LinearLayout lin_Evevator;
    private static ImageView ivCheckin;
    private ImageView ivCheckout;
//	private Spinner sp_myLock;
    //设置我的酒店的list 名字，
//	private List<String>  list_lock=new ArrayList<>();
//	private HomeButtonView home_OpenDoor;

    private MyPopupWindow myPopupWindow;


    public static int pos;
    public static ArrayAdapter<String> adapter;
    //我的酒店全部信息
    public static List<MyLockEntity> list = new ArrayList<>();
    private boolean isNoti = true;
    public static boolean noInHole = false;

    private int num = 0;

    public static LinearLayout lin_opendoor_fragment;
    //	private SharedPreferences share;
//    private SharedPreferences.Editor editor = null;
//	private Animation animation;
    private AnimationDrawable drawable;
    private int n = 0;
    private boolean flag = true;
    private SceneAnimation animation;
    int[] a = {R.drawable.opendoor23, R.drawable.open02, R.drawable.open04, R.drawable.opendoor06, R.drawable.opendoor08, R.drawable.opendoor10, R.drawable.opendoor12, R.drawable.opendoor14, R.drawable.opendoor16, R.drawable.opendoor18, R.drawable.opendoor20, R.drawable.opendoor22};

    //-------------------------------服务页面-------------------------------
    private LinearLayout lin_Chat;
    private LinearLayout line_ShopPing;
    private LinearLayout line_Ordering;
    private LinearLayout line_MomingCall;
    private LinearLayout line_NOT_Disturb;
    private LinearLayout line_ClearRoom;
    private LinearLayout Line_ChangeRoom_Pm;
    private LinearLayout line_Business_Centre;
    private LinearLayout line_Another;
    private HomeButtonView home_Group_Chat, home_ShopPing, home_Ordering, home_MomingCall, home_NOT_Disturb, home_ClearRoom, home_ChangeRoom_Pm, home_Business_Centre, home_Another;
    public static LinearLayout lin_service_fragment;

    private TextView tv_Door_tile;
    private Dialog dialog_power;
    private static int closepower = 101;

    // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
    NotificationManager manager;
    private static final int NOTIFICATION_FLAG = 1;

    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    iv_door.setImageResource(R.drawable.btn_opendoor);
                    tv_DoorState.setText(R.string.open_door_State);
                    tv_Door_tile.setText(R.string.open_door_Title);
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
    public static TextView tv_Mylock;
    private static ImageView iv_Evevator, iv_Electric;


    @Override
    public void onResume() {
        super.onResume();
        if (AppContext.getShareUserSessinid() == null) {
        } else {
            tv_Mylock.setText("");
            getMyLockInfo();

        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.main_tab_index, container, false);
        mPermissionHelper = new PermissionHelper(getActivity());
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);
        initView(messageLayout);
        //开门页面的监听
        setOnOpendoorClickListener();
        //服务页面的监听
        setServicerHomeButtonViewonClick();
        manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (AppContext.getProperty(Config.ISBACKGROUD) != null) {//断电提示
//            getDialog();
        }
        return messageLayout;
    }

    private void initView(View view) {
//		 myViewPager = (MyViewPager) view.findViewById(R.id.myViewPager);
//
//		OpenDoorFragment openfragment=new OpenDoorFragment();
//		LockServerFragment lockServerfragment=new LockServerFragment();
//		mFragments.add(openfragment);
//		mFragments.add(lockServerfragment);
//
//
//		mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
//			@Override
//			public int getCount() {
//				return mFragments.size();
//			}
//
//			@Override
//			public Fragment getItem(int arg0) {
//				return mFragments.get(arg0);
//			}
//		};
//		myViewPager.setAdapter(mAdapter);
        //开门页面
        initOpenDoorView(view);

        //服务页面
        initServiceView(view);
    }

    private void initOpenDoorView(View view) {
        lin_opendoor_fragment = (LinearLayout) view.findViewById(R.id.lin_opendoor_fragment);
        tv_Lock = (TextView) view.findViewById(R.id.tv_Lock);
        imageView_Go = (ImageView) view.findViewById(R.id.imageView_Go);
//		sp_myLock = (Spinner) view.findViewById(R.id.sp_myLock);
        //开门
//        frlay = (FrameLayout) view.findViewById(R.id.frlay);
        iv_door = (ImageView) view.findViewById(R.id.iv_door);
        iv_DoorCloseAndOpen = (ImageView) view.findViewById(R.id.iv_DoorCloseAndOpen);
        //开门记录
        lin_OpenDoor_recore = (LinearLayout) view.findViewById(R.id.lin_OpenDoor_recore);
        //门已关好
        tv_DoorState = (TextView) view.findViewById(R.id.tv_DoorState);
        //电梯
        lin_Evevator = (LinearLayout) view.findViewById(R.id.lin_Evevator);
        //自助chenkin
        ivCheckin = (ImageView) view.findViewById(R.id.ivCheckin);
        ivCheckout = (ImageView) view.findViewById(R.id.ivCheckout);

        tv_Door_tile = (TextView) view.findViewById(R.id.tv_Door_tile);
        //
//		home_OpenDoor = (HomeButtonView) view.findViewById(R.id.home_OpenDoor);

        iv_Evevator = (ImageView) view.findViewById(R.id.iv_Evevator);
        iv_Electric = (ImageView) view.findViewById(R.id.iv_Electric);


        if (AppContext.getProperty(Config.POWER) == null) {
            iv_Electric.setImageResource(R.drawable.common_electric_bg);
        } else {
            if (AppContext.getProperty(Config.POWER).equals("1")) {
                iv_Electric.setImageResource(R.drawable.common_powering);
            } else if (AppContext.getProperty(Config.POWER).equals("2")) {
                iv_Electric.setImageResource(R.drawable.common_electric_bg);
            }
        }


        tv_Mylock = (TextView) view.findViewById(R.id.tv_myLock);
        tv_Mylock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * 	选择当前酒店，
				*
				* */
                if (myPopupWindow == null || (!myPopupWindow.isShowing())) {
                    myPopupWindow = new MyPopupWindow(getActivity(), list, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            pos = (int) id;
                            tv_Mylock.setText(list.get(pos).getHotel_caption());
                            if (list.get(pos).getCheckin_time().equals("null")) {
                                noInHole = false;
                            } else {
                                noInHole = true;
                            }
                            if (noInHole) {
                                ivCheckin.setImageResource(R.drawable.btn_checked_in);
                            } else {
                                ivCheckin.setImageResource(R.drawable.mycheckin01);
                            }
                            myPopupWindow.dismiss();
                        }
                    });
                    // 加了下面这行，onItemClick才好用
                    myPopupWindow.setFocusable(true);
                    /*
                    * 	设置宽度等于上面控件的宽度
					* */
                    myPopupWindow.setWidth(v.getWidth());
                    myPopupWindow.showAsDropDown(v);
                } else {
                    myPopupWindow.dismiss();
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
            tv_Mylock.setText(list.get(pos).getHotel_caption());
            if (noInHole) {
                ivCheckin.setImageResource(R.drawable.btn_checked_in);
            } else {
                ivCheckin.setImageResource(R.drawable.mycheckin01);
            }
        } else {
            noInHole = false;
        }
    }

    private void setOnOpendoorClickListener() {
        iv_door.setOnClickListener(this);
        tv_Lock.setOnClickListener(this);
        imageView_Go.setOnClickListener(this);
        iv_DoorCloseAndOpen.setOnClickListener(this);
        lin_OpenDoor_recore.setOnClickListener(this);
//		lin_Evevator.setOnClickListener(this);
        ivCheckin.setOnClickListener(this);
        ivCheckout.setOnClickListener(this);
        iv_Evevator.setOnClickListener(this);
        iv_Electric.setOnClickListener(this);
    }

    public static void refreshMyLock() {
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//
//				if (AppContext.getShareUserSessinid()==null){
//				}else {
//					handler.sendEmptyMessage(10);
//				}
//				//sendEmptyMessage()方法等同于以下几句话。所以。如果只发送一个what，就可以使用sendEmptyMessage()。这样更简单。
//				//Message message = Message.obtain();
//				// Message message2 = handler.obtainMessage();
//				//message.what = 0;
//				//handler.sendMessage(message);
//			}
//		}, 1, 15000);
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
                        noInHole = false;
                        list.clear();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        iv_Electric.setImageResource(R.drawable.common_electric_bg);
                        if (noInHole) {
                            ivCheckin.setImageResource(R.drawable.btn_checked_in);
                        } else {
                            ivCheckin.setImageResource(R.drawable.mycheckin01);
                        }
                    } else {
                        list.clear();
                        List<MyLockEntity> list_s = responseBean.getListData(MyLockEntity.class);
                        list.addAll(list_s);
                        AppContext.toLog("mylock" + responseBean.toString() + list.size() + list.toString());
                        getMyLockData();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_Lock:
            case R.id.imageView_Go://选择酒店
                break;
//            case R.id.frlay:
            case R.id.iv_door:
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
                        getDialog_OpenDoor();
                    } else {
                        BaseApplication.toast(R.string.Toast04);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast05);
                }

                break;
            case R.id.lin_OpenDoor_recore://开门记录
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    intent.setClass(getActivity(), OpenDoorRecordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("room_id", list.get(pos).getRoom_id());
                    bundle.putString(Config.BOOK_ID, list.get(pos).getBook_id());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
//                startActivity(new Intent(getActivity(), OtherActivity.class));
//                openGPSSettings();
//                setChenkInfo("1");
//                startActivity(new Intent(getActivity(), CallPhoneActivity.class));
                break;
            case R.id.iv_Evevator://电梯
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (list.size() > 0) {
                    getDialog_UnlockLift();
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivCheckin://自助chenkin
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
                        Intent intent1 = new Intent();
                        intent1.setClass(getActivity(), MyselfChenkinActivity.class);
                        startActivity(intent1);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast07);
                }

                break;
            case R.id.iv_Electric://取电
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
            //自助checkout
            case R.id.ivCheckout:


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
//                        BaseApplication.toast(R.string.Toast06);
                        isGoToChinkOut();
                    } else {
//                        Intent intent1 = new Intent();
//                        intent1.setClass(getActivity(), MyselfChenkinActivity.class);
//                        startActivity(intent1);
                        //

                    }
                } else {
                    BaseApplication.toast(R.string.Toast07);
                }
                break;

        }
    }

    //获取用户订酒店的信息
    private void getMyLockInfo() {
        if (AppContext.getShareUserSessinid() == null) {
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
                        noInHole = false;
                        list.clear();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        iv_Electric.setImageResource(R.drawable.common_electric_bg);
                        if (noInHole) {
                            ivCheckin.setImageResource(R.drawable.btn_checked_in);
                        } else {
                            ivCheckin.setImageResource(R.drawable.mycheckin01);
                        }
                    } else {
                        list.clear();
                        List<MyLockEntity> list_s = responseBean.getListData(MyLockEntity.class);
                        list.addAll(list_s);
                        AppContext.toLog("mylock" + responseBean.toString() + list.size() + list.toString());
                        getMyLockData();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    //开门
    private void openDoor(String roomid, String unlock_password, String book_id) {

        HttpClient.instance().newUserUnlock(roomid, unlock_password, book_id,"1", new HttpCallBack() {

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                animation.stopAnimation();
                iv_DoorCloseAndOpen.setVisibility(View.GONE);
                getDialog_OpenDoorFailure("1", "网络错误");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog(responseBean.toString());
//				if (drawable.isRunning()){
//					drawable.stop();
//					iv_DoorCloseAndOpen.setBackgroundResource(R.drawable.open01);
//				}
                animation.stopAnimation();
                iv_DoorCloseAndOpen.setVisibility(View.GONE);
                try {
//                    //测试
//                    setServierByPower();
                    OpenDoorEntity data = responseBean.getData(OpenDoorEntity.class);
                    if (data.getCode().equals("1")) {
                        iv_door.setImageResource(R.drawable.btn_opendoor);
                        tv_DoorState.setText(R.string.open_door_State_open);
                        tv_Door_tile.setText(R.string.open_door_Title_open);
                        AppContext.setProperty(Config.POWER, "1");
                        if (data.getPower().equals("-1")) {
                            BaseApplication.toast("门已开,未配置取电开关");
                        } else if (data.getPower().equals("0")) {
                            BaseApplication.toast("门已开,硬件取电失败");
                        } else if (data.getPower().equals("1")) {
                            BaseApplication.toast("门已开,取电成功");
                            iv_Electric.setImageResource(R.drawable.common_powering);
                        } else {
                            AppContext.toast(data.getMsg());
                        }
                        //存储服务监听需要值 并开启广播和服务
                        if (AppContext.getProperty(Config.CHINCKIN_TRUE) == null && !data.getPower().equals("-1")) {
                            setServierByPower();
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

    //开门
    private void getDialog_OpenDoor() {
        final Dialog dialog_open = new Dialog(getActivity(), R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.dialog_opendoor01, null);
        dialog_open.setContentView(view);
        //我知道了
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        final EditText ed_pdw = (EditText) view.findViewById(R.id.ed_pdw);
        View dialog_Line = view.findViewById(R.id.dialog_Line);
        View lin_SureOrNo = view.findViewById(R.id.lin_SureOrNo);

        ed_pdw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				if (s.length()==4){
//					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//				}
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
                    animation = new SceneAnimation(iv_DoorCloseAndOpen, a, 1);
                    if (istrue != null) {
                        if (istrue.equals("true")) {
                            ed_pdw.setVisibility(View.VISIBLE);
                            tv_title.setText(R.string.dialog_opendoor_pdw);
                            n++;
                            if (n == 2) {
                                n = 0;
                                dialog_open.dismiss();
                                animation.playConstant(1);
                                openDoor(list.get(pos).getRoom_id(), ed_pdw.getText().toString().trim(), list.get(pos).getBook_id());
                                iv_DoorCloseAndOpen.setVisibility(View.VISIBLE);
                            }

                        } else {
                            dialog_open.dismiss();
                            animation.playConstant(1);
                            openDoor(list.get(pos).getRoom_id(), "", list.get(pos).getBook_id());
//						ObjectAnimator animator = ObjectAnimator.ofFloat(iv_DoorCloseAndOpen, "rotation", 0f, 360f);
//						animator.setDuration(3000);
//						animator.start();
                            iv_DoorCloseAndOpen.setVisibility(View.VISIBLE);
                        }
                    } else {
                        dialog_open.dismiss();
                        animation.playConstant(1);
                        openDoor(list.get(pos).getRoom_id(), "", list.get(pos).getBook_id());
//						ObjectAnimator animator = ObjectAnimator.ofFloat(iv_DoorCloseAndOpen, "rotation", 0f, 360f);
//						animator.setDuration(3000);
//						animator.start();
                        iv_DoorCloseAndOpen.setVisibility(View.VISIBLE);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast01);

                    dialog_open.dismiss();
                }
            }
        });
        dialog_open.show();

    }


    private void getDialog_OpenDoorFailure(final String phone, String errorMessage) {
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
//				tv_pdw.setVisibility(View.VISIBLE);
//				tv_sure.setVisibility(View.GONE);
//				tv_title.setVisibility(View.GONE);
//				tv_cancel.setVisibility(View.GONE);
//				dialog_Line.setVisibility(View.GONE);
//				lin_SureOrNo.setVisibility(View.GONE);
//				tv_pdw.setText(R.string.dialog_Unlock_lift);
//				if (list.size() > 0) {
//					service_Unlock_Lift(list.get(pos).getID(),iv_true,tv_pdw,dialog);
//
//				}
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
            public void onSuccess(ResponseBean responseBean) {
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
                        iv_Electric.setImageResource(R.drawable.common_powering);
                    } else if (data.getCode().equals("2")) {//关闭电源成功
                        AppContext.setProperty(Config.POWER, "2");
                        BaseApplication.toast(data.getMsg());
                        iv_Electric.setImageResource(R.drawable.common_electric_bg);
                    } else if (data.getCode().equals("3")) {//关闭电源成功
                        BaseApplication.toast("开关配置错误");
                        iv_Electric.setImageResource(R.drawable.common_electric_bg);
                    } else {
                        BaseApplication.toast(data.getMsg());
                        iv_Electric.setImageResource(R.drawable.common_electric_bg);
                    }
                } catch (Exception e) {

                }

            }
        });
    }


    @Override
    public void onClickDown(HomeButtonView homebtn) {
        switch (homebtn.getId()) {
            case R.id.home_OpenDoor:

//				iv_door.setImageResource(R.drawable.open_lock_door);
//				if (list.size()>0){
//					openDoor(list.get(pos).getRoom_id(),"");
//				}else {
//					BaseApplication.toast("暂无房间，请检查是否登录了");
//				}
                break;
        }
    }


    //-----------------------------------------------服务页面-----------------------------


    @Override
    public void onClickUp(HomeButtonView homebtn) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (homebtn.getId()) {
            case R.id.home_Group_Chat:
                intent.setClass(getActivity(), MyLockGroupsActivity.class);
                startActivity(intent);
                break;
            case R.id.home_ShopPing:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
//				noInHole=true;
                //判断是否入住
                if (noInHole) {
                    if (list.get(pos).getShopping().equals("0")) {
                        intent.setClass(getActivity(), OrderFoodActivity.class);
                        bundle.putString(Config.ORDER_TAB, 1 + "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂时不支持购物");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_Ordering:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                if (noInHole) {
                    if (list.get(pos).getDinner().equals("0")) {
                        intent.setClass(getActivity(), OrderFoodActivity.class);
                        bundle.putString(Config.ORDER_TAB, 2 + "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂时不支持订餐");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_MomingCall:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
//				noInHole=true;
                //判断是否入住
                if (noInHole) {
                    if (list.get(pos).getAwaken().equals("0")) {
                        intent.setClass(getActivity(), SettingMorningCallActivity.class);
                        bundle.putString(Config.SERVICE_TYPE, "3");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂无叫醒服务");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_NOT_Disturb:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (noInHole) {
//					BaseApplication.toast("免打扰");
                    intent.setClass(getActivity(), SetFreeActivity.class);
                    bundle.putString("statefree", "0");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_ClearRoom:
//				startActivity(new Intent(getActivity(), RoomCleaningActivity.class));
//				BaseApplication.toast("打扫房间");
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (noInHole) {
                    if (list.get(pos).getClear().equals("0")) {
                        intent.setClass(getActivity(), SettingMorningCallActivity.class);
                        bundle.putString(Config.SERVICE_TYPE, "1");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂无清扫房间服务");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_ChangeRoom_Pm:
//				startActivity(new Intent(getActivity(), ReplacementBeddingActivity.class));
//				BaseApplication.toast("更换床上用品");
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }

                //判断是否入住
                if (noInHole) {
                    if (list.get(pos).getReplace().equals("0")) {
                        intent.setClass(getActivity(), SettingMorningCallActivity.class);
                        bundle.putString(Config.SERVICE_TYPE, "2");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂无更换床上用品服务");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_Business_Centre:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (noInHole) {
                    startActivity(new Intent(getActivity(), BusinessCentreActivity.class));
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_Another:
//				startActivity(new Intent(getActivity(), OtherActivity.class));
//				BaseApplication.toast("其他");
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (noInHole) {
                    if (list.get(pos).getOther().equals("0")) {
                        intent.setClass(getActivity(), SettingMorningCallActivity.class);
                        bundle.putString(Config.SERVICE_TYPE, "4");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast("该酒店暂无其他服务");
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
        }

    }


    private void initServiceView(View view) {
        lin_service_fragment = (LinearLayout) view.findViewById(R.id.lin_service_fragment);

        lin_Chat = (LinearLayout) view.findViewById(R.id.lin_Chat);
        //购物
        line_ShopPing = (LinearLayout) view.findViewById(R.id.line_ShopPing);
        //订餐
        line_Ordering = (LinearLayout) view.findViewById(R.id.line_Ordering);
        line_MomingCall = (LinearLayout) view.findViewById(R.id.line_MomingCall);
        //设置免打扰
        line_NOT_Disturb = (LinearLayout) view.findViewById(R.id.line_NOT_Disturb);
        line_ClearRoom = (LinearLayout) view.findViewById(R.id.line_ClearRoom);
        Line_ChangeRoom_Pm = (LinearLayout) view.findViewById(R.id.Line_ChangeRoom_Pm);
        line_Business_Centre = (LinearLayout) view.findViewById(R.id.line_Business_Centre);
        //其他
        line_Another = (LinearLayout) view.findViewById(R.id.line_Another);


        //
        home_Group_Chat = (HomeButtonView) view.findViewById(R.id.home_Group_Chat);
        home_ShopPing = (HomeButtonView) view.findViewById(R.id.home_ShopPing);
        home_Ordering = (HomeButtonView) view.findViewById(R.id.home_Ordering);
        home_MomingCall = (HomeButtonView) view.findViewById(R.id.home_MomingCall);
        home_NOT_Disturb = (HomeButtonView) view.findViewById(R.id.home_NOT_Disturb);
        home_ClearRoom = (HomeButtonView) view.findViewById(R.id.home_ClearRoom);
        home_ChangeRoom_Pm = (HomeButtonView) view.findViewById(R.id.home_ChangeRoom_Pm);
        home_Business_Centre = (HomeButtonView) view.findViewById(R.id.home_Business_Centre);
        home_Another = (HomeButtonView) view.findViewById(R.id.home_Another);


    }

    private void setServicerHomeButtonViewonClick() {
        home_Group_Chat.setHomeBtbOnClickListener(this);
        home_ShopPing.setHomeBtbOnClickListener(this);
        home_Ordering.setHomeBtbOnClickListener(this);
        home_MomingCall.setHomeBtbOnClickListener(this);
        home_NOT_Disturb.setHomeBtbOnClickListener(this);
        home_ClearRoom.setHomeBtbOnClickListener(this);
        home_ChangeRoom_Pm.setHomeBtbOnClickListener(this);
        home_Business_Centre.setHomeBtbOnClickListener(this);
        home_Another.setHomeBtbOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b){
                getLocation();
            }else {
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
//        intent.setClass(getActivity(), LocationByPower.class);
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
//            if (AppContext.getProperty(Config.LEAVE_STATE02) != null && AppContext.getProperty(Config.LEAVE_STATE02).equals("1")) {//入住
//                if (Float.parseFloat(distance)>500){//入住状态离开500米
//                    isCanClosePower(bookid,roomid,distance, this);
//                }
//            } else {
//                if (Float.parseFloat(distance)<500){//离开状态回到500米
//                    isCanClosePower(bookid,roomid,distance, this);
//                }else {
//                    if (AppContext.getProperty(Config.LEAVE_STATE02) == null){//若是坐标误差第一次开门检测距离超过500米
//                        isCanClosePower(bookid,roomid,distance, this);
//                    }
//                }
//            }
//            BaseApplication.toast(distance + "米");
            Log.e("TAAG", "--------------------------" + distance + "bookid=" + bookid + "roomid=" + roomid);
//            getActivity().unregisterReceiver(this);// 不需要时注销
        }
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
                .setTicker("网客网:" + "您有新短消息，请注意查收！")// 设置在status
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

    private void setCheckout() {
        HttpClient.instance().chenkout(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), new HttpCallBack() {
            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ivCheckout.setEnabled(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ivCheckout.setEnabled(true);
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("TAG", "---------------->" + responseBean.toString());
                ivCheckout.setEnabled(true);
                ChenkOutEntity data = responseBean.getData(ChenkOutEntity.class);
                switch (data.getCode()) {
                    case "0":
                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            tv_Mylock.setText("");
                            getMyLockInfo();
                        }
                        break;
                    case "1":
                        AppContext.toast(data.getMsg());
//                        AppContext.toast(data.getMsg());
                        if (AppContext.getShareUserSessinid() == null) {
                        } else {
                            tv_Mylock.setText("");
                            getMyLockInfo();
                        }
                        break;
                    case "2":
                        isGoToChenkOut(list.get(pos).getRoom_id(), list.get(pos).getBook_id(), data.getFee(), data.getMsg());

                        break;
                    case "3":
//                        isGoToChenkOut(data.getMsg());
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
                            tv_Mylock.setText("");
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

    private void isGoToChinkOut() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("是否确定现在自动退房");
        dialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setCheckout();
                        ivCheckout.setEnabled(false);
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
                            tv_Mylock.setText("");
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
//                    Intent intent = new Intent(MyLockGroupsActivity.this,PermissionActivity.class);
//                    startActivity(intent);
//                    AppContext.toast("ok");
                    openGPSSettings();

                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开定位权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

}
