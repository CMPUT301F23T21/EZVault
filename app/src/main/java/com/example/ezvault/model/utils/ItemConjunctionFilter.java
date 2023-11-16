package com.example.ezvault.model.utils;

import com.example.ezvault.model.Item;

import java.util.Collection;

/**
 * An item filter that ensures several filters all pass on each item.
 */
public class ItemConjunctionFilter implements IItemFilter {
    private Collection<IItemFilter> filters;

    /**
     * Construct a new conjunction filter
     * @param filters The filters that all must pass
     */
    public ItemConjunctionFilter(Collection<IItemFilter> filters) {
        this.filters = filters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(Item item) {
        return filters.stream().allMatch(filter -> filter.keep(item));
    }
}