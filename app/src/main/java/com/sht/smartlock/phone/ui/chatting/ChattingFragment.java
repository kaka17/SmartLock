/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.sht.smartlock.phone.ui.chatting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECAlertDialog;
import com.sht.smartlock.phone.common.dialog.ECListDialog;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.ClipboardUtils;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.ECNotificationManager;
import com.sht.smartlock.phone.common.utils.ECPreferenceSettings;
import com.sht.smartlock.phone.common.utils.ECPreferences;
import com.sht.smartlock.phone.common.utils.EmoticonUtil;
import com.sht.smartlock.phone.common.utils.FileAccessor;
import com.sht.smartlock.phone.common.utils.FileUtils;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.MediaPlayTools;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.core.ECAsyncTask;
import com.sht.smartlock.phone.storage.ContactSqlManager;
import com.sht.smartlock.phone.storage.ConversationSqlManager;
import com.sht.smartlock.phone.storage.GroupSqlManager;
import com.sht.smartlock.phone.storage.IMessageSqlManager;
import com.sht.smartlock.phone.storage.ImgInfoSqlManager;
import com.sht.smartlock.phone.ui.CCPFragment;
import com.sht.smartlock.phone.ui.LocationActivity;
import com.sht.smartlock.phone.ui.LocationInfo;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.chatting.base.ECPullDownView;
import com.sht.smartlock.phone.ui.chatting.base.OnListViewBottomListener;
import com.sht.smartlock.phone.ui.chatting.base.OnListViewTopListener;
import com.sht.smartlock.phone.ui.chatting.base.OnRefreshAdapterDataListener;
import com.sht.smartlock.phone.ui.chatting.model.ImgInfo;
import com.sht.smartlock.phone.ui.chatting.view.CCPChattingFooter2;
import com.sht.smartlock.phone.ui.chatting.view.SmileyPanel;
import com.sht.smartlock.phone.ui.contact.AtSomeoneUI;
import com.sht.smartlock.phone.ui.contact.ContactDetailActivity;
import com.sht.smartlock.phone.ui.contact.ContactLogic;
import com.sht.smartlock.phone.ui.contact.ECContacts;
import com.sht.smartlock.phone.ui.group.GroupInfoActivity;
import com.sht.smartlock.phone.ui.plugin.FileExplorerActivity;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECChatManager.OnChangeVoiceListener;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECUserState;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.Parameters;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.ecsdk.im.ECLocationMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVideoMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import java.io.File;
import java.io.InvalidClassException;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.ui.chatting in ECDemo_Android
 * Created by Jorstin on 2015/6/16.
 */
