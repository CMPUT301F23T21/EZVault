package com.example.ezvault;

import static org.junit.Assert.assertEquals;

import com.example.ezvault.model.Tag;

import org.junit.Test;

public class TagTest {
    private Tag mockTag() {
        return new Tag("my-tag", "tag-unique");
    }

    @Test
    public void testContents() {
        assertEquals(mockTag().getContents(), "my-tag");
    }

    @Test
    public void testUid() {
        assertEquals(mockTag().getUid(), "tag-unique");
    }
}
