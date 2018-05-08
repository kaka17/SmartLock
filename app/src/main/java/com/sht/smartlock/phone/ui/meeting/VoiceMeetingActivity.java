package com.sht.smartlock.phone.ui.meeting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.base.OverflowAdapter;
import com.sht.smartlock.phone.common.base.OverflowHelper;
import com.sht.smartlock.phone.common.dialog.ECAlertDialog;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.interphone.InterPhoneBannerView;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.meeting.ECMeeting;
import com.yuntongxun.ecsdk.meeting.ECMeetingMember;
import com.yuntongxun.ecsdk.meeting.ECVoiceMeetingMember;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingExitMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingJoinMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMemberForbidOpt;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingRejectMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingRemoveMemberMsg;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音会议界面
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/17.
 */
public class VoiceMeetingActivity extends MeetingBaseActivity implements VoiceMeetingMicAnim.OnMeetingMicEnableListener {

    private static final String TAG = "ECSDK_Demo.VoiceMeetingActivity";

    public static final String EXTRA_MEETING = "com.yuntongxun.ecdemo.Meeting";
    public static final String EXTRA_MEETING_PASS = "com.yuntongxun.ecdemo.Meeting_Pass";
    public static final String EXTRA_CALL_IN = "com.yuntongxun.ecdemo.Meeting_Join";
    /**管理会议成员*/
    public static final int REQUEST_CODE_KICK_MEMBER = 0x001;
    /**外呼电话邀请加入会议*/
    public static final int REQUEST_CODE_INVITE_BY_PHONECALL = 0x002;
    /**会议成员显示控件*/
    private GridView mGridView;
    /**会议成员显示控件适配器*/
    private MeetingMemberAdapter mMeetingMemberAdapter;
    /**语音会议加入状态通知*/
    private InterPhoneBannerView mInterPhoneBannerView;
    /**语音会议参与状态动画*/
    private VoiceMeetingCenter mVoiceCenter;
    /**语音会议底部Mic和声音振幅区域*/
    private VoiceMeetingMicAnim mMeetingMic;
    private TextView mMikeToast ;
    /**创建会议需要的参数*/
    private ECMeetingManager.ECCreateMeetingParams mParams;
    /**会议信息*/
    private ECMeeting mMeeting;
    /**会议房间密码*/
    private String mMeetingPassword;
    /**是否需要呼入会议*/
    private boolean mMeetingCallin;
    /**会议成员*/
    private List<ECVoiceMeetingMember> sMembers;
    /**是否是自己创建的会议房间*/
    private boolean isSelfMeeting = false;
    /**会议房间是否已经被解散*/
    private boolean isMeetingOver = false;
    private boolean isMeeting = false;
    /**是否是扬声器模式*/
    private boolean mSpeakerOn = false;
    /***会议成员是否有自己*/
    private boolean hasSelf = false;

    /**下拉菜单显示项目*/
    private OverflowAdapter.OverflowItem[] mItems ;
    /**右上角下拉菜单*/
    private OverflowHelper mOverflowHelper;