public class ChattingFragment extends CCPFragment implements
		View.OnClickListener, AbsListView.OnScrollListener,
		IMChattingHelper.OnMessageReportCallback,
		CustomerServiceHelper.OnCustomerServiceListener {

    public static final String TAG = "ECSDK_Demo.ChattingFragment";
    private static final int WHAT_ON_COMPUTATION_TIME = 10000;
    /**request code for tack pic*/
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x3;
    public static final int REQUEST_CODE_TAKE_LOCATION = 0x11;
    public static final int REQUEST_CODE_LOAD_IMAGE = 0x4;
    public static final int REQUEST_CODE_IMAGE_CROP = 0x5;
    /**查看名片*/
    public static final int REQUEST_VIEW_CARD = 0x6;
    /**选择回复联系人*/
    public static final int SELECT_AT_SOMONE = 0xD4;

	/** 会话ID，数据库主键 */
	public final static String THREAD_ID = "thread_id";
	/** 联系人账号 */
	public final static String RECIPIENTS = "recipients";
	/** 联系人名称 */
	public final static String CONTACT_USER = "contact_user";
	public final static String FROM_CHATTING_ACTIVITY = "from_chatting_activity";
	public final static String CUSTOMER_SERVICE = "is_customer_service";
	/** 按键振动时长 */
	public static final int TONE_LENGTH_MS = 200;
	/** 音量值 */
	private static final float TONE_RELATIVE_VOLUME = 100.0F;
	/** 待发送的语音文件最短时长 */
	private static final int MIX_TIME = 1000;
	/** 聊天界面消息适配器 */
	private ChattingListAdapter2 mChattingAdapter;
	/** 界面消息下拉刷新 */
	// private RefreshableView mPullDownView;
	// private long mPageCount;
	/** 历史聊天纪录消息显示View */
	private ListView mListView;
	private View mListViewHeadView;
	/** 聊天界面附加聊天控件面板 */
	private CCPChattingFooter2 mChattingFooter;
	/** 选择图片拍照路径 */
	private String mFilePath;
	/** 会话ID */
	private long mThread = -1;
	/** 会话联系人账号 */
	private String mRecipients;
	/** 联系人名称 */
	private String mUsername;
	/** 计算当前录音时长 */
	private long computationTime = -1L;
	/** 当前语言录制文件的时间长度 */
	private int mVoiceRecodeTime = 0;
	/** 是否使用边录制便传送模式发送语音 */
	private boolean isRecordAndSend = false;
	/** 手机震动API */
	private Vibrator mVibrator;
	private ToneGenerator mToneGenerator;
	/** 录音剩余时间Toast提示 */
	private Toast mRecordTipsToast;
	private ECHandlerHelper mHandlerHelper = new ECHandlerHelper();
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private Handler mVoiceHandler;
	private Looper mChattingLooper;
	/** IM聊天管理工具 */
	private ECChatManager mChatManager;
	/** 聊天底部导航控件通知回调 */
	private OnChattingFooterImpl mChattingFooterImpl = new OnChattingFooterImpl(
			(ChattingActivity) getActivity());
	/** 聊天功能插件接口实现 */
	private OnOnChattingPanelImpl mChattingPanelImpl = new OnOnChattingPanelImpl();
	private ECPullDownView mECPullDownView;
	/** 是否查看消息模式 */
	private boolean isViewMode = false;
	private View mMsgLayoutMask;
	public boolean mAtsomeone = false;
	/** 在线客服 */
	private boolean mCustomerService = false;
	private OnChattingAttachListener mAttachListener;

    @Override
    protected int getLayoutId() {
        return R.layout.chatting_activity;
    }
    
    public ECMessage getTopMsg(){
    	
    	return mChattingAdapter.getItem(0);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mAttachListener = (OnChattingAttachListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnChattingAttachListener");
		}
	}

	private ChattingActivity getChattingActivity() {
		if (getActivity() instanceof ChattingActivity) {
			return (ChattingActivity) getActivity();
		}
		throw new RuntimeException(getActivity().toString()
				+ " must implement OnChattingAttachListener");
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化联系人信息
		initActivityState(savedInstanceState);
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container,
				savedInstanceState);
		ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
		}
		contentView.setLayoutParams(layoutParams);
		return contentView;
	}
	
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		// 初始化界面资源
		initView();

		queryUIMessage();

		// 初始化IM聊天工具API
		mChatManager = SDKCoreHelper.getECChatManager();
		HandlerThread thread = new HandlerThread("ChattingVoiceRecord",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mChattingLooper = thread.getLooper();
		mVoiceHandler = new Handler(mChattingLooper);
		mVoiceHandler.post(new Runnable() {

            @Override
            public void run() {
                doEmojiPanel();
            }
        });

		registerReceiver(new String[] { IMessageSqlManager.ACTION_GROUP_DEL });
	}

	private void queryUIMessage() {
		mListView.post(new Runnable() {

			@Override
			public void run() {
				if (mChattingAdapter.getCount() < 18) {
					mECPullDownView.setIsCloseTopAllowRefersh(true);
					mECPullDownView.setTopViewInitialize(false);
				}
				mListView.clearFocus();
				mChattingAdapter.notifyChange();
				mListView.setSelection(mChattingAdapter.getCount());
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		AppPanelControl.setShowVoipCall(true);
		if (mCustomerService) {
			CustomerServiceHelper.addCustomerServiceListener(null);
			CustomerServiceHelper.finishService(mRecipients);
		}
		if (mChattingLooper != null) {
			mChattingLooper.quit();
			mChattingLooper = null;
		}
		if (mChattingFooter != null) {
			mChattingFooter.onDestory();
			mChattingFooter = null;
		}

		if (mHandlerHelper != null) {
			mHandlerHelper.getTheadHandler().removeCallbacksAndMessages(null);
			mHandlerHelper = null;
		}
		if (mVoiceHandler != null) {
			mVoiceHandler.removeCallbacksAndMessages(null);
			mVoiceHandler = null;
		}
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}
		if (mListView != null) {
			mListView.setOnItemLongClickListener(null);
			mListView.setOnItemClickListener(null);
		}
		if (mChattingAdapter != null) {
			mChattingAdapter.onDestory();
			mListView.setAdapter(null);
		}
		mChatManager = null;
		mOnItemLongClickListener = null;
		mOnListViewBottomListener = null;
		mOnListViewTopListener = null;
		mOnRefreshAdapterDataListener = null;
		if (mChattingFooterImpl != null) {
			mChattingFooterImpl.release();
			mChattingFooterImpl = null;
		}
		mChattingPanelImpl = null;
		mECPullDownView = null;
		setChattingContactId("");
		IMChattingHelper.setOnMessageReportCallback(null);
		System.gc();
		
		
	}

	/**
	 * 初始化聊天界面资源
	 */
	private void initView() {
		mListView = (ListView) findViewById(R.id.chatting_history_lv);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		mListView.setItemsCanFocus(false);
		mListView.setOnScrollListener(this);
		mListView.setKeepScreenOn(false);
		mListView.setStackFromBottom(false);
		mListView.setFocusable(false);
		mListView.setFocusableInTouchMode(false);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);
		registerForContextMenu(mListView);

		mListViewHeadView = getChattingActivity().getLayoutInflater().inflate(
				R.layout.chatting_list_header, null);
		mListView.addHeaderView(mListViewHeadView);
		mListView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				hideSoftKeyboard();
				if (mChattingFooter != null) {
					// After the input method you can use the record button.
					// mGroudChatRecdBtn.setEnabled(true);
					// mChatFooter.setMode(1);
					mChattingFooter.hideBottomPanel();
				}
				return false;
			}
		});

		mMsgLayoutMask = findViewById(R.id.message_layout_mask);
		mMsgLayoutMask.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideMsgLayoutMask();
				mListView.setSelection(mListView.getCount() - 1);
				return true;
			}
		});
		/************************************************************************************************************/
		mECPullDownView = (ECPullDownView) findViewById(R.id.chatting_pull_down_view);
		mECPullDownView.setTopViewInitialize(true);
		mECPullDownView.setIsCloseTopAllowRefersh(false);
		mECPullDownView.setHasbottomViewWithoutscroll(false);
		mECPullDownView
				.setOnRefreshAdapterDataListener(mOnRefreshAdapterDataListener);
		mECPullDownView.setOnListViewTopListener(mOnListViewTopListener);
		mECPullDownView.setOnListViewBottomListener(mOnListViewBottomListener);

        // 初始化聊天功能面板
        mChattingFooter = (CCPChattingFooter2) findViewById(R.id.nav_footer);
        // 注册聊天面板状态回调通知、包含录音按钮按钮下放开等录音操作
        mChattingFooter.setOnChattingFooterLinstener(mChattingFooterImpl);
        // 注册聊天面板附加功能（图片、拍照、文件）被点击回调通知
        mChattingFooter.setOnChattingPanelClickListener(mChattingPanelImpl);
        // 注册一个聊天面板文本输入框改变监听
        mChattingFooter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG , "[onTextChanged]");
                String text = String.valueOf(s);
                String value = text.substring(start, start + count);
                if(("@".equals(value) && isPeerChat()) && !text.equals(mChattingFooter.getLastContent()) && !mChattingFooter.isSetAtSomeoneing()) {
                    mChattingFooter.setLastContent(text);
                    mChattingFooter.setInsertPos(start + 1);
                    boolean handler = (text == null || start < 0 || text.length() < start);
                    if(!handler) {
                        Intent action = new Intent();
                        action.setClass(getChattingActivity(), AtSomeoneUI.class);
                        action.putExtra(AtSomeoneUI.EXTRA_GROUP_ID, mRecipients);
                        action.putExtra(AtSomeoneUI.EXTRA_CHAT_USER, CCPAppManager.getClientUser().getUserId());
                        startActivityForResult(action, 212);
                    }
                    return ;
                } else if (!text.equals(mChattingFooter.getLastContent())) {
                    mChattingFooter.setLastContent(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mChattingAdapter = new ChattingListAdapter2(getActivity() , ECMessage.createECMessage(ECMessage.Type.NONE) , mRecipients , mThread);
        mListView.setAdapter(mChattingAdapter);
    }

    private void hideBottom() {
        // 隐藏键盘
        hideSoftKeyboard();
        if(mChattingFooter != null) {
            // 隐藏更多的聊天功能面板
            mChattingFooter.hideBottomPanel();
        }
    }

    private Animation mAnimation;
    private void showMsgLayoutMask() {
        if(isViewMode && !mMsgLayoutMask.isShown() ) {
            if(mAnimation == null) {
                mAnimation = AnimationUtils.loadAnimation(getChattingActivity(), R.anim.buttomtip_in);
            }
            mMsgLayoutMask.setVisibility(View.VISIBLE);
            mMsgLayoutMask.startAnimation(mAnimation);
            mAnimation.start();
        }
    }

    private void hideMsgLayoutMask() {
        if(mMsgLayoutMask != null && mMsgLayoutMask.isShown()) {
            mMsgLayoutMask.setVisibility(View.GONE);
        }
    }

    /**
     * 读取聊天界面联系人会话参数信息
     * @param savedInstanceState
     */
    private void initActivityState(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        mRecipients = intent.getStringExtra(RECIPIENTS);
        mUsername = intent.getStringExtra(CONTACT_USER);

        if(TextUtils.isEmpty(mRecipients)) {
            ToastUtil.showMessage("联系人账号不存在");
            finish();
            return ;
        }
        mCustomerService = ContactLogic.isCustomService(mRecipients);

        if(mUsername == null) {
            ECContacts contact = ContactSqlManager.getContact(mRecipients);
            if(contact != null) {
                mUsername = contact.getNickname();
            } else {
                mUsername = mRecipients;
            }
        }
        if(!isPeerChat()) {
            IMessageSqlManager.checkContact(mRecipients, mUsername);
        }
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, isPeerChat() ? R.drawable.actionbar_facefriend_icon : R.drawable.actionbar_particular_icon, mUsername, this);
        setActionBarTitle(mUsername);
        mThread = ConversationSqlManager.querySessionIdForBySessionId(mRecipients);
        //mPageCount =  IMessageSqlManager.qureyIMCountForSession(mThread);
        aysnUserState();
    }

    public void aysnUserState() {
		if(isPeerChat()) {
			return ;
		}
		ECDevice.getUserState(mRecipients, new ECDevice.OnGetUserStateListener() {
			@Override
			public void onGetUserState(ECError ecError, ECUserState userState) {
				if (ecError.errorCode == SdkErrorCode.REQUEST_SUCCESS && userState != null) {
					String subTile = "对方不在线";
					if(userState.isOnline()) {
						subTile = DemoUtils.getDeviceWithType(userState.getDeviceType())
								+ "-" + DemoUtils.getNetWorkWithType(userState.getNetworkType());
					}

					if(getTopBarView()!=null){
					getTopBarView().setTopBarToStatus(1,
							R.drawable.topbar_back_bt,
							isPeerChat() ? R.drawable.actionbar_facefriend_icon : R.drawable.actionbar_particular_icon,
							null, null,
							mUsername,
							subTile,
							ChattingFragment.this);
					}
					return;
				}
				LogUtil.e(TAG, "getUserState fail");
			}
		});
	}

    /**
     * 是否群组
     * @return
     */
    public boolean isPeerChat() {

        return mRecipients != null && mRecipients.toLowerCase().startsWith("g");
    }

    /**
     * 返回聊天消息适配器
     * @return the mChattingAdapter
     */
    public ChattingListAdapter2 getChattingAdapter() {
        return mChattingAdapter;
    }

    public CCPChattingFooter2 getChattingFooter() {
        return mChattingFooter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            View topView = mListView.getChildAt(mListView.getFirstVisiblePosition());
            if ((topView != null) && (topView.getTop() == 0)){
                LogUtil.d(LogUtil.getLogUtilsTag(ChattingActivity.class), "doLoadingView auto pull");
                mECPullDownView.startTopScroll();
            }
        }
    }


    private boolean mHandlerDelChar = false;
	private String fileName;
	private String filePath;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            LogUtil.d(TAG, "keycode back , chatfooter mode: " +  mChattingFooter.getMode());
            if(!mChattingFooter.isButtomPanelNotVisibility()) {
                hideBottom();
                return true;
            }
            setIsFinish(true);
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                mHandlerDelChar = !(mChattingFooter.getCharAtCursor() != (char)(8197));
            }
            if(event.getAction() == KeyEvent.ACTION_UP && mHandlerDelChar) {
                mHandlerDelChar = false;
                CCPChattingFooter2 footer = this.mChattingFooter;
                int selectionStart = footer.getSelectionStart();
                String text = footer.getLastText().substring(0, selectionStart);
                int atIndex = text.lastIndexOf('@');
                if(atIndex < text.length() && atIndex >= 0) {
                    String subStartText = text.substring(0, atIndex);
                    String subSecondText = footer.getLastText().substring(selectionStart);
                    StringBuilder sb = new StringBuilder();
                    sb.append(subStartText).append(subSecondText);
                    footer.setLastText(sb.toString());
                    footer.mEditText.setSelection(atIndex);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override  //控制是否能刷新数据top
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        LogUtil.d(TAG, "[onScroll] firstVisibleItem :" + firstVisibleItem + " ,visibleItemCount:" + visibleItemCount + " ,totalItemCount:" + totalItemCount);
        isViewMode = !((firstVisibleItem + visibleItemCount) == totalItemCount);
        if(mECPullDownView != null ){
            if(!mChattingAdapter.isLimitCount()){
                mECPullDownView.setIsCloseTopAllowRefersh(false);
            }else{
                mECPullDownView.setIsCloseTopAllowRefersh(true);//小于18
            }
        }
        if(!isViewMode) hideMsgLayoutMask();
    }


    @Override
    public void onResume() {
        super.onResume();

        mChattingFooter.switchChattingPanel(SmileyPanel.APP_PANEL_NAME_DEFAULT);
        mChattingFooter.initSmileyPanel();
        IMChattingHelper.setOnMessageReportCallback(this);
        if(mCustomerService) {
            CustomerServiceHelper.addCustomerServiceListener(this);
        }
        // 将所有的未读消息设置为已读
        setChattingSessionRead();
        mChattingAdapter.onResume();

        checkPreviewImage();
        setChattingContactId(mRecipients);
        ECNotificationManager.getInstance().forceCancelNotification();

        initSettings(mRecipients);
        if(isPeerChat() && !GroupSqlManager.getJoinState(mRecipients)) {
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, mUsername, this);
            mChattingFooter.setVisibility(View.GONE);
            return ;
        }
        mChattingFooter.setVisibility(View.VISIBLE);
    }

    /**
     * @param mRecipients
     */
    private void initSettings(String mRecipients) {
        if(isPeerChat()) {
            ECGroup ecGroup = GroupSqlManager.getECGroup(mRecipients);
            if(ecGroup != null) {
                setActionBarTitle(ecGroup.getName() != null ? ecGroup.getName() : ecGroup.getGroupId());
                SpannableString charSequence = setNewMessageMute(!ecGroup.isNotice());
                if(charSequence != null) {
                    getTopBarView().setTitle(charSequence);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayVoice();
        setChattingContactId("");
    }

    /**
     * 保存当前的聊天界面所对应的联系人、方便来消息屏蔽通知
     */
    private void setChattingContactId(String contactid) {
        try {
            ECPreferences.savePreference(ECPreferenceSettings.SETTING_CHATTING_CONTACTID, contactid, true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有预览带发送图片
     */
    private void checkPreviewImage() {
        if(TextUtils.isEmpty(mFilePath)) {
            return ;
        }
        boolean previewImage = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_PREVIEW_SELECTED.getId()
                ,(Boolean)ECPreferenceSettings.SETTINGS_PREVIEW_SELECTED.getDefaultValue());
        if(previewImage){
            try {
                ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_PREVIEW_SELECTED, Boolean.FALSE, true);
                new ChattingAsyncTask(getChattingActivity()).execute(mFilePath);
                mFilePath = null;
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }
    }

    public long getmThread() {
        return mThread;
    }

    private void doEmojiPanel() {
        if(EmoticonUtil.getEmojiSize() == 0) {
            EmoticonUtil.initEmoji();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG ,"onActivityResult: requestCode=" + requestCode
                + ", resultCode=" + resultCode + ", data=" + data);

        // If there's no data (because the user didn't select a picture and
        // just hit BACK, for example), there's nothing to do.
        
        if (requestCode == 0x2a || requestCode == SELECT_AT_SOMONE) {
            if (data == null) {
                return;
            }
        } else if (resultCode != ChattingActivity.RESULT_OK) {
            LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
            isFireMsg=false;
            return;
        }

        if(data != null && 0x2a == requestCode) {
            handleAttachUrl(data.getStringExtra("choosed_file_path"));
            return ;
        }

        if(requestCode == REQUEST_CODE_TAKE_PICTURE
                || requestCode == REQUEST_CODE_LOAD_IMAGE) {
            if(requestCode == REQUEST_CODE_LOAD_IMAGE) {
                mFilePath = DemoUtils.resolvePhotoFromIntent(getChattingActivity(), data, FileAccessor.IMESSAGE_IMAGE);
            }
            if(TextUtils.isEmpty(mFilePath)) {
                return ;
            }
            File file = new File(mFilePath);
            if(file == null || !file.exists()) {
            	
                return;
            }
            try {
                ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_CROPIMAGE_OUTPUTPATH, file.getAbsolutePath(), true);
                Intent intent = new Intent(getChattingActivity(), ImagePreviewActivity.class);
                startActivityForResult(intent, REQUEST_CODE_IMAGE_CROP);
            } catch (InvalidClassException e1) {
                e1.printStackTrace();
            }
            return ;
        }
        if(requestCode == REQUEST_VIEW_CARD && data != null) {
            boolean exit = data.getBooleanExtra(GroupInfoActivity.EXTRA_QUEIT , false);
            if(exit) {
                finish();
                return ;
            }
            boolean reload = data.getBooleanExtra(GroupInfoActivity.EXTRA_RELOAD , false);
            if(reload) {
                mThread = mChattingAdapter.setUsername(mRecipients);
                queryUIMessage();
            }
        }

		if (requestCode == SELECT_AT_SOMONE) {
			String selectUser = data
					.getStringExtra(AtSomeoneUI.EXTRA_SELECT_CONV_USER);
			if (TextUtils.isEmpty(selectUser)) {
				mChattingFooter.setAtSomebody("");
				LogUtil.d(TAG, "@ [nobody]");
				return;
			}
			LogUtil.d(TAG, "@ " + selectUser);
			ECContacts contact = ContactSqlManager.getContact(selectUser);
			if (contact == null) {
				return;
			}
			if (TextUtils.isEmpty(contact.getNickname())) {
				contact.setNickname(contact.getContactid());
			}
			mChattingFooter.setAtSomebody(contact.getNickname());
			postSetAtSome();
			return;
		}
		if (requestCode == GlobalConstant.ACTIVITY_FOR_RESULT_VIDEORECORD) {
			handleVideoRecordSend(data);
		}
		if(requestCode==REQUEST_CODE_TAKE_LOCATION){
			
			locationInfo=(LocationInfo) data.getSerializableExtra("location");
			
			handleSendLocationMessage(locationInfo);
		}

	}
    private LocationInfo locationInfo;

    
	private void handleVideoRecordSend(Intent data) {
		if (data.hasExtra("file_name")) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				fileName = extras.getString("file_name");
			}
		}

		if (data.hasExtra("file_url")) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				filePath = extras.getString("file_url");
			}
		}
		File f = new File(filePath);
		if (!f.exists()) {
			return;
		}
		handleSendVideoAttachMessage(f.length(), filePath);

	}

	/**
	 * 处理@某人
	 */
	private void postSetAtSome() {
		String atSomebody = mChattingFooter.getAtSomebody();
		if (!TextUtils.isEmpty(atSomebody)) {
			String text = mChattingFooter.getLastText();
			int someInsertPosition = mChattingFooter.getInsertPos();
			if (someInsertPosition > text.length()) {
				someInsertPosition = text.length();
			}
			String message = text.substring(0, someInsertPosition) + atSomebody
					+ (char) (8197)
					+ text.substring(someInsertPosition, text.length());
			int selectoin = 1 + someInsertPosition + atSomebody.length();
			mChattingFooter.setLastContent(message);
			mChattingFooter.setLastText(message, selectoin, false);
			mChattingFooter.setLastContent(null);
			toggleSoftInput();
		}

    }

    /**
     * 处理附件
     * @param path
     */
    private void handleAttachUrl(final String path) {
        File file = new File(path);
        if(!file.exists()) {
            return ;
        }
        final long length = file.length();
        if(length > (10 * 1048576.0F)) {
            ToastUtil.showMessage("文件大小超过限制，最大不能超过10M");
            return;
        }
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), getString(R.string.plugin_upload_attach_size_tip , FileUtils.formatFileLength(length)), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleSendFileAttachMessage(length , path);
            }});

        buildAlert.setTitle(R.string.app_tip);
        buildAlert.show();
    }

    /**
     * 处理文本发送方法事件通知
     * @param text
     */
    private void handleSendTextMessage(CharSequence text) {
        if(text == null) {
            return ;
        }
        if(text.toString().trim().length() <= 0) {
            canotSendEmptyMessage();
            return ;
        }
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        // 设置消息接收者
        msg.setTo(mRecipients);
        // 创建一个文本消息体，并添加到消息对象中
        ECTextMessageBody msgBody = new ECTextMessageBody(text.toString());
        msg.setBody(msgBody);
        try {
            // 发送消息，该函数见上
            long rowId = -1;
            if(mCustomerService) {
                rowId = CustomerServiceHelper.sendMCMessage(msg);
            } else {
                rowId = IMChattingHelper.sendECMessage(msg);
            }
            // 通知列表刷新
            msg.setId(rowId);
            notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void handleSendLocationMessage(LocationInfo locationInfo) {
        if(locationInfo == null) {
            return ;
        }
        String address =locationInfo.getAddress();
        if(TextUtils.isEmpty(address)){
        	return;
        }
        
        
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.LOCATION);
        // 设置消息接收者
        msg.setTo(mRecipients);
        // 创建一个文本消息体，并添加到消息对象中
        ECLocationMessageBody msgBody = new ECLocationMessageBody(locationInfo.getLat(), locationInfo.getLon());
        msgBody.setTitle(locationInfo.getAddress());
        msg.setBody(msgBody);
        try {
            // 发送消息，该函数见上
            long rowId = -1;
            if(mCustomerService) {
                rowId = CustomerServiceHelper.sendMCMessage(msg);
            } else {
                rowId = IMChattingHelper.sendECMessage(msg);
            }
            // 通知列表刷新
            msg.setId(rowId);
            notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 不允许发送空白消息
     */
    private void canotSendEmptyMessage() {

        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.chatting_empty_message_cant_be_sent ,R.string.dialog_btn_confim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChattingFooter.setEditTextNull();
            }
        });
        buildAlert.setTitle(R.string.app_tip);
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.show();
    }

    /**
     * 处理发送附件消息
     * @param length
     * @param pathName
     */
    private void handleSendFileAttachMessage(long length, String pathName) {
        if(TextUtils.isEmpty(pathName)) {
            return ;
        }
        // 组建一个待发送的附件ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.FILE);
        // 设置接收者
        msg.setTo(mRecipients);
        // 创建附件消息体
        ECFileMessageBody msgBody  = new ECFileMessageBody();
        // 设置附件名
        msgBody.setFileName(DemoUtils.getFilename(pathName));
        // 设置附件扩展名
        msgBody.setFileExt(DemoUtils.getExtensionName(pathName));
        // 设置附件本地路径
        msgBody.setLocalUrl(pathName);
        // 设置附件长度
        msgBody.setLength(length);
        msg.setBody(msgBody);
        
        try {
            // 调用发送API
            // 发送消息，该函数见上
            long rowId = -1;
            if(mCustomerService) {
                rowId = CustomerServiceHelper.sendMCMessage(msg);
            } else {
                rowId = IMChattingHelper.sendECMessage(msg);
            }
            // 通知列表刷新
            msg.setId(rowId);
            notifyIMessageListView(msg);
        } catch (Exception e) {
        }
    }

	private void handleSendVideoAttachMessage(long length, String pathName) {
		if (TextUtils.isEmpty(pathName)) {
			return;
		}
		// 组建一个待发送的附件ECMessage
		ECMessage msg = ECMessage.createECMessage(ECMessage.Type.VIDEO);
		// 设置接收者
		msg.setTo(mRecipients);
		// 创建附件消息体
		ECVideoMessageBody msgBody = new ECVideoMessageBody();
		// 设置附件名
		msgBody.setFileName(DemoUtils.getFilename(pathName));
		// 设置附件扩展名
		msgBody.setFileExt(DemoUtils.getExtensionName(pathName));
		// 设置附件本地路径
		msgBody.setLocalUrl(pathName);
		// 设置附件长度
		msgBody.setLength(length);
		// 扩展附件名称、对方可以用此名称界面显示
		msg.setBody(msgBody);
		try {
			// 调用发送API
			// 发送消息，该函数见上
			long rowId = -1;
			if (mCustomerService) {
				rowId = CustomerServiceHelper.sendMCMessage(msg);
			} else {
				rowId = IMChattingHelper.sendECMessage(msg);
			}
			// 通知列表刷新
			msg.setId(rowId);
			notifyIMessageListView(msg);
		} catch (Exception e) {
		}
	}

    /**
     * 处理发送图片消息
     * @param imgInfo
     */
    public void handleSendImageMessage(ImgInfo imgInfo) {
        String fileName = imgInfo.getBigImgPath();
        String fileUrl = FileAccessor.getImagePathName() + "/" + fileName;
        if(new File(fileUrl).exists()) {
            // 组建一个待发送的ECMessage
            ECMessage msg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
            // 设置接收者
            msg.setTo(mRecipients);
            // 设置附件包体（图片也是相当于附件）
            ECFileMessageBody msgBody  = new ECFileMessageBody();

			// 设置附件名
			msgBody.setFileName(fileName);
			// 设置附件扩展名
			msgBody.setFileExt(DemoUtils.getExtensionName(fileName));
			// 设置附件本地路径
			msgBody.setLocalUrl(fileUrl);
			msg.setBody(msgBody);
            try {
                long rowId;
                if(mCustomerService) {
                    rowId = CustomerServiceHelper.sendImageMessage(imgInfo ,msg);
                } else{
                    rowId = IMChattingHelper.sendImageMessage(imgInfo ,msg);
                }
                // 通知列表刷新
                msg.setId(rowId);
                notifyIMessageListView(msg);
            } catch (Exception e) {
            }finally{
//            	isFireMsg=false;//重置
            }
        }
    }

    /**
     * 将发送的消息放入消息列表
     * @param message
     */
    public  void notifyIMessageListView(ECMessage message) {
        if(!checkUserThread()) {
            return ;
        }
        mListView.setSelection(mListView.getCount() - 1);
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if(IMessageSqlManager.ACTION_GROUP_DEL.equals(intent.getAction()) && intent.hasExtra("group_id")) {
            String id = intent.getStringExtra("group_id");
            if(id != null && id.equals(mRecipients)) {
                setIsFinish(true);
                finish();
            }
        }
    }

    /**
     * 获得最后一条消息的时间
     * @return
     */
    private long getMessageAdapterLastMessageTime() {
        long lastTime = 0;
        if(mChattingAdapter != null && mChattingAdapter.getCount() >0) {
            ECMessage item = mChattingAdapter.getItem(mChattingAdapter.getCount() - 1);
            if(item != null) {
                lastTime = item.getMsgTime();
            }
        }
        return lastTime;
    }

    /**
     * <error code="SdkErrorCode.NON_GROUPMEMBER">文件上传发送者不在群组内</error>
     * <error code="SdkErrorCode.SPEAK_LIMIT_FILE">文件上传接受者被禁言</error>
     * 消息发送报告
     */
    @Override
    public void onMessageReport(ECError error ,ECMessage message) {
        if(mChattingAdapter != null) {
            mChattingAdapter.notifyChange();
        }
        if(error == null) {
            return ;
        }
        if((SdkErrorCode.SPEAK_LIMIT_FILE == error.errorCode || SdkErrorCode.SPEAK_LIMIT_TEXT == error.errorCode)) {
            // 成员被禁言
            showAlertTips(R.string.sendmsg_error_15032);
            return ;
        }
        if(( SdkErrorCode.NON_GROUPMEMBER == error.errorCode)) {
            // 文件上传发送者不在群组内
            showAlertTips(R.string.sendmsg_error_16072);
            return ;
        }

        if(SdkErrorCode.SDK_TEXT_LENGTH_LIMIT == error.errorCode) {
            // 文本长度超过限制
            showAlertTips(R.string.sendmsg_error_170001);
        }

    }

    private void showAlertTips(int message) {
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), message, R.string.dialog_btn_confim ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        buildAlert.setTitle(R.string.app_tip);
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.show();
    }

    public boolean checkUserThread() {
        ChattingListAdapter2 forceAdapter = mChattingAdapter;
        if(forceAdapter == null) {
            return false;
        }
        if(mThread <= 0 || mThread != forceAdapter.getThread()) {
            mThread = forceAdapter.setUsername(mRecipients);
        }
        forceAdapter.notifyChange();
        return true;
    }

    /**
     * 收到新的Push消息
     */
    @Override
    public void onPushMessage(String sid ,List<ECMessage> msgs) {

        if(!mRecipients.equals(sid)) {
            return ;
        }

        if(!checkUserThread()) {
            return ;
        }
        showMsgLayoutMask();
        // 当前是否正在查看消息
        if(!isViewMode)mListView.setSelection(mListView.getCount() - 1);

        setChattingSessionRead();
    }

    /**
     * 更新所有的未读消息
     */
    private void setChattingSessionRead() {
        ConversationSqlManager.setChattingSessionRead(mThread);
    }

    /**
     * 给予客户端震动提示
     */
    protected void readyOperation() {
        computationTime = -1L;
        mRecordTipsToast = null;
        playTone(ToneGenerator.TONE_PROP_BEEP, TONE_LENGTH_MS);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                stopTone();
            }
        }, TONE_LENGTH_MS);
        vibrate(50L);
    }

    private Object mToneGeneratorLock = new Object();
    // 初始化
    private void initToneGenerator() {
        AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mToneGenerator == null) {
            try {
                int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = (int) (TONE_RELATIVE_VOLUME * (streamVolume / streamMaxVolume));
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);

            } catch (RuntimeException e) {
                LogUtil.d("Exception caught while creating local tone generator: "
                        + e);
                mToneGenerator = null;
            }
        }
    }

    /**
     * 停止播放声音
     */
    public void stopTone() {
        if(mToneGenerator != null)
            mToneGenerator.stopTone();
    }

    /**
     * 播放提示音
     * @param tone
     * @param durationMs
     */
    public void playTone(int tone ,int durationMs) {
        synchronized(mToneGeneratorLock) {
            initToneGenerator();
            if (mToneGenerator == null) {
                LogUtil.d("playTone: mToneGenerator == null, tone: "+tone);
                return;
            }

            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, durationMs);
        }
    }

	/**
	 * 手机震动
	 * 
	 * @param milliseconds
	 */
	public synchronized void vibrate(long milliseconds) {
		Vibrator mVibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		if (mVibrator == null) {
			return;
		}
		mVibrator.vibrate(milliseconds);
	}

	public void showTakeStyle(final Context ctx) {
		ECListDialog dialog = new ECListDialog(ctx, R.array.take_chat_arr);
		;
		dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
			@Override
			public void onDialogItemClick(Dialog d, int position) {
				LogUtil.d("onDialogItemClick", "position " + position);

				if (position == 0) {

					handleTackPicture();

				} else if (position == 1) {
					handleVideoRecord();
				}

			}
		});
		dialog.setTitle(R.string.take_title);
		dialog.show();
	}

	private void handleTackPicture() {
		if (!FileAccessor.isExistExternalStore()) {
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = FileAccessor.getTackPicFilePath();
		if (file != null) {
			Uri uri = Uri.fromFile(file);
			if (uri != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			}
			mFilePath = file.getAbsolutePath();
		}
		startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
	}

	/**
     *
     */
    private void scrollListViewToLast() {
        if(mListView != null){
            mListView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    int lastVisiblePosition = mListView.getLastVisiblePosition();
                    int count = mListView.getCount() - 1;
                    LogUtil.v(LogUtil.TAG + "ChattingFooterEventImpl", "last visible/adapter=" + lastVisiblePosition + "/" + count);
                    /*if(lastVisiblePosition > count - 1) {
                        mListView.setSelectionFromTop(count - 1, 0);
                    } else {
                        mListView.setSelection(count);
                    }*/

                    if(mListView.getCount() <= 1) {
                        SmoothScrollToPosition.setSelection(mListView, count, true);
                        return ;
                    }
                    SmoothScrollToPosition.setSelectionFromTop(mListView, count - 1, 0, true);
                }
            }, 10L);
        }
    }

    private void handleSelectImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CODE_LOAD_IMAGE);
    }

    /**
     * 消息重发
     * @param msg
     * @param position
     */
    public void doResendMsgRetryTips(final ECMessage msg , final int position) {
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.chatting_resend_content, null, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                resendMsg(msg, position);
            }
        });
        buildAlert.setTitle(R.string.chatting_resend_title);
        buildAlert.show();
    }
    
    private void keepScreenOnState(boolean screenOn) {
    	if(mListView != null) {
			mListView.setKeepScreenOn(screenOn);
		}
    }
    
    
    
    /**
     * @param msg
     * @param position
     */
    protected void resendMsg(ECMessage msg, int position) {
        if(msg == null || position < 0 || mChattingAdapter.getItem(position) == null) {
            LogUtil.d(TAG, "ignore resend msg , msg " + msg + " , position " + position);
            return ;
        }
        ECMessage message = mChattingAdapter.getItem(position);
        message.setTo(mRecipients);
        long rowid = IMChattingHelper.reSendECMessage(message);
        if(rowid != -1) {
            mChattingAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 聊天插件功能实现
     */
    private class OnOnChattingPanelImpl implements CCPChattingFooter2.OnChattingPanelClickListener {

		@Override
		public void OnTakingPictureRequest() {
			
			showTakeStyle(getActivity());
			hideBottomPanel();
		}

		@Override
		public void OnSelectImageReuqest() {
			handleSelectImageIntent();
			hideBottomPanel();
		}

		@Override
		public void OnSelectFileRequest() {
			startActivityForResult(new Intent(getActivity(),
					FileExplorerActivity.class), 0x2a);
			hideBottomPanel();
		}

		private void hideBottomPanel() {
			mChattingFooter.hideBottomPanel();
		}

		@Override
		public void OnSelectVoiceRequest() {

			handleVoiceCall();
			hideBottomPanel();

		}

		@Override
		public void OnSelectVideoRequest() {
			handleVideoCall();
			hideBottomPanel();

		}

		@Override
		public void OnSelectFireMsg() {
			showTakeFireStyle(getActivity());
			hideBottomPanel();
		}

		@Override
		public void OnSelectLocationRequest() {//位置
			
			Intent intent =new Intent(getActivity(), LocationActivity.class);
			startActivityForResult(intent, REQUEST_CODE_TAKE_LOCATION);
			hideBottomPanel();
		}

	}
    public static  boolean isFireMsg=false;
    public void showTakeFireStyle(final Context ctx) {//阅后即焚
		ECListDialog dialog = new ECListDialog(ctx, R.array.take_chat_fire_msg);
		;
		dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
			@Override
			public void onDialogItemClick(Dialog d, int position) {
				LogUtil.d("onDialogItemClick", "position " + position);
				isFireMsg=true;
				if (position == 0) {
					handleTackPicture();

				} else if (position == 1) {
					handleSelectImageIntent();
				}

			}
		});
		dialog.setTitle(R.string.take_title);
		dialog.show();
	}

	/**
	 * 聊天功能面板（发送、录音、切换输入选项）
	 */
	private class OnChattingFooterImpl implements
			CCPChattingFooter2.OnChattingFooterLinstener {

		ChattingActivity mActivity;
		protected String mAmrPathName;
		/** 保存当前的录音状态 */
		public int mRecordState = RECORD_IDLE;
		/** 语音录制空闲 */
		public static final int RECORD_IDLE = 0;
		/** 语音录制中 */
		public static final int RECORD_ING = 1;
		/** 语音录制结束 */
		public static final int RECORD_DONE = 2;
		/** 待发的ECMessage消息 */
		private ECMessage mPreMessage;
		
		MediaPlayTools instance;
		public String bianShengFilePath;
		/** 同步锁 */
		Object mLock = new Object();
		private void changeVoiceInSDK(String appendName){
			
			
			final File file =new File(FileAccessor.getVoicePathName().getAbsolutePath()+"/"+appendName+mAmrPathName);
			bianShengFilePath=file.getAbsolutePath();
			if(file!=null&&file.exists()){
				instance.playVoice(file.getAbsolutePath(), false);
			}else {
			  final Parameters parameters =getParameters(appendName);
			  SDKCoreHelper.getECChatManager().changeVoice(parameters, new OnChangeVoiceListener() {
				@Override
				public void onChangeVoice(ECError error, Parameters para) {
					if(error.errorCode==SdkErrorCode.REQUEST_SUCCESS){
						instance.playVoice(parameters.outFileName, false);
					}else {
						file.delete();
					}
				}
			});
	
			}
		}
		
		private Parameters getParameters(String appendName){
			Parameters parameters =new Parameters();
			parameters.inFileName=FileAccessor.getVoicePathName().getAbsolutePath()+"/"+mAmrPathName;
			parameters.outFileName=FileAccessor.getVoicePathName().getAbsolutePath()+"/"+appendName+mAmrPathName;
			if("yuansheng".equals(appendName)){
				
			}else if("luoli".equals(appendName)){
				parameters.pitch=12;  //- 12  12
				parameters.tempo=1;  // -0.05 1
			}else if("dashu".equals(appendName)){
				parameters.pitch=2;
				parameters.tempo=1;
			}else if("jingsong".equals(appendName)){
				parameters.pitch=1;
				parameters.tempo=-3;
			}else if("gaoguai".equals(appendName)){
				parameters.pitch=5;
				parameters.tempo=1;
			}else if("kongling".equals(appendName)){
				parameters.pitch=1;
				parameters.tempo=-1;
			}
			return parameters;
		}
		

		public OnChattingFooterImpl(ChattingActivity ctx) {
			mActivity = ctx;
			 instance= MediaPlayTools.getInstance();
		}

		@Override
		public void OnVoiceRcdInitReuqest() {
			mAmrPathName = DemoUtils.md5(String.valueOf(System
					.currentTimeMillis())) + ".amr";
			if (FileAccessor.getVoicePathName() == null) {
				ToastUtil.showMessage("Path to file could not be created");
				mAmrPathName = null;
				return;
			}
			keepScreenOnState(true);
            if (getRecordState() != RECORD_ING) {
                setRecordState(RECORD_ING);

                // 手指按下按钮，按钮给予振动或者声音反馈
                readyOperation();
                // 显示录音提示框
                mChattingFooter.showVoiceRecordWindow(findViewById(R.id.chatting_bg_ll).getHeight() - mChattingFooter.getHeight());

                final ECChatManager chatManager = SDKCoreHelper.getECChatManager();
                if(chatManager == null) {
                    return ;
                }
                mVoiceHandler.post(new Runnable() {

                    @SuppressWarnings("deprecation")
					@Override
                    public void run() {
                        try {
                            ECMessage message = ECMessage.createECMessage(ECMessage.Type.VOICE);
                            message.setTo(mRecipients);
                            ECVoiceMessageBody messageBody = new ECVoiceMessageBody(new File(FileAccessor.getVoicePathName() ,mAmrPathName ), 0);
                            message.setBody(messageBody);
                            mPreMessage = message;
                            // 仅录制语音消息，录制完成后需要调用发送接口发送消息
                            chatManager.startVoiceRecording(messageBody, new ECChatManager.OnRecordTimeoutListener() {
                                @Override
                                public void onRecordingTimeOut(long duration) {
									LogUtil.d(TAG , "onRecordingTimeOut");
                                    // 如果语音录制超过最大60s长度,则发送
                                    
                                    if(mChattingFooter.isChangeVoice){
                                    	OnVoiceRcdStopRequest(false);
                                    	mChattingFooter.showBianShengView();
                                    }else {
                                    	doProcesOperationRecordOver(false,true);
                                    }
                                    
                                    
                                }

                                @Override
                                public void onRecordingAmplitude(
                                        double amplitude) {
                                    // 显示声音振幅
                                    if(mChattingFooter != null && getRecordState()  == RECORD_ING) {
                                        mChattingFooter.showVoiceRecording();
                                        mChattingFooter.displayAmplitude(amplitude);
                                    }
                                }

                            });
                        } catch (Exception e) {
                            LogUtil.e(TAG , "请检查录音权限是否被禁止");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void OnVoiceRcdStartRequest() {
            // SDK完成初始化底层音频设备、开始采集音频数据
            mHandler.removeMessages(WHAT_ON_COMPUTATION_TIME);
            mHandler.sendEmptyMessageDelayed(WHAT_ON_COMPUTATION_TIME, TONE_LENGTH_MS);
        }

        @Override
        public void OnVoiceRcdCancelRequest() {
            handleMotionEventActionUp(true,false);
        }

        @Override
        public void OnVoiceRcdStopRequest(boolean isSend) {
            handleMotionEventActionUp(false,isSend);
        }

        @Override
        public void OnSendTextMessageRequest(CharSequence text) {
            if(text != null && text.toString().trim().startsWith("starttest://")) {

                handleTest(text.toString().substring("starttest://".length()));
                return ;
            } else if (text != null && text.toString().trim().startsWith("endtest://")) {
                debugeTest = false;
                return ;
            }
            handleSendTextMessage(text);
        }

        @Override
        public void OnUpdateTextOutBoxRequest(CharSequence text) {

        }

        @Override
        public void OnSendCustomEmojiRequest(int emojiid, String emojiName) {

        }

        @Override
        public void OnEmojiDelRequest() {

        }

        @Override
        public void OnInEditMode() {
            scrollListViewToLast();
        }

        @Override
        public void onPause() {
            stopPlayVoice();
        }

        @Override
        public void onResume() {

        }

        @Override
        public void release() {
            mActivity = null;
            mPreMessage = null;
            bianShengFilePath=null;
        }

        /**
         * 处理Button 按钮按下抬起事件
         * @param doCancle 是否取消或者停止录制
         */
        private void handleMotionEventActionUp(final boolean doCancle,boolean isSend) {
        	keepScreenOnState(false);
            if(getRecordState()  == RECORD_ING) {
                doVoiceRecordAction(doCancle,isSend);
            }
        }

        /**
         * 处理语音录制结束事件
         * @param doCancle 是否取消或者停止录制
         */
        private void doVoiceRecordAction(boolean doCancle,final boolean isSend) {
            final boolean cancleVoice = doCancle;
            
            if(mChatManager != null) {
                mVoiceHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // 停止或者取消普通模式语音
                        LogUtil.d(LogUtil.getLogUtilsTag(getClass()), "handleMotionEventActionUp stop normal record");
                        mChatManager.stopVoiceRecording(new ECChatManager.OnStopVoiceRecordingListener() {
                            @Override
                            public void onRecordingComplete() {
								LogUtil.d(TAG , "onRecordingComplete");
                                doProcesOperationRecordOver(cancleVoice,isSend);
                            }
                        });
                    }
                });
            }


        }
       
        

        /**
         * 处理录音结束消息是否发送逻辑
         * @param cancle 是否取消发送
         */
        protected void doProcesOperationRecordOver(boolean cancle,boolean isSend) {
            if(getRecordState() == RECORD_ING) {
                // 当前是否有正在录音的操作
                // 定义标志位判断当前所录制的语音文件是否符合发送条件
                // 只有当录制的语音文件的长度超过1s才进行发送语音
                boolean isVoiceToShort = false;
                File amrPathFile = new File(FileAccessor.getVoicePathName() ,mAmrPathName);
                if(amrPathFile.exists()) {
                    mVoiceRecodeTime = DemoUtils.calculateVoiceTime(amrPathFile.getAbsolutePath());
                    if(!isRecordAndSend) {
                        if (mVoiceRecodeTime * 1000 < MIX_TIME) {
                            isVoiceToShort = true;
                        }
                    }
                } else {
                    isVoiceToShort = true;
                }
                // 设置录音空闲状态
                setRecordState(RECORD_IDLE);
                if(mChattingFooter != null ) {
                    if (isVoiceToShort && !cancle) {
                        // 提示语音文件长度太短
                        mChattingFooter.tooShortPopuWindow();
                        return;
                    }
                    
                    if(!isSend&&mChattingFooter.isChangeVoice&&!cancle){
                    	mChattingFooter.showBianShengView();
                    }
                    // 关闭语音录制对话框
                    mChattingFooter.dismissPopuWindow();
                }
                
                
                

                if(!cancle && mPreMessage != null&&isSend) {
                    if(!isRecordAndSend) {
                        // 如果当前的录音模式为非Chunk模式
                        try {
                            ECVoiceMessageBody body = (ECVoiceMessageBody) mPreMessage.getBody();
                            body.setDuration(mVoiceRecodeTime);
                            long rowId;
                            if(mCustomerService) {
                                rowId = CustomerServiceHelper.sendMCMessage(mPreMessage);
                            } else {
                                rowId = IMChattingHelper.sendECMessage(mPreMessage);
                            }
                            mPreMessage.setId(rowId);
                            notifyIMessageListView(mPreMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return ;
                }

                // 删除语音文件
                amrPathFile.deleteOnExit();
                // 重置语音时间长度统计
                mVoiceRecodeTime = 0;
            }
        }

        public int getRecordState() {
            synchronized (mLock) {
                return mRecordState;
            }
        }

        public void setRecordState(int state) {
            synchronized (mLock) {
                this.mRecordState = state;
            }
        }

		@Override
		public void onVoiceChangeRequest(int position) {
			handlerChangeVoice(position);
			
			
		}

		private void handlerChangeVoice(int position) {

		
		switch (position) {
		case 0:
			if(instance!=null){
			    instance.playVoice(FileAccessor.getVoicePathName()+"/"+mAmrPathName, false);
			}
			break;
		case 1:
			changeVoiceInSDK("luoli");
			
			break;
		case 2:
			
			changeVoiceInSDK("dashu");
			break;
		case 3:
			
			changeVoiceInSDK("jingsong");
			break;
		case 4:
			changeVoiceInSDK("gaoguai");
			
			break;
		case 5:
			changeVoiceInSDK("kongling");
			
			break;

		default:
			break;
		}
		
	}

		public void sendChangeVoiceMsg(boolean isSendYuanSheng) {
			if( mPreMessage != null) {
                if(!isRecordAndSend) {
                    // 如果当前的录音模式为非Chunk模式
                    try {
                        ECVoiceMessageBody body = (ECVoiceMessageBody) mPreMessage.getBody();
                        
                        if(isSendYuanSheng){
                        	body.setDuration(DemoUtils.calculateVoiceTime(FileAccessor.getVoicePathName()+"/"+mAmrPathName));
                            body.setLocalUrl(FileAccessor.getVoicePathName()+"/"+mAmrPathName);
                        }else {
                        	body.setDuration(DemoUtils.calculateVoiceTime(bianShengFilePath));
                            body.setLocalUrl(bianShengFilePath);
                        }
                        
                        
                        long rowId;
                        if(mCustomerService) {
                            rowId = CustomerServiceHelper.sendMCMessage(mPreMessage);
                        } else {
                            rowId = IMChattingHelper.sendECMessage(mPreMessage);
                        }
                        mPreMessage.setId(rowId);
                        notifyIMessageListView(mPreMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return ;
            }
		}

		@Override
		public void stopVoicePlay() {
			// TODO Auto-generated method stub
			if(instance!=null&&instance.isPlaying()){
				instance.stop();
			}
			
		}

    }

    private void stopPlayVoice() {
        if(mChattingAdapter != null)  {
            // 停止播放语音
            mChattingAdapter.onPause();
            mChattingAdapter.notifyDataSetChanged();
        }
    }

	
	
	
	

	public void handleVideoCall() {
		ECContacts contact = ContactSqlManager.getContact(mRecipients);

		if (contact == null) {
			return;
		}
		CCPAppManager.callVoIPAction(getActivity(), CallType.VIDEO,
				contact.getNickname(), contact.getContactid(),false);

	}

	public void handleVoiceCall() {
		ECContacts contact = ContactSqlManager.getContact(mRecipients);
		if (contact == null) {
			return;
		}
		CCPAppManager.callVoIPAction(getActivity(), CallType.VOICE,
				contact.getNickname(), contact.getContactid(),false);

	}

	private void handleVideoRecord() {

		Intent intent = new Intent();
		intent.setClass(getActivity(), VideoRecordActivity.class);
		startActivityForResult(intent,
				GlobalConstant.ACTIVITY_FOR_RESULT_VIDEORECORD);
	}

	public class ChattingAsyncTask extends ECAsyncTask {

        /**
         * @param context
         */
        public ChattingAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Object doInBackground(Object... params) {
            ImgInfo createImgInfo ;
            if(((String)params[0]).endsWith(".gif")) {
                createImgInfo = ImgInfoSqlManager.getInstance().createGIFImgInfo((String)params[0]);
            } else {
                createImgInfo = ImgInfoSqlManager.getInstance().createImgInfo((String)params[0]);
            }
            return createImgInfo;
        }


        protected void onPostExecute(Object result) {
            if(result instanceof ImgInfo) {
                ImgInfo imgInfo = (ImgInfo) result;
                handleSendImageMessage(imgInfo);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                setIsFinish(true);
                hideSoftKeyboard();
                finish();
                break;
            case R.id.btn_right:
                if(!isPeerChat()) {
                    // 如果是点对点聊天
                    ECContacts contact = ContactSqlManager.getContact(mRecipients);
                    Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                    intent.putExtra(ContactDetailActivity.RAW_ID, contact.getId());
                    startActivityForResult(intent, REQUEST_VIEW_CARD);

                    return ;
                }
                // 群组聊天室
                Intent intent = new Intent(getActivity(), GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.GROUP_ID, mRecipients);
                startActivityForResult(intent, REQUEST_VIEW_CARD);
                break;
            case R.id.btn_middle:
                if (mListView != null) {
                    getTopBarView().post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setSelection(mChattingAdapter.getCount());
                        }
                    });
                }
                break;
            default:
                break;
        }
    }



    private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener = new OnRefreshAdapterDataListener() {

        @Override
        public void refreshData() {//刷新数据
            if(getActivity() == null || getActivity().isFinishing()) {
                return ;
            }
            int size = mChattingAdapter.increaseCount();
            mChattingAdapter.checkTimeShower();
            mChattingAdapter.notifyChange();
            int count = mChattingAdapter.getCount() - size;
            LogUtil.d(TAG, "onRefreshing history msg count " + count);
            mListView.setSelectionFromTop(size + 1, mListViewHeadView.getHeight() + mECPullDownView.getTopViewHeight());
        }

    };

    private OnListViewBottomListener mOnListViewBottomListener = new OnListViewBottomListener() {

        @Override
        public boolean getIsListViewToBottom() {
            View lastChildAt = mListView.getChildAt(mListView.getChildCount() - 1) ;
            if(lastChildAt == null) {
                return false;
            }
            if((lastChildAt.getBottom() <= mListView.getHeight())
                    && mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1) {
                return true;
            }
            return false;
        }
    };

    private OnListViewTopListener mOnListViewTopListener = new OnListViewTopListener() {

        @Override
        public boolean getIsListViewToTop() {
            View topChildAt =  mListView.getChildAt(mListView.getFirstVisiblePosition());
            return ((topChildAt != null) && (topChildAt.getTop() == 0));
        }
    };


    private AdapterView.OnItemLongClickListener mOnItemLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final int itemPosition = position;
            if(mChattingAdapter != null) {
                int headerViewsCount = mListView.getHeaderViewsCount();
                if (itemPosition < headerViewsCount) {
                    return false;
                }
                int _position = itemPosition - headerViewsCount;

                if (mChattingAdapter == null || mChattingAdapter.getItem(_position) == null) {
                    return false;
                }
                ECMessage item = mChattingAdapter.getItem(_position);
                String title = mUsername;
                if(item.getDirection() == ECMessage.Direction.SEND) {
                    title = CCPAppManager.getClientUser().getUserName();
                }
                ECListDialog dialog;
                if(item.getType() == ECMessage.Type.TXT) {
                    // 文本有复制功能
                    dialog = new ECListDialog(getActivity() , R.array.chat_menu);
                } else {
                    dialog = new ECListDialog(getActivity() , new String[]{getString(R.string.menu_del)});
                }
                dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {
                        handleContentMenuClick(itemPosition ,position);
                    }
                });
                dialog.setTitle(title);
                dialog.show();
                return true;
            }
            return false;
        }
    };


    private Boolean handleContentMenuClick(int convresion ,int position) {
        if(mChattingAdapter != null) {
            int headerViewsCount = mListView.getHeaderViewsCount();
            if (convresion < headerViewsCount) {
                return false;
            }
            int _position = convresion - headerViewsCount;

            if (mChattingAdapter == null || mChattingAdapter.getItem(_position) == null) {
                return false;
            }
            ECMessage msg = mChattingAdapter.getItem(_position);
            switch (position) {
                case 0: // 删除
                    doDelMsgTips(msg , _position);

                    break;
                case 1: // 复制
                    try {
                        if(msg.getType() == ECMessage.Type.TXT) {
                            ECTextMessageBody body = (ECTextMessageBody) msg.getBody();
                            ClipboardUtils.copyFromEdit(getActivity(), getString(R.string.app_pic), body.getMessage());
                            ToastUtil.showMessage(R.string.app_copy_ok);
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "clip.setText error ");
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     *
     * @param msg
     * @param position
     */
    public void doDelMsgTips(final ECMessage msg , final int position) {
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.app_delete_tips, null, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandlerHelper.postRunnOnThead(new Runnable() {
                    @Override
                    public void run() {
                        IMessageSqlManager.delSingleMsg(msg.getMsgId());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChattingAdapter.notifyChange();
                            }
                        });
                    }
                });
            }
        });
        buildAlert.setTitle(R.string.chatting_resend_title);
        buildAlert.show();
    }

    public static class SmoothScrollToPosition {
        public static void setSelectionFromTop(ListView listview , int position, int y , boolean smooth) {
            if(listview == null) {
                return ;
            }
            LogUtil.i(TAG , "setSelectionFromTop position " + position + " smooth " + smooth);
            listview.setItemChecked(position, true);
            listview.setSelectionFromTop(position , y);
        }


        public static void setSelection(ListView listview , int position, boolean smooth) {
            if(listview == null) {
                return ;
            }
            LogUtil.i(TAG , "setSelection position " + position + " smooth " + smooth);
            listview.setItemChecked(position, true);
            listview.setSelection(position);
        }
    }



    /****************************在线客服****************************/
    @Override
    public void onServiceStart(String event) {
        ToastUtil.showMessage("开启咨询[" + event + "]");
    }

    @Override
    public void onServiceFinish(String even) {

    }

    @Override
    public void onError(ECError error) {

    }
    /****************************在线客服****************************/




    /*******************************************DEBUGE START*********************************************/
    private void handleTest(final String count) {
        if(TextUtils.isEmpty(count) || count.trim().length() == 0) {
            ToastUtil.showMessage("测试协议失败，测试消息条数必须大于0");
            return ;
        }
        final String text = getString(R.string.app_test_message);
        // final String text = getTestText();
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), "是否开始发送"+count+"条测试消息\n["+text+"]？",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandlerHelper.postRunnOnThead(new Runnable() {
                    @Override
                    public void run() {
                        debugeTest = true;
                        doStartTest(count, text);
                    }
                });
                mChattingFooter.setEditText("endtest://");
            }
        });
        buildAlert.setTitle("开发模式");
        buildAlert.show();

    }

    private boolean debugeTest = false;
    private void doStartTest(String count , final String text) {
        try {
            final int num = Integer.parseInt(count);
            ECHandlerHelper handlerHelper = new ECHandlerHelper();
            handlerHelper.postRunnOnThead(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showMessage("开始测试.");
                    for (int i = 0; i < num && debugeTest ; i++) {
                        try {
                            ToastUtil.showMessage("正在发送第[" + (i+1) + "]条测试消息");
                            final String pretext = "[第" + (i+1) + "条]\n" + text;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleSendTextMessage(pretext);
                                }
                            });
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mChattingFooter.setEditTextNull();
                            ToastUtil.showMessage("测试结束...");
                        }
                    });
                }
            });
        } catch (Exception e) {}
    }

    

    /*******************************************DEBUGE END*********************************************/


    public interface OnChattingAttachListener {
        void onChattingAttach();
    }
    
    private ECProgressDialog mPostingdialog;
    
    public  void showProcessDialog() {
		if (mPostingdialog != null && mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog = new ECProgressDialog(getActivity(),
				R.string.downloading);
		mPostingdialog.show();
	}

	

	/**
	 * 关闭对话框
	 */
	public void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
		
		ToastUtil.showMessage("下载完成,再次点击即可播放");
	}
    
    
    
    
}
