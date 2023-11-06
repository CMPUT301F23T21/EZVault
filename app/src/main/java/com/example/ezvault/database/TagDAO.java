package com.example.ezvault.database;

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
        return firebase.getDb().collection(collectionName)
                .document(id)
                .get()
                .continueWith(task -> task.getResult().toObject(Tag.class));
    }

    @Override
    public Task<Void> update(String id, Tag tag) {
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(tag);
    }

    @Override
    public Task<Void> delete(String id) {
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
