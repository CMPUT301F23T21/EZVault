package com.example.ezvault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
@AndroidEntryPoint
public class NewUserFragment extends Fragment {
    Button createUser;
    ImageButton backButton;

    @Inject
    UserManager userManager;

    public NewUserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);

        AppCompatImageButton backButton = view.findViewById(R.id.create_user_back_button);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        EditText emailText = view.findViewById(R.id.create_email_text);
        EditText userNameText = view.findViewById(R.id.create_username_text);
        EditText passwordText = view.findViewById(R.id.create_password_text);
        EditText confirmPasswordText = view.findViewById(R.id.confirm_password_text);

        Button createButton = view.findViewById(R.id.create_user_button);

        createButton.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String userName = userNameText.getText().toString();
            String password = passwordText.getText().toString();
            String confirmPassword = confirmPasswordText.getText().toString();

            if (password.equals(confirmPassword)) {
                EmailPasswordRegistrationStrategy rs = new EmailPasswordRegistrationStrategy(new FirebaseBundle(), email, password);
                RegistrationHandler rh = new RegistrationHandler(rs);

                rh.register(userName).addOnSuccessListener(user -> {
                    Log.v("EZVault", "Successfully Registered: " + user.getUid());
                    userManager.setUser(user);
                    getActivity().finish();
                }).addOnFailureListener(e -> {
                    Log.e("EZVault", "Failed Registration.", e);
                });
            }
        });


        return view;
    }
}