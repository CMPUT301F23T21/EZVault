package com.example.ezvault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ezvault.model.Item;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditItemDetails extends Fragment {
    public EditItemDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MutableLiveData<Item> item = new ViewModelProvider(getActivity()).get(ItemViewModel.class).get();
        View view = inflater.inflate(R.layout.fragment_edit_item_details, container, false);

        Button saveButton = view.findViewById(R.id.edit_details_save);
        EditText makeText = view.findViewById(R.id.edit_details_make);
        EditText modelText = view.findViewById(R.id.edit_details_model);
        EditText descText = view.findViewById(R.id.edit_details_description);
        EditText commentText = view.findViewById(R.id.edit_details_comment);
        EditText countText = view.findViewById(R.id.edit_details_count);
        EditText valueText = view.findViewById(R.id.edit_details_value);
        EditText serialText = view.findViewById(R.id.edit_details_serial_number);

        Item raw = item.getValue();
        Log.v("EZVault", "Editing item: " + raw.getId());

        //set original details
        makeText.setText(raw.getMake());
        modelText.setText(raw.getModel());
        descText.setText(raw.getDescription());
        commentText.setText(raw.getComment());
        countText.setText(String.valueOf(raw.getCount()));
        valueText.setText(String.valueOf(raw.getValue()));
        serialText.setText(raw.getSerialNumber());

        //update to new values on save
        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);

            String make = makeText.getText().toString();
            String model = modelText.getText().toString();
            String desc = descText.getText().toString();
            String comment = commentText.getText().toString();
            double count = Double.parseDouble(countText.getText().toString());
            double value = Double.parseDouble(valueText.getText().toString());
            String serial = serialText.getText().toString();

            raw.setMake(make);
            raw.setModel(model);
            raw.setDescription(desc);
            raw.setComment(comment);
            raw.setCount(count);
            raw.setValue(value);
            raw.setSerialNumber(serial);

            saveButton.setEnabled(true);
        });

        return view;
    }
}