package com.sht.smartlock.ui.activity.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.ChatMsgEntity;
import com.sht.smartlock.ui.activity.BusinessCentreActivity;
import com.sht.smartlock.ui.activity.CompleteActivity;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.MyChatHistoryActivity;
import com.sht.smartlock.ui.activity.MyLockGroupsActivity;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.activity.SetWakeActivity;
import com.sht.smartlock.ui.activity.SettingMorningCallActivity;
import com.sht.smartlock.ui.activity.TastByListActivity;
import com.sht.smartlock.ui.activity.mine.SetFreeActivity;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.activity.myview.HomeButtonView;
import com.sht.smartlock.ui.activity.myview.LinearLayoutForListView;
import com.sht.smartlock.ui.activity.myview.MyImagView;
import com.sht.smartlock.ui.activity.myview.MyListView;
import com.sht.smartlock.ui.adapter.ChatMsgViewAdapter;
import com.sht.smartlock.ui.chat.applib.activity.ChatActivity;
import com.sht.smartlock.ui.chat.applib.adapter.ChatAllHistoryAdapter;
import com.sht.smartlock.ui.chat.applib.adapter.VoicePlayClickListener;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.InviteMessgeDao;
import com.sht.smartlock.ui.chat.applib.db.UserDao;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.ui.chat.applib.utils.CommonUtils;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.MyVoid;
import com.sht.smartlock.widget.dialog.CommonDialog;
import com.sht.smartlock.widget.dialog.DialogHelper;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class NewServiceFragment extends Fragment implements View.OnClickListener {

    //    private RadioGroup rgTab;
    public static LinearLayout linLeft, linRight;
    private ImageView ivShopping, ivFoods, ivClean, ivChang, ivOtherServicer, ivNoCallMe, icCallMe;
    //    private RelativeLayout reChatList;
//    private LinearLayout linChat;
    private MyListView lvChatList;

    //-------------------------------
    private ChatAllHistoryAdapter adapter;
    private boolean isBlack = false;
    private EMConversation serviceChat;
    private List<User> myUsers = new ArrayList<>();
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private List<String> blacklist;
    public static TextView tvMyChatNum;
    //    private ImageView ivVoicer, ivVoicerAnim, ivWrite, ivSend;
//    private RelativeLayout reMessageLayout;
    private View linContain;
    private Drawable[] micImages;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
//            ivVoicerAnim.setImageDrawable(micImages[msg.what]);
            Log.e("voice", "di ji ge tupian===" + msg.what);
        }
    };
    //    private EditText etMessage;
    private LinearLayout linOther;
    private ImageView ivEmpty;
    private InputMethodManager inputMethodManager;
    //    private ImageView ivRefresh;
//    private ListView lvServiceForOther;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 22;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.RECORD_AUDIO;//录音权限
    //    private static final String WRITE_PERMISSION02 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//    private static final String WRITE_PERMISSION03 = Manifest.permission.READ_EXTERNAL_STORAGE;
    private List<String> listPermission = new ArrayList<>();
    private MyImagView ivChatRoom;
    private RelativeLayout relold, relNoTask, relOkTask;
    private RelativeLayout relChatHistory;
    public static ImageView ivCleanMsg,ivChangMsg,ivOtherMsg;
    private MySQLiteOpenHelper mydbHelper = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.newservicefragment, container, false);
        mPermissionHelper = new PermissionHelper(getActivity());
        listPermission.add(WRITE_PERMISSION);
