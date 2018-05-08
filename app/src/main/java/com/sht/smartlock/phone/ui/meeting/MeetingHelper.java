package com.sht.smartlock.phone.ui.meeting;

import com.sht.smartlock.phone.common.utils.LogUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.ECMeetingManager.ECMeetingType;
import com.yuntongxun.ecsdk.OnMeetingListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.meeting.ECMeeting;
import com.yuntongxun.ecsdk.meeting.ECMeetingMember;
import com.yuntongxun.ecsdk.meeting.ECVoiceMeetingMember;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;
import com.yuntongxun.ecsdk.meeting.video.ECVideoMeetingMsg;
import com.yuntongxun.ecsdk.meeting.voice.ECVoiceMeetingMsg;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/17.
 */
public class MeetingHelper implements OnMeetingListener {

    private static final String TAG = "ECSDK.Demo.MeetingHelper";

    private static MeetingHelper ourInstance = new MeetingHelper();
    private static LinkedList<WeakReference<? extends OnMeetingCallback>> sCallbacks = new LinkedList<WeakReference<? extends  OnMeetingCallback>>();
    private boolean mSyncMeetings = false;

    public static MeetingHelper getInstance() {
        return ourInstance;
    }

    private MeetingHelper() {

    }

    /**
     * 查询会议列表
     * @param meetingType
     */
    public static boolean queryMeetings(ECMeetingManager.ECMeetingType meetingType) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null || getInstance().mSyncMeetings) {
            return false;
        }
        getInstance().mSyncMeetings = true;
        meetingManager.listAllMultiMeetingsByType("", meetingType, new ECMeetingManager.OnListAllMeetingsListener<ECMeeting>() {
            @Override
            public void onListAllMeetings(ECError reason, List<ECMeeting> list) {
                getInstance().mSyncMeetings = false;
                if(reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                    getInstance().notifyMeetings(list);
                    return ;
                }
                getInstance().notifyError(reason);
            }
        });
        return true;
    }

    /**
     * 创建一个新的语音会议
     */
    public static void startVoiceMeeting(ECMeetingManager.ECCreateMeetingParams params) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.createMultiMeetingByType(params, ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE,
                new ECMeetingManager.OnCreateOrJoinMeetingListener() {
                    @Override
                    public void onCreateOrJoinMeeting(ECError reason, String meetingNo) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                            getInstance().notifyMeetingStart(meetingNo);
                            return;
                        }
                        getInstance().notifyError(OnMeetingCallback.MEETING_JOIN, reason);
                    }
                });
    }

    /**
     * 查询会议成员
     * @param meetingNo
     */
    public static void queryMeetingMembers(String meetingNo) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.queryMeetingMembersByType(meetingNo, ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE,
                new ECMeetingManager.OnQueryMeetingMembersListener<ECVoiceMeetingMember>() {
                    @Override
                    public void onQueryMeetingMembers(ECError reason, List<ECVoiceMeetingMember> members) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                            getInstance().notifyMeetingMembers(members);
                            return;
                        }
                        getInstance().notifyError(OnMeetingCallback.QUERY_MEMBERS, reason);
                    }
                });
    }

    /**
     * 处理会议房间解散
     * @param meetingNo
     */
    public static void disMeeting(String meetingNo) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.deleteMultiMeetingByType(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE, meetingNo,
                new ECMeetingManager.OnDeleteMeetingListener() {
                    @Override
                    public void onMeetingDismiss(ECError reason, String meetingNo) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                            getInstance().notifyMeetingDis(meetingNo);
                            return;
                        }
                        getInstance().notifyError(reason);
                    }
                });
    }

    /**
     * 加入会议请求
     * @param meetingNo
     * @param password
     */
    public static void joinMeeting(String meetingNo , String password) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.joinMeetingByType(meetingNo, password, ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE, new ECMeetingManager.OnCreateOrJoinMeetingListener() {
            @Override
            public void onCreateOrJoinMeeting(ECError reason, String meetingNo) {
                if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                    getInstance().notifyMeetingStart(meetingNo);
                    return;
                }
                getInstance().notifyError(OnMeetingCallback.MEETING_JOIN, reason);
            }
        });
    }

    public static void exitMeeting() {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null ) {
            return ;
        }
        meetingManager.exitMeeting(ECMeetingManager.ECMeetingType.MEETING_MULTI_VOICE);
    }

    private void notifyMeetingMembers(List<? extends  ECMeetingMember> members) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnMeetingCallback> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onMeetingMembers(members);
            }
        }
    }

    private void notifyMeetingStart(String meetingNo) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnMeetingCallback> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onMeetingStart(meetingNo);
            }
        }
    }

    private void notifyMeetingDis(String meetingNo) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnMeetingCallback> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onMeetingDismiss(meetingNo);
            }
        }
    }

    private void notifyMeetings(List<ECMeeting> list) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnMeetingCallback> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onMeetings(list);
            }
        }
    }

    /**
     * 通知请求错误
     * @param e
     */
    private void notifyError(ECError e) {
        notifyError(-1 , e);
    }

    /**
     * 通知请求错误
     * @param e
     */
    private void notifyError(int type , ECError e) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnMeetingCallback> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onError(type ,e);
            }
        }
    }

    /**
     * 设置实时对讲监听
     * @param listener
     */
    public static void addInterPhoneCallback(OnMeetingCallback listener) {
        if(listener == null) {
            return ;
        }
        WeakReference<OnMeetingCallback> interListener = new WeakReference<OnMeetingCallback>(listener);
        sCallbacks.add(0,interListener);
    }

    /**
     * 移除实时对讲状态监听
     * @param listener
     */
    public static void removeInterPhoneCallback(OnMeetingCallback listener) {
        int size = sCallbacks.size();
        LogUtil.d(TAG, "removeCallback size " + size + " , " + listener);
        if(listener == null) {
            return ;
        }
        LinkedList<Integer> list = new LinkedList<Integer>();
        for(int i = 0 ; i < sCallbacks.size() ; i ++) {
            if(listener != sCallbacks.get(i).get()) {
                list.add(0 , Integer.valueOf(i));
            } else {
                sCallbacks.remove(i);
                LogUtil.d(TAG, "removeCallback directly, index " + i);
            }
        }

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            WeakReference<? extends OnMeetingCallback> remove = sCallbacks.remove(next.intValue());
            LogUtil.d(TAG , "removeCallback, popup " + (remove != null ? remove : "NULL-CALLBACK"));
        }

    }

    @Override
    public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {

    }

    @Override
    public void onReceiveVoiceMeetingMsg(ECVoiceMeetingMsg msg) {

    }

    @Override
    public void onReceiveVideoMeetingMsg(ECVideoMeetingMsg msg) {

    }

    public interface OnMeetingCallback {

        int QUERY_MEMBERS = 0x0012;
        int MEETING_JOIN = 0x0013;

        // 查询会议列表
        void onMeetings(List<ECMeeting> list);
        // 会议成员
        void onMeetingMembers(List<? extends ECMeetingMember> members);
        // 会议开始
        void onMeetingStart(String meetingNo);
        // 会议解散
        void onMeetingDismiss(String meetingNo);
        // 会议请求错误
        void onError(int type, ECError e);
    }


	@Override
	public void onVideoRatioChanged(VideoRatio arg0) {
		// TODO Auto-generated method stub
		
	}
}
