package com.libby.hanna.thecarslord.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.entities.Driver;

public class LoginActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private FirebaseAuth userAuth;
    FirebaseUser currentUser;
    private AppCompatButton register;
    private CheckBox remember;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.editTextUserName);
        password = (EditText) findViewById(R.id.editTextUserPassword);
        //remember = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userAuth = FirebaseAuth.getInstance();
        register = (AppCompatButton) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean loadSharedPreferences() {
        if (sharedPreferences.contains("SavePass"))
            if (!sharedPreferences.getBoolean("SavePass", false))
                return true;
            else
                remember.setChecked(true);
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
    private void saveSharedPrefences() {
        if (remember.isChecked()) {
            try {
                editor = sharedPreferences.edit();
                editor.putBoolean("SavePass", true);
                editor.putString("userName", name.getText().toString());
                editor.putString("userPassward", password.getText().toString());
                editor.commit();
            } catch (Exception ex) {
                Toast.makeText(this, "failed to save Preferences", Toast.LENGTH_SHORT).show();
            }
        } else {
            editor.clear();
            editor.commit();
        }
    }

    private void logIn(String email, String password) {
        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {                         // Sign in success, update UI with the signed-in user's information
                    currentUser = userAuth.getCurrentUser();
                } else {                         // If sign in fails, display a message to the user.
                    Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
