package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.CollectionInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetail02Activity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetailActivity;
import com.sht.smartlock.ui.adapter.CollectionInfoAdapter;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class MyCollectionActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private PullToRefreshListView list_Collection;

    private List<CollectionInfo> collectionlist = new ArrayList<CollectionInfo>();
    private CollectionInfoAdapter collectionInfoAdapter;
    List<CollectionInfo> collectioninfo = new ArrayList<CollectionInfo>();
    private List<CollectionInfo> lists;


    private int pageid=1;
    private boolean isResult = false;
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
        initDate(1);
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        list_Collection = (PullToRefreshListView)findViewById(R.id.list_Collection);
//        list_Collection.setDivider(null);
        tvTitlePanel.setText("我的收藏");
        list_Collection.setMode(PullToRefreshBase.Mode.BOTH);
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        list_Collection.setOnRefreshListener(this);
        collectionInfoAdapter = new CollectionInfoAdapter(MyCollectionActivity.this,collectionlist);
        list_Collection.setAdapter(collectionInfoAdapter);
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
        list_Collection.setOnItemClickListener(itemClickListener);
    }

    private int isTraion;

    private void initDate(int i){
        isTraion = i;
        HttpClient.instance().myhotel_collection_list(pageid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    if(isResult){//如果是刷新就清空
                        if(isTraion == 0){
                            if(lists.size() > 0){
                                if(collectionlist.size() > 0){
                                    collectionlist.clear();
                                    lists.clear();
                                }
                            }
                        }
                    }else{
                        isResult = true;

//                        if (collectionlist.size() < 1) {
//                            BaseApplication.toast("没有更多数据");
//                        }
                    }

                    lists = responseBean.getListDataWithGson(CollectionInfo.class);
                    collectionlist.addAll(lists);
                    collectionInfoAdapter.notifyDataSetChanged();
                    if (collectionlist.size()>0){
                        reEnpty.setVisibility(View.GONE);
                    }else {
                        reEnpty.setVisibility(View.VISIBLE);
                        AppContext.toast("您还没有收藏酒店");
                    }

                }catch (Exception e){
                     BaseApplication.toast("没有更多数据");
                    reEnpty.setVisibility(View.VISIBLE);
                }
                list_Collection.onRefreshComplete();
            }
        });
    }



    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent();
            intent.setClass(MyCollectionActivity.this, ShowHotelDetail02Activity.class);
         //   Bundle bundle = new Bundle();
            intent.putExtra(Config.KEY_HOTEL_ID,collectionlist.get(i-1).getHotel_id());
            intent.putExtra(Config.KEY_HOTEL_URL,  collectionlist.get(i-1).getIntroduction());
            intent.putExtra(Config.KEY_HOTEL_CAPTION, collectionlist.get(i - 1).getCaption());
            intent.putExtra(Config.LOCKPIC,collectionlist.get(i - 1).getPicture());
            intent.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_ALL);
            intent.putExtra(Config.KEY_HOTEL_IS_COLLECT,collectionlist.get(i-1).getIs_collection());
//                    putString(Config.KEY_HOTEL_IS_COLLECT,collectionlist.get(i-1).getIs_collection());
          //  intent.putExtras(bundle);
            startActivity(intent);
        }
    };


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    class  NetworkRequestLoginResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
            collectioninfo.clear();
            ProgressDialog.show(mContext, "正在加载..");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            list_Collection.onRefreshComplete();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            list_Collection.onRefreshComplete();
            if(pageid == 1){
                collectionlist.clear();
            }
            try {
                JSONObject jsonObject2=new JSONObject(responseBean.toString());
                if(jsonObject2.optString("error","")==""){
                    if(!responseBean.toString().equals("")){
                        try {
                            JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();

                            if(!jsonObject.getString("result").equals("")){
                                collectioninfo = responseBean.getListDataWithGson(CollectionInfo.class);

                                collectionlist.addAll(collectioninfo);
                                if (collectionlist.size()>0){
                                    reEnpty.setVisibility(View.GONE);
                                }else {
                                    reEnpty.setVisibility(View.VISIBLE);
                                }

                            }else{
//
                                toastFail(getString(R.string.no_result_left));
                                reEnpty.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){
                            toastFail(getString(R.string.no_result_left));
                            reEnpty.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
//                    collectionlist.clear();
//                    collectionInfoAdapter.notifyDataSetChanged();
                    toastFail(getString(R.string.no_result_left));
                    reEnpty.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                LogUtil.log(e.toString());
                reEnpty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        collectionlist.clear();
//        HttpClient.instance().myhotel_collection_list(pageid, new NetworkRequestLoginResult());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid = 1;
        initDate(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageid ++;
        isResult = false;
        initDate(1);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collection;
    }
}
