package com.example.ezvault;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseBundle firebase = new FirebaseBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String email = "test@gmail.com";
        String password = "test123";

        LoginStrategy ls = new EmailPasswordLoginStrategy(firebase, email, password);
        Log.i("EZVault", "created strategy");
        LoginHandler lh = new LoginHandler(ls);
        Log.i("EZVault", "created handler");
        lh.login().continueWith(uTask -> {
            User u = uTask.getResult();
            Log.i("EZVault", "uid: " + u.getUid() + " uname: " + u.getUserName());
            return null;
        });
    }
}