package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MyselfChenkinActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_Content;
    private TextView tv_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_myself_chenkin);
        onBack();
        initView();
        setOnClickListener();
    }
    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_myself_chenkin;
    }

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
        tv_Content = (TextView) findViewById(R.id.tv_Content);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
    }

    private void setOnClickListener(){
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sure:
                if(NewDoorFragment.list.size()==0){
                    //无数据
                    return;
                }
                service_Myself_Checkin(NewDoorFragment.list.get(NewDoorFragment.pos).getBook_id(),NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                break;
        }
    }

    //自助 订单号 ；房间id
    private void service_Myself_Checkin(String book_id,String room_id){
        HttpClient.instance().service_Myself_Checkin(book_id, room_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog("chenkin"+responseBean.toString());
                try {
                    JSONObject object=new JSONObject(responseBean.toString());
                    String result=object.getString("result");
                    //自助chenkin成功
                    if (result.equals("1")){
                        BaseApplication.toast(R.string.Toast_Success_Chenkin);
                        finish();
                    }else if(result.equals("-1")){
                        BaseApplication.toast(R.string.Chenkin_failure01);
                    }else if(result.equals("-2")){
                        BaseApplication.toast(R.string.Chenkin_failure02);
                    }else if(result.equals("-3")){
                        BaseApplication.toast(R.string.Chenkin_failure03);
                    }else if(result.equals("-4")){
                        BaseApplication.toast(R.string.Chenkin_failure04);
                    }else if(result.equals("-5")){
                        BaseApplication.toast(R.string.Chenkin_failure05);
                    }else if(result.equals("-6")){
                        BaseApplication.toast(R.string.Chenkin_failure06);
                    }else if(result.equals("-7")){
                        BaseApplication.toast(R.string.Chenkin_failure07);
                    }else if(result.equals("-8")){
                        BaseApplication.toast(R.string.Chenkin_failure08);
                    }else if(result.equals("2")){
                        BaseApplication.toast("请到前台验证身份，办理入住");
                    }else {
                        BaseApplication.toast("自助chinkin失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
