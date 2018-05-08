package com.sht.smartlock.ui.activity.booking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2015/9/28.
 */
public class RoomDetailPopWindow extends PopupWindow {
    private View mMenuView;
    private WebView wvRoomDetail;
    HotelRoom mListHotelRoom;
    ImageView ivErrorPage;
    Context mContext;

    public RoomDetailPopWindow(Context context, HotelRoom hotelRoom) {
        super(context);
        this.mContext = context;
        mListHotelRoom = hotelRoom;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.activity_booking_room_pop, null);
        ivErrorPage = (ImageView) mMenuView.findViewById(R.id.wvErrorPage);
        wvRoomDetail = (WebView) mMenuView.findViewById(R.id.wvRoomDetail);
        final String url = hotelRoom.getContent();
        //  final String url = "http://192.168.1.84/admintest/web/58/58_391111.html";
        //   url="http://www.baidu.com";
        wvRoomDetail.setWebViewClient(new MyWebViewClient());


        setContentView(mMenuView);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        //setAnimationStyle(R.style.Animation_AppCompat_Dialog);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.layout_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        if (url.equals("0")) {
            wvRoomDetail.setVisibility(View.GONE);
            ivErrorPage.setVisibility(View.VISIBLE);
            System.out.println("paramss 404 found 00000");
            return;
        }
        wvRoomDetail.loadUrl(url);
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            ProgressDialog.disMiss();
            System.out.println("paramss onPageFinished");

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            wvRoomDetail.setVisibility(View.GONE);
            ivErrorPage.setVisibility(View.VISIBLE);
            System.out.println("paramss onReceivedError");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


}
