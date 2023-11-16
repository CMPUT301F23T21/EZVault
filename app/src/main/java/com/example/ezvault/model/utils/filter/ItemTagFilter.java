package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.utils.filter.IItemFilter;

import java.util.Collection;

/**
 * Item filter based on the inclusion of tags
 */
public class ItemTagFilter implements IItemFilter {
    private Collection<Tag> tags;

    /**
     * Create a new item tag filter
     * @param tags The tags to test for
     */
    public ItemTagFilter(Collection<Tag> tags) {
        this.tags = tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(Item item) {
        for (Tag tag : tags) {
            if (!item.hasTag(tag)) {
                return false;
            }
        }
        return true;
    }
}
