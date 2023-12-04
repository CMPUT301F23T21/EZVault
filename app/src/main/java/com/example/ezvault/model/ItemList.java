// Model class for a user's ItemList

package com.example.ezvault.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ezvault.model.utils.ItemListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Represents a list of items
 */
public class ItemList implements List<Item>, ItemListView {
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

    public List<String> getItemIds() {return
            items.stream()
            .map(item -> item.getId())
            .collect(Collectors.toList());}

    public List<String> getTagIds() {return
            tags.stream()
                    .map(tag -> tag.getUid())
                    .collect(Collectors.toList());}

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(@Nullable Object o) {
        return items.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return items.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Item item) {
        return items.add(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(@Nullable Object o) {
        return items.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return items.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(@NonNull Collection<? extends Item> c) {
        return items.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Item> c) {
        return items.addAll(index, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return items.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return items.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        items.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item get(int index) {
        return items.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item set(int index, Item element) {
        return items.set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, Item element) {
        items.add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item remove(int index) {
        return items.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(@Nullable Object o) {
        return items.indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(@Nullable Object o) {
        return items.lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ListIterator<Item> listIterator() {
        return items.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ListIterator<Item> listIterator(int index) {
        return items.listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<Item> subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }
}
