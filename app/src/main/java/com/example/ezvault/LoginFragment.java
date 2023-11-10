package com.example.ezvault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ezvault.authentication.authentication.AuthenticationHandler;
import com.example.ezvault.authentication.authentication.EmailPasswordAuthenticationStrategy;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.utils.UserManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 */
@AndroidEntryPoint
public class LoginFragment extends Fragment {
    Button loginButton;
    ImageButton backButton;

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
        backButton = view.findViewById(R.id.login_back_button);
        loginButton = view.findViewById(R.id.login_button);
        EditText emailText = view.findViewById(R.id.username_text);
        EditText passwordText = view.findViewById(R.id.password_text);

        // setup back button
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // setup login button
        loginButton.setOnClickListener(v -> {
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
                Log.e("EZVault", "Failed login.", e);
            });
        });

        return view;
    }
}