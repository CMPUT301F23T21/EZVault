package com.example.ezvault.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.model.Item;

/**
 *
 */
public class SelectedItemViewModel extends ViewModel {
    private final MutableLiveData<Item> itemData = new MutableLiveData<>();

    public void set(Item item) {
        this.itemData.setValue(item);
    }

    public MutableLiveData<Item> get() {
        return this.itemData;
    }
}
