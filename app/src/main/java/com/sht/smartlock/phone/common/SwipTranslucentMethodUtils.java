package com.sht.smartlock.phone.common;

import android.app.Activity;
import android.app.ActivityOptions;
import android.util.Log;


import com.sht.smartlock.phone.common.utils.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * com.yuntongxun.ecdemo.common in ECDemo_Android
 * Created by Jorstin on 2015/6/25.
 */
public class SwipTranslucentMethodUtils {

    private static final String TAG = "ECSDK_Demo.SwipTranslucentMethodUtils";
    private SwipTranslucentMethodUtils() {
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity , MethodInvoke.SwipeInvocationHandler handler) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }

            //if(translucentConversionListenerClazz != null) {
            Object proxy = Proxy.newProxyInstance(translucentConversionListenerClazz.getClassLoader(), new Class[] {translucentConversionListenerClazz}, handler);
            //}
            LogUtil.d(TAG, "proxy " + proxy);
            if(!SDKVersionUtils.isGreaterorEqual(21)) {
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", new Class[] {translucentConversionListenerClazz});
                method.setAccessible(true);
                method.invoke(activity, new Object[]{proxy});
            } else {
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", new Class[] {translucentConversionListenerClazz , ActivityOptions.class});
                method.setAccessible(true);
                method.invoke(activity, new Object[]{proxy ,null});
            }

        } catch (Throwable t) {
            LogUtil.e(TAG, Log.getStackTraceString(t));
        }
    }

}
