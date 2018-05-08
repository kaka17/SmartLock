/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sht.smartlock.ui.chat.applib.uidemo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;

import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
//import com.iflytek.sunflower.FlowerCollector;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.MyLockGroupsActivity;
import com.sht.smartlock.ui.activity.TastByListActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.fragment.NewServiceFragment;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.activity.VideoCallActivity;
import com.sht.smartlock.ui.chat.applib.activity.VoiceCallActivity;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.domain.RobotUser;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.model.HXNotifier;
import com.sht.smartlock.ui.chat.applib.model.HXSDKModel;
import com.sht.smartlock.ui.chat.applib.recever.CallReceiver;
import com.sht.smartlock.ui.chat.applib.utils.CommonUtils;

import com.sht.smartlock.ui.entity.TaskListEntity;
import com.sht.smartlock.util.LogUtil;
//import com.iflytek.sunflower.FlowerCollector;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 *
 * @author easemob
 */
public class DemoHXSDKHelper extends HXSDKHelper {

    private static final String TAG = "DemoHXSDKHelper";

    private static final Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    NewDoorFragment.tvLock.setText("");
                    break;
                case 1:
//                    Toast.makeText(appContext.,"",0).show();
                    break;
            }
        }
    };

    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;

    /**
     * robot list in cache
     */
    private Map<String, RobotUser> robotList;
    private CallReceiver callReceiver;

    private UserProfileManager userProManager;

    /**
     * 用来记录foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();
//    private List<String> voiceList = new ArrayList<String>();
    private MySQLiteOpenHelper mydbHelper = null;
    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    @Override
    public synchronized boolean onInit(Context context) {
        if (super.onInit(context)) {
            getUserProfileManager().onInit(context);

            //if your app is supposed to user Google Push, please set project number
            String projectNumber = "562451699741";
            EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
            return true;
        }

        return false;
    }

    @Override
    protected void initHXOptions() {
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
    }

    @Override
    protected void initListener() {
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);
        //注册消息事件监听
        initEventListener();
        //透传消息
        EMChat.getInstance().setAppInited();

        Log.e("TAG", "------------->DemoHXSDKHelper启动");
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                    Log.e("TAG", "receive the event : " + event.getEvent() + ",id : " + message.getMsgId() + "action====" + ((CmdMessageBody) message.getBody()).action);
                }

                switch (event.getEvent()) {
                    case EventNewMessage:
                        //应用在后台，不需要刷新UI,通知栏提示新消息
                        if (activityList.size() <= 0) {
                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        }
                        break;
                    case EventOfflineMessage:
                        if (activityList.size() <= 0) {
                            EMLog.d(TAG, "received offline messages");
                            List<EMMessage> messages = (List<EMMessage>) event.getData();
                            HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {

                        EMLog.d(TAG, "收到透传消息");
                        //获取消息body
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                        final String action = cmdMsgBody.action;//获取自定义action
                        handle.sendEmptyMessage(1);
                        //获取扩展属性 此处省略
                        //message.getStringAttribute("");
                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                        final String str = appContext.getString(R.string.receive_the_passthrough);
                        Log.e("TAG_DemoHXSDKHelper", "------------>action=" + action + "str=" + str);
                        Log.e("TAG", String.format("透传消息：action:%s,message:%s", action, message.toString()));
                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);


                        if (broadCastReceiver == null) {
                            broadCastReceiver = new BroadcastReceiver() {

                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    // TODO Auto-generated method stub
                                    if (action.equals("msg")) {
                                        String room_servicer_id = intent.getStringExtra("cmd_value");//会话记录消息id
//                                        voiceAnnouncements(intent.getStringExtra("cmd_value").toString());
                                        //
                                        setMsgInfo();
//                                        shuaXin();
                                        Intent intentMessage=new Intent();
                                        intentMessage.setAction(Config.SERVICERMESSAGE);
                                        intent.putExtra(Config.SERVICERMESSAGE_BYSRID, room_servicer_id);
                                        appContext.sendBroadcast(intentMessage);
                                    }
//                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                                }
                            };

                            //注册广播接收者
                            appContext.registerReceiver(broadCastReceiver, cmdFilter);
                        }
                        try {
                            Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
//                      broadcastIntent.putExtra("cmd_value", str+action);
                            broadcastIntent.putExtra("cmd_value", message.getStringAttribute("msg").toString());
                            broadcastIntent.putExtra("service_type", message.getStringAttribute("service_type").toString());
//                            AppContext.setProperty();
                            if (AppContext.getProperty(message.getStringAttribute("msg"))==null){
                                AppContext.setProperty(message.getStringAttribute("msg"),"1");
                            }else {
                                AppContext.setProperty(message.getStringAttribute("msg"), (Integer.parseInt(AppContext.getProperty(message.getStringAttribute("msg")))+1)+"");
                            }
                            //存储数据库
                            saveDb(message.getStringAttribute("service_type"), message.getStringAttribute("msg"));

                            appContext.sendBroadcast(broadcastIntent, null);
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;

            private void showToast(String value) {
                if (!registered) {
                    //注册广播接收者
                    appContext.registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
//                            Toast.makeText(appContext, intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }

                    }, filter);

                    registered = true;
                }

                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info", "onChatRoomDestroyed=" + roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
//                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined=" + participant);

            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited=" + participant);

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked=" + participant);

            }

        });
    }

    /**
     * 自定义通知栏提示内容
     *
     * @return
     */
    @Override
    protected HXNotifier.HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotifier.HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                if (robotMap != null && robotMap.containsKey(message.getFrom())) {
                    String nick = robotMap.get(message.getFrom()).getNick();
                    if (!TextUtils.isEmpty(nick)) {
                        return nick + ": " + ticker;
                    } else {
                        return message.getFrom() + ": " + ticker;
                    }
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                //有电话时优先跳转到通话页面
                if (isVideoCalling) {
                    intent = new Intent(appContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    intent = new Intent(appContext, VoiceCallActivity.class);
                } else {
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("groupId", message.getTo());
                        if (chatType == ChatType.GroupChat) {
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        };
    }


    @Override
    protected void onConnectionConflict() {
        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
        AppContext.toLog("Demo--finish");
//        EMChatManager.getInstance().logout(true,null);//此方法为同步方法
//        clearSharePassword();
    }

    @Override
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
        AppContext.toLog("Demo--removed");
    }

    /**
     * 清除密码
     */
    private void clearSharePassword() {
        try {
            AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
            AppContext.setProperty(Config.SHARE_USERPWD, null);
            AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
            AppContext.setProperty(Config.SHARE_USER_Name, null);
            //把选择的数据恢复
            NewDoorFragment.pos = 0;
            NewDoorFragment.list.clear();
            //开门密码,
            AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(false));
            EMChatManager.getInstance().logout();//此方法为同步方法
            AppContext.setProperty(Config.EMID, null);
            AppContext.setProperty(Config.MYHUANXINPWD, null);

            if (NewDoorFragment.tvLock != null) {
                handle.sendEmptyMessage(0);

            }

        } catch (Exception e) {
            AppContext.toLog(e.toString());
        }
    }

    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier() {
        return new HXNotifier() {
            public synchronized void onNewMsg(final EMMessage message) {
                if (EMChatManager.getInstance().isSlientMessage(message)) {
                    return;
                }

                String chatUsename = null;
                List<String> notNotifyIds = null;
                // 获取设置的不提示新消息的用户或者群组ids
                if (message.getChatType() == ChatType.Chat) {
                    chatUsename = message.getFrom();
                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledGroups();
                } else {
                    chatUsename = message.getTo();
                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }

                    viberateAndPlayTone(message);
                }
            }
        };
    }

    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel() {
        return (DemoHXSDKModel) hxModel;
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }

        return contactList;
    }

    public Map<String, RobotUser> getRobotList() {
        if (getHXId() != null && robotList == null) {
            robotList = ((DemoHXSDKModel) getModel()).getRobotList();
        }
        return robotList;
    }


    public boolean isRobotMenuMessage(EMMessage message) {

        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
            if (jsonObj.has("choice")) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public String getRobotMenuMessageDigest(EMMessage message) {
        String title = "";
        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
            if (jsonObj.has("choice")) {
                JSONObject jsonChoice = jsonObj.getJSONObject("choice");
                title = jsonChoice.getString("title");
            }
        } catch (Exception e) {
        }
        return title;
    }


    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    /**
     * 保存单个user
     */
    public void saveContact(User user) {
        contactList.put(user.getUsername(), user);
        ((DemoHXSDKModel) getModel()).saveContact(user);
    }

    @Override
    public void logout(final boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        super.logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                setRobotList(null);
                getUserProfileManager().reset();
                getModel().closeDB();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onError(code, message);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update User cach And db
     *
     * @param contactInfoList
     */
    public void updateContactList(List<User> contactInfoList) {
        for (User u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<User> mList = new ArrayList<User>();
        mList.addAll(contactList.values());
        ((DemoHXSDKModel) getModel()).saveContactList(mList);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }


    //语音播报
    // 语音合成对象
//    private SpeechSynthesizer mTts;
//
//    private void voiceAnnouncements(String message) {
//        LogUtil.log("语言播报");
//        if (!message.isEmpty()) {
//            voiceList.add(message);
//        }
//        // 初始化合成对象
//        mTts = SpeechSynthesizer.createSynthesizer(appContext, mTtsInitListener);
//        //设置语音朗读者，可以根据需要设置男女朗读，具体请看api文档和官方论坛
//
//        // 移动数据分析，收集开始合成事件
//        FlowerCollector.onEvent(appContext, "tts_play");
//
//        // 设置参数
//        setParam();
//
//        if (voiceList.size() == 1) {
//            voiceListenter(voiceList.get(0));
//        }
//
//    }
//
//    private void voiceListenter(String message) {
//
//        int code = mTts.startSpeaking(message, new SynthesizerListener() {
//            @Override
//            public void onSpeakBegin() {
//
//            }
//
//            @Override
//            public void onBufferProgress(int i, int i1, int i2, String s) {
//
//            }
//
//            @Override
//            public void onSpeakPaused() {
//
//            }
//
//            @Override
//            public void onSpeakResumed() {
//
//            }
//
//            @Override
//            public void onSpeakProgress(int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onCompleted(SpeechError speechError) {
//                if (speechError == null) {
//                    voiceList.remove(0);
//                    if (voiceList.size() > 0) {
//                        voiceListenter(voiceList.get(0));
//                    } else {
//                        mTts.stopSpeaking();
//                        // 退出时释放连接
//                        mTts.destroy();
//                    }
//                } else {
//                    AppContext.toast(speechError.getPlainDescription(true));
//                }
//            }
//
//            @Override
//            public void onEvent(int i, int i1, int i2, Bundle bundle) {
//
//            }
//        });
//
//        if (code != ErrorCode.SUCCESS) {
//            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
//                //未安装则跳转到提示安装页面
////                        mInstaller.install();
////                showTip("未安装则跳转到提示安装页面: " + code);
//                AppContext.toast("未安装本地语音合成");
//            } else {
////                showTip("语音合成失败,错误码: " + code);
//                AppContext.toast("语音合成失败,错误码");
//            }
//        }
//
//    }
//
//    /**
//     * 初始化监听。
//     */
//    private InitListener mTtsInitListener = new InitListener() {
//        @Override
//        public void onInit(int code) {
//            Log.d(TAG, "InitListener init() code = " + code);
//            if (code != ErrorCode.SUCCESS) {
////                showTip("初始化失败,错误码："+code);
//                Log.e("Void", "--------->" + code);
//                AppContext.toast("初始化失败,错误码：" + code);
//            } else {
//                // 初始化成功，之后可以调用startSpeaking方法
//                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
//                // 正确的做法是将onCreate中的startSpeaking调用移至这里
//            }
//        }
//    };
//
//    /**
//     * 参数设置
//     *
//     * @param param
//     * @return
//     */
//    private void setParam() {
//        // 清空参数
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//        // 根据合成引擎设置相应参数
////        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
////            Toast.makeText(getApplicationContext(),"在线",Toast.LENGTH_SHORT).show();
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        // 设置在线合成发音人
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//        //设置合成语速
//        mTts.setParameter(SpeechConstant.SPEED, "50");
//        //设置合成音调
//        mTts.setParameter(SpeechConstant.PITCH, "50");
//        //设置合成音量
//        mTts.setParameter(SpeechConstant.VOLUME, "50");
////        }else {//本地语音
////            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
////            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
////            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
////            /**
////             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
////             * 开发者如需自定义参数，请参考在线合成参数设置
////             */
////        }
//        //设置播放器音频流类型
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
//    }
    private void  saveDb(String servicetype,String room_service_id){
        mydbHelper = MySQLiteOpenHelper.getInstance(appContext);
        String sqlString2 = "select * from NUNoMSG where service_type=? and room_servicer_id=?";
        int count = mydbHelper.selectCount(
                sqlString2,
                new String[] {servicetype,room_service_id});
        Log.e("Save","__________存>"+count);
        if (count > 0) {
            // "数据已经存在需要更改
//            String sqlUpda="update service_type set msgnum=? where room_servicer_id='"+room_service_id+"'";
//            boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
//           //BaseApplication.toast(isfale+"数据更改");
        } else {
            // 先插入数据再查询数据
            String sql = "insert into NUNoMSG (service_type ,room_servicer_id) values (?,?)";
            mydbHelper.execData(
                    sql,
                    new Object[] {servicetype,room_service_id});
        }

    }

    private void setMsgInfo(){

        mydbHelper = MySQLiteOpenHelper.getInstance(appContext);
        String sqlString2 = "select * from NUNoMSG where service_type=? ";
        int cleanCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"1"});
        int changCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"2"});
        int otherCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"4"});

        Log.e("Save","__________cleanCount=>"+cleanCount+"changCount="+changCount+"otherCount="+otherCount);
        if (cleanCount > 0) {
            // "数据已经存在需要更改
            NewServiceFragment.ivCleanMsg.setVisibility(View.VISIBLE);
        }else {
            NewServiceFragment.ivCleanMsg.setVisibility(View.GONE);

        }
        if (changCount > 0) {
            // "数据已经存在需要更改
            NewServiceFragment.ivChangMsg.setVisibility(View.VISIBLE);
        }else {
            NewServiceFragment.ivChangMsg.setVisibility(View.GONE);

        }
        if (otherCount > 0) {
            // "数据已经存在需要更改
            NewServiceFragment.ivOtherMsg.setVisibility(View.VISIBLE);
        }else {
            NewServiceFragment.ivOtherMsg.setVisibility(View.GONE);
        }
        if (cleanCount>0||changCount>0||otherCount>0){
            MainActivity.ivServiceMsg.setVisibility(View.VISIBLE);
        }else {
            MainActivity.ivServiceMsg.setVisibility(View.GONE);
        }
    }

}
