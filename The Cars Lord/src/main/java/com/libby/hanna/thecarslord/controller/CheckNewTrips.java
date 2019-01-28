package com.libby.hanna.thecarslord.controller;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.ArrayList;
import java.util.List;


/**
 * creates the service that checks every 2 minutes if there's a new trip
 * that is under 20 km far from the driver
 */
public class CheckNewTrips extends Service {
    private DB_manager be;
    private List<Trip> tripList = new ArrayList<Trip>();
    boolean isRun = false;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        be = DBManagerFactory.GetFactory();
        final List<String> tripsId = new ArrayList<String>();//keeps all the trips that has been notified in this play
        isRun = true;
        try {
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    while (isRun) {
                        try {
                            Thread.sleep(1200000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tripList = be.getNotHandeledTrips();//all the available trips
                        for (Trip i : tripList)
                            if (!tripsId.contains(i.get_id()))//if not notified yet
                                if (be.distanceCalc(i, getBaseContext()) < 20) {
                                    {
                                        //send to the broadcast receiver
                                        sendBroadcast(new Intent(getBaseContext(), NewTripsBroadcastReceiver.class));
                                        tripsId.add(i.get_id());
                                    }
                                }
                    }
                }
            };
            thread.start();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}



