package com.example.ezvault;

import android.util.Pair;

import com.google.android.gms.tasks.Task;

public class UserDAO extends AbstractDAO<User, String> {

    public UserDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    @Override
    Task<Pair<User, String>> create(User user) {
        return null;
    }

    @Override
    Task<User> read(String s) {
        return null;
    }

    @Override
    Task<Void> update(String s, User user) {
        return null;
    }

    @Override
    Task<Void> delete(String s) {
        return null;
    }
}
