package com.example.ezvault;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that interacts with the Firestore database in regards to the user's items
 */
public class ItemsDAO {
    private FirebaseBundle firebase;
    public ItemsDAO(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    /**
     * Fetches all the item documents that the user owns. This should only be called upon
     * initialization
     * @param itemIds
     * @return
     */
    public Task<QuerySnapshot> fetchItemDocs(List<String> itemIds){
        CollectionReference itemCollection = firebase.getDb()
                .collection("items");

        // We cant execute 'whereIn' with empty array. This is a workaround.
        if (itemIds.size() == 0){
            itemIds.add("-dummy-");
        }

        return itemCollection
                .whereIn(FieldPath.documentId(), itemIds)
                .get();
    }

    /**
     * Adds an item to the database
     * @param item Item to be added
     * @param allItemIds The ids of the user's currently owned items
     * @return
     */
    public Task<String> addItem(Item item, List<String> allItemIds) {
        Map<String, Object> itemFields = new HashMap<>();
        itemFields.put("make", item.getMake());
        itemFields.put("model", item.getModel());

        // Create the new document/item id using Firestore's auto-id
        DocumentReference itemDoc = firebase.getDb()
                .collection("items")
                .document();

        String docId = itemDoc.getId();

        // Add our new item id
        allItemIds.add(docId);

        return itemDoc.set(itemFields)
                .continueWithTask(t -> updateUserItemIds(allItemIds))
                .continueWith(t -> docId);
    }

    /**
     * Updates the attributes of an existing item in the database
     * @param updatedItem The item with updated attributes
     * @return
     */
    public Task<Void> updateItem(Item updatedItem) {
        Map<String, Object> itemFields = new HashMap<>();
        itemFields.put("make", updatedItem.getMake());
        itemFields.put("model", updatedItem.getModel());

        return firebase.getDb()
                .collection("items")
                .document(updatedItem.getId())
                .set(itemFields, SetOptions.merge());
    }

    /**
     * Deletes items from the database
     * @param items Items to be deleted
     * @param allItemIds The ids of the user's currently owned items
     * @return
     */
    public Task<Void> deleteItems(List<Item> items, List<String> allItemIds){
        List<String> idsPendingDeletion = new ArrayList<>();

        items.forEach(item -> {
            String itemId = item.getId();
            allItemIds.remove(itemId);
            idsPendingDeletion.add(itemId);
        });

        // First we remove the references from the user. Then we delete the items from the db
        return updateUserItemIds(allItemIds)
                .continueWithTask(t -> deleteItemDocs(idsPendingDeletion));
    }

    /**
     * Used in conjunction with deleteItems. This method deletes the item docs in the items
     * collection
     * @param itemIds The ids of the items to be deleted
     * @return
     */
    private Task<Void> deleteItemDocs(List<String> itemIds){
        WriteBatch batch = firebase.getDb().batch();

        return firebase.getDb()
                .collection("items")
                .whereIn(FieldPath.documentId(), itemIds)
                .get()
                .continueWith(queryTask -> {
                    QuerySnapshot idsToDelete = queryTask.getResult();

                    idsToDelete.forEach(doc -> {
                        batch.delete(doc.getReference());
                    });

                    return null;
                }).continueWithTask(t -> batch.commit());
    }

    /**
     * Updates the user's local item store within the database
     * @param allItemIds The ids of the user's currently owned items
     * @return
     */
    private Task<Void> updateUserItemIds(List<String> allItemIds){
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("itemids", allItemIds);

        return firebase.getDb()
                .collection("users")
                .document(firebase.getAuth().getUid())
                .set(userDoc, SetOptions.merge());
    }
}
