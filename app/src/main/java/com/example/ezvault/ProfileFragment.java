package com.example.ezvault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ezvault.database.FirebaseBundle;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays the user profile and handles changes in username, email or password
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logOutButton = view.findViewById(R.id.log_out_button);

        logOutButton.setOnClickListener(v -> {
            FirebaseBundle currFb = new FirebaseBundle();
            currFb.getAuth().signOut();

            Navigation.findNavController(view).navigate(R.id.profileFragment_to_welcomeFragment);
        });

        return view;
    }
}