package com.libby.hanna.thecarslord.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DB_manager be;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private Driver driver;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        be = DBManagerFactory.GetFactory();
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
               /* if (studentsRecycleView.getAdapter() == null) {
                    students = obj;
                    studentsRecycleView.setAdapter(new StudentsRecycleViewAdapter());
                } else studentsRecycleView.getAdapter().notifyDataSetChanged();*/
               /* List<Trip> d = be.getTripsByPrice();
                if (d != null || d.size() != 0)
                    done.setText(d.get(0).getEmailAddress());*/
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get trips list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        t = new ActionBarDrawerToggle(this, dl, 0, 1) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("Close");
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Open");
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
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
                    case R.id.allTrips:
                        loadFragment(new FirstFragment());
                        return true;
                    case R.id.thisTrips:
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
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }


    /*private void loadDataOnCurrentDriver(){
        String id=currentUser.getUid();
}*/
    protected void onDestroy() {
        Firebase_DBManager.stopNotifyToTripList();
        super.onDestroy();
    }

    private void signOut() {
        userAuth.signOut();
    }
}