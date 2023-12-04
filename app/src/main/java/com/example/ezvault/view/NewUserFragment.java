package com.example.ezvault.view;

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
import com.example.ezvault.data.authentication.registration.EmailPasswordRegistrationStrategy;
import com.example.ezvault.data.authentication.registration.RegistrationException;
import com.example.ezvault.data.authentication.registration.RegistrationHandler;
import com.example.ezvault.data.database.FirebaseBundle;
import com.example.ezvault.utils.textwatchers.MirroredTextWatcher;
import com.example.ezvault.utils.textwatchers.NonEmptyTextWatcher;
import com.example.ezvault.utils.textwatchers.PasswordWatcher;
import com.example.ezvault.utils.FragmentUtils;
import com.example.ezvault.utils.UserManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Handles the creation of a new user
 */
@AndroidEntryPoint
public class NewUserFragment extends Fragment {

    private final int MIN_PASSWORD_LENGTH = 6;
    Button createUser;
    ImageButton backButton;

    @Inject
    UserManager userManager;

    private EditText passwordText;
    private EditText confirmPasswordText;

    private EditText emailText;
    private EditText userNameText;

    public NewUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);

        backButton = view.findViewById(R.id.create_user_back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        emailText = view.findViewById(R.id.create_email_text);
        userNameText = view.findViewById(R.id.create_username_text);
        passwordText = view.findViewById(R.id.create_password_text);
        confirmPasswordText = view.findViewById(R.id.confirm_password_text);

        createUser = view.findViewById(R.id.create_user_button);

        createUser.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String userName = userNameText.getText().toString();
            String password = passwordText.getText().toString();
            String confirmPassword = confirmPasswordText.getText().toString();

            if (canRegister() && password.equals(confirmPassword) && password.length() >= MIN_PASSWORD_LENGTH) {
                EmailPasswordRegistrationStrategy rs = new EmailPasswordRegistrationStrategy(new FirebaseBundle(), email, password);
                RegistrationHandler rh = new RegistrationHandler(rs);

                rh.register(userName).addOnSuccessListener(user -> {
                    Log.v("EZVault", "Successfully Registered: " + user.getUid());
                    userManager.setUser(user);
                    Navigation.findNavController(view).navigate(R.id.newUserFragment_to_itemsFragment);
                }).addOnFailureListener(e -> {
                    if (e instanceof RegistrationException.UserAlreadyExists){
                        userNameText.setError("Username already exists");
                    }

                    String toastText = "Could not register: " + e.getMessage();
                    Toast.makeText(requireContext(),
                            toastText,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });

        return view;
    }

    private void setupTextWatchers(){
        emailText.addTextChangedListener(new NonEmptyTextWatcher(emailText));
        userNameText.addTextChangedListener(new NonEmptyTextWatcher(userNameText));

        passwordText.addTextChangedListener(new NonEmptyTextWatcher(passwordText));
        passwordText.addTextChangedListener(new PasswordWatcher(MIN_PASSWORD_LENGTH, passwordText));

        confirmPasswordText.addTextChangedListener(new NonEmptyTextWatcher(confirmPasswordText));
        confirmPasswordText.addTextChangedListener(new MirroredTextWatcher(confirmPasswordText,
                passwordText,
                "Passwords do not match"));
    }

    private boolean canRegister(){
        return FragmentUtils.textHasNoErrors(emailText,
                userNameText,
                passwordText,
                confirmPasswordText);
    }
}