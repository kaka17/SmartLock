package com.sht.smartlock.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.RSAUtils;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.MyNetworkHelper;
import com.sht.smartlock.model.UserLoginInfo;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.utils.FileAccessor;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.core.ClientUser;
import com.sht.smartlock.phone.ui.CallPhoneActivity;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.voip.VideoActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallHelper;
import com.sht.smartlock.ui.activity.Login2Activity;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import org.json.JSONObject;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class LoginActivity extends BaseActivity {
    private ImageView login_image_logo;
    private Button btn_regist;
    private TextView tvTitlePanel;
    private ImageView btn_cancle;
    private Button btn_login;
    private Button btn_gorgetpassword;
    private EditText edit_username;
    private EditText edit_userpwd;
    private HttpClient httpClient;
    private String nameString;
    private String pwdString;
    private String decryptStr;

    /** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
//    private static String SHARE_LOGIN_TAG = "SHARE_LOGIN_TAG";
//    public static String SHARE_USER_ACCOUNT = "SHARE_USER_ACCOUNT";
//    public static String SHARE_USERPWD = "SHARE_USERPWD";

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        initdata();
        setClickListener();
        shareData();
        //
        if (AppContext.getShareUserSessinid() == null) {
            //账号没有登录
        } else {
            EMChatManager.getInstance().logout();//此方法为同步方法
        }
        Button btnDire=(Button)findViewById(R.id.btnDire);
        btnDire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callVoIPAction(LoginActivity.this, ECVoIPCallManager.CallType.DIRECT, "abc", "13543268651", false);
                startActivity(new Intent(getApplicationContext(), CallPhoneActivity.class));

            }
        });

    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private void findviewbyid(){
        login_image_logo = (ImageView)findViewById(R.id.login_image_logo);
        btn_regist = (Button)findViewById(R.id.btn_regist);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_gorgetpassword = (Button)findViewById(R.id.btn_gorgetpassword);
        edit_username = (EditText)findViewById(R.id.edit_username);
        edit_userpwd = (EditText)findViewById(R.id.edit_userpwd);



//        BaseApplication.toast("aaa =" + HttpClient.BASEPATH);
//        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
//        BaseApplication.toast(share.getString(SHARE_LOGIN_USERNAME, ""));
    }

    private void initdata(){
        tvTitlePanel.setText("登录");
    }

    private void setClickListener(){
        btn_regist.setOnClickListener(clickListener);
        btn_cancle.setOnClickListener(clickListener);
        btn_login.setOnClickListener(clickListener);
        btn_gorgetpassword.setOnClickListener(clickListener);
    }

    private void shareData(){
        if(! (AppContext.getProperty(Config.SHARE_USER_ACCOUNT)==null)){
            edit_username.setText(AppContext.getProperty(Config.SHARE_USER_ACCOUNT));
        }
        if(!(AppContext.getProperty(Config.SHARE_USERPWD)==null)){
            String password = AppContext.getProperty(Config.SHARE_USERPWD);
             //解密操作
            try
            {
                // 从字符串中得到私钥
                PrivateKey privateKey = RSAUtils.loadPrivateKey(Config.PRIVATE_KEY);
                // 从文件中得到私钥
//            InputStream inPrivate = getResources().getAssets().open("pkcs8_rsa_private_key.pem");
//            PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
                // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
                byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(password), privateKey);
                decryptStr = new String(decryptByte);
//            et3.setText(decryptStr);
            //    BaseApplication.toast("解密成功"+decryptStr);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            edit_userpwd.setText(decryptStr);
        }

    }

    /** 清除密码 */
    private void clearSharePassword() {

        AppContext.setProperty(Config.SHARE_USERPWD,"");
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_login:
                    if (!MyNetworkHelper.isNetworkAvailable(LoginActivity.this)) {
                        BaseApplication.toast("当前网络不可用，请检查网络连接情况");
                        return;
                    }
                    nameString = edit_username.getText().toString();
                    pwdString = edit_userpwd.getText().toString();
                    if(nameString.equals("")){
                        BaseApplication.toast("请输入用户名！");
                        return;
                    }
                    if(pwdString.equals("")){
                        BaseApplication.toast("请输入密码！");
                        return;
                    }
                    HttpClient.instance().login(nameString, pwdString, new NetworkRequestLoginResult());
                    ProgressDialog.show(LoginActivity.this, "正在登录...请稍候...");
                    break;
                case R.id.btn_regist:
                    startActivity(new Intent(LoginActivity.this,Login2Activity.class));
                   // AppManager.getAppManager().finishActivity();
//                    callVoIPAction(LoginActivity.this, ECVoIPCallManager.CallType.VOICE, "张金杰","13543268651", true);
                   finish();
                    break;
                case R.id.btn_gorgetpassword:
                    startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                    finish();
                    break;
                case R.id.btn_cancle:
                   // AppManager.getAppManager().finishActivity();\
                    finish();
                    break;
            }
        }
    };

    /**
     * 根据呼叫类型通话
     * @param ctx 上下文
     * @param callType 呼叫类型
     * @param nickname 昵称
     * @param contactId 号码
     */
    public static void callVoIPAction(Context ctx , ECVoIPCallManager.CallType callType ,String nickname, String contactId,boolean flag) {
        // VoIP呼叫
        Intent callAction = new Intent(ctx , VoIPCallActivity.class);
        if(callType == ECVoIPCallManager.CallType.VIDEO) {
            callAction = new Intent(ctx , VideoActivity.class);
            VoIPCallHelper.mHandlerVideoCall = true;
        } else {
            VoIPCallHelper.mHandlerVideoCall = false;
        }
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NAME , nickname);
        callAction.putExtra(VoIPCallActivity.EXTRA_CALL_NUMBER , contactId);
        callAction.putExtra(ECDevice.CALLTYPE , callType);
        callAction.putExtra(VoIPCallActivity.EXTRA_OUTGOING_CALL , true);

        if(flag){
            callAction.putExtra(VoIPCallActivity.ACTION_CALLBACK_CALL, true);
        }
        ctx.startActivity(callAction);
    }




    private int issuccess;
    String afterencrypt;
    private UserLoginInfo userLogin;
    class  NetworkRequestLoginResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            LogUtil.log("message="+message.toString());
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
           Log.e("reb", responseBean.toString());
            if (responseBean.isFailure()) {
                clearSharePassword();
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    LogUtil.log(jsonObject.getString("error"));
                    if(!jsonObject.getString("error").equals("")||jsonObject.getString("error").equals("null")){
                        JSONObject jsonObjecterror = new JSONObject(jsonObject.getString("error").toString());
                        BaseApplication.toast(jsonObjecterror.getString("message"));
                    }

                }catch (Exception e){
                LogUtil.log(e.toString());
                }
