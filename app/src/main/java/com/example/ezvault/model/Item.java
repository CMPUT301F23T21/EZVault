// Represents an item

package com.example.ezvault.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * Represents an item
 */
public class Item {
    /**
     * The ID of the Item
     */
    private final String id;
    /**
     * The make of the item
     */
    private String make;
    /**
     * The model of the Item
     */
    private String model;
    /**
     * The time of acquisition, does not include timezone info
     */
    private Timestamp acquisitionDate;
    /**
     * A description of the item
     */
    private String description;
    /**
     * A comment on the item
     */
    private String comment;
    /**
     * The serial number of the item.
     * Null if there is not one.
     */
    private @Nullable String serialNumber;

    /**
     * List of tags associated with the item.
     */
    private ArrayList<Tag> tags;

    /**
     * List of images associated with the item
     */
    private ArrayList<Image> images;

    /**
     * The value of the item in CAD.
     */
    private double value;

    /**
     * The amount or quantity of the item.
     */
    private double count;

    /**
     * Create an item with a serial number.
     * @param id The id of the item
     * @param make the make of the item
     * @param model The model of the item
     * @param acquisitionDate The time of acquisition
     * @param description The description of the item
     * @param comment The comment on the item
     * @param serialNumber The serial number of the item
     * @param tags The tags associated with the item
     * @param images The images associated with the item
     */
    public Item(String id, String make, String model, Timestamp acquisitionDate, String description, String comment, String serialNumber, ArrayList<Tag> tags, ArrayList<Image> images, double count, double value) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.acquisitionDate = acquisitionDate;
        this.description = description;
        this.comment = comment;
        this.serialNumber = serialNumber;
        this.tags = tags;
        this.images = images;
        this.count = count;
        this.value = value;
    }

    /**
     * Returns the make of the item.
     * @return The make associated with the item.
     */
    public String getMake() {
        return make;
    }

    /**
     * Returns the model of the item.
     * @return The specific model of the item.
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the ID of the item.
     * @return The unique identifier for the item.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the acquisition date of the item.
     * @return The specific instant of acquisition.
     */
    public Timestamp getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Returns the description of the item.
     * @return The description with the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the comment on the item.
     * @return The comment of the item.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Get the tags of the item
     * @return Get a list of the tags associated with the item
     */
    public ArrayList<Tag> getTags() { return tags; }

    /**
     * Get the value of the item
     * @return The value of the item
     */
    public double getValue() {
        return value;
    }

    /**
     * Get the count of the item
     * @return The count/amount of the item.
     */
    public double getCount() {
        return count;
    }

    /**
     * Returns the serial number of the item.
     * @return The serial number of the item
     * or null if it does not have a serial code.
     */
    @Nullable
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Get associated images
     * @return List of images associated with the item
     */
    public ArrayList<Image> getImages() { return images; }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Set the acquisition date.
     * @param acquisitionDate The date of acquisition of the item.
     */
    public void setAcquisitionDate(Timestamp acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    /**
     * Set the description of the item.
     * @param description The new description of the item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the comment on the item.
     * @param comment The new comment.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Set the serial number.
     * @param serialNumber The serial number or null if there is not one.
     */
    public void setSerialNumber(@Nullable String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Set the tags.
     * @param tags The list of tags to be associated with the item.
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Set the images
     * @param images List of images to be associated with the item.
     */
    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}
