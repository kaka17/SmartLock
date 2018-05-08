package com.sht.smartlock.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetailActivity;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.adapter.MyChatHistory_Adapter;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.adapter.ChatAllHistoryAdapter;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.InviteMessgeDao;
import com.sht.smartlock.ui.chat.applib.db.UserDao;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.model.HXNotifier;
import com.sht.smartlock.ui.entity.MyChatHistoryEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

public class MyChatHistoryActivity extends BaseActivity implements MyItemClickListener, EMEventListener {

    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    private EditText query;
    private ImageButton clearSearch;
    public RelativeLayout errorItem;

    public TextView errorText;
    private boolean hidden;
    private boolean isBlack=false;
    private EMConversation serviceChat;
    private List<User> myUsers=new ArrayList<>();


    private List<EMConversation> conversationList = new ArrayList<EMConversation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_chat_history);
        onBack();
        initView();
        //显示客服的聊天账号
        getserviceChat();
        //获取我的app好友列表
        //获取本地服务器好友消息
        if (AppContext.getShareUserSessinid() != null) {
            getMyFriendsLists();
        }

    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
//        return R.layout.activity_my_chat_history;
        return R.layout.fragment_conversation_history;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(MyChatHistoryActivity.class);
            }
        });
    }


    private void initView() {

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getApplication(), 1, conversationList,myUsers);
        // 设置adapter
        listView.setAdapter(adapter);
        // 从本地获取黑名单
        final List<String> blacklist = EMContactManager.getInstance().getBlackListUsernames();

        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();

                if (username.equals(AppContext.getInstance().getUserName()))
                    Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                            // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                            intent.putExtra("groupId", username);
                        } else {
                            // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                            intent.putExtra("groupId", username);
                        }
                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                        isBlack=false;
                        if (blacklist.size()>0){
                            for (int i=0;i<blacklist.size();i++){
                                if (blacklist.get(i).equals(username)){
                                    isBlack=true;
                                    break;//跳出for循环
                                }
                            }
                            intent.putExtra(Config.ISBLACK,isBlack);
                        }else {
                            intent.putExtra(Config.ISBLACK,isBlack);
                        }
                        //好友就带昵称传递
                        for (int i=0;i<myUsers.size();i++){
                            if (myUsers.get(i).getEmid().equals(username)){
                                intent.putExtra(Config.NICKNAME, myUsers.get(i).getName());
                                break;  //跳出for循环
                            }
                        }
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftKeyboard();
                return false;
            }

        });
        // 搜索框
        query = (EditText) findViewById(R.id.query);
        String strSearch = getResources().getString(R.string.search);
        query.setHint(strSearch);
        // 搜索框中清除button
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

    }


    //
    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getHistoryByHuanXin() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getApplicationContext(), 1, conversationList,myUsers);
        // 设置adapter
        listView.setAdapter(adapter);
    }

    private void getserviceChat(){
        AppContext.setProperty(Config.SERVICECHAT,"service");
        String serviceUserName = AppContext.getProperty(Config.SERVICECHAT);
        serviceChat=new EMConversation(serviceUserName);
//        AppContext.toast("1"+conversationList.size());
        if (conversationList.size()>0){
            for (int i=0;i<conversationList.size();i++){
                if (conversationList.get(i).getUserName().equals(serviceUserName)){
                   //已存在服务账号
                    return;
                }
            }
            conversationList.add(serviceChat);
//            AppContext.toast("2" + conversationList.size());
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }else {
            conversationList.add(serviceChat);
//            AppContext.toast("3:" + conversationList.size());
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
            handled = true;
        }
        EMConversation tobeDeleteCons = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getApplicationContext());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        adapter.remove(tobeDeleteCons);
        adapter.notifyDataSetChanged();

        // 更新消息未读数
//        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled ? true : super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden && !MainActivity.isConflict) {
            refresh();
        }
        /*
        *   注册监听
        *
        * */
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage ,EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});


    }



    /**
     * 刷新页面
     */
    public void refresh() {

        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        getserviceChat();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @param //context
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param //usernames
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    @Override
    public void onItemClick(View view, int postion) {
        BaseApplication.toast(postion + "我跳");
//        Intent intent=new Intent();
//        intent.setClass(getApplicationContext(), ChatActivity.class);
//        if (2>1){//群聊
//            intent.putExtra("chatType", 3);
//            intent.putExtra("groupId", list.get(postion).getGroup_id());
//        }else {//单聊
//            intent.putExtra("userId", list.get(postion).getID());
//        }
//        intent.putExtras(intent);
//        startActivity(intent);
    }


    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {

        switch (emNotifierEvent.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) emNotifierEvent.getData();
                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 刷新bottom bar消息未读数

                  refresh();

                    }
                });

                break;
            }

            case EventOfflineMessage: {
                refresh();

                break;
            }

            case EventConversationListChanged: {
                refresh();

                break;
            }

            default:
                break;
        }
    }

    //获取app好友列表
    private void getMyFriendsLists() {
        if (AppContext.getProperty(Config.MYFRIENDSTR)!=null){
            //缓存数据
            getJSON(AppContext.getProperty(Config.MYFRIENDSTR));
        }
        HttpClient.instance().myfriends_list(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.setProperty(Config.MYFRIENDSTR,responseBean.toString());
                List<User> listData = responseBean.getListData(User.class);
//                for (int i=0;i<listData.size();i++){
//                    listData.get(i).setAvatar("http://img1.imgtn.bdimg.com/it/u=1820659434,386753461&fm=21&gp=0.jpg");
//                }
                UserDao userDao=new UserDao(MyChatHistoryActivity.this);
                userDao.saveContactList(listData);
                myUsers.addAll(listData);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    private void getJSON(String str){
        try {
            JSONObject jsonObject=new JSONObject(str);
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i=0;i<result.length();i++){
                JSONObject object = result.getJSONObject(i);
                String id_image = object.getString("id_image");
                String name = object.getString("name");
                String emid = object.getString("emid");
                User user=new User();
                user.setEmid(emid);
                user.setName(name);
                user.setId_image(id_image);
                myUsers.add(user);
            }
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
