package com.example.beberagua;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;

public class NotificationPublisher extends BroadcastReceiver {
    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_NOTIFICATION_ID = "key_notification_id";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, ii, 0);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String message = intent.getStringExtra(KEY_NOTIFICATION);
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

        Notification notification = getNotification(message, context, notificationManager, pIntent);

        notificationManager.notify(id, notification);
    }

    private Notification getNotification(String content, Context context,
                                         NotificationManager manager,
                                         PendingIntent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(4000);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentText(content)
                .setTicker("Alerta")
                .setContentIntent(intent)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel =
                    new NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);

            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();
        }
        return builder.build();
    }
}
