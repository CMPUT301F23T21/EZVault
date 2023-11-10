package com.example.ezvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */

public class WelcomeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button newUserButton = view.findViewById(R.id.new_user_button);
        Button existingUserButton = view.findViewById(R.id.existing_user_button);

        newUserButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .addToBackStack("register")
                    .replace(R.id.activity_auth_container, new NewUserFragment())
                    .commit();
        });

        existingUserButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .addToBackStack("login")
                    .replace(R.id.activity_auth_container, new LoginFragment())
                    .commit();
        });

        return view;
    }
}
