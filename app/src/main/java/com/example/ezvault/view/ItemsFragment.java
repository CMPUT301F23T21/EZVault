package com.example.ezvault.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezvault.ItemAdapter;
import com.example.ezvault.R;
import com.example.ezvault.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays items in a list as well as total value and total quantity
 */
@AndroidEntryPoint
public class ItemsFragment extends Fragment {

    @Inject
    public UserManager userManager;

    public ItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        TextView filter = view.findViewById(R.id.text_filterSort);
        filter.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_filterFragment)
        );


        FloatingActionButton floatingButton = view.findViewById(R.id.button_add_item);

        floatingButton.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_addItemFragment)
        );

        // Item adapter for the recycler view
        ItemAdapter itemAdapter = new ItemAdapter(view.getContext(),
                userManager.getUser().getItemList(),
                (v, position) -> {});

        // Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setItems(userManager.getUser().getItemList());

        // hide the empty items text if items exist
        TextView no_item_warning = view.findViewById(R.id.empty_items);
        if (itemAdapter.getItemCount() != 0) {
            no_item_warning.setVisibility(View.GONE);
        }
        else {
            no_item_warning.setVisibility(View.VISIBLE);
        }

        // set the total quantity and total cost text
        TextView numItemsView = view.findViewById(R.id.text_number_of_items);
        numItemsView.setText(String.valueOf(itemAdapter.getItemCount()));

        TextView totalItemValueView = view.findViewById(R.id.text_total_value);
        double totalItemValue = userManager.getUser().getItemList().getTotalValue();
        totalItemValueView.setText(String.valueOf(totalItemValue));

        // Inflate the layout for this fragment
        return view;
    }
}