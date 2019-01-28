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


public class CheckNewTrips extends Service {
    private DB_manager be;
    private List<Trip> tripList = new ArrayList<Trip>();
    boolean isRun=false;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        be = DBManagerFactory.GetFactory();
        isRun = true;
        try {
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    while (isRun) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (Trip i : tripList)
                            if (be.distanceCalc(i, getBaseContext()) < 50) {
                                sendBroadcast(new Intent(getBaseContext(), NewTripsBroadcastReceiver.class));
                                break;
                            }
                    }
                }
            };
            Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
                @Override
                public void OnDataChanged(List<Trip> obj) {
                    tripList.addAll(obj);
                    if (be.distanceCalc(obj.get(obj.size() - 1), getBaseContext()) < 5)
                        sendBroadcast(new Intent(getBaseContext(), NewTripsBroadcastReceiver.class));
                    thread.start();
                }

                @Override
                public void onFailure(Exception exception) {

                }
            });

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
        isRun=false;
        Firebase_DBManager.stopNotifyToDriversList();
    }
}



