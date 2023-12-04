package com.example.ezvault.textwatchers;

import android.text.Editable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

public class NonEmptyTextWatcher extends TextInputWatcher {
    private static final String EMPTY_ERROR_MESSAGE = "Field must not be empty";


    public NonEmptyTextWatcher(@NonNull TextView textView, TextInputLayout textInputLayout) {
        super(textView, textInputLayout, EMPTY_ERROR_MESSAGE);

        if (textView.getText().length() == 0) {
            setError(EMPTY_ERROR_MESSAGE);
        }
    }

    public NonEmptyTextWatcher(@NonNull TextView textView) {
        this(textView, null);
    }

    @Override
    void onAfterTextChanged(Editable s) {
        if (s.length() == 0){
            setError(errorMessage);
        } else if (isOwnError()){
            clearError();
        }
    }
}
