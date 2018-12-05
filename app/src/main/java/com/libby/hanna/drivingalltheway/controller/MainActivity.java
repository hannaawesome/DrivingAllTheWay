package com.libby.hanna.drivingalltheway.controller;

import android.content.Context;
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
        Button b= (Button)findViewById(R.id.AddTrip);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
        {
            Toast.makeText(getApplicationContext(), "wow, it's working!", Toast.LENGTH_LONG).show();
        }
        });

    }


}
