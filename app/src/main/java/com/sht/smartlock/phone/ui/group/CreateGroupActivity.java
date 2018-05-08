/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui.group;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECListDialog;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.storage.GroupSqlManager;
import com.sht.smartlock.phone.ui.ActivityTransition;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.contact.MobileContactSelectActivity;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroup;

/**
 * 群组创建功能
 * 
 * @author Jorstin Chan@容联•云通讯
 * @date 2014-12-27
 * @version 4.0
 */
@ActivityTransition(5)
public class CreateGroupActivity extends ECSuperActivity implements
		View.OnClickListener, ECGroupManager.OnCreateGroupListener,
		GroupMemberService.OnSynsGroupMemberListener {

	private static final String TAG = "ECDemo.CreateGroupActivity";
	String[] stringArray = null;
	String[] groupCodeArray = null;
	String[] groupContentArray = null;
	/** 群组名称 */
	private EditText mNameEdit;
	/** 群组公告 */
	private EditText mNoticeEdit;
	/** 创建按钮 */
	private Button mCreateBtn;
	/** 创建的群组 */
	private ECGroup group;
	private ECProgressDialog mPostingdialog;
	private Spinner mPermissionSpinner;
	private Button mSetPermission;
	private int mPermissionModel;
	private int mGroupTypePosition;

	final private TextWatcher textWatcher = new TextWatcher() {

		private int fliteCounts = 20;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			LogUtil.d(LogUtil.getLogUtilsTag(textWatcher.getClass()),
					"fliteCounts=" + fliteCounts);
			fliteCounts = filteCounts(s);
			if (fliteCounts < 0) {
				fliteCounts = 0;
			}
			if (checkNameEmpty()) {
				mCreateBtn.setEnabled(true);
				return;
			}
			mCreateBtn.setEnabled(false);
		}
	};
	private boolean isDiscussion =false;
	private Button buGroupType;
	private EditText mProviceEdit;
	private EditText mCityEdit;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stringArray = null;
		isDiscussion=false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.new_group;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stringArray = getResources().getStringArray(R.array.group_join_model);
		
		groupCodeArray=getResources().getStringArray(R.array.create_group_type_code);
		groupContentArray=getResources().getStringArray(R.array.create_group_type_content);

		isDiscussion = getIntent().getBooleanExtra("is_discussion", false);

		int resId = R.string.app_title_create_new_group;
		if (isDiscussion) {
			resId = R.string.discussion_create;
		}
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,
				resId, this);

		initView();
	}

	/**
	 * 关闭对话框
	 */
	private void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

	/**
     *
     */
	private void initView() {
		mNameEdit = (EditText) findViewById(R.id.group_name);

		mNoticeEdit = (EditText) findViewById(R.id.group_notice);
	mProviceEdit = (EditText) findViewById(R.id.group_provice);
	mCityEdit = (EditText) findViewById(R.id.group_city);
		
		buGroupType = (Button) findViewById(R.id.str_group_type);
		
		buGroupType.setOnClickListener(this);
		if (isDiscussion) {
			mNameEdit.setHint(R.string.discussion_name);
			mNoticeEdit.setHint(R.string.discussion_notice);
		}
		InputFilter[] inputFiltersNotice = new InputFilter[1];
		inputFiltersNotice[0] = new ITextFilter(1);
		mNoticeEdit.setFilters(inputFiltersNotice);

		mCreateBtn = (Button) findViewById(R.id.create);
		mCreateBtn.setOnClickListener(this);
		mCreateBtn.setEnabled(false);

		InputFilter[] inputFilters = new InputFilter[1];
		inputFilters[0] = new ITextFilter();
		mNameEdit.setFilters(inputFilters);
		mNameEdit.addTextChangedListener(textWatcher);

		mPermissionSpinner = (Spinner) findViewById(R.id.str_group_permission_spinner);
		
		
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.group_join_model,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mPermissionSpinner.setAdapter(adapter);
		mPermissionSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mPermissionModel = position;
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		mSetPermission = (Button) findViewById(R.id.str_group_permission_spinner2);
		mSetPermission.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPermissionDialog();
			}
		});
		initPermissionText();
	}

	/**
	 * @return
	 */
	private boolean checkNameEmpty() {
		return mNameEdit != null
				&& mNameEdit.getText().toString().trim().length() > 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			hideSoftKeyboard();
			finish();
			break;
		case R.id.str_group_type:
			
			showGroupTypeDialog();
			break;
		case R.id.create:
			hideSoftKeyboard();
			ECGroupManager ecGroupManager = SDKCoreHelper.getECGroupManager();
			if (!checkNameEmpty() || ecGroupManager == null) {
				return;
			}
			// 调用API创建群组、处理创建群组接口回调

			
			ECGroup group=getGroup();
			mPostingdialog = new ECProgressDialog(this,
					isDiscussion ? R.string.create_dis_posting
							: R.string.create_group_posting);
			mPostingdialog.show();
			
			ecGroupManager.createGroup(group, this);
			

			break;
		default:
			break;
		}
	}

	private boolean showPermissionDialog() {
		ECListDialog dialog = new ECListDialog(this, stringArray,
				mPermissionModel);
		dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
			@Override
			public void onDialogItemClick(Dialog d, int position) {
				mPermissionModel = position;
				initPermissionText();
			}
		});
		dialog.setTitle(R.string.str_group_permission_spinner);
		dialog.show();
		return true;
	}
	private boolean showGroupTypeDialog() {
		ECListDialog dialog = new ECListDialog(this, groupContentArray,
				mGroupTypePosition);
		dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
			@Override
			public void onDialogItemClick(Dialog d, int position) {
				mGroupTypePosition = position;
				initGroupTypeText();
			}
		});
		dialog.setTitle(R.string.str_group_permission_type);
		dialog.show();
		return true;
	}
	
	
	
	
	

	private void initPermissionText() {
		mSetPermission.setText(stringArray[mPermissionModel]);
	}
	
	private void initGroupTypeText(){
		
		buGroupType.setText(groupContentArray[mGroupTypePosition]);
	}

	/**
	 * 创建群组参数
	 * 
	 * @return
	 */
	private ECGroup getGroup() {
		ECGroup group = new ECGroup();
		// 设置群组名称
		group.setName(mNameEdit.getText().toString().trim());
		// 设置群组公告
		group.setDeclare(mNoticeEdit.getText().toString().trim());
		// 临时群组（100人）
		group.setScope(ECGroup.Scope.TEMP);
		// 群组验证权限，需要身份验证
		group.setPermission(ECGroup.Permission.values()[mPermissionModel + 1]);
		// 设置群组创建者
		group.setOwner(CCPAppManager.getClientUser().getUserId());
		
		group.setProvince(mProviceEdit.getText().toString().trim());
		group.setCity(mCityEdit.getText().toString().trim());
		
		group.setGroupType(Integer.parseInt(groupCodeArray[mGroupTypePosition]));

	
			group.setIsDiscuss(false);

		return group;
	}

	@Override
	protected void onResume() {
		super.onResume();
		GroupMemberService.addListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "onActivityResult: requestCode=" + requestCode
				+ ", resultCode=" + resultCode + ", data=" + data);

		// If there's no data (because the user didn't select a picture and
		// just hit BACK, for example), there's nothing to do.
		if (requestCode == 0x2a) {
			if (data == null) {
				finish();
				return;
			}
		} else if (resultCode != RESULT_OK) {
			LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
			finish();
			return;
		}

		String[] selectUser = data.getStringArrayExtra("Select_Conv_User");
		if (selectUser != null && selectUser.length > 0) {
			mPostingdialog = new ECProgressDialog(this,
					R.string.invite_join_group_posting);
			mPostingdialog.show();
			GroupMemberService.inviteMembers(group.getGroupId(), "",
					ECGroupManager.InvitationMode.FORCE_PULL, selectUser);
		}

	}

	@Override
	public void onCreateGroupComplete(ECError error, ECGroup group) {
		if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
			// 创建的群组实例化到数据库
			// 其他的页面跳转逻辑
			group.setIsNotice(true);

			GroupSqlManager.insertGroup(group, true, false, isDiscussion);
			this.group = group;
			Intent intent = new Intent(this, MobileContactSelectActivity.class);
			intent.putExtra("group_select_need_result", true);
			intent.putExtra("isFromCreateDiscussion", false);
			startActivityForResult(intent, 0x2a);
		} else {
			if(isDiscussion){
				ToastUtil.showMessage("创建讨论组失败[" + error.errorCode + "]");
			}else {
				
				ToastUtil.showMessage("创建群组失败[" + error.errorCode + "]");
			}
		}
		dismissPostingDialog();
	}

	@Override
	public void onSynsGroupMember(String groupId) {
		dismissPostingDialog();
		CCPAppManager.startChattingAction(CreateGroupActivity.this, groupId,
				group.getName());
		finish();
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static int filteCounts(CharSequence text) {
		int count = (30 - Math.round(calculateCounts(text)));
		LogUtil.v(LogUtil.getLogUtilsTag(SearchGroupActivity.class), "count "
				+ count);
		return count;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static float calculateCounts(CharSequence text) {

		float lengh = 0.0F;
		for (int i = 0; i < text.length(); i++) {
			if (!DemoUtils.characterChinese(text.charAt(i))) {
				lengh += 1.0F;
			} else {
				lengh += 0.5F;
			}
		}

		return lengh;
	}

	class ITextFilter implements InputFilter {
		private int limit = 50;

		public ITextFilter() {
			this(0);
		}

		public ITextFilter(int type) {
			if (type == 1) {
				limit = 128;
			}
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			LogUtil.i(LogUtil.getLogUtilsTag(CreateGroupActivity.class), source
					+ " start:" + start + " end:" + end + " " + dest
					+ " dstart:" + dstart + " dend:" + dend);
			float count = calculateCounts(dest);
			int overplus = limit - Math.round(count) - (dend - dstart);
			if (overplus <= 0) {
				if ((Float.compare(count, (float) (limit - 0.5D)) == 0)
						&& (source.length() > 0)
						&& (!(DemoUtils.characterChinese(source.charAt(0))))) {
					return source.subSequence(0, 1);
				}
				ToastUtil.showMessage("超过最大限制");
				return "";
			}

			if (overplus >= (end - start)) {
				return null;
			}
			int tepmCont = overplus + start;
			if ((Character.isHighSurrogate(source.charAt(tepmCont - 1)))
					&& (--tepmCont == start)) {
				return "";
			}
			return source.subSequence(start, tepmCont);
		}
	}
}
