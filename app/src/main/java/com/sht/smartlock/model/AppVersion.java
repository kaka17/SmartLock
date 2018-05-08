package com.sht.smartlock.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2015/10/10.
 */
public class AppVersion {
     private static String delCache;

    public static String getDelCache() {
        return delCache;
    }

    public static void setDelCache(String delCache) {
        AppVersion.delCache = delCache;
    }
}
