package com.example.ezvault.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.TagDAO;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TagsViewModel extends ViewModel {
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private final FirebaseBundle firebase = new FirebaseBundle();

    @Inject
    public TagsViewModel(UserManager userManager) {
        tags.setValue(userManager.getUser().getItemList().getTags());
    }

    public LiveData<List<Tag>> getTags() {
        return tags;
    }

    public void addTag(String name) {
        TagDAO tagDAO = new TagDAO(firebase);
        Tag tag = new Tag(name);
        Task<String> task = tagDAO.create(tag);
        task.addOnSuccessListener(id -> {
            List<Tag> current = tags.getValue();
            assert current != null;
            current.add(tag);
            tags.setValue(current);
        });
    }
}
