package com.example.androidhabittracker;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AppNotification {
    private static String CHANNEL_ID = "Base channel";
    private static String CHANNEL_DESC = "Base channel desc";
    private Context context;
    private NotificationManager notificationManager;

    public AppNotification(Context context) {
        this.context = context;
        notificationManager = context.getSystemService(NotificationManager.class);
        createNotificationChannel();
    }
    public void send(int icon, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void getPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            }, 100);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.SCHEDULE_EXACT_ALARM
            }, 100);
        }
    }
}
