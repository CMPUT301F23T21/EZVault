package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.utils.ItemListView;
import com.example.ezvault.model.utils.filter.IItemFilter;
import com.example.ezvault.model.utils.filter.ItemListFilter;
import com.example.ezvault.utils.UserManager;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemViewModel extends ViewModel {
    private final ItemList itemList;
    private final MutableLiveData<ItemListView> itemListView;

    @Inject
    public ItemViewModel(UserManager userManager) {
        itemList = userManager.getUser().getItemList();
        itemListView = new MutableLiveData<>(itemList);
    }

    public void setItemFilter(@Nullable IItemFilter itemFilter) {
        if (itemFilter == null) {
            itemListView.setValue(itemList);
        } else {
            itemListView.setValue(new ItemListFilter(itemList, itemFilter));
        }
    }

    public LiveData<ItemListView> getItemListView() {
        return itemListView;
    }

    public LiveData<Double> getTotalValue() {
        return Transformations.map(itemListView, ItemListView::getTotalValue);
    }

    public LiveData<Integer> getNumberOfItems() {
        return Transformations.map(itemListView, ItemListView::size);
    }
}
