package com.example.ezvault.model.utils;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;

import java.util.Collection;

public class ItemTagFilter implements IItemFilter {
    Collection<Tag> tags;

    public ItemTagFilter(Collection<Tag> tags) {
        this.tags = tags;
    }

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
