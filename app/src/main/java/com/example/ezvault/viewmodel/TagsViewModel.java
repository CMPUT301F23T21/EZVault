package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.data.database.FirebaseBundle;
import com.example.ezvault.data.database.UserService;
import com.example.ezvault.model.Tag;
import com.example.ezvault.data.UserManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TagsViewModel extends ViewModel {
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private final UserManager userManager;

    @Inject
    public TagsViewModel(UserManager userManager) {
        this.userManager = userManager;
        tags.setValue(userManager.getUser().getItemList().getTags());
    }

    public LiveData<List<Tag>> getTags() {
        return tags;
    }

    public void addTag(String name) {
        UserService userService = new UserService(new FirebaseBundle());
        Tag tag = new Tag(name, null);
        userService.addTag(userManager.getUser(), tag);
        tags.getValue().add(tag);
        tags.setValue(new ArrayList<>(tags.getValue()));
    }
}
