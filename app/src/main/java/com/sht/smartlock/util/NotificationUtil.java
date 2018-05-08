package com.sht.smartlock.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.OtherOrderInfoActivity;

import java.io.File;

/**
 * Created by Administrator on 2017/5/18.
 */
public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;
    public NotificationUtil(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 普通的Notification
     */
    public void postNotification(String order_id,String bookingId) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, OtherOrderInfoActivity.class);  //需要跳转指定的页面
        intent.putExtra(Config.GTCIDBYISTRUE, true);
        intent.putExtra(Config.GTCIDBYJSOn, order_id);
        intent.putExtra(Config.GTBOOKINGID, bookingId);
        //设置 点击 消失
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.icon);// 设置图标
        builder.setContentTitle("来住吧");// 设置通知的标题
        builder.setContentText("您有1笔消费计入客房账单，可在‘我的订单’中查看");// 设置通知的内容
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        builder.setAutoCancel(true); //自己维护通知的消失
        builder.setTicker("您有一条新的通知");// 第一次提示消失的时候显示在通知栏上的
        builder.setOngoing(true);
        builder.setNumber(20);

        Notification notification = builder.build();
        /**
         * sound属性是一个 Uri 对象。 可以在通知发出的时候播放一段音频，这样就能够更好地告知用户有通知到来.
         * 如：手机的/system/media/audio/ringtones 目录下有一个 Basic_tone.ogg音频文件，
         * 可以写成： Uri soundUri = Uri.fromFile(new
         * File("/system/media/audio/ringtones/Basic_tone.ogg"));
         * notification.sound = soundUri; 我这里为了省事，就去了手机默认设置的铃声
         */
        Uri uri = Uri.fromFile(new File("/system/media/audio/ringtones/Basic_tone.ogg"));
        notification.sound = uri;
        /**
         * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
        long[] vibrates = { 0, 1000, 1000, 1000 };
        notification.vibrate = vibrates;

        notification.flags = Notification.FLAG_NO_CLEAR;  //只有全部清除时，Notification才会清除
        notificationManager.notify(0,notification);
    }

    /**
     * 普通的Notification
     */
    public void postNotification02(String text) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);  //需要跳转指定的页面
        //设置 点击 消失
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.icon);// 设置图标
        builder.setContentTitle("来住吧");// 设置通知的标题
        builder.setContentText(text);// 设置通知的内容
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        builder.setAutoCancel(true); //自己维护通知的消失
        builder.setTicker("您有一条新的通知");// 第一次提示消失的时候显示在通知栏上的
        builder.setOngoing(true);
        builder.setNumber(20);

        Notification notification = builder.build();
        /**
         * sound属性是一个 Uri 对象。 可以在通知发出的时候播放一段音频，这样就能够更好地告知用户有通知到来.
         * 如：手机的/system/media/audio/ringtones 目录下有一个 Basic_tone.ogg音频文件，
         * 可以写成： Uri soundUri = Uri.fromFile(new
         * File("/system/media/audio/ringtones/Basic_tone.ogg"));
         * notification.sound = soundUri; 我这里为了省事，就去了手机默认设置的铃声
         */
