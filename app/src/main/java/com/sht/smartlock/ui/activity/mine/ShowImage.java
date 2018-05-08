package com.sht.smartlock.ui.activity.mine;

import android.graphics.Bitmap;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.UserInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.BitmapUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ShowImage extends BaseActivity {
    private ImageView showImageIDcard;
    private Bundle bundle;
    private  Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewByid();
    }

    private void findviewByid(){
        showImageIDcard = (ImageView)findViewById(R.id.showImageIDcard);
       bundle = getIntent().getExtras();
       final UserInfo userInfo= (UserInfo) bundle.getSerializable("IDCardImage");
        if(!userInfo.getId_image().equals("")&&
                !userInfo.getId_image().equals("null")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] b= Base64Utils.decode(userInfo.getId_image());
                    bitmap= BitmapUtil.Bytes2Bimap(b);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }).start();
        }
//        BaseApplication.toast(bundle.get("IDCardImage").toString());
    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showImageIDcard.setImageBitmap(bitmap);
                    break;

            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        int Action = event.getAction();
        float X = event.getX();
        float Y = event.getY();
//        mAction.setText("Action = " + Action);
//        mPosition.setText("Position = (" + X + " , " + Y + ")");
//        BaseApplication.toast(Action+"");
        if(Action==0||Action==1){
            finish();
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_image;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
