package com.example.ezvault.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ezvault.model.utils.filter.IItemFilter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class FilterRepository {
    private String make = null;
    private List<String> keywords = null;
    private Date endDate = null;
    private Date startDate = null;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

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
