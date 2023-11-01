package com.example.ezvault;

import android.util.Pair;

import com.google.android.gms.tasks.Task;

public abstract class AbstractDAO<T, ID> {
    protected FirebaseBundle firebase;

    public AbstractDAO(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    abstract Task<Pair<T, ID>> create(T t);
    abstract Task<T> read(ID id);
    abstract Task<Void> update(ID id, T t);
    abstract Task<Void> delete(ID id);
}
