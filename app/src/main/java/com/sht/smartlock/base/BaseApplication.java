package com.sht.smartlock.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.sht.smartlock.Config;
import com.sht.smartlock.util.LogUtil;

public class BaseApplication extends MultiDexApplication {
    private static String PREF_NAME = Config.APP + ".pref";

    public static Context _context;
    public static Resources _resource;

    private static final String KEY_SCREEN_WIDTH = "screen_width";
    private static final String KEY_SCREEN_HEIGHT = "screen_height";


    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        _resource = _context.getResources();
    }

    public static SharedPreferences getPreferences() {
        return context().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static int[] getDisplaySize() {
        return new int[]{getPreferences().getInt(KEY_SCREEN_WIDTH, 480),
                getPreferences().getInt(KEY_SCREEN_HEIGHT, 854)};
    }

    public static void saveDisplaySize(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(KEY_SCREEN_WIDTH, displaymetrics.widthPixels);
        editor.putInt(KEY_SCREEN_HEIGHT, displaymetrics.heightPixels);
        editor.putFloat("density", displaymetrics.density);
        editor.commit();
        LogUtil.log("分辨率:" + displaymetrics.widthPixels + "x" + displaymetrics.heightPixels + " 密度:" + displaymetrics.density + " " + displaymetrics.densityDpi);
    }

    public static synchronized BaseApplication context() {
        return (BaseApplication) _context;
    }

    public static Resources resources() {
        return _resource;
    }

    public static void toast(int message){
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String message){
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }
    public static void toastLong(String message){
        Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
    }

    public static void toastFail(String message) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastFail(int message) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastSuccess(String message) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastSuccess(int message) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }
}
