package com.example.ezvault.database;

import android.util.Log;

import com.example.ezvault.model.Tag;
import com.google.android.gms.tasks.Task;

public class TagDAO extends AbstractDAO<Tag,String> {
    private final String collectionName = "tags";

    /**
     * Constructs a TagDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public TagDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    @Override
    public Task<String> create(Tag tag) {
        return firebase.getDb().collection(collectionName)
                .add(tag)
                .continueWith(tagTask -> tagTask.getResult().getId());
    }

    @Override
    public Task<Tag> read(String id) {
        Log.v("EZVault", "Reading tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .get()
                .continueWith(task -> {
                    String identifier = task.getResult().getString("identifier");
                    return new Tag(identifier);
                });
    }

    @Override
    public Task<Void> update(String id, Tag tag) {
        Log.v("EZVault", "Updating tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(tag);
    }

    @Override
    public Task<Void> delete(String id) {
        Log.v("EZVault", "Deleting tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
