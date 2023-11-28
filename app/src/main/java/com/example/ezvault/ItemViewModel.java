package com.example.ezvault;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.model.Item;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<Item> itemData = new MutableLiveData<Item>();

    public void set(Item item) {
        this.itemData.setValue(item);
    }

    public MutableLiveData<Item> get() {
        return this.itemData;
    }
}
