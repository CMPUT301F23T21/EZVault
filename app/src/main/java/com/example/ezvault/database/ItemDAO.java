package com.example.ezvault.database;

import android.util.Log;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.TaskUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ItemDAO extends AbstractDAO<Item, String> {
    private final String collectionName = "items";

    private final TagDAO tagDAO;

    /**
     * Constructs an AbstractDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public ItemDAO(FirebaseBundle firebase) {
        super(firebase);
        this.tagDAO = new TagDAO(firebase);
    }

    @Override
    public Task<String> create(Item item) {
        return firebase.getDb().collection(collectionName)
                .add(item)
                .continueWith(itemTask -> itemTask.getResult().getId());
    }

    public Task<Item> readContinuation(String id, DocumentSnapshot doc) {
        String comment = doc.getString("comment");
        Timestamp date = doc.getTimestamp("date");
        String description = doc.getString("description");
        String make = doc.getString("make");
        String model = doc.getString("model");
        ArrayList<String> tagIds = (ArrayList<String>) doc.get("tags");
        Task<ArrayList<Tag>> tagsTask = tagDAO.pluralRead(tagIds);
        return TaskUtils.onSuccess(tagsTask, tags -> new ItemBuilder()
                .setAcquisitionDate(date)
                .setComment(comment)
                .setDescription(description)
                .setId(id)
                .setMake(make)
                .setModel(model)
                .build());
    }

    @Override
    public Task<Item> read(String id) {
        Log.v("EZVault", "Reading item: " + id);
        Task<DocumentSnapshot> docTask = firebase.getDb()
                .collection(collectionName)
                .document(id)
                .get();
        return docTask.onSuccessTask(d -> readContinuation(id, d));
    }

    @Override
    public Task<Void> update(String id, Item item) {
        Log.v("EZVault", "Updating item: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(item);
    }

    @Override
    public Task<Void> delete(String id) {
        Log.v("EZVault", "Deleting item: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
