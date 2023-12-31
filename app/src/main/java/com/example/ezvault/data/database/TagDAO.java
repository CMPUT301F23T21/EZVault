/*
Manages direct database operations in the 'tags' collections.
 */

package com.example.ezvault.data.database;

import android.util.Log;

import com.example.ezvault.model.Tag;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that deals with operations in the 'tags' collection.
 */
public class TagDAO extends AbstractDAO<Tag,String> {
    private final String collectionName = "tags";

    private HashMap<String, Object> toMap(Tag tag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("identifier", tag.getContents());
        return map;
    }

    private Tag fromMap(Map<String, Object> map, String id) {
        return new Tag((String) map.get("identifier"), id);
    }

    /**
     * Constructs a TagDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public TagDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    /**
     * Adds a new tag to the 'tags' collection.
     * @param tag Tag to be saved.
     * @return Task with the ID of the new tag.
     */
    @Override
    public Task<String> create(Tag tag) {
        return firebase.getDb().collection(collectionName)
                .add(toMap(tag))
                .continueWith(tagTask -> tagTask.getResult().getId());
    }

    /**
     * Fetches a tag from the 'tags' collection.
     * @param id ID of the tag to fetch.
     * @return Task with the tag data.
     */
    @Override
    public Task<Tag> read(String id) {
        Log.v("EZVault", "Reading tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .get()
                .continueWith(task -> fromMap(task.getResult().getData(), task.getResult().getId()));
    }

    /**
     * Updates an existing tag in the 'tags' collection.
     * @param id ID of the tag to update.
     * @param tag Updated tag information.
     * @return Task indicating the completion of the update.
     */
    @Override
    public Task<Void> update(String id, Tag tag) {
        Log.v("EZVault", "Updating tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(toMap(tag));
    }

    /**
     * Removes a tag from the 'tags' collection.
     * @param id ID of the tag to delete.
     * @return Task indicating the completion of the deletion.
     */
    @Override
    public Task<Void> delete(String id) {
        Log.v("EZVault", "Deleting tag: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
