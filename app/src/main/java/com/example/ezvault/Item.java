package com.example.ezvault;

import java.time.Instant;

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
    private Instant acquisitionDate;
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
     * Create an item without a serial number.
     * @param id The id of the item
     * @param make the make of the item
     * @param model The model of the item
     * @param acquisitionDate The time of acquisition
     * @param description The description of the item
     * @param comment The comment on the item
     */
    public Item(String id, String make, String model, Instant acquisitionDate, String description, String comment) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.acquisitionDate = acquisitionDate;
        this.description = description;
        this.comment = comment;
        this.serialNumber = null;
    }

    /**
     * Create an item with a serial number.
     * @param id The id of the item
     * @param make the make of the item
     * @param model The model of the item
     * @param acquisitionDate The time of acquisition
     * @param description The description of the item
     * @param comment The comment on the item
     * @param serialNumber The serial number of the item
     */
    public Item(String id, String make, String model, Instant acquisitionDate, String description, String comment, String serialNumber) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.acquisitionDate = acquisitionDate;
        this.description = description;
        this.comment = comment;
        this.serialNumber = serialNumber;
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
    public Instant getAcquisitionDate() {
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
     * Returns the serial number of the item.
     * @return The serial number of the item
     * or null if it does not have a serial code.
     */
    @Nullable
    public String getSerialNumber() {
        return serialNumber;
    }

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
    public void setAcquisitionDate(Instant acquisitionDate) {
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
}
