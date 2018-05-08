package com.sht.smartlock.phone.ui.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.ui.ECSuperActivity;

import java.util.HashMap;



public class WebAboutActivity
//        extends ECSuperActivity implements View.OnClickListener, PlatformActionListener
{

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initResViews();
//        ShareSDK.initSDK(this);

//        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
//                R.drawable.btn_style_green, null,
//                "分享",
//                "下载", null, this);

//    }
    ShareDialog  shareDialog;
//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.btn_left:
////                hideSoftKeyboard();
////                finish();
//                break;
//
//
//            case R.id.text_right:
//               shareDialog = new ShareDialog(this);
//                shareDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        shareDialog.dismiss();
//
//                    }
//                });
//                shareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> arg0, View arg1,
//                                            int arg2, long arg3) {
//                        HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
//                        if (item.get("ItemText").equals("微博")) {
//
//
//
//                        } else if (item.get("ItemText").equals("微信好友")) {
//
//                            ShareParams sp = new ShareParams();
//                            sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
//                            sp.setTitle("容联云通讯");  //分享标题
//                            sp.setText("国内顶级云通讯平台,功能全,技术强,集成快");   //分享文本
//                            sp.setImageUrl("http://www.yuntongxun.com/front/images/im_img4.png");//网络图片rul
//                            sp.setUrl("http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");   //网友点进链接后，可以看到分享的详情
//
//                            //3、非常重要：获取平台对象
//                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//                            wechat.setPlatformActionListener(WebAboutActivity.this); // 设置分享事件回调
//                            // 执行分享
//                            wechat.share(sp);
//
//
//                        } else if (item.get("ItemText").equals("微信朋友圈")) {
//                            //2、设置分享内容
//                            ShareParams sp = new ShareParams();
//                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
//                            sp.setTitle("容联云通讯");  //分享标题
//                            sp.setText("国内顶级云通讯平台,功能全,技术强,集成快");   //分享文本
//                            sp.setImageUrl("http://www.yuntongxun.com/front/images/im_img4.png");//网络图片rul
//                            sp.setUrl("http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");   //网友点进链接后，可以看到分享的详情
//
//                            //3、非常重要：获取平台对象
//                            Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
//                            wechatMoments.setPlatformActionListener(WebAboutActivity.this); // 设置分享事件回调
//                            // 执行分享
//                            wechatMoments.share(sp);
//
//                        } else if (item.get("ItemText").equals("QQ")) {
//
//
//                        }
//
//
//                        shareDialog.dismiss();
//
//                    }
//                });
//
//                break;
//
//            default:
//                break;
//        }
//
//    }


//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_web_about_url;
//    }
//
//    private WebView mWebView;
//
//    private void initResViews() {
//        mWebView = (WebView) findViewById(R.id.webview);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        // mWebView.getSettings().setPluginsEnabled(true);
//        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setSavePassword(false);
//        mWebView.getSettings().setSaveFormData(false);
//        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//        mWebView.getSettings().setGeolocationEnabled(true);
//        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//
//        mWebView.loadUrl("http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");
//
//    }
//
//
//
//
//    @Override
//    public void onCancel(Platform arg0, int arg1) {//回调的地方是子线程，进行UI操作要用handle处理
//        handler.sendEmptyMessage(5);
//
//    }
//
//    @Override
//    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {//回调的地方是子线程，进行UI操作要用handle处理
//          if (arg0.getName().equals(Wechat.NAME)) {
//            handler.sendEmptyMessage(1);
//        } else if (arg0.getName().equals(WechatMoments.NAME)) {
//            handler.sendEmptyMessage(3);
//        }
//
//    }
//
//    @Override
//    public void onError(Platform arg0, int arg1, Throwable arg2) {//回调的地方是子线程，进行UI操作要用handle处理
//        arg2.printStackTrace();
//        Message msg = new Message();
//        msg.what = 6;
//        msg.obj = arg2.getMessage();
//        handler.sendMessage(msg);
//    }
//
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    break;
//
//                case 2:
//                    Toast.makeText(getApplicationContext(), "微信分享成功", Toast.LENGTH_LONG).show();
//                    break;
//                case 3:
//                    Toast.makeText(getApplicationContext(), "微信朋友圈分享成功", Toast.LENGTH_LONG).show();
//                    break;
//                case 4:
//                    break;
//
//                case 5:
//                    Toast.makeText(getApplicationContext(), "取消分享", Toast.LENGTH_LONG).show();
//                    break;
//                case 6:
//                    Toast.makeText(getApplicationContext(), "分享失败" + msg.obj, Toast.LENGTH_LONG).show();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//    };
}
