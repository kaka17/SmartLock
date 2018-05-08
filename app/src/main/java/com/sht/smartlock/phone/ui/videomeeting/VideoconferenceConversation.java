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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.common.view.CCPAlertDialog;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager.ECMeetingType;
import com.yuntongxun.ecsdk.ECMeetingManager.OnDeleteMeetingListener;
import com.yuntongxun.ecsdk.ECMeetingManager.OnListAllMeetingsListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.meeting.ECMeeting;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingDeleteMsg;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingExitMsg;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingJoinMsg;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author luhuashan 该类是视频会议列表界面、通过调用sdk 获取视频会议列表显示在界面
 *
 */
public class VideoconferenceConversation extends VideoconferenceBaseActivity
		implements View.OnClickListener, OnItemClickListener,
		CCPAlertDialog.OnPopuItemClickListener {

	public static final String CONFERENCE_CREATOR = "com.voice.demo.ccp.VIDEO_CREATE";
	public static final String CONFERENCE_TYPE = "COM.VOICE.DEMO.CCP.VIDEO_TYPE";

	public static final int TYPE_SINGLE = 0;
	public static final int TYPE_MULIT = 1;

	private RelativeLayout mVideoCEmpty;
	private ListView mVideoCListView;
	private TextView mVideoTips;

	private ArrayList<ECMeeting> mVideoCList;
	private VideoConferenceConvAdapter mVideoCAdapter;

	private int mVideoConfType = TYPE_SINGLE;
	protected String TAG = "VideoconferenceConversation";

	private int selectPostion = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.videomeeting_create),
                getString(R.string.videomeeting_list), null, this);

		initResourceRefs();
		mVideoConfType = getIntent().getIntExtra(CONFERENCE_TYPE,
				mVideoConfType);

		mVideoConfType = TYPE_MULIT;

		registerReceiver2(new String[] { ECGlobalConstants.INTENT_VIDEO_CONFERENCE_DISMISS });

	}

	@Override
	protected void onResume() {
		super.onResume();
		showCustomProcessDialog(getString(R.string.common_progressdialog_title));
		doQueryVideoMeetings();

	}

	/**
	 * 调用sdk获取会议列表
	 */
	private void doQueryVideoMeetings() {


		if(!checkSDK()){
			return;
		}
		ECDevice.getECMeetingManager().listAllMultiMeetingsByType("",
				ECMeetingType.MEETING_MULTI_VIDEO,
				new OnListAllMeetingsListener<ECMeeting>() {

					@Override
					public void onListAllMeetings(ECError reason,
							List<ECMeeting> list) {

						dismissPostingDialog();
						if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {

							if (list == null) {
								return;
							}
							mVideoCList = new ArrayList<ECMeeting>();
							for (ECMeeting conference : list) {
								if (conference != null) {
									mVideoCList.add(conference);
								}
							}

							if (mVideoCList == null || mVideoCList.isEmpty()) {
								if (mVideoCAdapter != null) {
									mVideoCAdapter.clear();
								}
								return;
							}

							mVideoTips
									.setText(R.string.str_click_into_video_conference);
							initVideoConferenceAdapter();
						} else {
							Toast.makeText(
									getApplicationContext(),
									"获取会议列表失败,错误码"+reason.errorCode,
									Toast.LENGTH_SHORT).show();

						}

					}

				});

	}

	private void initResourceRefs() {
		mVideoCEmpty = (RelativeLayout) findViewById(R.id.video_conferenc_empty);
		mVideoCListView = (ListView) findViewById(R.id.video_conferenc_list);
		mVideoTips = (TextView) findViewById(R.id.video_notice_tips);

		findViewById(R.id.begin_create_video_conference).setOnClickListener(
				this);
		mVideoCListView.setOnItemClickListener(this);
		mVideoCListView.setEmptyView(mVideoCEmpty);

	}

	@Override
	public void onClick(View v) {
		
		
		
		switch (v.getId()) {
		case R.id.title_btn4:

			this.finish();
			break;
		case R.id.title_btn1:
		case R.id.begin_create_video_conference:
		
		case R.id.text_right:

			Intent intent = new Intent(VideoconferenceConversation.this,
					CreateVideoConference.class);
			intent.putExtra(VideoconferenceConversation.CONFERENCE_TYPE,
					mVideoConfType);
			startActivity(intent);
			overridePendingTransition(R.anim.video_push_up_in,
					R.anim.push_empty_out);

			break;
		case R.id.btn_left:
            hideSoftKeyboard();
            finish();
            break;
		
		default:
			break;
		}
	}

	int mPosition = -1;

	private ECMeeting videoConferenceInfo;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mPosition = -1;

		startJoinVideoConferenceAction(position);

		selectPostion = position;

	}

	@Override
	protected void handleDialogOkEvent(int requestKey) {

		if (requestKey == DIALOG_REQUEST_VOIP_NOT_WIFI_WARNNING) {
			if (mPosition >= 0) {
				startJoinVideoConferenceAction(mPosition);
				mPosition = -1;
			} else {
				startActivity(new Intent(VideoconferenceConversation.this,
						CreateVideoConference.class));
				overridePendingTransition(R.anim.video_push_up_in,
						R.anim.push_empty_out);
			}
		} else {
			super.handleDialogOkEvent(requestKey);
		}
	}

	private void startJoinVideoConferenceAction(int position) {
		if (mVideoCAdapter != null && mVideoCAdapter.getItem(position) != null) {

			videoConferenceInfo = mVideoCAdapter.getItem(position);
			if (videoConferenceInfo == null) {
				return;
			}
			int num = videoConferenceInfo.getJoined();
			String creator = videoConferenceInfo.getCreator();

			if (!CCPAppManager.getUserId().equals(creator) && num >= 5) {

				ToastUtil.showMessage("当前参会人数已达上限");
				return;
			}

			if (CCPAppManager.getUserId().equals(
					videoConferenceInfo.getCreator())) {
				doVideoConferenceControl();
			} else {
				doVideoConferenceAction();
			}
		}
	}

	// ----------------------------------CCP SDK Device CallBack.

	private void doVideoConferenceControl() {
		int[] ccpAlertResArray = null;
		int title = R.string.chatroom_control_tip;
		ccpAlertResArray = new int[] { R.string.chatroom_c_join,
				R.string.chatroom_c_dismiss };
		CCPAlertDialog ccpAlertDialog = new CCPAlertDialog(
				VideoconferenceConversation.this, title, ccpAlertResArray, 0,
				R.string.dialog_cancle_btn);

		ccpAlertDialog.setOnItemClickListener(VideoconferenceConversation.this);
		ccpAlertDialog.create();
		ccpAlertDialog.show();
	}

	private void initVideoConferenceAdapter() {
		if (mVideoCAdapter == null) {
			mVideoCAdapter = new VideoConferenceConvAdapter(
					VideoconferenceConversation.this);
			mVideoCListView.setAdapter(mVideoCAdapter);
		}

		mVideoCAdapter.setData(mVideoCList);
	}

	// --------------------------------SDK Callback -----------------

	@Override
	protected void handleReceiveVideoConferenceMsg(ECVideoMeetingMsg VideoMsg) {
		super.handleReceiveVideoConferenceMsg(VideoMsg);
		synchronized (VideoconferenceConversation.class) {
			if (VideoMsg == null) {
				return;
			}

			// remove the Video Conference Conversation form the Video Adapter.
			if (VideoMsg instanceof ECVideoMeetingDeleteMsg) {
				ECVideoMeetingDeleteMsg videoConferenceDismissMsg = (ECVideoMeetingDeleteMsg) VideoMsg;

				String conferenceId = videoConferenceDismissMsg.getMeetingNo();
				if (mVideoCAdapter == null || mVideoCAdapter.isEmpty()) {
					return;
				}

				for (int position = 0; position < mVideoCAdapter.getCount(); position++) {
					ECMeeting item = mVideoCAdapter.getItem(position);
					if (item == null || TextUtils.isEmpty(item.getMeetingNo())) {
						continue;
					}

					if (item.getMeetingNo().equals(conferenceId)) {
						mVideoCAdapter.remove(item);
						return;
					}
				}

				// if Video Conference list empty .
				if (mVideoCAdapter.isEmpty()) {
					// str_tips_no_video_c
					mVideoTips.setText(R.string.str_tips_no_video_c);
				}
			} else if (VideoMsg instanceof ECVideoMeetingJoinMsg
					|| VideoMsg instanceof ECVideoMeetingExitMsg) {

				doQueryVideoMeetings();

			}
		}
	}

	@Override
	protected void onReceiveBroadcast(Intent intent) {
		super.onReceiveBroadcast(intent);

		if (ECGlobalConstants.INTENT_VIDEO_CONFERENCE_DISMISS.equals(intent
				.getAction())) {
			// receive action dismiss Video Conference.
			if (intent.hasExtra(ECGlobalConstants.CONFERENCE_ID)) {
				String conferenceId = intent
						.getStringExtra(ECGlobalConstants.CONFERENCE_ID);
				ECVideoMeetingDeleteMsg VideoMsg = new ECVideoMeetingDeleteMsg();
				VideoMsg.setMeetingNo(conferenceId);
				;
				handleReceiveVideoConferenceMsg(VideoMsg);
			}

		}
	}

	/**
	 * 
	 * @ClassName: VideoConferenceConvAdapter
	 * @Description: TODO
	 * @author Jorstin Chan
	 * @date 2013-12-3
	 * 
	 */
	class VideoConferenceConvAdapter extends ArrayAdapter<ECMeeting> {

		LayoutInflater mInflater;

		public VideoConferenceConvAdapter(Context context) {
			super(context, 0);

			mInflater = getLayoutInflater();
		}

		public void setData(List<ECMeeting> data) {
			setNotifyOnChange(false);
			clear();
			setNotifyOnChange(true);
			if (data != null) {
				for (ECMeeting appEntry : data) {
					add(appEntry);
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			VideoConferenceHolder holder;
			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.list_item_chatroom,
						null);
				holder = new VideoConferenceHolder();
				convertView.setTag(holder);

				holder.videoConName = (TextView) convertView
						.findViewById(R.id.chatroom_name);
				holder.videoConTips = (TextView) convertView
						.findViewById(R.id.chatroom_tips);
				holder.gotoIcon = (TextView) convertView
						.findViewById(R.id.goto_icon);

				LayoutParams params = (LayoutParams) holder.gotoIcon
						.getLayoutParams();
				params.setMargins(0, 0, 10, 0);
				holder.gotoIcon.setLayoutParams(params);

				// convertView
				// .setBackgroundResource(R.drawable.video_list_item_conference);
				// // set Video Conference Item Style
				// holder.videoConName.setTextColor(Color.parseColor("#FFFFFF"));
				// holder.gotoIcon
				// .setBackgroundResource(R.drawable.video_item_goto);
			} else {
				holder = (VideoConferenceHolder) convertView.getTag();
			}

			try {
				// do ..
				ECMeeting item = getItem(position);
				if (item != null) {

					String conferenceName = "";
					String voipAccount = item.getCreator();
					if (TextUtils.isEmpty(voipAccount)) {
						voipAccount = "";
					}

					// Video Conference Name
					if (!TextUtils.isEmpty(item.getMeetingName())) {
						conferenceName = item.getMeetingName();
					} else {
						conferenceName = getString(
								R.string.video_conference_name, voipAccount);
					}

					holder.videoConName.setText(conferenceName);

					int resourceId;

					if (item.getJoined() >= 5) {
						//
						resourceId = R.string.str_chatroom_list_join_full;
					} else {

						resourceId = R.string.str_chatroom_list_join_unfull;
					}

					holder.videoConTips.setText(getString(resourceId,
							item.getJoined(), voipAccount));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

		class VideoConferenceHolder {
			TextView videoConName;
			TextView videoConTips;
			TextView gotoIcon;
		}

	}

	@Override
	protected int getLayoutId() {
		return R.layout.video_conference_conversation;
	}

	@Override
	public void onItemClick(ListView parent, View view, int position,
			int resourceId) {
		switch (resourceId) {
		case R.string.chatroom_c_join:
			if (mVideoCAdapter == null) {
				return;
			}
			ECMeeting meeting = mVideoCAdapter.getItem(selectPostion);

			if (meeting != null && meeting.getJoined() >= 5) {

				ToastUtil.showMessage("当前参会人数已达上限");
				;
				return;
			}

			doVideoConferenceAction();
			break;
		case R.string.chatroom_c_dismiss:
			if (videoConferenceInfo != null) { // ??

				
				if(!checkSDK()){
					return;
				}
				showCustomProcessDialog(getString(R.string.common_progressdialog_title));
				ECDevice.getECMeetingManager().deleteMultiMeetingByType(
						ECMeetingType.MEETING_MULTI_VIDEO,
						videoConferenceInfo.getMeetingNo(),
						new OnDeleteMeetingListener() {

							public void onMeetingDismiss(ECError reason,
									String meetingNo) {
								// TODO Auto-generated method stub
								dismissPostingDialog();
								doQueryVideoMeetings();
							}
						});

			}
			break;
		}

	}

	/**
	 * 
	 */
	private void doVideoConferenceAction() {
		if (videoConferenceInfo == null) {
			Toast.makeText(this, "会议号不存在", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent();
		if (mVideoConfType == 0) {
		} else {
			intent.setClass(VideoconferenceConversation.this, // 多路视频
					MultiVideoconference.class);

		}
		intent.putExtra(ECGlobalConstants.CONFERENCE_ID,
				videoConferenceInfo.getMeetingNo());
		intent.putExtra(CONFERENCE_CREATOR, videoConferenceInfo.getCreator());
		if (!TextUtils.isEmpty(videoConferenceInfo.getMeetingName())) {
			intent.putExtra(ECGlobalConstants.CHATROOM_NAME,
					videoConferenceInfo.getMeetingName());
		}
		startActivity(intent);
	}

}
