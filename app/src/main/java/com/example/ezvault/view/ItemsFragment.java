package com.example.ezvault.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezvault.ItemAdapter;
import com.example.ezvault.R;
import com.example.ezvault.model.utils.ItemListView;
import com.example.ezvault.viewmodel.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays items in a list as well as total value and total quantity
 */
@AndroidEntryPoint
public class ItemsFragment extends Fragment {
    private ItemAdapter itemAdapter;

    public ItemsFragment() {
        // Required empty public constructor
    }

    private void setupRecycler(View view, ItemListView itemListView) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        itemAdapter = new ItemAdapter(requireContext(), itemListView, (v,p) -> {});
        recyclerView.setAdapter(itemAdapter);
    }

    private void displayList(ItemListView itemListView) {
        itemAdapter.setItems(itemListView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        ItemViewModel viewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        TextView filter = view.findViewById(R.id.text_filterSort);
        filter.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_filterFragment)
        );

        FloatingActionButton floatingButton = view.findViewById(R.id.button_add_item);

        floatingButton.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_addItemFragment)
        );

        TextView totalItemValueView = view.findViewById(R.id.text_total_value);
        TextView numItemsView = view.findViewById(R.id.text_number_of_items);
        viewModel.getTotalValue().observe(getViewLifecycleOwner(), total -> {
            Locale locale = Locale.getDefault();
            String text = String.format(locale, "%.2f", total);
            totalItemValueView.setText(text);
        });
        viewModel.getNumberOfItems().observe(getViewLifecycleOwner(), number -> {
            Locale locale = Locale.getDefault();
            String text = String.format(locale, "%d", number);
            numItemsView.setText(text);

            // hide the empty items text if items exist
            TextView no_item_warning = view.findViewById(R.id.empty_items);
            if (number != 0) {
                no_item_warning.setVisibility(View.GONE);
            } else {
                no_item_warning.setVisibility(View.VISIBLE);
            }
        });

        setupRecycler(view, viewModel.getItemListView().getValue());
        viewModel.getItemListView().observe(getViewLifecycleOwner(), this::displayList);

        return view;
    }
}