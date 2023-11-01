package com.example.ezvault;

import androidx.annotation.NonNull;

public class Item {
    private String id;
    private String make;
    private String model;
    private int unixDayEpoch;
    private String description;
    private String comment;
    private String serialNumber;

    public Item(String id, String make, String model, int unixDayEpoch, String description, String comment) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.unixDayEpoch = unixDayEpoch;
        this.description = description;
        this.comment = comment;
        this.serialNumber = null;
    }

    public Item(String id, String make, String model, int unixDayEpoch, String description, String comment, String serialNumber) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.unixDayEpoch = unixDayEpoch;
        this.description = description;
        this.comment = comment;
        this.serialNumber = serialNumber;
    }

    public Item(String make, String model){
        this.make = make;
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
