// Model class for tags

package com.example.ezvault.model;

/**
 * Represents a tag.
 */
public class Tag {
    /**
     * The contents of the tag.
     */
    private String contents;

    /**
     * The unique id of the tag
     */
    private String uid;

    /**
     * Construct a tag with an identifier (should already be preprocessed and validated).
     * @param contents The string that is used for a tag.
     * @param uid The unique identifier of the tag
     */
    public Tag(String contents, String uid) {
        this.contents = contents;
        this.uid = uid;
    }

    /**
     * Get the contents.
     * @return The contents of the tag.
     */
    public String getContents() {
        return contents;
    }

    /**
     * Set the contents.
     * @param contents The contents of the tag.
     */
    public void setContents(String contents) {
        this.contents = contents;
    }
}
