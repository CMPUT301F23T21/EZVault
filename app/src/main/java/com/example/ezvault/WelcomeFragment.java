package com.example.ezvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A welcome page fragment that presents the user with 2 options
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

        // find the views
        Button existingUser = view.findViewById(R.id.existing_user_button);
        Button newUser = view.findViewById(R.id.new_user_button);

        // click listener for existingUser button
        existingUser.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.welcomeFragment_to_loginFragment));

        // click listener for newUser button
        newUser.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.welcomeFragment_to_newUserFragment)
        );

        return view;
    }
}
