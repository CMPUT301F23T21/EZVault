package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.data.FilterRepository;
import com.example.ezvault.model.utils.filter.IItemFilter;
import com.example.ezvault.model.utils.filter.ItemConjunctionFilter;
import com.example.ezvault.model.utils.filter.ItemDateFilter;
import com.example.ezvault.model.utils.filter.ItemKeywordFilter;
import com.example.ezvault.model.utils.filter.ItemMakeFilter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * A view model for the filter fragment.
 */
@HiltViewModel
public class FilterViewModel extends ViewModel {
    private final MutableLiveData<Date> startDate = new MutableLiveData<>();
    private final MutableLiveData<Date> endDate = new MutableLiveData<>();
    private String make;
    private final LiveData<String> startDateText;
    private final LiveData<String> endDateText;

    private List<String> keywords;

    private final Calendar calendar;

    private final FilterRepository filterRepository;

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        if (date != null) {
            return format.format(date);
        } else {
            return null;
        }
    }

    @Inject
    public FilterViewModel(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
        calendar = Calendar.getInstance();
        calendar.clear();

        // set cached values
        startDate.setValue(filterRepository.getStartDate());
        endDate.setValue(filterRepository.getEndDate());
        make = filterRepository.getMake();
        keywords = filterRepository.getKeywords();

        startDateText = Transformations.map(startDate, this::formatDate);
        endDateText = Transformations.map(endDate, this::formatDate);
    }

    public LiveData<String> getStartDate() {
        return startDateText;
    }

    public LiveData<String> getEndDate() {
        return endDateText;
    }

    public void setStartDate(int year, int month, int day) {
        calendar.set(year, month, day);
        startDate.setValue(calendar.getTime());
    }

    public void setEndDate(int year, int month, int day) {
        calendar.set(year, month, day);
        endDate.setValue(calendar.getTime());
    }

    private List<String> parseKeywords(String input) {
        return Arrays.asList(input.split(" "));
    }

    // TODO
    public void setKeywords(String input) {
        keywords = parseKeywords(input);
    }

    // TODO
    public void setMake(String input) {
        make = input;
    }

    public void apply() {
        Instant start = null;
        Instant end = null;

        if (startDate.getValue() != null) {
            start = startDate.getValue().toInstant();
        }

        if (endDate.getValue() != null) {
            end = endDate.getValue().toInstant();
        }

        ItemDateFilter dateFilter = new ItemDateFilter(start, end);
        ItemKeywordFilter keywordFilter = new ItemKeywordFilter(keywords);
        ItemMakeFilter makeFilter = new ItemMakeFilter(make);
        IItemFilter filter = new ItemConjunctionFilter(dateFilter, makeFilter, keywordFilter);

        filterRepository.setMake(make);
        filterRepository.setKeywords(keywords);
        filterRepository.setStartDate(startDate.getValue());
        filterRepository.setEndDate(endDate.getValue());

        // set filter to use
        filterRepository.setFilter(filter);
    }

    public String getMake() {
        return make;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    @FunctionalInterface
    public interface DateConsumer {
        void accept(int year, int month, int day);
    }
}