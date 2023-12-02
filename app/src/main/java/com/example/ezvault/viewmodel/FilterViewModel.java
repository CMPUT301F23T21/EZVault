package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.data.FilterRepository;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.utils.filter.ItemDateFilter;
import com.example.ezvault.model.utils.filter.ItemKeywordFilter;
import com.example.ezvault.model.utils.filter.ItemMakeFilter;
import com.example.ezvault.model.utils.filter.ItemTagFilter;
import com.example.ezvault.model.utils.filter.MainItemFilter;
import com.example.ezvault.utils.UserManager;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
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
    // live data set from view
    private final MutableLiveData<Date> startDate = new MutableLiveData<>();
    private final MutableLiveData<Date> endDate = new MutableLiveData<>();

    // live data dependent on the dates
    private final LiveData<String> startDateText;
    private final LiveData<String> endDateText;

    // non-live state
    private String make;
    private List<String> keywords;
    private List<Tag> tags;

    private final Calendar calendar = Calendar.getInstance();
    private final FilterRepository filterRepository;
    private final UserManager userManager;

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        if (date != null) {
            return format.format(date);
        } else {
            return null;
        }
    }

    private void initializePreviousState() {
        MainItemFilter prevFilter = filterRepository.getFilter().getValue();

        if (prevFilter == null) return;

        startDate.setValue(prevFilter.getStartDate());
        endDate.setValue(prevFilter.getEndDate());
        make = prevFilter.getMake();
        keywords = prevFilter.getKeywords();
        tags = prevFilter.getTags();
    }

    @Inject
    public FilterViewModel(UserManager userManager, FilterRepository filterRepository) {
        this.userManager = userManager;
        this.filterRepository = filterRepository;

        // reset calendar so seconds, millis are zeroed
        calendar.clear();

        // set previous values
        initializePreviousState();

        // set date displays to track date state
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

    // TODO better tag input
    public void setTags(String input) {
        String[] tagNames = input.split(",\\s*");
        tags = new ArrayList<>();
        for (String tagName : tagNames) {
            for (Tag tag : userManager.getUser().getItemList().getTags()) {
                if (tag.getContents().equals(tagName)) {
                    tags.add(tag);
                    break;
                }
            }
        }
    }

    // TODO
    private List<String> parseKeywords(String input) {
        return Arrays.asList(input.split(" "));
    }

    public void setKeywords(String input) {
        keywords = parseKeywords(input);
    }

    // TODO
    public void setMake(String input) {
        make = input.equals("") ? null : input;
    }

    private MainItemFilter createFilter() {
        Instant start = startDate.getValue() != null ? startDate.getValue().toInstant() : null;
        Instant end = endDate.getValue() != null ? endDate.getValue().toInstant() : null;

        MainItemFilter filter = new MainItemFilter();
        filter.setDateFilter(new ItemDateFilter(start, end));
        filter.setMakeFilter(new ItemMakeFilter(make));
        filter.setKeywordFilter(new ItemKeywordFilter(keywords));
        filter.setTagFilter(new ItemTagFilter(tags != null ? tags : new ArrayList<>()));

        return filter;
    }

    public void apply() {
        MainItemFilter filter = createFilter();

        filterRepository.setFilter(filter);
    }

    public void reset() {
        startDate.setValue(null);
        endDate.setValue(null);
        make = null;
        keywords = null;

        apply();
    }

    public String getMake() {
        return make;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    public List<Tag> getTags() {
        return tags;
    }
    @FunctionalInterface
    public interface DateConsumer {
        void accept(int year, int month, int day);
    }

    public void setSortField(SortedItemListView.SortField sortField) {
        filterRepository.setSortField(sortField);
    }

    public void setSortOrder(SortedItemListView.SortOrder sortOrder) {
        filterRepository.setSortOrder(sortOrder);
    }
}