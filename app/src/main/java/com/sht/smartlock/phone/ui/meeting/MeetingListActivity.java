package com.sht.smartlock.phone.ui.meeting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECListDialog;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.meeting.ECMeeting;

import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/15.
 */
public class MeetingListActivity extends MeetingBaseActivity{

    private static final String TAG = "ECSDK_Demo.MeetingListActivity";
    /**选择联系人*/
    public static final int SELECT_USER_FOR_CHATROOM = 0x002;
    /**创建群聊房间*/
    public static final int REQUEST_CODE_CREATE = 0x003;
    /**会议房间列表*/
    private ListView mMeetingListView;
    /**会议列表适配器*/
    private MeetingAdapter meetingAdapter;
    /**创建的会议是否自动加入*/
    private boolean mMeetingAutoJoin = false;
    /**创建会议参数*/
    private ECMeetingManager.ECCreateMeetingParams mMeetingParams;
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(meetingAdapter != null) {
                final ECMeeting meeting = meetingAdapter.getItem(position);
                if(meeting == null) {
                    ToastUtil.showMessage(R.string.meeting_voice_room_error);
                    return ;
                }
                // 如果自己是会议的创建者
                if(CCPAppManager.getClientUser().getUserId().equals(meeting.getCreator())) {
                    ECListDialog dialog = new ECListDialog(MeetingListActivity.this , R.array.meeting_control);
                    dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                        @Override
                        public void onDialogItemClick(Dialog d, int position) {
                            handleMeetingClick(meeting, position);
                        }
                    });
                    dialog.setTitle(meeting.getMeetingName());
                    dialog.show();
                    return ;
                }
                // 验证是否有加入权限
                startMeeting(meeting);
            }
        }
    };

    /**
     * 根据权限加入会议
     * @param meeting
     */
    private void startMeeting(ECMeeting meeting) {
        if(meeting.isValidate()) {
            // 会议加入需要验证
            showInputCodeDialog(meeting , null, getString(R.string.dialog_message_chatroom_auth_reason));
            return ;
        }

        doStartMeetingActivity(meeting , null);
    }

    @Override
    protected void handleInput(ECMeeting meeting, EditText editText) {
        super.handleInput(meeting, editText);
        if(editText != null) {
            String password = editText.getText().toString().trim();
            doStartMeetingActivity(meeting , password);
        }
    }

    /**
     * 处理点击会议加入操作
     * @param meeting 会议
     * @param position
     */
    private void handleMeetingClick(ECMeeting meeting, int position) {
        switch (position) {
            case 0:
                // 验证是否有加入权限
                startMeeting(meeting);
                break;
            case 1:
                if(meeting != null) {
                    // 解散会议
                    MeetingHelper.disMeeting(meeting.getMeetingNo());
                    
                }
                break;
            case 2:
                // 管理会议
                // 管理会议成员操作
                Intent intent = new Intent(MeetingListActivity.this, VoiceMeetingMemberManager.class);
                intent.putExtra(VoiceMeetingActivity.EXTRA_MEETING, meeting);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ec_interphone_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.str_btn_launch_chatroom),
                getString(R.string.ec_app_title_chatroom), null, this);
        initView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mMeetingListView = (ListView) findViewById(R.id.meeting_lv);
        TextView emptyView = (TextView) findViewById(R.id.empty_meeting_tv);
        emptyView.setText(R.string.ec_empty_voice_meeting);
        mMeetingListView.setEmptyView(emptyView);
        mMeetingListView.setOnItemClickListener(itemClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        boolean handle = MeetingHelper.queryMeetings(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE);
        if(handle) showCustomProcessDialog(getString(R.string.common_progressdialog_title));
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.text_right:
                
                Intent intent = new Intent(MeetingListActivity.this , CreateVoiceMeetingActivity.class);
                startActivityForResult(intent , REQUEST_CODE_CREATE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult: requestCode=" + requestCode
                + ", resultCode=" + resultCode + ", data=" + data);

       
        // If there's no data (because the user didn't select a picture and
        // just hit BACK, for example), there's nothing to do.
        if (requestCode == REQUEST_CODE_CREATE) {
            if (data == null) {
                finish();
                return;
            }
        } else if (resultCode != RESULT_OK) {
            LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
            finish();
            return;
        }
        if(REQUEST_CODE_CREATE == requestCode) {
            if(!data.hasExtra(CreateVoiceMeetingActivity.EXTRA_MEETING_PARAMS)) {
                LogUtil.e(TAG , "create meeting error params null");
                return ;
            }
            mMeetingParams = data.getParcelableExtra(CreateVoiceMeetingActivity.EXTRA_MEETING_PARAMS);
            if(mMeetingParams != null) {
                showProcessDialog();
                mMeetingAutoJoin = mMeetingParams.isAutoJoin();
                // 处理创建群聊房间
                MeetingHelper.startVoiceMeeting(mMeetingParams);
            }
        }
    }

    @Override
    public void onMeetingStart(String meetingNo) {
        super.onMeetingStart(meetingNo);
        dismissPostingDialog();
        MeetingHelper.queryMeetings(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE);
        if(mMeetingParams != null && mMeetingParams.isAutoJoin()) {
            // 自动加入会议的
            ECMeeting meeting = new ECMeeting();
            meeting.setMeetingNo(meetingNo);
            meeting.setMeetingName(mMeetingParams.getMeetingName());
            meeting.setCreator(CCPAppManager.getClientUser().getUserId());
            meeting.setJoined(1);
            doStartMeetingActivity(meeting, null, false);
        }
    }

    @Override
    public void onMeetingDismiss(String meetingNo) {
        super.onMeetingDismiss(meetingNo);
        dismissPostingDialog();
        if(meetingAdapter != null) {
            for(int i = 0 ; i < meetingAdapter.getCount() ; i++) {
                ECMeeting meeting = meetingAdapter.getItem(i);
                if(meeting != null && meeting.getMeetingNo() != null
                        && meeting.getMeetingNo().equals(meetingNo)) {
                    meetingAdapter.remove(meeting);
                    return ;
                }
            }
        }
    }

    @Override
    public void onError(int type, ECError e) {
        super.onError(type, e);
        dismissPostingDialog();
    }

    @Override
    public void onMeetings(List<ECMeeting> list) {
        super.onMeetings(list);
        dismissPostingDialog();
        if(list == null) {
            mMeetingListView.setAdapter(null);
            return ;
        }
        meetingAdapter = new MeetingAdapter(this , list);
        mMeetingListView.setAdapter(meetingAdapter);
    }

    public class MeetingAdapter extends ArrayAdapter<ECMeeting> {

        public MeetingAdapter(Context context, List<ECMeeting> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            MeetingHolder holder;
            if (convertView == null|| convertView.getTag() == null) {
                view = getLayoutInflater().inflate(R.layout.voice_meeting_item, null);
                holder = new MeetingHolder();
                view.setTag(holder);

                holder.name = (TextView) view.findViewById(R.id.chatroom_name);
                holder.tips = (TextView) view.findViewById(R.id.chatroom_tips);
                holder.lock = (ImageView) view.findViewById(R.id.lock);
            } else {
                view = convertView;
                holder = (MeetingHolder) convertView.getTag();
            }

            ECMeeting meeting = getItem(position);
            if(meeting != null) {
                holder.name.setText(meeting.getMeetingName());
                boolean meetingFill = (meeting.getJoined() == meeting.getSquare());
                // 当前会议是否满人
                int resId = meetingFill ? R.string.str_chatroom_list_join_full :R.string.str_chatroom_list_join_unfull;
                holder.tips.setText(getString(resId, meeting.getJoined() , meeting.getCreator()));
                // 会议是否需要验证加入
                holder.lock.setVisibility(meeting.isValidate() ? View.VISIBLE : View.GONE);
            }

            return view;

        }

        class MeetingHolder {
            TextView name;
            TextView tips;
            ImageView lock;
        }

    }
}
