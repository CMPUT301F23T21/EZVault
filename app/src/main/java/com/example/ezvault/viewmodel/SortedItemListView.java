package com.example.ezvault.viewmodel;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.utils.ItemListView;
import com.google.firebase.Timestamp;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortedItemListView implements ItemListView {
    private List<Item> items;
    private SortOrder currentSortOrder = SortOrder.ASCENDING;
    private SortField currentSortField = SortField.DATE;

    // Enum for sort order
    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    // Enum for sort field
    public enum SortField {
        DATE, DESCRIPTION, MAKE, VALUE
    }

    // Constructor
    public SortedItemListView(List<Item> items) {
        this.items = items;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Item get(int position) {
        return items.get(position);
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    // Method to sort the items list
    public void sortItems(SortField field, SortOrder order) {
        Comparator<Item> comparator = null;

        switch (field) {
            case DATE:
                comparator = Comparator.comparing(Item::getAcquisitionDate, Comparator.nullsFirst(Timestamp::compareTo));
                break;
            case DESCRIPTION:
                comparator = Comparator.comparing(Item::getDescription, Comparator.nullsFirst(String::compareTo));
                break;
            case MAKE:
                comparator = Comparator.comparing(Item::getMake, Comparator.nullsFirst(String::compareTo));
                break;
            case VALUE:
                comparator = Comparator.comparingDouble(Item::getValue);
                break;
        }

        if (comparator != null) {
            if (order == SortOrder.DESCENDING) {
                comparator = comparator.reversed();
            }
            Collections.sort(items, comparator);
        }

        currentSortField = field;
        currentSortOrder = order;
    }

    // Method to reverse the current sort order
    public void reverseSortOrder() {
        sortItems(currentSortField, currentSortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING);
    }
}
