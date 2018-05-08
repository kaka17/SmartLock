package com.sht.smartlock.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.model.Version;
import com.sht.smartlock.util.DeviceUtil;
import com.sht.smartlock.util.FileUtil;
import com.sht.smartlock.util.MyPathUtil;
import com.sht.smartlock.util.download.DownloadConstant;
import com.sht.smartlock.util.download.IDownloadCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szfore_chenjl on 2015/3/31.
 */
public class DownloadService extends Service {
    private static final int NOTIFY_ID = 0;
    private int mProgress;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private boolean mCanceled;
    private Version mVersion;
    private IDownloadCallBack mCallBack;
    private boolean mServiceIsDestroy;
    private Context mContext = this;
    private DownloadBinder mBinder;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    AppContext.instance().setDownload(false);
                    mNotificationManager.cancel(NOTIFY_ID);
//                    DeviceUtil.installAPK(mContext, new File(Config.UPDATE_APK_PAHT + "/" + FileUtil.getFileName("http://www.dreamboxs.cn/pub/dl.php")));
                    DeviceUtil.installAPK(mContext, new File(MyPathUtil.getInstance().getApkPath() + "/" + FileUtil.getFileName(mVersion.getLink()+mVersion.getVision())));
                    mCallBack.onResult(DownloadConstant.STATUS_SUCCESS, null);
                    break;
                case 2:
                    AppContext.instance().setDownload(false);
                    mNotificationBuilder.setOngoing(false);
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
                case 1:
                    int rate = msg.arg1;
                    AppContext.instance().setDownload(true);
                    if (rate < 100) {
                        mNotificationBuilder.setProgress(100, rate, false);
                    } else {
                        // 下载完毕后变换通知形式  暂时屏蔽20151008 by liaoc
                        mNotificationBuilder.setOngoing(false);
//                        Intent intent = new Intent(mContext, TestActivity.class);
//                        // 告知已完成
//                        intent.putExtra("completed", "yes");
//                        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
//                        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//                        mNotificationBuilder.build().setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", contentIntent);
                        mServiceIsDestroy = true;
                        stopSelf();// 停掉服务自身
                    }
                    mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
                    break;
            }
        }
    };

    public class DownloadBinder extends Binder {
        public void start() {
            String apkFile = MyPathUtil.getInstance().getApkPath() + "/" + FileUtil.getFileName(mVersion.getLink()+mVersion.getVision());
            // 检测是否已经下载过
            mCallBack.onResult(DownloadConstant.STATUS_CHECKEXISTS, null);


            if(FileUtil.checkFilePathExists(apkFile)){
                mCallBack.onResult(DownloadConstant.STATUS_FILEEXISTS, apkFile);
                AppContext.instance().setDownload(false);
                return;
            }

            if (downLoadThread == null || !downLoadThread.isAlive()) {
                setProgress(0);
                setUpNotification();
                new Thread() {
                    public void run() {
                        startDownload();
                    }
                }.start();
            }
        }

        public void cancel() {
            mCanceled = true;
        }

        public int getProgress() {
            return mProgress;
        }

        public void setProgress(int progress) {
            DownloadService.this.mProgress = progress;
        }

        public boolean isCanceled() {
            return mCanceled;
        }

        public boolean serviceIsDestroy() {
            return mServiceIsDestroy;
        }

        public void cancelNotification() {
            mHandler.sendEmptyMessage(2);
        }

        public void addCallback(IDownloadCallBack callback) {
            DownloadService.this.mCallBack = callback;
        }
    }

    private void startDownload() {
        mCanceled = false;
        downloadApk();
    }


    private void setUpNotification() {
        try {
            PackageManager pm = getPackageManager();
            Drawable icon = pm.getApplicationIcon(mContext.getPackageName());
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            String contentTitle = mContext.getApplicationContext().getString(R.string.app_name);
            int smallIcon = mContext.getApplicationInfo().icon;
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (mNotificationBuilder == null) {
                mNotificationBuilder = new NotificationCompat.Builder(mContext)
                        // .setLargeIcon(largeIcon)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(contentTitle)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setOngoing(true);
            }
            mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        int icon = R.drawable.ic_launcher;
//        CharSequence tickerText = "开始下载";
//        long when = System.currentTimeMillis();
//        mNotification = new Notification(icon, tickerText, when);
//        // 放置在"正在运行"栏目中
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
//
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_download);
//        contentView.setTextViewText(R.id.name, getString(R.string.download_notification_desc_head, mContext.getString(R.string.app_name)));
//        // 指定个性化视图
//        mNotification.contentView = contentView;
//        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    private Thread downLoadThread;

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private int lastRate = 0;

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {

            try {
//                System.out.println("url:" + HttpClient.getDownloadUrl(mVersion.getAddress()));
                URL url = new URL(mVersion.getLink());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15 * 1000);
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                String apkFile = MyPathUtil.getInstance().getApkPath() + "/" + FileUtil.getFileName(mVersion.getLink()+mVersion.getVision());
                File file = new File(Config.UPDATE_APK_PAHT);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                Map<String, Object> downloadInfoMap = new HashMap<>();
                downloadInfoMap.put("fileSize", getAppSize(length));
                downloadInfoMap.put("filePath", apkFile);
                mCallBack.onResult(DownloadConstant.STATUS_START, downloadInfoMap);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    mProgress = (int) (((float) count / length) * 100);
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = mProgress;
                    if (mProgress >= lastRate + 1) {
                        mHandler.sendMessage(msg);
                        lastRate = mProgress;
                        if (mCallBack != null)
                            mCallBack.onResult(DownloadConstant.STATUS_DOWNLOADING, mProgress);
                    }
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(0);
                        // 下载完了，cancelled也要设置
                        mCanceled = true;
                        lastRate = 0;
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!mCanceled);// 点击取消就停止下载.
                lastRate = 0;
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                lastRate = 0;
                mCallBack.onResult(DownloadConstant.STATUS_FAIL, null);
            } catch (IOException e) {
                lastRate = 0;
                mCallBack.onResult(DownloadConstant.STATUS_FAIL, null);
            }
        }
    };

    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

    public static final int MB_2_BYTE = 1024 * 1024;
    public static final int KB_2_BYTE = 1024;

    /**
     * @param size
     * @return
     */
    public static CharSequence getAppSize(long size) {
        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mVersion = (Version) intent.getSerializableExtra("version");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppContext.instance().setDownload(false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
