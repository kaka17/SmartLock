package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.core.ClientUser;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
//import com.sht.smartlock.phone.ui.account.LoginActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.ui.entity.UserInfoEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.sht.smartlock.zxing.CaptureActivity;
import com.yuntongxun.ecsdk.ECInitParams;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Login2Activity extends BaseActivity implements View.OnClickListener {

    private ImageView ivGoToRegisger,ivGoToLogin,ivGoToScan;
    private String afterencrypt;
    public static int ISSCAN=100;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.CAMERA;
    //    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login2);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        onBack();
        initView();
        setOnclickListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login2;
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
        ivGoToRegisger= (ImageView) findViewById(R.id.ivGoToRegisger);
        ivGoToLogin= (ImageView) findViewById(R.id.ivGoToLogin);
        ivGoToScan= (ImageView) findViewById(R.id.ivGoToScan);
    }

    private void setOnclickListener(){
        ivGoToRegisger.setOnClickListener(this);
        ivGoToLogin.setOnClickListener(this);
        ivGoToScan.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivGoToRegisger:
                initData();
                break;
            case R.id.ivGoToLogin:
                Intent intentLogin=new Intent(getApplicationContext(), LoginByNameActivity.class);
//                intentLogin.putExtra(Config.RANDOMPWD,"12");
                startActivity(intentLogin);
                finish();
                break;
            case R.id.ivGoToScan:
                boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                if (b){
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, ISSCAN);
                }else {
                    mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                }
                break;
        }
    }


    private void initData(){

        HttpClient.instance().userRegister(new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(Login2Activity.this, "正在注册");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Log.e("LOGIN", "->" + responseBean.toString());
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
                AppContext.setProperty(Config.SHARE_USER_Name,data.getUser_name());
                AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
                AppContext.setProperty(Config.SHARE_USERSESSIONID, data.getSessionid());
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
                getCancelDialog(data.getUser_name(), data.getPasswd());
                loginHuanXin(data.getEmid(), data.getEm_passwd());
                rly(data.getVoip_account(), data.getVoip_pwd());
            }
        });
    }
    /*
        *   Dialog
        * */
    private void getCancelDialog(String userName, final String userPwd ) {
        final Dialog dialog = new Dialog(Login2Activity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.registerdialog, null);
        dialog.setContentView(view);
        //
      TextView tv_Username= (TextView) view.findViewById(R.id.tv_Username);
      TextView tv_userPwd= (TextView) view.findViewById(R.id.tv_userPwd);
        tv_Username.setText(userName);
        tv_userPwd.setText(userPwd);
        view.findViewById(R.id.tvNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                dialog.dismiss();
                finish();
            }
        });
        view.findViewById(R.id.tvGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intentLogin=new Intent(getApplicationContext(), ChangPwdActivity.class);
                intentLogin.putExtra(Config.RANDOMPWD,userPwd);
                startActivity(intentLogin);
                finish();
            }
        });
        dialog.show();
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
        String appKey = Config.VOIPappKey;
        String token = Config.VOIPtoken;

        ClientUser clientUser = new ClientUser(yuntxUser);
        clientUser.setAppKey(appKey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(yuntxPassWord);
        CCPAppManager.setClientUser(clientUser);

        SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("DATA", "resultCode" + resultCode);
        if (resultCode==ISSCAN){
            Bundle extras = data.getExtras();
            Log.e("DATA", "-------------->" + extras.getString("result"));
           String url=extras.getString("result")+"&reg=xxx";
            Intent intent=new Intent(getApplicationContext(),ScanSuccessActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString(Config.URLFORREANDIN,url);
//            bundle.putString(Config.SCANNAME,data.getUser_name());
//            bundle.putString(Config.SCANNAMEPWD,data.getPasswd());
            intent.putExtras(bundle);
            startActivity(intent);
//            scanRegister(url);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x2a) {
            doLauncherAction();
        }
    }

    private void doLauncherAction() {
        try {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("launcher_from", 1);
//            // 注册成功跳转
//            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, ISSCAN);

                } else {
                    //如果请求失败
                    AppContext.toast("请手动相机权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }




}
