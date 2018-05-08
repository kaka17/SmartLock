package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
//import com.sht.smartlock.ui.activity.fragment.MainTabIndexFragment;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.OrderEntity;
import com.sht.smartlock.ui.ordering.apdapter.MyOrdering_Adapter;
import com.sht.smartlock.ui.ordering.apdapter.ShopOrderDishesExpandListViewAdapter;
import com.sht.smartlock.ui.ordering.apdapter.ShopOrderDishesListViewAdapter;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.entity.ProductType;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;
import com.sht.smartlock.ui.ordering.widget.PinnedHeaderExpandableListView;
import com.sht.smartlock.util.DoubleUtil;
import com.sht.smartlock.util.ViewUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderFoodActivity extends BaseActivity implements
        View.OnClickListener, ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, PinnedHeaderExpandableListView.OnHeaderUpdateListener,MyItemClickListener {

    private ListView categoryList;
    private ShopOrderDishesListViewAdapter listViewAdapter;
    private PinnedHeaderExpandableListView expandableListView;
    public ShopOrderDishesExpandListViewAdapter expandListViewAdapter;
    private TextView tvTotalPrice;
    private TextView btnFinish;
    private RelativeLayout rela_Order_menu;
    private TextView tv_Ordering_Num;
    private List<ProductType> shopMenuList = new ArrayList<>();//总数据

    private List<Product> myShopList = new ArrayList<>();//每一个分类里的商品集合
    private Bag productList = new HashBag();//选购了的物品存储
    // 弹出框的属性
    private View upView;
    private PopupWindow upWindow;

    private RelativeLayout relativeBottom;//底部

    private int oldPositiion = 0;
    private double oldPrice;//获取在上次退出时，在该店预定的商品的总价格
    private MySQLiteOpenHelper mydbHelper = null;

    private int selectPosition = 0;
    private ListView lv_MyOrdering;
    private MyOrdering_Adapter adapter_ordering;
    private String tab;
    private TextView tv_title;
    private String tpye;
    private boolean isclick = true;
    private boolean isflat = false;//用于判断是否设置了适配器
    private int index = 0;
    private boolean isNoList = false;
    //    private boolean isBottom;
//    private boolean isSelect = true;
    private int n = 0;
    private TextView tv_Empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_food);
        Bundle bundle = getIntent().getExtras();
        tab = bundle.getString(Config.ORDER_TAB);
        onBack();
        mydbHelper = new MySQLiteOpenHelper(OrderFoodActivity.this);
        initView();

        if (tab.equals("1")) {//购物
            tv_title.setText(R.string.Shopping);
            tpye = "2";
        } else {//订餐
            tv_title.setText(R.string.Ordering);
            tpye = "1";
        }
        List<ProductType> shopMenuList1 = initData();

        shopMenuList.addAll(shopMenuList1);
        if (shopMenuList != null && shopMenuList.size() > 0) {
            setAdapter();
        }
        MyPopupWindow();
//        //获取缓存数据
        getMyDBJSON();
