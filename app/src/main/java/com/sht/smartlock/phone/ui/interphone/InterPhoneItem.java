package com.sht.smartlock.phone.ui.interphone;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMember;

/**
 * 处理实时对讲成员状态图标显示
 * 以及加入、未加入、正在说话等状态
 * com.yuntongxun.ecdemo.ui.interphone in ECDemo_Android
 * Created by Jorstin on 2015/7/16.
 */
public class InterPhoneItem  {

    /**实时对讲加入成员状态*/
    private ImageView mInterSpeak;
    /**实时对讲加入成员昵称*/
    private TextView mUsernameView;
    /**实时对讲成员状态描述*/
    private TextView mUserActionView;

    public InterPhoneItem(View view) {
        initView(view);
    }

    /**
     * 初始化界面资源
     */
    private void initView(View view) {
        mInterSpeak = (ImageView) view.findViewById(R.id.inter_phone_join_icon);
        mUsernameView = (TextView) view.findViewById(R.id.name);
        mUserActionView = (TextView) view.findViewById(R.id.action_tips);
    }

    /**
     * 设置实时对讲成员信息
     * @param member 实时对讲成员信息
     */
    public void setInterMember(ECInterPhoneMeetingMember member) {
        if(member == null || member.getMember() == null) {
            return ;
        }

        mUsernameView.setText(member.getMember());
        // 如果实时对讲成员不在线，则不处理控麦等状态
        if(setInterMemberOnline(member.getOnline())) {
            // 处理控麦等状态
            setInterMemberMic(member.getMic());
        }

        // 如果实时对讲的成员账号是自己，则显示单独区分图标
        if(member.getMember().equals(CCPAppManager.getClientUser().getUserId())) {
            mInterSpeak.setImageResource(R.drawable.inter_person_icon);
        }
    }

    /**
     * 初始化实时对讲在线加入状态
     * @param online 实时对讲成员加入状态
     */
    private boolean setInterMemberOnline(ECInterPhoneMeetingMember.Online online) {
        if(online != ECInterPhoneMeetingMember.Online.ONLINE) {
            // 实时对讲成员状态显示未加入
            mInterSpeak.setImageResource(R.drawable.status_wait);
            mUserActionView.setText(R.string.str_join_wait);
            return false;
        }
        // 实时对讲成员在线
        return true;
    }

    /**
     * 设置实时对讲成员控麦状态
     * @param mic 实时对讲成员控麦状态
     */
    private void setInterMemberMic(ECInterPhoneMeetingMember.Mic mic) {
        if(mic == ECInterPhoneMeetingMember.Mic.MIC_CONTROLLER) {
            // 实时对讲成员控麦
            mInterSpeak.setImageResource(R.drawable.status_speaking);
            mUserActionView.setText(R.string.str_join_speaking);
            return ;
        }
        mInterSpeak.setImageResource(R.drawable.status_join);
        mUserActionView.setText(R.string.str_join_success);
    }
}
