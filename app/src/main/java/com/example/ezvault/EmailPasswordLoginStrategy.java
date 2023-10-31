package com.example.ezvault;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

/**
 * A strategy for logging in using email and password, relies on Firebase.
 */
public class EmailPasswordLoginStrategy extends FirebaseLoginStrategy {
    /**
     * The email of the login attempt
     */
    private final String email;

    /**
     * The password of the login attempt
     */
    private final String password;

    /**
     * @param firebase The FirebaseBundle providing access to the Firebase auth and Firestore database.
     * @param email The email of the login attempt
     * @param password The password of the login attempt
     */
    public EmailPasswordLoginStrategy(FirebaseBundle firebase, String email, String password) {
        super(firebase);
        this.email = email;
        this.password = password;
    }

    @Override
    public Task<String> authenticate(FirebaseBundle firebase) {

        return firebase.getAuth()
                .signInWithEmailAndPassword(email, password)
                .continueWith(authTask -> {
                    FirebaseUser firebaseUser = authTask.getResult().getUser();
                    if (firebaseUser == null) {
                        throw new RuntimeException();
                    }
                    return firebaseUser.getUid();
                });
    }
}
