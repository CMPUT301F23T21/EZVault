package com.example.ezvault.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordWatcher extends TextInputWatcher {

    private final int minPasswordLength;

    public PasswordWatcher(int minPasswordLength, @NonNull TextView textView, TextInputLayout textInputLayout){
        super(textView, textInputLayout, "Password must be at least " + minPasswordLength + " characters");
        this.minPasswordLength = minPasswordLength;
    }

    public PasswordWatcher(int minPasswordLength, @NonNull TextView textView){
        this(minPasswordLength, textView, null);
    }

    @Override
    void onAfterTextChanged(Editable s) {
        int passwordLength = s.length();

        if (passwordLength < minPasswordLength && passwordLength != 0){
            setError(errorMessage);
        } else if (isOwnError()){
            clearError();
        }
    }
}
