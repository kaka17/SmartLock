package com.sht.smartlock.phone.ui.interphone;

import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.ui.MeetingMsgReceiver;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMeetingManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMember;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.interphone in ECDemo_Android
 * Created by Jorstin on 2015/7/16.
 */
public class InterPhoneHelper {
    private static final String TAG = "ECSDK_Demo.InterPhoneHelper";
    private static InterPhoneHelper ourInstance = new InterPhoneHelper();
    private static LinkedList<WeakReference<? extends  OnInterPhoneListener>> sCallbacks = new LinkedList<WeakReference<? extends  OnInterPhoneListener>>();
    public static InterPhoneHelper getInstance() {
        return ourInstance;
    }

    private InterPhoneHelper() {

    }

    /**
     * 发起实时对讲
     * @param members 参与实时对讲的成员
     */
    public static void startInterphone(String[] members) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            LogUtil.e(TAG, "start inter phone error meetingManager null");
            return ;
        }
        meetingManager.createInterPhoneMeeting(members, new ECMeetingManager.OnCreateOrJoinMeetingListener() {
            @Override
            public void onCreateOrJoinMeeting(ECError reason, String meetingNo) {
                if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                    // 发起实时对讲成功
                    getInstance().notifyInter(meetingNo);
                    MeetingMsgReceiver.putInterPhone(meetingNo);
                    return ;
                }
                getInstance().notifyError(reason);

            }
        });
    }

    /**
     * 加入实时对讲请求
     * @param interNo 实时对讲会议号
     */
    public static void joinInterPhone(String interNo) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            LogUtil.e(TAG , "join inter phone error meetingManager null");
            return ;
        }
        meetingManager.joinMeetingByType(interNo, "", ECMeetingManager.ECMeetingType.MEETING_INTERCOM,
                new ECMeetingManager.OnCreateOrJoinMeetingListener() {
                    @Override
                    public void onCreateOrJoinMeeting(ECError reason, String meetingNo) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                            // 加入实时对讲成功
                            getInstance().notifyInter(meetingNo);
                            return;
                        }
                        getInstance().notifyError(reason);
                    }
                });
    }

    /**
     * 查询实时对讲成员列表
     * @param interNo 实时对讲会议号
     */
    public static void queryInterPhoneMember(String interNo) {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            LogUtil.e(TAG , "query inter phone members error meetingManager null");
            return ;
        }
        meetingManager.queryMeetingMembersByType(interNo, ECMeetingManager.ECMeetingType.MEETING_INTERCOM,
                new ECMeetingManager.OnQueryMeetingMembersListener<ECInterPhoneMeetingMember>() {
                    @Override
                    public void onQueryMeetingMembers(ECError reason, List<ECInterPhoneMeetingMember> members) {
                        if (reason.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                            // 查询实时对讲成员成功
                            getInstance().notifyMembers(members);
                            return;
                        }
                        getInstance().notifyError(reason);
                    }
                });
    }

    /**
     * 退出实时对讲
     */
    public static void exitInterPhone() {
        ECMeetingManager meetingManager = ECDevice.getECMeetingManager();
        if(meetingManager == null) {
            LogUtil.e(TAG , "query inter phone members error meetingManager null");
            return ;
        }
        meetingManager.exitMeeting(ECMeetingManager.ECMeetingType.MEETING_INTERCOM);
    }

    /**
     * 刷新界面
     * @param inter
     */
    private void notifyInter(String inter) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnInterPhoneListener> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onInterPhoneStart(inter);
            }
        }
    }

    /**
     * 刷新界面实时对讲成员
     * @param members
     */
    private void notifyMembers(List<ECInterPhoneMeetingMember> members) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnInterPhoneListener> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onInterPhoneMembers(members);
            }
        }
    }

    private void notifyError(ECError e) {
        if(sCallbacks == null) {
            return ;
        }
        for(WeakReference<? extends  OnInterPhoneListener> cb : sCallbacks) {
            if(cb != null && cb.get() != null) {
                cb.get().onInterPhoneError(e);
            }
        }
    }

    /**
     * 设置实时对讲监听
     * @param listener
     */
    public static void addInterPhoneCallback(OnInterPhoneListener listener) {
        if(listener == null) {
            return ;
        }
        WeakReference<OnInterPhoneListener> interListener = new WeakReference<OnInterPhoneListener>(listener);
        sCallbacks.add(0 ,interListener);
    }

    /**
     * 移除实时对讲状态监听
     * @param listener
     */
    public static void removeInterPhoneCallback(OnInterPhoneListener listener) {
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
                LogUtil.d(TAG , "removeCallback directly, index " + i);
            }
        }

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            WeakReference<? extends OnInterPhoneListener> remove = sCallbacks.remove(next.intValue());
            LogUtil.d(TAG , "removeCallback, popup " + (remove != null ? remove : "NULL-CALLBACK"));
        }
    }

    /**
     * 用于应用层实时对讲Ui界面刷新
     */
    public interface OnInterPhoneListener {
        /**
         * 实时对讲创建或者加入成功
         * @param interNo 实时对讲号
         */
        void onInterPhoneStart(String interNo);

        /**
         * 实时对讲成员
         * @param members
         */
        void onInterPhoneMembers(List<ECInterPhoneMeetingMember> members);

        /**
         *
         * @param e
         */
        void onInterPhoneError(ECError e);
    }

}
