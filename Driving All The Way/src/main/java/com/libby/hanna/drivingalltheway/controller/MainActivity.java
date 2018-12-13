package com.libby.hanna.drivingalltheway.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.libby.hanna.drivingalltheway.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c2833")));
        Button b= (Button)findViewById(R.id.AddTrip);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent=new Intent(getApplicationContext(), TripApp.class);
                startActivity(intent);
            }
        });

    }

}
