package com.example.ezvault.database;

import com.example.ezvault.FirebaseBundle;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

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
    Task<String> create(RawUser rawUser) {
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
    Task<Void> update(String s, RawUser rawUser) {
        return null;
    }

    @Override
    Task<Void> delete(String s) {
        return null;
    }

    @Override
    String createTransactional(Transaction transaction, RawUser rawUser) {
        return null;
    }

    @Override
    RawUser readTransactional(Transaction transaction, String s) {
        return null;
    }

    @Override
    void updateTransactional(Transaction transaction, RawUser rawUser, String s) {

    }

    @Override
    void deleteTransactional(Transaction transaction, String s) {

    }

    public static class RawUser {
        protected String userName;
        protected ArrayList<String> tags;
        protected ArrayList<String> items;

        @Override
        public String toString() {
            return "RawUser{" +
                    "userName='" + userName + '\'' +
                    ", tags=" + tags +
                    ", items=" + items +
                    '}';
        }

        public RawUser(String userName, ArrayList<String> tags, ArrayList<String> items) {
            this.userName = userName;
            this.tags = tags;
            this.items = items;
        }

        public String getUserName() {
            return this.userName;
        }

        public ArrayList<String> getTags() {
            return tags;
        }

        public ArrayList<String> getItems() {
            return items;
        }
    }
}
