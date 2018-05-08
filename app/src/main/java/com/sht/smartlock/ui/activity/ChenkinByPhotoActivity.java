package com.sht.smartlock.ui.activity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myview.CameraSurfaceView;
import com.sht.smartlock.ui.activity.myview.MyGiftView;
import com.sht.smartlock.ui.activity.myview.RectOnCamera;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.sht.smartlock.zxing.cameras.CameraManager;

public class ChenkinByPhotoActivity extends BaseActivity implements View.OnClickListener,RectOnCamera.IAutoFocus,CameraSurfaceView.TakePhotoSuccess{

    private CameraSurfaceView camera;
    private RectOnCamera mRectOnCamera;
    private ImageView ivTakePhoto,ivImg;
    private MyGiftView gif1;
    private String bookId;
    private String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chenkin_by_photo);
        Intent intent = getIntent();
        bookId=intent.getStringExtra("book_id");
        roomId=intent.getStringExtra("room_id");
        onBack();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chenkin_by_photo;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                camera.closeCamera();
                finish();
            }
        });
    }
    private void initView(){
        camera = (CameraSurfaceView) findViewById(R.id.camera);
        mRectOnCamera= (RectOnCamera) findViewById(R.id.rectCamera);
        ivTakePhoto = (ImageView) findViewById(R.id.ivTakePhoto);
        ivImg = (ImageView) findViewById(R.id.ivImg);
        gif1 = (MyGiftView) findViewById(R.id.gif1);

        camera.isTakePhoto(this);
        mRectOnCamera.setIAutoFocus(this);
        ivTakePhoto.setOnClickListener(this);
    }

    @Override
    public void autoFocus() {
        camera.setAutoFocus();
    }

    @Override
    public void takephotoSuccess(byte[] data) {
        ivTakePhoto.setEnabled(true);
        if (data.length>0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ivImg.setImageBitmap(bitmap);
            ivImg.setVisibility(View.GONE);
            gif1.setVisibility(View.VISIBLE);
            gif1.setMovieResource(R.raw.pic_verifying);
            String id_image = Base64Utils.encode(data);
            initCheckin(bookId,roomId,id_image);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivTakePhoto:
                ivTakePhoto.setEnabled(false);
                camera.takePicture();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.closeCamera();
    }
    private void initCheckin(String book_id,String room_id,String photo){
        HttpClient.instance().checkinByPhonto(book_id, room_id, photo, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(ChenkinByPhotoActivity.this,"正在验证身份...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("验证失败");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
//
                ProgressDialog.disMiss();
                gif1.setPaused(true);
                gif1.setVisibility(View.GONE);
                    switch (responseBean.getData()){
                        case "1":
                            AppContext.toast("自助入住成功");
                            finish();
                            break;
                        case "2":
                            AppContext.toast("需要前台验证身份证");
                            break;
                        case "-1":
                            AppContext.toast("身份证没有通过验证");
                            break;
                        case "-2":
                            AppContext.toast("该订单已经不允许checkin");
                            break;
                        case "-3":
                            AppContext.toast("没有付款记录");
                            break;
                        case "-4":
                            AppContext.toast("还未支付成功");
                            break;
                        case "-5":
                            AppContext.toast("还未到入住的时间段");
                            break;
                        case "-6":
                            AppContext.toast("该酒店不允许自助checkin");
                            break;
                        case "-7":
                            AppContext.toast("该房间已经取消，不能自助checkin");
                            break;
                        case "-8":
                            AppContext.toast("该房间还未退房");
                            break;
                        case "-9":
                            AppContext.toast("上传头像失败");
                            break;
                        case "-10":
                            AppContext.toast("头像认证失败");
                            break;
                        case "-11":
                            AppContext.toast("图片太大");
                            break;
                        case "-12":
                            AppContext.toast("后台没有上传认证图片");
                            break;
                        default:{
                            AppContext.toast("" + responseBean.getData());
                        }

                    }
            }
        });
    }
}
