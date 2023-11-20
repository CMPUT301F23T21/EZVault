package com.example.ezvault;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

/**
 * A custom dialogue fragment
 */
public class TagDialogue extends DialogFragment {
    
    TextView tagName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_tag_dialogue, null);

        Button cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        Button add = view.findViewById(R.id.tag_dialog_add_button);
        tagName = view.findViewById(R.id.tag_dialog_edit_text);

        builder.setView(view);

        add.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.getPreviousBackStackEntry().getSavedStateHandle().set("tag", tagName.getText().toString());
            dismiss();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });


        return builder.create();
    }
}
