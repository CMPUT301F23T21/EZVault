package com.example.ezvault.authentication.authentication;

import com.example.ezvault.data.database.FirebaseBundle;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class EmailPasswordAuthenticationStrategy extends FirebaseAuthenticationStrategy {
    private final String email;
    private final String password;

    public EmailPasswordAuthenticationStrategy(FirebaseBundle firebase, String email, String password) {
        super(firebase);
        this.email = email;
        this.password = password;
    }

    @Override
    public Task<AuthResult> authenticate(FirebaseBundle firebase) {
        return firebase.getAuth().signInWithEmailAndPassword(email, password);
    }
}