//        listPermission.add(WRITE_PERMISSION02);
//        listPermission.add(WRITE_PERMISSION03);
        initView(view);
        initVoiceView(view);
        setClickListener();

        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            //显示客服的聊天账号
            getserviceChat();
            //获取我的app好友列表
            //获取本地服务器好友消息
            if (AppContext.getShareUserSessinid() != null) {
                getMyFriendsLists();
            }
        }

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{getResources().getDrawable(R.drawable.pic_1),
                getResources().getDrawable(R.drawable.pic_2),
                getResources().getDrawable(R.drawable.pic_3),
                getResources().getDrawable(R.drawable.pic_1),
                getResources().getDrawable(R.drawable.pic_2),
                getResources().getDrawable(R.drawable.pic_3),
                getResources().getDrawable(R.drawable.pic_1),
                getResources().getDrawable(R.drawable.pic_2),
                getResources().getDrawable(R.drawable.pic_3),
                getResources().getDrawable(R.drawable.pic_1),
                getResources().getDrawable(R.drawable.pic_2),
                getResources().getDrawable(R.drawable.pic_3),
                getResources().getDrawable(R.drawable.pic_1),
                getResources().getDrawable(R.drawable.pic_2)};


        return view;
    }

    private void initView(View view) {

        linLeft = (LinearLayout) view.findViewById(R.id.linLeft);
        linRight = (LinearLayout) view.findViewById(R.id.linRight);
        //
        ivShopping = (ImageView) view.findViewById(R.id.ivShopping);
        ivFoods = (ImageView) view.findViewById(R.id.ivFoods);
        ivClean = (ImageView) view.findViewById(R.id.ivClean);
        ivChang = (ImageView) view.findViewById(R.id.ivChang);
        ivOtherServicer = (ImageView) view.findViewById(R.id.ivOtherServicer);
        ivNoCallMe = (ImageView) view.findViewById(R.id.ivNoCallMe);
        icCallMe = (ImageView) view.findViewById(R.id.icCallMe);
        linContain = (View) view.findViewById(R.id.linContain);

        tvMyChatNum = (TextView) view.findViewById(R.id.tvMyChatNum);
//        linChat = (LinearLayout) view.findViewById(R.id.linChat);
//        reChatList = (RelativeLayout) view.findViewById(R.id.reChatList);
        ivChatRoom = (MyImagView) view.findViewById(R.id.ivChatRoom);
//        ivChatRoom.setRa
        ivChatRoom.setRatio(2.5f);
        relold = (RelativeLayout) view.findViewById(R.id.relold);
        relNoTask = (RelativeLayout) view.findViewById(R.id.relNoTask);
        relOkTask = (RelativeLayout) view.findViewById(R.id.relOkTask);
        relChatHistory = (RelativeLayout) view.findViewById(R.id.relChatHistory);
        //------
        lvChatList = (MyListView) view.findViewById(R.id.lvChatList);
        conversationList.addAll(loadConversationsWithRecentChat());
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList, myUsers);

        ivCleanMsg = (ImageView) view.findViewById(R.id.ivCleanMsg);
        ivChangMsg = (ImageView) view.findViewById(R.id.ivChangMsg);
        ivOtherMsg = (ImageView) view.findViewById(R.id.ivOtherMsg);

        // 设置adapter
        lvChatList.setAdapter(adapter);
        // 从本地获取黑名单
        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            blacklist = EMContactManager.getInstance().getBlackListUsernames();
        } else {
            blacklist = new ArrayList<>();
        }

        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);

        lvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
////                    startActivity(new Intent(getActivity(),MyLockGroupsActivity.class));
//                    return;
//                } else {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();

                if (username.equals(AppContext.getInstance().getUserName()))
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
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
                        isBlack = false;
                        if (blacklist.size() > 0) {
                            for (int i = 0; i < blacklist.size(); i++) {
                                if (blacklist.get(i).equals(username)) {
                                    isBlack = true;
                                    break;//跳出for循环
                                }
                            }
                            intent.putExtra(Config.ISBLACK, isBlack);
                        } else {
                            intent.putExtra(Config.ISBLACK, isBlack);
                        }
                        //好友就带昵称传递
                        for (int i = 0; i < myUsers.size(); i++) {
                            if (myUsers.get(i).getEmid().equals(username)) {
                                intent.putExtra(Config.NICKNAME, myUsers.get(i).getName());
                                break;  //跳出for循环
                            }
                        }
                    }
                    startActivity(intent);
                }
//                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(lvChatList);
        lvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    private void setClickListener() {
        ivShopping.setOnClickListener(this);
        ivFoods.setOnClickListener(this);
        ivClean.setOnClickListener(this);
        ivChang.setOnClickListener(this);
        ivNoCallMe.setOnClickListener(this);
        icCallMe.setOnClickListener(this);
        ivOtherServicer.setOnClickListener(this);

//        linChat.setOnClickListener(this);
        linContain.setOnClickListener(this);

        //聊天列表
        ivChatRoom.setOnClickListener(this);
        relold.setOnClickListener(this);
        relNoTask.setOnClickListener(this);
        relOkTask.setOnClickListener(this);
        relChatHistory.setOnClickListener(this);


    }

    public static RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rbService:
                    linLeft.setVisibility(View.VISIBLE);
                    linRight.setVisibility(View.GONE);
                    break;
                case R.id.rbCall:
                    linLeft.setVisibility(View.GONE);
                    linRight.setVisibility(View.VISIBLE);
