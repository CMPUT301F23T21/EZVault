package com.example.ezvault.utils.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import javax.annotation.Nullable;

public abstract class TextInputWatcher implements TextWatcher {

    protected final TextView targetTextView;
    protected final TextInputLayout targetTextInputLayout;
    protected final String errorMessage;

    protected TextInputWatcher(TextView targetTextView, TextInputLayout targetTextInputLayout, String errorMessage){
        this.targetTextView = targetTextView;
        this.targetTextInputLayout = targetTextInputLayout;
        this.errorMessage = errorMessage;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        onAfterTextChanged(s);
    }

    abstract void onAfterTextChanged(Editable s);

    protected final boolean isOwnError() {
        CharSequence error = getError();
        return error != null && error.toString().equals(errorMessage);
    }

    @Nullable
    protected final CharSequence getError(){
        return (targetTextInputLayout == null)
                ? targetTextView.getError()
                : targetTextInputLayout.getError();
    }

    protected final void setError(CharSequence error){
        if (targetTextInputLayout == null){
            targetTextView.setError(error);
        } else {
            targetTextInputLayout.setError(error);
        }
    }

    protected final void clearError(){
        setError(null);
    }


}
