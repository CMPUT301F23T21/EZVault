package com.example.ezvault.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ezvault.viewmodel.SortedItemListView;
import com.example.ezvault.utils.MainItemFilter;

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
    private SortedItemListView.SortField sortField;
    private SortedItemListView.SortOrder sortOrder;

    public void setSortField(SortedItemListView.SortField sortField) {
        this.sortField = sortField;
    }

    public void setSortOrder(SortedItemListView.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    public SortedItemListView.SortField getSortField() {
        return sortField;
    }

    public SortedItemListView.SortOrder getSortOrder() {
        return sortOrder;
    }
}
