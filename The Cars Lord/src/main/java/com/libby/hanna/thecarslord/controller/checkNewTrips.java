package com.libby.hanna.thecarslord.controller;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.libby.hanna.thecarslord.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class checkNewTrips extends IntentService {

    public checkNewTrips() {
        super("checkNewTrips");
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        Notification.Builder nBuilder = new Notification.Builder(getBaseContext());
        nBuilder.setSmallIcon(R.drawable.ic_local_taxi);
        nBuilder.setContentTitle("New route found!");
        nBuilder.setContentText("here is a new route waiting for you");
        Notification notification = nBuilder.build();
        startForeground(1234, notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
