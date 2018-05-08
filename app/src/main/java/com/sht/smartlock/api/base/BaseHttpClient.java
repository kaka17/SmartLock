package com.sht.smartlock.api.base;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;

import java.util.concurrent.ThreadPoolExecutor;

public class BaseHttpClient {
    public static final Integer HTTP_TIMEOUT = 30000;

    private static AsyncHttpClient mClient = new AsyncHttpClient();

    static {
        mClient.setTimeout(HTTP_TIMEOUT);
        
      //  mClient.setUserAgent("");
      //  mClient.setMaxConnections(100);
      // mClient.setThreadPool(new ThreadPoolExecutor(Integer.MAX_VALUE,));
    }

    public static void post(String url, AsyncHttpResponseHandler handler) {
        post(url, null, handler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        mClient.post(url, params, handler);
    }

    public static void post(Context context, String url, StringEntity entity, String contentType, AsyncHttpResponseHandler handler) {
        mClient.post(context, url, entity, contentType, handler);
    }

    public static void get(String url, AsyncHttpResponseHandler handler) {
        get(url, null, handler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        mClient.get(url, params, handler);
    }

    public static void cancel(Context context) {
        if (mClient == null) {
            return;
        }
        mClient.cancelRequests(context, true);
    }
}
