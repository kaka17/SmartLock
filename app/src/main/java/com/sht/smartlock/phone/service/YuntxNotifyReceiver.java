package com.sht.smartlock.phone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.LauncherActivity;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.chatting.IMChattingHelper;
import com.sht.smartlock.phone.ui.voip.VoIPCallActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECDeviceType;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMultiDeviceState;
import com.yuntongxun.ecsdk.booter.ECNotifyReceiver;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

/**
 * SDK通知会分三种提醒方式
 * <p>1、直接通过设置的回调接口（OnChatReceiveListener）Push给应用
 * <p>2、如果应用没有设置回调接口则采用（BroadcastReceiver）广播通知（v5.1.8版本）此时如果应用处于未运行状态则会直接唤醒应用处理
 * <p>3、如果应用未处于运行状态并且不想被唤醒应用运行则采用状态栏通知处理（SDK直接提醒，不会通知应用）,比如调用
 * {@link ECDevice#logout(ECDevice.NotifyMode, ECDevice.OnLogoutListener)}退出接口传入后台接收消息才会有提醒
 * @author 容联•云通讯
 * @version 5.1.8
 * @since 2015-10-23
 */
public class YuntxNotifyReceiver extends ECNotifyReceiver {

    public static final String SERVICE_OPT_CODE = "service_opt_code";
    public static final String MESSAGE_SUB_TYPE  = "message_type";

    /** 来电 */
    public static final int EVENT_TYPE_CALL = 1;
    /** 消息推送 */
    public static final int EVENT_TYPE_MESSAGE = 2;

    /**
     * 创建一个服务Intent
     * @return Intent
     */
    public Intent buildServiceAction(int optCode) {
        Intent notifyIntent = new Intent(getContext(), NotifyService.class);
        notifyIntent.putExtra("service_opt_code" , optCode);
        return notifyIntent;
    }
    
  

    /**
     * 创建一个服务Intent
     * @return Intent
     */
    public Intent buildMessageServiceAction(int subOptCode) {
        Intent intent = buildServiceAction(EVENT_TYPE_MESSAGE);
        intent.putExtra(MESSAGE_SUB_TYPE , subOptCode);
        return intent;
    }





    public void showToast(ECMultiDeviceState deviceState){
        if(deviceState==null){
            return;
        }
        StringBuilder stringBuilder =new StringBuilder();
        String name=null;
        String state=null;
        if(deviceState.getDeviceType()== ECDeviceType.ANDROID_PAD){
            name="安卓pad端";
        }else if(deviceState.getDeviceType()==ECDeviceType.IPAD){
            name="ipad设备端";
        }else if(deviceState.getDeviceType()==ECDeviceType.PC){
            name="pc电脑端";
        }else if(deviceState.getDeviceType()==ECDeviceType.WEB){
            name="web浏览器端";
        }else if(deviceState.getDeviceType()==ECDeviceType.IPHONE){
            name="苹果手机端";
        }else if(deviceState.getDeviceType()==ECDeviceType.ANDROID_PHONE){
            name="安卓手机端";
        }else if(deviceState.getDeviceType()==ECDeviceType.UN_KNOW){
            name="未知设备上";
        }

        if(deviceState.isOnline()){
            state="登录";
        }else{
            state="下线";
        }

        stringBuilder.append("您的账号在另外的");
        stringBuilder.append(name);
        stringBuilder.append(state);
        ToastUtil.showMessage(stringBuilder.toString());


    }


    @Override
    public void onReceivedMessage(Context context, ECMessage msg) {
        Intent intent = buildMessageServiceAction(OPTION_SUB_NORMAL);
        intent.putExtra(EXTRA_MESSAGE , msg);
        context.startService(intent);
    }

    @Override
    public void onCallArrived(Context context, Intent intent) {
        Intent serviceAction = buildServiceAction(EVENT_TYPE_CALL);
        serviceAction.putExtras(intent);
        context.startService(serviceAction);
    }



    @Override
    public void onReceiveGroupNoticeMessage(Context context, ECGroupNoticeMessage notice) {
        Intent intent = buildMessageServiceAction(OPTION_SUB_GROUP);
        intent.putExtra(EXTRA_MESSAGE , notice);
        context.startService(intent);
    }

    @Override
    public void onNotificationClicked(Context context, int notifyType, String sender) {
        LogUtil.d("ECSDK_Demo.YuntxNotifyReceiver", "onNotificationClicked notifyType " + notifyType + " ,sender " + sender);
        Intent intent = new Intent(context, LauncherActivity.class);
        intent.putExtra("Main_Session", sender);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onReceiveMessageNotify(Context context, ECMessageNotify notify) {
        Intent intent = buildMessageServiceAction(OPTION_SUB_MESSAGE_NOTIFY);
        intent.putExtra(EXTRA_MESSAGE , notify);
        context.startService(intent);
    }
    
    @Override
    public void onSoftVersion(Context context, String version , String softDesc, boolean force) {
    	// TODO Auto-generated method stub
    	super.onSoftVersion(context, version, softDesc, force);
    	SDKCoreHelper.setSoftUpdate(version, softDesc, force);
    }

    public static class NotifyService extends Service {
        public static final String TAG = "ECSDK_Demo.NotifyService";
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
        private void receiveImp(Intent intent) {
            if(intent == null) {
                LogUtil.e(TAG, "receiveImp receiveIntent == null");
                return ;
            }
            LogUtil.i(TAG, "intent:action " + intent.getAction());
            int optCode = intent.getIntExtra(SERVICE_OPT_CODE, 0);
            if(optCode == 0) {
                LogUtil.e(TAG, "receiveImp invalid opcode.");
                return ;
            }
            if(!SDKCoreHelper.isUIShowing()) {
                SDKCoreHelper.init(this);
            }
            switch (optCode) {
                case EVENT_TYPE_CALL:
                    LogUtil.d(TAG, "receive call event ");
                    Intent call = new Intent(this ,VoIPCallActivity.class);
                    call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    call.putExtras(intent);
                    startActivity(call);
                    break;
                case EVENT_TYPE_MESSAGE:
                    dispatchOnReceiveMessage(intent);
                    break;
            }
        }

        // Android Version 2.0以下版本
        @Override
        public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {
                receiveImp(intent);
            }
        }

        // Android 2.0以上版本回调/同时会执行onStart方法
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            LogUtil.v(TAG, String.format("onStartCommand flags :%d startId :%d intent %s", flags, startId, intent));
            receiveImp(intent);
            return super.onStartCommand(intent, flags, startId);
        }


        private void dispatchOnReceiveMessage(Intent intent) {
            if(intent == null) {
                LogUtil.e(TAG , "dispatch receive message fail.");
                return ;
            }
            int subOptCode = intent.getIntExtra(MESSAGE_SUB_TYPE , -1);
            if(subOptCode == -1) {
                return ;
            }
            switch (subOptCode){
                case OPTION_SUB_NORMAL:
                    ECMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
                    IMChattingHelper.getInstance().OnReceivedMessage(message);
                    break;
                case OPTION_SUB_GROUP:
                    ECGroupNoticeMessage notice = intent.getParcelableExtra(EXTRA_MESSAGE);
                    IMChattingHelper.getInstance().OnReceiveGroupNoticeMessage(notice);
                    break;
                case OPTION_SUB_MESSAGE_NOTIFY:
                    ECMessageNotify notify = intent.getParcelableExtra(EXTRA_MESSAGE);
                    IMChattingHelper.getInstance().onReceiveMessageNotify(notify);
                    break;
            }
        }
    }
}
