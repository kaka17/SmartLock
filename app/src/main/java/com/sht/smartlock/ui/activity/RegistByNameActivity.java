package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.core.ClientUser;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myview.CountDownTimerUtils;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.RegistNameEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.sht.smartlock.zxing.CaptureActivity;
import com.yuntongxun.ecsdk.ECInitParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistByNameActivity extends BaseActivity implements View.OnClickListener {

    private EditText etTrueName,etPwd,etSurePwd,etPhone,etPhoneNum;
    private ImageView tvDeletePwd,ivOpenPwd,ivClosePwd;
    private TextView tvGetPhoneCode;
    private Button bnRegist;
    private TextView tvGotoLogin;
    private ImageView ivScan;
    private boolean isOpenEye=true;
    private boolean isCloseEye=true;
    private String afterencrypt;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    public static int ISSCAN=100;
    //权限检测类
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.CAMERA;
    //    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();
    private CountDownTimerUtils mCountDownTimerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_regist_by_name);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        initView();
        setOnClickListener();
        onBack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_regist_by_name;
    }

    @Override
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
    private void initView(){
        etTrueName = (EditText) findViewById(R.id.etTrueName);
        etPwd = (EditText) findViewById(R.id.etPwd);
        etSurePwd = (EditText) findViewById(R.id.etSurePwd);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        tvGetPhoneCode = (TextView) findViewById(R.id.tvGetPhoneCode);
        tvDeletePwd = (ImageView) findViewById(R.id.tvDeletePwd);
        ivOpenPwd = (ImageView) findViewById(R.id.ivOpenPwd);
        ivClosePwd = (ImageView) findViewById(R.id.ivClosePwd);

        bnRegist = (Button) findViewById(R.id.bnRegist);
        tvGotoLogin = (TextView) findViewById(R.id.tvGotoLogin);
        ivScan = (ImageView) findViewById(R.id.ivScan);
    }



    private void setOnClickListener(){
        tvDeletePwd.setOnClickListener(this);
        ivOpenPwd.setOnClickListener(this);
        ivClosePwd.setOnClickListener(this);
        tvGetPhoneCode.setOnClickListener(this);
        bnRegist.setOnClickListener(this);
        tvGotoLogin.setOnClickListener(this);
        ivScan.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvGetPhoneCode://获取验证码
                String phone=etPhone.getText().toString().trim();
                boolean mobile = isMobile(phone);
                if (mobile){
                     mCountDownTimerUtils = new CountDownTimerUtils(tvGetPhoneCode, 60000, 1000);
                    mCountDownTimerUtils.start();
                    getPhoneCode(phone);
                }else {
                    AppContext.toast("请输入正确的电话号码");
                    return;
                }
                break;
            case R.id.bnRegist://注册
                String myName=etTrueName.getText().toString().trim();
                //
                String myetPwd= etPwd.getText().toString().trim();
                String mySureetPwd= etSurePwd.getText().toString().trim();
                //
                String phoneCode= etPhoneNum.getText().toString().trim();
                if (myetPwd.length()<6){
                    AppContext.toast("请输入6到18位密码");
                    return;
                }
                if (myetPwd.length()>18){
                    AppContext.toast("请输入6到18位密码");
                    return;
                }
                if ( ! myetPwd.equals(mySureetPwd)){
                    AppContext.toast("密码不一致请重新输入");
                    return;
                }

                //
                String phones=etPhone.getText().toString().trim();
                boolean mobiles = isMobile(phones);
                if (!mobiles){
                    AppContext.toast("电话号码不正确，请重新输入");
                    return;
                }

                if (phoneCode.isEmpty()){
                    AppContext.toast("请输入验证码");
                    return;
                }

                //
                if (myName.equals("")){
                    initRegist(myName, myetPwd, phones, phoneCode);
                }else {
                    boolean chiose = isChiose(myName);
                    if (chiose) {
                        bnRegist.setClickable(false);
                        initRegist(myName, myetPwd, phones, phoneCode);
                    } else {
                        AppContext.toast("请输入18位以内汉英字");
                    }
                }


                break;
            case R.id.tvGotoLogin:
                startActivity(new Intent(getApplicationContext(),LoginByNameActivity.class));
                break;
            case R.id.ivScan:
                boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                if (b){
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, ISSCAN);
                }else {
                    mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                }
                break;
            case R.id.tvDeletePwd:
                etPwd.setText("");
                 break;
            case R.id.ivOpenPwd:
                //显示密码
                if (isOpenEye) {
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivOpenPwd.setImageResource(R.drawable.btn_eye_close);
                    isOpenEye=false;
                }else {
                    etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivOpenPwd.setImageResource(R.drawable.btn_eye_open);
                    isOpenEye=true;
                }
                break;
            case R.id.ivClosePwd:
                //隐藏密码
                if (isCloseEye) {
                    etSurePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivClosePwd.setImageResource(R.drawable.btn_eye_open);
                    isCloseEye=false;
                }else {
                    etSurePwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivClosePwd.setImageResource(R.drawable.btn_eye_close);
                    isCloseEye=true;
                }
                 break;

        }

    }

    //判断是否全部是中文
    private boolean isChiose(String str){
        Pattern pa   =   Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z]{0,18}$");
        Matcher matcher   =pa.matcher(str);
        boolean b = matcher.find();//true为全部是汉字，否则是false
        return b;
    }

    /**
     * 手机号验证
     * @author ：shijing
     * 2016年12月5日下午4:34:46
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(final String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    private void getPhoneCode(String phone){
        HttpClient.instance().registGetPhoneCode(phone, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
//                ProgressDialog.show(RegistByNameActivity.this,"正在发送中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
//                ProgressDialog.disMiss();
                mCountDownTimerUtils.cancel();
                mCountDownTimerUtils.onFinish();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
//                ProgressDialog.disMiss();
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.toast(data.getMsg());
                }else if (data.getCode()==0){
                    AppContext.toast(data.getMsg());
                    mCountDownTimerUtils.cancel();
                    mCountDownTimerUtils.onFinish();
                }else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }
    private void initRegist(String name,String password,String phone_no,String code){
        HttpClient.instance().registNewVersion(name, password, phone_no, code, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(RegistByNameActivity.this, "正在注册...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("注册失败");
                bnRegist.setClickable(true);
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("Regist", "---------->" + responseBean.toString());
                bnRegist.setClickable(true);
                ProgressDialog.disMiss();
                RegistNameEntity data = responseBean.getData(RegistNameEntity.class);
                if (data.getCode() == 1) {//成功
                    //设置内部数据
                    data.paseJson(data.getResult());
                    //先登录对应的环信账号
                    showMyDialog(data.getUser_name());
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
                    if (AppContext.getProperty(Config.GTCID)!=null){
                        bangGT(AppContext.getProperty(Config.GTCID));
                    }
                    loginHuanXin(data.getEmid(), data.getEm_passwd());
                    rly(data.getVoip_account(), data.getVoip_pwd());
                } else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }

    private void bangGT(String cid){
        HttpClient.instance().bandGT(cid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.setProperty(Config.ISBINDGTCID,"true");
                }else {
                    AppContext.setProperty(Config.ISBINDGTCID,"false");
                }
            }
        });
    }

    private void showMyDialog(String id){
        final Dialog dialog = new Dialog(mContext, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.registsuccessdialog, null);
        dialog.setContentView(view);
        //
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);


        tvMessage.setText(getString(R.string.RegistNam)+id+"\n"+getString(R.string.RegistNameees));


        view.findViewById(R.id.tvSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

    //容联云登录
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
