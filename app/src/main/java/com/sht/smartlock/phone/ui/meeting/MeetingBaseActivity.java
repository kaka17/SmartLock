package com.sht.smartlock.phone.ui.meeting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.dialog.ECAlertDialog;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.MeetingMsgReceiver;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.meeting.ECMeeting;
import com.yuntongxun.ecsdk.meeting.ECMeetingMember;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;

import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/17.
 */
public abstract class MeetingBaseActivity extends ECSuperActivity
        implements MeetingHelper.OnMeetingCallback , View.OnClickListener , MeetingMsgReceiver.OnVoiceMeetingMsgReceive{


    private ECProgressDialog mPostingdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	MeetingMsgReceiver.addVoiceMeetingListener(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        MeetingHelper.addInterPhoneCallback(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MeetingHelper.removeInterPhoneCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MeetingHelper.removeInterPhoneCallback(this);
        MeetingMsgReceiver.removeVoiceMeetingListener(this);
    }

    @Override
    public void onMeetings(List<ECMeeting> list) {

    }

    @Override
    public void onError(int type ,ECError e) {
        ToastUtil.showMessage("请求错误[" + e.errorCode + "]");
    }

    @Override
    public void onMeetingStart(String meetingNo) {

    }

    @Override
    public void onMeetingDismiss(String meetingNo) {

    }

    @Override
    public void onMeetingMembers(List<? extends ECMeetingMember> members) {

    }

    @Override
    public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {

    }

    @Override
    public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
        }
    }

    /**
     * 跳转到会议聊天界面
     * @param meeting
     * @param pass
     */
    protected void doStartMeetingActivity(ECMeeting meeting ,String pass) {
        doStartMeetingActivity(meeting , pass , true);
    }

    /**
     * 跳转到会议聊天界面
     * @param meeting
     * @param pass
     */
    protected void doStartMeetingActivity(ECMeeting meeting ,String pass , boolean callin) {
        Intent intent = new Intent(MeetingBaseActivity.this , VoiceMeetingActivity.class);
        intent.putExtra(VoiceMeetingActivity.EXTRA_MEETING , meeting);
        if(!TextUtils.isEmpty(pass)) {
            intent.putExtra(VoiceMeetingActivity.EXTRA_MEETING_PASS , pass);
        }
        intent.putExtra(VoiceMeetingActivity.EXTRA_CALL_IN , callin);
        startActivity(intent) ;
    }

    protected void showInputCodeDialog(final ECMeeting meeting, String title, String message) {
        View view = View.inflate(this , R.layout.dialog_edit_context , null);
        final EditText editText = (EditText) view.findViewById(R.id.sendrequest_content);
        ((TextView) view.findViewById(R.id.sendrequest_tip)).setText(message);
        ECAlertDialog dialog = ECAlertDialog.buildAlert(this, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleInput(meeting , editText);
            }
        });
        dialog.setContentView(view);
        dialog.setTitle(title);
        dialog.show();
    }

    protected void handleInput(ECMeeting meeting , EditText editText) {

    }


    protected void showProcessDialog() {
    	if(mPostingdialog!=null && mPostingdialog.isShowing()){
    		return;
    	}
        mPostingdialog = new ECProgressDialog(MeetingBaseActivity.this, R.string.login_posting_submit);
        mPostingdialog.show();
    }
    protected void showCustomProcessDialog(String content) {
    	
    	if(mPostingdialog!=null&&mPostingdialog.isShowing()){
    		return;
    	}
    	
    	mPostingdialog = new ECProgressDialog(MeetingBaseActivity.this, content);
    	mPostingdialog.show();
    }
    
    /**
     * 关闭对话框
     */
    protected void dismissPostingDialog() {
        if(mPostingdialog == null || !mPostingdialog.isShowing()) {
            return ;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }
}
