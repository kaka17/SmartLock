package com.sht.smartlock.phone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.keyboard.KeyboardUtil;
import com.sht.smartlock.phone.ui.voip.VideoActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallActivity;
import com.sht.smartlock.phone.ui.voip.VoIPCallHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class CallPhoneActivity extends AppCompatActivity {

    private Context ctx;
    private Activity act;
    private EditText edit;

    KeyboardUtil util;
    private Button btnBoH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_phone);
        ctx = this;
        act = this;
        edit = (EditText) this.findViewById(R.id.edit1);
        btnBoH = (Button)findViewById(R.id.btnBoH);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit,InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0); //强制隐藏键盘

        btnBoH.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String number = edit.getText().toString();
                if (number.length()!=11){
                    AppContext.toast("请输入正确的电话号码"+number.length());
                }
                //用intent启动拨打电话
//                callVoIPAction(CallPhoneActivity.this, ECVoIPCallManager.CallType.DIRECT, "abc", number, false);

                callVoIPAction(ctx, ECVoIPCallManager.CallType.values()[0], "abc", number, false);
            }
        });

        if (android.os.Build.VERSION.SDK_INT <= 10) {
            edit.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
//        	setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus = cls.getMethod("setSoftInputShownOnFocus", boolean.class);

                //4.0的是setShowSoftInputOnFocus,4.2的是setSoftInputOnFocus
                setShowSoftInputOnFocus.setAccessible(false);
                setShowSoftInputOnFocus.invoke(edit, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        util=new KeyboardUtil(act, ctx, edit);

        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                util.showKeyboard();
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(util.isShow){
                util.hideKeyboard();
            }else{
                finish();
            }
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    //==============================================================
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



}

