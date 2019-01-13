package com.libby.hanna.thecarslord.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.entities.Driver;

public class LoginActivity extends Activity {
    private EditText name;
    private EditText password;
    private FirebaseAuth userAuth;
    FirebaseUser currentUser;
    private AppCompatButton log;
    private TextView reg;
    private CheckBox remember;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.editTextUserName);
        password = (EditText) findViewById(R.id.editTextUserPassword);
        remember = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // userAuth = FirebaseAuth.getInstance();
        loadSharedPreferences();
        log = (AppCompatButton) findViewById(R.id.login);
        log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(validations()){
                saveSharedPrefences();
                logIn(name.getText().toString(),password.getText().toString());
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);}
            }
        });
        SpannableString ss = new SpannableString("No account yet? Create one");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg = (TextView) findViewById(R.id.link_signup);
        reg.setText(ss);
        reg.setMovementMethod(LinkMovementMethod.getInstance());
        reg.setHighlightColor(Color.TRANSPARENT);
    }
    private boolean validations() {
        boolean check=true;
        String strEmail = name.getText().toString();
        String strPassword = password.getText().toString();
        if (TextUtils.isEmpty(strEmail)) {
            name.setError("Email must be entered");
            check = false;
        }
        if (TextUtils.isEmpty(strPassword)) {
            password.setError("password must be entered");
            check = false;
        }
        return check;
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
