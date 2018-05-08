package com.sht.smartlock.api.base;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.http.Header;
import org.json.JSONObject;

public class ResponseHandler extends AsyncHttpResponseHandler {
    private HttpCallBack mHandler;
    private Context context;
    public final int INVALID_REQUEST = -32600;//无效的请求
    public final int METHOD_NOT_FOUND = -32601;//请求方法不存在
    public final int INVALID_PARAMS = -32602;//无效的参数
    public final int INTERNAL_ERROR = -32603;//内部错误
    public final int PARSE_ERROR = -32700;//请求语句错误
    public final int CLASS_NOT_FOUND = -32701;//请求的类不存在
    public final int NAME_OR_PWD_ISEMPTY = -32702;//用户名和密码错误
    public final int DATA_ISEMPTY = -32703;//搜索结果为空
    public final int VALIDATION_FAILURE = -32704;//身份验证失败或无效
    public final int CHINKINFUAIL = -32705;//自助checkin失败
    public final int LOCK_ISEMPTY = -32706;//没有酒店房间
    public final int VALIDATION_CHARE = -32707;//申请还未通过审核

    public ResponseHandler(Context context, HttpCallBack mHandler) {
        this.context = context;
        this.mHandler = mHandler;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (context == null || !AppContext.instance().isNetworkConnected()) {
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mHandler.onStart();
            }
        });
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (context == null) {
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mHandler.onFinish();
            }
        });
    }

    private static final int DELAYTIME = 700;

    @Override
    public void onFailure(final int statusCode, Header[] headers, final byte[] responseBody, final Throwable arg3) {
        if (context == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("paramss error " + statusCode + " " + arg3.toString());
                mHandler.onFailure("" + statusCode, arg3.toString());//+responseBody.toString(), arg3.toString());
                //Toast.makeText(AppManager.getAppManager().currentActivity(), "error code:" + statusCode + "\n" + arg3.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(AppManager.getAppManager().currentActivity(), R.string.net_error_and_try_later, Toast.LENGTH_LONG).show();
            }
        }, DELAYTIME);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, final byte[] responseBody) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (context == null) {
                        return;
                    }
                    String result = new String(responseBody);
                    LogUtil.log("paramss onsuccess " + result);
                    //   Log.e("paramss onsuccess",result);
                    JSONObject s = new JSONObject(result);
                    if (!s.optString("error").equals("")) {
                        JSONObject err = s.getJSONObject("error");
                        int code = err.getInt("code");
                        if (code == VALIDATION_FAILURE) {
                            //       ProgressDialog.show(AppManager.getAppManager().currentActivity(),result+"\n\n");

                            AppContext.setProperty(Config.SHARE_USER_ACCOUNT, null);
                            AppContext.setProperty(Config.SHARE_USERSESSIONID, null);
                            AppContext.setProperty(Config.SHARE_USERPWD, null);
                            AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, null);
                            AppContext.setProperty(Config.KEY_USER_NICKNAME, null);
                            AppContext.setProperty(Config.SHARE_USER_Name,null);
                            Intent i = new Intent(context, LoginByNameActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(i);
                        }else if(code == INVALID_REQUEST){//无效请求
                            AppContext.toast(R.string.ERROR600);
                        }else if(code == METHOD_NOT_FOUND){//请求方法不存在
                            AppContext.toast(R.string.ERROR601);
                        }else if(code == INVALID_PARAMS){//无效的参数
                            AppContext.toast(R.string.ERROR602);
                        }else if(code == INTERNAL_ERROR){//内部错误
                            AppContext.toast(R.string.ERROR603);
                        }else if(code == PARSE_ERROR){//请求语句错误
                            AppContext.toast(R.string.ERROR700);
                        }else if(code == CLASS_NOT_FOUND){//请求的类不存在
                            AppContext.toast(R.string.ERROR701);
                        }else if(code == NAME_OR_PWD_ISEMPTY){//用户名和密码错误
                            AppContext.toast(R.string.ERROR702);
                        }else if(code == DATA_ISEMPTY){//搜索结果为空
                            AppContext.toast(R.string.ERROR703);
                        }else if(code == CHINKINFUAIL){//自助checkin失败
                            AppContext.toast(R.string.ERROR705);
                        }else if(code == LOCK_ISEMPTY){//没有酒店房间
                            AppContext.toast(R.string.ERROR706);
                        }else if(code == VALIDATION_CHARE){///申请还未通过审核
                            AppContext.toast(R.string.ERROR707);
                        }else{//
                            AppContext.toast(err.getString("message"));
                        }
                    }
                    mHandler.onSuccess(new ResponseBean(result));
                } catch (Exception e) {
                    e.printStackTrace();
//                    ProgressDialog.disMiss();
                    String result = new String(responseBody);
                    LogUtil.log("errors " + result);
//
//                    if (AppManager.getAppManager().currentActivity() != null) {
//                        ProgressDialog.show(AppManager.getAppManager().currentActivity(), result + "\n\n" + e.toString());
//                    }
                    ///  Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                    // Toast.makeText(context,"服务器君走神了~~",Toast.LENGTH_SHORT).show();
                }
            }
        }, DELAYTIME);
    }
}
