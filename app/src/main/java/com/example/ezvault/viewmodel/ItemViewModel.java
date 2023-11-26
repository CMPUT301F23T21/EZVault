package com.example.ezvault.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.data.FilterRepository;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.utils.ItemListView;
import com.example.ezvault.model.utils.filter.IItemFilter;
import com.example.ezvault.model.utils.filter.ItemListFilter;
import com.example.ezvault.utils.UserManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemViewModel extends ViewModel {
    private final LiveData<ItemList> itemList;
    private final MediatorLiveData<ItemListView> itemListView;
    private final LiveData<IItemFilter> filter;

    @Inject
    public ItemViewModel(UserManager userManager, FilterRepository filterRepository) {
        filter = filterRepository.getFilter();
        itemList = new MutableLiveData<>(userManager.getUser().getItemList());

        itemListView = new MediatorLiveData<>();
        itemListView.addSource(filter, f -> viewTransformation(itemList.getValue(), f));
        itemListView.addSource(itemList, items -> viewTransformation(items, filter.getValue()));
    }

    private void viewTransformation(ItemList items, IItemFilter filter) {
        if (filter == null) {
            Log.d("EZVault", "Transforming with null filter");
            itemListView.setValue(items);
        } else {
            Log.d("EZVault", "Transforming with non-null filter");
            itemListView.setValue(new ItemListFilter(items, filter));
        }
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
}
