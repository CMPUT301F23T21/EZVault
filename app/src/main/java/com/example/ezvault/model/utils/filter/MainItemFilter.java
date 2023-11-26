package com.example.ezvault.model.utils.filter;

import com.example.ezvault.model.Item;

import java.util.Date;
import java.util.List;

public class MainItemFilter implements IItemFilter {
    private ItemKeywordFilter keywordFilter = null;
    private ItemDateFilter dateFilter = null;
    private ItemMakeFilter makeFilter = null;

    public boolean keep(Item item) {
        return (keywordFilter == null || keywordFilter.keep(item))
                && (dateFilter == null || dateFilter.keep(item))
                && (makeFilter == null || makeFilter.keep(item));
    }

    public void setDateFilter(ItemDateFilter dateFilter) {
        this.dateFilter = dateFilter;
    }

    public Date getStartDate() {
        return dateFilter != null ? Date.from(dateFilter.getStart()) : null;
    }

    public Date getEndDate() {
        return dateFilter != null ? Date.from(dateFilter.getEnd()) : null;
    }

    public void setKeywordFilter(ItemKeywordFilter keywordFilter) {
        this.keywordFilter = keywordFilter;
    }

    public List<String> getKeywords() {
        return keywordFilter != null ? keywordFilter.getKeywords() : null;
    }

    public void setMakeFilter(ItemMakeFilter makeFilter) {
        this.makeFilter = makeFilter;
    }

    public String getMake() {
        return makeFilter != null ? makeFilter.getMake() : null;
    }
}
