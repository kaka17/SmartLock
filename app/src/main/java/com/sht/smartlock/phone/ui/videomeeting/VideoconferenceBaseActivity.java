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
 */package com.sht.smartlock.phone.ui.videomeeting;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.dialog.ECAlertDialog;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECMeetingManager.ECCreateMeetingParams.ToneMode;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingMsg;

/**
 * 
 * <p>
 * Title: VideoConfBaseActivity.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: http://www.cloopen.com
 * </p>
 * 
 * @author Jorstin Chan
 * @date 2013-10-28
 * @version 3.5
 */
public abstract class VideoconferenceBaseActivity extends ECSuperActivity {

	/**
	 * defined message code so that the recipient can identify what this message
	 * is about. Each {@link Handler} has its own name-space for message codes,
	 * so you do not need to worry about yours conflicting with other handlers.
	 */
	public static final int KEY_VIDEO_RECEIVE_MESSAGE = 0x1;

	/**
	 * defined message code for Receive Video Conference Message that you create
	 * or Join.
	 */
	public static final int KEY_VIDEO_CONFERENCE_STATE = 0x2;

	public static final int WHAT_SHOW_PROGRESS = 0x101A;
	public static final int WHAT_CLOSE_PROGRESS = 0x101B;

	/**
	 * defined message code for query members
	 */
	public static final int KEY_VIDEO_CONFERENCE_MEMBERS = 0x3;
	public static final int KEY_VIDEO_RATIO_CHANGED = 0x111;

	/**
	 * defined message code for query Video Conference list.
	 */
	public static final int KEY_VIDEO_CONFERENCE_LIST = 0x4;

	/**
	 * defined message code for invite member join Video Conference.
	 */
	public static final int KEY_VIDEO_CONFERENCE_INVITE_MEMBER = 0x5;

	/**
	 * defined message code for dismiss a Video Conference.
	 */
	public static final int KEY_VIDEO_CONFERENCE_DISMISS = 0x6;

	/**
	 * defined message code for remove member from a Video Conference.
	 */
	public static final int KEY_VIDEO_REMOVE_MEMBER = 0x7;

	/**
	 * defined message code for download video conference member portrait.
	 */
	public static final int KEY_VIDEO_DOWNLOAD_PORTRAIT = 0x8;

	/**
	 * defined message code for query video conference member portrait list.
	 */
	public static final int KEY_VIDEO_GET_PORPRTAIT = 0x9;

	/**
	 * defined message code for get local porprtait of Video Conference.
	 * 
	 * @deprecated
	 */
	public static final int KEY_DELIVER_VIDEO_FRAME = 0x10;

	/**
	 * defined message code for Sswitch main screen of Video Conference.
	 */
	public static final int KEY_SWITCH_VIDEO_SCREEN = 0x11;

	/**
	 * defined message code for do something in background
	 * 
	 *
	 */
	public static final int KEY_TASK_DOWNLOAD_PORPRTAIT = 0x12;

	/**
	 * defined message code for init members to VideoUI
	 * 
	 * @see
	 */
	public static final int KEY_TASK_INIT_VIDEOUI_MEMBERS = 0x13;

	/**
	 * defined message code for Sswitch main screen of Video Conference.
	 */
	public static final int KEY_SEND_LOCAL_PORTRAIT = 0x14;

	private ProgressDialog pVideoDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKCoreHelper.getInstance().setHandler(handler);

		registerReceiver2(new String[] { Intent.ACTION_MEDIA_MOUNTED,
				Intent.ACTION_MEDIA_REMOVED });

