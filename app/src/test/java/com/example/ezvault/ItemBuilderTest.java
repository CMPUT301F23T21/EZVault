package com.example.ezvault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;

import org.junit.Test;

public class ItemBuilderTest {
    @Test
    public void getsResultCorrectly() {
        ItemBuilder itemBuilder = new ItemBuilder();
        String id = "asdfjasldkfj";
        String make = "Nissan";
        String model = "Altima";
        String desc = "vehicle";
        String comment = "some comment";
        itemBuilder.setId(id)
                .setMake(make)
                .setModel(model)
                .setDescription(desc)
                .setComment(comment);

        Item item = itemBuilder.build();

        assertEquals(item.getId(), id);
        assertEquals(item.getMake(), make);
        assertEquals(item.getModel(), model);
        assertEquals(item.getDescription(), desc);
        assertEquals(item.getComment(), comment);
        assertTrue(item.getTags().isEmpty());
        assertTrue(item.getImages().isEmpty());
    }
    @Test
    public void getsResultCorrectlyWithOverride() {
        ItemBuilder itemBuilder = new ItemBuilder();
        String badId = "asdklfjasfdkjl0";
        String id = "asdfjasldkfj";
        String badMake = "Nisn";
        String make = "Nissan";
        String badModel = "alt";
        String model = "Altima";
        String badDesc = "";
        String desc = "vehicle";
        String badComment = "";
        String comment = "some comment";
        itemBuilder.setId(badId)
                .setMake(badMake)
                .setDescription(badDesc)
                .setMake(make)
                .setModel(badModel)
                .setModel(model)
                .setDescription(desc)
                .setComment(comment)
                .setId(id);

        Item item = itemBuilder.build();

        assertEquals(item.getId(), id);
        assertEquals(item.getMake(), make);
        assertEquals(item.getModel(), model);
        assertEquals(item.getDescription(), desc);
        assertEquals(item.getComment(), comment);
        assertTrue(item.getTags().isEmpty());
        assertTrue(item.getImages().isEmpty());

        assertNotEquals(item.getId(), badId);
        assertNotEquals(item.getMake(), badMake);
        assertNotEquals(item.getModel(), badModel);
        assertNotEquals(item.getDescription(), badDesc);
        assertNotEquals(item.getComment(), badComment);
    }
}
