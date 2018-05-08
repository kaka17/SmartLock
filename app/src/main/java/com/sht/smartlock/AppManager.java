package com.sht.smartlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (activityStack.get(i).getClass().equals(cls)) {
                activityStack.get(i).finish();
            }
        }
    }

    /**
     * 除了指定类名结束其他的Activity
     */
    public void finishOtherActivity(Class<?> cls) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && !activityStack.get(i).getClass().equals(cls)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

//    public void finishToActivity(Class<?> cls) {
//
//        for (int i = activityStack.size() - 1; i >= 0; i--) {
//            if (null != activityStack.get(i)) {
//                if (!activityStack.get(i).getClass().equals(cls)) {
//                    activityStack.get(i).finish();
//                    activityStack.remove(activityStack.get(i));
//                } else {
//                    break;
//                }
//            }
//        }
//
//    }

    public void finishToActivity(Class<?>... cls) {

        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (null != activityStack.get(i)) {
                for (int j = 0; j < cls.length; j++) {
                    if (activityStack.get(i).getClass().equals(cls[j])) {
                        return;
                    }
                }
                activityStack.get(i).finish();
                activityStack.remove(activityStack.get(i));
            }
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}