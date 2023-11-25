package com.example.ezvault.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ezvault.R;
import com.example.ezvault.model.utils.filter.IItemFilter;
import com.example.ezvault.viewmodel.FilterViewModel;

import java.util.function.Consumer;

/**
 * Fragment for the filtering UI.
 */
public class FilterFragment extends Fragment {
    private FilterViewModel viewModel;

    public FilterFragment() {
        // Required empty public constructor
    }

    private void createDatePicker(View view, FilterViewModel.DateConsumer dateConsumer) {
        view.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
            datePickerDialog.setOnDateSetListener((DatePicker, year, month, day) ->
                dateConsumer.accept(year, month, day)
            );
            datePickerDialog.show();
        });
    }

    private void setupApplyButton(View view) {
        Button applyButton = view.findViewById(R.id.button_filter_apply);
        applyButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            IItemFilter filter = viewModel.getFilter();
            bundle.putSerializable("filter", filter);
            Navigation.findNavController(view)
                    .navigate(R.id.filterFragment_to_itemsFragment, bundle);
        });
    }

    private void setupTextWatcher(TextView view, Consumer<String> consumer) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                consumer.accept(s.toString());
            }
        };
        view.addTextChangedListener(textWatcher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        viewModel = new ViewModelProvider(this).get(FilterViewModel.class);

        EditText dateStart = view.findViewById(R.id.edittext_filter_date_start);
        EditText dateEnd = view.findViewById(R.id.edittext_filter_date_end);

        viewModel.getStartDate().observe(getViewLifecycleOwner(), dateStart::setText);
        viewModel.getEndDate().observe(getViewLifecycleOwner(), dateEnd::setText);

        createDatePicker(dateStart, viewModel::setStartDate);
        createDatePicker(dateEnd, viewModel::setEndDate);

        EditText keywords = view.findViewById(R.id.edittext_filter_search);
        EditText make = view.findViewById(R.id.edittext_filter_make);

        setupTextWatcher(keywords, viewModel::setKeywords);
        setupTextWatcher(make, viewModel::setMake);

        setupApplyButton(view);

        return view;
    }
}