//        Uri uri = Uri.fromFile(new File("/system/media/audio/ringtones/Basic_tone.ogg"));
//        notification.sound = uri;
        /**
         * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
//        long[] vibrates = { 0, 1000, 1000, 1000 };
//        notification.vibrate = vibrates;

        notification.flags = Notification.FLAG_NO_CLEAR;  //只有全部清除时，Notification才会清除
        notificationManager.notify(0,notification);
    }

    /**
     * 使用下载的Notification,在4.0以后才能使用<p></p>
     * Notification.Builder类中提供一个setProgress(int max,int progress,boolean indeterminate)方法用于设置进度条，
     * max用于设定进度的最大数，progress用于设定当前的进度，indeterminate用于设定是否是一个确定进度的进度条。
     * 通过indeterminate的设置，可以实现两种不同样式的进度条，一种是有进度刻度的（true）,一种是循环流动的（false）。
     */
    public void postDownloadNotification() {
        final Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.icon)
                .setTicker("showProgressBar").setContentInfo("contentInfo")
                .setOngoing(true).setContentTitle("ContentTitle")
                .setContentText("ContentText");
        // 模拟下载过程
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress ;
                for (progress = 0; progress < 100; progress += 5) {
                    // 将setProgress的第三个参数设为true即可显示为无明确进度的进度条样式
                    builder.setProgress(100, progress, false);
                    notificationManager.notify(0, builder.build());
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("sleep failure");
                    }
                }
                builder.setContentTitle("Download complete")
                        .setProgress(0, 0, false).setOngoing(false);
                notificationManager.notify(0, builder.build());
            }
        }).start();
    }

    /**
     * 大视图通知在4.1以后才能使用，BigTextStyle<p></p>
     * ****************************************************<p></p>
     * Helper class for generating large-format notifications that include a lot of text.
     *
     * Here's how you'd set the <code>BigTextStyle</code> on a notification:
     * <pre class="prettyprint">
     * Notification notif = new Notification.Builder(mContext)
     *     .setContentTitle(&quot;New mail from &quot; + sender.toString())
     *     .setContentText(subject)
     *     .setSmallIcon(R.drawable.new_mail)
     *     .setLargeIcon(aBitmap)
     *     .setStyle(new Notification.BigTextStyle()
     *         .bigText(aVeryLongString))
     *     .build();
     * </pre>
     *
     * @see Notification#bigContentView
     */
    public void postBigTextNotification() {
        Notification.BigTextStyle textStyle = new Notification.BigTextStyle();
        textStyle.setBigContentTitle("大标题")
                // 标题
                .setSummaryText("SummaryText")
                .bigText("Helper class for generating large-format notifications" +
                        " that include a lot of text;  !!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Notification.Builder builder2 = new Notification.Builder(
                context);
        builder2.setSmallIcon(R.drawable.push);// 小图标
        // 大图标
        builder2.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.push));  //R.mipmap.close
        builder2.setTicker("showBigView_Text")
                .setContentInfo("contentInfo");
        builder2.setStyle(textStyle);
        builder2.setAutoCancel(true);

        notificationManager.notify(0, builder2.build());
    }

    /**
     * 大布局通知在4.1以后才能使用,大布局图片
     */
    public void postBigPictureNotification() {
        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.push));  //R.drawable.back

        Notification.Builder builder = new Notification.Builder(
                context);
        builder.setSmallIcon(R.drawable.push);// 小图标
        // 大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.push));
        builder.setTicker("showBigView_Picture")
                .setContentInfo("contentInfo");
        builder.setStyle(bigPictureStyle);
        builder.setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    /**
     * 大布局通知在4.1以后才能使用，InboxStyle
     */
    public void postInboxNotification() {
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        inboxStyle.setBigContentTitle("InboxStyle");
        inboxStyle.setSummaryText("Test");
        for(int i = 0 ; i < 10; i++){
            inboxStyle.addLine("new:" + i);
        }

        Notification.Builder builder5 = new Notification.Builder(
                context);
        builder5.setSmallIcon(R.drawable.push);// 小图标
        // 大图标
        builder5.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.icon));
        builder5.setTicker("showBigView_InboxStyle")
                .setContentInfo("contentInfo");
        builder5.setStyle(inboxStyle);
        builder5.setAutoCancel(true);

        notificationManager.notify(0, builder5.build());
    }

    /**
     * 自定义通知<p></p>
     *
     * 不设置notification.contentIntent = pendingIntent;则报如下异常：
     * android.app.RemoteServiceException:
     * Bad notification posted from package com.test.testandroid: Couldn't expand RemoteViews for: StatusBarNotification(
     * pkg=com.test.testandroid user=UserHandle{0} id=0 tag=null score=0 key=0|com.test.testandroid|0|null|10168|0: Notification
     * (pri=0 contentView=com.test.testandroid/0x7f040038 vibrate=null sound=null defaults=0x0 flags=0x10 color=0xff00aeff vis=PRIVATE))
     */
    public void postCustomNotification() {
        RemoteViews contentViews = new RemoteViews(context.getPackageName(),
                R.layout.mynotification);
        contentViews.setImageViewResource(R.id.imageNotifi,R.drawable.push);
        contentViews.setTextViewText(R.id.titleTV,"自定义通知标题");
        contentViews.setTextViewText(R.id.textTV,"自定义通知内容");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentViews.setOnClickPendingIntent(R.id.titleTV, pendingIntent);
        contentViews.setOnClickPendingIntent(R.id.textTV, PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.push);
        builder.setContentTitle("custom notification");
        builder.setContentText("custom test");
        builder.setTicker("custom ticker");
        builder.setAutoCancel(true);
        builder.setContent(contentViews);

        notificationManager.notify(0,builder.build());
    }

    public void cancelById() {
        notificationManager.cancel(0);  //对应NotificationManager.notify(id,notification);第一个参数
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}
