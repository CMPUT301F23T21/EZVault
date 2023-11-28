package com.example.ezvault;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.model.Item;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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
        MutableLiveData<Item> item = new ViewModelProvider(requireActivity()).get(ItemViewModel.class).get();
        View view = inflater.inflate(R.layout.fragment_edit_item_details, container, false);

        Button saveButton = view.findViewById(R.id.edit_details_save);
        EditText makeText = view.findViewById(R.id.edit_details_make);
        EditText modelText = view.findViewById(R.id.edit_details_model);
        EditText descText = view.findViewById(R.id.edit_details_description);
        EditText commentText = view.findViewById(R.id.edit_details_comment);
        EditText countText = view.findViewById(R.id.edit_details_count);
        EditText valueText = view.findViewById(R.id.edit_details_value);
        EditText serialText = view.findViewById(R.id.edit_details_serial_number);
        EditText dateText = view.findViewById(R.id.edit_details_date);

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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = format.format(raw.getAcquisitionDate().toDate());

        dateText.setText(dateString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(raw.getAcquisitionDate().toDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        TextInputLayout dateLayout = view.findViewById(R.id.edit_details_date_layout);
        dateLayout.setEndIconOnClickListener(v -> {
            Log.d("EZVault", "Setting new date.");

            DatePickerDialog dialog = new DatePickerDialog(getContext());
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            dialog.updateDate(y, m, d);
            dialog.setOnDateSetListener((datePicker, year, month, day) -> {
                calendar.set(year, month, day);

                dateText.setText(format.format(calendar.getTime()));
            });
            dialog.show();
        });


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
            Timestamp date = new Timestamp(calendar.getTime());

            raw.setMake(make);
            raw.setModel(model);
            raw.setDescription(desc);
            raw.setComment(comment);
            raw.setCount(count);
            raw.setValue(value);
            raw.setSerialNumber(serial);
            raw.setAcquisitionDate(date);

            new ItemDAO(new FirebaseBundle())
                    .update(raw.getId(), raw)
                    .addOnSuccessListener(x -> {
                        saveButton.setEnabled(true);
                    });
        });

        return view;
    }
}