package com.example.ezvault.database;

import com.example.ezvault.FirebaseBundle;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Transaction;

public class RawUserDAO extends AbstractDAO<RawUserDAO.RawUser, String> {
    /**
     * Constructs a RawUserDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public RawUserDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    @Override
    Task<String> create(RawUser rawUser) {
        return null;
    }

    @Override
    Task<RawUser> read(String s) {
        return null;
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

    }
}
