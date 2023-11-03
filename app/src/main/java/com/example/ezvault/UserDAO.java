package com.example.ezvault;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Does raw database interactions regarding users.
 */
public class UserDAO extends AbstractDAO<User, String> {
    /**
     * The name of the collection concerning users
     */
    private final String collectionName = "users";

    /**
     * Construct a UserDAO
     * @param firebase The firebase bundle
     */
    public UserDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    /**
     * Convert a {@code User} into a {@code Map}
     * @param user The user to convert.
     * @return The {@code Map} to use in firebase interactions.
     */
    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getUserName());
        //map.put("itemids", user.getItemList().getItems());
        return map;
    }

    /**
     * Create a user on the database.
     * @param user The object to be created on the database.
     * @return The UID of the User.
     */
    @Override
    Task<String> create(User user) {
        return firebase.getDb()
                .collection(collectionName)
                .add(userToMap(user))
                .continueWith(t -> {
                    if (t.isSuccessful()) {
                        return t.getResult().getId();
                    } else {
                        throw new UnsupportedOperationException("to be implemented");
                    }
                });
    }

    /**
     * Read a user from the database.
     * @param uid The unique identifier of the user.
     * @return Return the read user.
     */
    @Override
    Task<User> read(String uid) {
        Task<DocumentSnapshot> docTask = firebase.getDb()
                .collection(collectionName)
                .document(uid)
                .get();
        Continuation<DocumentSnapshot, User> readContinuation = task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                String userName = doc.getString("name");
                return new User(userName, uid);
            } else {
                throw new UnsupportedOperationException("to be implemented");
            }
        };
        return docTask.continueWith(readContinuation);
    }

    /**
     * Update a user.
     * @param uid The id of the user.
     * @param user The new value to update the user to.
     * @return An empty task representing the operation.
     */
    @Override
    Task<Void> update(String uid, User user) {
        return firebase.getDb()
                .collection(collectionName)
                .document(uid)
                .set(userToMap(user));
    }

    /**
     * Delete a user from the database.
     * @param uid The id of the object to be deleted.
     * @return An empty task representing the operation.
     */
    @Override
    Task<Void> delete(String uid) {
        return firebase.getDb()
                .collection(collectionName)
                .document(uid)
                .delete();
    }
}
