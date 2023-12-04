package com.example.ezvault.utils;

import com.example.ezvault.PhotoAdapter;
import com.example.ezvault.ViewpagerAdapter;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManager {
    private User user = null;
    private List<Image> localImages = new ArrayList<>();

    @Inject
    public UserManager() {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public List<Image> getLocalImages() {return this.localImages;}
    public void addLocalImage(Image image) { localImages.add(image); }

    public void clearLocalImages() { localImages.clear(); }

    public void synchronizeToAdapter(List<Image> unsyncedImages,
                                   PhotoAdapter photoAdapter){

        int unsyncedLen = unsyncedImages.size();
        int newPhotosLen = localImages.size();

        for(int i = 0; i < newPhotosLen; i++){
            unsyncedImages.add(localImages.get(i));
            photoAdapter.notifyItemInserted(unsyncedLen + i);
        }
        photoAdapter.notifyItemRangeInserted(unsyncedLen, newPhotosLen - 1);
        clearLocalImages();
    }
}
