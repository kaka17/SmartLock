/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.sht.smartlock.phone.common;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.dialog.ECListDialog;
import com.sht.smartlock.phone.common.utils.ECPreferenceSettings;
import com.sht.smartlock.phone.common.utils.ECPreferences;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.MimeTypesTools;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.core.ClientUser;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.LocationInfo;
import com.sht.smartlock.phone.ui.ShowBaiDuMapActivity;
import com.sht.smartlock.phone.ui.chatting.ChattingActivity;
import com.sht.smartlock.phone.ui.chatting.ChattingFragment;
import com.sht.smartlock.phone.ui.chatting.ImageGalleryActivity;
import com.sht.smartlock.phone.ui.chatting.ImageGralleryPagerActivity;
import com.sht.smartlock.phone.ui.chatting.ImageMsgInfoEntry;
import com.sht.smartlock.phone.ui.chatting.ViewImageInfo;
import com.sht.smartlock.phone.ui.chatting.view.ChatFooterPanel;
import com.sht.smartlock.phone.ui.voip.VideoActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.im.ECLocationMessageBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 存储SDK一些全局性的常量
 * Created by Jorstin on 2015/3/17.
 */
public class CCPAppManager {

    public static Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
    /**Android 应用上下文*/
    private static Context mContext = null;
    /**包名*/
    public static String pkgName = "com.yuntongxun.ecdemo";
    /**SharedPreferences 存储名字前缀*/
    public static final String PREFIX = "com.yuntongxun.ecdemo_";
    public static final int FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT = 0x10000000;
    /**IM功能UserData字段默认文字*/
    public static final String USER_DATA = "yuntongxun.ecdemo";
    public static HashMap<String, Integer> mPhotoCache = new HashMap<String, Integer>();
    public static ArrayList<ECSuperActivity> activities = new ArrayList<ECSuperActivity>();
    
    /**IM聊天更多功能面板*/
    private static ChatFooterPanel mChatFooterPanel;
    public static String getPackageName() {
        return pkgName;
    }
    private static ClientUser mClientUser;
    /**
     * 返回SharePreference配置文件名称
     * @return
     */
    public static String getSharePreferenceName() {
        return pkgName + "_preferences";
    }

    public static SharedPreferences getSharePreference() {
        if (mContext != null) {
            return mContext.getSharedPreferences(getSharePreferenceName(), 0);
        }
        return null;
    }

    /**
     * 返回上下文对象
     * @return
     */
    public static Context getContext(){
        return mContext;
    }
    
    public static void sendRemoveMemberBR(){
    	
    	getContext().sendBroadcast(new Intent("com.yuntongxun.ecdemo.removemember"));
    }

    /**
     * 设置上下文对象
     * @param context
     */
    public static void setContext(Context context) {
        mContext = context;
        pkgName = context.getPackageName();
        LogUtil.d(LogUtil.getLogUtilsTag(CCPAppManager.class),
                "setup application context for package: " + pkgName);
    }

    public static ChatFooterPanel getChatFooterPanel(Context context) {
        return mChatFooterPanel;
    }

    /**
     * 缓存账号注册信息
     * @param user
     */
    public static void setClientUser(ClientUser user) {
        mClientUser = user;
    }

    /**
     * 保存注册账号信息
     * @return
     */
    public static ClientUser getClientUser() {
        if(mClientUser != null) {
            return mClientUser;
        }
        String registAccount = getAutoRegistAccount();
        if(!TextUtils.isEmpty(registAccount)) {
            mClientUser = new ClientUser("");
            return mClientUser.from(registAccount);
        }
        return null;
    }

    public static String getUserId() {
        return getClientUser().getUserId();
    }

    private static String getAutoRegistAccount() {
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings registAuto = ECPreferenceSettings.SETTINGS_REGIST_AUTO;
        String registAccount = sharedPreferences.getString(registAuto.getId(), (String) registAuto.getDefaultValue());
        return registAccount;
    }

