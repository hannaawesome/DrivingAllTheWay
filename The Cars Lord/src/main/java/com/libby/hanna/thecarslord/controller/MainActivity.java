package com.libby.hanna.thecarslord.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.List;

public class MainActivity extends AppCompatActivity {
private TextView done;
private DB_manager be;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         be = DBManagerFactory.GetFactory();

        done = (TextView) findViewById(R.id.something);

        Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
               /* if (studentsRecycleView.getAdapter() == null) {
                    students = obj;
                    studentsRecycleView.setAdapter(new StudentsRecycleViewAdapter());
                } else studentsRecycleView.getAdapter().notifyDataSetChanged();*/
                List<Trip> d = be.getTripsByPrice();
                if (d != null||d.size()!=0)
                    done.setText(d.get(0).getEmailAddress());
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get trips list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
   protected void onDestroy() {
        Firebase_DBManager.stopNotifyToTripList();
        super.onDestroy();
    }
}
