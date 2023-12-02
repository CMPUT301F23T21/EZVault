package com.example.ezvault.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ezvault.model.utils.filter.IItemFilter;
import com.example.ezvault.model.utils.filter.MainItemFilter;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class FilterRepository {
    private final MutableLiveData<MainItemFilter> itemFilter;

    @Inject
    public FilterRepository() {
        itemFilter = new MutableLiveData<>(null);
    }

    public LiveData<Boolean> hasFilter() {
        return Transformations.map(itemFilter, Objects::isNull);
    }

    public LiveData<MainItemFilter> getFilter() {
        return itemFilter;
    }

    public void setFilter(@Nullable MainItemFilter itemFilter) {
        this.itemFilter.setValue(itemFilter);
    }
}
