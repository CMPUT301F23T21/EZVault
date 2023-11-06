package com.example.ezvault.model;

import java.util.ArrayList;

/**
 * Represents a list of items
 */
public class ItemList {
    /**
     * The underlying list of items
     */
    private ArrayList<Item> items;
    /**
     * The available tags for the list
     */
    private ArrayList<Tag> tags;

    /**
     * Constructs an empty list of items and tags.
     */
    public ItemList() {
        this.items = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    /**
     * Constructs a list with provided items and tags.
     *
     * @param items The initial list of items.
     * @param tags  The initial list of tags.
     */
    public ItemList(ArrayList<Item> items, ArrayList<Tag> tags) {
        this.items = items;
        this.tags = tags;
    }

    /**
     * Get the list of items
     * @return The raw list of items.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Get the list of available tags
     * @return The list of available tags.
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }
}
