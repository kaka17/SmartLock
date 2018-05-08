package com.sht.smartlock.ui.activity.mine;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppManager;

import com.sht.smartlock.CacheManagerHelper;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKModel;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.util.VersionUpdateUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MySettingsActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private TextView tvAbout_us;
    private TextView tvCheck_update;
    private TextView tvCache;
//    private TextView tgButtonAlert_tone;
//    private TextView tgButtonCircular_motion;
    private ToggleButton tgButtonAlert_tone;
    private ToggleButton tgButtonCircular_motion;
    private EMChatOptions chatOptions;
    DemoHXSDKModel model;
//    private SharedPreferences share;
//    private SharedPreferences.Editor editor = null;
    private Map<String, String> version = new HashMap<String, String>();
    private Map<String, String> mapversion = new HashMap<String, String>();
//    private List<Version> versionList = new ArrayList<Version>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        share = getSharedPreferences(Config.SOUND_AND_VIBRATTON, Context.MODE_PRIVATE);
//        editor= share.edit();
        findviewbyid();
        setClickLister();
//        initData();
        setMyHuanXin();
    }

    private void setMyHuanXin(){
        chatOptions = EMChatManager.getInstance().getChatOptions();
        model = (DemoHXSDKModel) HXSDKHelper.getInstance().getModel();


// 环信
        // 震动和声音总开关，来消息时，是否允许此开关打开
        // the vibrate and sound notification are allowed or not?
        if (model.getSettingMsgNotification()) {
//            iv_switch_open_notification.setVisibility(View.VISIBLE);
//            iv_switch_close_notification.setVisibility(View.INVISIBLE);
        } else {
//            iv_switch_open_notification.setVisibility(View.INVISIBLE);
//            iv_switch_close_notification.setVisibility(View.VISIBLE);
        }

        // 是否打开声音
        // sound notification is switched on or not?
        if (model.getSettingMsgSound()) {
//            iv_switch_open_sound.setVisibility(View.VISIBLE);
//            iv_switch_close_sound.setVisibility(View.INVISIBLE);
//            BaseApplication.toast(closesound+"声音11");
            tgButtonAlert_tone.setChecked(true);

        } else {
//            iv_switch_open_sound.setVisibility(View.INVISIBLE);
//            iv_switch_close_sound.setVisibility(View.VISIBLE);
//            BaseApplication.toast(closesound+"声音22");
            tgButtonAlert_tone.setChecked(false);
        }

        // 是否打开震动
        // vibrate notification is switched on or not?
        if (model.getSettingMsgVibrate()) {
//            iv_switch_open_vibrate.setVisibility(View.VISIBLE);
//            iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
            tgButtonCircular_motion.setChecked(true);
        } else {
//            iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
//            iv_switch_close_vibrate.setVisibility(View.VISIBLE);
            tgButtonCircular_motion.setChecked(false);
        }

        // 是否打开扬声器
        // the speaker is switched on or not?
        if (model.getSettingMsgSpeaker()) {
//            iv_switch_open_speaker.setVisibility(View.VISIBLE);
//            iv_switch_close_speaker.setVisibility(View.INVISIBLE);
        } else {
//            iv_switch_open_speaker.setVisibility(View.INVISIBLE);
//            iv_switch_close_speaker.setVisibility(View.VISIBLE);
        }

    }
    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        tvAbout_us = (TextView)findViewById(R.id.tvAbout_us);
        tvCheck_update = (TextView)findViewById(R.id.tvCheck_update);
        tvCache = (TextView)findViewById(R.id.tvCache);
        tvTitlePanel.setText("我的设置");
        tgButtonAlert_tone = (ToggleButton) findViewById(R.id.tgButtonAlert_tone);
        tgButtonCircular_motion = (ToggleButton) findViewById(R.id.tgButtonCircular_motion);
//        if(!AppVersion.getDelCache().toString().equals("")){
//            tvCache.setText(AppVersion.getDelCache());
//        }
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
        tvAbout_us.setOnClickListener(listener);
        tvCache.setOnClickListener(listener);
        tgButtonAlert_tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgButtonAlert_tone.isChecked()){//第一次点击事件
                    //开启声音，
                    chatOptions.setNoticeBySound(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
//                    editor.putBoolean(Config.CLOSE_SOUND, true);
//                    editor.commit();

                }else {//
                    //关闭声音
                    chatOptions.setNoticeBySound(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
//                    editor.putBoolean(Config.CLOSE_SOUND, false);
//                    editor.commit();
                }
            }
        });

        //设置震动
        tgButtonCircular_motion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgButtonCircular_motion.isChecked()){//开启震动
                    chatOptions.setNoticedByVibrate(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
//                    editor.putBoolean(Config.VIBRATION, true);
//                    editor.commit();
                }else {//关闭震动
                    chatOptions.setNoticedByVibrate(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
//                    editor.putBoolean(Config.VIBRATION, false);
//                    editor.commit();

                }
            }
        });
        tvCheck_update.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancle://反回按钮
                   finish();
                    break;
                case R.id.tvAbout_us:
                    startActivity(new Intent(MySettingsActivity.this, AboutUsActivity.class));
                    break;
                case R.id.tvCheck_update://检查版本更新
                    VersionUpdateUtil updateUtil = new VersionUpdateUtil(MySettingsActivity.this);
                    updateUtil.check(true);
                    break;
                case R.id.tvCache:
                    // 清理缓存
//                    CacheManagerHelper.clearApplicationCache(getApplicationContext());
                    tvCache.setText("0M");
//                    initData();
                     //删除图片缓存
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                ImageLoader.getInstance().clearDiskCache();
//                                CacheManagerHelper.delelteDirAllFiles(Environment
//                                        .getExternalStorageDirectory());
                            } catch (Exception e) {
                                LogUtil.log(e.toString());
                            }
                        }
                    }).start();
                    break;
                case R.id.tgButtonAlert_tone:
                    break;
                case R.id.tgButtonCircular_motion:
                    break;
            }
        }
    };

    TextView tvVersionInfo;

    public void eliminateCach() {//检查缓存
        try {
            File externalCacheDir = getApplicationContext()
                    .getExternalCacheDir();
            String cacheSize = CacheManagerHelper
                    .getCacheSize(externalCacheDir);
            tvCache.setText(cacheSize);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        eliminateCach();//检查缓存大小
//        getVersionName();//检查版本
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version.put("versionName",packInfo.versionName);
            version.put("versionCode", packInfo.versionCode + "");
//            BaseApplication.toast(version.get("versionName"));
            tvCheck_update.setText("V" + packInfo.versionName);
        }catch (Exception e){
            BaseApplication.toast(e.toString());
        }
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_settings;
    }
}
