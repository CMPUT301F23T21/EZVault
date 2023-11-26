package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private Date startDate = null;
    private Date endDate = null;
    private String make = null;
    private final MutableLiveData<String> startDateText = new MutableLiveData<>();
    private final MutableLiveData<String> endDateText = new MutableLiveData<>();

    private List<String> keywords;

    private final SimpleDateFormat format;

    private final Calendar calendar;

    private final FilterRepository filterRepository;

    @Inject
    public FilterViewModel(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
        format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();
        calendar.clear();
    }

    public LiveData<String> getStartDate() {
        return startDateText;
    }

    public LiveData<String> getEndDate() {
        return endDateText;
    }

    public void setStartDate(int year, int month, int day) {
        calendar.set(year, month, day);
        startDate = calendar.getTime();
        startDateText.setValue(format.format(startDate));
    }

    public void setEndDate(int year, int month, int day) {
        calendar.set(year, month, day);
        endDate = calendar.getTime();
        endDateText.setValue(format.format(endDate));
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

        if (startDate != null) {
            start = startDate.toInstant();
        }

        if (endDate != null) {
            end = endDate.toInstant();
        }

        ItemDateFilter dateFilter = new ItemDateFilter(start, end);
        ItemKeywordFilter keywordFilter = new ItemKeywordFilter(keywords);
        ItemMakeFilter makeFilter = new ItemMakeFilter(make);
        IItemFilter filter = new ItemConjunctionFilter(dateFilter, makeFilter, keywordFilter);
        filterRepository.setFilter(filter);
    }

    @FunctionalInterface
    public interface DateConsumer {
        void accept(int year, int month, int day);
    }
}