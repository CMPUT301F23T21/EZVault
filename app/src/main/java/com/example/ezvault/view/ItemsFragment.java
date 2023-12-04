package com.example.ezvault.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.viewmodel.SelectedItemViewModel;
import com.example.ezvault.view.adapter.ItemAdapter;
import com.example.ezvault.R;
import com.example.ezvault.data.database.FirebaseBundle;
import com.example.ezvault.data.database.ImageDAO;
import com.example.ezvault.data.database.ItemDAO;
import com.example.ezvault.data.database.RawUserDAO;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;
import com.example.ezvault.view.utils.ItemListView;
import com.example.ezvault.utils.UserManager;
import com.example.ezvault.viewmodel.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays items in a list as well as total value and total quantity
 */
@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
public class ItemsFragment extends Fragment {
    @Inject
    UserManager userManager;

    private ItemAdapter itemAdapter;

    private ItemViewModel viewModel;

    // UI elements that have cross-function utilization and need exposed access

    private FloatingActionButton floatingButton;

    ImageButton tagItemsButton;


    public ItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
    }

    private void setupRecycler(View view, ItemListView itemListView) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        itemAdapter = new ItemAdapter(requireContext(), itemListView, new ItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("EZVault", userManager.getUser().getItemList().get(position).getModel());

                SelectedItemViewModel viewModel = new ViewModelProvider(requireActivity())
                        .get(SelectedItemViewModel.class);

                viewModel.set(userManager.getUser()
                        .getItemList()
                        .get(position));

                Navigation.findNavController(view).navigate(R.id.action_itemsFragment_to_editItemDetails);
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

        // navigation to filter fragment
        TextView filter = view.findViewById(R.id.text_filterSort);
        filter.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_filterFragment)
        );

        // navigation to add item fragment
        floatingButton = view.findViewById(R.id.button_add_item);
        floatingButton.setOnClickListener(v -> {
            itemAdapter.clearSelected(); // We don't want them to persist over to item creation
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_addItemFragment);
        });

        tagItemsButton = view.findViewById(R.id.tag_items_button);
        tagItemsButton.setOnClickListener(v -> tagSelected());

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

    private void deleteItemImages(Item item, ImageDAO imageDAO){
        item.getImages().forEach(image -> {
            imageDAO.delete(image.getId());
        });
    }


    private void tagSelected() {
        if (itemAdapter.getSelectedItems().isEmpty()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setTitle("Tag Items");

        builder.setPositiveButton("Add Tag(s)", (dialog, x) -> {
            String inputText = input.getText().toString();
            HashSet<String> tagNames =
                    new HashSet<>(Arrays.asList(inputText.split("\\s*,\\s*")));

            HashSet<Tag> tags = new HashSet<>();
            for (Tag tag : userManager.getUser().getItemList().getTags()) {
                if (tagNames.contains(tag.getContents())) {
                    tags.add(tag);
                }
            }
            for (Item item : itemAdapter.getSelectedItems()) {
                for (Tag tag : tags) {
                    item.getTags().add(tag);
                    new ItemDAO(new FirebaseBundle()).update(item.getId(), item);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, x) -> dialog.dismiss());

        builder.show();
    }

    private void deleteSelected(){
        FirebaseBundle fb = new FirebaseBundle();
        ItemDAO itemDAO = new ItemDAO(fb);
        ImageDAO imageDAO = new ImageDAO(fb);
        RawUserDAO rawUserDAO = new RawUserDAO(fb);

        User currUser = userManager.getUser();

        List<Item> unselectedItems = itemAdapter.getUnselectedItems();
        List<Item> selectedItems = itemAdapter.getSelectedItems();

        List<String> selectedItemIds = unselectedItems.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());

        rawUserDAO.update(currUser.getUid(), new RawUserDAO.RawUser(currUser.getUserName(),
                        (ArrayList<String>) currUser.getItemList().getTagIds(),
                        (ArrayList<String>) selectedItemIds))
                        .continueWith(t -> {
                            for (int i = 0; i < selectedItems.size(); i++){
                                Item item = selectedItems.get(i);

                                itemDAO.delete(item.getId());
                                deleteItemImages(item, imageDAO);

                                userManager.getUser()
                                        .getItemList()
                                        .remove(item);

                                itemAdapter.notifyItemRemoved(i);
                            }

                            viewModel.synchronizeItems(userManager.getUser().getItemList());

                            itemAdapter.notifyDataSetChanged();
                            return null;
                        });
    }

    @Override
    public void onPause() {
        super.onPause();

        itemAdapter.deleteMode = false;
        itemAdapter.clearSelected();
    }

    private void setupToolbar(){
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();

                if(itemAdapter.deleteMode) {
                    menuInflater.inflate(R.menu.toolbar_edit_item_menu, menu);
                } else {
                    menuInflater.inflate(R.menu.toolbar_menu, menu);
                }
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();

                if (menuId == R.id.toolbar_trash || menuId == R.id.edit_item_cancel){
                    toggleDeleteMode(true);
                    menuHost.invalidateMenu();
                } else if (menuId == R.id.edit_item_confirm && !itemAdapter.getSelectedItems().isEmpty()) {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Are you sure you want to delete " + itemAdapter.getSelectedCount() + " item(s)?")
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                toggleDeleteMode(false);
                                deleteSelected();
                                menuHost.invalidateMenu();
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void toggleDeleteMode(boolean updateAdapter){
        boolean isDeleting = !itemAdapter.deleteMode;
        itemAdapter.deleteMode = isDeleting;

        floatingButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
        tagItemsButton.setVisibility(isDeleting ? View.VISIBLE : View.GONE);

        if (updateAdapter) {
            itemAdapter.notifyDataSetChanged();
        }
    }

}