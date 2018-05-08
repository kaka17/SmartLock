package com.sht.smartlock.phone.common.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;


import com.sht.smartlock.R;

import java.util.ArrayList;
import java.util.List;

public class CCPAlertDialog {

	private Dialog mDialog;
	private Context mContext;
	
	
	private int mTitle;
	private int[] mNormal;
	private int mSpecial;
	private int mCancle;
	
	private LayoutInflater mInflater;
	private ListView mListView;
	

	private OnPopuItemClickListener onItemClickListener;
	private WindowManager mWindowManager;

	public void setOnItemClickListener(OnPopuItemClickListener l) {
		this.onItemClickListener = l;
	}

	public OnCancelListener getOnCancelListener() {
		return mOnCancelListener;
	}

	public void setOnCancelListener(OnCancelListener mOnCancelListener) {
		this.mOnCancelListener = mOnCancelListener;
	}
	
	public void setOnDismissListener(OnDismissListener mOnDismissListener) {
		this.mOnDismissListener = mOnDismissListener;
	}

	private PopupAdapter mAdapter;
	private OnCancelListener mOnCancelListener;
	private OnDismissListener mOnDismissListener;
	
	private Object userData;

	/**
	 * 
	 * @param context
	 * @param title 对话框标题 不设标题则0
	 * @param normal 多选按钮/灰色
	 * @param special 特殊按钮/红色
	 * @param cancle 取消按钮
	 */
	public CCPAlertDialog(Context context , int title, int[] normal ,int special ,int cancle){
		release();
		this.mContext = context ;
		this.mInflater = LayoutInflater.from(context);
		this.mDialog = new Dialog(context, R.style.CCPAlertdialog);
		this.mTitle = title;
		this.mSpecial = special;
		this.mNormal = normal;
		this.mCancle = cancle;
	}
	
	
	public Dialog create(){
		
		final LinearLayout mLlayout = (LinearLayout) this.mInflater.inflate(R.layout.ccp_dialog_menu_layout, null);
		mLlayout.setMinimumWidth(LayoutParams.MATCH_PARENT);
		mListView = (ListView)mLlayout.findViewById(R.id.content_list);


		mAdapter = new PopupAdapter(mContext, mTitle ,mNormal, mSpecial, mCancle);
		mListView.setAdapter(mAdapter);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mAdapter != null){
					if(onItemClickListener != null){
						int item = (Integer) mAdapter.getItem(position);
						if(userData != null) view.setTag(userData);
						onItemClickListener.onItemClick(mListView ,view ,position ,item);
					}
					if(mDialog != null){
						mDialog.dismiss();
					}
				}
			}
		});
		WindowManager.LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.x = 0;
		attributes.y = -1000;
		attributes.gravity = Gravity.BOTTOM;
		mDialog.onWindowAttributesChanged(attributes);
		mDialog.setCanceledOnTouchOutside(true);
		 if (this.mOnCancelListener != null)
	        this.mDialog.setOnCancelListener(mOnCancelListener);
		 if(this.mOnDismissListener != null) {
			 this.mDialog.setOnDismissListener(mOnDismissListener);
		 }
		 mDialog.setContentView(mLlayout);
		
		return mDialog;
	}

	public void show(){
		if(mDialog == null){
			throw new NullPointerException("this Dialog show is " + mDialog);
		}
		
		mDialog.show();
		if(mWindowManager == null){
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		Display display = mWindowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		mDialog.getWindow().setAttributes(lp);
	}
	
	class PopupAdapter extends BaseAdapter {

		private List<Integer> mList;
		private int[] type;

		private LayoutInflater mInflater;

		private static final int TYPE_BUTTON_NORMAL = 0;
		private static final int TYPE_BUTTON_SPECIAL = 1;
		private static final int TYPE_BUTTON_CANCLE = 2;
		private static final int TYPE_BUTTON_TITLE = 3;

		
		public PopupAdapter(Context context, int title ,int[] list,
				int redButtonStr, int cancleStr) {
			mInflater = LayoutInflater.from(mContext);
			this.mList = new ArrayList<Integer>();
			
			this.type = new int[list==null?3:(list.length+2) + this.mList.size()];
			if(title != 0) {
				this.type[0] = TYPE_BUTTON_TITLE;
				mList.add(0, title);
			}
			
			//this.mList.add(0,mContext.getResources().getString(R.string.popu_dialog_help_desc));
			//this.type[0] = TYPE_BUTTON_TITLE;
			if (/*redButtonStr != null && !"".equals(redButtonStr)*/redButtonStr != 0) {
				int index = 0;
				if(title != 0) {
					index = 1;
				}
				this.type[index] = TYPE_BUTTON_SPECIAL;
				mList.add(index, redButtonStr);
			}
			
			if (list != null && list.length != 0) {
				for(int str : list){
					mList.add(str);
				}
			}
			if (/*cancleStr != null && !"".equals(cancleStr)*/cancleStr != 0) {
				this.type[this.mList.size()] = TYPE_BUTTON_CANCLE;
				mList.add(cancleStr);
			}
		}

		@Override
		public int getCount() {
			return this.mList.size();
		}

		@Override
		public Object getItem(int position) {
			return this.mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int btnType = this.type[position];
			if (this.mList != null) {
				String btnStr = mContext.getResources().getString((Integer) getItem(position));
				if (btnType != TYPE_BUTTON_NORMAL) {
					View view = null;
					if (btnType == TYPE_BUTTON_SPECIAL) {
						view = mInflater.inflate(
								R.layout.ccp_dialog_menu_list_layout_special,
								null);
					} else if (btnType == TYPE_BUTTON_CANCLE) {
						view = mInflater
								.inflate(
										R.layout.ccp_dialog_menu_list_layout_cancel,
										null);
					} else if (btnType == TYPE_BUTTON_TITLE){
						view = mInflater
						.inflate(
								R.layout.ccp_dialog_menu_list_layout_title,
								null);
					}
					((TextView) view.findViewById(R.id.popup_text)).setText(btnStr);
					return view;
				} 
				
				PopuHolder holder = null;
				View view = null;
				if (convertView != null && (PopuHolder)convertView.getTag() != null &&((PopuHolder)convertView.getTag()).type == TYPE_BUTTON_NORMAL) {
					holder = (PopuHolder) convertView.getTag();
					view = convertView;
				} else {
					view = mInflater
							.inflate(R.layout.ccp_dialog_menu_list_layout, null);
					holder = new PopuHolder();
					holder.title = (TextView) view.findViewById(R.id.popup_text);
					holder.type = TYPE_BUTTON_NORMAL;
					view.setTag(holder);
				}
				
				holder.title.setText(btnStr);
				return view;
			}

			return null;
		}

		@Override
		public boolean isEnabled(int position) {
			/*if(position == 0){
				return false;
			}*/
			return super.isEnabled(position);
		}
		
		class PopuHolder {
			TextView title;
			int type;
		}
	}
	
	
	public void setUserData(Object object) {
		userData = object;
	}
	
	
	public Object getUserData() {
		return userData;
	}

	public void release() {
		userData = null;
	}
	
	public static interface OnPopuItemClickListener{
		/**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param resourceId The resource id that the button text.
         */
		void onItemClick(ListView parent, View view,
						 int position, int resourceId);
	}
	

}

