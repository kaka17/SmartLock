package com.sht.smartlock.ui.activity.mine;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sht.smartlock.model.UserInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.BitmapUtil;
import com.sht.smartlock.util.ImageUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyInformationActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnmyInfoCancle;
    private TextView tvTitlePanel;

    //    private TextView tvOpen_door_pwd;
    private TextView tvFull_name;
    private TextView tvID_number;
    private TextView tvContact_phone;
    private TextView tvInvoice_title;
    private TextView tvPhotocopy_ID_card;
    private RelativeLayout linearPicIDCard;
    private boolean stateImage = false;

    private UserInfo userInfo;
    private static int CAMERE_REQUEST_CODE = 1;// 相机
    private static int GALLERY_REQUEST_CODE = 2;// 相册
    private static int CROP_REQUEST_CODE = 3;// 剪切

    private ArrayList<HashMap<String, String>> InVoicelistItem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bitmap bm = (Bitmap) msg.obj;
//                    ivPhotocopy_ID_card.setImageBitmap(bm);
//                    BaseApplication.toast("00");
                    ivCard.setVisibility(View.VISIBLE);
                    ivCard.setImageBitmap(bm);
                    stateImage = true;
                    break;
                case 1:
                    if (ProgressDialog.dialog.isShowing()) {
                        ProgressDialog.disMiss();
                        BaseApplication.toast("图片上传失败,请重新上传");
                    }
                    break;
            }

        }
    };
    private LinearLayout linMyImg, linName, linCard;
    private RelativeLayout reCard;
    private ImageView ivCard;
    private int isMyPic = 0;// 0 上传头像  1上传身份证
    private ImageView ivMyPic;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.CAMERA;
    //    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission = new ArrayList<>();
    File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        // 创建一个以当前时间为名称的文件
        tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        findviewbyid();
        setClickLister();
        get_user_head_image();
        MyPopupWindow();
    }

    private void findviewbyid() {
        InVoicelistItem = new ArrayList<HashMap<String, String>>();
        btnmyInfoCancle = (ImageView) findViewById(R.id.btnmyInfoCancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        tvTitlePanel.setText("我的资料");

        tvFull_name = (TextView) findViewById(R.id.tvFull_name);
        tvID_number = (TextView) findViewById(R.id.tvID_number);
        tvInvoice_title = (TextView) findViewById(R.id.tvInvoice_title);
        linearPicIDCard = (RelativeLayout) findViewById(R.id.linearPicIDCard);
        ivPhotocopy_ID_card = (ImageView) findViewById(R.id.ivPhotocopy_ID_card);

        linMyImg = (LinearLayout) findViewById(R.id.linMyImg);
        ivMyPic = (ImageView) findViewById(R.id.ivMyPic);
        linName = (LinearLayout) findViewById(R.id.linName);
        linCard = (LinearLayout) findViewById(R.id.linCard);
        reCard = (RelativeLayout) findViewById(R.id.reCard);
        ivCard = (ImageView) findViewById(R.id.ivCard);


        HttpClient.instance().myinfo(new NetworkRequestLoginResult());
    }

    private void setClickLister() {
        btnmyInfoCancle.setOnClickListener(listener);
        tvInvoice_title.setOnClickListener(this);
        linearPicIDCard.setOnClickListener(this);
        tvID_number.setOnClickListener(this);
        tvFull_name.setOnClickListener(this);
        ivPhotocopy_ID_card.setOnClickListener(this);
        linMyImg.setOnClickListener(this);
        linName.setOnClickListener(this);
        linCard.setOnClickListener(this);
        reCard.setOnClickListener(this);

    }

    class NetworkRequestLoginResult extends HttpCallBack {
        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext, "正在加载...");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            super.onFailure(error, message);
            BaseApplication.toast("请求失败");
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if (responseBean.isFailure()) {
                BaseApplication.toast(responseBean.toString());
            }
            userInfo = responseBean.getData(UserInfo.class);
            if (!userInfo.getName().equals("") && !userInfo.getName().equals("null")) {
                tvFull_name.setText(userInfo.getName());
            }
            if (!userInfo.getId_no().equals("") && !userInfo.getId_no().equals("null")) {
                tvID_number.setText(userInfo.getId_no());
            }
            LogUtil.log("enen" + userInfo.getInvoice().toString());
            if (!userInfo.getInvoice().equals("[]") && !userInfo.getInvoice().equals("null")) {
                JsonInfo(userInfo.getInvoice().toString());
            } else {
                tvInvoice_title.setText("");
            }
            if (stateImage == false) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //将图片转码成bitmap
                        byte[] b = Base64Utils.decode(userInfo.getId_image());
                        Bitmap bitmap = BitmapUtil.Bytes2Bimap(b);
                        Message message = Message.obtain();
                        message.obj = bitmap;
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            ProgressDialog.disMiss();
        }
    }


    private void JsonInfo(String inviocetitle) {
        try {
            JSONArray jArray = new JSONArray(inviocetitle);
            InVoicelistItem.clear();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", json_data.getString("title"));
                InVoicelistItem.add(map);
            }
            LogUtil.log("InVoicelistItem=" + InVoicelistItem.get(0).get("title"));
//                    if(!InVoicelistItem.get(0).get("title").equals("")&&
//                            !InVoicelistItem.get(0).get("title").equals("null")){
            tvInvoice_title.setText(InVoicelistItem.get(0).get("title"));
//                    }

        } catch (Exception e) {
//                LogUtil.log("aaax"+e.toString());
            tvInvoice_title.setText("");
        }

    }

    Intent intent = null;
    Bundle bundle = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvFull_name:
            case R.id.linName:
                String strUserName = tvFull_name.getText().toString();
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString("tvFull_name", strUserName);
                intent.putExtras(bundle);
                intent.setClass(MyInformationActivity.this, EditUserNmaeActivity.class);
                startActivity(intent);
