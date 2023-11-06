package com.example.ezvault.model;

import javax.annotation.Nullable;

/**
 * Represents an image or photograph
 */
public class Image {
    /**
     * The id of the image, null if not in database
     */
    private @Nullable String id;

    /**
     * Get the Image's id
     * @return The id of the image in the database,
     * null if it is not referring to an image
     * in the database.
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Set the Image's id
     * @param id The id of the item in the database or null
     */
    public void setId(@Nullable String id) {
        this.id = id;
    }

    /**
     * Get the base64 representation of the image.
     * @return
     */
    public String getBase64() {
        throw new UnsupportedOperationException("unimplemented");
    }
}
