package com.example.ezvault.utils;

import com.example.ezvault.model.Item;

/**
 * Represents the functionality needed to view an item list.
 */
public interface ItemListView extends Iterable<Item> {
    /**
     * Get the number of items that should be viewed.
     * @return The number of Items that are displayable.
     */
    int size();

    /**
     * Check if there are no items that should be displayed.
     * @return Whether or not there are items to be displayed.
     */
    boolean isEmpty();

    /**
     * Get an item.
     * @param position The position to get the item.
     * @return The item at that position.
     */
    Item get(int position);

    default double getTotalValue() {
        double total = 0.0;
        for (Item item : this) {
            total += item.getValue() * item.getCount();
        }
        return total;
    }
}