//                startActivity(new Intent(MyInformationActivity.this, EditUserNmaeActivity.class));
                break;
            case R.id.tvID_number:
            case R.id.linCard:
                String strIDNumbere = tvID_number.getText().toString();
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString("tvID_number", strIDNumbere);
                intent.putExtras(bundle);
                intent.setClass(MyInformationActivity.this, EditIDNumberActivity.class);
                startActivity(intent);
//                startActivity(new Intent(MyInformationActivity.this,EditIDNumberActivity.class));
                break;
//            case R.id.tvContact_phone:
//
//                break;
            case R.id.tvInvoice_title:
                startActivity(new Intent(MyInformationActivity.this, InvoiceTitleActivity.class));
                break;
//            case R.id.linearPicIDCard:
            case R.id.reCard:
                isMyPic = 1;
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                WindowManager.LayoutParams params = MyInformationActivity.this.getWindow().getAttributes();
                params.alpha = 0.7f;
                MyInformationActivity.this.getWindow().setAttributes(params);
                upWindow.showAtLocation(reCard, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.ivPhotocopy_ID_card:
//                BaseApplication.toast("放大"+userInfo.getId_image());
                String IDNumImage = userInfo.getId_image();
                if (!IDNumImage.equals("") && !IDNumImage.equals("null")) {
                    intent = new Intent();
                    bundle = new Bundle();
//                    bundle.putString("IDCardImage", userInfo.getId_image());
                    bundle.putSerializable("IDCardImage", userInfo);
                    intent.putExtras(bundle);
                    intent.setClass(MyInformationActivity.this, ShowImage.class);
                    startActivity(intent);
                }
                LogUtil.log("qq=" + userInfo.getId_image().length() + "");
                break;
            case R.id.linMyImg:
                isMyPic = 0;
                int[] location1 = new int[2];
                view.getLocationOnScreen(location1);
                WindowManager.LayoutParams params1 = MyInformationActivity.this.getWindow().getAttributes();
                params1.alpha = 0.7f;
                MyInformationActivity.this.getWindow().setAttributes(params1);
                upWindow.showAtLocation(reCard, Gravity.BOTTOM, 0, 0);
                break;
        }
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
            //栽剪
            startPhotoZoom(Uri.fromFile(tempFile), 150);
            //直接上传
