package com.sht.smartlock.phone.ui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.yuntongxun.ecdemo.ui in ECDemo_Android
 * Created by Jorstin on 2015/6/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface ActivityTransition {
    int value();
}
