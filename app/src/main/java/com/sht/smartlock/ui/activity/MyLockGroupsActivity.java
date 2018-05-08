package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.helper.PwdGenHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.activity.myview.MyRefresh;
import com.sht.smartlock.ui.adapter.ExpandableListViewAdapter;
import com.sht.smartlock.ui.adapter.MyLockChat_Adapter;
import com.sht.smartlock.ui.adapter.MyLockG_Adapter;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.ui.entity.GroupEntity;
import com.sht.smartlock.ui.entity.HuanXinUserEntity;
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyLockGroupsActivity extends BaseActivity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener {


    private ImageView imgBack;
    private ImageView img_Search;

    private final int pagesize = 10;
    private LinearLayout line_MyLock;
    private String useName;
    private String pwd;
    private PullToRefreshExpandableListView pull_Expandable_lv;
    private ExpandableListView expandableListView;
    private ExpandableListViewAdapter expandableListViewAdapter;
    private List<GroupEntity> group = new ArrayList<>(); // 组列表
    private List<List<LockGroupsChatEntity>> child = new ArrayList<>(); // 子列表
    //    private double latitude;
//    private double longitude;
    private double latitude;
    private double longitude;
    private boolean ispullrefresh = true;
    private int pageid = 1;
    private boolean isResume=false;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_lock_groups);
        //登录环信
        makeHuanXinLogin();
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);

        initView();
        clickGoBack();
