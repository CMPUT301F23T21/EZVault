package com.example.ezvault.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ezvault.R;
import com.example.ezvault.authentication.authentication.AuthenticationHandler;
import com.example.ezvault.authentication.authentication.EmailPasswordAuthenticationStrategy;
import com.example.ezvault.data.database.FirebaseBundle;
import com.example.ezvault.utils.textwatchers.NonEmptyTextWatcher;
import com.example.ezvault.utils.FragmentUtils;
import com.example.ezvault.utils.UserManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Handles user login
 */
@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;

    @Inject
    UserManager userManager;

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // find the views
        ImageButton backButton = view.findViewById(R.id.login_back_button);
        loginButton = view.findViewById(R.id.login_button);
        emailText = view.findViewById(R.id.username_text);
        passwordText = view.findViewById(R.id.password_text);

        // setup back button
        backButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        // setup login button
        loginButton.setOnClickListener(v -> {
            if (!FragmentUtils.textHasNoErrors(emailText, passwordText)){
                Toast.makeText(requireContext(),
                                "Please enter all credentials",
                                Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            loginButton.setEnabled(false);

            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            EmailPasswordAuthenticationStrategy as = new EmailPasswordAuthenticationStrategy(new FirebaseBundle(), email, password);
            AuthenticationHandler ah = new AuthenticationHandler(as);

            ah.authenticate().addOnSuccessListener(user -> {
                Log.v("EZVault", "Successful login: " + user.getUid());
                userManager.setUser(user);
                Navigation.findNavController(view).navigate(R.id.loginFragment_to_itemsFragment);
            }).addOnFailureListener(e -> {
                loginButton.setEnabled(true);

                Toast.makeText(requireContext(),
                        "Invalid credentials, please try again",
                        Toast.LENGTH_SHORT)
                        .show();

                Log.e("EZVault", "Failed login.", e);
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWatchers();
    }

    private void setupWatchers(){
        emailText.addTextChangedListener(new NonEmptyTextWatcher(emailText));
        passwordText.addTextChangedListener(new NonEmptyTextWatcher(passwordText));
    }
}