//        getDbData();
    }

    private void setAdapter() {
        isflat = true;
        //左侧菜单列表
        listViewAdapter = new ShopOrderDishesListViewAdapter(shopMenuList, OrderFoodActivity.this);
        listViewAdapter.setSelected(0);//设置默认第一个为选中的

        categoryList.setAdapter(listViewAdapter);
        //右侧ExpandableListView
        expandListViewAdapter = new ShopOrderDishesExpandListViewAdapter(shopMenuList, OrderFoodActivity.this, productList, tvTotalPrice, tab);
        expandListViewAdapter.setMyOnClick(this);
        expandableListView.setAdapter(expandListViewAdapter);
        //取消listView的横线
        expandableListView.setDivider(null);
        deployExpandableListView(shopMenuList.size());//配置ExpandableListView的相关参数的方法必须等到数据加载完成后执行
        //设置pop的listView改变
        //获取缓存数据
        getDbData();


    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_food;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(OrderFoodActivity.class);
            }
        });
    }

    public void setNotifyDataSetChanged() {
        expandListViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_Empty = (TextView) findViewById(R.id.tv_Empty);
        // 中间部分
        categoryList = (ListView) findViewById(R.id.lv_category_list);
        expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expand_content_list);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        if (oldPrice > 0) {
            tvTotalPrice.setText(String.valueOf(oldPrice));
        }
        btnFinish = (TextView) findViewById(R.id.btn_order_finish);
        relativeBottom = (RelativeLayout) findViewById(R.id.relative_bottom);

        //价格布局
        rela_Order_menu = (RelativeLayout) findViewById(R.id.rela_Order_menu);
        //购物车 显示数量
        tv_Ordering_Num = (TextView) findViewById(R.id.tv_Ordering_Num);


        rela_Order_menu.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        //当左侧的分类列表点击的时候的监听事件
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectPosition = position;
                //下面两横主要作用是改变分类选择项，并把焦点恢复
                //修改选中的item的背景色
                isclick = false;
                expandableListView.setSelectedGroup(position);
                //设置点击项，为true
                listViewAdapter.setSelect(position);
                listViewAdapter.notifyDataSetChanged();
            }
        });
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isclick = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isclick = true;
            }
        });
    }


    /**
     * 配置ExpandableListView的相关信息
     */
    private void deployExpandableListView(int expandableListSize) {
        expandableListView.setOnHeaderUpdateListener(this);// 必须在Adapter实例化之后执行
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setGroupIndicator(null);// 去掉默认的指示箭头
//		expandableListView.setDivider(null);// 设置分割线样式
        for (int i = 0; i < expandableListSize; i++) {
            expandableListView.expandGroup(i);
        }
        //false 设置为顶部条目不可点击，（去丢顶部浮着的顶部View）
        expandableListView.setOnGroupClickListener(this, false);
    }

    // 读取订购的缓存数据
    private void getDbData() {
        int n = 0;//获取订餐数量
        // 查询日志数据数据
        if (tab.equals("1")) {//购物-----------------------------------------------
            String sql1 = "select * from shopping_words";
            List<Map<String, Object>> list = mydbHelper.selectList(sql1, null);
            // 更新数据库内容，刷新。
            if (list.size() > 0) {
                // 设置缓存日志
                tv_Ordering_Num.setVisibility(View.VISIBLE);

                btnFinish.setVisibility(View.VISIBLE);
                productList.clear();
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map = list.get(i);
                    String num = (String) map.get("num");
//                    String sql_name = (String) map.get("name");
                    String sql_ID = (String) map.get("ID_item");
                    for (int j = 0; j < shopMenuList.size(); j++) {
                        List<Product> products = shopMenuList.get(j).getProductList();
                        for (int k = 0; k < products.size(); k++) {//获取对应的订餐对象
//                          String name = products.get(k).getName();
                            String name_ID = products.get(k).getID();
                            if (name_ID.equals(sql_ID)) {
                                products.get(k).setNum(Integer.parseInt(num));
                                productList.add(products.get(k));
                                n += Integer.parseInt(num);
                            }
                        }

                    }
//                product.setPrice(Double.parseDouble(price));
                }
                getcapetyShopNum();
                expandListViewAdapter.notifyDataSetChanged();
                adapter_ordering.notifyDataSetChanged();
                tv_Ordering_Num.setText(n + "");
                double totalPrice = getTotalPrice();
                tvTotalPrice.setText(" 共￥" + totalPrice);
//                BaseApplication.toast(list.size()+":");
                relativeBottom.setVisibility(View.VISIBLE);
            } else {//缓存数据为空 ;即无购物
                //设置为购物车为空的
                tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
                tvTotalPrice.setVisibility(View.VISIBLE);
                //
                tv_Ordering_Num.setVisibility(View.GONE);
//                btnFinish.setVisibility(View.GONE);
                relativeBottom.setVisibility(View.GONE);
            }

        } else {//订餐------------------------------------------------------
            String sql1 = "select * from tb_words";
            List<Map<String, Object>> list = mydbHelper.selectList(sql1, null);
            // 更新数据库内容，刷新。
            if (list.size() > 0) {
                // 设置缓存日志
                tv_Ordering_Num.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.VISIBLE);
//                int n = 0;//获取订餐数量
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map = list.get(i);
                    String num = (String) map.get("num");
//                    String sql_name = (String) map.get("name");
                    String sql_ID = (String) map.get("ID_item");
                    for (int j = 0; j < shopMenuList.size(); j++) {
                        List<Product> products = shopMenuList.get(j).getProductList();
                        for (int k = 0; k < products.size(); k++) {//获取对应的订餐对象
//                            String name = products.get(k).getName();
                            String name_ID = products.get(k).getID();
                            if (name_ID.equals(sql_ID)) {
                                products.get(k).setNum(Integer.parseInt(num));
                                productList.add(products.get(k));
                                n += Integer.parseInt(num);
                            }
                        }
                    }
//                product.setPrice(Double.parseDouble(price));
                }
                getcapetyShopNum();
                expandListViewAdapter.notifyDataSetChanged();
                adapter_ordering.notifyDataSetChanged();
                tv_Ordering_Num.setText(n + "");
                double totalPrice = getTotalPrice();
                tvTotalPrice.setText(" 共￥" + totalPrice);
