package com.example.ezvault.utils;

import androidx.annotation.NonNull;

import com.example.ezvault.model.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that represents a filter on top of an item list.
 */
public class ItemListFilter implements ItemListView {
    /**
     * The underlying item list.
     */
    protected final ItemListView itemListView;
    /**
     * The indices that are the positions
     * of the valid items that comply with the filter.
     */
    protected final ArrayList<Integer> indices;
    /**
     * Whether or not the indices accurately represent
     * the indices of the items that are valid.
     */
    private boolean valid = false;

    /**
     * The filter for each item.
     */
    private IItemFilter itemFilter;

    /**
     * Create a new filter on an item-list
     * @param itemListView The view of a list of items to filter on.
     */
    public ItemListFilter(ItemListView itemListView, IItemFilter itemFilter) {
        this.indices = new ArrayList<>();
        this.itemListView = itemListView;
        this.itemFilter = itemFilter;
    }

    /**
     * Try to validate the indices.
     * @return The new value of valid, true on success, false on failure.
     */
    private boolean validate() {
        indices.clear();
        for (int i = 0; i < itemListView.size(); i++) {
            if (itemFilter.keep(itemListView.get(i))) {
                indices.add(i);
            }
        }
        return true;
    }

    /**
     * If the indices are invalid validate them.
     */
    private void updateIndices() {
        if (!valid) {
            valid = validate();
        }
    }

    /**
     * Mark the indices as invalid.
     */
    public final void invalidate() {
        valid = false;
    }

    /**
     * Make an iterator of the valid filtered items.
     * @return An iterator of the filtered items.
     */
    @NonNull
    @Override
    public final Iterator<Item> iterator() {
        updateIndices();
        return new Iterator<Item>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < indices.size();
            }

            @Override
            public Item next() {
                if (hasNext()) {
                    Item next = itemListView.get(indices.get(index));
                    index++;
                    return next;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    /**
     * Get the number of valid items.
     * @return The number of valid items.
     */
    @Override
    public final int size() {
        updateIndices();
        return indices.size();
    }

    /**
     * Check whether there is valid items.
     * @return Whether or not there are valid items.
     */
    @Override
    public final boolean isEmpty() {
        updateIndices();
        return indices.isEmpty();
    }

    /**
     * Get a valid item that complies with the filter.
     * @param position The position to get the item.
     * @return The item at that position.
     */
    @Override
    public final Item get(int position) {
        updateIndices();
        return itemListView.get(indices.get(position));
    }
}
