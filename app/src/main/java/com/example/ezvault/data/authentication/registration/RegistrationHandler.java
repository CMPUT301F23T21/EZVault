package com.example.ezvault.data.authentication.registration;

import com.example.ezvault.model.User;
import com.google.android.gms.tasks.Task;

public class RegistrationHandler {
    private final IRegistrationStrategy strategy;

    public RegistrationHandler(IRegistrationStrategy strategy) {
        this.strategy = strategy;
    }

    public Task<User> register(String userName) {
        return strategy.register(userName);
    }
}
