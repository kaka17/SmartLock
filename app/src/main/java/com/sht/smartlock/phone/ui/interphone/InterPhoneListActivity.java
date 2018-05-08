package com.sht.smartlock.phone.ui.interphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.ui.MeetingMsgReceiver;
import com.sht.smartlock.phone.ui.contact.MobileContactSelectActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneInviteMsg;
import com.yuntongxun.ecsdk.meeting.intercom.ECInterPhoneMeetingMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.interphone in ECDemo_Android
 * Created by Jorstin on 2015/7/15.
 */
public class InterPhoneListActivity extends InterPhoneBaseActivity {

    private static final String TAG = "ECSDK_Demo.InterPhoneListActivity";
    public static final int SELECT_USER_FOR_INTERPHONE = 0x002;
    /**实时对讲房间列表*/
    private ListView mInterPhoneListView;
    /**实时对讲会议列表*/
    private InterPhoneAdapter mInterPhoneAdapter;

    private AdapterView.OnItemClickListener itemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mInterPhoneAdapter != null) {
                ECInterPhoneInviteMsg interPhone = mInterPhoneAdapter.getItem(position);
                if(interPhone != null) {
                    Intent intent = new Intent(InterPhoneListActivity.this , InterPhoneChatActivity.class);
                    intent.putExtra(ECDevice.MEETING_NO , interPhone.getMeetingNo());
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.ec_interphone_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.str_btn_launch_talkback),
                getString(R.string.ec_app_title_inter_phone), null, this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyInterPhoneChanged();
    }

    @Override
    public void onInterPhoneStart(String interNo) {
        super.onInterPhoneStart(interNo);

    }

    /**
     * 初始化界面资源控件
     */
    private void initView() {
        mInterPhoneListView = (ListView) findViewById(R.id.meeting_lv);
        View emptyView = findViewById(R.id.empty_meeting_tv);
        mInterPhoneListView.setEmptyView(emptyView);
        mInterPhoneListView.setOnItemClickListener(itemClickListener);
        mInterPhoneAdapter = new InterPhoneAdapter(this);
        mInterPhoneAdapter.setData(MeetingMsgReceiver.mInterPhones);
        mInterPhoneListView.setAdapter(mInterPhoneAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult: requestCode=" + requestCode
                + ", resultCode=" + resultCode + ", data=" + data);

        // If there's no data (because the user didn't select a picture and
        // just hit BACK, for example), there's nothing to do.
        if (requestCode == SELECT_USER_FOR_INTERPHONE) {
            if (data == null) {
                return;
            }
        } else if (resultCode != RESULT_OK) {
            LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
            return;
        }
        if(requestCode == SELECT_USER_FOR_INTERPHONE) {
            String[] selectUser = data.getStringArrayExtra("Select_Conv_User");
            if(selectUser != null && selectUser.length > 0) {
                Intent intent = new Intent(InterPhoneListActivity.this , InterPhoneChatActivity.class);
                intent.putExtra(InterPhoneChatActivity.EXTRA_MEMBERS , selectUser);
                startActivity(intent);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.text_right:
                Intent intent = new Intent(InterPhoneListActivity.this, MobileContactSelectActivity.class);
                intent.putExtra("group_select_need_result", true);
                intent.putExtra("select_type", false);
                startActivityForResult(intent, SELECT_USER_FOR_INTERPHONE);
                break;
        }
    }

    @Override
    public void onReceiveInterPhoneMeetingMsg(ECInterPhoneMeetingMsg msg) {
        super.onReceiveInterPhoneMeetingMsg(msg);

        notifyInterPhoneChanged();
    }

    /**
     * 刷新列表
     */
    private void notifyInterPhoneChanged() {
        mInterPhoneAdapter.setData(MeetingMsgReceiver.mInterPhones);
        mInterPhoneAdapter.notifyDataSetChanged();
    }


    public class InterPhoneAdapter extends ArrayAdapter<ECInterPhoneInviteMsg> {

        public InterPhoneAdapter(Context context) {
            super(context, 0, new ArrayList<ECInterPhoneInviteMsg>());
        }

        public void setData(List<ECInterPhoneMeetingMsg> datas) {
            clear();
            for(ECInterPhoneMeetingMsg msg : datas) {
                if(msg != null && msg instanceof ECInterPhoneInviteMsg) {
                    add((ECInterPhoneInviteMsg)msg);
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            InterPhoneHolder holder;
            if (convertView == null|| convertView.getTag() == null) {
                view = View.inflate(getContext(), R.layout.intephone_list_item, null);
                holder = new InterPhoneHolder();
                view.setTag(holder);

                holder.roomName = (TextView) view.findViewById(R.id.room_name);
            } else {
                view = convertView;
                holder = (InterPhoneHolder) convertView.getTag();
            }

            ECInterPhoneMeetingMsg phoneMeetingMsg = getItem(position);
            if(phoneMeetingMsg != null) {
                holder.roomName.setText(phoneMeetingMsg.getMeetingNo());
            }

            return view;
        }


        class InterPhoneHolder {
            TextView roomName;
        }
    }
}
