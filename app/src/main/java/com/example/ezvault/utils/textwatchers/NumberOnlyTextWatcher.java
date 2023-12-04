package com.example.ezvault.utils.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class NumberOnlyTextWatcher implements TextWatcher {

    private final TextView textView;
    private final String NON_NUMBER_ERROR_MESSAGE = "Field must be a number";

    public NumberOnlyTextWatcher(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean isNumber = true;

        try {
            Double.parseDouble(s.toString());
        } catch (Exception e) {
            isNumber = false;
        }

        if (s.length() != 0 && !isNumber){
            textView.setError(NON_NUMBER_ERROR_MESSAGE);
        } else if (isNumber && textView.getError() != null && textView.getError().toString().equals(NON_NUMBER_ERROR_MESSAGE)){
            textView.setError(null);
        }
    }
}
