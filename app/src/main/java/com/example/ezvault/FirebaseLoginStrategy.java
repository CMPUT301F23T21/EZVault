package com.example.ezvault;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Represents a login strategy ({@link LoginStrategy}) that relies upon a {@link FirebaseBundle}.
 */
public abstract class FirebaseLoginStrategy implements LoginStrategy {
    private final FirebaseBundle firebase;

    /**
     * Authenticate by getting a user's UID.
     * @param firebase The {@link FirebaseBundle} to use
     * @return A Task with the user's UID
     */
    public abstract Task<String> authenticate(FirebaseBundle firebase);

    private User finalContinuation(String uid, Task<DocumentSnapshot> docTask) {
        DocumentSnapshot doc = docTask.getResult();
        return new User(doc.getString("name"), uid);
    }

    private Task<User> uidContinuation(Task<String> uidTask) {
        String uid = uidTask.getResult();
        Task<DocumentSnapshot> documentSnapshotTask =
                firebase.getDb()
                        .collection("users")
                        .document(uid)
                        .get();
        return documentSnapshotTask
                .continueWith(d -> finalContinuation(uid, d));
    }

    @Override
    public Task<User> authenticateUser() {
        return authenticate(this.firebase)
                .continueWithTask(this::uidContinuation);
    }

    public FirebaseLoginStrategy(FirebaseBundle firebase) {
        this.firebase = firebase;
    }
}
