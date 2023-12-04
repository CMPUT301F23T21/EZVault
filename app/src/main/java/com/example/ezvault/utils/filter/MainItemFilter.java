package com.example.ezvault.utils.filter;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class MainItemFilter implements IItemFilter {
    private final Instant start;
    private final Instant end;
    private final List<Tag> tags;
    private final List<String> keywords;
    private final String make;
    private boolean enabled;

    public MainItemFilter(Instant start, Instant end, List<Tag> tags,
                          List<String> keywords, String make, boolean enabled) {
        this.start = start;
        this.end = end;
        this.tags = tags;
        this.keywords = keywords;
        this.make = make;
        this.enabled = enabled;
    }

    public boolean keep(Item item) {
        if (!enabled) return true;

        boolean datePasses = start == null
            || !item.getAcquisitionDate().toDate().before(getStartDate());
        if (end != null && !item.getAcquisitionDate().toDate().after(getEndDate())) {
            datePasses = false;
        }

        boolean tagsPass = true;
        if (tags != null) {
            for (Tag tag : item.getTags()) {
                tagsPass = tagsPass && item.hasTag(tag);
            }
        }

        boolean keywordsPass = true;
        if (keywords != null) {
            for (String keyword : keywords) {
                if (!item.getDescription().contains(keyword)) {
                    keywordsPass = false;
                    break;
                }
            }
        }

        boolean makePasses = make == null || item.getMake().equalsIgnoreCase(make);

        return datePasses && tagsPass && keywordsPass && makePasses;
    }

    public Date getStartDate() {
        return start != null ? Date.from(start) : null;
    }

    public Date getEndDate() {
        return end != null ? Date.from(end) : null;
    }


    public List<Tag> getTags() {
        return tags;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getMake() {
        return make;
    }

    public void disable() {
        this.enabled = false;
    }
}
