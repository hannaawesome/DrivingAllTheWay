package com.libby.hanna.drivingalltheway.controller;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.libby.hanna.drivingalltheway.R;
import com.libby.hanna.drivingalltheway.model.backend.SpinnerAdapter;

import java.util.List;

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;

public class TripApp extends Activity {

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_app);
        Button b= (Button)findViewById(R.id.DoneButton);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                EditText emailid = (EditText)findViewById(R.id.EmailEnter);
                EditText phoneid=(EditText)findViewById(R.id.PhoneEnter);
                EditText timeid=(EditText)findViewById(R.id.TimeEnter);
                String getEmailId = emailid.getText().toString();
                String getPhoneId = phoneid.getText().toString();
                String getTimeId=timeid.getText().toString();
                // Check if email id is valid or not
                if (!isEmailValid(getEmailId))
                    Toast.makeText(getApplicationContext(), "Your email is invalid!", Toast.LENGTH_LONG).show();
                else if(!isGlobalPhoneNumber(getPhoneId))
                    Toast.makeText(getApplicationContext(), "Your phone number is invalid!", Toast.LENGTH_LONG).show();
                else if(!getTimeId.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")){
                    Toast.makeText(getApplicationContext(), "The time you entered was invalid", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Added Successfully!", Toast.LENGTH_LONG).show();

            }
        });

        Spinner StSpinner=(Spinner)findViewById(R.id.StatusSpinner);
        StSpinner.setAdapter(new SpinnerAdapter(this));
    }





}
