package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;

import java.io.Serializable;

/**
 * An interface for classes that filter items.
 */
public interface IItemFilter extends Serializable {
    /**
     * Determine whether or not an object passes the filter.
     * @param item The item to be checked.
     * @return Whether or not the item should be kept.
     */
    boolean keep(Item item);
}
