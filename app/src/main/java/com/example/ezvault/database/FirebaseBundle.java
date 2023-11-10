package com.example.ezvault.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Utility class for grouping Firebase components together.
 */
public class FirebaseBundle {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    /**
     * Construct a FirebaseBundle based on the default
     * Firestore database and Firebase auth.
     */
    public FirebaseBundle() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance(), FirebaseStorage.getInstance());
    }

    /**
     * Construct a FirebaseBundle from arguments.
     * @param db Firestore Database
     * @param auth Firebase Auth
     */
    public FirebaseBundle(FirebaseFirestore db, FirebaseAuth auth, FirebaseStorage storage) {
        this.db = db;
        this.auth = auth;
        this.storage = storage;
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

    /**
     * Get the storage
     * @return The Firebase storage instnace
     */
    public FirebaseStorage getStorage() { return storage; }
}
