package com.example.ezvault.database;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;


import com.example.ezvault.database.RawUserDAO.RawUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserService {
    private final RawUserDAO rawUserDAO;
    private final ItemDAO itemDAO;
    private final TagDAO tagDAO;
    private final FirebaseAuth auth;

    public UserService(FirebaseBundle firebase) {
        this.rawUserDAO = new RawUserDAO(firebase);
        this.tagDAO = new TagDAO(firebase);
        this.itemDAO = new ItemDAO(firebase);
        this.auth = firebase.getAuth();
    }

    public Task<User> readUser(String uid) {
        Task<RawUser> rawUserTask = rawUserDAO.read(uid);
        Task<ArrayList<Item>> itemsTask = rawUserTask.onSuccessTask(rawUser -> itemDAO.pluralRead(rawUser.getItemids()));
        Task<ArrayList<Tag>> tagsTask = rawUserTask.onSuccessTask(rawUser -> tagDAO.pluralRead(rawUser.getTagids()));
        return Tasks.whenAllSuccess(rawUserTask, itemsTask, tagsTask)
                .onSuccessTask(tasks -> {
                    RawUser rawUser = (RawUser) tasks.get(0);
                    ArrayList<Item> items = (ArrayList<Item>) tasks.get(1);
                    ArrayList<Tag> tags = (ArrayList<Tag>) tasks.get(2);
                    return Tasks.forResult(new User(rawUser.getName(), uid));
                });
    }

    public Task<String> registerUser(String email, String password, String userName) {
        return auth.createUserWithEmailAndPassword(email, password)
                .onSuccessTask(authResult -> {
                    String uid = authResult.getUser().getUid();
                    RawUser rawUser = new RawUser(userName, new ArrayList<>(), new ArrayList<>());
                    return rawUserDAO.update(uid, rawUser)
                            .onSuccessTask(t -> Tasks.forResult(uid));
                });
    }
}
