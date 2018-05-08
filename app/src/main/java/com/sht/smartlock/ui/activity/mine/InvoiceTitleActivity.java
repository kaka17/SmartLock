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

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvoiceTitleActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_btncancle;
    private TextView tvBtnTitlePanel;
    private Button btnPanelSave;
    private EditText editInvoiceTitle;

    private ArrayList<HashMap<String, String>> listItem;
    private  String str ="";
    private  String strJson = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setOnClickLister();

        LogUtil.log("a1a="+AppContext.getProperty(Config.INVOICETITLE));

            if(AppContext.getProperty(Config.INVOICETITLE)!=null ){
            if(!AppContext.getProperty(Config.INVOICETITLE).equals("")){
                JsonData(AppContext.getProperty(Config.INVOICETITLE));
            }
        }else{
                AppContext.setProperty(Config.INVOICETITLE,"");
            }
    }

    private String result;
    private void findviewbyid(){
        btn_btncancle = (ImageView)findViewById(R.id.btn_btncancle);
        tvBtnTitlePanel = (TextView)findViewById(R.id.tvBtnTitlePanel);
        btnPanelSave = (Button)findViewById(R.id.btnPanelSave);
        editInvoiceTitle = (EditText)findViewById(R.id.editInvoiceTitle);

        listItem = new ArrayList<HashMap<String, String>>();
        tvBtnTitlePanel.setText("发票抬头");
//        if(!AppContext.getProperty(Config.INVOICETITLE).equals("")&&){}
        HttpClient.instance().get_invoice_title_list(new HttpCallBack() {

            @Override
            public void onFailure(String error, String message) {
                ProgressDialog.disMiss();
                super.onFailure(error, message);
            }

            @Override
            public void onFinish() {
                ProgressDialog.disMiss();
                super.onFinish();
            }

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在加载..");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                LogUtil.log("title="+responseBean.toString());
                JsonData(responseBean.toString());
            }
        });
    }

    private void setOnClickLister(){
        btn_btncancle.setOnClickListener(this);
        btnPanelSave.setOnClickListener(listener);
    }

    private void JsonData(String invioceTitle){
        try {
            JSONTokener jsonParser = new JSONTokener(invioceTitle);
            JSONObject registererjson = (JSONObject) jsonParser.nextValue();
            LogUtil.log(registererjson.getString("result"));
            result = registererjson.getString("result");

            JSONArray jArray = new JSONArray(result);
            listItem.clear();
            str = "";
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", json_data.getString("title"));
                listItem.add(map);
            }
            LogUtil.log("title" + listItem.size());
            if(listItem.size() > 0){
                for(int j = 0;j<listItem.size();j ++){
                    if(!listItem.get(j).get("title").toString().equals("")&&
                            !listItem.get(j).get("title").toString().equals("null")){
//                                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//                                Matcher m = p.matcher(str);
//                                dest = m.replaceAll("");
                        str += listItem.get(j).get("title")+"\n";
                    }
//                            LogUtil.log(str);
                }
                editInvoiceTitle.setText(str.trim());
                AppContext.setProperty(Config.INVOICETITLE,invioceTitle);
            }
        } catch (Exception e) {
            LogUtil.log(e.toString());
        }
    }


    @Override
    public void onClick(View view) {
        finish();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnPanelSave:
                    final List<JSONObject> list_json = new ArrayList<JSONObject>();
                    String[] sourceStrArray = editInvoiceTitle.getText().toString().split("\n");


                    for (int i = 0; i < sourceStrArray.length; i++) {
                        JSONObject jsonObject = new JSONObject();
                        try{
                            if(!sourceStrArray[i].toString().trim().equals("")&&
                                    !sourceStrArray[i].toString().trim().equals("null")){
//                                System.out.println("arrys="+sourceStrArray[i]);
                                jsonObject.put("title",sourceStrArray[i].toString().trim());
                                list_json.add(jsonObject);
                            }else{
                                AppContext.setProperty(Config.INVOICETITLE,"");
                            }
                        }catch (Exception e){
                          LogUtil.log(e.toString());
                        }
//                        System.out.println("arry="+sourceStrArray[i]);
                    }
//                     LogUtil.log("qa=="+list_json.toString());
//                    LogUtil.log("qa=="+sourceStrArray.toString());
                    HttpClient.instance().update_invoice_title_list(list_json.toString().trim(), new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
//                            LogUtil.log(responseBean.toString());
//                            BaseApplication.toast("");
                            if (!responseBean.toString().equals("") || !responseBean.toString().equals("null")) {
                                try {
                                    JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                                    if (jsonObject.getString("result").equals("true")) {
                                        BaseApplication.toast("设置成功！");
                                        finish();
                                    } else {
                                        BaseApplication.toast("设置失败！");
                                    }
                                } catch (Exception e) {
                                    LogUtil.log(e.toString());
                                }
                            }
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invoice_title;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
