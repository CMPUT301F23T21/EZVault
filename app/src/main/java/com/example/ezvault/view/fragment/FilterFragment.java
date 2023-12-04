package com.example.ezvault.view.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ezvault.R;
import com.example.ezvault.model.Tag;
import com.example.ezvault.viewmodel.FilterViewModel;
import com.example.ezvault.viewmodel.SortedItemListView;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for the filtering UI.
 */
@AndroidEntryPoint
public class FilterFragment extends Fragment {
    private FilterViewModel viewModel;
    private EditText keywords;
    private EditText make;
    private EditText tags;

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

    private void restorePreviousInput() {
        // restore previous values
        String makeText = viewModel.getMake();
        if (makeText != null) {
            make.setText(makeText);
        }
        List<String> keywordsRaw = viewModel.getKeywords();
        if (keywordsRaw != null) {
            keywords.setText(String.join(" ", keywordsRaw));
        }
        List<Tag> prevTags = viewModel.getTags();
        if (prevTags != null) {
            tags.setText(prevTags.stream().map(Tag::getContents)
                .collect(Collectors.joining(", ")));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FilterViewModel.class);

        setupDatePickers(view);

        setupEditTexts(view);

        setupApplyButton(view);
        setupResetButton(view);

        restorePreviousInput();

        setupSortingOptions(view);
    }

    private void setupDatePickers(View view) {
        EditText dateStart = view.findViewById(R.id.edittext_filter_date_start);
        EditText dateEnd = view.findViewById(R.id.edittext_filter_date_end);

        viewModel.getStartDate().observe(getViewLifecycleOwner(), dateStart::setText);
        viewModel.getEndDate().observe(getViewLifecycleOwner(), dateEnd::setText);

        createDatePicker(dateStart, viewModel::setStartDate);
        createDatePicker(dateEnd, viewModel::setEndDate);
    }

    private void setupApplyButton(View view) {
        Button applyButton = view.findViewById(R.id.button_filter_apply);
        applyButton.setOnClickListener(v -> {
            viewModel.apply();
            Navigation.findNavController(view).navigate(R.id.filterFragment_to_itemsFragment);
        });
    }

    private void setupResetButton(View view) {
        Button resetButton = view.findViewById(R.id.button_filter_reset);
        resetButton.setOnClickListener(v -> {
            viewModel.reset();
            make.setText("");
            keywords.setText("");
        });
    }

    private void setupEditTexts(View view) {
        keywords = view.findViewById(R.id.edittext_filter_search);
        setupTextWatcher(keywords, viewModel::setKeywords);

        make = view.findViewById(R.id.edittext_filter_make);
        setupTextWatcher(make, viewModel::setMake);

        tags = view.findViewById(R.id.edittext_filter_tags);
        setupTextWatcher(tags, viewModel::setTags);
    }

    private void setupSortingOptions(View view) {
        RadioGroup sortFieldGroup = view.findViewById(R.id.sort_field_group);
        RadioGroup sortOrderGroup = view.findViewById(R.id.sort_order_group);

        sortFieldGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SortedItemListView.SortField sortField;
            if (checkedId == R.id.sort_by_date) {
                sortField = SortedItemListView.SortField.DATE;
            } else if (checkedId == R.id.sort_by_description) {
                sortField = SortedItemListView.SortField.DESCRIPTION;
            } else if (checkedId == R.id.sort_by_make) {
                sortField = SortedItemListView.SortField.MAKE;
            } else if (checkedId == R.id.sort_by_value) {
                sortField = SortedItemListView.SortField.VALUE;
            }
            else if (checkedId == R.id.sort_by_tags) {
                sortField = SortedItemListView.SortField.TAGS;
            }else {
                sortField = SortedItemListView.SortField.DATE; // Default case
            }

            viewModel.setSortField(sortField);
        });

        sortOrderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SortedItemListView.SortOrder sortOrder = (checkedId == R.id.sort_ascending) ?
                    SortedItemListView.SortOrder.ASCENDING : SortedItemListView.SortOrder.DESCENDING;
            viewModel.setSortOrder(sortOrder);
        });
    }

}