//                        todo();
                    break;
            }
        }
    };


    private View getListHeadView() {
        View lsHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.chatroom, null);
        tvMyChatNum = (TextView) lsHeadView.findViewById(R.id.tvMyChatNum);
//        linChat = (LinearLayout) lsHeadView.findViewById(R.id.linChat);
        return lsHeadView;
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.ivShopping:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
//				noInHole=true;
                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getShopping().equals("0")) {
                        intent.setClass(getActivity(), OrderFoodActivity.class);
                        bundle.putString(Config.ORDER_TAB, 1 + "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer01));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivFoods:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getDinner().equals("0")) {
                        intent.setClass(getActivity(), OrderFoodActivity.class);
                        bundle.putString(Config.ORDER_TAB, 2 + "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer02));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivClean:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getClear().equals("0")) {
                        if (NewDoorFragment.list.get(NewDoorFragment.pos).getService_start_time().equals("null")) {//24小时提供
                            intent.setClass(getActivity(), TastByListActivity.class);
                            bundle.putString(Config.SERVICE_TYPE, "1");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            final int startTime = Integer.parseInt(NewDoorFragment.list.get(NewDoorFragment.pos).getService_start_time());
                            final int durationTime = Integer.parseInt(NewDoorFragment.list.get(NewDoorFragment.pos).getService_duration());
                            CommonDialog dialog = DialogHelper.getPinterestDialog(getActivity());
                            dialog.setTitle("服务时间：");
                            int n = 0;
                            if ((startTime + durationTime) / 60 < 24) {
                                n = 0;
                                if (startTime < DateUtil.getNowTime() && (startTime + durationTime) > DateUtil.getNowTime()) {
                                    intent.setClass(getActivity(), TastByListActivity.class);
                                    bundle.putString(Config.SERVICE_TYPE, "1");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }else {
                                    dialog.setMessage("每天：" + startTime / 60 + ":" + startTime % 60 + "~" + (startTime + durationTime) / 60 + ":" + (startTime + durationTime) % 60);
                                    dialog.show();
                                }
                            } else if ((startTime + durationTime) / 60 > 24 && (startTime + durationTime) / 60 - startTime / 60 < 24) {
                                n = 1;
                                if (startTime < DateUtil.getNowTime() || (startTime + durationTime - 24 * 60) > DateUtil.getNowTime()) {
                                    intent.setClass(getActivity(), TastByListActivity.class);
                                    bundle.putString(Config.SERVICE_TYPE, "1");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }else {
                                    dialog.setMessage("" + startTime / 60 + ":" + startTime % 60 + "~次日：" + ((startTime + durationTime) / 60 - 24) + ":" + (startTime + durationTime) % 60);
                                    dialog.show();
                                }
                            } else {
                                n = 2;
                                intent.setClass(getActivity(), TastByListActivity.class);
                                bundle.putString(Config.SERVICE_TYPE, "1");
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dialog.setMessage("全天服务");

                            }
                            final int finalN = n;
                            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (finalN == 0) {
                                        if (startTime < DateUtil.getNowTime() && (startTime + durationTime) > DateUtil.getNowTime()) {
                                            intent.setClass(getActivity(), TastByListActivity.class);
                                            bundle.putString(Config.SERVICE_TYPE, "1");
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } else if (finalN == 1) {
                                        if (startTime < DateUtil.getNowTime() || (startTime + durationTime - 24 * 60) > DateUtil.getNowTime()) {
                                            intent.setClass(getActivity(), TastByListActivity.class);
                                            bundle.putString(Config.SERVICE_TYPE, "1");
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } else if (finalN == 2) {
                                        intent.setClass(getActivity(), TastByListActivity.class);
                                        bundle.putString(Config.SERVICE_TYPE, "1");
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }

                                }
                            });
//                            dialog.show();
                        }
                    } else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer03));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivChang:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }

                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getReplace().equals("0")) {

                        if (NewDoorFragment.list.get(NewDoorFragment.pos).getService_start_time().equals("null")) {//24小时提供
                            intent.setClass(getActivity(), TastByListActivity.class);
                            bundle.putString(Config.SERVICE_TYPE, "2");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            final int startTime = Integer.parseInt(NewDoorFragment.list.get(NewDoorFragment.pos).getService_start_time());
                            final int durationTime = Integer.parseInt(NewDoorFragment.list.get(NewDoorFragment.pos).getService_duration());
                            CommonDialog dialog = DialogHelper.getPinterestDialog(getActivity());
                            dialog.setTitle("服务时间：");

                            int n = 0;
                            if ((startTime + durationTime) / 60 < 24) {
                                n = 0;
                                if (startTime < DateUtil.getNowTime() && (startTime + durationTime) > DateUtil.getNowTime()) {
                                    intent.setClass(getActivity(), TastByListActivity.class);
                                    bundle.putString(Config.SERVICE_TYPE, "2");
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }else {
                                    dialog.setMessage("每天：" + startTime / 60 + ":" + startTime % 60 + "~" + (startTime + durationTime) / 60 + ":" + (startTime + durationTime) % 60);
                                    dialog.show();

                                }
                            } else if ((startTime + durationTime) / 60 > 24 && (startTime + durationTime) / 60 - startTime / 60 < 24) {
                                n = 1;

                                if (startTime < DateUtil.getNowTime() || (startTime + durationTime - 24 * 60) > DateUtil.getNowTime()) {
                                    intent.setClass(getActivity(), TastByListActivity.class);
                                    bundle.putString(Config.SERVICE_TYPE, "2");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    Log.e("TIME", "---------->" + true);
                                }else {
                                    dialog.setMessage("" + startTime / 60 + ":" + startTime % 60 + "~次日：" + ((startTime + durationTime) / 60 - 24) + ":" + (startTime + durationTime) % 60);
                                    dialog.show();
                                    Log.e("TIME", "---------->" + false);
                                }
                            } else {
                                n = 2;
                                intent.setClass(getActivity(), TastByListActivity.class);
                                bundle.putString(Config.SERVICE_TYPE, "2");
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dialog.setMessage("全天服务");
                            }
                            final int finalN = n;
                            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (finalN == 0) {
                                        if (startTime < DateUtil.getNowTime() && (startTime + durationTime) > DateUtil.getNowTime()) {
                                            intent.setClass(getActivity(), TastByListActivity.class);
                                            bundle.putString(Config.SERVICE_TYPE, "2");
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } else if (finalN == 1) {
                                        if (startTime < DateUtil.getNowTime() || (startTime + durationTime - 24 * 60) > DateUtil.getNowTime()) {
                                            intent.setClass(getActivity(), TastByListActivity.class);
                                            bundle.putString(Config.SERVICE_TYPE, "2");
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            Log.e("TIME", "----------dialog>" + true);
                                        }
                                    } else if (finalN == 2) {
                                        intent.setClass(getActivity(), TastByListActivity.class);
                                        bundle.putString(Config.SERVICE_TYPE, "2");
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }


                                }
                            });
