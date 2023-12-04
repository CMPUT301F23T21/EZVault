package com.example.ezvault.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ezvault.model.utils.filter.MainItemFilter;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class FilterRepository {
    private final MutableLiveData<MainItemFilter> itemFilter;

    @Inject
    public FilterRepository() {
        itemFilter = new MutableLiveData<>(null);
    }

    public LiveData<MainItemFilter> getFilter() {
        return itemFilter;
    }

    public void setFilter(@Nullable MainItemFilter itemFilter) {
        this.itemFilter.setValue(itemFilter);
    }
}
