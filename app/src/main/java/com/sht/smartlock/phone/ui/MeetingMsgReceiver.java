package com.sht.smartlock.phone.ui;

import android.os.Bundle;

import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.ui.videomeeting.VideoconferenceBaseActivity;
import com.yuntongxun.ecsdk.OnMeetingListener;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneInviteMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneOverMsg;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议消息处理 com.yuntongxun.ecdemo.ui in ECDemo_Android Created by Jorstin on
 * 2015/7/25.
 */
public class MeetingMsgReceiver implements OnMeetingListener {

	private static final String TAG = "ECSDK_Demo."
			+ MeetingMsgReceiver.class.getSimpleName();

	private static MeetingMsgReceiver ourInstance = new MeetingMsgReceiver();
	/** 实时对讲会议列表 */
	public static List<ECInterPhoneMeetingMsg> mInterPhones = new ArrayList<ECInterPhoneMeetingMsg>();

	public static MeetingMsgReceiver getInstance() {
		return ourInstance;
	}

	private MeetingMsgReceiver() {

	}

	private List<WeakReference<OnVoiceMeetingMsgReceive>> sObserver = new ArrayList<WeakReference<OnVoiceMeetingMsgReceive>>();

	public static void addVoiceMeetingListener(OnVoiceMeetingMsgReceive receiver) {
		if (getInstance().sObserver == null
				&& getInstance().sObserver.isEmpty()) {
			return;
		}
		WeakReference<OnVoiceMeetingMsgReceive> add = null;
		for (WeakReference<OnVoiceMeetingMsgReceive> wr : getInstance().sObserver) {
			if (wr != null) {
				OnVoiceMeetingMsgReceive msgReceive = wr.get();
				if (receiver == msgReceive) {
					add = wr;
					break;
				}
			}
		}
		if (add != null) {
			return;
		}
		getInstance().sObserver
				.add(new WeakReference<OnVoiceMeetingMsgReceive>(receiver));
	}

	public static void removeVoiceMeetingListener(
			OnVoiceMeetingMsgReceive receiver) {
		if (getInstance().sObserver == null
				&& getInstance().sObserver.isEmpty()) {
			return;
		}
		WeakReference<OnVoiceMeetingMsgReceive> remove = null;
		for (WeakReference<OnVoiceMeetingMsgReceive> wr : getInstance().sObserver) {
			if (wr != null) {
				OnVoiceMeetingMsgReceive msgReceive = wr.get();
				if (receiver == msgReceive) {
					remove = wr;
					break;
				}
			}
		}
		if (remove != null) {
			getInstance().sObserver.remove(remove);
			LogUtil.d(TAG, "remove observer success , observer " + receiver);
		}
	}

	@Override
	public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {
		// 预先缓存实时对讲房间列表消息
		filterInterPhones(msg);
		if (sObserver == null && sObserver.isEmpty()) {
			return;
		}

		for (WeakReference<OnVoiceMeetingMsgReceive> wr : sObserver) {
			if (wr != null) {
				OnVoiceMeetingMsgReceive receiver = wr.get();
				if (receiver != null) {
					receiver.onReceiveInterPhoneMeetingMsg(msg);
				}
			}
		}
	}

	/**
	 * 这边用于列表刷新的消息
	 * 
	 * @param msg
	 */
	private void filterInterPhones(ECInterPhoneMeetingMsg msg) {
		if (msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.INVITE) {
			// 处理邀请实时对讲消息
			ECInterPhoneInviteMsg inviteMsg = (ECInterPhoneInviteMsg) msg;
			if (mInterPhones == null) {
				mInterPhones = new ArrayList<ECInterPhoneMeetingMsg>();
			}
			mInterPhones.add(inviteMsg);

		} else if (msg.getMsgType() == ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.OVER) {
			// 实时对讲结束
			ECInterPhoneOverMsg overMsg = (ECInterPhoneOverMsg) msg;
			for (ECInterPhoneMeetingMsg inter : mInterPhones) {
				if (inter != null && inter.getMeetingNo() != null
						&& inter.getMeetingNo().equals(overMsg.getMeetingNo())) {
					// 从列表中删除这个实时对讲
					mInterPhones.remove(inter);
					return;
				}
			}
		}
	}

	@Override
	public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {
		if (sObserver == null && sObserver.isEmpty()) {
			return;
		}

		for (WeakReference<OnVoiceMeetingMsgReceive> wr : sObserver) {
			if (wr != null) {
				OnVoiceMeetingMsgReceive receiver = wr.get();
				if (receiver != null) {
					receiver.onReceiveVoiceMeetingMsg(msg);
				}
			}
		}
	}

	@Override
	public void onReceiveVideoMeetingMsg(ECVideoMeetingMsg msg) {

		SDKCoreHelper.getInstance().onReceiveVideoMeetingMsg(msg);

	}

	/**
	 * 生成一个实时对讲列表
	 * 
	 * @param meetingNo
	 */
	public static void putInterPhone(String meetingNo) {
		ECInterPhoneInviteMsg inviteMsg = new ECInterPhoneInviteMsg(
				ECInterPhoneMeetingMsg.ECInterPhoneMeetingMsgType.INVITE);
		inviteMsg.setMeetingNo(meetingNo);
		inviteMsg.setFrom(CCPAppManager.getUserId());
		inviteMsg.setReceiver(CCPAppManager.getUserId());
		if (mInterPhones == null) {
			mInterPhones = new ArrayList<ECInterPhoneMeetingMsg>();
		}
		mInterPhones.add(inviteMsg);
	}

	public interface OnVoiceMeetingMsgReceive {
		void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg);

		void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg);
	}


	@Override
	public void onVideoRatioChanged(VideoRatio video) {

		if (video != null) {
			Bundle b = new Bundle();
			b.putString("voip", video.getAccount());
			b.putInt("width", video.getWidth());
			b.putInt("height", video.getHeight());
			SDKCoreHelper.getInstance().sendTarget(
					VideoconferenceBaseActivity.KEY_VIDEO_RATIO_CHANGED, b);
		}
	}

}
