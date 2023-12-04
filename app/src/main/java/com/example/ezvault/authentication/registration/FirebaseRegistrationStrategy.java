package com.example.ezvault.authentication.registration;

import android.util.Log;

import com.example.ezvault.authentication.registration.RegistrationException.UserAlreadyExists;
import com.example.ezvault.data.database.FirebaseBundle;
import com.example.ezvault.model.User;
import com.example.ezvault.data.database.UserService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;

/**
 * Represents a registration strategy that involves accessing firebase
 */
public abstract class FirebaseRegistrationStrategy implements IRegistrationStrategy {
    private final FirebaseBundle firebase;

    /**
     * Construct a FirebaseRegistrationStrategy
     * @param firebase The firebase bundle
     */
    public FirebaseRegistrationStrategy(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    /**
     * Register the use in the firebase auth system
     * @param firebase The firebase bundle
     * @param userName The userName of the user
     * @return Task representing the user's registration in firebase auth
     */
    public abstract Task<AuthResult> register(FirebaseBundle firebase, String userName);

    /**
     * Register the user in the firebase auth, and then readUser details
     * @param userName The user name of the registering user
     * @return The asynchronous task containing user
     */
    @Override
    public Task<User> register(String userName) {
        UserService userService = new UserService(this.firebase);
        Task<Boolean> userExists = userService.userExists(userName);
        return userExists.onSuccessTask(exists -> {
            if (exists) {
                Log.d("EZVault", "User " + userName + " does not exist!");
                return this.register(firebase, userName);
            } else {
                Log.d("EZVault", "User " + userName + " does exist!");
                return Tasks.forException(new UserAlreadyExists(userName));
            }
        }).onSuccessTask(authResult -> userService.createUser(authResult.getUser().getUid(), userName));
    }
}
