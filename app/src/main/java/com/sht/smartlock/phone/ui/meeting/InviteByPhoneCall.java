package com.sht.smartlock.phone.ui.meeting;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.base.CCPClearEditText;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.ECMeetingManager.OnInviteMembersJoinToMeetingListener;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * 外呼邀请加入会议
 * Created by Jorstin on 2015/7/26.
 */
public class InviteByPhoneCall extends MeetingBaseActivity {

    private static final String TAG = "ECSDK_Demo.InviteByPhoneCall";
    /**电话号码输入框*/
    private CCPClearEditText mSayHiEdit;
    /**会议号*/
    private String mMeetingNo;
    private boolean misLandingCall;
	private TextView tvInvite;
    @Override
    protected int getLayoutId() {
        return R.layout.invite_by_phone_call;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeetingNo = getIntent().getStringExtra(ECDevice.MEETING_NO);
        misLandingCall = getIntent().getBooleanExtra("isLandingCall" , true);
        if (TextUtils.isEmpty(mMeetingNo)) {
            ToastUtil.showMessage(R.string.toast_confno_Illegal);
            finish();
            return;
        }


        initView();

        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.app_title_right_button),
                getString(R.string.dialog_title_invite), null, this);
    }

    private void initView() {
        mSayHiEdit = (CCPClearEditText) findViewById(R.id.say_hi_content);

        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(20)};
        mSayHiEdit.setFilters(filters);

        mSayHiEdit.setInputType(InputType.TYPE_CLASS_PHONE);
        
        tvInvite = (TextView) findViewById(R.id.tv_invite_voicemeeting);
        
        if(!misLandingCall){
        	tvInvite.setText(R.string.dialog_title_invite);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.text_right:
                hideSoftKeyboard();
                String mPhoneNumber = mSayHiEdit.getText().toString();
                if(TextUtils.isEmpty(mPhoneNumber)){
                    ToastUtil.showMessage(R.string.regbymobile_reg_mobile_format_err_msg);
                    return ;
                }
                doInviteMobileMember(mPhoneNumber);
                break;
        }
    }

    /**
     * 处理邀请成员加入会议请求
     */
    @SuppressWarnings("deprecation")
	private void doInviteMobileMember(String phoneNumber) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            return ;
        }
        showProcessDialog();
        
        
        
        meetingManager.inviteMembersJoinToVoiceMeeting(mMeetingNo, new String[]{phoneNumber}, misLandingCall, new OnInviteMembersJoinToMeetingListener() {
			
			@Override
			public void onInviteMembersJoinToMeeting(ECError reason, String arg1) {
				
				dismissPostingDialog();
                if(SdkErrorCode.REQUEST_SUCCESS == reason.errorCode) {
                    // 邀请加入会议成功
                    setResult(RESULT_OK);
                    finish();
                    return ;
                }
                ToastUtil.showMessage("邀请加入会议失败["+ reason.errorCode + "]");
				
			}
		});
        
        
        
    }
}
