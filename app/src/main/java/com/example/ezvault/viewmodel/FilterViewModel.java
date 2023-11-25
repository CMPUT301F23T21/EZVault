package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A view model for the filter fragment.
 */
public class FilterViewModel extends ViewModel {
    private final MutableLiveData<String> startDate = new MutableLiveData<>();
    private final MutableLiveData<String> endDate = new MutableLiveData<>();
    private final MutableLiveData<String> make = new MutableLiveData<>();

    private final SimpleDateFormat format;

    private final Calendar calendar;

    public FilterViewModel() {
        format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();
        calendar.clear();
    }

    public LiveData<String> getStartDate() {
        return startDate;
    }

    public LiveData<String> getEndDate() {
        return endDate;
    }

    public LiveData<String> getMake() {
        return make;
    }

    public void setStartDate(int year, int month, int day) {
        calendar.set(year, month, day);
        startDate.setValue(format.format(calendar.getTime()));
    }

    public void setEndDate(int year, int month, int day) {
        calendar.set(year, month, day);
        endDate.setValue(format.format(calendar.getTime()));
    }

    private List<String> parseKeywords(String input) {
        return Arrays.asList(input.split(" "));
    }

    // TODO
    public void setKeywords(String input) {
    }

    // TODO
    public void setMake(String input) {
    }

    @FunctionalInterface
    public interface DateConsumer {
        void accept(int year, int month, int day);
    }
}