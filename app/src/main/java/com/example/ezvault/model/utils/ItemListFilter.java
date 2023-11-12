package com.example.ezvault.model.utils;

import androidx.annotation.NonNull;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ItemListFilter implements Iterable<Item> {
    private final ItemList itemList;
    private final ArrayList<Integer> indices = new ArrayList<>();
    private Predicate<Item> predicate;
    private boolean valid = false;

    /**
     * Create a new item-wise filter on an item-list
     * @param itemList The list of items to sort.
     * @param predicate The predicate to ensure holds true.
     */
    public ItemListFilter(ItemList itemList, Predicate<Item> predicate) {
        this.predicate = predicate;
        this.itemList = itemList;
        updateIndices();
    }

    private void updateIndices() {
        if (valid) {
            return;
        }
        indices.clear();
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (predicate.test(item)) {
                indices.add(i);
            }
        }
        valid = true;
    }

    public void invalidate() {
        valid = false;
    }

    @NonNull
    @Override
    public Iterator<Item> iterator() {
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
}
