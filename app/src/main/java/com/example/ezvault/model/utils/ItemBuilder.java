// A Builder Utility Class for Building Items

package com.example.ezvault.model.utils;

import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * Builder class for Item
 */
public class ItemBuilder {
    private String id;
    private String make;
    private String model;
    private Timestamp acquisitionDate;
    private String description = "";
    private String comment = "";
    private String serialNumber = null;
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    private double value;
    private double count;

    /**
     * Set the id
     * @param id The id for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Set the make
     * @param make The make for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setMake(String make) {
        this.make = make;
        return this;
    }

    /**
     * Set the model
     * @param model The model for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setModel(String model) {
        this.model = model;
        return this;
    }


    /**
     * Set the date
     * @param acquisitionDate The date of acquisition for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setAcquisitionDate(Timestamp acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
        return this;
    }

    /**
     * Set the description
     * @param description The description for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the comment
     * @param comment The comment on the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the serial number
     * @param serialNumber The serial number for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    /**
     * Set the tags
     * @param tags The tags for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setTags(ArrayList<Tag> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Set the make
     * @param images The images for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setImages(ArrayList<Image> images) {
        this.images = images;
        return this;
    }

    /**
     * Set the value
     * @param value The estimated value for for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setValue(double value) {
        this.value = value;
        return this;
    }

    /**
     * Set the count
     * @param count The count for the future item
     * @return The ItemBuilder after this change
     */
    public ItemBuilder setCount(double count) {
        this.count = count;
        return this;
    }

    /**
     * Built the item
     * @return The item built
     */
    public Item build() {
        return new Item(id, make, model, acquisitionDate, description, comment, serialNumber, tags, images, count, value);
    }
}
