package com.example.ezvault.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

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

    public ItemBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ItemBuilder setMake(String make) {
        this.make = make;
        return this;
    }

    public ItemBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    public ItemBuilder setAcquisitionDate(Timestamp acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
        return this;
    }

    public ItemBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public ItemBuilder setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public ItemBuilder setTags(ArrayList<Tag> tags) {
        this.tags = tags;
        return this;
    }
    public ItemBuilder setImages(ArrayList<Image> images) {
        this.images = images;
        return this;
    }

    public ItemBuilder setValue(double value) {
        this.value = value;
        return this;
    }

    public ItemBuilder setCount(double count) {
        this.count = count;
        return this;
    }

    public Item build() {
        return new Item(id, make, model, acquisitionDate, description, comment, serialNumber, tags, images, count, value);
    }
}
