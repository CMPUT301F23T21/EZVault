package com.example.ezvault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.textwatchers.PasswordWatcher;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays the user profile and handles changes in username, email or password
 */
@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    @Inject
    protected UserManager userManager;

    private FirebaseBundle firebaseBundle;

    private EditText newPasswordText;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseBundle = new FirebaseBundle();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTextWatchers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String currentUsername = userManager.getUser()
                .getUserName();

        String currentEmail = firebaseBundle.getAuth()
                .getCurrentUser()
                .getEmail();

        EditText usernameText = view.findViewById(R.id.edittext_profile_username);
        usernameText.setText(currentUsername);

        EditText emailText = view.findViewById(R.id.edittext_profile_email);
        emailText.setText(currentEmail);

        newPasswordText = view.findViewById(R.id.edittext_profile_password);

        Button logOutButton = view.findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(v -> {
            firebaseBundle.getAuth().signOut();
            Navigation.findNavController(view).navigate(R.id.profileFragment_to_welcomeFragment);
        });

        Button updateButton = view.findViewById(R.id.update_info_button);
        updateButton.setOnClickListener(v -> {
            List<Task<Void>> profileTasks = new ArrayList<>();

            String updatedUsername = usernameText.getText().toString();
            if (!updatedUsername.equals(currentUsername)){
                ItemList itemsList = userManager.getUser().getItemList();

                RawUserDAO.RawUser newRawUser = new RawUserDAO.RawUser(updatedUsername,
                        (ArrayList<String>)itemsList.getTagIds(),
                        (ArrayList<String>)itemsList.getItemIds());

                Task<Void> updateUsernameTask = new RawUserDAO(firebaseBundle)
                        .update(userManager.getUser().getUid(), newRawUser);

                updateUsernameTask.addOnSuccessListener(t -> {
                    userManager.getUser()
                            .applyUserNameUpdate(updatedUsername);
                });

                profileTasks.add(updateUsernameTask);
            }

            String updatedEmail = emailText.getText().toString();
            if (!updatedEmail.equals(currentEmail)){
                profileTasks.add(firebaseBundle.getAuth().getCurrentUser().updateEmail(updatedEmail));
            }

            String updatedPassword = newPasswordText.getText().toString();
            if (!updatedPassword.isEmpty() && updatedPassword.length() >= 6){
                profileTasks.add(firebaseBundle.getAuth().getCurrentUser().updatePassword(updatedPassword));
            }

            if (profileTasks.isEmpty() ) return; // We didn't do anything

            Tasks.whenAllComplete(profileTasks)
                    .addOnSuccessListener(t -> {
                        Toast.makeText(requireContext(),
                                "Profile successfully updated",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        newPasswordText.setText("");
                     }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(),
                                "Error updating profile, please try again",
                                    Toast.LENGTH_SHORT)
                                    .show();
                     });

        });

        return view;
    }

    private void setupTextWatchers(){
        newPasswordText.addTextChangedListener(new PasswordWatcher(6, newPasswordText));
    }
}