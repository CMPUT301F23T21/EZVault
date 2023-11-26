package com.example.ezvault.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ezvault.model.utils.filter.IItemFilter;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class FilterRepository {
    private final MutableLiveData<IItemFilter> itemFilter;

    @Inject
    public FilterRepository() {
        Log.d("EZVault", "Creating Filter Repository!");
        itemFilter = new MutableLiveData<>(null);
    }

    public LiveData<Boolean> hasFilter() {
        return Transformations.map(itemFilter, Objects::isNull);
    }

    public LiveData<IItemFilter> getFilter() {
        Log.d("EZVault", "Getting filter, null: " + (itemFilter.getValue() == null));
        return itemFilter;
    }

    public void setFilter(@Nullable IItemFilter itemFilter) {
        Log.d("EZVault", "Setting filter, null: " + (itemFilter == null));
        this.itemFilter.setValue(itemFilter);
    }
}
