package com.libby.hanna.thecarslord.controller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;

public class NewTripsBroadcastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

           /* Notification.Builder nBuilder = new Notification.Builder(context);
            nBuilder.setSmallIcon(R.drawable.ic_local_taxi);
            nBuilder.setContentTitle("New route found!");
            nBuilder.setContentText("here is a new route waiting for you");*/
            Object obj = context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = (NotificationManager) obj;
            //notificationManager.notify(1234, nBuilder.build());
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context,LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("New route found!")
                .setContentText("here is a new route waiting for you")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND).setSmallIcon(R.drawable.ic_local_taxi);

        notificationManager.notify(1, b.build());

    }
}
