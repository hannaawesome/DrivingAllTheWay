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

import java.util.List;


public class CheckNewTrips extends Service {
    private DB_manager be;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        be = DBManagerFactory.GetFactory();
        try {
            Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
                @Override
                public void OnDataChanged(List<Trip> obj) {
                    if (be.distanceCalc(obj.get(obj.size() - 1), getBaseContext()) < 5)
                        sendBroadcast(new Intent(getBaseContext(), NewTripsBroadcastReceiver.class));
                }

                @Override
                public void onFailure(Exception exception) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}



