package com.example.ezvault;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.ezvault.model.Tag;
import com.example.ezvault.TagDialogueDirections;

public class TagDialogue extends DialogFragment {
    private Button add;
    private TextView tagName;
    private Button cancel;

/*
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_tag_dialogue, null);
        builder.setView(view);

        Button cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        Button add = view.findViewById(R.id.tag_dialog_add_button);
        TextView tagName = view.findViewById(R.id.tag_dialog_edit_text);

        //old code
        add.setOnClickListener(v -> {
            String tagname = tagName.getText().toString();
            Tag tag = new Tag(tagname);
            TagDialogueDirections.ActionTagDialogueToTagsFragment action;
            action = TagDialogueDirections.actionTagDialogueToTagsFragment(tag);
            Navigation.findNavController(requireView()).navigate(action);
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        tagName = view.findViewById(R.id.tag_dialog_edit_text);
        add = view.findViewById(R.id.tag_dialog_add_button);

        add.setOnClickListener(v -> {
            String tagname = tagName.getText().toString();
            Tag tag = new Tag(tagname);
            TagDialogueDirections.ActionTagDialogueToTagsFragment action;
            action = TagDialogueDirections.actionTagDialogueToTagsFragment(tag);
            Navigation.findNavController(requireView()).navigate(action);
        });
        cancel.setOnClickListener(v -> {
            dismiss();
        });


    }
}

*/

    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_tag_dialogue, container, false);

        tagName = view.findViewById(R.id.tag_dialog_edit_text);
        add = view.findViewById(R.id.tag_dialog_add_button);

        add.setOnClickListener(v -> {
            String tagname = tagName.getText().toString();
            Tag tag = new Tag(tagname);
            TagDialogueDirections.ActionTagDialogueToTagsFragment action =
                    TagDialogueDirections.actionTagDialogueToTagsFragment(tag);
            Navigation.findNavController(requireParentFragment().requireView()).navigate(action);
            dismiss(); //
        });

        return view;
    }
    }
    */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_tag_dialogue, null);
        builder.setView(view);

        Button cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        Button add = view.findViewById(R.id.tag_dialog_add_button);
        TextView tagName = view.findViewById(R.id.tag_dialog_edit_text);

        //old code
        add.setOnClickListener(v -> {
            String tagname = tagName.getText().toString();
            Tag tag = new Tag(tagname);

            Bundle bundle = new Bundle();
            bundle.putSerializable("tagData", tag);

            getParentFragmentManager().setFragmentResult("tagResult", bundle);
            dismiss();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });

        return builder.create();
    }
}
/*
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_tag_dialogue, container, false);
        cancel = view.findViewById(R.id.tag_dialog_cancel_button);
        tagName = view.findViewById(R.id.tag_dialog_edit_text);
        add = view.findViewById(R.id.tag_dialog_add_button);

        add.setOnClickListener(v ->{
            String tagname = tagName.getText().toString();
            Tag tag = new Tag(tagname);

            Bundle bundle = new Bundle();
            bundle.putSerializable("tagData", tag);

            getParentFragmentManager().setFragmentResult("tagResult", bundle);
            dismiss();
        });

        cancel.setOnClickListener(v -> dismiss());


        return view;
    }
}*/