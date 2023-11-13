package com.example.ezvault.model.utils;

import com.example.ezvault.model.Item;

public interface ItemListView extends Iterable<Item> {
    int size();
    boolean isEmpty();
    Item get(int position);
}
