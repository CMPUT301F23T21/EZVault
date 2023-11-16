package com.example.ezvault.model.utils;

import android.util.Log;

import com.example.ezvault.model.Item;
import com.google.firebase.Timestamp;

import java.time.Instant;

/**
 * Filter for an item based on its date
 */
public class ItemDateFilter implements IItemFilter {
    private Instant start;
    private boolean startInclusive;
    private Instant end;
    private boolean endInclusive;

    /**
     * Set the start date for the filter to use.
     * If null then the start is unbounded
     * @param start The start date
     */
    public void setStart(Instant start) {
        this.start = start;
    }

    /**
     * Set whether the start date is inclusive or exclusive
     * @param startInclusive Whether or not the start date is included
     */
    public void setStartInclusive(boolean startInclusive) {
        this.startInclusive = startInclusive;
    }


    /**
     * Set the end date for the filter to use
     * If null then the end is unbounded
     * @param end The end date
     */
    public void setEnd(Instant end) {
        this.end = end;
    }

    /**
     * Set whether the end date is inclusive or exclusive
     * @param endInclusive Whether or not the end date is included
     */
    public void setEndInclusive(boolean endInclusive) {
        this.endInclusive = endInclusive;
    }

    /**
     * Create a new filter based on date, with an inclusive range
     * @param start The start date
     * @param end The end date
     */
    public ItemDateFilter(Instant start, Instant end) {
        this(start, true, end, true);
    }

    /**
     * Create a new filter
     * @param start The start date
     * @param startInclusive Whether to include start date
     * @param end The end date
     * @param endInclusive Whether to include end date
     */
    public ItemDateFilter(Instant start, boolean startInclusive, Instant end, boolean endInclusive) {
        this.start = start;
        this.end = end;
        this.startInclusive = startInclusive;
        this.endInclusive = endInclusive;
    }

    private boolean checkStart(Instant instant) {
        if (start == null) {
            return true;
        }
        if (startInclusive) {
            return start.compareTo(instant) <= 0;
        } else {
            return start.compareTo(instant) < 0;
        }
    }

    private boolean checkEnd(Instant instant) {
        if (end == null) {
            return true;
        }
        if (endInclusive) {
            return end.compareTo(instant) >= 0;
        } else {
            return end.compareTo(instant) > 0;
        }
    }

    @Override
    public boolean keep(Item item) {
        Timestamp timestamp = item.getAcquisitionDate();
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanoseconds());
        return checkStart(instant) && checkEnd(instant);
    }
}
