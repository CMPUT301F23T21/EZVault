package com.example.ezvault.utils;

import com.example.ezvault.model.User;

import javax.inject.Singleton;

@Singleton
public class UserManager {
    private User user = null;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
