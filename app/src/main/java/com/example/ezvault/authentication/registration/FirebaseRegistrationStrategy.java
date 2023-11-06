package com.example.ezvault.authentication.registration;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.model.User;
import com.example.ezvault.database.UserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public abstract class FirebaseRegistrationStrategy implements IRegistrationStrategy {
    private final FirebaseBundle firebase;

    public FirebaseRegistrationStrategy(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    public abstract Task<AuthResult> register(FirebaseBundle firebase, String userName);

    @Override
    public Task<User> register(String userName) {
        UserService userService = new UserService(this.firebase);
        return this.register(firebase, userName)
                .onSuccessTask(authResult -> userService.readUser(authResult.getUser().getUid()));
    }
}
