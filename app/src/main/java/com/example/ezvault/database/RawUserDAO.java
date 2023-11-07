/*
Handles direct database access to users within the 'users' collection.
*/


package com.example.ezvault.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RawUserDAO extends AbstractDAO<RawUserDAO.RawUser, String> {
    private final String collectionName = "users";
    private final FirebaseFirestore db;

    /**
     * Constructs a RawUserDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public RawUserDAO(FirebaseBundle firebase) {
        super(firebase);
        db = firebase.getDb();
    }

    /**
     * Creates a user entry in the 'users' collection.
     * @param rawUser User data to save.
     * @return Task with created user ID.
     */
    @Override
    public Task<String> create(RawUser rawUser) {
        Task<DocumentReference> t = db.collection(collectionName)
                .add(rawUser);
        return t.continueWith(task -> task.getResult().getId());
    }

    /**
     * Retrieves a user from the 'users' collection.
     * @param s User ID to find.
     * @return Task with user data.
     */
    @Override
    public Task<RawUser> read(String s) {
        Task<DocumentSnapshot> t = db.collection(collectionName)
                .document(s)
                .get();
        return t.continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot d = task.getResult();
                String userName = d.getString("name");
                ArrayList<String> tags = (ArrayList<String>) d.get("tagids");
                ArrayList<String> items = (ArrayList<String>) d.get("itemids");

                return new RawUser(userName, tags, items);
            } else {
                throw new UnsupportedOperationException("unimplemeted");
            }
        });
    }

    /**
     * Updates a user's data in the 'users' collection.
     * @param s User ID to update.
     * @param rawUser Updated user data.
     * @return Task indicating operation status.
     */
    @Override
    public Task<Void> update(String s, RawUser rawUser) {
        return db.collection(collectionName)
                .document(s)
                .set(rawUser);
    }

    /**
     * Deletes a user from the 'users' collection.
     * @param s User ID to remove.
     * @return Task indicating operation status.
     */
    @Override
    public Task<Void> delete(String s) {
        return db.collection(collectionName)
                .document(s)
                .delete();
    }

    /**
     * Represents the raw structure of the user
     * as it exists in the 'users' collection.
     */
    public static class RawUser {
        protected String name;
        protected ArrayList<String> tagids;
        protected ArrayList<String> itemids;

        /**
         * Constructs a new user with details.
         * @param userName User's name.
         * @param tags User's tag IDs.
         * @param items User's item IDs.
         */
        public RawUser(String userName, ArrayList<String> tags, ArrayList<String> items) {
            this.name = userName;
            this.tagids = tags;
            this.itemids = items;
        }

        /**
         * Gets the user's name.
         * @return User's name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the user's tag IDs.
         * @return List of tag IDs.
         */
        public ArrayList<String> getTagids() {
            return tagids;
        }

        /**
         * Gets the user's item IDs.
         * @return List of item IDs.
         */
        public ArrayList<String> getItemids() {
            return itemids;
        }
    }
}
