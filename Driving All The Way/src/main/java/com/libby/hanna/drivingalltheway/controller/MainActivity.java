/*
Hanna Weissberg 318796398
Libby Olidort 209274612
*/
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

/**
 * The main of this application
 * it is only used as a home page to the adding of the data
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b= (Button)findViewById(R.id.AddTrip);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent=new Intent(getBaseContext(), TripApp.class);
                startActivity(intent);
            }
        });

    }

}
