package com.sht.smartlock.ui.activity.mine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.HotelOrder;
import com.sht.smartlock.model.SearchFrinedsInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.MyBalanceAdapter;
import com.sht.smartlock.ui.adapter.SearchFrinedsAdapter;
import com.sht.smartlock.ui.chat.applib.activity.AddContactActivity;
import com.sht.smartlock.ui.chat.applib.activity.MyAlertDialog;
import com.sht.smartlock.ui.chat.applib.adapter.NewFriendsMsgAdapter;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.InviteMessgeDao;
import com.sht.smartlock.ui.chat.applib.domain.InviteMessage;
import com.sht.smartlock.ui.chat.applib.uidemo.Constant;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends BaseActivity implements SearchFrinedsAdapter.ListItemClickHelp{
    private ImageView img_back;
    private EditText editSearchFrids;
    private ListView listAddFrids,listSearchFrids;


    private String strSearchFrineds;
    private List<SearchFrinedsInfo> list = new ArrayList<SearchFrinedsInfo>();
    private SearchFrinedsAdapter searchFrinedsAdapter;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        setOnClickLister();
        initDate();

    }


    private void initDate(){
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        //设置adapter
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listAddFrids.setAdapter(adapter);

        //搜索时另外设置适配器
        searchFrinedsAdapter = new SearchFrinedsAdapter(SearchFriendsActivity.this,list,SearchFriendsActivity.this);
        listSearchFrids.setAdapter(searchFrinedsAdapter);

        AppContext.toLog("msgs.size()==="+msgs.size());
        try{
             ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
        }catch (Exception e){
            AppContext.toLog(e.toString());
        }
    }


    private void findViewById(){
        img_back = (ImageView)findViewById(R.id.img_back);
        editSearchFrids = (EditText)findViewById(R.id.editSearchFrids);
        listAddFrids = (ListView)findViewById(R.id.listAddFrids);
        listSearchFrids = (ListView)findViewById(R.id.listSearchFrids);

        strSearchFrineds = editSearchFrids.getText().toString();
        editSearchFrids.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if(editSearchFrids.getText().toString().length() > 0){
                    //显示搜索的listView,隐藏别人申请的listView
                    listAddFrids.setVisibility(View.GONE);
                    listSearchFrids.setVisibility(View.VISIBLE);

                    initData(editSearchFrids.getText().toString());
                }else {
                    //显示别人申请的listView,隐藏搜索的listView
                    listAddFrids.setVisibility(View.VISIBLE);
                    listSearchFrids.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setOnClickLister(){
        img_back.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    public void onClick(View item, View widget, int position, int which) {
        switch (which){
            case R.id.igButtonAddFrinds:
                if (list.size()>position) {
                    addContact(item, list.get(position).getEmid());
                }
                break;
        }
    }

    private void initData(String content){
        HttpClient.instance().search_friends(content, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                List<SearchFrinedsInfo>   lists = responseBean.getListDataWithGson(SearchFrinedsInfo.class);
                list.clear();
                list.addAll(lists);
                searchFrinedsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addContact(View view, final String emid){
        if(AppContext.getInstance().getUserName().equals(emid)){
            String str = getString(R.string.not_add_myself);
            startActivity(new Intent(this, MyAlertDialog.class).putExtra("msg", str));
            return;
        }

        if(((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().containsKey(emid)){
            //提示已在好友列表中，无需添加
            if(EMContactManager.getInstance().getBlackListUsernames().contains(emid)){
                startActivity(new Intent(this, MyAlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
                return;
            }
            String strin = getString(R.string.This_user_is_already_your_friend);
            startActivity(new Intent(this, MyAlertDialog.class).putExtra("msg", strin));
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
                    EMContactManager.getInstance().addContact(emid, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_friends;
    }
}
