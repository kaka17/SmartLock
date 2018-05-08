package com.sht.smartlock.phone.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.common.view.TopBarView;
import com.sht.smartlock.phone.storage.GroupSqlManager;
import com.sht.smartlock.phone.ui.ContactListFragment;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.chatting.ChattingActivity;
import com.sht.smartlock.phone.ui.chatting.ChattingFragment;
import com.sht.smartlock.phone.ui.group.GroupMemberService;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.ECGroupManager.OnCreateGroupListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroup;

import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.contact in ECDemo_Android
 * Created by Jorstin on 2015/4/1.
 */
public class MobileContactSelectActivity  extends ECSuperActivity implements
        View.OnClickListener  , ContactListFragment.OnContactClickListener, OnCreateGroupListener, GroupMemberService.OnSynsGroupMemberListener {
    private ECProgressDialog mPostingdialog;
    private static final String TAG = "ECSDK_Demo.ContactSelectListActivity";
    /**查看群组*/
    public static final int REQUEST_CODE_VIEW_GROUP_OWN = 0x2a;
    private TopBarView mTopBarView;
    private boolean mNeedResult;
    
    private boolean isFromCreateDiscussion=false;
    @Override
    protected int getLayoutId() {
        return R.layout.layout_contact_select;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();

        mNeedResult = getIntent().getBooleanExtra("group_select_need_result", false);
        isFromCreateDiscussion=getIntent().getBooleanExtra("isFromCreateDiscussion", false);
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(R.id.contact_container) == null) {
            MobileContactActivity.MobileContactFragment list = MobileContactActivity.MobileContactFragment.newInstance(ContactListFragment.TYPE_SELECT);
            fm.beginTransaction().add(R.id.contact_container, list).commit();
        }
        mTopBarView = getTopBarView();
        String actionBtn = getString(R.string.radar_ok_count, getString(R.string.dialog_ok_button) , 0);
        mTopBarView.setTopBarToStatus(1, R.drawable.topbar_back_bt, R.drawable.btn_style_green, null, actionBtn, getString(R.string.select_contacts), null, this);
        mTopBarView.setRightBtnEnable(false);
        
      
        ECGroupManager ecGroupManager = SDKCoreHelper.getECGroupManager();
		if ( ecGroupManager == null) {
			finish();
			return;
		}
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	isFromCreateDiscussion=false;
    }
    
    /**
     * 
     * 获取讨论组名称
     */
    private ECGroup getDisGroup() {
		ECGroup group = new ECGroup();
		// 设置讨论组名称
		group.setName(getDisGroupName());
		// 设置讨论组公告
		group.setDeclare("");
		group.setScope(ECGroup.Scope.TEMP);
		// 讨论组验证权限，需要身份验证
		group.setPermission(ECGroup.Permission.AUTO_JOIN);
		// 设置讨论组创建者
		group.setOwner(CCPAppManager.getClientUser().getUserId());
		
		group.setProvince("");
		group.setCity("");
		group.setIsDiscuss(true);
		return group;
	}
    
    
   
    private String getDisGroupName(){
    	StringBuilder stringBuilder=new StringBuilder();
    	stringBuilder.append(CCPAppManager.getClientUser().getUserName());
    	stringBuilder.append("、");
    	for(int i=0;i<memberArrs.length;i++){
    		if(i==5){
    			break;
    		}
    		stringBuilder.append(memberArrs[i]);
    		if(!(i==memberArrs.length-1)){
    			stringBuilder.append("、");
    		}
    	}
    	stringBuilder.append("创建的讨论组");
    	
    	return stringBuilder.toString();
    }
    
    private String[] phoneArr;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                goBack();
                break;
            case R.id.text_right:
                List<Fragment> fragments = getSupportFragmentManager().getFragments();

                if(fragments.get(0) instanceof MobileContactActivity.MobileContactFragment) {
                    String chatuser = ((MobileContactActivity.MobileContactFragment) fragments.get(0) ).getChatuser();
                    String[] split = chatuser.split(",");
                    String userNameArr=((MobileContactActivity.MobileContactFragment) fragments.get(0) ).getChatuserName();
                    memberArrs = userNameArr.split(",");
                    phoneArr=split;
                    if(split.length == 1 && !mNeedResult) {
                        String recipient = split[0];
                        CCPAppManager.startChattingAction(MobileContactSelectActivity.this , recipient,recipient);
                        finish();
                        return ;
                    }
                    
                    if(mNeedResult&&isFromCreateDiscussion){
                    	if (split != null && split.length > 0) {
                			if(isFromCreateDiscussion){
                				  SDKCoreHelper.getECGroupManager().createGroup(getDisGroup(), this);
                				}
                			return;
                		}
                    }
                    if(mNeedResult) {
                        Intent intent = new Intent();
                        intent.putExtra("Select_Conv_User", split);
                        setResult(-1, intent);
                        finish();
                        return ;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupMemberService.addListener(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        hideSoftKeyboard();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult: requestCode=" + requestCode
                + ", resultCode=" + resultCode + ", data=" + data);

        // If there's no data (because the user didn't select a picture and
        // just hit BACK, for example), there's nothing to do.
        if (requestCode == REQUEST_CODE_VIEW_GROUP_OWN) {
            if (data == null) {
                return;
            }
        } else if (resultCode != RESULT_OK) {
            LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
            return;
        }

        String contactId = data.getStringExtra(ChattingFragment.RECIPIENTS);
        String contactUser = data.getStringExtra(ChattingFragment.CONTACT_USER);
        if(contactId != null && contactId.length() > 0) {
            Intent intent = new Intent(this ,  ChattingActivity.class);
            intent.putExtra(ChattingFragment.RECIPIENTS, contactId);
            intent.putExtra(ChattingFragment.CONTACT_USER, contactUser);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onContactClick(int count) {
        mTopBarView.setRightBtnEnable(count > 0 ? true:false);
        mTopBarView.setRightButtonText(getString(R.string.radar_ok_count, getString(R.string.dialog_ok_button) , count));
    }

    @Override
    public void onSelectGroupClick() {

    }

    private ECGroup mGroup;
	private String[] memberArrs;
	@Override
	public void onCreateGroupComplete(ECError error, ECGroup group) {
		
		
		if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
			// 创建的群组实例化到数据库
			// 其他的页面跳转逻辑
			group.setIsNotice(true);
			this.mGroup=group;
			GroupSqlManager.insertGroup(group, true, false, true);
			
			showCommonProcessDialog("");
			GroupMemberService.inviteMembers(mGroup.getGroupId(), "",
					ECGroupManager.InvitationMode.FORCE_PULL, phoneArr);
		} else {
			ToastUtil.showMessage("创建讨论组失败[" + error.errorCode + "]");
			finish();
		}
	}

	@Override
	public void onSynsGroupMember(String groupId) {
		dismissCommonPostingDialog();
		CCPAppManager.startChattingAction(MobileContactSelectActivity.this, groupId,
				mGroup.getName());
		finish();
		
	}

}
