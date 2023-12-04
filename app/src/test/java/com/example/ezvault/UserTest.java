package com.example.ezvault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;

import org.junit.Test;

import java.util.ArrayList;

public class UserTest {
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
    private User mockUser() {
        return new User("test", "uid", mockList());
    }

    @Test
    public void checkUserName() {
        assertEquals(mockUser().getUserName(), "test");
    }

    @Test
    public void checkUid() {
        assertEquals(mockUser().getUid(), "uid");
    }

    @Test
    public void checkItemList() {
        assertNotNull(mockUser().getItemList());
    }
}
