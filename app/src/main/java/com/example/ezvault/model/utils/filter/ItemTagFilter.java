package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.IItemFilter;

import java.util.List;

/**
 * Item filter based on the inclusion of tags
 */
public class ItemTagFilter implements IItemFilter {

    private final List<Tag> tags;

    /**
     * Create a new item tag filter
     * @param tags The tags to test for
     */
    public ItemTagFilter(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
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
