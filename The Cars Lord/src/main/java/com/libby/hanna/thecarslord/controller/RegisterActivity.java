package com.libby.hanna.thecarslord.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libby.hanna.thecarslord.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.editTextUserName);
        password = (EditText) findViewById(R.id.editTextPasswordR);
        verifyPassword = (EditText) findViewById(R.id.editTextPasswordRVerify);
        lastName = (EditText) findViewById(R.id.editTextLastName);
        firstName = (EditText) findViewById(R.id.editTextFirstName);
        _id = (EditText) findViewById(R.id.editTextId);
        phoneNumber = (EditText) findViewById(R.id.editTextPhone);
        creditCardNumber = (EditText) findViewById(R.id.editTextCreditCard);
        userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();
    }

    private boolean verifyPass() {
        if (password.toString().equals(verifyPassword.toString()))
            return true;
        else {
            verifyPassword.setError("password does not match");
            return false;
        }
    }

    private boolean saveSharedPrefences() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", email.toString());
            editor.putString("userPassward", password.toString());
            //editor.putBoolean("SavePass",true); why?
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(this, "failed to save Preferences", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void register(String email, String password) {
        userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {                         // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = userAuth.getCurrentUser();
                } else {                         // If sign in fails, display a message to the user.
                    Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
