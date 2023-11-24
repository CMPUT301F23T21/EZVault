package com.example.ezvault.utils;

import android.net.Uri;

import com.example.ezvault.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManager {
    private User user = null;
    private List<Uri> cachedUris = new ArrayList<>();

    @Inject
    public UserManager() {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public List<Uri> getUriCache() { return this.cachedUris; }
    public void addUri(Uri uri) { cachedUris.add(uri); }
    public void clearUriCache() { cachedUris.clear(); }
}
