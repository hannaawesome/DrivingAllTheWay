package com.libby.hanna.thecarslord.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.entities.Driver;

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;
import static android.telephony.PhoneNumberUtils.replaceUnicodeDigits;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth userAuth;
    private EditText email;
    private EditText password;
    private EditText verifyPassword;
    private EditText lastName;
    private EditText firstName;
    private EditText _id;
    private EditText phoneNumber;
    private EditText creditCardNumber;
    private TextView login;
    private AppCompatButton register;
    private DB_manager be;
    private Driver d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        be = DBManagerFactory.GetFactory();
        findViews();
        userAuth = FirebaseAuth.getInstance();
        setupFloatingLabelError();

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validations()) {
                    d = getDriver();
                    if (saveSharedPrefences()) {
                        registerToFireBase(email.getText().toString(), password.getText().toString());
                        be.addDriver(d, new DB_manager.Action<Long>() {
                            @Override
                            public void onSuccess(Long obj) {
                                Toast.makeText(getBaseContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(getBaseContext(), "Could not add your data to the system, must be something wrong \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
        //region toLogin
        SpannableString ss = new SpannableString("Already a member? Login");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login = (TextView) findViewById(R.id.link_login);
        login.setText(ss);
        login.setMovementMethod(LinkMovementMethod.getInstance());
        login.setHighlightColor(Color.TRANSPARENT);
        //endregion
    }


    private boolean saveSharedPrefences() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", email.getText().toString());
            editor.putString("userPassward", password.getText().toString());
            editor.putBoolean("SavePass", false);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(this, "failed to save Preferences", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void registerToFireBase(String email, String password) {
        userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {                         // Sign in success, update UI with the signed-in user's information
                    //curr = userAuth.getCurrentUser();
                } else {                         // If sign in fails, display a message to the user.
                    Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findViews() {
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPasswordR);
        verifyPassword = (EditText) findViewById(R.id.editTextPasswordRVerify);
        lastName = (EditText) findViewById(R.id.editTextLastName);
        firstName = (EditText) findViewById(R.id.editTextFirstName);
        _id = (EditText) findViewById(R.id.editTextId);
        phoneNumber = (EditText) findViewById(R.id.editTextPhone);
        creditCardNumber = (EditText) findViewById(R.id.editTextCreditCard);
        register = (AppCompatButton) findViewById(R.id.done);

    }

    /**
     * checks if the email entered is valid
     */
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validations() {
        boolean check = true;
        //region getStrings
        String strEmail = email.getText().toString();
        String strPhone = phoneNumber.getText().toString();
        String strFirstName = firstName.getText().toString();
        String strLastName = lastName.getText().toString();
        String strId = _id.getText().toString();
        String strCredit = creditCardNumber.getText().toString();
        String strPassword = password.getText().toString();
        String strVPassword = verifyPassword.getText().toString();

        //endregion

        //region isEmpty
        if (TextUtils.isEmpty(strEmail)) {
            email.setError("Email must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strCredit)) {
            creditCardNumber.setError("credit card number must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strFirstName)) {
            firstName.setError("Your first name must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strPhone)) {
            phoneNumber.setError("Phone number must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strId)) {
            _id.setError("Id must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strPassword)) {
            password.setError("Password must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strVPassword)) {
            verifyPassword.setError("You must verify your password");
            check = false;
        }
        if (TextUtils.isEmpty(strLastName)) {
            lastName.setError("Your last name must be entered");
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

        if (!password.getText().toString().equals(verifyPassword.getText().toString())) {
            verifyPassword.setError("password does not match");
            return false;
        }
        //endregion
        return true;
    }

    private void setupFloatingLabelError() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.Password_text_input_layout);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 8) {
                    floatingUsernameLabel.setError("Password too short");
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * @return The Driver data entered by the user
     */
    private Driver getDriver() {
        Driver temp = new Driver();
        temp.setFirstName(firstName.getText().toString());
        temp.setPhoneNumber(phoneNumber.getText().toString());
        temp.setEmailAddress(email.getText().toString());
        temp.setLastName(lastName.getText().toString());
        temp.setCreditCardNumber(creditCardNumber.getText().toString());
        temp.set_id(Long.valueOf(_id.getText().toString()));
        return temp;
    }

    private void sendEmailVerification() {
        // Send verification email
        final FirebaseUser user = userAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    //Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
