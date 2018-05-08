package com.sht.smartlock.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.mine.SearchFriendsActivity;
import com.sht.smartlock.ui.chat.applib.activity.AddContactActivity;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.activity.GroupsActivity;
import com.sht.smartlock.ui.chat.applib.activity.NewFriendsMsgActivity;
import com.sht.smartlock.ui.chat.applib.activity.PublicChatRoomsActivity;
import com.sht.smartlock.ui.chat.applib.activity.RobotsActivity;
import com.sht.smartlock.ui.chat.applib.adapter.ContactAdapter;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.InviteMessgeDao;
import com.sht.smartlock.ui.chat.applib.db.UserDao;
import com.sht.smartlock.ui.chat.applib.domain.InviteMessage;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.uidemo.Constant;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.ui.chat.applib.widget.Sidebar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatFriendsActivity extends BaseActivity {

    public static final String TAG = "ChatFriendsActivity";
    private ContactAdapter adapter;
    private List<User> contactList;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    ImageButton clearSearch;
    EditText query;
    HXContactSyncListener contactSyncListener;
    HXBlackListSyncListener blackListSyncListener;
    HXContactInfoSyncListener contactInfoSyncListener;
    View progressBar;
    Handler handler = new Handler();
    private User toBeProcessUser;
    private String toBeProcessUsername;
    private User NotiUser;
    private InviteMessgeDao inviteMessgeDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_friends);
        onBack();


        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (ListView) findViewById(R.id.list);
        sidebar = (Sidebar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        inviteMessgeDao = new InviteMessgeDao(this);
        //黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();

        //搜索框
//        query = (EditText) findViewById(R.id.query);
//        query.setHint(R.string.search);
//        clearSearch = (ImageButton) findViewById(R.id.search_clear);
//        query.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.getFilter().filter(s);
//                if (s.length() > 0) {
//                    clearSearch.setVisibility(View.VISIBLE);
//                } else {
//                    clearSearch.setVisibility(View.INVISIBLE);
//
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
//        clearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                query.getText().clear();
//                hideSoftKeyboard();
//            }
//        });
//        init();
        // 设置adapter
        adapter = new ContactAdapter(getApplicationContext(), R.layout.row_contact, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = adapter.getItem(position).getUsername();
                if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
                    // 进入申请与通知页面
                    try {
                        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
                        user.setUnreadMsgCount(0);
                    }catch (Exception e){}
                    startActivity(new Intent(getApplicationContext(), SearchFriendsActivity.class));
                }
//                else if (Constant.GROUP_USERNAME.equals(username)) {
//                    // 进入群聊列表页面
//                    startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
//                } else if (Constant.CHAT_ROOM.equals(username)) {
//                    //进入聊天室列表页面
//                    startActivity(new Intent(getApplicationContext(), PublicChatRoomsActivity.class));
//                } else if (Constant.CHAT_ROBOT.equals(username)) {
//                    //进入Robot列表页面
//                    startActivity(new Intent(getApplicationContext(), RobotsActivity.class));
//                }
                else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
//                    startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUsername()));
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("userId", adapter.getItem(position).getUsername());
                    intent.putExtra("myNickName", adapter.getItem(position).getName());
                    intent.putExtra("MyPic", adapter.getItem(position).getId_image());
                    startActivity(intent);

                }
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        ImageView addContactView = (ImageView) findViewById(R.id.iv_new_contact);
        // 进入添加好友页
        addContactView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddContactActivity.class));
            }
        });
        registerForContextMenu(listView);

        progressBar = (View) findViewById(R.id.progress_bar);

        contactSyncListener = new HXContactSyncListener();
        HXSDKHelper.getInstance().addSyncContactListener(contactSyncListener);

        blackListSyncListener = new HXBlackListSyncListener();
        HXSDKHelper.getInstance().addSyncBlackListListener(blackListSyncListener);

        contactInfoSyncListener = new HXContactInfoSyncListener();
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);

        if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
            progressBar.setVisibility(View.GONE);//不需要都隐藏了
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }

    //
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((AdapterView.AdapterContextMenuInfo) menuInfo).position > 3) {
            toBeProcessUser = adapter.getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position);
            toBeProcessUsername = toBeProcessUser.getUsername();
            getMenuInflater().inflate(R.menu.context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            try {
                // 删除此联系人
                deleteContact(toBeProcessUser);
                // 删除相关的邀请消息
                InviteMessgeDao dao = new InviteMessgeDao(getApplicationContext());
                dao.deleteMessage(toBeProcessUser.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (item.getItemId() == R.id.add_to_blacklist) {
            moveToBlacklist(toBeProcessUsername);
            return true;
        }
        return super.onContextItemSelected(item);
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        this.hidden = hidden;
//        if (!hidden) {
//            refresh();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }
    //

    /**
     * 删除联系人
     *
     * @param tobeDeleteUser
     */
    public void deleteContact(final User tobeDeleteUser) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getApplicationContext());
                    dao.deleteContact(tobeDeleteUser.getUsername());
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().remove(tobeDeleteUser.getUsername());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), st2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
//        //
//        EMChatManager.getInstance().registerEventListener(this,
//                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});

    }

    //
    @Override
    public void onDestroy() {
        if (contactSyncListener != null) {
            HXSDKHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }

        if (blackListSyncListener != null) {
            HXSDKHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }

        if (contactInfoSyncListener != null) {
            ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
        super.onDestroy();
    }

    public void showProgressBar(boolean show) {
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
//        contactList.clear();
        //获取本地好友列表
        Map<String, User> users = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();

        if (users.get(Constant.NEW_FRIENDS_USERNAME) != null) {
            getMyFriendsLists(users.get(Constant.NEW_FRIENDS_USERNAME));
        }else {
            // 添加user"申请与通知"
            User newFriends = new User();
            newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
            String strChat =getString(R.string.Application_and_notify);
            newFriends.setNick(strChat);
            getMyFriendsLists(newFriends);
        }

//        Iterator<Map.Entry<String, User>> iterator = users.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, User> entry = iterator.next();
//            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME)
//                    && !entry.getKey().equals(Constant.GROUP_USERNAME)
//                    && !entry.getKey().equals(Constant.CHAT_ROOM)
//                    && !entry.getKey().equals(Constant.CHAT_ROBOT)
//                    && !blackList.contains(entry.getKey()))
//                contactList.add(entry.getValue());
//        }

        // 排序
//        Collections.sort(contactList, new Comparator<User>() {
//
//            @Override
//            public int compare(User lhs, User rhs) {
//                return lhs.getUsername().compareTo(rhs.getUsername());
//            }
//        });

//        if (users.get(Constant.CHAT_ROBOT) != null) {
//            contactList.add(0, users.get(Constant.CHAT_ROBOT));
//        }
//        // 加入"群聊"和"聊天室"
//        if (users.get(Constant.CHAT_ROOM) != null)
//            contactList.add(0, users.get(Constant.CHAT_ROOM));
//        if (users.get(Constant.GROUP_USERNAME) != null)
//            contactList.add(0, users.get(Constant.GROUP_USERNAME));

        // 把"申请与通知"添加到首位
//        if (users.get(Constant.NEW_FRIENDS_USERNAME) != null)
//            contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));
//        if (adapter != null)
//            adapter.notifyDataSetChanged();
//        AppContext.toLog(contactList.size()+"集合大小");


//        AppContext.toast(contactList.size()+"集合大小"+users.size());
    }


    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if(((MainActivity)getActivity()).isConflict){
//            outState.putBoolean("isConflict", true);
//        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
//            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
//        }

    }

    private void getMyFriendsLists(final User user) {
        if (AppContext.getProperty(Config.MYFRIENDSTR)!=null){
            //缓存数据
            getJSON(AppContext.getProperty(Config.MYFRIENDSTR),user);
        }
        HttpClient.instance().myfriends_list(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.setProperty(Config.MYFRIENDSTR,responseBean.toString());
                List<User> listData = responseBean.getListData(User.class);
                UserDao userDao=new UserDao(mContext);
                userDao.saveContactList(listData);
//                userDao.
                List<User> mylistData=new ArrayList<User>();
                for (int i = 0; i < listData.size(); i++) {
                    if (blackList.contains(listData.get(i).getUsername())) {
                        listData.remove(i);
                        i--;
                        continue;
                    }else {
                        User user1 = listData.get(i);
                        setUserHearder(user1.getUsername(), user1);

//                    //删除数据库该好友消息
//                    userDao.deleteContact(listData.get(i).getEmid());
                        //重新存储该好友消息
//                        userDao.saveContact(user1);
                        mylistData.add(user1);
                    }
                }

//                if (contactList.size() > 0) {
//                    NotiUser = contactList.get(0);
//
//                }
                contactList.clear();
                contactList.addAll(mylistData);
                // 排序
                Collections.sort(contactList, new Comparator<User>() {

                    @Override
                    public int compare(User lhs, User rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                contactList.add(0, user);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                AppContext.toLog(contactList.size() + "集合大小");


            }
        });
    }

    private void getJSON(String str,User user1){
        try {
            JSONObject jsonObject=new JSONObject(str);
            JSONArray result = jsonObject.getJSONArray("result");
            contactList.clear();
            for (int i=0;i<result.length();i++){
                JSONObject object = result.getJSONObject(i);
                String id_image = object.getString("id_image");
                String name = object.getString("name");
                String emid = object.getString("emid");
                User user=new User();
                user.setEmid(emid);
                user.setName(name);
                user.setId_image(id_image);
                contactList.add(user);
            }
            for (int i=0;i<contactList.size();i++){
                setUserHearder(contactList.get(i).getEmid(),contactList.get(i));
            }
            // 排序
            Collections.sort(contactList, new Comparator<User>() {

                @Override
                public int compare(User lhs, User rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            contactList.add(0, user1);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_friends;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public void onEvent(EMNotifierEvent emNotifierEvent) {
//
//    }


    class HXContactSyncListener implements HXSDKHelper.HXSyncListener {
        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            runOnUiThread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (success) {
                                progressBar.setVisibility(View.GONE);
                                refresh();
                            } else {
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            });
        }
    }


    //
    class HXBlackListSyncListener implements HXSDKHelper.HXSyncListener {

        @Override
        public void onSyncSucess(boolean success) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    blackList = EMContactManager.getInstance().getBlackListUsernames();
                    refresh();
                }

            });
        }

    }

    ;

    //
    class HXContactInfoSyncListener implements HXSDKHelper.HXSyncListener {

        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        refresh();
                    }
                }
            });
        }

    }


    private void init() {
        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        EMChat.getInstance().setAppInited();
        Log.e("TAAG","----------------------->chatFriendsActivity"+"重新监听好友变化");
        // 注册一个监听连接状态的listener
//        connectionListener = new MyConnectionListener();
//        EMChatManager.getInstance().addConnectionListener(connectionListener);
////        AppContext.toast("进来 init()");
//
//        groupChangeListener = new MyGroupChangeListener();
//        // 注册群聊相关的listener
//        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);


    }
    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
