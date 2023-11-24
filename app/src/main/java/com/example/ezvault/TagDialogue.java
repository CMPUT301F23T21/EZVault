package com.example.ezvault;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import androidx.navigation.Navigation;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.TagDAO;
import com.example.ezvault.model.Tag;
import com.example.ezvault.TagDialogueDirections;


/**
 * A custom dialogue fragment
 */
public class TagDialogue extends DialogFragment {
    private Button add;
    private TextView tagName;
    private Button cancel;


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

        FirebaseBundle firebaseBundle = new FirebaseBundle();
        TagDAO tagDAO = new TagDAO(firebaseBundle);


        //old code
        add.setOnClickListener(v -> {
            String tagname = tagName.getText().toString().trim();



            if (tagName.length()==0){
                Toast.makeText(getContext(), "Please enter a tag name", Toast.LENGTH_SHORT).show();
            }
            else {
            Tag tag = new Tag(tagname);


            tagDAO.create(tag).addOnSuccessListener(task -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("tagData", tag);
                getParentFragmentManager().setFragmentResult("tagResult", bundle);
                dismiss();
            }).addOnFailureListener(e -> {
                Log.d("Mytag", "This ain't working");
                dismiss();

            });
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("tagData", tag);

            getParentFragmentManager().setFragmentResult("tagResult", bundle);
            dismiss();}

        });
*/}});
        cancel.setOnClickListener(v -> {
            dismiss();
        });


        return builder.create();
    }
}