    /**
     * @param context
     * @param path
     */
    public static void doViewFilePrevieIntent(Context context ,String path) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = MimeTypesTools.getMimeType(context, path);
            File file = new File(path);
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(LogUtil.getLogUtilsTag(CCPAppManager.class), "do view file error " + e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param cotnext
     * @param value
     */
    public static void startChattingImageViewAction(Context cotnext, ImageMsgInfoEntry value) {
        Intent intent = new Intent(cotnext, ImageGralleryPagerActivity.class);
        intent.putExtra(ImageGalleryActivity.CHATTING_MESSAGE, value);
        cotnext.startActivity(intent);
    }

    /**
     * 批量查看图片
     * @param ctx
     * @param position
     * @param session
     */
    public static void startChattingImageViewAction(Context ctx , int position , ArrayList<ViewImageInfo> session) {
        Intent intent = new Intent(ctx, ImageGralleryPagerActivity.class);
        intent.putExtra(ImageGralleryPagerActivity.EXTRA_IMAGE_INDEX, position);
        intent.putParcelableArrayListExtra(ImageGralleryPagerActivity.EXTRA_IMAGE_URLS, session);
        ctx.startActivity(intent);
    }

    /**
     * 获取应用程序版本名称
     * @return
     */
    public static String getVersion() {
        String version = "0.0.0";
        if(mContext == null) {
            return version;
        }
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 获取应用版本号
     * @return 版本号
     */
    public static int getVersionCode() {
        int code = 1;
        if(mContext == null) {
            return code;
        }
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }


    public static void addActivity(ECSuperActivity activity) {
        activities.add(activity);
    }

    public static void clearActivity() {
        for(ECSuperActivity activity : activities) {
            if(activity != null) {
                activity.finish();
                activity = null;
            }
            activities.clear();
        }
    }
    

    /**
     * 打开浏览器下载新版本
     * @param context
     */
    public static void startUpdater(Context context) {
        Uri uri = Uri.parse("http://dwz.cn/F8Amj");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static HashMap<String, Object> prefValues = new HashMap<String, Object>();

    /**
     *
     * @param key
     * @param value
     */
    public static void putPref(String key , Object value) {
        prefValues.put(key, value);
    }

    public static Object getPref(String key) {
        return prefValues.remove(key);
    }

    public static void removePref(String key) {
        prefValues.remove(key);
    }

    /**
     * 开启在线客服
     * @param context
     * @param contactid
     */
    public static void startCustomerServiceAction(Context context , String contactid) {
        Intent intent = new Intent(context, ChattingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ChattingFragment.RECIPIENTS, contactid);
        intent.putExtra(ChattingFragment.CONTACT_USER, "在线客服");
        intent.putExtra(ChattingActivity.CONNECTIVITY_SERVICE, true);
        context.startActivity(intent);
    }

    /**
     * 聊天界面
     * @param context
     * @param contactid
     * @param username
     */
    public static void startChattingAction(Context context , String contactid , String username) {
        startChattingAction(context, contactid, username, false);
    }

    /**
     *
     * @param context
     * @param contactid
     * @param username
     * @param clearTop
     */
    public static void startChattingAction(Context context , String contactid , String username , boolean clearTop) {
        Intent intent = new Intent(context, ChattingActivity.class);
        if(clearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.putExtra(ChattingFragment.RECIPIENTS, contactid);
        intent.putExtra(ChattingFragment.CONTACT_USER, username);
        intent.putExtra(ChattingFragment.CUSTOMER_SERVICE, false);
        context.startActivity(intent);
    }

    /**
     * VoIP呼叫
     * @param nickname 昵称
     * @param contactId 呼出号码
     */
    public static void callVoIPAction(Context ctx , String nickname, String contactId) {
        // VoIP呼叫
        callVoIPAction(ctx, ECVoIPCallManager.CallType.VOICE, nickname, contactId, false);
    }

    /**
     * 根据呼叫类型通话
     * @param ctx 上下文
     * @param callType 呼叫类型
     * @param nickname 昵称
     * @param contactId 号码
     */
    public static void callVoIPAction(Context ctx , ECVoIPCallManager.CallType callType ,String nickname, String contactId,boolean flag) {
        // VoIP呼叫
        Intent callAction = new Intent(ctx , VoIPCallActivity.class);
        if(callType == ECVoIPCallManager.CallType.VIDEO) {
            callAction = new Intent(ctx , VideoActivity.class);
            VoIPCallHelper.mHandlerVideoCall = true;
        } else {
            VoIPCallHelper.mHandlerVideoCall = false;
        }
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NAME , nickname);
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NUMBER , contactId);
        callAction.putExtra(ECDevice.CALLTYPE , callType);
        callAction.putExtra(VoIPCallActivity.EXTRA_OUTGOING_CALL , true);
        
        if(flag){
        callAction.putExtra(VoIPCallActivity.ACTION_CALLBACK_CALL, true);	
        }
        ctx.startActivity(callAction);
    }

    /**
     * 根据呼叫类型通话
     * @param ctx 上下文
     * @param callType 呼叫类型
     * @param nickname 昵称
     * @param contactId 号码
     */
    public static void callVideoAction(Context ctx , ECVoIPCallManager.CallType callType ,String nickname, String contactId , String callid) {
        // VoIP呼叫
        Intent callAction = new Intent(ctx , VoIPCallActivity.class);
        if(callType == ECVoIPCallManager.CallType.VIDEO) {
            callAction = new Intent(ctx , VideoActivity.class);
            VoIPCallHelper.mHandlerVideoCall = true;
        } else {
            VoIPCallHelper.mHandlerVideoCall = false;
        }
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NAME , nickname);
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NUMBER , contactId);
        callAction.putExtra(ECDevice.CALLTYPE , callType);
        callAction.putExtra(ECDevice.CALLID, callid);
        callAction.putExtra(VoIPCallActivity.EXTRA_OUTGOING_CALL, true);
        ctx.startActivity(callAction);
    }