//                            dialog.show();
                        }


                    } else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer04));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.home_Business_Centre:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    startActivity(new Intent(getActivity(), BusinessCentreActivity.class));
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivNoCallMe:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
                //判断是否入住
                if (NewDoorFragment.noInHole) {
//					BaseApplication.toast("免打扰");
                    intent.setClass(getActivity(), SetFreeActivity.class);
                    bundle.putString("statefree", "0");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.icCallMe:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
//				noInHole=true;
                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getAwaken().equals("0")) {
                        intent.setClass(getActivity(), SetWakeActivity.class);
                        bundle.putString(Config.SERVICE_TYPE, "3");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer05));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;
            case R.id.ivChatRoom:
                intent.setClass(getActivity(), MyLockGroupsActivity.class);
                startActivity(intent);
                break;

            case R.id.ivOtherServicer:
                //判断是否登录
                if (AppContext.getShareUserSessinid() == null) {
                    BaseApplication.toast(R.string.Toast_Login);
                    intent.setClass(getActivity(), LoginByNameActivity.class);
                    startActivity(intent);
                    return;
                }
//				noInHole=true;
                //判断是否入住
                if (NewDoorFragment.noInHole) {
                    if (NewDoorFragment.list.get(NewDoorFragment.pos).getOther().equals("0")) {
                    intent.setClass(getActivity(), TastByListActivity.class);
                    bundle.putString(Config.SERVICE_TYPE, "4");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    }else {
                        AppContext.toast(getResources().getString(R.string.HotelNoServicer06));
                    }
                } else {
                    BaseApplication.toast(R.string.Toast_Login_Chenkin);
                }
                break;

            case R.id.linContain:
                MainActivity.idMenu.closeMenu();
                break;
            case R.id.relold:
                startActivity(new Intent(getActivity(), CompleteActivity.class));
                break;
            case R.id.relNoTask:
                startActivity(new Intent(getActivity(), CompleteActivity.class));
                break;
            case R.id.relOkTask:
                startActivity(new Intent(getActivity(), CompleteActivity.class));
                break;
            case R.id.relChatHistory:
                startActivity(new Intent(getActivity(), MyChatHistoryActivity.class));
                break;

        }


    }