//                BaseApplication.toast(responseBean.toString());
                return;
            }
//            LogUtil.log("responseBean="+responseBean);
            userLogin = responseBean.getData(UserLoginInfo.class);
             //加密操作
            try
            {
                // 从字符串中得到公钥
                 PublicKey publicKey = RSAUtils.loadPublicKey(Config.PUCLIC_KEY);
                // 从文件中得到公钥
//                InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
//                PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
                // 加密
                byte[] encryptByte = RSAUtils.encryptData(pwdString.getBytes(), publicKey);
                // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
                afterencrypt = Base64Utils.encode(encryptByte);
             //   BaseApplication.toast(afterencrypt);
//                et2.setText(afterencrypt);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

           AppContext.setProperty(Config.SHARE_USER_ACCOUNT, edit_username.getText().toString());
            AppContext.setProperty(Config.SHARE_USERSESSIONID, userLogin.getSessionid());
            AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
           // share.edit().putString(Config.SHARE_USERPWD,afterencrypt).commit();
            AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(userLogin.isSet_unlock_pwd()));
            AppContext.setProperty(Config.KEY_USER_NICKNAME,userLogin.getNick_name());
            AppContext.setProperty(Config.KEY_USER_NAME,userLogin.getName());
           // AppManager.getAppManager().finishActivity();
            //环信账号
            AppContext.setProperty(Config.EMID,userLogin.getEmid());
            AppContext.setProperty(Config.MYHUANXINPWD, userLogin.getEm_passwd());
            //先登录对应的环信账号
            loginHuanXin(userLogin.getEmid(), userLogin.getEm_passwd());
            rly(userLogin.getVoip_account(), userLogin.getVoip_pwd());
