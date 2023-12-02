package com.example.ezvault;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.UserManager;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FragmentTaggerItemViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private final FirebaseBundle firebase = new FirebaseBundle();
    private final UserManager userManager;

    @Inject
    public FragmentTaggerItemViewModel(UserManager userManager) {
        this.userManager = userManager;
        tags.setValue(userManager.getUser().getItemList().getTags());
    }

}