//-------------------------------------------------------------------------------------------------


    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        getUnreadMsgCountTotal();
        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            getserviceChat();
        }
        if (adapter != null) {
//            AppContext.toast("adapter.notifyDataSetChanged()" + conversationList.size());
            adapter.getList(loadConversationsWithRecentChat());
            if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 刷新页面
     */
    public void refreshMain() {
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        getUnreadMsgCountTotal();
        getserviceChat();
        if (adapter != null)
            if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
                adapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
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
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        adapter.remove(tobeDeleteCons);
        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            adapter.notifyDataSetChanged();
        }

        // 更新消息未读数
//        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled ? true : super.onContextItemSelected(item);
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
    public void onResume() {
        super.onResume();
        if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
            blacklist = EMContactManager.getInstance().getBlackListUsernames();
            getUnreadMsgCountTotal();
        }
        setMsgInfo();
//        if (!hidden) {
//        }

         /*
        *   注册监听
        *
        * */
//        EMChatManager.getInstance().registerEventListener(this,
//                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
        refresh();
//        todo();

    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
//        if (unreadMsgCountTotal > 0) {
//            tvMyChatNum.setText(unreadMsgCountTotal + "");
//            tvMyChatNum.setVisibility(View.VISIBLE);
//        } else {
//            tvMyChatNum.setText(0 + "");
//            tvMyChatNum.setVisibility(View.GONE);
//        }
//        return unreadMsgCountTotal-chatroomUnreadMsgCount;
        return unreadMsgCountTotal;
    }


    private void getserviceChat() {
        AppContext.setProperty(Config.SERVICECHAT, "service");
        String serviceUserName = AppContext.getProperty(Config.SERVICECHAT);
        serviceChat = new EMConversation(serviceUserName);
//        AppContext.toast("1"+conversationList.size());
        if (conversationList.size() > 0) {
            for (int i = 0; i < conversationList.size(); i++) {
                if (conversationList.get(i).getUserName().equals(serviceUserName)) {
                    //已存在服务账号
                    return;
                }
            }
            conversationList.add(serviceChat);
//            AppContext.toast("2" + conversationList.size());
            if (adapter != null)
                if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
                    adapter.notifyDataSetChanged();
                }
        } else {
            conversationList.add(serviceChat);
//            AppContext.toast("3:" + conversationList.size());
            if (adapter != null) {
                if (DemoHXSDKHelper.getInstance().isLogined()) {//环信是否已经登录了
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    //获取app好友列表
    private void getMyFriendsLists() {
        if (AppContext.getProperty(Config.MYFRIENDSTR) != null) {
            //缓存数据
            getJSON(AppContext.getProperty(Config.MYFRIENDSTR));
        }
        HttpClient.instance().myfriends_list(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.setProperty(Config.MYFRIENDSTR, responseBean.toString());
                List<User> listData = responseBean.getListData(User.class);
//                for (int i=0;i<listData.size();i++){
//                    listData.get(i).setAvatar("http://img1.imgtn.bdimg.com/it/u=1820659434,386753461&fm=21&gp=0.jpg");
//                }
                UserDao userDao = new UserDao(getActivity());
                userDao.saveContactList(listData);
                myUsers.addAll(listData);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    private void getJSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i = 0; i < result.length(); i++) {
                JSONObject object = result.getJSONObject(i);
                String id_image = object.getString("id_image");
                String name = object.getString("name");
                String emid = object.getString("emid");
                User user = new User();
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

    //----------------------------------------呼叫服务功能-------------------------------
    private PowerManager.WakeLock wakeLock;
    public MyVoid voiceRecorder;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private Animation operatingAnim;

    private void initVoiceView(View view) {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        voiceRecorder = new MyVoid(micImageHandler);
        wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        linOther = (LinearLayout) view.findViewById(R.id.linOther);
        ivEmpty = (ImageView) view.findViewById(R.id.ivEmpty);


//        lvServiceForOther = (ListView) view.findViewById(R.id.lvServiceForOther);
        mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
//        lvServiceForOther.setAdapter(mAdapter);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.ivRefresh:
//                        ivRefresh.setImageResource(R.drawable.pic_refreshing);
//                        animOpenDoor(ivRefresh);
//                        initChatData();
//                        break;
//                    case R.id.reMessageLayout:
//                        break;
//                    case R.id.ivSend:
//                        send();
//                        break;
//                    case R.id.ivWrite:
//                        int visibility = reMessageLayout.getVisibility();
//                        if (visibility == View.VISIBLE) {
//                            reMessageLayout.setVisibility(View.GONE);
//                        } else {
//                            reMessageLayout.setVisibility(View.VISIBLE);
//                        }
//                        break;
                }
            }
        };

//        ivVoicer.setOnClickListener(clickListener);

//        ivVoicer.setOnTouchListener(new PressToSpeakListen());

    }

    private void todo() {
        if (NewDoorFragment.list.size() > 0 && !NewDoorFragment.list.get(NewDoorFragment.pos).getCheckin_time().equals("null")) {
            linOther.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.GONE);
            initChatData();
//            lvChatList.setSelection(lvChatList.getBottom());
        } else {
            linOther.setVisibility(View.GONE);
            ivEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        AppContext.toast(st4);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
//                        ivVoicerAnim.setVisibility(View.VISIBLE);
                        boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                        if (b) {
                            if (NewDoorFragment.list.get(NewDoorFragment.pos).getID().isEmpty() || NewDoorFragment.list.get(NewDoorFragment.pos).getID().equals("null")) {
                                voiceRecorder.startRecording(null, "service", getActivity());
                            } else {
                                voiceRecorder.startRecording(null, NewDoorFragment.list.get(NewDoorFragment.pos).getID(), getActivity());
                            }
                        } else {
                            mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
//                        ivVoicerAnim.setVisibility(View.GONE);
                        AppContext.toast(R.string.recoding_fail + "e==" + e.toString());
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                    } else {
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
//                    recordingContainer.setVisibility(View.INVISIBLE);
//                    ivVoicerAnim.setImageResource(R.drawable.pic_1);
//                    ivVoicerAnim.setVisibility(View.GONE);
//                    ivVoicer.setImageResource(R.drawable.voicer);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
//                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
//                                        Integer.toString(length), false);

                                Log.e("taaag", "path======" + voiceRecorder.getVoiceFilePath());
                                setVoid(voiceRecorder.getVoiceFilePath(), Integer.toString(length));
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getActivity(), st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppContext.toast(st3 + "" + e.toString());
                        }

                    }
                    return true;
                default:
//                    ivVoicerAnim.setImageResource(R.drawable.pic_1);
//                    ivVoicerAnim.setVisibility(View.GONE);
//                    ivVoicer.setImageResource(R.drawable.voicer);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    private void setVoid(String marFilePath, String time) {
        File file = new File(marFilePath);
        if (!file.exists()) {
        } else {//文件存在时才发送
            byte[] fileData = fileData(marFilePath);
            String strs = Base64Utils.encode(fileData);
            //  删除数据
//            file.delete();

            String str = null;
            //1 房间id, 2 内容，
                            /*第三个参数service_type		服务类型
                            1 – 房间清扫
                            2 – 更换床上用品
                            3 - Morning Call
                            4 - 其它
                            */
            str = time + "-2" + strs;
            //4参 ：本文为1 ，语音为2
            Log.e("taaag", str);
            HttpClient.instance().addhotelService(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), str, "4", "2", new HttpCallBack() {
                @Override
                public void onStart() {
                    super.onStart();
                    ProgressDialog.show(getActivity(), "正在发送语音...");
                }

                @Override
                public void onFailure(String error, String message) {
                    super.onFailure(error, message);
                    ProgressDialog.disMiss();
                    AppContext.toast("语音发送失败");
                }

                @Override
                public void onSuccess(ResponseBean responseBean) {
                    ProgressDialog.disMiss();
                    AppContext.toast("语音发送成功");
//                    ivVoicerAnim.setImageResource(R.drawable.pic_1);
//                    ivVoicerAnim.setVisibility(View.GONE);
                    initChatData();
                }
            });
//                ChatMsgEntity entity = new ChatMsgEntity();
//                entity.setMsgType(false);
//                entity.setVoicTime(time + "\"");
//                entity.setCreate_time(getDate());
//                entity.setContent(str);
//                entity.setContent_type("2");
//                mDataArrays.add(entity);
//                mAdapter.notifyDataSetChanged();
//                mListView.setSelection(mListView.getCount() - 1);
//                rcChat_popup.setVisibility(View.GONE);
        }
    }

    // 读取文件数据转换成byte[]数据
    private byte[] fileData(String imageFilePath) {
        File file = new File(imageFilePath);
        FileInputStream fStream;
        try {
            fStream = new FileInputStream(file);
//            // 获取文件大小
//            int leng_file = fStream.available();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bufferSize = 1024 * 8;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
//            if (leng_file > (20 * 1024 * 1024)) {
////                handler.sendEmptyMessage(0);
//                return null;
//            }
            while ((length = fStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            fStream.close();
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void send() {
        String contString = "";
        if (contString.length() > 0) {
//            etMessage.setText("");
            if (NewDoorFragment.list.size() == 0) {
                //无数据
                return;
            }
            HttpClient.instance().addhotelService(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), contString, "4", "1", new HttpCallBack() {
                @Override
                public void onStart() {
                    super.onStart();
                    ProgressDialog.show(getActivity(), "正在发送...");
                }

                @Override
                public void onFailure(String error, String message) {
                    super.onFailure(error, message);
                    ProgressDialog.disMiss();
                    AppContext.toast("消息发送失败");
                }

                @Override
                public void onSuccess(ResponseBean responseBean) {
                    ProgressDialog.disMiss();
//                    etMessage.setText("");
                    AppContext.toast("消息发送成功");
                    initChatData();
                }
            });
        } else {
            AppContext.toast("请输入内容");
        }

    }

    //获取该服务的数据
    private void initChatData() {
        if (NewDoorFragment.list.size() == 0) {
            //无数据
            return;
        }
        HttpClient.instance().get_myhotelService_list(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), "4", new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog(responseBean.toString());
                List<ChatMsgEntity> list = responseBean.getListData(ChatMsgEntity.class);
//                ivRefresh.clearAnimation();
//                ivRefresh.setImageResource(R.drawable.btn_refresh);
                mDataArrays.clear();
                mDataArrays.addAll(list);
                mAdapter.notifyDataSetChanged();
                AppContext.toLog(list.size() + ";;;;;;;" + list.toString());

            }
        });
    }

    private void animOpenDoor(ImageView view) {
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.opendoor);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        view.startAnimation(operatingAnim);
    }

    private void setMsgInfo(){
        mydbHelper = MySQLiteOpenHelper.getInstance(getActivity());
        String sqlString2 = "select * from NUNoMSG where service_type=? ";
        int cleanCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"1"});
        int changCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"2"});
        int otherCount = mydbHelper.selectCount(
                sqlString2,
                new String[] {"4"});

        Log.e("Save","__________cleanCount=>"+cleanCount+"changCount="+changCount+"otherCount="+otherCount);
        if (NewServiceFragment.ivCleanMsg!=null){
            if (cleanCount > 0) {
                // "数据已经存在需要更改
                NewServiceFragment.ivCleanMsg.setVisibility(View.VISIBLE);
            }else {
                NewServiceFragment.ivCleanMsg.setVisibility(View.GONE);
            }
        }
        if (changCount > 0) {
            // "数据已经存在需要更改
            NewServiceFragment.ivChangMsg.setVisibility(View.VISIBLE);
        }else {
            NewServiceFragment.ivChangMsg.setVisibility(View.GONE);

        }
        if (otherCount > 0) {
            // "数据已经存在需要更改
            NewServiceFragment.ivOtherMsg.setVisibility(View.VISIBLE);
        }else {
            NewServiceFragment.ivOtherMsg.setVisibility(View.GONE);
        }

        if (cleanCount>0||changCount>0||otherCount>0){
            MainActivity.ivServiceMsg.setVisibility(View.VISIBLE);
        }else {
            MainActivity.ivServiceMsg.setVisibility(View.GONE);
        }
    }

}
