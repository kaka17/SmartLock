package com.sht.smartlock.phone.ui.interphone;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECAlertDialog;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.ECNotificationManager;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.meeting.MeetingHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneControlMicMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneExitMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneJoinMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMember;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneReleaseMicMsg;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.interphone in ECDemo_Android
 * Created by Jorstin on 2015/7/16.
 */
public class InterPhoneChatActivity extends InterPhoneBaseActivity
        implements InterPhoneHelper.OnInterPhoneListener , View.OnClickListener , InterPhoneMicController.OnInterPhoneMicListener{

    private static final String TAG = "ECSDK_Demo.InterPhoneChatActivity";

    public static final String EXTRA_MEMBERS = "com.yuntongxun.Meeting.meetingMembers";

    /**实时对讲加入状态通知*/
    private InterPhoneBannerView mBannerView;
    /**实时对讲成员列表*/
    private ListView mInterPhoneListView;
    /**实时对讲控麦面板*/
    private InterPhoneMicController mMicController;
    /**实时对讲呼入号*/
    private String mInterMeetingNo;
    /**实时对讲参与成员*/
    private List<ECInterPhoneMeetingMember> mInterMembers = new ArrayList<ECInterPhoneMeetingMember>();
    /**实时对讲成员状态信息适配器*/
    private InterPhoneMemberAdapter mInterAdapter;
    /**实时对讲在线人数统计*/
    private int onLineCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.inter_phone_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // 实时对讲会议号（加入）
        mInterMeetingNo = getIntent().getStringExtra(ECDevice.MEETING_NO);
        // 如果是创建实时对讲会议
        String[] members = getIntent().getStringArrayExtra(EXTRA_MEMBERS);

        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt , -1 ,R.string.ec_app_title_inter_phone , this);
        initView();

        if(mInterMeetingNo == null && members == null) {
            throw new IllegalArgumentException("create Inter phone error . meetingNo "
                    + mInterMeetingNo + " , members " + members);
        }
        initProwerManager();
        if(mInterMeetingNo != null) {
            // 如果有实时对讲会议号，则加入实时对讲
            InterPhoneHelper.joinInterPhone(mInterMeetingNo);
            return ;
        }
        initInviteMembers(members);
        // 开始发起实时对讲请求
        InterPhoneHelper.startInterphone(members);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onLineCount=0;
    }
    
    

    @Override
    public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {
        super.onReceiveInterPhoneMeetingMsg(msg);
        if(msg == null  || (mInterMeetingNo != null && !mInterMeetingNo.equals(msg.getMeetingNo()))) {
            LogUtil.e(TAG, "onReceiveInterPhoneMeetingMsg error msg " + msg + " , no " + msg.getMeetingNo());
            return ;
        }
        if(mInterMembers == null) {
            mInterMembers = new ArrayList<ECInterPhoneMeetingMember>();
        }
        boolean handle = convertToInterPhoneMeetingMember(msg);
        // 是否列表数据有改变

        if(onLineCount<=0){
            onLineCount=0;
        }
        mBannerView.setOnLineCount(getOnLineCount(), mInterMembers.size());

        if(handle && mInterAdapter != null) {
            mInterAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected boolean isEnableSwipe() {
        return false;
    }

    /**
     * 转换成成员消息
     * @param msg
     * @return
     */
    private boolean convertToInterPhoneMeetingMember(ECInterPhoneMeetingMsg msg) {
        if(msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.JOIN) {
            ECInterPhoneJoinMsg joinMsg = (ECInterPhoneJoinMsg) msg;
            if(joinMsg != null) {
                // 有人加入会议消息
                for(ECInterPhoneMeetingMember member : mInterMembers) {
                    if(member != null
                            && member.getMember() != null
                            && member.getMember().equals(joinMsg.getWho())) {
                        member.setOnline(ECInterPhoneMeetingMember.Online.ONLINE);
                        updateTopMeetingBarTips(getString(R.string.str_chatroom_join, joinMsg.getWho()));
                        onLineCount++;
                        return true;
                    }
                }
            }
            return false;
        }

        // 实时对讲有人退出
        if(msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.EXIT) {
            ECInterPhoneExitMsg exitMsg = (ECInterPhoneExitMsg) msg;
            // 有人退出会议消息
            for(ECInterPhoneMeetingMember member : mInterMembers) {
                if(member != null
                        && member.getMember() != null
                        && member.getMember().equals(exitMsg.getWho())) {
                    member.setOnline(ECInterPhoneMeetingMember.Online.UN_ONLINE);
                    updateTopMeetingBarTips(getString(R.string.str_quit_inter_phone, member.getMember()));
                    onLineCount--;
                    return true;
                }
            }
        }

        // 实时对讲有人控麦
        if(msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.CONTROL_MIC) {
            ECInterPhoneControlMicMsg controlMicMsg = (ECInterPhoneControlMicMsg) msg;
            // 改变成员状态
            for(ECInterPhoneMeetingMember member : mInterMembers) {
                if(member != null
                        && member.getMember() != null
                        && member.getMember().equals(controlMicMsg.getWho())) {
                    member.setMic(ECInterPhoneMeetingMember.Mic.MIC_CONTROLLER);
                    updateTopMeetingBarTips(getString(R.string.str_speaking, member.getMember()));
                    return true;
                }
            }
        }

        // 实时对讲有人结束控麦
        if(msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.RELEASE_MIC) {
            ECInterPhoneReleaseMicMsg releaseMicMsg = (ECInterPhoneReleaseMicMsg) msg;
            // 改变成员状态
            for(ECInterPhoneMeetingMember member : mInterMembers) {
                if(member != null
                        && member.getMember() != null
                        && member.getMember().equals(releaseMicMsg.getWho())) {
                    member.setMic(ECInterPhoneMeetingMember.Mic.MIC_UN_CONTROLLER);
                    updateTopMeetingBarTips(getString(R.string.str_can_control_mic, member.getMember()));
                    return true;
                }
            }
        }

        // 实时对讲已结束
        if(msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.OVER) {
            ToastUtil.showMessage("实时对讲结束[" + mInterMeetingNo + "]");
            finish();
        }
        return false;

    }

    private int getOnLineCount(){

        int i=0;
        for(ECInterPhoneMeetingMember member : mInterMembers) {
            if(member != null
                    && member.getMember() != null
                    && member.getOnline()==ECInterPhoneMeetingMember.Online.ONLINE) {

                i++;
            }
        }
        return i;
    }

    /**
     * 更新顶部会议状态通知描述
     * @param text 通知描述
     */
    private void updateTopMeetingBarTips(String text) {
        if(mBannerView != null) {
            mBannerView.setTips(text);
        }
        // 3秒后顶部会议状态栏通知恢复
        ECHandlerHelper.postDelayedRunnOnUI(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    if (mInterMeetingNo != null) {
                        mBannerView.setTips(getString(R.string.current_meeting_voice, mInterMeetingNo));
                    } else {
                        mBannerView.setTips(R.string.current_meeting_tips);
                    }
                }
            }
        }, 3000L);
    }

    /**
     * 初始化创建实时对讲邀请的成员
     * @param members 实时对讲初始成员
     */
    private void initInviteMembers(String[] members) {
        if(members == null) {
            return ;
        }
        if(mInterMembers == null) {
            mInterMembers = new ArrayList<ECInterPhoneMeetingMember>();
        }
        boolean containSelf = false;
        for(String member : members) {
            ECInterPhoneMeetingMember meetingMember = new ECInterPhoneMeetingMember(member);
            meetingMember.setMic(ECInterPhoneMeetingMember.Mic.MIC_UN_CONTROLLER);
            meetingMember.setOnline(ECInterPhoneMeetingMember.Online.UN_ONLINE);
            if(member.equals(CCPAppManager.getClientUser().getUserId())) {
                // 如果是自己
                meetingMember.setType(ECInterPhoneMeetingMember.Type.SPONSOR);
                mInterMembers.add(0 , meetingMember);
                containSelf = true;
            } else {
                meetingMember.setType(ECInterPhoneMeetingMember.Type.PARTICIPANT);
                mInterMembers.add(meetingMember);
            }
        }
        if(!containSelf) {
            // 将自己添加到列表中
            ECInterPhoneMeetingMember meetingMember = new ECInterPhoneMeetingMember(CCPAppManager.getUserId());
            meetingMember.setMic(ECInterPhoneMeetingMember.Mic.MIC_UN_CONTROLLER);
            meetingMember.setOnline(ECInterPhoneMeetingMember.Online.UN_ONLINE);
            meetingMember.setType(ECInterPhoneMeetingMember.Type.SPONSOR);
            mInterMembers.add(meetingMember);
        }

        mBannerView.setCount(mInterMembers.size());
    }

    /**
     * 初始化资源
     */
    private void initView() {
        mBannerView = (InterPhoneBannerView) findViewById(R.id.notice_tips_ly);
        mInterPhoneListView = (ListView) findViewById(R.id.inter_phone_list);
        mMicController = (InterPhoneMicController) findViewById(R.id.inter_phone_speak_ly);
        mMicController.setOnInterPhoneMicListener(this);
        mInterAdapter = new InterPhoneMemberAdapter(this , mInterMembers);
        mInterPhoneListView.setAdapter(mInterAdapter);
        updateTopMeetingBarTips(getString(R.string.top_tips_connecting_wait));
    }


    @Override
    public void onInterPhoneStart(String interNo) {
        if(interNo == null || (mInterMeetingNo != null && !mInterMeetingNo.equals(interNo))) {
            return ;
        }

        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        if(setupManager != null) {
            setupManager.enableLoudSpeaker(true);
        }
        // 缓存实时对讲会议号
        mInterMeetingNo = interNo;
        if(mInterMembers == null || mInterMembers.isEmpty()) {
            // 如果会议成员为空，则查询会议成员
            InterPhoneHelper.queryInterPhoneMember(mInterMeetingNo);
        } else {
            mInterAdapter.notifyDataSetChanged();
        }
        if(mMicController != null) {
            mMicController.setInterSpeakEnabled(true);
        }
        updateTopMeetingBarTips(CCPAppManager.getUserId() + getString(R.string.str_join_inter_phone_success));
        mBannerView.setOnLineCount(getOnLineCount(), mInterMembers.size());
    }

    @Override
    public void onInterPhoneMembers(List<ECInterPhoneMeetingMember> members) {
        if(mInterMembers == null) {
            mInterMembers = new ArrayList<ECInterPhoneMeetingMember>();
        }
        if(members != null) {
            mInterMembers.addAll(members);
        }
        mBannerView.setCount(mInterMembers.size());
        mBannerView.setOnLineCount(getOnLineCount(), mInterMembers.size());
        mInterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInterPhoneError(ECError e) {
        if(e.errorCode == 111609) {
            // 实时对讲房间号码异常
            InterPhoneHelper.exitInterPhone();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                exitInterPhone();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            exitInterPhone();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理退出实时对讲操作
     */
    private void exitInterPhone() {
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.talk_room_exit_room_tip
                , R.string.app_exit,R.string.app_cancel , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MeetingHelper.exitMeeting();
                setResult(RESULT_OK);
                finish();
            }
        }, null);
        buildAlert.setTitle(R.string.talk_room_exit_room);
        buildAlert.show();
    }

    @Override
    public void onPrepareControlMic() {
        try {
            ECNotificationManager.getInstance().playNotificationMusic("inter_phone_pressed.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterIncallMode();
    }

    @Override
    public void onControlMic() {
        mBannerView.setTips(R.string.top_tips_connecting_wait);
        controlOrReleaseMic(true);
    }

    @Override
    public void onReleaseMic() {
        controlOrReleaseMic(false);
        releaseWakeLock();
        try {
            ECNotificationManager.getInstance().playNotificationMusic("inter_phone_up.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBannerView.setTips(R.string.top_tips_intercom_ing);
    }

    /**
     * 是否控麦或者放麦
     * @param control
     */
    private boolean controlOrReleaseMic(boolean control) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            return false;
        }
        if(control) {
            meetingManager.controlMicInInterPhoneMeeting(mInterMeetingNo, new ECMeetingManager.OnControlMicInInterPhoneListener() {
                @Override
                public void onControlMicState(ECError reason, String speaker) {
                    if(SdkErrorCode.REQUEST_SUCCESS == reason.errorCode) {
                        // 实时对讲抢麦成功
                        try {
                            ECNotificationManager.getInstance().playNotificationMusic("inter_phone_connect.mp3");
                            //手机震动
                            DemoUtils.shakeControlMic(InterPhoneChatActivity.this, true);
                            updateTopMeetingBarTips(getString(R.string.str_control_mic_success));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 更新状态
                        sendControlMsgCommand(true ,CCPAppManager.getUserId());
                        updateTopMeetingBarTips(getString(R.string.str_control_mic_success));
                        mMicController.setControlMicType(InterPhoneMicController.MicType.CONTROL);
                        return;
                    }
                    updateTopMeetingBarTips(getString(R.string.str_control_mic_failed));
                    mMicController.setControlMicType(InterPhoneMicController.MicType.ERROR);
                }
            });
            return true;
        }

        meetingManager.releaseMicInInterPhoneMeeting(mInterMeetingNo, new ECMeetingManager.OnReleaseMicInInterPhoneListener() {
            @Override
            public void onReleaseMicState(ECError reason) {
                if (SdkErrorCode.REQUEST_SUCCESS == reason.errorCode) {
                    // 实时对讲放麦成功
                    mMicController.setControlMicType(InterPhoneMicController.MicType.IDLE);
                    // 更新状态
                    sendControlMsgCommand(false, CCPAppManager.getUserId());
                    return;
                }
                updateTopMeetingBarTips(getString(R.string.str_release_mic_failed));
            }
        });

        return false;
    }

    /**
     * 更改状态
     * @param userId
     */
    private void sendControlMsgCommand(boolean control , String userId) {
        ECInterPhoneMeetingMsg meetingMsg;
        if(control) {
            ECInterPhoneControlMicMsg controlMicMsg = new ECInterPhoneControlMicMsg(ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.CONTROL_MIC);
            controlMicMsg.setMeetingNo(mInterMeetingNo);
            controlMicMsg.setWho(userId);
            meetingMsg = controlMicMsg;
        } else {
            ECInterPhoneReleaseMicMsg releaseMicMsg = new ECInterPhoneReleaseMicMsg(ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.RELEASE_MIC);
            releaseMicMsg.setMeetingNo(mInterMeetingNo);
            releaseMicMsg.setWho(userId);
            meetingMsg = releaseMicMsg;
        }
        onReceiveInterPhoneMeetingMsg(meetingMsg);
    }

    /**
     * 实时对讲成员列表适配器
     * 对实时对讲成员各种状态信息（加入或者未加入、控麦或者非控麦）进行区分显示
     */
    public class InterPhoneMemberAdapter extends ArrayAdapter<ECInterPhoneMeetingMember> {
        public InterPhoneMemberAdapter(Context context,List<ECInterPhoneMeetingMember> objects) {
            super(context, 0, objects);
        }

        /**
         * 更新列表成员
         * @param objects
         */
        public void setMembers(List<ECInterPhoneMeetingMember> objects) {
            clear();

            if(objects == null) {
                return ;
            }
            for(ECInterPhoneMeetingMember meetingMember : objects) {
                add(meetingMember);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            InterPhoneItem mInterView ;
            if(convertView == null  || convertView.getTag() == null) {
                view = View.inflate(getContext(), R.layout.inter_phone_item, null);
                mInterView = new InterPhoneItem(view);
                view.setTag(mInterView);
            } else {
                view = convertView;
                mInterView = (InterPhoneItem) view.getTag();
            }

            ECInterPhoneMeetingMember meetingMember = mInterMembers.get(position);
            mInterView.setInterMember(meetingMember);
            return view;
        }
    }
}