//            BaseApplication.toast(list.size()+":");
                relativeBottom.setVisibility(View.VISIBLE);
            } else {//缓存数据为空 ;即无购物
                //设置为购物车为空的
                tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
                tvTotalPrice.setVisibility(View.VISIBLE);
                //
                tv_Ordering_Num.setVisibility(View.GONE);
//                btnFinish.setVisibility(View.GONE);
                relativeBottom.setVisibility(View.GONE);

            }
        }

    }
    //获取每一个分类用户选购了多少件商品
    private void getcapetyShopNum(){
        for (int j = 0; j < shopMenuList.size(); j++) {
            List<Product> products = shopMenuList.get(j).getProductList();
            int capetyShopNum=0;
            for (int k = 0; k < products.size(); k++) {//获取对应的订餐对象
                    capetyShopNum+=products.get(k).getNum();
            }
            shopMenuList.get(j).setShoppingNum(capetyShopNum);
        }
    }
    /*
    *   读取缓存json
    *
    * */
    private void getMyDBJSON() {
        if (tab.equals("2")) {//订餐
            String sqlString2 = "select * from ordering_ordering where roomid=?";
            int count = mydbHelper.selectCount(
                    sqlString2,
                    new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
            if (count > 0) {
                List<Map<String, Object>> list = mydbHelper.selectList(sqlString2, new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
                String json = (String) list.get(0).get("json");
                setDataBabyNet(json);
            }else {//刪除訂餐緩存json
                SQLiteDatabase db = mydbHelper
                        .getSQLiteDatabase();
                mydbHelper.onUpgrade_ordering_orderingDB(db, 1, 2);
            }
        } else {//购物
            String sqlString2 = "select * from ordering_Shopping where roomid=?";
            int count = mydbHelper.selectCount(
                    sqlString2,
                    new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
            if (count > 0) {
                List<Map<String, Object>> list = mydbHelper.selectList(sqlString2, new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
                String json = (String) list.get(0).get("json");
                setDataBabyNet(json);
            }else {//刪除購物json
                SQLiteDatabase db = mydbHelper
                        .getSQLiteDatabase();
                mydbHelper.onUpgrade_ordering_ShoppingDB(db, 1, 2);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order_finish:// 选购完成，跳到确认订单界面
                myShopList.clear();
                boolean isInTime=false;
                for (Iterator<?> itr = productList.uniqueSet().iterator(); itr.hasNext(); ) {
                    Product product = (Product) itr.next();
                    if (product.getOpen_time()!=null&& ! product.getOpen_time().equals("null")&&! product.getOpen_time().isEmpty()&&
                            product.getClose_time()!=null&& ! product.getClose_time().equals("null")&& ! product.getClose_time().isEmpty()){
//                        String operTime=Integer.parseInt(product.getOpen_time())/60+":"+Integer.parseInt(product.getOpen_time())%60;
//                        String closeTime=Integer.parseInt(product.getClose_time())/60+":"+Integer.parseInt(product.getClose_time())%60;
                        if (Integer.parseInt(product.getOpen_time())< getTime() && getTime() <Integer.parseInt(product.getClose_time())){
                        }else {
                            isInTime=true;
                        }
                    }else {
                        isInTime=false;
                    }
                    myShopList.add(product);
                }
                if (isInTime){
                    //有商品不在营业时间，不能下单
                    AppContext.toast("有商品不在营业时间，请删除重新下单");
                }else {
                    if (myShopList.size() > 0) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), OrderingInfoActivity.class);
                        Bundle bundle = new Bundle();
                        OrderEntity entity = new OrderEntity();
                        entity.setMyShopList(myShopList);
                        bundle.putSerializable("OrderEntity", entity);
                        bundle.putString(Config.ORDER_TAB, tab);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        BaseApplication.toast(R.string.Toast14);
                    }
                }
                break;
            case R.id.rela_Order_menu:
                myShopList.clear();
                for (Iterator<?> itr = productList.uniqueSet().iterator(); itr.hasNext(); ) {
                    Product product = (Product) itr.next();
//                    Double price=product.getPrice();
//                    product.setNum(productList.getCount(product));
                    myShopList.add(product);

                }

                int[] location = new int[2];
                v.getLocationOnScreen(location);
                WindowManager.LayoutParams params = OrderFoodActivity.this.getWindow().getAttributes();
                params.alpha = 0.7f;
                OrderFoodActivity.this.getWindow().setAttributes(params);

                upWindow.showAtLocation(relativeBottom, Gravity.BOTTOM, 0, relativeBottom.getHeight());
//                int[] screen = ViewUtil.getScreen(OrderFoodActivity.this);
//                upWindow.showAtLocation(v,Gravity.NO_GRAVITY,0, screen[1]-2*upWindow.getHeight());
                break;
            default:
                break;
        }
    }

    private void MyPopupWindow() {
        upView = LayoutInflater.from(OrderFoodActivity.this).inflate(
                R.layout.ordring_pop, null);
        upWindow = new PopupWindow(upView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // 需要设置一下此参数，点击外边可消失
        upWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置此参数获得焦点，否则无法点击
        upWindow.setFocusable(true);
        // 设置动画展示
//         upWindow.setAnimationStyle(R.style.AnimationGrowPop);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        ColorDrawable drawable = new
                ColorDrawable(Color.parseColor("#FFFFFF"));
        // 设置SelectPicPopupWindow弹出窗体的背景
        upView.setBackgroundDrawable(drawable);
        // 找控件
        //清空所以数据
        upView.findViewById(R.id.tv_Pop_Clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.toast(R.string.Toast15);
                for (int i = 0; i < myShopList.size(); i++) {
                    Product product = myShopList.get(i);
                    product.setNum(0);
                    productList.remove(product);
                }
                expandListViewAdapter.notifyDataSetChanged();
                myShopList.clear();
                upWindow.dismiss();
                //设置为购物车为空的
                tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
                tvTotalPrice.setVisibility(View.VISIBLE);
                //
                tv_Ordering_Num.setVisibility(View.GONE);
//                btnFinish.setVisibility(View.GONE);

                SQLiteDatabase db = mydbHelper
                        .getSQLiteDatabase();
                if (db != null) {
                    if (tab.equals("1")) {//购物
                        mydbHelper.onUpgrade_DefaultDB(db, 1, 2);
                    } else {//订餐
                        mydbHelper.onUpgrade(db, 1, 2);
                    }
                } else {
                    BaseApplication.toast(R.string.Toast16);
                }


            }
        });
        lv_MyOrdering = (ListView) upView.findViewById(R.id.lv_Ordering);
        adapter_ordering = new MyOrdering_Adapter(OrderFoodActivity.this, productList, myShopList, OrderFoodActivity.this, mydbHelper, upWindow, tab);
        lv_MyOrdering.setAdapter(adapter_ordering);
        lv_MyOrdering.setDivider(null);
        upWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getcapetyShopNum();
                listViewAdapter.notifyDataSetChanged();
                if (myShopList.size() == 0) {
                    //设置为购物车为空的
                    tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
                    tvTotalPrice.setVisibility(View.VISIBLE);
                    //
                    tv_Ordering_Num.setVisibility(View.GONE);
                    relativeBottom.setVisibility(View.GONE);
                }else {
                    relativeBottom.setVisibility(View.VISIBLE);
                }
                WindowManager.LayoutParams params = OrderFoodActivity.this.getWindow().getAttributes();
                params.alpha = 1f;
                OrderFoodActivity.this.getWindow().setAttributes(params);
            }
        });

    }

    /**
     * 在滚动的时候从新绘制头部，即组列表
     */
    @Override
    public View getPinnedHeader() {
        View headerView = getLayoutInflater().inflate(R.layout.shop_order_dishes_father_item, null);
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        headerView.setClickable(false);
        return headerView;
    }

    /**
     * 在重新绘制头部之后，给这个头部设置新的标题
     */
    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {

        //修改固定头部的标题
        TextView textView = (TextView) headerView.findViewById(R.id.tv_category_name);
        ProductType productType = (ProductType) expandListViewAdapter.getGroup(firstVisibleGroupPos);
        textView.setText(productType.getTypeName());// 更新标题
//        Toast.makeText(getApplicationContext(),firstVisibleGroupPos+"sdfa",Toast.LENGTH_LONG).show();

        //此处不能和上面的调换
        if (firstVisibleGroupPos < selectPosition) {
            firstVisibleGroupPos = selectPosition;
        }
        Log.e("ORDERS","-------》isclick="+isclick);
        if (isclick) {
            //修改选中的item的背景色
            changeItemBackground(oldPositiion, firstVisibleGroupPos);
        } else {
            // isclick = true;
        }
        // 保存旧分类的位置编号
        oldPositiion = firstVisibleGroupPos;
        selectPosition = -1;
    }

    /**
     * 修改左边ListView中当前选中的item的背景色，并把原来选中的item的背景色还原成默认背景色
     *
     * @param oldPosition 原来选中的item的编号
     * @param newPosition 当前选中的item的编号
     */
    private void changeItemBackground(int oldPosition, int newPosition) {
        /*修改左边分类列表的背景色*/
        View oldCateView = getViewByPosition(oldPosition, categoryList);// 旧的分类
        View cateView = getViewByPosition(newPosition, categoryList);// 当前所在分类
        // 旧分类的背景色R.color.whitesmoke
        oldCateView.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
        // 新分类的背景色
//        cateView.setBackgroundResource(R.drawable.selector_choose_eara_ite);
        cateView.setBackgroundColor(getResources().getColor(R.color.whitesmoke));

        int lastVisibalePosition = categoryList.getLastVisiblePosition();//在ListView中，第一个可见的item的编号
        int firstVisibalePosition = categoryList.getFirstVisiblePosition();//在ListView中，最后一个可见的item的编号

        if (newPosition >= lastVisibalePosition) {//向后滚动
            // 把分类列表定位到当前的分类位置,即当前选中的item
            categoryList.setSelection(newPosition);
        } else if (firstVisibalePosition >= newPosition) {//向前滚动
            // 把分类列表定位到当前的分类位置
            categoryList.setSelection(newPosition);
        }
        //必须把刷新数据，不然不执行adapter内的geiView方法，
        if (oldPosition != newPosition) {
            if (isclick) {
                Log.e("ORDERSs","---==="+oldPosition+"oldPosition"+newPosition+""+isclick);
                listViewAdapter.setSelected(newPosition);//把编号为newPosition的item设置为选中
                listViewAdapter.notifyDataSetChanged();
            } else {
                isclick = true;
            }
        }
    }

    /**
     * 获取ListView指定编号的item布局view
     *
     * @param pos      item编号
     * @param listView 目标ListView
     * @return
     */
    public View getViewByPosition(int pos, ListView listView) {
        //在可见的item当中，第一个item的位置编号
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        //最后一个item的位置编号
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * ExpandableListView 在组列表中的点击事件
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v,
                                int groupPosition, long id) {
        return true;
    }

    /**
     * ExpandableListView 子列表的点击事件 ExpandableListView 的二级列表的点击事件
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // 数据请求完成之后再弹出框
        Intent intent=new Intent();
        intent.setClass(getApplicationContext(),FoodInfoActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("caption",shopMenuList.get(groupPosition).getProductList().get(childPosition).getCaption());
//        bundle.putString("imgUrl",shopMenuList.get(groupPosition).getProductList().get(childPosition).getImgUrl());
        bundle.putString("content",shopMenuList.get(groupPosition).getProductList().get(childPosition).getContent());//服务内容描述路径
        bundle.putString("thumbnail",shopMenuList.get(groupPosition).getProductList().get(childPosition).getThumbnail());//图片
        bundle.putString("brief",shopMenuList.get(groupPosition).getProductList().get(childPosition).getBrief());//简介
        intent.putExtras(bundle);
        startActivity(intent);

        return false;
    }

    /**
     * 获取总金额
     *
     * @return
     */
    public double getTotalPrice() {
        double totalProce = 0;
        if (productList.size() == 0) {
            return totalProce;
        }
        for (Iterator<?> itr = productList.uniqueSet().iterator(); itr.hasNext(); ) {
            Product product = (Product) itr.next();
            Double price = product.getPrices();
            totalProce += product.getPrices() * product.getNum();
//			totalProce+=price;
            totalProce = NumberUtils.toDouble(NumberUtils.format(totalProce));
        }
        return totalProce;
    }

    /**
     * 当点餐之后，就显示出来，包括价格和结算按钮
     */
    public void updateBottomStatus(double totalPrice, int num) {
        if (totalPrice > 0) {
            //
            btnFinish.setVisibility(View.VISIBLE);
            tv_Ordering_Num.setVisibility(View.VISIBLE);
            if (num == 0) {
                tv_Ordering_Num.setText("");
                tv_Ordering_Num.setVisibility(View.GONE);
            } else {
                tv_Ordering_Num.setVisibility(View.VISIBLE);
                tv_Ordering_Num.setText(num + "");
            }
        } else {
//            btnFinish.setVisibility(View.GONE);
            if (num>0){//免费商品
                tv_Ordering_Num.setVisibility(View.VISIBLE);
            }else {
                tv_Ordering_Num.setVisibility(View.GONE);
            }

        }
        if (num == 0) {
            tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
            tvTotalPrice.setVisibility(View.VISIBLE);
            relativeBottom.setVisibility(View.GONE);
        } else {
            tvTotalPrice.setVisibility(View.VISIBLE);
            tvTotalPrice.setText(" 共 ￥ " + totalPrice);
            relativeBottom.setVisibility(View.VISIBLE);
        }
        getcapetyShopNum();
        listViewAdapter.notifyDataSetChanged();
    }

