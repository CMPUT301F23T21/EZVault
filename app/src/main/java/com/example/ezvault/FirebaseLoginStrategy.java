package com.example.ezvault;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    private User finalContinuation(ItemService itemService, String uid, DocumentSnapshot doc) {
        //DocumentSnapshot doc = docTask.getResult();
        return new User (
                itemService,
                doc.getString("name"),
                uid
        );
    }

    /**
     * Load the users item database via the {@link ItemService}
     * @param uid The uid of the user
     * @param docTask The completed task holding the user's Firestore document
     * @return
     */
    private Task<User> itemsContinuation(String uid, Task<DocumentSnapshot> docTask) {
        DocumentSnapshot doc = docTask.getResult();

        List<String> itemIds = (List<String>)doc.get("itemids");
        ItemService itemService = new ItemService(firebase);

        return itemService.initItems(itemIds).continueWith(t -> finalContinuation(itemService, uid, doc));
    }

    private Task<User> uidContinuation(Task<String> uidTask) {
        String uid = uidTask.getResult();
        Task<DocumentSnapshot> documentSnapshotTask =
                firebase.getDb()
                        .collection("users")
                        .document(uid)
                        .get();
        return documentSnapshotTask
                .continueWithTask(d -> itemsContinuation(uid, d));
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