//            loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
        }
    }

    //环信登录
    private void loginHuanXin(final String userName, final String pwd) {

        if (userName==null||userName.equals("null")){
//            AppContext.toast("聊天账号账号错误");
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//            loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
            return;
        }
        if (pwd==null||pwd.equals("null")){
//            AppContext.toast("聊天账号密码错误");
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//            loginVOIP(userLogin.getVoip_account(),userLogin.getVoip_pwd());
            return;
        }
        ProgressDialog.show(LoginActivity.this, R.string.Is_landing);
        //用户名 和密码相同
        EMChatManager.getInstance().login(userName, pwd, new EMCallBack() {//回调


            @Override
            public void onSuccess() {
//                AppContext.toast("黄鑫登录唱歌");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        ProgressDialog.disMiss();
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        //去获取所有会话, 获取单个会话
                        EMChatManager.getInstance().getConversation(userName);
//                        init();
                        Log.d("main", "登陆聊天服务器成功！");
                        // 登陆成功，保存用户名密码
                        AppContext.getInstance().setUserName(userName);
                        AppContext.getInstance().setPassword(pwd);

//                        loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        finish();
//                        rly(userLogin.getVoip_account(),userLogin.getVoip_pwd());
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {

            }


            @Override
            public void onError(int code, String message) {
                Log.e("main", "登陆聊天服务器失败！" + message);
//                AppContext.toast("黄鑫登录异常");
                ProgressDialog.disMiss();
//                loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
                finish();
            }
        });
    }

    /**
     * --------------------------------------------------------------------------------------------------------------------
     *
     */

    @Override
    protected void onResume() {
        super.onResume();
        initConfig();
    }

    private void initConfig() {

        String appkey = FileAccessor.getAppKey();
        String token = FileAccessor.getAppToken();
//        appkeyEt.setText(appkey);
//        tokenEt.setText(token);

        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(token)) {
//            signBtn.setEnabled(false);
            ToastUtil.showMessage(R.string.app_server_config_error_tips);
        }
    }


    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
    public void rly(String yuntxUser,String yuntxPassWord){
        String appKey = "8a48b5515438446d01543847d1480001";
        String token = "30dbe749b04c39f3290a53c0c6ed1dbc";


        ClientUser clientUser = new ClientUser(yuntxUser);
        clientUser.setAppKey(appKey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(yuntxPassWord);
        CCPAppManager.setClientUser(clientUser);

        SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);

//        AppContext.toast("登录成功！！！！！！！");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x2a) {
            doLauncherAction();
        }
    }

    private void doLauncherAction() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("launcher_from", 1);
            // 注册成功跳转
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginVOIP(final String yuntxUser,final String yuntxPassWord) {

        if (!ECDevice.isInitialized()) {
            ECDevice.initial(this, new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    // SDK已经初始化成功
                    System.out.println("paramsss: initial success");
                    ECInitParams params = ECInitParams.createParams();
                    params.setUserid(yuntxUser);
                    params.setPwd(yuntxPassWord);
                    params.setAppKey("8a48b5515438446d01543847d1480001");
                    params.setToken("30dbe749b04c39f3290a53c0c6ed1dbc");
                    // 设置登陆验证模式（是否验证密码）PASSWORD_AUTH-密码登录方式
                    params.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
                    // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
                    // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
                    // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
                    params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
                    ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
                        @Override
                        public void onConnect() {

                        }

                        @Override
                        public void onDisconnect(ECError ecError) {

                        }

                        @Override
                        public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                            if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
                                if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                                    System.out.println("paramsss:login 账号异地登陆");
                                    //
                                    AppContext.toast("yichang");
                                } else {
                                    System.out.println("paramsss:login 连接状态失败");
                                    AppContext.toast("失败");
                                    //连接状态失败
                                }
                                return;
                            } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                                // 登陆成功
                                System.out.println("paramsss:login 登陆成功");
//                               AppContext.toast("唱歌");
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));

                            }

                        }
                    });
                    ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
                        @Override
                        public void OnReceivedMessage(ECMessage ecMessage) {
                            // 收到新消息

                        }

                        @Override
                        public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {

                        }

                        @Override
                        public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {
// 收到群组通知消息（有人加入、退出...）
                            // 可以根据ECGroupNoticeMessage.ECGroupMessageType类型区分不同消息类型

                        }

                        @Override
                        public void onOfflineMessageCount(int i) {


                        }

                        @Override
                        public int onGetOfflineMessage() {
                            return 0;

                        }

                        @Override
                        public void onReceiveOfflineMessage(List<ECMessage> list) {

                        }

                        @Override
                        public void onReceiveOfflineMessageCompletion() {

                        }

                        @Override
                        public void onServicePersonVersion(int i) {

                        }

                        @Override
                        public void onReceiveDeskMessage(ECMessage ecMessage) {

                        }

                        @Override
                        public void onSoftVersion(String s, int i) {

                        }
                    });
                    ECVoIPCallManager callInterface = ECDevice.getECVoIPCallManager();
                    if(callInterface != null) {
                        callInterface.setOnVoIPCallListener(new ECVoIPCallManager.OnVoIPListener() {
                            @Override
                            public void onVideoRatioChanged(VideoRatio videoRatio) {

                            }

                            @Override
                            public void onSwitchCallMediaTypeRequest(String s, ECVoIPCallManager.CallType callType) {

                            }

                            @Override
                            public void onSwitchCallMediaTypeResponse(String s, ECVoIPCallManager.CallType callType) {

                            }

                            @Override
                            public void onDtmfReceived(String s, char c) {

                            }

                            @Override
                            public void onCallEvents(ECVoIPCallManager.VoIPCall voipCall) {
                                // 处理呼叫事件回调
                                if(voipCall == null) {
                                    Log.e("SDKCoreHelper", "handle call event error , voipCall null");
                                    return ;
                                }
                                // 根据不同的事件通知类型来处理不同的业务
                                ECVoIPCallManager.ECCallState callState = voipCall.callState;
                                switch (callState) {
                                    case ECCALL_PROCEEDING:
                                        // 正在连接服务器处理呼叫请求
                                        break;
                                    case ECCALL_ALERTING:
                                        // 呼叫到达对方客户端，对方正在振铃
                                        break;
                                    case ECCALL_ANSWERED:
                                        // 对方接听本次呼叫
                                        break;
                                    case ECCALL_FAILED:
                                        // 本次呼叫失败，根据失败原因播放提示音
                                        break;
                                    case ECCALL_RELEASED:
                                        // 通话释放[完成一次呼叫]
                                        break;
                                    default:
                                        Log.e("SDKCoreHelper", "handle call event error , callState " + callState);
                                        break;
                                }
                            }
                        });
                    }
                    if(params.validate()) {
                        // 判断注册参数是否正确
                        ECDevice.login(params);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    System.out.println("paramsss: initial fail " + exception);

                    // SDK 初始化失败,可能有如下原因造成
                    // 1、可能SDK已经处于初始化状态
                    // 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
                    //    或者未配置服务属性android:exported="false";
                    // 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
                    //    Android Build.VERSION.SDK_INT 以及以上版本）
                }
            });
        }else{
            ECInitParams params = ECInitParams.createParams();
            params.setUserid(yuntxUser);
            params.setPwd(yuntxPassWord);
            params.setAppKey("8a48b5515438446d01543847d1480001");
            params.setToken("30dbe749b04c39f3290a53c0c6ed1dbc");
            // 设置登陆验证模式（是否验证密码）PASSWORD_AUTH-密码登录方式
            params.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
            // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
            // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
            // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
            params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
            ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
                @Override
                public void onConnect() {

                }

                @Override
                public void onDisconnect(ECError ecError) {

                }

                @Override
                public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                    if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
                        if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                            System.out.println("paramsss:login 账号异地登陆");
                            //
                            AppContext.toast("yichang");
                        } else {
                            System.out.println("paramsss:login 连接状态失败");
                            AppContext.toast("失败");
                            //连接状态失败
                        }
                        return;
                    } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                        // 登陆成功
                        System.out.println("paramsss:login 登陆成功");
//                               AppContext.toast("唱歌");
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));

                    }

                }
            });
        }



    }


}
