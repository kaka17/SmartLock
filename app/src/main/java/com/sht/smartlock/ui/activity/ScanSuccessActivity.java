package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.core.ClientUser;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.UserInfoEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.yuntongxun.ecsdk.ECInitParams;

import java.security.PublicKey;

public class ScanSuccessActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvName,tvPwd,tvNo,tvChang;
//    private String name;
//    private String namePwd;
    private String url;
    private String afterencrypt;
    private LinearLayout linIsTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scan_success);
        Bundle extras = getIntent().getExtras();
//        name=extras.getString(Config.SCANNAME);
//        namePwd=extras.getString(Config.SCANNAMEPWD);
        url=extras.getString(Config.URLFORREANDIN);

        onBack();
        initView();
        setOnClickLister();
        scanRegister(url);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_success;
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
        linIsTrue = (LinearLayout) findViewById(R.id.linIsTrue);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPwd = (TextView) findViewById(R.id.tvPwd);
        tvNo = (TextView) findViewById(R.id.tvNo);
        tvChang = (TextView) findViewById(R.id.tvChang);

    }
    private void setOnClickLister(){
        tvNo.setOnClickListener(this);
        tvChang.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvNo:
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

                break;
            case R.id.tvChang:
                Intent intentChang=new Intent(getApplicationContext(),ChangPwdActivity.class);
                intentChang.putExtra(Config.RANDOMPWD,tvPwd.getText().toString());
                startActivity(intentChang);
                finish();
                break;
        }
    }

    private void scanRegister(String url){
        HttpClient.instance().scanUserRegister(url, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(ScanSuccessActivity.this, "正在注册并登陆。。。");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                linIsTrue.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Log.e("SCAN", "----------->" + responseBean.toString());
                linIsTrue.setVisibility(View.VISIBLE);
                UserInfoEntity data = responseBean.getData(UserInfoEntity.class);
                try {
                    // 从字符串中得到公钥
                    PublicKey publicKey = RSAUtils.loadPublicKey(Config.PUCLIC_KEY);
                    // 从文件中得到公钥
                    // 加密
                    byte[] encryptByte = RSAUtils.encryptData(data.getPasswd().getBytes(), publicKey);
                    // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
                    afterencrypt = Base64Utils.encode(encryptByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppContext.setProperty(Config.SHARE_USER_ACCOUNT, data.getUser_name());
                AppContext.setProperty(Config.SHARE_USER_Name, data.getUser_name());
                AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
                AppContext.setProperty(Config.SHARE_USERSESSIONID, data.getSessionid());
                tvName.setText(data.getUser_name());
                tvPwd.setText(data.getPasswd());
                //环信账号
                AppContext.setProperty(Config.EMID, data.getEmid());
                AppContext.setProperty(Config.MYHUANXINPWD, data.getEm_passwd());
                AppContext.setProperty(Config.SHARE_VOIP_ACCOUNT, data.getVoip_account());
                AppContext.setProperty(Config.SHARE_VOIP_PED, data.getVoip_pwd());
                if (data.getSet_unlock_pwd().equals("0")) {
                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(false));
                } else {
                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(true));
                }
                //先登录对应的环信账号
//                getCancelDialog(data.getUser_name(), data.getPasswd());
                loginHuanXin(data.getEmid(), data.getEm_passwd());
                rly(data.getVoip_account(), data.getVoip_pwd());
                if (AppContext.getProperty(Config.GTCID)!=null){
                    bangGT(AppContext.getProperty(Config.GTCID));
                }
            }
        });
    }

    private void bangGT(String cid){
        HttpClient.instance().bandGT(cid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("LOGIn", "------------bandGT--->" + responseBean.toString());
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.setProperty(Config.ISBINDGTCID,"true");
                }else {
                    AppContext.setProperty(Config.ISBINDGTCID,"false");
                }
            }
        });
    }

    //环信登录
    private void loginHuanXin(final String userName, final String pwd) {

        if (userName==null||userName.equals("null")){
            return;
        }
        if (pwd==null||pwd.equals("null")){
            return;
        }
//        ProgressDialog.show(Login2Activity.this, R.string.Is_landing);
        //用户名 和密码相同
        EMChatManager.getInstance().login(userName, pwd, new EMCallBack() {//回调


            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
//                        ProgressDialog.disMiss();
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        //去获取所有会话, 获取单个会话
                        EMChatManager.getInstance().getConversation(userName);
                        Log.d("main", "登陆聊天服务器成功！");
                        // 登陆成功，保存用户名密码
                        AppContext.getInstance().setUserName(userName);
                        AppContext.getInstance().setPassword(pwd);
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

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
//                ProgressDialog.disMiss();
//                loginVOIP(userLogin.getVoip_account(), userLogin.getVoip_pwd());
                finish();
            }
        });
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

    }

}
