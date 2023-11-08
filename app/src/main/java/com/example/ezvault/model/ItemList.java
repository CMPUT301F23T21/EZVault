package com.example.ezvault.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a list of items
 */
public class ItemList implements List<Item> {
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
        this(new ArrayList<>(), new ArrayList<>());
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

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return items.contains(o);
    }

    @NonNull
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return items.toArray(a);
    }

    @Override
    public boolean add(Item item) {
        return items.add(item);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return items.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Item> c) {
        return items.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Item> c) {
        return items.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return items.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return items.retainAll(c);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public Item get(int index) {
        return items.get(index);
    }

    @Override
    public Item set(int index, Item element) {
        return items.set(index, element);
    }

    @Override
    public void add(int index, Item element) {
        items.add(index, element);
    }

    @Override
    public Item remove(int index) {
        return items.remove(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return items.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return items.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<Item> listIterator() {
        return items.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Item> listIterator(int index) {
        return items.listIterator(index);
    }

    @NonNull
    @Override
    public List<Item> subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }
}