//    public  void getMyOrderingLists(ProductType types){
//        myShopList.add(types);
//    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    //数据库


    //数据
    private List<ProductType> initData() {
        Product product = null;
        List<Product> productList = null;
        ProductType type = null;
        final List<ProductType> productTypes = new ArrayList<ProductType>();

        /*
        *   一次性获取所以数据
        * */
        getMyData();


        return productTypes;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyDBJSON();
    }

    /*
        *   访问网络获取数据
        *
        * */
    private void getMyData() {
         /*
        *  一个借口返回全部数据
        *  第一个 酒店id号
        *  分类
        *  1 – 餐饮
        *  2 - 购物
        *
        *
        * */
        if(NewDoorFragment.list.size()==0){
            //无数据
            return;
        }
        HttpClient.instance().getAllServiceList(NewDoorFragment.list.get(NewDoorFragment.pos).getID(), tpye, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
//                final List<ProductType> productTypes_lis = responseBean.getListData(ProductType.class);
//                AppContext.toLog(productTypes_lis.toString());
                Log.e("TAG", "--------->" + responseBean.toString());

                /*
                *   订餐缓存
                * */
                if (tab.equals("2")) {
                    String sqlString2 = "select * from ordering_ordering where roomid=?";
                    int count = mydbHelper.selectCount(
                            sqlString2,
                            new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});

                    if (count > 0) {
                        // "数据已经存在需要更改
                        String sqlUpda = "update ordering_ordering set json=? where roomid='" + NewDoorFragment.list.get(NewDoorFragment.pos).getID() + "'";
                        boolean isfale = mydbHelper.updateData(sqlUpda, new String[]{responseBean.toString()});
//        				BaseApplication.toast(isfale+"数据更改");
                    } else {
                        // 先插入数据再查询数据
                        String sql = "insert into ordering_ordering (json ,roomid) values (?,?)";
                        boolean flag = mydbHelper.execData(
                                sql,
                                new Object[]{responseBean.toString(), NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
//                        BaseApplication.toast(flag + "数据存储");
                    }


                } else {
                    /*
                    *   购物缓存
                    *
                    * */
                    String sqlString2 = "select * from ordering_Shopping where roomid=?";
                    int count = mydbHelper.selectCount(
                            sqlString2,
                            new String[]{NewDoorFragment.list.get(NewDoorFragment.pos).getID()});

                    if (count > 0) {
                        // "数据已经存在需要更改
                        String sqlUpda = "update ordering_Shopping set json=? where roomid='" + NewDoorFragment.list.get(NewDoorFragment.pos).getID() + "'";
                        boolean isfale = mydbHelper.updateData(sqlUpda, new String[]{responseBean.toString()});
//                        BaseApplication.toast(isfale+"数据更改");
                    } else {
                        // 先插入数据再查询数据
                        String sql = "insert into ordering_Shopping (json ,roomid) values (?,?)";
                        boolean flag = mydbHelper.execData(
                                sql,
                                new Object[]{responseBean.toString(), NewDoorFragment.list.get(NewDoorFragment.pos).getID()});
//                        BaseApplication.toast(flag + "数据存储");
                    }
                }
                /*
                *   设置数据
                *
                * */
                setDataBabyNet(responseBean.toString());
            }
        });
    }

    private void setDataBabyNet(String str) {
        shopMenuList.clear();
        myShopList.clear();
        productList.clear();
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray array = jsonObject.getJSONArray("result");
            List<ProductType> productTypes_lis = new ArrayList<ProductType>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String ID = object.getString("ID");
                String caption = object.getString("caption");
                //设置分类数据
                ProductType productType = new ProductType();
                productType.setCaption(caption);
                productType.setID(ID);
                JSONArray items = object.getJSONArray("items");
                List<Product> productList = new ArrayList<Product>();
                for (int j = 0; j < items.length(); j++) {
                    JSONObject object_item = items.getJSONObject(j);
                    String ID_item = object_item.getString("ID");
                    String provider_id = object_item.getString("provider_id");
                    String caption_item = object_item.getString("caption");
                    String content = object_item.getString("content");
                    String price = object_item.getString("price");
                    String unit = object_item.getString("unit");
                    String thumbnail = object_item.getString("thumbnail");
                    String brief = object_item.getString("brief");
                    String start_time = object_item.getString("start_time");
                    String end_time = object_item.getString("end_time");
                    String hotel_price = object_item.getString("hotel_price");
                    String open_time = object_item.getString("open_time");
                    String close_time = object_item.getString("close_time");
                    //设置购物，订餐，数据
                    Product product1 = new Product();
                    product1.setID(ID_item);
                    product1.setProvider_id(provider_id);
                    product1.setCaption(caption_item);
                    product1.setContent(content);
                    product1.setPrice(price);
                    product1.setUnit(unit);
                    product1.setThumbnail(thumbnail);
                    product1.setBrief(brief);
                    product1.setStart_time(start_time);
                    product1.setEnd_time(end_time);
                    product1.setHotel_price(hotel_price);
                    product1.setOpen_time(open_time);
                    product1.setClose_time(close_time);
                    productList.add(product1);
                }
                productType.setProductList(productList);
                productTypes_lis.add(productType);
            }
            shopMenuList.clear();
            shopMenuList.addAll(productTypes_lis);
            //先判断expandListViewAdapter是否有适配器，若无就先设置适配器
            if (shopMenuList.size()>0){
                tv_Empty.setVisibility(View.GONE);
            }else {
                shopMenuList.clear();
                myShopList.clear();
                tv_Empty.setVisibility(View.VISIBLE);
            }
            if (isflat) {
                //获取缓存数据
                setAdapter();
                getDbData();
//                BaseApplication.toast(shopMenuList.get((shopMenuList.size() - 1)).getProductList().size()+"个");
            } else {
                setAdapter();
            }
            if (shopMenuList.size() > 0) {
                expandListViewAdapter.notifyDataSetChanged();
                adapter_ordering.notifyDataSetChanged();
            } else {
                BaseApplication.toast(R.string.Toast17);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            if (tab.equals("2")) {//订餐删除订餐缓存
                SQLiteDatabase db = mydbHelper
                        .getSQLiteDatabase();
                mydbHelper.onUpgrade_ordering_orderingDB(db, 1, 2);
            }else{//删除购物缓存
                SQLiteDatabase db = mydbHelper
                        .getSQLiteDatabase();
                mydbHelper.onUpgrade_ordering_ShoppingDB(db, 1, 2);
            }
            shopMenuList.clear();
            myShopList.clear();
            if (isflat) {
//                expandListViewAdapter.notifyDataSetChanged();
                expandListViewAdapter.notifyDataSetInvalidated();
            }
            if (shopMenuList.size()>0){
                tv_Empty.setVisibility(View.GONE);
            }else {
                tv_Empty.setVisibility(View.VISIBLE);
            }
            tvTotalPrice.setText(" " + getResources().getString(R.string.clear_order_empty));
            tvTotalPrice.setVisibility(View.VISIBLE);
            tv_Ordering_Num.setText(0 + "");
            tv_Ordering_Num.setVisibility(View.GONE);
        }
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

    @Override
    public void onItemClick(View view, int postion) {
        getcapetyShopNum();
        listViewAdapter.notifyDataSetChanged();
    }
}
