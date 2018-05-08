package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.entity.GroupEntity;
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;

import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

	private List<GroupEntity> group; // 组列表
	private List<List<LockGroupsChatEntity>> child; // 子列表
	private Context context;
	private EMChatRoom room;

	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					TextView textView= (TextView) msg.obj;
					textView.setText((Integer) textView.getTag()+"");
					break;
			}

		}
	};

	public ExpandableListViewAdapter(List<GroupEntity> group,
									 List<List<LockGroupsChatEntity>> child, Context context) {
		super();
		this.group = group;
		this.child = child;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		try {
			return child.get(groupPosition).size();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView title;
		TextView num;
		TextView nickname;
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.expandable_group_item, null);
		}
		/* 判断是否group张开，来分别设置背景图 */
		if (isExpanded) {
		} else {
		}
		TextView tv_name = (TextView) convertView
				.findViewById(R.id.tv_name);
		tv_name.setText(group.get(groupPosition).getName());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 final TextView tv_Many_Person;
		 TextView tv_LockName;
		 TextView tv_Lock_Distance;
		 View view_enpty;
		 ImageView iv_lock_logo;
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.mylock_item, null);
		}
		tv_Many_Person= (TextView) convertView.findViewById(R.id.tv_Many_Person);
		tv_LockName= (TextView) convertView.findViewById(R.id.tv_LockName);
		tv_Lock_Distance= (TextView) convertView.findViewById(R.id.tv_Lock_Distance);
		iv_lock_logo= (ImageView) convertView.findViewById(R.id.iv_lock_logo);
		view_enpty=convertView.findViewById(R.id.view_enpty);

		tv_LockName.setText(child.get(groupPosition).get(childPosition).getCaption());
		tv_Many_Person.setText(child.get(groupPosition).get(childPosition).getOn_num());
		tv_Lock_Distance.setText(child.get(groupPosition).get(childPosition).getAddress());
		String img=child.get(groupPosition).get(childPosition).getPicture();
		//获取聊天室的Emid
		final String roomId=child.get(groupPosition).get(childPosition).getEmid();
//		BaseApplication.toast(""+roomId);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				room = EMChatManager.getInstance().getChatRoom(roomId);
//				if (room!=null) {
//					Message message = Message.obtain();
//					if (room.getAffiliationsCount() != -1 && room.getAffiliationsCount() > 0) {
//						tv_Many_Person.setTag(room.getAffiliationsCount());
//					}
//					message.obj = tv_Many_Person;
//					message.what = 1;
//					handler.sendMessage(message);
//				}
//			}
//		}).start();
		if(img!=null) {
			ImageLoader.getInstance().displayImage(img, iv_lock_logo);

		}



		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
