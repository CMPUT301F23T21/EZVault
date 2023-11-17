package com.example.ezvault;

public class MockClass {
    public static final int TYPE_PLACEHOLDER = 0;
    public static final int TYPE_PICTURE = 1;

    private int type;
    private int image;

    public MockClass(int type, int image) {
        this.type = type;
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public int getImage() {
        return image;
    }
}
