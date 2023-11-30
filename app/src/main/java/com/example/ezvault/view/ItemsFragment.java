package com.example.ezvault.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ezvault.ItemAdapter;
import com.example.ezvault.R;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.database.TagDAO;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.User;
import com.example.ezvault.model.utils.ItemListView;
import com.example.ezvault.utils.UserManager;
import com.example.ezvault.viewmodel.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays items in a list as well as total value and total quantity
 */
@AndroidEntryPoint
public class ItemsFragment extends Fragment {
    @Inject
    UserManager userManager;

    private ItemAdapter itemAdapter;

    private ItemViewModel viewModel;


    public ItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }

    private void setupRecycler(View view, ItemListView itemListView) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        itemAdapter = new ItemAdapter(requireContext(), itemListView, new ItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("EZVault", userManager.getUser().getItemList().get(position).getModel());

                com.example.ezvault.ItemViewModel viewModel = new ViewModelProvider(requireActivity())
                        .get(com.example.ezvault.ItemViewModel.class);

                viewModel.set(userManager.getUser()
                        .getItemList()
                        .get(position));

                Navigation.findNavController(view).navigate(R.id.action_itemsFragment_to_editItemDetails);
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!itemAdapter.editMode) {
                    itemAdapter.editMode = true;
                }
            }
        });

        recyclerView.setAdapter(itemAdapter);
    }

    private void displayList(ItemListView itemListView) {
        itemAdapter.setItems(itemListView);
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

        floatingButton.setOnClickListener(v -> {
            itemAdapter.clearSelected(); // We don't want them to persist over to item creation
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_addItemFragment);
        });

        TextView totalItemValueView = view.findViewById(R.id.text_total_value);
        TextView numItemsView = view.findViewById(R.id.text_number_of_items);

        Switch edit_switch = view.findViewById(R.id.edit_switch);
        edit_switch.setOnClickListener(v -> {
            // The edit switch is temporary and will be replaced, not sure of its functionality yet
            itemAdapter.editMode = false;
            itemAdapter.clearSelected();
        });

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

    // Delete item from raw user
    // Delete the images
    // Delete the item


    private void deleteItemImages(Item item){
        ImageDAO imageDAO = new ImageDAO(new FirebaseBundle());
        item.getImages().forEach(image -> {
            imageDAO.delete(image.getId());
        });
    }

    private void deleteSelected(){
        ItemDAO itemDAO;

        RawUserDAO rawUserDAO = new RawUserDAO(new FirebaseBundle());
        User currUser = userManager.getUser();

        List<String> itemStr = itemAdapter.getUnselectedItems()
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());

        List<String> tagIds = currUser.getItemList()
                .getTags()
                .stream().
                map(tag -> tag.getUid())
                .collect(Collectors.toList());

        rawUserDAO.update(currUser.getUid(), new RawUserDAO.RawUser(currUser.getUserName(), (ArrayList<String>) tagIds, (ArrayList<String>) itemStr))
                        .continueWith(t -> {
                            return null;
                        });

        itemAdapter.getSelectedItems().forEach(item -> {
            deleteItemImages(item);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        itemAdapter.clearSelected();
    }
}