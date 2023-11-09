package com.example.ezvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_auth_container, new WelcomeFragment())
                .addToBackStack("welcome")
                .commit();
    }
}