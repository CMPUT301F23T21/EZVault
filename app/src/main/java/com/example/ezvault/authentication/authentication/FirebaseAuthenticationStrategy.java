package com.example.ezvault.authentication.authentication;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.model.User;
import com.example.ezvault.database.UserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public abstract class FirebaseAuthenticationStrategy implements IAuthenticationStrategy {
    private final FirebaseBundle firebase;

    public FirebaseAuthenticationStrategy(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    public abstract Task<AuthResult> authenticate(FirebaseBundle firebase);

    @Override
    public Task<User> authenticate() {
        UserService userService = new UserService(firebase);
        return authenticate(firebase)
                .onSuccessTask(authResult -> userService.readUser(authResult.getUser().getUid()));
    }
}
