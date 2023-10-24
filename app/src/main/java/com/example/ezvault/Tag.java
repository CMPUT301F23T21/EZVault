package com.example.ezvault;

/**
 * Represents a tag.
 */
public class Tag {
    /**
     * The identifying string of the tag.
     */
    private String identifier;

    /**
     * Construct a tag with an identifier (should already be preprocessed and validated).
     * @param identifier The string that is used for a tag.
     */
    public Tag(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the identifier.
     * @return The identifier of the tag.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the identifier.
     * @param identifier The identifying string of the tag.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}