		initScreenStates();

	}

	private WindowManager mWindowManager;
	private PowerManager mPowerManager;
	private AudioManager mAudioManager = null;
	private ToneGenerator mToneGenerator;

	Object mToneGeneratorLock = new Object();

	private void initScreenStates() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}
		if (mWindowManager == null) {
			mWindowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
			mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		}
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				try {
					int streamVolume = mAudioManager
							.getStreamVolume(STREAM_TYPE);
					int streamMaxVolume = mAudioManager
							.getStreamMaxVolume(STREAM_TYPE);
					int volume = (int) (TONE_RELATIVE_VOLUME * (streamVolume / streamMaxVolume));
					mToneGenerator = new ToneGenerator(STREAM_TYPE, volume);

				} catch (RuntimeException e) {

					mToneGenerator = null;
				}
			}
		}
		mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, SDKCoreHelper.TAG);
	}

	private static final float TONE_RELATIVE_VOLUME = 100.0F;

	private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	// Stream type used to play the DTMF tones off call,
	// and mapped to the volume control keys

	// The length of tones in milliseconds
	public static final int TONE_LENGTH_MS = 200;

	@Override
	protected void onResume() {
		super.onResume();

		SDKCoreHelper.getInstance().setHandler(handler);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SDKCoreHelper.getInstance().setHandler(null);
	}

	public ToneMode getToneMode(int voiceMode) {

		switch (voiceMode) {
		case 0:

			return ToneMode.ONLY_BACKGROUND;
		case 1:

			return ToneMode.ALL;
		case 2:

			return ToneMode.NONE;

		}
		return ToneMode.ALL;

	}

	public boolean checkSDK() {
		if (ECDevice.getECMeetingManager() == null) {

			ToastUtil.showMessage(R.string.SDK_INIT_FAILED);
			return false;
		}
		return true;
	}

	private final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == SDKCoreHelper.WHAT_SHOW_PROGRESS) {

			} else if (msg.what == SDKCoreHelper.WHAT_CLOSE_PROGRESS) {

			} else {

				Bundle b = (Bundle) msg.obj;
				int what = msg.what;

				if (b == null) {
					return;
				}


				switch (what) {
				case KEY_VIDEO_RECEIVE_MESSAGE:
					ECVideoMeetingMsg vConferenceMsg = (ECVideoMeetingMsg) b
							.getParcelable("VideoConferenceMsg");

					if (vConferenceMsg != null) {
						handleReceiveVideoConferenceMsg(vConferenceMsg);
					}
					break;

				case KEY_VIDEO_RATIO_CHANGED:

					String voip = b.getString("voip");
					int width = b.getInt("width");
					int height = b.getInt("height");
					handleVideoRatioChanged(voip, width, height);

					break;
				default:
					break;
				}

			}
		}

	};

	@SuppressLint("HandlerLeak")
	public Handler getBaseHandle() {
		return handler;
	}

	protected void handleVideoRatioChanged(String voip, int width, int height) {

	}

	public void showAlertTipsDialog(final int requestKey, String title,
			String message, final String posButton, String NegButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (!TextUtils.isEmpty(title))
			builder.setTitle(title);
		builder.setMessage(message);
		if (!TextUtils.isEmpty(posButton)) {
			builder.setPositiveButton(posButton, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
					if (!posButton
							.equals(getString(R.string.dialog_cancle_btn)))
						handleDialogOkEvent(requestKey);

				}
			});
		}
		if (!TextUtils.isEmpty(NegButton)) {
			builder.setNegativeButton(NegButton, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					handleDialogCancelEvent(requestKey);
				}
			});
		}

		builder.create().show();
	}

	protected void handleDialogCancelEvent(int requestKey) {
	}

	protected void handleNotifyMessage(Message msg) {

	}

	/**
	 * @return the handler
	 */
	public Handler getHandler() {
		return handler;
	}

	public final Message getHandleMessage() {

		Message msg = getHandler().obtainMessage();
		return msg;
	}

	public final void sendHandleMessage(Message msg) {
		getHandler().sendMessage(msg);
	}

	protected void handleReceiveVideoConferenceMsg(ECVideoMeetingMsg VideoMsg) {
	}

	protected void handleVideoConferenceState(int reason, String conferenceId) {
	}

	protected void handleVideoConferenceDismiss(int reason, String conferenceId) {
	}

	protected void handleVideoConferenceRemoveMember(int reason, String member) {
	}

	protected void handleSwitchRealScreenToVoip(int reason) {
	}

	public void setVideoTitleBackground() {
		findViewById(R.id.nav_title).setBackgroundResource(
				R.drawable.video_title_bg);
	}

	public void setVideoTitle(String title) {
		((TextView) findViewById(R.id.title)).setText(title);
	}

	public void HideSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			View localView = getCurrentFocus();
			if (localView != null && localView.getWindowToken() != null) {
				IBinder windowToken = localView.getWindowToken();
				inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
			}
		}
	}

	/**
	 *
	 * <p>
	 * Title: HideSoftKeyboard
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 *
	 * @param view
	 */
	public void HideSoftKeyboard(View view) {
		if (view == null) {
			return;
		}

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			IBinder localIBinder = view.getWindowToken();
			if (localIBinder != null)
				inputMethodManager.hideSoftInputFromWindow(localIBinder, 0);
		}
	}

	public void showConnectionProgress(String messageContent) {
		Message message = Message.obtain();
		message.obj = messageContent;
		message.what = WHAT_SHOW_PROGRESS;
		handler.sendMessage(message);
	}

	public void closeConnectionProgress() {
		handler.sendEmptyMessage(WHAT_CLOSE_PROGRESS);
	}

	private ECProgressDialog mPostingdialog;

	protected void showProcessDialog() {
		if (mPostingdialog != null && mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog = new ECProgressDialog(VideoconferenceBaseActivity.this,
				R.string.login_posting_submit);
		mPostingdialog.show();
	}

	protected void showCustomProcessDialog(String content) {

		if (mPostingdialog != null && mPostingdialog.isShowing()) {
			return;
		}

		mPostingdialog = new ECProgressDialog(VideoconferenceBaseActivity.this,
				content);
		mPostingdialog.show();
	}

	/**
	 * 关闭对话框
	 */
	protected void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

	private KeyguardLock kl = null;
	private WakeLock mWakeLock;

	public void lockScreen() {
		KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		// Get a keyboard lock manager object
		if (km.inKeyguardRestrictedInputMode()) {
			kl = km.newKeyguardLock(SDKCoreHelper.TAG);
			// Parameter is used by Tag LogCat.
			kl.disableKeyguard();// Unlock.
		}

		mWakeLock.acquire();
	}

	// Release the lock screen and the screen brightness manager,
	// reply to the system default state
	public void releaseLockScreen() {
		if (kl != null) {
			kl.reenableKeyguard();
		}

		if (mWakeLock != null) {
			try {
				mWakeLock.release();
			} catch (Exception e) {
				System.out.println("mWakeLock may already release");
			}
		}
	}

	public static final String INTETN_ACTION_EXIT_CCP_DEMO = "exit_demo";

	InternalReceiver internalReceiver = null;

	protected final void registerReceiver2(String[] actionArray) {
		if (actionArray == null) {
			return;
		}
		IntentFilter intentfilter = new IntentFilter(
				INTETN_ACTION_EXIT_CCP_DEMO);
		for (String action : actionArray) {
			intentfilter.addAction(action);
		}

		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		registerReceiver(internalReceiver, intentfilter);
	}

	private class InternalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null || intent.getAction() == null) {
				return;
			}
			handleReceiver(context, intent);
		}
	}

	public static final int DIALOG_SHOW_TIPS = 0x1;
	public static final int DIALOG_SHOW_KEY_INVITE = 0x2;
	public static final int DIALOG_SHOW_KEY_CHECKBOX = 0x3;
	public static final int DIALOG_SHOW_KEY_DISSMISS_CHATROOM = 0x4;
	public static final int DIALOG_SHOW_KEY_REMOVE_CHATROOM = 0x5;
	public static final int DIALOG_REQUEST_KEY_EXIT_CHATROOM = 0x6;
	public static final int DIALOG_REQUEST_VOIP_NOT_WIFI_WARNNING = 0x7;
	public static final int DIALOG_REQUEST_LOAD_FAILED_NETWORK = 0x8;

	public static final int DIALOG_SHOW_KEY_DISSMISS_VIDEO = DIALOG_SHOW_KEY_DISSMISS_CHATROOM;
	public static final int DIALOG_SHOW_KEY_REMOVE_VIDEO = DIALOG_SHOW_KEY_REMOVE_CHATROOM;

	protected void handleDialogOkEvent(int requestKey) {

	}

	protected void onReceiveBroadcast(Intent intent) {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator != null) {
				mToneGenerator.release();
				mToneGenerator = null;
			}
		}
		mAudioManager = null;
		unregisterReceiver(internalReceiver);
	}

	public void showVoIPWifiWarnning() {
		showAlertTipsDialog(DIALOG_REQUEST_VOIP_NOT_WIFI_WARNNING,
				getString(R.string.voip_not_wifi_warnning_title),
				getString(R.string.voip_not_wifi_warnning_message),
				getString(R.string.dialog_btn),
				getString(R.string.dialog_cancle_btn));
	}

	public void showNetworkNotAvailable() {
		showAlertTipsDialog(DIALOG_REQUEST_LOAD_FAILED_NETWORK,
				getString(R.string.voip_not_wifi_warnning_title),
				getString(R.string.load_failed_network),
				getString(R.string.dialog_btn), null);
	}

	protected void showInputCodeDialog( String title, String message , final boolean isLandCall) {
        View view = View.inflate(this , R.layout.dialog_edit_context , null);
        final EditText editText = (EditText) view.findViewById(R.id.sendrequest_content);
        ((TextView) view.findViewById(R.id.sendrequest_tip)).setText(message);
        ECAlertDialog dialog = ECAlertDialog.buildAlert(this, message, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleInput(editText , isLandCall);
            }
        });
        dialog.setContentView(view);
        dialog.setTitle(title);
        dialog.show();
    }

    protected void handleInput(EditText editText , boolean isLandCall) {

    }

}