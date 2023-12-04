package com.example.ezvault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.Tag;

import org.junit.Test;

import java.util.ArrayList;

public class ItemListTest {
    public ItemList mockList() {
        ItemList itemList = new ItemList();
        Tag tag1 = new Tag("tag1", null);
        Tag tag2 = new Tag("tag2", null);
        Item item1 = new Item(null, "tag", "1", null, null, null, null, new ArrayList<>(), null, 1.0, 1.0);
        Item item2 = new Item(null, "tag", "2", null, null, null, null, new ArrayList<>(), null, 1.0, 1.0);
        itemList.add(item1);
        itemList.add(item2);
        itemList.getTags().add(tag1);
        itemList.getTags().add(tag2);

        return itemList;
    }

    @Test
    public void iteratesCorrectly() {
        ItemList itemList = mockList();
        for (Item item : itemList) {
            assertEquals("tag", item.getMake());
            assertTrue(item.getModel().equals("1") || item.getMake().equals("2"));
        }
    }

    @Test
    public void correctTags() {
        ItemList itemList = mockList();
        for (Tag tag : itemList.getTags()) {
            assertTrue(tag.getContents().equals("tag1") || tag.getContents().equals("tag2"));
        }
    }
}
