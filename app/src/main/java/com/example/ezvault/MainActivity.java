package com.example.ezvault;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ezvault.authentication.authentication.AuthenticationHandler;
import com.example.ezvault.authentication.authentication.EmailPasswordAuthenticationStrategy;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.utils.TaskUtils;

public class MainActivity extends AppCompatActivity {
    FirebaseBundle firebase = new FirebaseBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String email = "nortif8@gmail.com";
        String password = "test123";
        EmailPasswordAuthenticationStrategy as = new EmailPasswordAuthenticationStrategy(firebase, email, password);
        AuthenticationHandler auth = new AuthenticationHandler(as);
        TaskUtils.onSuccess(auth.authenticate(), user -> {
            Log.d("EZVault", "Logged in: " + user.toString());
        });
    }
}