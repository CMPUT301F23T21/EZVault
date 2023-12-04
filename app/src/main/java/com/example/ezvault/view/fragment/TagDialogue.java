package com.example.ezvault.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ezvault.R;
import com.example.ezvault.viewmodel.TagsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A custom dialogue fragment
 */
@AndroidEntryPoint
public class TagDialogue extends DialogFragment {

    private TagsViewModel viewModel;
    TextView tagName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        viewModel = new ViewModelProvider(requireActivity()).get(TagsViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_tag_dialogue, null);

        Button cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        Button add = view.findViewById(R.id.tag_dialog_add_button);
        tagName = view.findViewById(R.id.tag_dialog_edit_text);

        builder.setView(view);

        add.setOnClickListener(v -> {
            viewModel.addTag(tagName.getText().toString());
            dismiss();
        });

        cancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}
