package com.sht.smartlock.ui.activity.myview;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/17.
 */
public class TimeUtils {
    private int time=61;

    private Timer timer;

    private Button btnSure;

    private String btnText;
    private Handler handler;
    public TimeUtils(Button btnSure, String btnText) {
        super();
        this.btnSure = btnSure;
        this.btnText = btnText;
    }
    public TimeUtils( Handler handler) {
            super();
        this.handler=handler;
    }





    public void runTimer(){
        timer=new Timer();
        TimerTask task=new TimerTask() {

            @Override
            public void run(){
                time--;
                if (time>0){
                    Message msg=handler.obtainMessage();
                    msg.what=1;
                    handler.sendMessage(msg);
                }else {
                    Message msg=handler.obtainMessage();
                    msg.what=0;
                    handler.sendMessage(msg);
                }

            }
        };


        timer.schedule(task, 100, 1000);
    }

    public void cancelRun(){
        timer.cancel();
        time=61;
    }

//    private Handler handler =new Handler(){
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case 1:
//
//                    if(time>0){
//                        btnSure.setEnabled(false);
//                        btnSure.setText(time+"秒后重新发送");
//                        btnSure.setTextSize(14);
//                    }else{
//
//                        timer.cancel();
//                        btnSure.setText(btnText);
//                        btnSure.setEnabled(true);
//                        btnSure.setTextSize(14);
//                    }
//
//                    break;
//
//
//                default:
//                    break;
//            }
//
//        };
//    };
}
