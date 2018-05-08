package com.sht.smartlock.ui.activity.mine;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.igexin.sdk.PushManager;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.helper.PermissionHelper;
//import com.sht.smartlock.mqtt.PushService;
import com.sht.smartlock.receiver.DemoIntentService;
import com.sht.smartlock.receiver.DemoPushService;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.GuideFragment01;
import com.sht.smartlock.ui.activity.fragment.GuideFragment02;
import com.sht.smartlock.ui.activity.fragment.GuideFragment03;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.zxing.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private ViewPager id_guideViewpager;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;

    private ImageView iv_frament1,iv_frament2,iv_frament3;
    private SimpleDateFormat formatter;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE04 = 104;//个推
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.CAMERA;
    //    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    //权限名称
    private static final String  WRITE_PERMISSION22 = Manifest.permission.ACCESS_COARSE_LOCATION;
    //个推权限
    private static final String  WRITE_PERMISSION02 = Manifest.permission.READ_PHONE_STATE;
    private static final String  WRITE_PERMISSION03 = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = DemoPushService.class;
    private List<String> listPermission = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION22);
        listPermission.add(WRITE_PERMISSION02);
        listPermission.add(WRITE_PERMISSION03);

        findviewbyid();
        getMyGeTui();
    }

    private void findviewbyid(){
        id_guideViewpager = (ViewPager)findViewById(R.id.id_guideViewpager);
        iv_frament1 = (ImageView)findViewById(R.id.iv_frament1);
        iv_frament2 = (ImageView)findViewById(R.id.iv_frament2);
        iv_frament3 = (ImageView)findViewById(R.id.iv_frament3);

        iv_frament1.setImageResource(R.drawable.dot_light);

        GuideFragment01 guideTab01 = new GuideFragment01();
        GuideFragment02 guideTab02 = new GuideFragment02();
        GuideFragment03 guideTab03 = new GuideFragment03();
        mFragments.add(guideTab01);
        mFragments.add(guideTab02);
        mFragments.add(guideTab03);

        id_guideViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        iv_frament1.setVisibility(View.VISIBLE);
                        iv_frament2.setVisibility(View.VISIBLE);
                        iv_frament3.setVisibility(View.VISIBLE);
                        iv_frament1.setImageResource(R.drawable.dot_light);
                        iv_frament2.setImageResource(R.drawable.dot_dim);
                        break;
                    case 1:
                        iv_frament1.setVisibility(View.VISIBLE);
                        iv_frament2.setVisibility(View.VISIBLE);
                        iv_frament3.setVisibility(View.VISIBLE);
                        iv_frament1.setImageResource(R.drawable.dot_dim);
                        iv_frament2.setImageResource(R.drawable.dot_light);
                        break;
                    case 2:
                        iv_frament1.setVisibility(View.GONE);
                        iv_frament2.setVisibility(View.GONE);
                        iv_frament3.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        id_guideViewpager.setAdapter(mAdapter);
        id_guideViewpager.setCurrentItem(0);
    }

    private void visibleBackground(){

    }


    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    private void getMyGeTui() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION,WRITE_PERMISSION22,WRITE_PERMISSION03);
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
        Log.d("TAG", "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

        // 检查 so 是否存在
//        File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
//        Log.e(TAG, "libgetuiext2.so exist = " + file.exists());


//        mPrefs = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
//        String deviceID = mPrefs.getString(PushService.PREF_DEVICE_ID, null);
//        if (deviceID!=null){
////            PushService.actionStart(getApplicationContext());
//            LogUtil.logE("MqttID=" + deviceID);
//        }else {
//            LogUtil.logE("MqttID=null   and go  to save  MqttId    ....");
//            SharedPreferences.Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
//            String mDeviceID= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//            editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
//            editor.commit();
//            LogUtil.logE("MqttID=" + mDeviceID);
////            PushService.actionStart(getApplicationContext());
//        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        AppContext.toast("MainActivity requestCode=" + requestCode);
        switch (requestCode) {
            case WRITE_RESULT_CODE04:
                //个推
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
