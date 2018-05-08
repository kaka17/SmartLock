package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.mine.ForgetPasswordActivity;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.db.UserDao;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.uidemo.Constant;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.yuntongxun.ecsdk.ECInitParams;

import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginByNameActivity extends BaseActivity implements View.OnClickListener{

    private EditText etName,etPwd;
    private ImageView ivEye,ivLogin;
    private TextView tvRegister,tvForGetPwd;
    String afterencrypt;
    private UserLoginInfo userLogin;
    private boolean isEye=true;
    private Button bnRegist;
    private TextView tvGotoLogin;
    private ImageView ivScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_by_name);
        onBack();
        initView();
        setOnClickLister();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_by_name;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void initView(){
        etName = (EditText) findViewById(R.id.etName);
        etPwd = (EditText) findViewById(R.id.etPwd);
        ivEye = (ImageView) findViewById(R.id.ivEye);
        ivLogin = (ImageView) findViewById(R.id.ivLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvForGetPwd = (TextView) findViewById(R.id.tvForGetPwd);
        bnRegist = (Button) findViewById(R.id.bnRegist);
        tvGotoLogin = (TextView) findViewById(R.id.tvGotoLogin);
        ivScan = (ImageView) findViewById(R.id.ivScan);

    }

    private void setOnClickLister(){
        ivEye.setOnClickListener(this);
        ivLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForGetPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivEye:
                if (isEye){
                    isEye=false;
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivEye.setImageResource(R.drawable.btn_eye_close);
                }else {
                    isEye=true;
                    etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivEye.setImageResource(R.drawable.btn_eye_open);
                }
                break;
            case R.id.ivLogin:
                if (!MyNetworkHelper.isNetworkAvailable(LoginByNameActivity.this)) {
                    BaseApplication.toast("当前网络不可用，请检查网络连接情况");
                    return;
                }
                String name = etName.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (name.isEmpty()||pwd.isEmpty()){
                    AppContext.toast("请输入账号和密码");
                    return;
                }
                initLogin(name,pwd);
                break;
            case R.id.tvRegister:
                startActivity(new Intent(getApplicationContext(), RegistByNameActivity.class));
//                finish();
                break;
            case R.id.tvForGetPwd:
                startActivity(new Intent(LoginByNameActivity.this, ForgetPasswordActivity.class));
                finish();
                break;

        }
    }
    private void initLogin(final String nameString, final String pwdString){
        HttpClient.instance().login(nameString, pwdString, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(LoginByNameActivity.this,"正在登录");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
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

                AppContext.setProperty(Config.SHARE_USER_ACCOUNT, userLogin.getUser_name());
                AppContext.setProperty(Config.SHARE_USERSESSIONID, userLogin.getSessionid());
                AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
                AppContext.setProperty(Config.SHARE_USER_Name,userLogin.getUser_name());
                if (userLogin.getPhone_no().equals("null")){
                    AppContext.setProperty(Config.USERPHONE,"");
                }else {
                    AppContext.setProperty(Config.USERPHONE,userLogin.getPhone_no());
                }
                // share.edit().putString(Config.SHARE_USERPWD,afterencrypt).commit();
                AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(userLogin.isSet_unlock_pwd()));
                AppContext.setProperty(Config.KEY_USER_NICKNAME,userLogin.getNick_name());
                AppContext.setProperty(Config.KEY_USER_NAME,userLogin.getName());
                // AppManager.getAppManager().finishActivity();
                //环信账号
                AppContext.setProperty(Config.EMID,userLogin.getEmid());
                AppContext.setProperty(Config.MYHUANXINPWD, userLogin.getEm_passwd());
                //先登录对应的环信账号
                if (AppContext.getProperty(Config.GTCID)!=null){
                    bangGT(AppContext.getProperty(Config.GTCID));
                }else {
                    Log.e("TAG","----------Cid=null");
                }
                loginHuanXin(userLogin.getEmid(), userLogin.getEm_passwd());
                rly(userLogin.getVoip_account(), userLogin.getVoip_pwd());
//            loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());

            }
        });
    }
    private void bangGT(String cid){
        HttpClient.instance().bandGT(cid,new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("LOGIn","------------bandGT--->"+responseBean.toString());
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.setProperty(Config.ISBINDGTCID,"true");
                }else {
                    AppContext.setProperty(Config.ISBINDGTCID,"false");
                }
            }
        });
    }


    /** 清除密码 */
    private void clearSharePassword() {

        AppContext.setProperty(Config.SHARE_USERPWD, "");
    }

    //环信登录
    private void loginHuanXin(final String userName, final String pwd) {

        if (userName==null||userName.equals("null")){
//            AppContext.toast("聊天账号账号错误");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//            loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
            return;
        }
        if (pwd==null||pwd.equals("null")){
//            AppContext.toast("聊天账号密码错误");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//            loginVOIP(userLogin.getVoip_account(),userLogin.getVoip_pwd());
            return;
        }
        ProgressDialog.show(LoginByNameActivity.this, R.string.Is_landing);
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
                        initializeContacts();
                        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                                        AppContext.getProperty(Config.SHARE_USER_Name));
                            }
                        }).start();
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

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(LoginByNameActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
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
        String appKey = Config.VOIPappKey;
        String token = Config.VOIPtoken;


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



}
