package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.utils.filter.IItemFilter;

/**
 * An item filter which ensures items conform to a specific make
 */
public class ItemMakeFilter implements IItemFilter {
    private String make;

    /**
     * Construct an item filter based on the make
     * @param make The make to check
     */
    public ItemMakeFilter(String make) {
        this.make = make;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(Item item) {
        return make.equals(item.getMake());
    }
}