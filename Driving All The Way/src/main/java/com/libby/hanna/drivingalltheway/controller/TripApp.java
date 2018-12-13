package com.libby.hanna.drivingalltheway.controller;

import android.app.Activity;
import android.content.ContentValues;
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

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;
import static com.libby.hanna.drivingalltheway.model.backend.TripConst.TripToContentValues;

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
        status.setAdapter(new SpinnerAdapter(this));
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViews();
                if (validations()) {
                    t = getTrip();
                    ContentValues cv = TripToContentValues(t);
                    try {
                        if (be.addTrip(cv)) {
                            Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_LONG).show();
                            clearAllPage();
                        }
                    } catch (Exception exception) {
                        Toast.makeText(getBaseContext(), "must be something wrong \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

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
        temp.setSource(from.getText().toString());
        temp.setDestination(to.getText().toString());
        temp.setStart(Time.valueOf(when.getText().toString()));
        temp.setState((Trip.TripState) status.getSelectedItem());
        return temp;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
