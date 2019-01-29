package com.jediupc.helloandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_SETTINGS = "settings";

    EditText mETUser;
    EditText mETPass;
    CheckBox mCBRemember;
    Button mBLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences sp = getSharedPreferences(
                KEY_SETTINGS, MODE_PRIVATE);
        if (sp.contains(KEY_USERNAME)) {
            Log.d("LoginActivity", "Already have username, skipping login");
            startContentActivity();
            return;
        }

        mETUser = findViewById(R.id.etUser);
        mETPass = findViewById(R.id.etPass);
        mCBRemember = findViewById(R.id.cbRemember);
        mBLogin = findViewById(R.id.bLogin);

        mBLogin.setOnClickListener(LoginActivity.this);
    }

    @Override
    public void onClick(View view) {
        Log.d("LoginActivity", "Login!");
        Log.d("LoginActivity", mETUser.getText().toString());
        Log.d("LoginActivity", mETPass.getText().toString());
        Log.d("LoginActivity", mCBRemember.isChecked() ?
                "remember" : "don't remember");

        if (mCBRemember.isChecked()) {
            SharedPreferences sp = getSharedPreferences(
                    KEY_SETTINGS, MODE_PRIVATE);
            sp.edit().putString(KEY_USERNAME, mETUser.getText().toString()).apply();
        }

        startContentActivity();
    }

    private void startContentActivity() {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
        finish();
    }
}
