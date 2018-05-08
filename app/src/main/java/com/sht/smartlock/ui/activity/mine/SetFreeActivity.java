package com.sht.smartlock.ui.activity.mine;

import android.app.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

public class SetFreeActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;


    private ToggleButton togStateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
//        initdata();


    }


    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        togStateType = (ToggleButton)findViewById(R.id.togStateType);
        tvTitlePanel.setText("设置免打扰");

    }
    private int str_true;
//    private void initdata(){
//        Bundle bundle = getIntent().getExtras();
//        str_true = bundle.getString("statefree");
//        LogUtil.log("str_true="+str_true);
//    }


    class NetworkRequestResultStatus extends HttpCallBack{
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            ProgressDialog.disMiss();
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext,R.string.on_loading);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            LogUtil.log(responseBean.toString());
            if(!responseBean.toString().equals("")){
                try{
                    JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                    JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                    if(jsonObject.getString("result").equals("0")){
                        togStateType.setChecked(false);
                    }else if(jsonObject.getString("result").equals("1")){
                        togStateType.setChecked(true);
                    }
                }catch (Exception e){
                    LogUtil.log(e.toString());
                }
            }
        }
    }



    class  NetworkRequestSetFreeResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
//            ProgressDialog.disMiss();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if(responseBean.isFailure()){
//                BaseApplication.toast(responseBean.toString());
                return;
            }

            try {
                JSONTokener jsonParser = new JSONTokener(responseBean.toString());
                JSONObject setfree = (JSONObject) jsonParser.nextValue();

                if(setfree.getString("result").equals("true")){
                    if(str_true ==2){
                        getDialog(R.string.togt_set_free_dialog);
                    }else if(str_true ==3){
                        getDialog(R.string.togf_set_free_dialog);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    View.OnClickListener toglistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
               if(togStateType.isChecked()){
//                   BaseApplication.toast("aaaaaaaaaaaa");
                   if(NewDoorFragment.list.size()==0){
                       //无数据
                       return;
                   }
                   HttpClient.instance().setroomtrouble(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), "1", new NetworkRequestSetFreeResult());
                   getDialog(R.string.togt_set_free_dialog);
               }else{
                   if(NewDoorFragment.list.size()==0){
                       //无数据
                       return;
                   }
                   HttpClient.instance().setroomtrouble(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), "0", new NetworkRequestSetFreeResult());
                   getDialog(R.string.togf_set_free_dialog);
               }
        }
    };


//    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//            if(isChecked){
////                if(str_true.equals("0")){
//
////                    AppContext.setProperty(Config.TOG_STATE_TYPE, "1");
////                }
//                BaseApplication.toast("aaaaaaaaaaaa");
//                HttpClient.instance().setroomtrouble(MainTabIndexFragment.list.get(MainTabIndexFragment.pos).getRoom_id(), "1", new NetworkRequestSetFreeResult());
//                str_true = 2;
////                BaseApplication.toast("已开启" +AppContext.getProperty(Config.TOG_STATE_TYPE));
//            }else {
//
////                HttpClient.instance().setroomtrouble("1", "0", new NetworkRequestLoginResult());
//
////                AppContext.setProperty(Config.TOG_STATE_TYPE, "0");
//                BaseApplication.toast("aaaaaaaaaaaa");
//                HttpClient.instance().setroomtrouble(MainTabIndexFragment.list.get(MainTabIndexFragment.pos).getRoom_id(), "0", new NetworkRequestSetFreeResult());
//                str_true = 3;
////                BaseApplication.toast("已关闭" + AppContext.getProperty(Config.TOG_STATE_TYPE));
//            }
//        }
//    };

    private String strtype;
    private void getDialog(int strInfo){
        final Dialog dialog=new Dialog(SetFreeActivity.this,R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.dialog_setfree, null);
        TextView tv_Order =  (TextView)view.findViewById(R.id.tv_Order);
        dialog.setContentView(view);

        tv_Order.setText(strInfo);
        view.findViewById(R.id.tv_IKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
        togStateType.setOnClickListener(toglistener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppManager.getAppManager().finishActivity();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(NewDoorFragment.list.size()==0){
            //无数据
            return;
        }
        HttpClient.instance().get_room_trouble(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), new NetworkRequestResultStatus());
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_free;
    }
}
