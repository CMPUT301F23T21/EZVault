package com.example.ezvault;

import com.google.android.gms.tasks.Task;

/**
 * Handles Logins
 */
public class LoginHandler {
    private LoginStrategy strategy;

    public LoginHandler(LoginStrategy strategy) {
        this.strategy = strategy;
    }

    public Task<User> login() {
        return strategy.authenticateUser();
    }
}
