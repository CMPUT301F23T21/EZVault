/**
 * Handles direct database operations relating to Item objects.
 */

package com.example.ezvault.database;

import android.util.Log;

import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.TaskUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemDAO extends AbstractDAO<Item, String> {
    private final String collectionName = "items";

    private final TagDAO tagDAO;

    private final ImageDAO imageDAO;

    private HashMap<String, Object> toMap(Item item) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("comment", item.getComment());
        map.put("count", item.getCount());
        map.put("value", item.getValue());
        map.put("date", item.getAcquisitionDate());
        map.put("description", item.getDescription());
        map.put("make", item.getMake());
        map.put("model", item.getModel());
        map.put("images", item.getImages().stream().map(Image::getId).toArray());
        map.put("tags", item.getTags().stream().map(Tag::getUid).toArray());

        return map;
    }

    /**
     * Constructs an AbstractDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public ItemDAO(FirebaseBundle firebase) {
        super(firebase);
        this.tagDAO = new TagDAO(firebase);
        this.imageDAO = new ImageDAO(firebase);
    }

    /**
     * Adds a new item to the 'items' collection.
     * @param item The item to be added.
     * @return A task with the new item's ID.
     */
    @Override
    public Task<String> create(Item item) {
        return firebase.getDb().collection(collectionName)
                .add(toMap(item))
                .continueWith(itemTask -> itemTask.getResult().getId());
    }

    private Task<Item> readContinuation(String id, DocumentSnapshot doc) {
        String comment = doc.getString("comment");
        Timestamp date = doc.getTimestamp("date");
        String description = doc.getString("description");
        String make = doc.getString("make");
        String model = doc.getString("model");
        double count = doc.getDouble("count");
        double value = doc.getDouble("value");
        ArrayList<String> tagIds = (ArrayList<String>) doc.get("tags");

        if (tagIds == null) {
            tagIds = new ArrayList<>();
        }

        ArrayList<String> imageIds = (ArrayList<String>) doc.get("images");

        if (imageIds == null) {
            imageIds = new ArrayList<>();
        }

        Task<ArrayList<Tag>> tagsTask = tagDAO.pluralRead(tagIds);
        Task<ArrayList<Image>> imagesTask = imageDAO.pluralRead(imageIds);

        return Tasks.whenAllSuccess(tagsTask, imagesTask)
                .onSuccessTask(tasks -> {
                    ArrayList<Tag> tags = (ArrayList<Tag>)tasks.get(0);

                    if (tags == null) {
                        tags = new ArrayList<>();
                    }

                    ArrayList<Image> images = (ArrayList<Image>)tasks.get(1);

                    if (images == null) {
                        images = new ArrayList<>();
                    }

                    return Tasks.forResult(new ItemBuilder()
                            .setAcquisitionDate(date)
                            .setComment(comment)
                            .setDescription(description)
                            .setId(id)
                            .setMake(make)
                            .setModel(model)
                            .setCount(count)
                            .setValue(value)
                            .setTags(tags)
                            .setImages(images)
                            .build()
                    );

                });
    }

    /**
     * Reads an item from the 'items' collection.
     * @param id The ID of the item to fetch.
     * @return A task with the item details.
     */
    @Override
    public Task<Item> read(String id) {
        Log.v("EZVault", "Reading item: " + id);
        Task<DocumentSnapshot> docTask = firebase.getDb()
                .collection(collectionName)
                .document(id)
                .get();
        return docTask.onSuccessTask(d -> readContinuation(id, d));
    }

    /**
     * Updates an item in the 'items' collection.
     * @param id The ID of the item to update.
     * @param item The item with updated information.
     * @return A task indicating the operation status.
     */
    @Override
    public Task<Void> update(String id, Item item) {
        Log.v("EZVault", "Updating item: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .set(toMap(item));
    }

    /**
     * Removes an item from the 'items' collection.
     * @param id The ID of the item to remove.
     * @return A task indicating the operation status.
     */
    @Override
    public Task<Void> delete(String id) {
        Log.v("EZVault", "Deleting item: " + id);
        return firebase.getDb().collection(collectionName)
                .document(id)
                .delete();
    }
}
