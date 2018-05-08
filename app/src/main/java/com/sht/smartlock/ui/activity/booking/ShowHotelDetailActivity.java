package com.sht.smartlock.ui.activity.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.umeng.analytics.social.UMSocialService;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.bean.SocializeConfig;
//import com.umeng.socialize.bean.SocializeEntity;
//import com.umeng.socialize.controller.UMServiceFactory;
//import com.umeng.socialize.controller.UMSocialService;
//import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.MailShareContent;
//import com.umeng.socialize.media.QQShareContent;
//import com.umeng.socialize.media.QZoneShareContent;
//import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.sso.EmailHandler;
//import com.umeng.socialize.sso.QZoneSsoHandler;
//import com.umeng.socialize.sso.SinaSsoHandler;
//import com.umeng.socialize.sso.SmsHandler;
//import com.umeng.socialize.sso.UMQQSsoHandler;
//import com.umeng.socialize.sso.UMSsoHandler;
//import com.umeng.socialize.weixin.controller.UMWXHandler;
//import com.umeng.socialize.weixin.media.CircleShareContent;
//import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.hourroom.BookingHourRoomActivity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

public class ShowHotelDetailActivity extends BaseActivity implements View.OnClickListener {
    private AlertDialog.Builder alertDialog;
    PullToRefreshWebView ptrWebView;
    WebView wvHotelDetail;
    ImageView tvCollect, tvShare;
    TextView tvTitle;
    ImageView tvGoBack;
    RelativeLayout lyBookingNow;
    boolean isHourRoom = false;
    String strHotelId, strHotelURL, strHotelCaption;
    String mRoomMode = Config.ROOM_MODE_ALL;//房间显示模式，选房间的时候用，默认是全模式显示

    private int is_collect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
    }


    private void InitView() {
        tvCollect = (ImageView) findViewById(R.id.collect);
        tvShare = (ImageView) findViewById(R.id.share);
        tvGoBack = (ImageView) findViewById(R.id.goBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        lyBookingNow = (RelativeLayout) findViewById(R.id.lyBookingNow);
        tvCollect.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);

        lyBookingNow.setOnClickListener(this);
        ptrWebView = (PullToRefreshWebView) findViewById(R.id.wvHotelDetail);
        ptrWebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
            @Override
            public void onRefresh(PullToRefreshBase<WebView> refreshView) {
                wvHotelDetail.loadUrl(strHotelURL);
            }
        });
        wvHotelDetail = ptrWebView.getRefreshableView();
        wvHotelDetail.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = wvHotelDetail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
       webSettings.setUseWideViewPort(true);//关键点
       webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
      //  webSettings.setDisplayZoomControls(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
         webSettings.setSupportZoom(true);//支持缩放
       webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        Log.d("maomao", "densityDpi = " + mDensity);
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        Intent i = getIntent();
        strHotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        strHotelURL = i.getStringExtra(Config.KEY_HOTEL_URL);
        strHotelCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);
        mRoomMode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        is_collect = i.getIntExtra(Config.KEY_HOTEL_IS_COLLECT, 0);
        if (is_collect == 1) {
            tvCollect.setImageResource(R.drawable.collect);
        } else {
            tvCollect.setImageResource(R.drawable.un_collect);
        }
        wvHotelDetail.loadUrl(strHotelURL);
        //设置视图客户端
        wvHotelDetail.setWebViewClient(new MyWebViewClient());
        wvHotelDetail.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBookingNow:
                Intent i;

                if (AppContext.getShareUserSessinid() != null) {
                    i = new Intent(mContext, BookingHourRoomActivity.class);
                    i.putExtra(Config.KEY_HOTEL_ID, strHotelId);
                    i.putExtra(Config.KEY_HOTEL_CAPTION, strHotelCaption);
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE, mRoomMode);
                    startActivity(i);
                } else {
                    i = new Intent(mContext, LoginByNameActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.share:
//                mController.openShare(this, false);
                new ShareAction(ShowHotelDetailActivity.this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                        .withTitle("手机开门，轻松快捷")
                        .withText("我在用来住吧开门软件，你也来试试吧!")
                        .withMedia(new UMImage(ShowHotelDetailActivity.this, R.drawable.icon))
                        .withTargetUrl("http://www.smarthoteltech.com/")
                        .setCallback(umShareListener)
                        .open();


                break;
            case R.id.collect:
                if (AppContext.getShareUserSessinid() != null) {
                    if (is_collect == 0) {
                        HttpClient.instance().addHotelFavorites(strHotelId, new HttpCallBack() {
                            @Override
                            public void onSuccess(ResponseBean responseBean) {
                                ProgressDialog.disMiss();
                                if (responseBean.optString("result").equals("true")) {
                                    toastSuccess(getString(R.string.operate_success));
                                    tvCollect.setImageResource(R.drawable.collect);
                                    is_collect = 1;
                                } else {
                                    toastFail(R.string.operate_fail);
                                }
                            }

                            @Override
                            public void onFailure(String error, String message) {
                                super.onFailure(error, message);
                                ProgressDialog.disMiss();
                                toastFail(getString(R.string.operate_fail));
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                ProgressDialog.show(mContext, R.string.on_loading);
                            }
                        });
                    } else {
                        HttpClient.instance().delHotelFavorites(strHotelId, new HttpCallBack() {
                            @Override
                            public void onSuccess(ResponseBean responseBean) {
                                ProgressDialog.disMiss();
                                if (responseBean.optString("result").equals("true")) {
                                    toastSuccess(getString(R.string.operate_success));
                                    tvCollect.setImageResource(R.drawable.un_collect);
                                    is_collect = 0;
                                } else {
                                    toastFail(getString(R.string.operate_fail));
                                }
                            }

                            @Override
                            public void onFailure(String error, String message) {
                                super.onFailure(error, message);
                                ProgressDialog.disMiss();
                                toastFail(getString(R.string.operate_fail));
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                ProgressDialog.show(mContext, R.string.on_loading);
                            }
                        });
                    }
                } else {
                    i = new Intent(mContext, LoginByNameActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.goBack:
                finish();
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(ShowHotelDetailActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ShowHotelDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShowHotelDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShowHotelDetailActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        com.umeng.socialize.utils.Log.d("result", "onActivityResult");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvHotelDetail.canGoBack()) {
            wvHotelDetail.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")||url.startsWith("sms:")) {
                   //  System.out.println("paramss:"+url);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ProgressDialog.disMiss();
            ptrWebView.onRefreshComplete();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            ptrWebView.onRefreshComplete();
            ProgressDialog.disMiss();
            //  toastFail(getString(R.string.load_fail));
            alertDialog = new AlertDialog.Builder(ShowHotelDetailActivity.this);
            alertDialog.setTitle("ERROR");
            alertDialog.setMessage(description);
//            alertDialog.setPositiveButton()
//            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // TODO Auto-generated method stub
//                }
//            });
            alertDialog.show();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_hotel_detail;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