//        openGPSSettings();
//        LoadAndShowData01();
//        BaseApplication.toast(locationInfo + latitude + ":" + longitude);
//        latitude=104.031601;
//        longitude=30.63578;//经度
//        latitude = 22.612130;
//        longitude = 114.022295;
//        initData(latitude, longitude);
//        LoadAndShowData01();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_lock_groups;
    }

    protected boolean hasToolBar() {
        return false;
    }


    /*
    *   登录环 信
    *
    * */
    private void makeHuanXinLogin() {

        if (AppContext.getProperty(Config.EMID) != null) {
            useName = AppContext.getProperty(Config.EMID);
        }
        if (AppContext.getProperty(Config.MYHUANXINPWD)!=null){
            pwd=AppContext.getProperty(Config.MYHUANXINPWD);
        }else {
            if (AppContext.getProperty(Config.SHARE_USERPWD) != null) {
                pwd = AppContext.getProperty(Config.SHARE_USERPWD);
                AppContext.toLog("pdw" + pwd);
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
                    if (AppContext.getProperty(Config.MYHUANXINPWD)!=null&&!AppContext.getProperty(Config.MYHUANXINPWD).equals("null")) {
                        loginHuanXin(useName, pwd);
                    }else {//只有环信账号没有密码
                        String b = String.valueOf((int) (Math.random() * 100));//
                        useName = useName + b;
                        AppContext.toLog(useName);
                        registerHuanXin();
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
                    CreateHuanXin();
                }
            } else {//账号不为空时再判断是否相等
                if (AppContext.getProperty(Config.MYHUANXINPWD)!=null&& ! AppContext.getProperty(Config.MYHUANXINPWD).equals("null")) {
                    loginHuanXin(useName, pwd);
                }else {//只有环信账号没有密码
                    String b = String.valueOf((int) (Math.random() * 100));//
                    useName = useName + b;
                    AppContext.toLog(useName);
                    registerHuanXin();
                }
            }
        }
    }

    private void CreateHuanXin() {

        if (AppContext.getProperty(Config.HUANXINUSER) != null) {//登录原本的游客账号
            loginHuanXin(AppContext.getProperty(Config.HUANXINUSER), AppContext.getProperty(Config.HUANXINPWD));
        } else {//创建新的账号
            HttpClient.instance().huanxin_CreateUser(new HttpCallBack() {

                @Override
                public void onFailure(String error, String message) {
                    super.onFailure(error, message);
                    System.out.println("失败" + error + message);
                    // 如果用户名密码都有，不需要再次登录
                    if (!DemoHXSDKHelper.getInstance().isLogined()) {
                        Log.i("TAG", DemoHXSDKHelper.getInstance().isLogined() + "");
                        BaseApplication.toast("聊天账号 创建失败");
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
                        JSONObject jsonObject = new JSONObject(responseBean.toString());
                        String result = jsonObject.getString("result");
                        if (result.equals("null")) {
                            BaseApplication.toast("聊天账号初始化失败" + result);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final HuanXinUserEntity user = responseBean.getData(HuanXinUserEntity.class);
                    Log.i("TAG", "---------" + user.toString());

                    //用户名 和密码相同
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
            });
        }
    }


    private void initView() {
        //上面数据已废除
        imgBack = (ImageView) findViewById(R.id.img_back);
        img_Search = (ImageView) findViewById(R.id.img_Search);
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);
        //
        pull_Expandable_lv = (PullToRefreshExpandableListView) findViewById(R.id.pull_Expandable_lv);
        expandableListView = pull_Expandable_lv.getRefreshableView();
        pull_Expandable_lv.setMode(PullToRefreshBase.Mode.BOTH);
        pull_Expandable_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                ispullrefresh = true;
                openGPSSettings();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                ispullrefresh = false;
                pageid++;
                initData(latitude, longitude, pageid);

            }
        });


    }

    private void setExpandableListViewAdapter(int n) {
        expandableListViewAdapter = new ExpandableListViewAdapter(group, child, getApplicationContext());
        expandableListView.setAdapter(expandableListViewAdapter);
        expandableListView.setGroupIndicator(null);// 去掉默认的指示箭头
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);
        for (int i = 0; i < child.size(); i++) {
            expandableListView.expandGroup(i);
        }
        expandableListViewAdapter.notifyDataSetChanged();


    }


    private void clickGoBack() {
        //返回键
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(MyLockGroupsActivity.class);
            }
        });
        //搜索键
        img_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LockSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData(double latitude, double longitude, int pageid) {
        HttpClient.instance().near_hotelchatroom(longitude + "", latitude + "", pageid, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                if (isResume) {
                } else {
                    ProgressDialog.show(MyLockGroupsActivity.this, "正在加载数据");
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                AppContext.toLog(responseBean.toString());
                try {
                    List<LockGroupsChatEntity> lists = responseBean.getListData(LockGroupsChatEntity.class);
                    boolean isMy = true;
                    List<LockGroupsChatEntity> list1 = new ArrayList<LockGroupsChatEntity>();
                    List<LockGroupsChatEntity> list2 = new ArrayList<LockGroupsChatEntity>();
                    if (ispullrefresh) {//刷新或者是第一次加载时才清数据，加载数据不清数据
                        group.clear();
                        child.clear();
                        for (int i = 0; i < lists.size(); i++) {
                            String id = lists.get(i).getID();//判断是属于我的或者是周边的-1 以下-2以上为我的 -2以下为周边
                            if (id.equals("-1")) {//分割用于判断是我的或者是周边的。
                                isMy = true;
                                GroupEntity entity = new GroupEntity();
                                entity.setName("我的");
                                group.add(entity);
                            } else if (id.equals("-2")) {
                                isMy = false;
                                GroupEntity entity = new GroupEntity();
                                entity.setName("周边酒店");
                                group.add(entity);
                            }
                            if (isMy) {
                                list1.add(lists.get(i));
                            } else {
                                list2.add(lists.get(i));
                            }
                        }
                        //先移除分割数据
                        list1.remove(0);
                        list2.remove(0);
                        if (list1.size() > 0) {
                            child.add(list1);
                        } else {
                            group.remove(0);
                            //如果没有数据就加载所以聊天室用于测试
                        }
                        child.add(list2);
                    } else {//还未分页，需要清空数据，
                        ispullrefresh = true;
                        for (int i = 0; i < lists.size(); i++) {
                            String id = lists.get(i).getID();//判断是属于我的或者是周边的-1 以下-2以上为我的 -2以下为周边
                            if (id.equals("-1")) {//分割用于判断是我的或者是周边的。
                                isMy = true;
                            } else if (id.equals("-2")) {
                                isMy = false;
                            }
                            if (isMy) {
//                            list1.add(lists.get(i));
                            } else {
                                list2.add(lists.get(i));
                            }
                        }
                        //先移除分割数据
//                    list1.remove(0);
                        list2.remove(0);
                        if (list2.size() > 0) {
                            if (child.size() > 1) {//我的酒店和周边酒店
                                child.get(1).addAll(list2);
                            } else {//只有周边酒店
                                child.get(0).addAll(list2);
                            }
                        } else {
                            BaseApplication.toast("未找到更多的酒店聊天室");
                        }
                    }
                    //刷新数据，
                    if (expandableListViewAdapter == null) {
                        setExpandableListViewAdapter(child.size());
                    } else {
                        expandableListViewAdapter.notifyDataSetChanged();
                        pull_Expandable_lv.onRefreshComplete();
                    }
                    pull_Expandable_lv.onRefreshComplete();
                    if (child.get(0).size() > 0 || child.get(1).size() > 0) {
                        reEnpty.setVisibility(View.GONE);
                    } else {
                        reEnpty.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    BaseApplication.toast(e.toString());
                    pull_Expandable_lv.onRefreshComplete();
                    reEnpty.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        openGPSSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                isResume=true;
                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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



    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this
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
//            dialog = new ProgressDialog(MyLockGroupsActivity.this);
//            dialog.setMessage("正在定位...");
//            dialog.setCancelable(true);
//            dialog.show();
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
//        Toast.makeText(this, R.string.Toast08, Toast.LENGTH_SHORT).show();
        //这个为跳转到安全页面
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        startActivityForResult(intent,0); //此为设置完成后返回到获取界面
    }


    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);
    }

    //组 内 子列表点击事件；
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ChatActivity.class);
            intent.putExtra("chatType", 3);
//            intent.putExtra("groupId", "145518083904111200");
            intent.putExtra("groupId", child.get(groupPosition).get(childPosition).getEmid());
            intent.putExtras(intent);
            startActivity(intent);
            AppContext.toLog("liaot" + child.get(groupPosition).get(childPosition).getEmid());
        } else {
            makeHuanXinLogin();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ChatActivity.class);
            intent.putExtra("chatType", 3);
