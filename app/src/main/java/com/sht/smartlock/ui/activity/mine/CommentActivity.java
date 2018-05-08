package com.sht.smartlock.ui.activity.mine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

public class CommentActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private EditText editCommentText;
    private Button btnComment;
    private String strHotel_id,strHotel_caption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setOnClickLister();
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        editCommentText = (EditText)findViewById(R.id.editCommentText);
        btnComment = (Button)findViewById(R.id.btnComment);

        Bundle bundle = getIntent().getExtras();
        strHotel_id = bundle.getString("hotel_id");
        strHotel_caption = bundle.getString("hotel_caption");
        if(!strHotel_caption.equals("")||!strHotel_caption.equals("null")){
            tvTitlePanel.setText(strHotel_caption);
        }
//        LogUtil.log("hotel_caption="+bundle.getString("hotel_caption"));
    }

    private void setOnClickLister(){
        btnComment.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnComment:
                String editCommentInfo = editCommentText.getText().toString();
                if(strHotel_id.equals("")||strHotel_id.equals("null")){
                    return;
                }
                if(!editCommentInfo.equals("")&&!editCommentInfo.equals("null")){
                    HttpClient.instance().addhotel_comment(strHotel_id,editCommentInfo,new NetworkRequestAddComment());
                }else{
                    BaseApplication.toast("请输入要点评的内容");
                }
                break;
            case R.id.btn_cancle:
                 finish();
                break;
        }
    }


    class  NetworkRequestAddComment extends HttpCallBack {
        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext, "正在发布..");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if(responseBean.isFailure()){
                BaseApplication.toast(responseBean.toString());
            }
            try{
                JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                if(jsonObject.getString("result").equals("true")){
                    BaseApplication.toast("发布成功");
                }else if(jsonObject.getString("result").equals("false")){
                    BaseApplication.toast("发布失败");
                }
            }catch (Exception e){
                LogUtil.log(e.toString());
            }


        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