    /**
     * 多选呼叫菜单
     * @param ctx 上下文
     * @param nickname  昵称
     * @param contactId 号码
     */
    public static void showCallMenu(final Context ctx , final String nickname, final String contactId) {
        ECListDialog dialog = new ECListDialog(ctx , R.array.chat_call);;
        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {
                LogUtil.d("onDialogItemClick", "position " + position);

                if (position == 3) {
                    callVoIPAction(ctx, ECVoIPCallManager.CallType.VOICE, nickname, contactId, true);
                    return;
                }

                callVoIPAction(ctx, ECVoIPCallManager.CallType.values()[position], nickname, contactId, false);
            }
        });
        dialog.setTitle(R.string.ec_talk_mode_select);
        dialog.show();
    }

    static List<Integer> mChecks;

    /**
     * 提示选择呼叫编码设置
     * @param ctx 上下文
     */
    public static void showCodecConfigMenu(final Context ctx) {
        final ECListDialog multiDialog = new ECListDialog(ctx , R.array.Codec_call);
        multiDialog.setOnDialogItemClickListener(false, new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {
                LogUtil.d("onDialogItemClick", "position " + position);
            }
        });
        if(mChecks == null) {
            mChecks = new ArrayList<Integer>();
            mChecks.add(ECVoIPSetupManager.Codec.Codec_iLBC.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_G729.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_PCMU.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_PCMA.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_H264.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_SILK8K.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_AMR.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_VP8.ordinal());
            mChecks.add(ECVoIPSetupManager.Codec.Codec_SILK16K.ordinal());
        }
        multiDialog.setChecks(mChecks);
        multiDialog.setTitle(R.string.ec_talk_codec_select);
        multiDialog.setButton(ECListDialog.BUTTON_POSITIVE, R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChecks = multiDialog.getCheck();
                if (mChecks == null) {
                    ToastUtil.showMessage("设置失败，未选择任何编码");
                    return;
                }
                ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
                if(setupManager == null) {
                    ToastUtil.showMessage("设置失败，请先初始化SDK");
                    return ;
                }
                for(ECVoIPSetupManager.Codec  code : ECVoIPSetupManager.Codec.values()) {
                    boolean enable = mChecks.contains(code.ordinal());
                    setupManager.setCodecEnabled(code , enable);
                }
                ToastUtil.showMessage("设置成功");
            }
        });
        multiDialog.show();
    }


	public static void startShowBaiDuMapAction(ChattingActivity mContext2,
			ECMessage iMessage) {

			if(iMessage==null||mContext2==null){
				return;
			}
			
		 Intent intent=new Intent(mContext2,ShowBaiDuMapActivity.class);
       	 
		 ECLocationMessageBody body=(ECLocationMessageBody) iMessage.getBody();
       	 LocationInfo locationInfo =new LocationInfo();
       	 locationInfo.setLat(body.getLatitude());
       	 locationInfo.setLon(body.getLongitude());
       	 locationInfo.setAddress(body.getTitle());
       	 intent.putExtra("location", locationInfo);
			
		mContext2.startActivity(intent);
			
	}
}
