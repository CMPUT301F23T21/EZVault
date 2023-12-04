package com.example.ezvault.utils;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Objects;

public class FragmentUtils {

    public static TextInputLayout getTextParentLayout(EditText textField){
        return (TextInputLayout)((ViewGroup)textField.getParent()).getParent();
    }

    public static boolean textHasNoErrors(TextView... args){
        return Arrays.stream(args)
                .map(TextView::getError)
                .allMatch(Objects::isNull);
    }

    public static boolean textLayoutHasNoErrors(TextInputLayout... args){
        return Arrays.stream(args)
                .map(TextInputLayout::getError)
                .allMatch(Objects::isNull);
    }
}
