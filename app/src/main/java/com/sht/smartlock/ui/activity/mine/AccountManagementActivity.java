package com.sht.smartlock.ui.activity.mine;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.ui.activity.BindingPhoneActivity;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.OldPhoneActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.util.BitmapUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.sht.smartlock.zxing.CaptureActivity;
import com.yuntongxun.ecsdk.ECDevice;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AccountManagementActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private Button btnExitAccount;
    private RelativeLayout relatUserHead;
    private RelativeLayout relatUpdataPwd;

    private ImageView igUserHead;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bitmap bm = (Bitmap) msg.obj;
                    igUserHead.setImageBitmap(bm);
//                    BaseApplication.toast("00");
                    break;
                case 1:
                    if (ProgressDialog.dialog.isShowing()) {
                        ProgressDialog.disMiss();
                        BaseApplication.toast("图片上传失败");
                    }
                    break;
            }
        }
    };
    private LinearLayout linPhone;
    private TextView tvPhone;
    private boolean isbinding=false;
    private String phone="";
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
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        findviewbyid();
        setClickLister();
        initData();
    }

    JSONTokener jsonParser;
    JSONObject registererjson;

    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        btnExitAccount = (Button) findViewById(R.id.btnExitAccount);
        relatUserHead = (RelativeLayout) findViewById(R.id.relatUserHead);
        relatUpdataPwd = (RelativeLayout) findViewById(R.id.relatUpdataPwd);
        igUserHead = (ImageView) findViewById(R.id.igUserHead);
        linPhone = (LinearLayout) findViewById(R.id.linPhone);
        tvPhone = (TextView) findViewById(R.id.tvPhone);

        tvTitlePanel.setText("账户管理");
        HttpClient.instance().get_user_head_image(new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在加载...");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                LogUtil.log("responseBean=" + responseBean);

                try {
                    jsonParser = new JSONTokener(responseBean.toString());
                    registererjson = (JSONObject) jsonParser.nextValue();
                    LogUtil.log("log=" + registererjson.getString("result"));

                    byte[] b = Base64Utils.decode(registererjson.getString("result"));
                    Bitmap bitmap = BitmapUtil.Bytes2Bimap(b);
                    igUserHead.setImageBitmap(bitmap);
                } catch (Exception e) {
                    LogUtil.log(e.toString());
                }
            }
        });

        get_user_head_image();

    }

    private void get_user_head_image() {
        HttpClient.instance().get_user_head_image(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                LogUtil.log(responseBean.toString());
                try {
                    JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    if (!jsonObject.getString("result").equals("")) {
//                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), igUserHead);
//                        BaseApplication.toast(jsonObject.getString("result"));
                        //显示图片的配置.cacheInMemory(true)
//                        .cacheOnDisk(true)
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                                .showImageOnLoading(R.drawable.default_image)
                                .showImageOnFail(R.drawable.default_image)
                                .cacheInMemory(false)
                                .cacheOnDisk(false)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .build();
//                        BaseApplication.toast(jsonObject.getString("result") + "；；；" + jsonObject.getString("result"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), igUserHead, options);
                    } else {
//                        BaseApplication.toast(jsonObject.getString("result") + "图片地址");
                    }
                } catch (Exception e) {
                    LogUtil.log(e.toString());
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ProgressDialog.disMiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExitAccount://推出登录
                HttpClient.instance().login_out(AppContext.getShareUserName(), AppContext.getShareUserPwd(), new NetworkRequestLoginResult());
                break;
            case R.id.relatUserHead://用户头像
                getDialog();
                break;
            case R.id.relatUpdataPwd://修改用户密码
                startActivity(new Intent(AccountManagementActivity.this, AForgetPasswordActivity.class));
                break;
            case R.id.linPhone:
                if (isbinding){
                    Intent intent=new Intent(getApplicationContext(), OldPhoneActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString(Config.OLDPHONE,phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(getApplicationContext(), BindingPhoneActivity.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putString(Config.OLDPHONE,phone);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    private void setClickLister() {
        btn_cancle.setOnClickListener(listener);
        btnExitAccount.setOnClickListener(this);
        relatUpdataPwd.setOnClickListener(this);
        relatUserHead.setOnClickListener(this);
        linPhone.setOnClickListener(this);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           finish();
        }
    };

    private String strtype;

    private void getDialog() {
//        strtype = distrub;
        final Dialog dialog = new Dialog(AccountManagementActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.accountmanagementdialog, null);
        TextView tvCamera = (TextView) view.findViewById(R.id.tvCamera);
        TextView tvFromAlbumSelection = (TextView) view.findViewById(R.id.tvFromAlbumSelection);
        TextView tvCancle = (TextView) view.findViewById(R.id.tvCancle);
        dialog.setContentView(view);

        view.findViewById(R.id.tvCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdStatus = Environment.getExternalStorageState();
                /* 检测sd是否可用 */
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(AccountManagementActivity.this, "SD卡不可用！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                if (b){
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, CAMERE_REQUEST_CODE);
                }else {
                    mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                }


                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tvFromAlbumSelection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtil.log("type=" + strtype);
//                HttpClient.instance().setroomtrouble("322", strtype, new NetworkRequestLoginResult());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static int CAMERE_REQUEST_CODE = 1;// 相机
    private static int GALLERY_REQUEST_CODE = 2;// 相册
    private static int CROP_REQUEST_CODE = 3;// 剪切

    class NetworkRequestLoginResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext, "正在退出...");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if (responseBean.isFailure()) {
                BaseApplication.toast(responseBean.toString());
            }
            clearSharePassword();
            //把选择的数据恢复
            NewDoorFragment.pos = 0;
            NewDoorFragment.list.clear();
            NewDoorFragment.tvLock.setText("");
            //开门密码,
            AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(false));
            AppContext.setProperty(Config.EMID, null);
            AppContext.setProperty(Config.MYHUANXINPWD, null);
            AppContext.setProperty(Config.USERPHONE, null);
            AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
            AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
            AppContext.setProperty(Config.SHARE_USERPWD, null);
            AppContext.setProperty(Config.SHARE_USER_Name,null);
            EMChatManager.getInstance().logout();//此方法为同步方法
            startActivity(new Intent(AccountManagementActivity.this, LoginByNameActivity.class));
            voip_LoginOut();
            finish();
//            BaseApplication.toast(responseBean.toString());
        }
    }

    private void voip_LoginOut(){
        ECDevice.logout(new ECDevice.OnLogoutListener() {
            @Override
            public void onLogout() {
                // SDK 回调通知当前登出成功
                // 这里可以做一些（与云通讯IM相关的）应用资源的释放工作
                // 如（关闭数据库，释放界面资源和跳转等）
//                                AppContext.toast("退出成功");
                ECDevice.unInitial();
            }
        });

    }

    private void  initData(){//手机号码
        HttpClient.instance().judgeuserPhoneNo(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject=new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if(!result.equals("false")){
                        isbinding=true;
                        tvPhone.setText(result);
                        phone=result;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    private String icon_path = "";
    private String path = "";
    private StringBuffer stringBuffer;
    private String IdCardImageUrl;
    private String strImageUri;
    private ImageView ivPhotocopy_ID_card;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERE_REQUEST_CODE) {// 相机拍照
            if (data == null) {
                Toast.makeText(getApplicationContext(), "图片不正确", Toast.LENGTH_LONG).show();
                return;
            } else {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bm = bundle.getParcelable("data");
                    // 把图片保存到sd卡上
                    Uri uri = saveBitmap(bm);
                    igUserHead.setImageBitmap(bm);
                    // 裁剪步骤
                    // startImageZoom(uri);
                    //将图片打包上传
                    BitmapToByte(bm);
//                    BaseApplication.toast(BitmapUtil.getBitmapSize(bm)+"大小");
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {// 相册区照片
            // ----------------------------
            Uri result_uri = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (null == result_uri)
                return;
            final String scheme = result_uri.getScheme();
            String path = null;
            if (scheme == null)
                path = result_uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                path = result_uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = getContentResolver().query(result_uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            path = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }


            IdCardImageUrl = path;
            if (IdCardImageUrl.equals("null") || IdCardImageUrl.equals("")) {
                Toast.makeText(AccountManagementActivity.this, "图片选择错误", Toast.LENGTH_LONG).show();
            } else {
                try {
//                  Bitmap bitmap = Utils.getLoacalBitmap(IdCardImageUrl);
                    Bitmap bitmap = BitmapUtil.revitionImageSize(IdCardImageUrl);
                    LogUtil.log("bm=" + bitmap);
                    igUserHead.setImageBitmap(bitmap);
                    ProgressDialog.show(AccountManagementActivity.this, "正在上传");
                    //将图片打包上传
                    BitmapToByte(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                BaseApplication.toast(BitmapUtil.getBitmapSize(bitmap) + "大小");
            }
        } else if (requestCode == CROP_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            Bitmap bm = bundle.getParcelable("data");
            LogUtil.log("bm=" + bm);
            igUserHead.setImageBitmap(bm);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void BitmapToByte(final Bitmap bm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //判断10s内若是上传不成功提示
                    Thread.sleep(10000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //将压缩后的图片转化成二进制数据
        //将图片转换成 byte
        byte[] bytes = BitmapUtil.Bitmap2Bytes(bm);
        String id_image = Base64Utils.encode(bytes);
        //上传数据
        HttpClient.instance().set_user_head_image(id_image, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
//                BaseApplication.toast(responseBean.toString());
                if (responseBean.isFailure()) {
                    LogUtil.log("上传失败！");
                } else {
                    try {
                        JSONTokener jsonParser = new JSONTokener(responseBean.toString());
                        JSONObject registererjson = (JSONObject) jsonParser.nextValue();
                        if (registererjson.getString("result").equals("true")) {
//                        HttpClient.instance().myinfo(new NetworkRequestLoginResult());
                            get_user_head_image();
                        } else {
                            BaseApplication.toast("上传失败！");
                        }
                    } catch (Exception e) {
                        LogUtil.log(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                BaseApplication.toast("图片上传失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ProgressDialog.disMiss();
            }
        });
    }


    // 将图片保存到sd卡上
    private Uri saveBitmap(Bitmap bm) {
        // 图片保存到sd卡的路径
        File tmpDir = new File(Environment.getExternalStorageDirectory()
                + "/smartbabybottle/image");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        String name = "mycare.png";

        File img = new File(tmpDir.getAbsolutePath() + "/", name);
        IdCardImageUrl = tmpDir.getAbsolutePath() + "/" + name;
//        LogUtil.log("jie="+IdCardImageUrl);
//        BaseApplication.toast("idcard==" + IdCardImageUrl);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            // bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除密码
     */
    private void clearSharePassword() {
        AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
        AppContext.setProperty(Config.SHARE_USERPWD, null);
        AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
        AppContext.setProperty(Config.SHARE_USER_Name,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, CAMERE_REQUEST_CODE);
                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开相机权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_management;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
