package com.ksource.hbStore.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;

import com.ksource.hbStore.R;
import com.ksource.hbStore.activity.ReceiveMoneyActivity;
import com.ksource.hbStore.bean.PayResultBean;

/**
 * 状态栏通知工具类
 */

public class NotificactionUtils {

    private static NotificactionUtils notificaction;

    private NotificactionUtils() {
    }

    public static synchronized NotificactionUtils getInstance() {
        if (notificaction == null) {
            notificaction = new NotificactionUtils();
        }
        return notificaction;
    }

    /**
     * 添加一个notification
     */
    @SuppressWarnings("deprecation")
    public void addMessageNotificaction(Context context,
                                        PayResultBean map, String token) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

//		String contentTitle = map.get("title"); // 通知栏标题
//		String contentText = map.get("content"); // 通知栏内容
//		Map<String, String> contentMap = new Gson().fromJson(contentText, new TypeToken<Map<String, String>>() {
//		}.getType());
        Intent intent = new Intent(context, ReceiveMoneyActivity.class);
        intent.putExtra("money", map.content.TOTAL_AMOUNT);
        intent.putExtra("time", map.content.PAY_TIME);
        intent.putExtra("orderId", map.content.MESSAGEID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        // Toast.makeText(context, "URL"+map.get("URL"), 0).show();
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                new Long(System.currentTimeMillis()).intValue(), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(map.title)
//				.setContentText(map.get("content"))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher) // 通知图标
                .setWhen(System.currentTimeMillis())
                .build();
        notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_NOTIFICATION);
        notification.sound = uri;
        notification.defaults = Notification.DEFAULT_ALL;

        // 点击状态栏的图标出现的提示信息设置
//		notification.setLatestEventInfo(context, contentTitle, contentText,
//				pendingIntent);
        manager.notify(new Long(System.currentTimeMillis()).intValue(),
                notification); // 这个函数中第一个参数代表i

    }

    /**
     * 添加单个用户消息的状态栏通知
     *
     * @param context
     * @param title
     * @param content
     * @param icon
     */
    public void addSingleNotification(Context context, String title,
                                      String content, String icon) {
//		NotificationManager notificationManager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification();
//		notification.icon = R.drawable.ic_launcher;// notification通知栏图标
//
//		// 自定义布局
//		RemoteViews contentView = new RemoteViews(context.getPackageName(),
//				R.layout.layout_notification);
//		// contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
//		contentView.setImageViewBitmap(
//				R.id.notification_image,
//				drawableToBitmap(context.getResources().getDrawable(
//						R.drawable.ic_launcher)));
//		contentView.setTextViewText(R.id.notification_title, title);
//		contentView.setTextViewText(R.id.notification_content, content);
//		contentView.setTextViewText(R.id.notification_time, TimeUtils
//				.getNowTimeString("yyyy-MM-dd hh:mm"));
//		notification.contentView = contentView;
//
//		// 跳到企业微信页面
//		Intent intent = new Intent(context, null);
//		intent.putExtra("skip", "main");
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		// notifcation.flags = Notification.FLAG_NO_CLEAR;
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//		/*
//		 * notification.setLatestEventInfo(this, titles[position],
//		 * artists[position],contentIntent);
//		 */
//
//		// 使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法，但是必须定义contentIntent
//		notification.contentIntent = contentIntent;
//		notificationManager.notify(123, notification);// 123是notification的唯一标识(id)
    }

    /**
     * 移除状态栏通知
     *
     * @param context
     * @param id      状态栏通知
     */
    public void removeNotificaction(Context context, int id) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    /**
     * 移除状态栏通知
     *
     * @param context 状态栏通知
     */
    public void removeAllNotificaction(Context context) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
