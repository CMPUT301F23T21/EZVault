package com.example.ezvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 */

public class WelcomeFragment extends Fragment {

    Button existingUser;
    Button newUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        existingUser = view.findViewById(R.id.existing_user_button);
        newUser = view.findViewById(R.id.new_user_button);

        // click listener for existingUser button
        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.welcomeFragment_to_loginFragment);
            }
        });

        // click listener for newUser button
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.welcomeFragment_to_newUserFragment);
            }
        });

        return view;
    }
}
