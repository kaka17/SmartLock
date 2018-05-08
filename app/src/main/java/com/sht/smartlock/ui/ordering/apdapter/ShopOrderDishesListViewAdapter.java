package com.sht.smartlock.ui.ordering.apdapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.ordering.entity.ProductType;


/**
 * @author Administrator
 *	点餐界面中左侧菜单分类ListView的Adapter
 */
@SuppressLint("InflateParams")
public class ShopOrderDishesListViewAdapter extends BaseAdapter implements ListAdapter {

	private List<ProductType> shopMenus;
	private LayoutInflater inflater;
	private Context mContext;
	private int selectedPosition = -1;
	private View oldConvertView;
	
	public ShopOrderDishesListViewAdapter(List<ProductType> shopMenus, Context context) {
		this.shopMenus=shopMenus;
		this.mContext=context;
		inflater=LayoutInflater.from(this.mContext);
		
	}
	/**
	 * 为外部提供接口设置item是否选中的状态
	 * @param selected
	 */
	public void setSelected(int selected){
		selectedPosition = selected;
//		setSelect(selected);
	}
	public void setSelect(int position) {
		for (int i = 0; i < shopMenus.size(); i++) {
			if (i == position) {
				shopMenus.get(i).setIschenk(true);
			} else {
				shopMenus.get(i).setIschenk(false);
			}
		}
		selectedPosition=position;
	}

	@Override
	public int getCount() {
		return shopMenus.size();
	}

	@Override
	public Object getItem(int position) {
		return shopMenus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mholder ;
		if(convertView==null){
//			convertView=inflater.inflate(android.R.layout.simple_list_item_1,null);
			convertView=inflater.inflate(R.layout.ordercapty_item,parent,false);
			mholder=new ViewHolder(convertView);
			convertView.setTag(mholder);
		}else {
			mholder= (ViewHolder) convertView.getTag();
		}
//		TextView textView=(TextView) convertView.findViewById(android.R.id.text1);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//		mholder.tvCapety.setLayoutParams(params);
		mholder.tvCapety.setText(shopMenus.get(position).getTypeName());
//		Log.e("TRG","--------------position"+position+"--->NUM="+shopMenus.get(position).getShoppingNum());
		if (shopMenus.get(position).getShoppingNum()==0){
			mholder.tvNums.setText(0+"");
			mholder.tvNums.setVisibility(View.GONE);
		}else {
			mholder.tvNums.setText(shopMenus.get(position).getShoppingNum()+"");
			mholder.tvNums.setVisibility(View.VISIBLE);
		}
		if (selectedPosition ==position) {
//			convertView.setBackgroundResource(R.drawable.selector_choose_eara_ite);
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.whitesmoke));
			mholder.tvCapety.setTextColor(mContext.getResources().getColor(R.color.ORderTepyfs));
		}else {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.whitesmoke));
			mholder.tvCapety.setTextColor(mContext.getResources().getColor(R.color.LOCKHourText));
		}
//		ProductType productType = shopMenus.get(position);
//		if (productType.ischenk()){
//			convertView.setBackgroundResource(R.drawable.selector_choose_eara_ite);
//		}else {
//			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.whitesmoke));
//		}
		return convertView;
	}
	class ViewHolder{
		private TextView tvCapety,tvNums;
		public ViewHolder(View view) {
			tvCapety= (TextView) view.findViewById(R.id.tvCapety);
			tvNums= (TextView) view.findViewById(R.id.tvNums);

		}
	}
}
