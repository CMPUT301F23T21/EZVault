package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;

import java.util.Collection;

/**
 * Item filter based on inclusion of keywords in the description
 */
public class ItemKeywordFilter implements IItemFilter {
    private Collection<String> keywords;

    /**
     * Create a filter based on keyword
     * @param keywords The keywords to test for.
     */
    public ItemKeywordFilter(Collection<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(Item item) {
        if (keywords == null) {
            return true;
        }

        for (String keyword : keywords) {
            if (!item.getDescription().contains(keyword)) {
                return false;
            }
        }
        return true;
    }
}