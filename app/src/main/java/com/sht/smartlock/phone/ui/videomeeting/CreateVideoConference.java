/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui.videomeeting;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.CCPDrawableUtils;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.common.view.CCPButton;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager.ECCreateMeetingParams;
import com.yuntongxun.ecsdk.ECMeetingManager.ECCreateMeetingParams.ToneMode;
import com.yuntongxun.ecsdk.ECMeetingManager.ECMeetingType;
import com.yuntongxun.ecsdk.ECMeetingManager.OnCreateOrJoinMeetingListener;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * 
 * @author luhuashan创建视频会议界面
 *
 */
public class CreateVideoConference extends VideoconferenceBaseActivity
		implements View.OnClickListener {

	protected static final String TAG = "CreateVideoConference";


	private EditText mVideoCEditText;
	private CCPButton mVideoCSubmit;

	private CheckBox cb_autoclose;
	private CheckBox cb_autojoin;
	private ToneMode voiceMod = ToneMode.ALL;
	private int autoDelete = 1;
	private int mVideoConfType = VideoconferenceConversation.TYPE_MULIT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                -1, null,
                null,
                getString(R.string.videomeeting_create), null, this);

		initResourceRefs();
		initialize(savedInstanceState);

	}

	private void initResourceRefs() {

		RadioGroup rg_autoDelete = (RadioGroup) findViewById(R.id.rg1_video);
		rg_autoDelete.check(R.id.rb1_video);
		rg_autoDelete.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				if (id == R.id.rb1_video) {
					autoDelete = 1;
				} else {
					autoDelete = 0;
				}
			}
		});
		RadioGroup rg_2 = (RadioGroup) findViewById(R.id.rg2);
		rg_2.check(R.id.rb3);
		rg_2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case R.id.rb3:
					voiceMod = ToneMode.ALL;
					break;
				case R.id.rb4:
					voiceMod = ToneMode.ONLY_BACKGROUND;
					break;
				case R.id.rb5:
					voiceMod = ToneMode.NONE;
					break;

				}
			}
		});
		View ll_cb = findViewById(R.id.ll_cb_autoclose);
		ll_cb.setOnClickListener(this);
		cb_autoclose = (CheckBox) findViewById(R.id.cb_autoclose);

		View ll_cb2 = findViewById(R.id.ll_cb_autojoin);
		ll_cb2.setOnClickListener(this);
		cb_autojoin = (CheckBox) findViewById(R.id.cb_autojoin);

		mVideoCEditText = (EditText) findViewById(R.id.room_name);

		mVideoCEditText.setCompoundDrawables(CCPDrawableUtils.getDrawables(
				this, R.drawable.video_input_icon), null, null, null);
		mVideoCEditText.setHint(R.string.str_create_input_name);

		mVideoCEditText.setSelection(mVideoCEditText.getText().length());
		mVideoCSubmit = (CCPButton) findViewById(R.id.create_video_c_submit);
		mVideoCSubmit.setImageResource(R.drawable.create_video);
		mVideoCSubmit.setOnClickListener(this);

		mVideoCEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mVideoCEditText.getText().length() <= 0) {
					mVideoCSubmit.setEnabled(false);
				} else {
					mVideoCSubmit.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mVideoCEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mVideoCEditText
							.setBackgroundResource(R.drawable.video_name_input);
				} else {
					mVideoCEditText
							.setBackgroundResource(R.drawable.video_name_input_no);
				}
			}
		});

	}

	private void initialize(Bundle savedInstanceState) {

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_cb_autojoin:
			cb_autojoin.setChecked(!cb_autojoin.isChecked());
			break;
		case R.id.ll_cb_autoclose:
			cb_autoclose.setChecked(!cb_autoclose.isChecked());
			break;
		case R.id.create_video_c_submit:
			HideSoftKeyboard();
			
			String name=mVideoCEditText.getText().toString().trim();
			if(TextUtils.isEmpty(name)){
				
				ToastUtil.showMessage("请输入会议名称");
				return;
			}
			
			
			if (!cb_autojoin.isChecked()) {
				if (mVideoConfType == 0) {

				} else {
					showConnectionProgress(getString(R.string.str_dialog_message_default));
					
					ECCreateMeetingParams.Builder builder = new ECCreateMeetingParams.Builder();
					
					builder.setMeetingName(
									mVideoCEditText.getText().toString())
							.setSquare(5).setVoiceMod(voiceMod)
							.setIsAutoDelete(autoDelete == 1 ? true : false)
							.setIsAutoJoin(false).setKeywords("")
							.setMeetingPwd("")
							.setIsAutoClose(cb_autoclose.isChecked());
					
					
					ECCreateMeetingParams params = builder.create();
					
					if(!checkSDK()){
						return;
					}
					ECDevice.getECMeetingManager().createMultiMeetingByType(
							params, ECMeetingType.MEETING_MULTI_VIDEO,
							new OnCreateOrJoinMeetingListener() {

								@Override
								public void onCreateOrJoinMeeting(
										ECError reason, String meetingNo) {
									closeConnectionProgress();
									LogUtil.e(TAG, reason.toString() + "---"
											+ meetingNo);
									if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
										finish();
									} else {
										ToastUtil.showMessage("创建会议失败,错误码"+reason.errorCode);
									}

								}
							});

				}

				return;
			}

			Intent intent = new Intent();
			if (mVideoConfType == 0) {
			} else {
				intent.setClass(CreateVideoConference.this,
						MultiVideoconference.class);

			}
			intent.putExtra(VideoconferenceConversation.CONFERENCE_CREATOR,
					CCPAppManager.getUserId());
			intent.putExtra(ECGlobalConstants.CHATROOM_NAME, mVideoCEditText
					.getText().toString());
			intent.putExtra(ECGlobalConstants.IS_AUTO_CLOSE,
					cb_autoclose.isChecked());
			intent.putExtra(ECGlobalConstants.AUTO_DELETE, autoDelete);
			intent.putExtra(ECGlobalConstants.VOICE_MOD, voiceMod.ordinal());
			startActivity(intent);
			finish();
			break;

		case R.id.title_btn4:
			finishVideo();
			break;
			
		case R.id.btn_left:
			
			finishVideo();
            break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			HideSoftKeyboard();
			finishVideo();
		}
		return super.onKeyDown(keyCode, event);

	}

	private void finishVideo() {
		HideSoftKeyboard();
		finish();
		
		overridePendingTransition(R.anim.push_empty_out,
				R.anim.video_push_down_out);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.video_conference_create;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
