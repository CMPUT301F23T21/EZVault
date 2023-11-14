package com.example.ezvault.model.utils;

import androidx.annotation.NonNull;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An abstract class that represents a filter on top of an item list.
 */
abstract class ItemListFilter implements ItemListView {
    /**
     * The underlying item list.
     */
    protected final ItemList itemList;
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
     * Create a new filter on an item-list
     * @param itemList The list of items to filter on.
     */
    public ItemListFilter(ItemList itemList) {
        this.indices = new ArrayList<>();
        this.itemList = itemList;
    }

    /**
     * Try to validate the indices.
     * @return The new value of valid, true on success, false on failure.
     */
    protected abstract boolean validate();

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
                    Item next = itemList.get(indices.get(index));
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
        return itemList.get(indices.get(position));
    }
}
