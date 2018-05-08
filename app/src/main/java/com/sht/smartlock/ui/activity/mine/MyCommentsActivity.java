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
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.CollectionInfo;
import com.sht.smartlock.model.CommentInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetail02Activity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetailActivity;
import com.sht.smartlock.ui.adapter.CollectionInfoAdapter;
import com.sht.smartlock.ui.adapter.CommentAdapter;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class MyCommentsActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private PullToRefreshListView listComment;
    private List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
    private List<CommentInfo> cLists;
    private CommentAdapter commentAdapter;

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
        listComment = (PullToRefreshListView)findViewById(R.id.listComment);
//        listComment.setDivider(null);
        tvTitlePanel.setText("我的点评");
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        listComment.setMode(PullToRefreshBase.Mode.BOTH);
        listComment.setOnRefreshListener(this);
        commentAdapter = new CommentAdapter(MyCommentsActivity.this,commentInfoList);
        listComment.setAdapter(commentAdapter);
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
        listComment.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent();
            intent.setClass(MyCommentsActivity.this,ShowHotelDetail02Activity.class);
            intent.putExtra(Config.KEY_HOTEL_ID, commentInfoList.get(i - 1).getHotel_id());
            intent.putExtra(Config.KEY_HOTEL_URL, commentInfoList.get(i-1).getIntroduction());
            intent.putExtra(Config.KEY_HOTEL_CAPTION, commentInfoList.get(i-1).getCaption());
            intent.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_ALL);
            intent.putExtra(Config.LOCKPIC,commentInfoList.get(i - 1).getPicture());
            intent.putExtra(Config.KEY_HOTEL_IS_COLLECT, commentInfoList.get(i-1).getIs_collection());
            startActivity(intent);
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private int cIsTraion;
    private void initDate(int i){
        cIsTraion = i;
        HttpClient.instance().myhotel_comment_list(pageid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {

                    if (isResult) {//如果是刷新就清空
                        if (cIsTraion == 0) {
                            if (cLists.size() > 0) {
                                if (commentInfoList.size() > 0) {
                                    commentInfoList.clear();
                                    cLists.clear();
                                }
                            }
                        }
                    } else {
                        isResult = true;
//                        if (commentInfoList.size() < 1) {
//                            BaseApplication.toast("没有更多数据");
//                        }
                    }
                    cLists = responseBean.getListDataWithGson(CommentInfo.class);

                    commentInfoList.addAll(cLists);
                    commentAdapter.notifyDataSetChanged();
                    if (commentInfoList.size()>0){
                        reEnpty.setVisibility(View.GONE);
                    }else {
                        reEnpty.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    BaseApplication.toast("没有更多数据");
                    reEnpty.setVisibility(View.VISIBLE);
                }
                listComment.onRefreshComplete();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
//        pageid =1;
//        HttpClient.instance().myhotel_comment_list(pageid, new NetworkRequestLoginResult());
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
        return R.layout.activity_my_comments;
    }
}
