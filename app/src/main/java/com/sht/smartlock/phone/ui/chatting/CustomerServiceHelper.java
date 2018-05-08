package com.sht.smartlock.phone.ui.chatting;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.FileAccessor;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.storage.IMessageSqlManager;
import com.sht.smartlock.phone.storage.ImgInfoSqlManager;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.chatting.model.ImgInfo;
import com.yuntongxun.ecsdk.ECDeskManager;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;

import java.io.File;
import java.io.IOException;

/**
 * com.yuntongxun.ecdemo.ui.chatting in ECDemo_Android
 * Created by Jorstin on 2015/6/16.
 */
public class CustomerServiceHelper {

    private static final String TAG = "ECSDK_Demo.CustomerServiceHelper";
    private static CustomerServiceHelper ourInstance = new CustomerServiceHelper();

    private ECDeskManager mDeskManager;
    private OnCustomerServiceListener mCustomerServiceListener;
    /**全局处理所有的IM消息发送回调*/
    private MessageListener mListener;
    public static CustomerServiceHelper getInstance() {
        return ourInstance;
    }

    private CustomerServiceHelper() {
        mListener = new MessageListener();
    }

    public static void startService(String event , final OnStartCustomerServiceListener listener) {
        if(!initECDeskManager()) {
            return ;
        }

        getInstance().mDeskManager.startConsultation(event, new ECDeskManager.OnStartConsultationListener() {
            @Override
            public void onStartConsultation(ECError e, String agent) {
                if (SdkErrorCode.REQUEST_SUCCESS == e.errorCode) {
                    OnStartCustomerServiceListener callback = listener;
                    // 成功
                    if (callback != null) {
                        callback.onServiceStart(agent);
                    }
                    return;
                }
                onRequestError(e , listener);
            }

        });
    }

    /**
     * 开启咨询
     * @param event
     */
    public static void startService(String event) {
        startService(event, getInstance().mCustomerServiceListener);
    }

    /**
     * 发送ECMessage 消息
     * @param msg
     */
    public static long sendMCMessage(ECMessage msg) {
        initECDeskManager();
        // 获取一个聊天管理器
        ECDeskManager manager = getInstance().mDeskManager;
        if(manager != null) {
            // 调用接口发送IM消息
            msg.setMsgTime(System.currentTimeMillis());
            manager.sendtoDeskMessage(msg, getInstance().mListener);
            // 保存发送的消息到数据库
            if(msg.getType() == ECMessage.Type.FILE && msg.getBody() instanceof ECFileMessageBody) {
                ECFileMessageBody fileMessageBody = (ECFileMessageBody) msg.getBody();
                msg.setUserData("fileName=" + fileMessageBody.getFileName());
            }
        } else {
            msg.setMsgStatus(ECMessage.MessageStatus.FAILED);
        }
        return IMessageSqlManager.insertIMessage(msg, ECMessage.Direction.SEND.ordinal());
    }

    /**
     *
     * @param imgInfo
     * @param message
     * @return
     */
    public static long sendImageMessage(ImgInfo imgInfo , ECMessage message) {

        ECDeskManager manager = getInstance().mDeskManager;
        if(manager != null) {
            // 调用接口发送IM消息
            manager.sendtoDeskMessage(message, getInstance().mListener);

            if(TextUtils.isEmpty(message.getMsgId())) {
                return -1;
            }
            imgInfo.setMsglocalid(message.getMsgId());
            BitmapFactory.Options options = DemoUtils.getBitmapOptions(new File(FileAccessor.IMESSAGE_IMAGE, imgInfo.getThumbImgPath()).getAbsolutePath());
            message.setUserData("outWidth://" + options.outWidth + ",outHeight://" + options.outHeight + ",THUMBNAIL://" + message.getMsgId()  + ",PICGIF://" + imgInfo.isGif);
            long row = IMessageSqlManager.insertIMessage(message, ECMessage.Direction.SEND.ordinal());
            if(row != -1) {
                return ImgInfoSqlManager.getInstance().insertImageInfo(imgInfo);
            }
        }
        return -1;

    }

    private static boolean initECDeskManager() {
        if(getInstance().mDeskManager == null) {
            getInstance().mDeskManager = SDKCoreHelper.getECDeskManager();
        }
        if(getInstance().mDeskManager == null) {
            LogUtil.e(TAG, "SDK not ready.");
            return false;
        }
        return true;
    }


    public static void finishService(String event) {
        if(!initECDeskManager()) {
            return ;
        }
        ECDeskManager manager = getInstance().mDeskManager;
        manager.finishConsultation(event, new ECDeskManager.OnFinishConsultationListener() {
            @Override
            public void onFinishConsultation(ECError e, String agent) {
                if (SdkErrorCode.REQUEST_SUCCESS == e.errorCode) {
                    OnCustomerServiceListener listener = getInstance().mCustomerServiceListener;
                    // 成功
                    if (listener != null) {
                        listener.onServiceFinish(agent);
                    }
                    return;
                }
                onRequestError(e);
            }

        });
    }

    public static  void addCustomerServiceListener(OnCustomerServiceListener listener) {
        getInstance().mCustomerServiceListener = listener;
    }

    public static void onRequestError(ECError error) {
        onRequestError(error , getInstance().mCustomerServiceListener);
    }

    public static void onRequestError(ECError error , OnStartCustomerServiceListener listener) {
        if(error == null || error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
            return ;
        }
        ToastUtil.showMessage("请求错误[" + error.errorCode + "]");
        if(listener != null) {
            listener.onError(error);
        }
    }


    public interface OnStartCustomerServiceListener {
        void onServiceStart(String event);
        void onError(ECError error);
    }

    public interface OnCustomerServiceListener extends OnStartCustomerServiceListener{
        void onServiceFinish(String even);
        void onMessageReport(ECError error, ECMessage message);
    }

    private class MessageListener implements ECDeskManager.OnSendDeskMessageListener {

        @Override
        public void onSendMessageComplete(ECError error, ECMessage message) {
            if(message == null) {
                return ;
            }
            // 处理ECMessage的发送状态
            if(message != null) {
                if(message.getType() == ECMessage.Type.VOICE) {
                    try {
                        DemoUtils.playNotifycationMusic(CCPAppManager.getContext(), "sound/voice_message_sent.mp3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                IMessageSqlManager.setIMessageSendStatus(message.getMsgId(), message.getMsgStatus().ordinal());
                IMessageSqlManager.notifyMsgChanged(message.getSessionId());
                OnCustomerServiceListener serviceListener = getInstance().mCustomerServiceListener;
                if(serviceListener != null) {
                    serviceListener.onMessageReport(error , message);
                }
                return ;
            }
        }

        @Override
        public void onProgress(String  msgId ,int total, int progress) {
            // 处理发送文件IM消息的时候进度回调
            LogUtil.d(TAG , "[MessageListener - onProgress] msgId：" +msgId + " ,total：" + total + " ,progress:" + progress);
        }

    }

}
