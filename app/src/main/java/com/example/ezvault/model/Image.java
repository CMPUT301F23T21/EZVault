// Model class for Images

package com.example.ezvault.model;

import com.google.firebase.firestore.Exclude;

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
     * The byte data of the image
     */
    @Exclude
    private byte[] contents;

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
     * Gets the byte data of the Image
     * @return
     */
    @Exclude
    public byte[] getContents() {
        return contents;
    }

    /**
     * Sets the byte data of the Image
     * @param contents The raw byte data of the desired image
     * @return
     */
    @Exclude
    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
