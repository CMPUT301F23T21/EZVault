package com.example.ezvault.model;

/**
 * Represents a user in the application.
 * Does not perform I/O.
 * Username is local and may be out of sync with the database.
 */
public class User {
    private String userName;
    private final String uid;
    private ItemList itemList;

    /**
     * Constructs a new User object with the specified user name and user id.
     *
     * @param userName The username of the user.
     * @param uid      The user ID of the user.
     * @param itemList
     */
    public User(String userName, String uid, ItemList itemList) {
        this.userName = userName;
        this.uid = uid;
        this.itemList = itemList;
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

    public ItemList getItemList() {
        return itemList;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", uid='" + uid + '\'' +
                ", items=" + itemList.getItems() +
                ", tags=" + itemList.getTags() +
                '}';
    }
}
