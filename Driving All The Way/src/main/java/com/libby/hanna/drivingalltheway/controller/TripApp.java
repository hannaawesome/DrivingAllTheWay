package com.libby.hanna.drivingalltheway.controller;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.libby.hanna.drivingalltheway.R;
import com.libby.hanna.drivingalltheway.model.backend.*;
import com.libby.hanna.drivingalltheway.model.entities.Trip;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;

public class TripApp extends Activity {
//maybe add an option of now button

    //region Views
    private Spinner status;
    private Button done;
    private EditText email;
    private EditText phone;
    private EditText name;
    private EditText from;
    private EditText to;
    private EditText when;
    private Trip t;
    //endregion

    DB_manager be = DBManagerFactory.GetFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_app);
        done = (Button) findViewById(R.id.DoneButton);
        status = (Spinner) findViewById(R.id.StatusSpinner);
        status.setAdapter(new SpinnerAdapter(this));
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViews();
                if (validations()) {
                    t = getTrip();
                        be.addTrip(t,new DB_manager.Action<Long>() {
                            @Override
                            public void onSuccess(Long obj) {
                                Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_LONG).show();
                                clearAllPage();
                            }
                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(getBaseContext(), "Could not add the data, must be something wrong \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onProgress(String status, double percent) {

                            }
                        });

                }
            }
        });
    }

    private void findViews() {
        name = (EditText) findViewById(R.id.NameEnter);
        from = (EditText) findViewById(R.id.FromEnter);
        to = (EditText) findViewById(R.id.ToEnter);
        email = (EditText) findViewById(R.id.EmailEnter);
        phone = (EditText) findViewById(R.id.PhoneEnter);
        when = (EditText) findViewById(R.id.TimeEnter);
        status = (Spinner) findViewById(R.id.StatusSpinner);
    }

    private boolean validations() {
        boolean check = true;
        //region getStrings
        String strEmail = email.getText().toString();
        String strPhone = phone.getText().toString();
        String strTime = when.getText().toString();
        String strFrom = from.getText().toString();
        String strName = name.getText().toString();
        String strTo = to.getText().toString();
        //endregion

        //region isEmpty
        if (TextUtils.isEmpty(strEmail)) {
            email.setError("Email must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strFrom)) {
            from.setError("Source location must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strName)) {
            name.setError("Your name must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strPhone)) {
            phone.setError("Phone number must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strTime)) {
            when.setError("Time must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strTo)) {
            to.setError("Destination must be entered");
            check = false;
        }
        if (!check)
            return false;
        //endregion

        //region validation
        //check if email is valid
        if (!isEmailValid(strEmail)) {
            Toast.makeText(getApplicationContext(), "Your email is invalid!", Toast.LENGTH_LONG).show();
            return false;
        }
        //check if phone number is valid
        if (!isGlobalPhoneNumber(strPhone)) {
            Toast.makeText(getApplicationContext(), "Your phone number is invalid!", Toast.LENGTH_LONG).show();
            return false;
        }
        //check if time is valid
        if (!strTime.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")) {
            Toast.makeText(getApplicationContext(), "The time you entered is invalid", Toast.LENGTH_LONG).show();
            return false;
        }
        //endregion

        return true;
    }

    private void clearAllPage() {
        email.getText().clear();
        phone.getText().clear();
        when.getText().clear();
        to.getText().clear();
        from.getText().clear();
        name.getText().clear();
        //status.clearAnimation();
    }

    private Trip getTrip() {
        Trip temp = new Trip();
        temp.setName(name.getText().toString());
        temp.setPhoneNumber(phone.getText().toString());
        temp.setEmailAddress(email.getText().toString());
        temp.setSource(fromStringToLocation(from.getText().toString()));
        temp.setDestination(fromStringToLocation(to.getText().toString()));
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm"); // 12 hour format
            java.util.Date d1 = (java.util.Date) format.parse(when.getText().toString());
            temp.setStart(new Time(d1.getTime()));
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Must be something wrong with the time you entered", Toast.LENGTH_LONG).show();
        }
        int selectedItemOfMySpinner = status.getSelectedItemPosition();
        if (selectedItemOfMySpinner != 0)
            temp.setState(Trip.TripState.valueOf(status.getSelectedItem().toString()));
        else
            temp.setState(Trip.TripState.available);

        return temp;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private Location fromStringToLocation(String str) {
        Geocoder gc = new Geocoder(this);
        try {
            if (gc.isPresent()) {
                List<Address> list = gc.getFromLocationName("155 Park Theater, Palo Alto, CA", 1);
                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();

                Location locationA = new Location(str);

                locationA.setLatitude(lat);
                locationA.setLongitude(lng);
                return locationA;
            }
            return null;
        } catch (Exception exception) {
            Toast.makeText(getBaseContext(), "must be something wrong with the location you entered\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

    }
}
