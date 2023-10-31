package com.example.ezvault;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Utility class for grouping Firebase components together.
 */
public class FirebaseBundle {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    /**
     * Construct a FirebaseBundle based on the default
     * Firestore database and Firebase auth.
     */
    public FirebaseBundle() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Construct a FirebaseBundle from arguments.
     * @param db Firestore Database
     * @param auth Firebase Auth
     */
    public FirebaseBundle(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    /**
     * Get the database
     * @return The Firestore database instance
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * Get the auth
     * @return The Firebase auth instnace
     */
    public FirebaseAuth getAuth() {
        return auth;
    }
}
