package com.example.ezvault.model.utils;

import com.example.ezvault.model.ItemList;
import com.google.firebase.Timestamp;

import java.time.Instant;

public class ItemListDateFilter extends ItemListFilter {
    private Instant startDate;
    private Instant endDate;

    public ItemListDateFilter(ItemList itemList, Instant startDate, Instant endDate) {
        super(itemList);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
        invalidate();
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
        invalidate();
    }

    public void setRange(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        invalidate();
    }

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
