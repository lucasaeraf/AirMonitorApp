package com.example.airmonitor.airmonitor.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.airmonitor.airmonitor.MainActivity;
import com.example.airmonitor.airmonitor.R;

public class NotificationUtil {
    private static final int FIRE_NOTIFICATION = 1465;
    private static final int PENDING_INTENT = 4567;
    private static final String FIRE_NOTIFICATION_CHANNEL_ID = "fire-notification-channel";

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void alertUserFire(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    FIRE_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.fire_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,FIRE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_alert)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.fire_notification_title))
                .setContentText(context.getString(R.string.fire_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.fire_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(FIRE_NOTIFICATION, notificationBuilder.build());
    }


    private static PendingIntent contentIntent(Context context){
        Intent startMainActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context,
                PENDING_INTENT,
                startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                );
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_baby_round);
        return largeIcon;
    }

}
