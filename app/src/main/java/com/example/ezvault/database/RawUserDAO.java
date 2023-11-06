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

    @Override
    public Task<String> create(RawUser rawUser) {
        Task<DocumentReference> t = db.collection(collectionName)
                .add(rawUser);
        return t.continueWith(task -> task.getResult().getId());
    }

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

    @Override
    public Task<Void> update(String s, RawUser rawUser) {
        return db.collection(collectionName)
                .document(s)
                .set(rawUser);
    }

    @Override
    public Task<Void> delete(String s) {
        return db.collection(collectionName)
                .document(s)
                .delete();
    }

    public static class RawUser {
        protected String name;
        protected ArrayList<String> tagids;
        protected ArrayList<String> itemids;

        public RawUser(String userName, ArrayList<String> tags, ArrayList<String> items) {
            this.name = userName;
            this.tagids = tags;
            this.itemids = items;
        }

        public String getName() {
            return this.name;
        }

        public ArrayList<String> getTagids() {
            return tagids;
        }

        public ArrayList<String> getItemids() {
            return itemids;
        }
    }
}