    private final AdapterView.OnItemClickListener mOverflowItemClicKListener
            = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            controlPlusSubMenu();
            doOverflowActionMenuClick(position);
        }

    };

    @Override
    protected int getLayoutId() {
        return R.layout.meeting_voice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMeeting = getIntent().getParcelableExtra(EXTRA_MEETING);
        mMeetingPassword = getIntent().getStringExtra(EXTRA_MEETING_PASS);
        mMeetingCallin = getIntent().getBooleanExtra(EXTRA_CALL_IN, false);
        initView();

        if(mMeeting == null) {
            LogUtil.e(TAG, " meeting error , meeting null");
            finish();
            return ;
        }
        setTopBar(CCPAppManager.getClientUser().getUserId().equals(mMeeting.getCreator()));
        isSelfMeeting = CCPAppManager.getUserId().equals(mMeeting.getCreator());
        // 创建一个下拉菜单
        mOverflowHelper = new OverflowHelper(this);
        initOverflowItems();

        if(mMeetingCallin) {
            // 判断是否需要调用加入接口加入会议
            mInterPhoneBannerView.setTips(R.string.top_tips_connecting_wait);
            MeetingHelper.joinMeeting(mMeeting.getMeetingNo(), mMeetingPassword);
            return ;
        }
        isMeeting = true;
        
    }

    /**
     * 设置状态栏
     * @param isCreator 是否会议创建者
     */
    private void setTopBar(boolean isCreator) {
        if(isCreator) {
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                    R.drawable.btn_style_green, null,
                    getString(R.string.app_title_right_button_pull_down),
                    mMeeting.getMeetingName(), null, this);
            return ;
        }
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.app_title_right_button_pull_down_1),
                mMeeting.getMeetingName(), null, this);

    }

    /**
     * 初始化界面资源
     */
    private void initView() {
        mInterPhoneBannerView = (InterPhoneBannerView) findViewById(R.id.notice_tips_ly);
        mVoiceCenter = (VoiceMeetingCenter) findViewById(R.id.meeting_speak_ly);
        mMeetingMic = (VoiceMeetingMicAnim) findViewById(R.id.bottom);
        mMeetingMic.setOnMeetingMicEnableListener(this);
        mGridView = (GridView) findViewById(R.id.chatroom_member_list);
        mMikeToast = (TextView) findViewById(R.id.mute_tips);
        mMeetingMemberAdapter = new MeetingMemberAdapter(this);
        mGridView.setAdapter(mMeetingMemberAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            doTitleLeftAction();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeAmplitude(true);
        mInterPhoneBannerView.setTips(CCPAppManager.getClientUser().getUserId() + getString(R.string.str_join_chatroom_success));

        if(isMeeting) {
            MeetingHelper.queryMeetingMembers(mMeeting.getMeetingNo());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        changeAmplitude(false);
    }

    private boolean isNeedGetData=true;
    @Override
    public void onMeetingStart(String meetingNo) {
        super.onMeetingStart(meetingNo);
        isMeeting = true;
        // 加入会议成功
        hasSelf = false;
        MeetingHelper.queryMeetingMembers(meetingNo);

        changeAmplitude(true);
        
    }
    
    @Override
	protected boolean isEnableSwipe() {
		// TODO Auto-generated method stub
		return false;
	}

    private void changeAmplitude(boolean enbale) {
        if(!isMeeting && enbale) {
            return ;
        }
        if(mVoiceCenter != null) {
            if(enbale) {
                mVoiceCenter.startAmplitude();
            } else {
                mVoiceCenter.stopAmplitude();
            }
        }
        if(mMeetingMic != null) {
            if(enbale) {
                mMeetingMic.startMicAmpl();
            } else {
                mMeetingMic.stopMicAmpl();
            }
        }
    }

    /**
     * 初始化下拉菜单
     */
    void initOverflowItems() {
        int size = isSelfMeeting? 5 : 1;

        if (mItems == null) {
            mItems = new OverflowAdapter.OverflowItem[size];
        }
        if(isSelfMeeting) {

            mItems[0] = new OverflowAdapter.OverflowItem(getString(R.string.pull_invited_phone_member));
            mItems[1] = new OverflowAdapter.OverflowItem(getString(R.string.videomeeting_invite_voip));
            if(mSpeakerOn) {
                mItems[2] = new OverflowAdapter.OverflowItem(getString(R.string.pull_mode_earpiece));
            } else  {
                mItems[2] = new OverflowAdapter.OverflowItem(getString(R.string.pull_mode_speaker));
            }
            mItems[3] = new OverflowAdapter.OverflowItem(getString(R.string.pull_manager_member));
            mItems[4] = new OverflowAdapter.OverflowItem(getString(R.string.pull_dissolution_room));
        } else {
            if(mSpeakerOn) {
                mItems[0] = new OverflowAdapter.OverflowItem(getString(R.string.pull_mode_earpiece));

            } else  {
                mItems[0] = new OverflowAdapter.OverflowItem(getString(R.string.pull_mode_speaker));
            }
        }
    }

    /**
     * 当前下拉菜单点击事件
     * @param position
     */
    void doOverflowActionMenuClick(int position) {
        if(!isSelfMeeting) {
            // 如果不是创建者，则只有切换听筒扬声器模式
            if(position == 0) {
                // 更改当前的扬声器模式
                changeSpeakerOnMode();
            }
            return ;
        }

        if(position == 0 || position == 1) {
            if(mMeeting == null && mMeeting.getMeetingNo() == null) {
                return ;
            }
            // 外呼电话邀请加入会议
            Intent callByPhone = new Intent(VoiceMeetingActivity.this, InviteByPhoneCall.class);
            
            callByPhone.putExtra(ECDevice.MEETING_NO, mMeeting.getMeetingNo());
            callByPhone.putExtra("isLandingCall", position == 0);
            startActivityForResult(callByPhone, REQUEST_CODE_INVITE_BY_PHONECALL);
        }else if (position == 2) {
            // 更改当前的扬声器模式
            changeSpeakerOnMode();
        } else if (position == 3) {
            if(mMeeting == null && mMeeting.getMeetingNo() == null) {
                return ;
            }
            // 管理会议成员操作
            Intent intent = new Intent(VoiceMeetingActivity.this, VoiceMeetingMemberManager.class);
            intent.putExtra(EXTRA_MEETING, mMeeting);
            startActivityForResult(intent, REQUEST_CODE_KICK_MEMBER);
        } else if (position == 4) {
            // 解散会议操作
            ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.dialog_message_dissmiss_chatroom
                    , R.string.dailog_button_dissmiss_chatroom,R.string.app_cancel , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doExitChatroomAction();
                }
            }, null);
            buildAlert.setTitle(R.string.dialog_title_dissmiss_chatroom);
            buildAlert.show();
        }
        // 如果是创建者，有管理权限
    }

    /**
     * 更改当前的扬声器模式
     */
    private void changeSpeakerOnMode() {
        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
        if(setupManager == null) {
            return ;
        }
        boolean speakerOn = setupManager.getLoudSpeakerStatus();
        setupManager.enableLoudSpeaker(!speakerOn);
        mSpeakerOn = setupManager.getLoudSpeakerStatus();
        if(mSpeakerOn) {
            ToastUtil.showMessage(R.string.fmt_route_speaker);
        } else {
            ToastUtil.showMessage(R.string.fmt_route_phone);
        }
    }

    /**
     * 控制菜单的显示和隐藏
     */
    private void controlPlusSubMenu() {
        if (mOverflowHelper == null) {
            return;
        }

        if (mOverflowHelper.isOverflowShowing()) {
            mOverflowHelper.dismiss();
            changeAmplitude(true);
            return;
        }
        changeAmplitude(false);
        mOverflowHelper.setOverflowItems(mItems);
        mOverflowHelper.setOnOverflowItemClickListener(mOverflowItemClicKListener);
        mOverflowHelper.showAsDropDown(findViewById(R.id.text_right));
    }


    /**
     * 停止界面动画
     */
    private void stopAmplitude() {
        if(mMeetingMic != null) {
            mMeetingMic.stopMicAmpl();
        }
        if(mVoiceCenter != null) {
            mVoiceCenter.stopAmplitude();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSelfMeeting=false;
        stopAmplitude();
    }
    private boolean isSelfMobile=false;

    @Override
    public void onMeetingMembers(List<? extends ECMeetingMember> members) {
        super.onMeetingMembers(members);
        sMembers = (List<ECVoiceMeetingMember>) members;
        if(sMembers != null) {
            for(ECVoiceMeetingMember mbr : sMembers) {
                if(mbr != null && mbr.getNumber().equals(CCPAppManager.getUserId())&&!mbr.isMobile()) {
                    hasSelf = true;
                }
            }
        }
        if(!hasSelf) {
            ECVoiceMeetingMember member = new ECVoiceMeetingMember();
            member.setNumber(CCPAppManager.getUserId());
            member.setIsMobile(false);
            if(sMembers == null) {
                sMembers = new ArrayList<ECVoiceMeetingMember>();
            }
            sMembers.add(member);
            LogUtil.e(TAG, " onMeetingMembers  add self");
            hasSelf = true;
        }
        

        if(mMeetingMemberAdapter == null) {
            mMeetingMemberAdapter = new MeetingMemberAdapter(this);
            mMeetingMemberAdapter.setMembers(sMembers);
            mGridView.setAdapter(mMeetingMemberAdapter);
        }
        mMeetingMemberAdapter.setMembers(sMembers);
    }

    @Override
    public void onError(int type , ECError e) {
        super.onError(type, e);
        dismissPostingDialog();
        if(MeetingHelper.OnMeetingCallback.MEETING_JOIN == type) {
            // 加入会议失败
            MeetingHelper.exitMeeting();
            finish();
        }
    }

    @Override
    public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {
        super.onReceiveVoiceMeetingMsg(msg);

        if(msg == null  || !(mMeeting != null && msg.getMeetingNo().equals(mMeeting.getMeetingNo()))) {
            LogUtil.e(TAG , "onReceiveVoiceMeetingMsg error msg " + msg + " , no " + msg.getMeetingNo());
            return ;
        }
        if(sMembers == null) {
            sMembers = new ArrayList<ECVoiceMeetingMember>();
        }
        boolean handle = convertToVoiceMeetingMember(msg);
        // 是否列表数据有改变
        if(handle && mMeetingMemberAdapter != null) {
            if(sMembers != null) {
                mMeetingMemberAdapter.setMembers(sMembers);
                mMeetingMemberAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onMeetingDismiss(String meetingNo) {
        super.onMeetingDismiss(meetingNo);
        isMeeting = false;
        if(mMeeting != null && meetingNo != null &&
                meetingNo.equals(mMeeting.getMeetingNo())) {
            dismissPostingDialog();
            finish();
        }
    }
    
    
    

    /**
     * 处理会议Mic动作
     * @param enable
     */
    private void notifyMeetingMikeEnable(boolean enable) {
        if(mMeetingMic != null) {
            mMeetingMic.setMikeEnable(enable);
        }
    }

    /**
     * 更新顶部会议状态通知描述
     * @param text 通知描述
     */
    private void updateTopMeetingBarTips(String text) {
        if(mInterPhoneBannerView != null) {
            mInterPhoneBannerView.setTips(text);
        }
        // 3秒后顶部会议状态栏通知恢复
        ECHandlerHelper.postDelayedRunnOnUI(new Runnable() {
            @Override
            public void run() {
                if(mInterPhoneBannerView != null) {
                    if(mMeeting != null) {
                        mInterPhoneBannerView.setTips(getString(R.string.current_meeting_voice , mMeeting.getMeetingName()));
                    } else {
                        mInterPhoneBannerView.setTips(R.string.current_meeting_tips);
                    }
                }
            }
        } , 3000L);
    }

    /**
     * 转换成成员消息
     * @param msg
     */
    private boolean convertToVoiceMeetingMember(ECVoiceMeetingMsg msg) {
        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.JOIN) {
            ECVoiceMeetingJoinMsg joinMsg = (ECVoiceMeetingJoinMsg) msg;
            // 有人加入会议消息
            for(String who : joinMsg.getWhos()) {
                LogUtil.e(TAG , " hasSelf :" + hasSelf);
                if(who.equals(CCPAppManager.getUserId()) && hasSelf&&(!joinMsg.isMobile())) {
                    LogUtil.e(TAG , " hasSelf true");
                    continue;
                }
                ECVoiceMeetingMember member = new ECVoiceMeetingMember();
                member.setNumber(who);
                member.setIsMobile(joinMsg.isMobile());
                if(!isMemberExist(member)){
                  sMembers.add(member);
                }
                LogUtil.e(TAG, " hasSelf " + who);
                updateTopMeetingBarTips(getString(R.string.str_chatroom_join, member.getNumber()));
            }
            return true;
        }

        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.EXIT) {
            ECVoiceMeetingExitMsg exitMsg = (ECVoiceMeetingExitMsg) msg;
            // 有人退出会议消息
            List<ECVoiceMeetingMember> exitMembers = new ArrayList<ECVoiceMeetingMember>();
            for(ECVoiceMeetingMember member : sMembers) {
                if(member != null && member.getNumber() != null) {
                    for(String who : exitMsg.getWhos()) {
                        if(member.getNumber().equals(who)&&(exitMsg.isMobile()==member.isMobile())) {
                        	exitMembers.add(member);
                        }
                    }
                }
            }
            if(exitMembers.size() > 0) {
                isMeetingOver = false;
                sMembers.removeAll(exitMembers);
                updateTopMeetingBarTips(getString(R.string.str_chatroom_exit, exitMembers.get(0).getNumber()));
            }
            return true;
        }

        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.REMOVE_MEMBER) {
            ECVoiceMeetingRemoveMemberMsg removeMemberMsg = (ECVoiceMeetingRemoveMemberMsg) msg;
            // 有成员被移除出会议消息
            if((!removeMemberMsg.isMobile())&&removeMemberMsg != null
                    && CCPAppManager.getClientUser().getUserId().equals(removeMemberMsg.getWho())) {
                // 如果被移除出会议的成员是自己
                // 提示被移除出聊天室对话框
                isMeetingOver = false;
                showRemovedFromChatroomAlert();
                return false;
            } else {
                // 如果移除的成员是其他人
                ECVoiceMeetingMember rMember = null;
                for(ECVoiceMeetingMember member : sMembers) {
                    String number = member.getNumber();
                    if(member != null && number != null && (member.isMobile()==removeMemberMsg.isMobile())&&number.equals(removeMemberMsg.getWho())) {
                        rMember = member;
                        break;
                    }
                }
                if(rMember != null) {
                    sMembers.remove(rMember);
                    // 刷新会议通知栏
                    updateTopMeetingBarTips(getString(R.string.str_chatroom_kick, rMember.getNumber()));
                }
                return true;
            }
        }

        // 处理会议房间被解散消息
        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.DELETE) {
            if(isSelfMeeting && isMeetingOver) {
                // 不需要处理
                return false;
            }
            onMeetingRoomDel(msg);
            return false;
        }

        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.SPEAK_OPT) {
            // 处理会议成员被禁言操作
            doChatroomMemberForbidOpt((ECVoiceMeetingMemberForbidOpt) msg);
        }

        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.REJECT) {
            // 处理对方拒绝邀请加入请求
            ECVoiceMeetingRejectMsg rejectMsg = (ECVoiceMeetingRejectMsg) msg;
            onVoiceMeetingRejectMsg(rejectMsg);
        }

        return false;
    }
    
    
    private boolean isMemberExist(ECVoiceMeetingMember member){
    	for(ECVoiceMeetingMember item:sMembers){
        	if(item!=null&&(item.getNumber().equals(member.getNumber()))&&(item.isMobile()==member.isMobile())){
        		return true;
        	}
        }
    	return false;
    	
    }

    private void onVoiceMeetingRejectMsg(ECVoiceMeetingRejectMsg msg) {
        if(msg == null) {
            return ;
        }
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(VoiceMeetingActivity.this
                , getString(R.string.meeting_invite_reject , msg.getWho()),
                getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        buildAlert.setTitle(R.string.app_tip);
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.show();
    }


    /**
     * 处理会议成员被禁言
     * @param forbidOpt
     * @return
     */
    private boolean doChatroomMemberForbidOpt(ECVoiceMeetingMemberForbidOpt forbidOpt) {
        if(forbidOpt == null) {
            return false;
        }
        String userId = CCPAppManager.getUserId();
        ECVoiceMeetingMsg.ForbidOptions options = forbidOpt.getForbid();
        if(options.inSpeak == ECVoiceMeetingMsg.ForbidOptions.OPTION_SPEAK_LIMIT && userId.equals(forbidOpt.getMember())) {
            ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.chatroom_forbid_speak_message
                    , R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doExitChatroomAction();
                }
            });
            buildAlert.setTitle(R.string.app_tip);
            buildAlert.show();
        }
        updateTopMeetingBarTips(getString(R.string.top_tips_chatroom_disforbid, forbidOpt.getMember()));
        return false;
    }

    /**
     * 处理会议房间被解散消息
     * @param msg 会议被解散的消息
     */
    private void onMeetingRoomDel(ECVoiceMeetingMsg msg) {

        if (isFinishing()) {
            return;
        }
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.dialog_message_be_dissmiss_chatroom
                , R.string.settings_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doExitChatroomAction();
            }
        });
        buildAlert.setTitle(R.string.dialog_title_be_dissmiss_chatroom);
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.setCancelable(false);
        buildAlert.show();
        // 处理Mic状态
        notifyMeetingMikeEnable(true);
        updateTopMeetingBarTips(getString(R.string.dialog_title_be_dissmiss_chatroom));
    }

    /**
     * 处理退出会议按钮事件
     */
    private void doTitleLeftAction() {
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.chatroom_room_exit_room_tip
                , R.string.settings_logout,R.string.app_cancel , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitOrDismissChatroom(false);
            }
        }, null);
        buildAlert.setTitle(R.string.chatroom_room_exit_room);
        buildAlert.show();
    }

    /**
     * 处理会议退出逻辑
     */
    private void doExitChatroomAction() {
        notifyMeetingMikeEnable(true);
        if(isSelfMeeting) {
            exitOrDismissChatroom(true);
        } else {
            // Here is the receipt dissolution news, not so directly off the Page Creator
            finish();
        }
    }

    /**
     * 处理退出会议逻辑
     * @param exit 是否退出或者解散
     */
    private void exitOrDismissChatroom(boolean exit) {
        if(!exit) {
            // 处理会议退出
            MeetingHelper.exitMeeting();

            ECHandlerHelper.postDelayedRunnOnUI(new Runnable() {

                @Override
                public void run() {

                    finish();
                }
            }, 2000);
        } else {
            showProcessDialog();
            if(mMeeting != null) {
            	disMeeting(mMeeting.getMeetingNo());
                isMeetingOver = true;
                isMeeting = false;
            }
        }
    }
    
    public void disMeeting(String meetingNo){
    	
    	ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.deleteMultiMeetingByType(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE, meetingNo,
                new ECMeetingManager.OnDeleteMeetingListener() {
                    @Override
                    public void onMeetingDismiss(ECError reason, String meetingNo) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                           
                        	isMeeting = false;
                            if(mMeeting != null && meetingNo != null &&
                                    meetingNo.equals(mMeeting.getMeetingNo())) {
                                dismissPostingDialog();
                                finish();
                            }
                            return;
                        }
                        ToastUtil.showMessage("解散会议失败,错误码"+reason.errorCode);
                    }
                });
    	
    }

    /**
     * 提示被移除出聊天室对话框
     */
    private void showRemovedFromChatroomAlert() {
        if(isFinishing()) {
            return ;
        }
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.dialog_message_be_kick_chatroom
                , R.string.dialog_btn_confim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyMeetingMikeEnable(true);
                finish();
            }
        });
        buildAlert.setTitle(R.string.dialog_title_be_kick_chatroom);
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.setCancelable(false);
        buildAlert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                doTitleLeftAction();
                break;
            case R.id.text_right:
                initOverflowItems();
                controlPlusSubMenu();
                break;
        }
    }


    @Override
    public void onMeetingMicEnable(boolean enable) {
        mMikeToast.setText(enable ? R.string.str_chatroom_mike_disenable : R.string.str_chatroom_mike_enable);
    }

    public class MeetingMemberAdapter extends ArrayAdapter<ECVoiceMeetingMember> {

        public MeetingMemberAdapter(Context context) {
            super(context, 0, new ArrayList<ECVoiceMeetingMember>());
        }

        public void setMembers(List<? extends ECMeetingMember> members) {
            clear();
            if(members != null) {
                for(ECMeetingMember member :members) {
                    if(member instanceof  ECVoiceMeetingMember) {
                        if(member == null) {
                            continue;
                        }
                        if(((ECVoiceMeetingMember) member).getNumber().equals(CCPAppManager.getUserId())) {

                            super.insert((ECVoiceMeetingMember)member,0);
                        } else {
                            super.add((ECVoiceMeetingMember)member);
                        }
                    }
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            MeetingHolder holder;
            if (convertView == null|| convertView.getTag() == null) {
                view = getLayoutInflater().inflate(R.layout.list_item_meeting_member, null);
                holder = new MeetingHolder();
                view.setTag(holder);

                holder.name = (TextView) view.findViewById(R.id.member_name);
                holder.icon = (ImageView) view.findViewById(R.id.meeting_icon);
            } else {
                view = convertView;
                holder = (MeetingHolder) convertView.getTag();
            }
            ECVoiceMeetingMember member = getItem(position);
            if(member != null) {
                if(CCPAppManager.getClientUser().getUserId().equals(member.getNumber())) {
                    holder.icon.setImageResource(R.drawable.touxiang);
                } else {
                    holder.icon.setImageResource(R.drawable.status_uncreateor);
                }
                
                if(member.isMobile()){
                	holder.name.setText("m"+member.getNumber());
                }else {
                	holder.name.setText(member.getNumber());
                }
            }

            return view;
        }


        class MeetingHolder {
            TextView name;
            ImageView icon;
        }
    }
}
