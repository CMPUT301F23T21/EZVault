package com.example.ezvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.ezvault.authentication.authentication.AuthenticationHandler;
import com.example.ezvault.authentication.authentication.EmailPasswordAuthenticationStrategy;
import com.example.ezvault.database.FirebaseBundle;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    FirebaseBundle firebase;

    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        emailEditText = findViewById(R.id.username_text);
        passwordEditText = findViewById(R.id.password_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            loginButton.setEnabled(false);
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            EmailPasswordAuthenticationStrategy as = new EmailPasswordAuthenticationStrategy(firebase, email, password);
            AuthenticationHandler ah = new AuthenticationHandler(as);
            ah.authenticate().addOnFailureListener(e -> {
                Log.e("EZVault", "Failed to login", e);
                loginButton.setEnabled(true);
            }).addOnSuccessListener(u -> {
                Log.v("EZVault", "Success on login " + u.toString());
                finish();
            });
        });
    }
}