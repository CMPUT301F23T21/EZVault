package com.example.ezvault.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import com.example.ezvault.R;
import com.example.ezvault.model.utils.ItemListView;
import com.example.ezvault.viewmodel.ItemViewModel;
import com.example.ezvault.viewmodel.SortedItemListView;
import com.example.ezvault.model.Item;

import java.util.ArrayList;
import java.util.List;

public class SortFragment extends Fragment {
    private SortedItemListView sortedItemListView;
    private ItemViewModel itemViewModel;
    public SortFragment() {
        // Required empty public constructor
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

        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        itemViewModel.getItemListView().observe(getViewLifecycleOwner(), this::onItemListUpdated);

        setupSortOptions(view);
        setupApplyButton(view);
    }
    private void onItemListUpdated(ItemListView itemListView) {
        // 根据观察到的数据更新 SortedItemListView
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < itemListView.size(); i++) {
            items.add(itemListView.get(i));
        }
        sortedItemListView = new SortedItemListView(items);

        updateUI();
    }
    private void setupSortOptions(View view) {
        RadioGroup sortFieldGroup = view.findViewById(R.id.sort_field_group);
        RadioGroup sortOrderGroup = view.findViewById(R.id.sort_order_group);

        sortFieldGroup.setOnCheckedChangeListener((group, checkedId) -> {
            sortedItemListView.sortItems(getSelectedSortField(checkedId), sortedItemListView.getCurrentSortOrder());
        });

        sortOrderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            sortedItemListView.sortItems(sortedItemListView.getCurrentSortField(),
                    checkedId == R.id.sort_ascending ? SortedItemListView.SortOrder.ASCENDING : SortedItemListView.SortOrder.DESCENDING);
        });
    }

    private SortedItemListView.SortField getSelectedSortField(int checkedId) {
        if (checkedId == R.id.sort_by_date) {
            return SortedItemListView.SortField.DATE;
        } else if (checkedId == R.id.sort_by_description) {
            return SortedItemListView.SortField.DESCRIPTION;
        } else if (checkedId == R.id.sort_by_make) {
            return SortedItemListView.SortField.MAKE;
        } else if (checkedId == R.id.sort_by_value) {
            return SortedItemListView.SortField.VALUE;
        } else {
            return SortedItemListView.SortField.DATE;
        }
    }

    private void setupApplyButton(View view) {
        Button applyButton = view.findViewById(R.id.button_filter_apply);
        applyButton.setOnClickListener(v -> {
            updateUI();
        });
    }

    private void updateUI() {
    }
}