//            if (data == null) {
//                Toast.makeText(getApplicationContext(), "图片不正确", Toast.LENGTH_LONG).show();
//                return;
//            } else {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    Bitmap bm = bundle.getParcelable("data");
//                    // 把图片保存到sd卡上
////                    Uri uri = saveBitmap(bm);
//                    // 裁剪步骤
////                     startImageZoom(uri);
//                    if (isMyPic==1){//上传身份证
//                        bm= ImageUtil.zoomBitmap(bm, 100, 100);
//                        ivCard.setImageBitmap(bm);
//                        BitmapToByte(bm);
//                    }else if (isMyPic==0){//上传头像
//                        bm= ImageUtil.zoomBitmap(bm, 100, 100);
//                        ivMyPic.setImageBitmap(bm);
//                        BitmapToByte2MyPic(bm);
//                    }
//                }
//            }
//           BaseApplication.toast("path="+path);
        } else if (requestCode == GALLERY_REQUEST_CODE) {// 相册区照片
            // ----------------------------
            Uri result_uri = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (null == result_uri)
                return;
            //栽剪
            startPhotoZoom(data.getData(), 150);
            //直接上传
//            final String scheme = result_uri.getScheme();
//            String path = null;
//            if (scheme == null)
//                path = result_uri.getPath();
//            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
//                path = result_uri.getPath();
//            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//                Cursor cursor = getContentResolver().query(result_uri,
//                        new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
//                if (null != cursor) {
//                    if (cursor.moveToFirst()) {
//                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                        if (index > -1) {
//                            path = cursor.getString(index);
//                        }
//                    }
//                    cursor.close();
//                }
//            }
//            IdCardImageUrl = path;
//            if (IdCardImageUrl.equals("null") || IdCardImageUrl.equals("")) {
//                Toast.makeText(MyInformationActivity.this, "图片选择错误", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//
////                    Bitmap  bitmap = BitmapUtil.revitionImageSize(IdCardImageUrl);
//                    Bitmap bitmap= ImageUtil.loadImgThumbnail(IdCardImageUrl, 100, 100);
//                    LogUtil.log("bm=" + bitmap);
//                    ProgressDialog.show(MyInformationActivity.this, "正在上传");
//                    //将图片打包上传
//                    if (isMyPic==1){//上传身份证
//                        ivCard.setImageBitmap(bitmap);
//                        BitmapToByte(bitmap);
//                    }else if (isMyPic==0){//上传头像
//                        ivMyPic.setImageBitmap(bitmap);
//                        BitmapToByte2MyPic(bitmap);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } else if (requestCode == CROP_REQUEST_CODE) {
//            AppContext.toast("jietu");
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            Bitmap bm = bundle.getParcelable("data");
            LogUtil.log("bm=" + bm);
//            ivMyPic.setImageBitmap(bm);
            if (isMyPic == 1) {//上传身份证
                bm = ImageUtil.zoomBitmap(bm, 100, 100);
                ivCard.setImageBitmap(bm);
                BitmapToByte(bm);
            } else if (isMyPic == 0) {//上传头像
                bm = ImageUtil.zoomBitmap(bm, 100, 100);
                ivMyPic.setImageBitmap(bm);
                BitmapToByte2MyPic(bm);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void BitmapToByte(final Bitmap bm) {
        //将压缩后的图片转化成二进制数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //判断10s内若是上传不成功提示
//                    Thread.sleep(10000);
//                    handler.sendEmptyMessage(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        //将图片转换成 byte
        byte[] bytes = BitmapUtil.Bitmap2Bytes(bm);
        String id_image = Base64Utils.encode(bytes);
        //上传数据
        HttpClient.instance().set_User_Id_Image(id_image, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
//                BaseApplication.toast(responseBean.toString());
                try {
                    JSONTokener jsonParser = new JSONTokener(responseBean.toString());
                    JSONObject registererjson = (JSONObject) jsonParser.nextValue();
//                      BaseApplication.toast(registererjson.getString("result").toString());
                    if (registererjson.getString("result").equals("true")) {
                        HttpClient.instance().myinfo(new NetworkRequestLoginResult());
                    }
                } catch (Exception e) {
                    LogUtil.log(e.toString());
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ProgressDialog.disMiss();
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                BaseApplication.toast("图片上传失败");
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
        BaseApplication.toast("idcard==" + IdCardImageUrl);
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


    private View upView;
    private PopupWindow upWindow;

    private void MyPopupWindow() {
        upView = LayoutInflater.from(MyInformationActivity.this).inflate(
                R.layout.photocopycard_pop, null);
        upWindow = new PopupWindow(upView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        upWindow = new PopupWindow(upView,display.getWidth(),display.getHeight(), true);


        // 需要设置一下此参数，点击外边可消失
        upWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置此参数获得焦点，否则无法点击
        upWindow.setFocusable(true);
        // 设置动画展示
//         upWindow.setAnimationStyle(R.style.AnimationGrowPop);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        ColorDrawable drawable = new
                ColorDrawable(Color.parseColor("#ffffffff"));
        // 设置SelectPicPopupWindow弹出窗体的背景
        upView.setBackgroundDrawable(drawable);
        // 找控件

        //拍照
        upView.findViewById(R.id.tvPhotoGraph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdStatus = Environment.getExternalStorageState();
                /* 检测sd是否可用 */
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(MyInformationActivity.this, "SD卡不可用！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                if (b) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    // 指定调用相机拍照后照片的储存路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(intent, CAMERE_REQUEST_CODE);
                } else {
                    mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                }
                upWindow.dismiss();
            }
        });

        //相册
        upView.findViewById(R.id.tvFromAlbumSelection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
                upWindow.dismiss();
            }
        });

        //取消
        upView.findViewById(R.id.tvPhotoCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upWindow.dismiss();
            }
        });

        upWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = MyInformationActivity.this.getWindow().getAttributes();
                params.alpha = 1f;
                MyInformationActivity.this.getWindow().setAttributes(params);
            }
        });
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    // 读取文件数据转换成byte[]数据
    private byte[] fileData(String imageFilePath) {
        File file = new File(imageFilePath);
        FileInputStream fStream;
        try {
            fStream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bufferSize = 1024 * 8;
            byte[] buffer = new byte[bufferSize];

            int length = -1;

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

    @Override
    protected void onResume() {
        super.onResume();
        HttpClient.instance().myinfo(new NetworkRequestLoginResult());
    }


    private void BitmapToByte2MyPic(final Bitmap bm) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //判断10s内若是上传不成功提示
//                    Thread.sleep(10000);
//                    handler.sendEmptyMessage(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

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
                            AppContext.toast("图片上传成功");
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
                                .showImageOnFail(R.drawable.pic_upond_pic)
                                .cacheInMemory(false)
                                .cacheOnDisk(false)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .build();
//                        BaseApplication.toast(jsonObject.getString("result") + "；；；" + jsonObject.getString("result"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), ivMyPic, options);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, CAMERE_REQUEST_CODE);
                } else {
                    //如果请求失败
                    AppContext.toast("请手动相机权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_information;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
