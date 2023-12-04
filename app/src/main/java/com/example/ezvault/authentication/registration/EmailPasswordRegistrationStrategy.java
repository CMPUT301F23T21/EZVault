package com.example.ezvault.authentication.registration;

import com.example.ezvault.database.FirebaseBundle;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Represents the registration strategy using firebase and it's email and password provider.
 */
public class EmailPasswordRegistrationStrategy extends FirebaseRegistrationStrategy {
    private final String email;
    private final String password;

    /**
     * Make a email/password based firebase strategy
     * @param firebase The firebase bundle
     * @param email The email of the registration
     * @param password The password of the registration
     */
    public EmailPasswordRegistrationStrategy(FirebaseBundle firebase, String email, String password) {
        super(firebase);
        this.email = email;
        this.password = password;
    }

    /**
     * Register the user on firebase auth
     * @param firebase The firebase bundle
     * @param userName The userName of the user
     * @return A Task representing the registration of the user
     */
    @Override
    public Task<AuthResult> register(FirebaseBundle firebase, String userName) {
        return firebase.getAuth().createUserWithEmailAndPassword(email, password);
    }
}
