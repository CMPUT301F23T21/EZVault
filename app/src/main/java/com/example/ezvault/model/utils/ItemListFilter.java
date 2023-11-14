package com.example.ezvault.model.utils;

import androidx.annotation.NonNull;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class ItemListFilter implements ItemListView {
    protected final ItemList itemList;
    protected final ArrayList<Integer> indices;
    private boolean valid = false;

    /**
     * Create a new filter on an item-list
     * @param itemList The list of items to filter on.
     */
    public ItemListFilter(ItemList itemList) {
        this.indices = new ArrayList<>();
        this.itemList = itemList;
    }

    protected abstract boolean validate();

    private void updateIndices() {
        if (!valid) {
            valid = validate();
        }
    }

    public final void invalidate() {
        valid = false;
    }

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

    @Override
    public final int size() {
        updateIndices();
        return indices.size();
    }

    @Override
    public final boolean isEmpty() {
        updateIndices();
        return indices.isEmpty();
    }

    @Override
    public final Item get(int position) {
        updateIndices();
        return itemList.get(indices.get(position));
    }
}
