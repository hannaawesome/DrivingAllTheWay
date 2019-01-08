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

public class LoginActivity extends AppCompatActivity {
    EditText name;
    EditText password;
    private FirebaseAuth userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.editTextUserName);
        password = (EditText) findViewById(R.id.editTextUserPassword);
        userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();
    }



    private boolean loadSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains("userName"))
            name.setText(sharedPreferences.getString("userName", null));
        else
            return false;
        if (sharedPreferences.contains("userPassword"))
            password.setText(sharedPreferences.getString("userPassword", null));
        else
            return false;
        return true;
    }



    private void singIn(String email, String password) {
        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
