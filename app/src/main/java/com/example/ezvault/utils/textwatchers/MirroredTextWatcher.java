package com.example.ezvault.utils.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class MirroredTextWatcher implements TextWatcher {

    private final TextView hostText;
    private final TextView matchText;

    private final String errorMessage;


    public MirroredTextWatcher(@NonNull TextView hostText, @NonNull TextView matchText, @NonNull String errorMessage) {
        this.hostText = hostText;
        this.matchText = matchText;
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
        if (s.length() != 0 && !s.toString().equals(matchText.getText().toString())){
            hostText.setError(errorMessage);
        } else if (hostText.getError() != null && hostText.getError().toString().equals(errorMessage)){
            hostText.setError(null);
        }
    }
}
