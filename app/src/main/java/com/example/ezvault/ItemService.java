package com.example.ezvault;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will act as the user-accessible abstraction of the {@link ItemsDAO}. All public
 * database-related methods in this class are Tasks so the end-user has access to when each task has
 * been fully completed (if needed). The class exposes access to the user's items/item list.
 */
public class ItemService {
    private final ItemsDAO itemsDAO;
    private Map<String, Item> itemList;

    public ItemService(FirebaseBundle firebase) {
        itemsDAO = new ItemsDAO(firebase);
    }

    /**
     * Responsible for initializing the user's item list. The method will lookup the user's owned
     * item ids and will fetch each corresponding item; The fetched items will be placed into the
     * item list.
     * @param itemIds
     * @return
     */
    public Task<Void> initItems(List<String> itemIds) {
        itemList = new HashMap<>();
        Task<QuerySnapshot> itemDocs = itemsDAO.fetchItemDocs(itemIds);

        // Add each item to the item list
        return itemDocs.continueWith(task -> {
            task.getResult().forEach(itemDoc -> {

                Log.d("ijiojef", String.valueOf(task.isSuccessful()));

                String key = itemDoc.getId();

                // Retrieve all item attributes. Stored in local variables for readability
                String make = itemDoc.getString("make");
                String model = itemDoc.getString("model");

                Item item = new Item(make, model);
                item.setId(key);

                itemList.put(key, item);
            });

            printItems();
            return null;
        });

    }

    /**
     * Prints the currently loaded items
     */
    private void printItems(){

        Log.d("EZVAULT", "Printing " + String.valueOf(itemList.size()) + " items");

        itemList.keySet().forEach(key -> {
            StringBuilder sb = new StringBuilder(); // Using StringBuilder to clean up String concat

            // Less messy when logging if we use StringBuilder
            sb.append("Make: ").append(itemList.get(key).getMake());
            sb.append("\n");
            sb.append("Model: ").append(itemList.get(key).getModel());

            Log.d("EZVAULT", sb.toString());
        });
    }

    /**
     * Adds an item to the item list
     * @param item
     * @return
     */
    public Task<Void> addItem(Item item){
        return itemsDAO.addItem(item, getItemIds())
                .continueWith(docIdTask -> {
                    String docId = docIdTask.getResult();

                    itemList.put(docId, item);
                    printItems();

                    return null;
                });
    }


    public Task<Void> applyItemEdit(Item updatedItem){
        return itemsDAO.updateItem(updatedItem)
                .continueWith(t ->{
                    itemList.replace(updatedItem.getId(), updatedItem);
                    printItems();

                    return null;
                });
    }

    public Task<Void> deleteItems(List<Item> items){
        return itemsDAO.deleteItems(items, getItemIds())
                .continueWith(t -> {
                    items.forEach(item -> {
                        itemList.remove(item.getId());
                    });

                    printItems();
                    return null;
                });
    }

    public List<Item> getItems(){
        return new ArrayList<>(itemList.values());
    }
    public List<String> getItemIds() { return new ArrayList<>(itemList.keySet()); }
}
