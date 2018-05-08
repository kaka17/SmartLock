package com.sht.smartlock.ui.ordering.apdapter;

import java.util.Iterator;
import java.util.List;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.baidu.android.common.logging.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.helper.Tools;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.entity.ProductType;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;


/**
 * @author Administrator
 *	点餐界面中的右侧列表ExpandableListView的Adapter
 */
@SuppressLint("InflateParams")
public class ShopOrderDishesExpandListViewAdapter extends
		BaseExpandableListAdapter {
	
	private OrderFoodActivity orderActivity;
	private LayoutInflater fatherInflater,sonInflater;
	/** 菜单分类，及分类下的菜单 */
	private List<ProductType> categoryList;
	private Bag productList;
	private boolean flag = false;
	private MySQLiteOpenHelper mydbHelper = null;
	private ViewGroup anim_mask_layout;// 动画层
	private ImageView buyImg;// 这是在界面上跑的小图片
	private TextView shopCart;// 购物车
	private String tab;//判断是购物或者是订餐
	private MyItemClickListener myOnClick;

	//private ImageLoader imageLoader;
	private DisplayImageOptions options;
	/**
	 * 保存商品的数量
	 */

	public ShopOrderDishesExpandListViewAdapter(List<ProductType> categoryList,OrderFoodActivity baseActivity,Bag productList,TextView order_cart,String tab){
		this.categoryList = categoryList;
		this.orderActivity = baseActivity;
		fatherInflater = this.orderActivity.getLayoutInflater();
		sonInflater = this.orderActivity.getLayoutInflater();
		this.productList=productList;
		mydbHelper = new MySQLiteOpenHelper(orderActivity);
		this.shopCart=order_cart;
		this.tab=tab;
		//显示图片的配置
		if (tab.equals("1")) {//订餐
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.pic_foods_defaut)
					.showImageOnFail(R.drawable.pic_foods_defaut)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		}else {//购物
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.pic_shopping_default)
					.showImageOnFail(R.drawable.pic_shopping_default)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		}


	}
	public void setMyOnClick(MyItemClickListener myOnClick){
		this.myOnClick=myOnClick;
	}
	@Override
	public int getGroupCount() {
		return this.categoryList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(categoryList.get(groupPosition).getProductList()!=null){
			return categoryList.get(groupPosition).getProductList().size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.categoryList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ProductType productMenu=this.categoryList.get(groupPosition);//获取分类
		return productMenu.getProductList().get(childPosition);//该分类下的商品
	}

	@Override
	public long getGroupId(int groupPosition) {
//		ProductType productMenu=this.categoryList.get(groupPosition);//获取分类
//		return productMenu.getId();
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
//		ProductType productMenu=this.categoryList.get(groupPosition);//获取分类
//		return productMenu.getProductList().get(childPosition).getId();//商品的id
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TypeViewHolder holder = null;
		if(convertView==null){
			convertView=fatherInflater.inflate(R.layout.shop_order_dishes_father_item, null);
			holder = new TypeViewHolder();
			holder.tvTypeName = (TextView) convertView.findViewById(R.id.tv_category_name);
			convertView.setTag(holder);
		}else{
			holder = (TypeViewHolder) convertView.getTag();
		}
		holder.tvTypeName.setText(categoryList.get(groupPosition).getTypeName());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ProductType productMenu=categoryList.get(groupPosition);
		final Product product = productMenu.getProductList().get(childPosition);
//		String imageUrl = Contants.SERVER_PHOTO_URL_BASE+ product.getImgUrl();
		String imageUrl = product.getThumbnail();
//		imageUrl="http://192.168.1.113/smartlock/a.jpg";
		final ViewHolder holder;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=sonInflater.inflate(R.layout.shop_order_dishes_son_item, null);
			holder.imgIcon=(ImageView) convertView.findViewById(R.id.img_product_icon);
			holder.tvTitle=(TextView) convertView.findViewById(R.id.tv_order_product_title);
			holder.tvSinglePrice=(TextView) convertView.findViewById(R.id.tv_product_single_price);
			holder.imgOrderSub=(ImageView) convertView.findViewById(R.id.img_order_sub);
			holder.tvOrderNum=(TextView) convertView.findViewById(R.id.tv_order_num);
			holder.imgOrderAdd=(ImageView) convertView.findViewById(R.id.img_order_add);
			holder.tvTime=(TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		holder.imgOrderAdd.setVisibility(View.VISIBLE);
		if(imageUrl!=null&&!imageUrl.equals("null")) {
			ImageLoader.getInstance().displayImage(imageUrl, holder.imgIcon,options);
		}else {
//			imageUrl="http://192.168.1.113/smartlock/a.jpg";
			ImageLoader.getInstance().displayImage(imageUrl, holder.imgIcon,options);
		}
		holder.tvTitle.setText(product.getName());
		holder.tvSinglePrice.setText("￥"+ NumberUtils.format(product.getPrices()));
//		final int pnum = productList.getCount(product);

		if (product.getOpen_time()!=null&& ! product.getOpen_time().equals("null")&&! product.getOpen_time().isEmpty()&&
				product.getClose_time()!=null&& ! product.getClose_time().equals("null")&& ! product.getClose_time().isEmpty()){
			String operTime=Integer.parseInt(product.getOpen_time())/60+":"+Integer.parseInt(product.getOpen_time())%60;
			String closeTime=Integer.parseInt(product.getClose_time())/60+":"+Integer.parseInt(product.getClose_time())%60;
			holder.tvTime.setText("营业时间："+operTime+"-"+closeTime);
		}else {
			holder.tvTime.setText("营业时间：未设置");
		}
		final int time = getTime();
//		Log.e("TIME", "--->time" + time);

		final int pnum = product.getNum();

		if(pnum == 0){
			isShow(holder, View.GONE);//在没有选中的商品的时候，隐藏减少按钮
		}else if (pnum>0) {
			isShow(holder, View.VISIBLE);//显示减少按钮
		}
		holder.tvOrderNum.setText(pnum+"");


		if (tab.equals("1")){//购物
//			holder.imgOrderAdd.setImageResource(R.drawable.shoppingadd);
		}


		//减少按钮
		holder.imgOrderSub.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//数据库存储
				if (product.getNum()-1==0){

					//数据库删除
					if (tab.equals("1")){//购物
//						String sql="delete from shopping_words where name='"+product.getName()+"'";
						String sql="delete from shopping_words where ID_item='"+product.getID()+"'";
						boolean isdelete=mydbHelper.delete(sql);
//					BaseApplication.toast(isdelete+"删除缓存");
					}else {//订餐
//						String sql="delete from tb_words where name='"+product.getName()+"'";
						String sql="delete from tb_words where ID_item='"+product.getID()+"'";
						boolean isdelete=mydbHelper.delete(sql);
					}
					productList.remove(product);
				}else {
					getMySQLData(product.getName(), product.getPrices() + "", product.getNum() + "",product, 0);
				}
				product.setNum(product.getNum() - 1);
				//
				orderActivity.updateBottomStatus(getTotalPrice(), getTotalNumber());
				ShopOrderDishesExpandListViewAdapter.this.notifyDataSetChanged();
				myOnClick.onItemClick(v,childPosition);
			}
		});
		//增加
		holder.imgOrderAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(product.getOpen_time()!=null&& ! product.getOpen_time().equals("null")&& ! product.getOpen_time().isEmpty()&&
						product.getClose_time()!=null&& ! product.getClose_time().equals("null")&& ! product.getClose_time().isEmpty()){

				}else {
					AppContext.toast("该商品未设置营业时间，不能下单");
					return;
				}
				if (Integer.parseInt(product.getOpen_time())<time&&time<Integer.parseInt(product.getClose_time())){
					//先查询数据库是否存储有数据
					getMySQLData(product.getName(), product.getPrices() + "", product.getNum()+"",product,1);
					product.setNum(product.getNum()+1);

					//设置动画
					int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
					v.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
					buyImg = new ImageView(orderActivity);
					buyImg.setImageBitmap(getAddDrawBitMap(product.getPrices()));// 设置buyImg的图片
					setAnim(buyImg, start_location);// 开始执行动画
					//
					productList.add(product);
					orderActivity.updateBottomStatus(getTotalPrice(), getTotalNumber());
					ShopOrderDishesExpandListViewAdapter.this.notifyDataSetChanged();
					myOnClick.onItemClick(v,childPosition);
				}else {
					AppContext.toast("该商品不在营业时间，不能下单");
				}

			}
		});
		
		return convertView;
	}

	//设置动画的方法
	private void setAnim(final View v, int[] start_location) {
		anim_mask_layout = null;
		anim_mask_layout = createAnimLayout();
		anim_mask_layout.addView(v);// 把动画小球添加到动画层
		final View view = addViewToAnimLayout(anim_mask_layout, v,
				start_location);
		int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
		shopCart.getLocationInWindow(end_location);// shopCart是那个购物车

		// 计算位移
		int endX = 0 - start_location[0] + 40;// 动画位移的X坐标
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标
		TranslateAnimation translateAnimationX = new TranslateAnimation(0,
				endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		AnimationSet set = new AnimationSet(false);
		set.setFillAfter(false);
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(800);// 动画的执行时间
		view.startAnimation(set);
		// 动画监听事件
		set.setAnimationListener(new Animation.AnimationListener() {
			// 动画的开始
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			// 动画的结束
			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
//				buyNum++;// 让购买数量加1
				// buyNumView.setText(buyNum + "");//
				// buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				// buyNumView.show();
			}
		});

	}
	/**
	 * @Description: 创建动画层
	 * @param
	 * @return void
	 * @throws
	 */
	private ViewGroup createAnimLayout() {
		ViewGroup rootView = (ViewGroup) orderActivity.getWindow().getDecorView();
		LinearLayout animLayout = new LinearLayout(orderActivity);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(Integer.MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;
	}

	private View addViewToAnimLayout(final ViewGroup vg, final View view,
									 int[] location) {
		int x = location[0];
		int y = location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}
	public Bitmap getAddDrawBitMap(double price) {
		Tools tools = new Tools();
		View drawableViewPar = LayoutInflater.from(orderActivity).inflate(R.layout.food_list_item_operation, null);
		TextView text = (TextView) drawableViewPar.findViewById(R.id.food_list_item_price_text_view);
		text.setText("￥" + price);
		return tools.convertViewToBitmap(text);
	}



	private void getMySQLData(String name,String price,String num,Product product,int isadd){//isadd  1为添加0 为减少
		 // 把数据存储到数据库中
		if (tab.equals("1")){//购物-----------------------------------------------------------
			String sqlString2 = "select * from shopping_words where name=? and price=? and num=? and ID_item=?";
			int count = mydbHelper.selectCount(
					sqlString2,
					new String[] {name,price,num,product.getID()});
//			BaseApplication.toast(count+"数据是否已存在");
			//数据库的数量跟着改变
			if (isadd==1){
				int n=Integer.parseInt(num)+1;
				num=n+"";
			}else {
				int n=Integer.parseInt(num)-1;
				num=n+"";
			}
			if (count > 0) {
				// "数据已经存在需要更改
				String sqlUpda="update shopping_words set num=? where ID_item='"+product.getID()+"'";
				boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
//				BaseApplication.toast(isfale+"数据更改");
			} else {
				// 先插入数据再查询数据
				String sql = "insert into shopping_words (name ,price,num,ID_item) values (?,?,?,?)";
				flag = mydbHelper.execData(
						sql,
						new Object[] {name,price,num ,product.getID()});
//				BaseApplication.toast(flag+"数据存储");
			}


		}else {//订餐 -------------------------------------------------------------------

			String sqlString2 = "select * from tb_words where name=? and price=? and num=? and ID_item=?";
			int count = mydbHelper.selectCount(
					sqlString2,
					new String[] {name,price,num,product.getID()});
//			BaseApplication.toast(count+"数据是否已存在");
			//数据库的数量跟着改变
			if (isadd==1){
			int n=Integer.parseInt(num)+1;
				num=n+"";
			}else {
				int n=Integer.parseInt(num)-1;
				num=n+"";
			}
			if (count > 0) {
				// "数据已经存在需要更改
			String sqlUpda="update tb_words set num=? where ID_item='"+product.getID()+"'";
			boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
//				BaseApplication.toast(isfale+"数据更改");
			} else {
				// 先插入数据再查询数据
				String sql = "insert into tb_words (name ,price,num,ID_item) values (?,?,?,?)";
				flag = mydbHelper.execData(
						sql,
						new Object[] {name,price,num ,product.getID()});
//				BaseApplication.toast(flag+"数据存储");
			}

		}
	}

	/**
	 * 获取总金额
	 * @return
	 */
	public double getTotalPrice(){
		double totalProce = 0;
		if(productList.size() == 0){
			return totalProce;
		}
		for(Iterator<?> itr = productList.uniqueSet().iterator();itr.hasNext();){
			Product product = (Product)itr.next();
			Double price=product.getPrices();
			totalProce += product.getPrices()*product.getNum();
//			totalProce+=price;
			totalProce=NumberUtils.toDouble(NumberUtils.format(totalProce));
		}
		return totalProce;
	}
	
	/**
	 * 获取总数量
	 * @return
	 */
	public int getTotalNumber(){
		int totalNumber = 0;
		if(productList.size() == 0){
			return totalNumber;
		}
		for(Iterator<?> itr = productList.uniqueSet().iterator();itr.hasNext();){
			Product product = (Product)itr.next();
			totalNumber +=product.getNum();
		}
		return totalNumber;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private final class TypeViewHolder{
		public TextView tvTypeName;//分类名称
	}
	
	private final class ViewHolder{
		public ImageView imgIcon;//产品图标
		public TextView tvTitle;//产品名称
		public TextView tvSinglePrice;//产品单价
		public ImageView imgOrderSub;//减少
		public TextView tvOrderNum;//产品预定数量
		public ImageView imgOrderAdd;//增加
		private TextView tvTime;
	}

	
	/**
	 * 是否显示减少按钮和商品数量
	 * @param holder
	 */
	private void isShow(ViewHolder holder,int isVisible){
		holder.tvOrderNum.setVisibility(isVisible);
		holder.imgOrderSub.setVisibility(isVisible);
	}

	private int getTime(){
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		t.setToNow(); // 取得系统时间。
		int year = t.year;
		int month = t.month;
		int date = t.monthDay;
		int hour = t.hour; // 0-23
		int minute = t.minute;
		int second = t.second;
		return hour*60+minute;
	}

}
