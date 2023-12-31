/*
Provides utilities for interacting with Users on the database.
 */

package com.example.ezvault.data.database;

import com.example.ezvault.data.database.RawUserDAO.RawUser;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.TaskUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Provides operations involving users and the database.
 */
public class UserService {
    private final RawUserDAO rawUserDAO;
    private final ItemDAO itemDAO;
    private final TagDAO tagDAO;

    /**
     * Initializes services for user-related operations.
     * @param firebase FirebaseBundle containing Firebase services
     */
    public UserService(FirebaseBundle firebase) {
        this.rawUserDAO = new RawUserDAO(firebase);
        this.tagDAO = new TagDAO(firebase);
        this.itemDAO = new ItemDAO(firebase);
    }

    /**
     * Retrieves complete user information including their items and tags.
     * @param uid User ID of the user to read.
     * @return Task with the user's data.
     */
    public Task<User> readUser(String uid) {
        Task<RawUser> rawUserTask = rawUserDAO.read(uid);
        Task<ArrayList<Item>> itemsTask = rawUserTask.onSuccessTask(rawUser -> itemDAO.pluralRead(rawUser.getItemids()));
        Task<ArrayList<Tag>> tagsTask = rawUserTask.onSuccessTask(rawUser -> tagDAO.pluralRead(rawUser.getTagids()));
        return Tasks.whenAllSuccess(rawUserTask, itemsTask, tagsTask)
                .onSuccessTask(tasks -> {
                    RawUser rawUser = (RawUser) tasks.get(0);
                    ArrayList<Item> items = (ArrayList<Item>) tasks.get(1);
                    if (items == null) {
                        items = new ArrayList<>();
                    }
                    ArrayList<Tag> tags = (ArrayList<Tag>) tasks.get(2);
                    if (tags == null) {
                        tags = new ArrayList<>();
                    }
                    ItemList itemList = new ItemList(items, tags);
                    return Tasks.forResult(new User(rawUser.getName(), uid, itemList));
                });
    }

    public Task<Boolean> userExists(String userName) {
        return TaskUtils.onSuccess(rawUserDAO.find(userName), QuerySnapshot::isEmpty);
    }

    public Task<User> createUser(String uid, String userName) {
        User user = new User(userName, uid, new ItemList());
        RawUser rawUser = new RawUser(userName, new ArrayList<>(), new ArrayList<>());
        return rawUserDAO.update(uid, rawUser).onSuccessTask(v -> Tasks.forResult(user));
    }

    public Task<String> addTag(User user, Tag tag) {
        TagDAO tagDAO = new TagDAO(new FirebaseBundle());
        Task<String> tagIdTask = tagDAO.create(tag);
        return tagIdTask.onSuccessTask(tagId -> {
            ArrayList<Tag> tags = user.getItemList().getTags();
            tags.add(tag);
            RawUserDAO userDAO = new RawUserDAO(new FirebaseBundle());
            Task<RawUser> rawUserTask = userDAO.read(user.getUid());
            return rawUserTask.onSuccessTask(rawUser -> {
                rawUser.getTagids().add(tagId);
                return TaskUtils.onSuccess(rawUserDAO.update(user.getUid(), rawUser),
                        v -> tagId);
            });
        });
    }
}