//            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
//            Map<String, User> toAddUsers = new HashMap<String, User>();
//            for (String username : usernameList) {
//                User user = setUserHead(username);
//                // 添加好友时可能会回调added方法两次
//                if (!localUsers.containsKey(username)) {
//                    userDao.saveContact(user);
//                }
//                toAddUsers.put(username, user);
//            }
//            localUsers.putAll(toAddUsers);
//            Log.e("TAG", "------------->保存增加的联系人" + usernameList.size());
//            // 刷新ui
//            if (currentTabIndex == 1)
//                contactListFragment.refresh();
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
//            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
//            for (String username : usernameList) {
//                localUsers.remove(username);
//                userDao.deleteContact(username);
//                inviteMessgeDao.deleteMessage(username);
//            }
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    // 如果正在与此用户的聊天页面
//                    String st10 = getResources().getString(R.string.have_you_removed);
//                    if (ChatActivity.activityInstance != null
//                            && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
//                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
//                                .show();
//                        ChatActivity.activityInstance.finish();
//                    }
////                    updateUnreadLabel();
////                    // 刷新ui
////                    contactListFragment.refresh();
////                    chatHistoryFragment.refresh();
//                }
//            });

        }

        @Override
        public void onContactInvited(String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            Log.e("TAG", "------------->" + username + "请求加你为好友,reason: " + reason);
            Log.e("TAAG","----------------------->chatFriendsActivity"+"请求加你为好友,reason: " + reason);
        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {

            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
        // 提示有新消息
        HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
//        updateUnreadAddressLable();
        // 刷新好友页面ui

    }

}
