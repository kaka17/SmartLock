package com.sht.smartlock.phone.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.storage.GroupMemberSqlManager;
import com.sht.smartlock.phone.ui.CCPListAdapter;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.chatting.base.EmojiconTextView;
import com.sht.smartlock.phone.ui.group.GroupMemberService;
import com.yuntongxun.ecsdk.im.ECGroupMember;

import java.util.ArrayList;

/**
 * com.yuntongxun.ecdemo.ui.contact in ECDemo_Android Created by Jorstin on
 * 2015/6/15.
 */
public class AtSomeoneUI extends ECSuperActivity implements
		View.OnClickListener, GroupMemberService.OnSynsGroupMemberListener {

	public static final String EXTRA_GROUP_ID = "at_group_id";
	public static final String EXTRA_CHAT_USER = "at_chat_user";
	public static final String EXTRA_SELECT_CONV_USER = "select_conv_user";

	private String mGroupId;
	private String mChatUser;
	private ListView mListView;
	private AtSomeAdapter mAdapter;
	private ECProgressDialog mPostingdialog;

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ECGroupMember member = mAdapter.getItem(position);
			if (!TextUtils.isEmpty(member.getVoipAccount())) {
				Intent intent = new Intent();
				intent.putExtra(EXTRA_SELECT_CONV_USER, member.getVoipAccount());
				setResult(RESULT_OK, intent);
			}
			finish();
		}
	};

	@Override
	protected int getLayoutId() {
		return R.layout.at_someone_ui;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
		mChatUser = getIntent().getStringExtra(EXTRA_CHAT_USER);

		GroupMemberService.addListener(this);
		GroupMemberService.synsGroupMember(mGroupId);

		initView();
		
	    
	}

	private void initView() {
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,
				R.string.room_at_someone, this);

		mListView = (ListView) findViewById(R.id.chatroom_member_lv);
		mListView.setOnItemClickListener(onItemClickListener);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			hideSoftKeyboard();
			finish();
			break;

		default:
			break;
		}
	}

	public static class AtSomeAdapter extends CCPListAdapter<ECGroupMember> {

		private String mGroupId;

		/**
		 * 构造方法
		 * 
		 * @param ctx
		 * @param o
		 */
		public AtSomeAdapter(Context ctx, ECGroupMember o, String groupId) {
			super(ctx, o);
			mGroupId = groupId;
		}

		@Override
		protected void notifyChange() {
			Cursor cursor = GroupMemberSqlManager
					.getGroupMembersByCursorExceptSelf(mGroupId);
			setCursor(cursor);
			super.notifyDataSetChanged();
		}

		@Override
		protected void initCursor() {
			notifyChange();
		}

		@Override
		protected ECGroupMember getItem(ECGroupMember member, Cursor cursor) {
			ECGroupMember person = new ECGroupMember();
			person.setBelong(mGroupId);
			person.setVoipAccount(cursor.getString(0));
			person.setDisplayName(cursor.getString(1));
			person.setRemark(cursor.getString(2));
			person.setRole(cursor.getInt(3));
			person.setBan(cursor.getInt(4) == 2 ? true : false);
			return person;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder mViewHolder;
			if (convertView == null || convertView.getTag() == null) {
				view = View.inflate(mContext, R.layout.at_someone_item, null);

				mViewHolder = new ViewHolder();
				mViewHolder.mAvatar = (ImageView) view
						.findViewById(R.id.content);
				mViewHolder.name_tv = (EmojiconTextView) view
						.findViewById(R.id.at_someone_item_nick);

				view.setTag(mViewHolder);
			} else {
				view = convertView;
				mViewHolder = (ViewHolder) view.getTag();
			}
			final ECGroupMember item = getItem(position);
			if (item != null) {
				item.setDisplayName(TextUtils.isEmpty(item.getDisplayName()) ? item
						.getVoipAccount() : item.getDisplayName());
				mViewHolder.name_tv.setText(item.getDisplayName());
				mViewHolder.mAvatar.setImageBitmap(ContactLogic.getPhoto(item
						.getRemark()));
			}

			return view;
		}

		static class ViewHolder {
			/** 头像 */
			ImageView mAvatar;
			/** 名称 */
			EmojiconTextView name_tv;
		}
	}

	@Override
	public void onSynsGroupMember(String groupId) {
		if (groupId == null || !mGroupId.equals(groupId)) {
			return;
		}
		ArrayList<ECGroupMember> members = GroupMemberSqlManager
				.getGroupMemberWithName(mGroupId);

		mAdapter = new AtSomeAdapter(this, new ECGroupMember(), mGroupId);

		mListView.setAdapter(mAdapter);
	}
	
	
	

	
}
