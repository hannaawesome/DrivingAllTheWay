package com.libby.hanna.thecarslord.controller;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private DB_manager be;
    private List<Trip> tripList;
    private List<Driver> driverList;
    private Driver d;
    private FirebaseAuth userAuth;
    public static Location thisLoca;
    // Acquire a reference to the system Location Manager
    private LocationManager locationManager;
    // Define a listener that responds to location updates
    private LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationClient;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        be = DBManagerFactory.GetFactory();
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        userAuth = FirebaseAuth.getInstance();
        t = new ActionBarDrawerToggle(this, dl, 0, R.string.app_name);
        getLocation(this);
        //get driver's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                thisLoca = location;
                // Remove the listener you previously added
                locationManager.removeUpdates(locationListener);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.tripsOnWait:
                        loadFragment(new FirstFragment());
                        return true;
                    case R.id.myTips:
                        loadFragment(new SecondFragment());
                        return true;
                    case R.id.exit:
                        signOut();
                        finish();
                        System.exit(0);
                    default:
                        return true;
                }
            }
        });
        Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
                tripList = obj;
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get trips list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        Firebase_DBManager.NotifyToDriversList(new Firebase_DBManager.NotifyDataChange<List<Driver>>() {
            @Override
            public void OnDataChanged(List<Driver> obj) {
                driverList = obj;
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get Drivers list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        d=be.loadDataOnCurrentDriver(this);
        startService(new Intent(getBaseContext(), CheckNewTrips.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        dl.closeDrawer(nv);
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.relativeMain, fragment);
        fragmentTransaction.commit(); // save the changes
    }


    protected void onDestroy() {
        Firebase_DBManager.stopNotifyToTripList();
        Firebase_DBManager.stopNotifyToDriversList();
        stopService(new Intent(getBaseContext(), CheckNewTrips.class));
        super.onDestroy();
    }

    private void signOut() {
        userAuth.signOut();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation(Activity a) {
        try {
            //     Check the SDK version and whether the permission is already granted or not.
            if ( a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&& a.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                a.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if(thisLoca==null) {   //get Provider location from the user location services
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
                //run the function on the background and add onSuccess listener
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(a, new OnSuccessListener<Location>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Location _location) {
                                // Got last known location. In some rare situations this can be null.
                                if (_location != null) {
                                    //save the location
                                    thisLoca = _location;
                                } else {
                                    Toast.makeText(getBaseContext(), "can't find your location", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        } catch (Exception ex) {
            Toast.makeText(a, "must be something wrong with getting your location", Toast.LENGTH_LONG).show();
        }

 }
}
