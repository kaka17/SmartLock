/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.sht.smartlock.phone.common;

import android.os.Handler;

/**
 * Created by Jorstin on 2015/3/13.
 */
public abstract interface CallCommandHandler {
    public abstract void postCommand(Runnable paramRunnable);
    public abstract void destroy();
    public abstract Handler getCommandHandler();
}

