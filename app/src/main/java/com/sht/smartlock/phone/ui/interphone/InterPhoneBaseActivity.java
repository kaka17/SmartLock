package com.sht.smartlock.phone.ui.interphone;

import android.view.View;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.MeetingMsgReceiver;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMember;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;

import java.util.List;

/**
 * Created by Jorstin on 2015/7/27.
 */
public abstract class InterPhoneBaseActivity extends ECSuperActivity implements
        InterPhoneHelper.OnInterPhoneListener , View.OnClickListener , MeetingMsgReceiver.OnVoiceMeetingMsgReceive{


    private ECProgressDialog mPostingdialog;
    @Override
    protected void onResume() {
        super.onResume();
        InterPhoneHelper.addInterPhoneCallback(this);
        MeetingMsgReceiver.addVoiceMeetingListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        InterPhoneHelper.removeInterPhoneCallback(this);
        MeetingMsgReceiver.removeVoiceMeetingListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onInterPhoneError(ECError e) {
        ToastUtil.showMessage("请求错误[" + e.errorCode + "]");
    }

    @Override
    public void onInterPhoneMembers(List<ECInterPhoneMeetingMember> members) {

    }

    @Override
    public void onInterPhoneStart(String interNo) {

    }


    @Override
    public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {

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

    @Override
    public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {

    }

    protected void showProcessDialog() {
        mPostingdialog = new ECProgressDialog(InterPhoneBaseActivity.this, R.string.login_posting_submit);
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
