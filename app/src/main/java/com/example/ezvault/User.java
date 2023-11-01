package com.example.ezvault;

import java.util.UUID;

/**
 * Represents a user in the application.
 * Does not perform I/O.
 * Username is local and may be out of sync with the database.
 */
public class User {
    private final ItemService itemService;
    private String userName;
    private final String uid;

    /**
     * Constructs a new User object with the specified user name and user id.
     * @param userName The username of the user.
     * @param uid      The user ID of the user.
     */
    public User(ItemService itemService, String userName, String uid) {
        this.itemService = itemService;
        this.userName = userName;
        this.uid = uid;
    }

    /**
     * @return The local username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Applies an update to the local username.
     * @param updatedUserName the updated username.
     */
    public void applyUserNameUpdate(String updatedUserName) {
        this.userName = updatedUserName;
    }

    /**
     * Gets the user id of the user.
     * @return The user id of the user.
     */
    public String getUid() {
        return uid;
    }

    public ItemService getItemService() {
        return itemService;
    }
}
