package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.User;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.adapter.MyLockG_Adapter;
import com.sht.smartlock.ui.adapter.Search_Chat_Adapter;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;
import com.sht.smartlock.ui.entity.Search_ChatEntity;
import com.sht.smartlock.util.JPushUtil;
import com.sht.smartlock.util.TextUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;
//通过关键字 搜索酒店聊天室
public class LockSearchActivity extends BaseActivity implements MyItemClickListener,View.OnClickListener{

    private RecyclerView recyc_SearchLock;
    private Search_Chat_Adapter searchAdapter;
    private List<Search_ChatEntity> list_Search=new ArrayList<>();
    private EditText et_Search;
    private ImageView iv_No_Search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lock_search);

        initView();
        setOnClickLintener();


    }
    //获取布局文件1
    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_search;
    }

    protected boolean hasToolBar() {
        return false;
    }
    private  void initView(){
         recyc_SearchLock = (RecyclerView) findViewById(R.id.recyc_SearchLock);
        findViewById(R.id.img_back).setOnClickListener(this);

         et_Search = (EditText) findViewById(R.id.et_Search);
        iv_No_Search = (ImageView) findViewById(R.id.iv_No_Search);

        // 设置LinearLayoutManager
        recyc_SearchLock.setLayoutManager(new LinearLayoutManager(this));
        // 设置ItemAnimator
        recyc_SearchLock.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        recyc_SearchLock.setHasFixedSize(true);
        // 初始化自定义的适配器
        searchAdapter = new Search_Chat_Adapter(getApplicationContext(),list_Search);
        recyc_SearchLock.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(this);

    }

    private void setOnClickLintener(){
        iv_No_Search.setOnClickListener(this);
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    initData(s.toString());
                    iv_No_Search.setVisibility(View.VISIBLE);
                }else {
                    iv_No_Search.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onItemClick(View view, int postion) {
        Intent intent=new Intent();
        intent.setClass(getApplicationContext(), ChatActivity.class);
        intent.putExtra("chatType", 3);
        intent.putExtra("groupId", list_Search.get(postion).getEmid());
        intent.putExtras(intent);
        startActivity(intent);
    }

    private void initData(String hotelName){
        HttpClient.instance().huanxin_searchHotel(hotelName,new HuanXinCallBack());
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                AppManager.getAppManager().finishActivity(LockSearchActivity.class);
                break;
            case R.id.iv_No_Search:
                et_Search.setText("");
                iv_No_Search.setVisibility(View.GONE);
                break;

        }
    }

        private class HuanXinCallBack extends HttpCallBack{
        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
//            ProgressDialog.disMiss();
            System.out.println("失败"+error+message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
//            ProgressDialog.disMiss();
//            if(responseBean.isFailure()){
//                toastFail("失败----");
//                return;
//            }
            System.out.println(responseBean.toString());
            List<Search_ChatEntity> entities=responseBean.getListData(Search_ChatEntity.class);
            list_Search.clear();
            list_Search.addAll(entities);
            searchAdapter.notifyDataSetChanged();

        }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        }

}
