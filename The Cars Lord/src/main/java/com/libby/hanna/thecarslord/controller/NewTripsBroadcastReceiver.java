package com.libby.hanna.thecarslord.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;

public class NewTripsBroadcastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

            Notification.Builder nBuilder = new Notification.Builder(context);
            nBuilder.setSmallIcon(R.drawable.ic_local_taxi);
            nBuilder.setContentTitle("New route found!");
            nBuilder.setContentText("here is a new route waiting for you");
           // Notification notification = nBuilder.build();
            //startForeground(1234, notification);
            Object obj = context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = (NotificationManager) obj;
            notificationManager.notify(1234, nBuilder.build());
    }
}
