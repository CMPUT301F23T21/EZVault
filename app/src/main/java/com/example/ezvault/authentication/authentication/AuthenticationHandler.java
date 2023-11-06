package com.example.ezvault.authentication.authentication;

import com.example.ezvault.model.User;
import com.google.android.gms.tasks.Task;

public class AuthenticationHandler {
    private final IAuthenticationStrategy strategy;

    public AuthenticationHandler(IAuthenticationStrategy strategy) {
        this.strategy = strategy;
    }

    public Task<User> authenticate() {
        return strategy.authenticate();
    }
}
