package com.example.ezvault.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.data.FilterRepository;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.utils.ItemListView;
import com.example.ezvault.utils.IItemFilter;
import com.example.ezvault.utils.ItemListFilter;
import com.example.ezvault.utils.MainItemFilter;
import com.example.ezvault.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemViewModel extends ViewModel {
    private final MutableLiveData<ItemList> itemList;
    private final MediatorLiveData<ItemListView> itemListView;
    private final LiveData<MainItemFilter> filter;

    private final MutableLiveData<SortedItemListView.SortField> sortField = new MutableLiveData<>();
    private final MutableLiveData<SortedItemListView.SortOrder> sortOrder = new MutableLiveData<>();

    @Inject
    public ItemViewModel(UserManager userManager, FilterRepository filterRepository) {
        filter = filterRepository.getFilter();
        sortField.setValue(filterRepository.getSortField());// Getting sorted fields
        sortOrder.setValue(filterRepository.getSortOrder()); // Getting sort order
        itemList = new MutableLiveData<>(userManager.getUser().getItemList());

        itemListView = new MediatorLiveData<>();
        itemListView.addSource(filter, f -> {
            SortedItemListView.SortField currentSortField = sortField.getValue(); // Get it directly from MutableLiveData
            SortedItemListView.SortOrder currentSortOrder = sortOrder.getValue(); // Get it directly from MutableLiveData
            viewTransformation(itemList.getValue(), f, currentSortField, currentSortOrder);
        });
        itemListView.addSource(itemList, items -> viewTransformation(items, filter.getValue(), sortField.getValue(), sortOrder.getValue()));
        // Add sort fields and source for sort order
//        itemListView.addSource(itemList, items -> viewTransformation(items, filter.getValue()));
        itemListView.addSource(sortField, field -> viewTransformation(itemList.getValue(), filter.getValue(), field, sortOrder.getValue()));
        itemListView.addSource(sortOrder, order -> viewTransformation(itemList.getValue(), filter.getValue(), sortField.getValue(), order));
    }

    private void viewTransformation(ItemList items, IItemFilter filter, SortedItemListView.SortField sortField, SortedItemListView.SortOrder sortOrder) {
        // Apply filtering and sorting logic
        if (filter == null) {
            Log.d("EZVault", "Transforming with null filter");
            itemListView.setValue(items);
        } else {
            Log.d("EZVault", "Transforming with non-null filter");
            // Apply the filtering logic using ItemListFilter
            ItemListFilter filteredListView = new ItemListFilter(items, filter);
            // Extracts List<Item> from ItemListFilter
            if (sortField != null && sortOrder != null) {
                List<Item> filteredItems = extractItemsFromFilter(filteredListView);
                // Create a SortedItemListView and apply the sorting logic
                SortedItemListView sortedListView = new SortedItemListView(filteredItems);
                sortedListView.sortItems(sortField, sortOrder);
                itemListView.setValue(sortedListView);
            } else {
                itemListView.setValue(filteredListView);
            }
        }
    }

    public void synchronizeItems(ItemList items){
        itemList.setValue(items);
    }

    public LiveData<ItemListView> getItemListView() {
        Log.d("EZVault", "Getting item list view.");
        return itemListView;
    }

    public LiveData<Double> getTotalValue() {
        return Transformations.map(itemListView, ItemListView::getTotalValue);
    }

    public LiveData<Integer> getNumberOfItems() {
        return Transformations.map(itemListView, ItemListView::size);
    }

    /**
     * Extract a list of Item objects from the given ItemListFilter that satisfy the filtering criteria.
     * This method iterates over the ItemListFilter instance, which implements the ItemListView interface,
     * and contains the Item object filtered by specific criteria.
     *
     * @param filter ItemListFilter object representing the filtered list of items.
     * @return List<Item> Returns a list of all Item objects that meet the filter criteria.
     *
     */
    private List<Item> extractItemsFromFilter(ItemListFilter filter) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : filter) {
            filteredItems.add(item);
        }
        return filteredItems;
    }
}
