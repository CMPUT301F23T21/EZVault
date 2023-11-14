package com.example.ezvault.model.utils;

import com.example.ezvault.model.ItemList;
import com.google.firebase.Timestamp;

import java.time.Instant;

/**
 * A filter for an item list based on a range of dates.
 * When the underlying list changes, invalidate() must be called.
 */
public class ItemListDateFilter extends ItemListFilter {
    /**
     * The start date for the range.
     */
    private Instant startDate;
    /**
     * The end date for the range.
     */
    private Instant endDate;

    /**
     * Construct an item list filter based on a date range.
     * @param itemList The item list to be filtered.
     * @param startDate The start date to filter with.
     * @param endDate The end date to filter with.
     */
    public ItemListDateFilter(ItemList itemList, Instant startDate, Instant endDate) {
        super(itemList);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Set the start date of the filter's range.
     * @param startDate The start date of the filter's range.
     */
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
        invalidate();
    }

    /**
     * Set the end date of the filter's range.
     * @param endDate The end date of the filter's range.
     */
    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
        invalidate();
    }

    /**
     * Set the range of the filter.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     */
    public void setRange(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        invalidate();
    }

    /**
     * Validates the indices list.
     * @return Whether or not the updated indices are valid or not.
     */
    @Override
    protected boolean validate() {
        indices.clear();
        for (int i = 0; i < itemList.size(); i++) {
            Timestamp timestamp = itemList.get(i).getAcquisitionDate();
            Instant date = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanoseconds());
            if (date.isAfter(startDate) && date.isBefore(endDate)) {
                indices.add(i);
            }
        }
        return true;
    }
}
