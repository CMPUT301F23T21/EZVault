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
 *
 */
public class ItemsDAO {
    private FirebaseBundle firebase;
    public ItemsDAO(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

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

    public Task<Void> updateItem(Item updatedItem) {
        Map<String, Object> itemFields = new HashMap<>();
        itemFields.put("make", updatedItem.getMake());
        itemFields.put("model", updatedItem.getModel());

        return firebase.getDb()
                .collection("items")
                .document(updatedItem.getId())
                .set(itemFields, SetOptions.merge());
    }

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

    private Task<Void> updateUserItemIds(List<String> allItemIds){
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("itemids", allItemIds);

        return firebase.getDb()
                .collection("users")
                .document(firebase.getAuth().getUid())
                .set(userDoc, SetOptions.merge());
    }
}
