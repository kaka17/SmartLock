package com.sht.smartlock.phone.ui.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.meeting.ECMeeting;
import com.yuntongxun.ecsdk.meeting.ECMeetingMember;
import com.yuntongxun.ecsdk.meeting.ECVoiceMeetingMember;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingExitMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingJoinMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音会议成员管理界面
 * Created by Jorstin on 2015/7/26.
 */
public class VoiceMeetingMemberManager extends MeetingBaseActivity {

    private static final String TAG = "ECSDK_Demo.VoiceMeetingMemberManager" ;

    private ListView mListView;
    /**会议成员列表*/
    private MeetingMemberAdapter mListAdapter;
    /**会议信息*/
    private ECMeeting mMeeting;
    /**是否移除过成员*/
    private boolean mRemoveMember;
    /**会议成员*/
    private List<ECVoiceMeetingMember> sMembers;

    @Override
    protected int getLayoutId() {
        return R.layout.voice_meeting_members;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeeting = getIntent().getParcelableExtra(VoiceMeetingActivity.EXTRA_MEETING);
        if(mMeeting == null) {
            finish();
            return;
        }
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, R.string.meeting_member_mgr_title, this);
        initView();
        // 查询会议成员
        MeetingHelper.queryMeetingMembers(mMeeting.getMeetingNo());
    }

    @Override
    public void onMeetingMembers(List<? extends ECMeetingMember> members) {
        super.onMeetingMembers(members);
        sMembers = (List<ECVoiceMeetingMember>) members;
        if(mListAdapter == null) {
            mListAdapter = new MeetingMemberAdapter(this);
            mListAdapter.setMembers(sMembers);
        }
        mListAdapter.setMembers(sMembers);
    }

    @Override
    public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {
        super.onReceiveVoiceMeetingMsg(msg);
        // 因为只有管理员才有权限管理会议成员
        // 所以这里只需要处理会议成员的加入和退出就可以了
        if(msg == null  || !(mMeeting != null && msg.getMeetingNo().equals(mMeeting.getMeetingNo()))) {
            LogUtil.e(TAG, "onReceiveVoiceMeetingMsg error msg " + msg + " , no " + msg.getMeetingNo());
            return ;
        }
        if(sMembers == null) {
            sMembers = new ArrayList<ECVoiceMeetingMember>();
        }
        boolean handle = convertToVoiceMeetingMember(msg);
        // 是否列表数据有改变
        if(handle && mListAdapter != null) {
            if(sMembers != null) {
                mListAdapter.setMembers(sMembers);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 初始化界面资源
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.meeting_member_lv);
        View emptyView = findViewById(R.id.empty_tip_recommend_bind_tv);
        mListAdapter = new MeetingMemberAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemLongClickListener(null);
        mListView.setEmptyView(emptyView);
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
                ECVoiceMeetingMember member = new ECVoiceMeetingMember();
                member.setNumber(who);
                sMembers.add(member);
            }
            return true;
        }

        if(msg.getMsgType() == ECVoiceMeetingMsg.ECVoiceMeetingMsgType.EXIT) {
            ECVoiceMeetingExitMsg exitMsg = (ECVoiceMeetingExitMsg) msg;
            // 有人退出会议消息
            ECVoiceMeetingMember exitMember = null;
            for(ECVoiceMeetingMember member : sMembers) {
                if(member != null
                        && member.getNumber() != null
                        && member.getNumber().equals(exitMsg.getWhos())) {
                    exitMember = member;
                    break;
                }
            }
            if(exitMember != null) {
                sMembers.remove(exitMember);
            }
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                setResultOk();
                break;
        }
    }

    /**
     * 设置返回更新
     */
    private void setResultOk() {
        Intent intent = new Intent(VoiceMeetingMemberManager.this, VoiceMeetingActivity.class);
        intent.putExtra("isKicked", mRemoveMember);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 处理移除会议成员操作
     * @param position 成员所在列表位置
     */
    private void doRemoveMeetingMember(final int position) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            return ;
        }
        if(mListAdapter != null) {
            final ECVoiceMeetingMember meetingMember = mListAdapter.getItem(position);
            if(meetingMember == null) {
                return ;
            }
            
            
            showProcessDialog();
            meetingManager.removeMemberFromMultiMeetingByType(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE,
                    mMeeting.getMeetingNo() ,meetingMember.getNumber(), meetingMember.isMobile() ,new ECMeetingManager.OnRemoveMemberFromMeetingListener() {
                        @Override
                        public void onRemoveMemberFromMeeting(ECError reason, String member) {
                            dismissPostingDialog();
                            if(SdkErrorCode.REQUEST_SUCCESS == reason.errorCode) {
                                if(sMembers == null) {
                                    return ;
                                }
        
                              sMembers.remove(position);
                              
                                if(mListAdapter!=null){
                                	mListAdapter.setMembers(sMembers);
                                }
                                return ;
                            }
                            ToastUtil.showMessage("移除会议成员失败[" + reason.errorCode + "]");
                        }
                    });
        }
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
                        super.add((ECVoiceMeetingMember)member);
                    }
                }
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder mViewHolder;
            if(convertView == null || convertView.getTag() == null) {
                view = View.inflate(getContext() , R.layout.meeting_member_item, null);

                mViewHolder = new ViewHolder();
                mViewHolder.mNikeName = (TextView) view.findViewById(R.id.meeting_contact_item_nick_tv);
                mViewHolder.mPermission = (TextView) view.findViewById(R.id.meeting_contact_item_digest_tv);
                mViewHolder.mOperatekick = (Button) view.findViewById(R.id.chatroom_contact_kick_ok_btn);
                mViewHolder.mOperateMute = (Button) view.findViewById(R.id.chatroom_contact_mute_btn);
                mViewHolder.mAvatar = (ImageView) view.findViewById(R.id.content);
                view.setTag(mViewHolder);
            } else {
                view = convertView;
                mViewHolder = (ViewHolder) view.getTag();
            }
            final ECVoiceMeetingMember item = getItem(position);
            if(item != null) {
            	
            	if(item.isMobile()){
            		mViewHolder.mNikeName.setText("m"+item.getNumber());
            	}else {
            		mViewHolder.mNikeName.setText(item.getNumber());
            	}

                if(CCPAppManager.getUserId().equals(item.getNumber())&&!item.isMobile()) {
                    mViewHolder.mOperatekick.setVisibility(View.GONE);
                    mViewHolder.mOperateMute.setVisibility(View.GONE);
                    return view;
                }
                mViewHolder.mOperatekick.setVisibility(View.VISIBLE);
                mViewHolder.mOperateMute.setVisibility(View.GONE);

                final ECVoiceMeetingMsg.ForbidOptions options = item.getForbid();
                if(options.inSpeak == ECVoiceMeetingMsg.ForbidOptions.OPTION_SPEAK_FREE) {
                    mViewHolder.mOperateMute.setText(R.string.chatroom_permission_mute);
                } else {
                    mViewHolder.mOperateMute.setText(R.string.chatroom_permission_mute_revert);
                }
                mViewHolder.mOperatekick.setText(R.string.chatroom_permission_remove);
                mViewHolder.mOperatekick.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        doRemoveMeetingMember(position);
                    }
                });
            }
            return view;

        }


        class ViewHolder {
            TextView mNikeName;
            ImageView mAvatar;
            TextView mPermission;
            Button mOperateMute;
            Button mOperatekick;
        }
    }
}