//            intent.putExtra("groupId", "145518083904111200");
            intent.putExtra("groupId", child.get(groupPosition).get(childPosition).getEmid());
            intent.putExtras(intent);
            startActivity(intent);
            AppContext.toLog("liaot" + child.get(groupPosition).get(childPosition).getEmid());
        }
        return false;
    }

    //组的点击事件 ，返回true 为无点击事件（不可点击）
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            latitude = bundle.getDouble(Config.LATITUDE);//纬度
            longitude = bundle.getDouble(Config.LONGITUDE);//经度
//            latitude=22.612130;
//            longitude=114.022295;
//            if (dialog.isShowing()){
//                dialog.dismiss();
//            }
            pageid = 1;
            initData(latitude, longitude, pageid);

            MyLockGroupsActivity.this.unregisterReceiver(this);// 不需要时注销
        }
    }


    private void registerHuanXin() {
        if (useName == null) {
            return;
        }
        //随机生成环信密码
        PwdGenHelper pwdGenHelper=new PwdGenHelper();
        pwd=pwdGenHelper.getpwd(12,true,false,true);
        HttpClient.instance().huanxin_RegisterUser(useName, pwd, new HuanXinUser());
//        HttpClient.instance().huanxin_RegisterUser("666666","666666",new HuanXinUser());

    }

    class HuanXinUser extends HttpCallBack {

        public void onStart() {
            super.onStart();
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            System.out.println(R.string.Toast10 + error + message);
            registerHuanXin();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            if (responseBean.isFailure()) {
                toastFail(R.string.Toast10);
                return;
            }
            try {
                final HuanXinUserEntity user = responseBean.getData(HuanXinUserEntity.class);
                AppContext.toLog(user.toString());
                //如果账号已经存在
                if (user.getError() != null) {
                    if (user.getError().equals("null")) {
                    } else {
                        if (user.getError().equals("duplicate_unique_property_exists")) {
                            String b = String.valueOf((int) (Math.random() * 100));//
                            useName = useName + b;
                            //使用我的账号注册登录
                            registerHuanXin();
                        }
                    }
                }
                //有账号就登录
                if (user.getUsername() != null) {
                    if (!user.getUsername().equals("null")) {
                        //登录环信
                        loginHuanXin(user.getUsername(), pwd);
                    }
                }
            } catch (Exception e) {

            }
        }

    }

    private void loginHuanXin(final String userName, final String pwd) {
        if (userName==null||userName.equals("null")){
            return;
        }
        if (pwd==null||pwd.equals("null")){
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
                        Log.d("main", "登陆聊天服务器成功！");
                        // 登陆成功，保存用户名密码

                        AppContext.getInstance().setUserName(userName);
                        AppContext.getInstance().setPassword(pwd);
                        AppContext.setProperty(Config.EMID, userName);
                        AppContext.setProperty(Config.MYHUANXINPWD,pwd);

                        openGPSSettings();

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

}






