package com.example.ezvault.database;

import com.example.ezvault.model.Item;
import com.google.android.gms.tasks.Task;

public class ItemDAO extends AbstractDAO<Item, String> {
    private final String collectionName = "items";

    /**
     * Constructs an AbstractDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public ItemDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    @Override
    public Task<String> create(Item item) {
        return firebase.getDb().collection(collectionName)
                .add(item)
                .continueWith(itemTask -> itemTask.getResult().getId());
    }

    @Override
    public Task<Item> read(String id) {
        return firebase.getDb().collection(collectionName)
                .document(id)
                .get()
                .continueWith(task -> task.getResult().toObject(Item.class));
    }

    @Override
    public Task<Void> update(String id, Item item) {
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(item);
    }

    @Override
    public Task<Void> delete(String id) {
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
