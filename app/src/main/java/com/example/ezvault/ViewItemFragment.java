package com.example.ezvault;

import android.content.ContentResolver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.User;
import com.example.ezvault.viewmodel.ItemViewModel;
import com.example.ezvault.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import me.relex.circleindicator.CircleIndicator3;

/**
 * Allows user to view an item's details
 */
@AndroidEntryPoint
public class ViewItemFragment extends Fragment {
    @Inject
    UserManager userManager;

    // view model of item that is being displayed
    private MutableLiveData<Item> itemModel;

    // adapter for view pager
    private ViewpagerAdapter viewpagerAdapter;

    // images to be shown in view pager
    private ArrayList<Image> images;

    private ItemViewModel viewModel;
    public ViewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup variables
        itemModel = new ViewModelProvider(requireActivity()).get(com.example.ezvault.ItemViewModel.class).get();
        images = new ArrayList<>();
        images.addAll(itemModel.getValue().getImages());
        viewpagerAdapter = new ViewpagerAdapter(requireContext(), images);
        viewModel = new ViewModelProvider(this).get(com.example.ezvault.viewmodel.ItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_item_details, container, false);

        // load images into the viewpager
        userManager.synchronizeToPager(images, viewpagerAdapter);
        ViewPager2 viewPager = view.findViewById(R.id.view_item_viewpager);
        viewPager.setAdapter(viewpagerAdapter);

        // Set up the circle indicator
        CircleIndicator3 circleIndicator = view.findViewById(R.id.circle_indicator);
        circleIndicator.setViewPager(viewPager);

        // find text fields
        EditText makeText = view.findViewById(R.id.view_details_make);
        EditText modelText = view.findViewById(R.id.view_details_model);
        EditText descText = view.findViewById(R.id.view_details_description);
        EditText commentText = view.findViewById(R.id.view_details_comment);
        EditText valueText = view.findViewById(R.id.view_details_value);
        EditText countText = view.findViewById(R.id.view_details_count);
        EditText serialText = view.findViewById(R.id.view_details_serial_number);
        EditText dateText = view.findViewById(R.id.view_details_date);

        Item raw = itemModel.getValue();
        Log.v("EZVault", "Viewing item: " + raw.getId());

        // set item details
        makeText.setText(raw.getMake());
        modelText.setText(raw.getModel());
        descText.setText(raw.getDescription());
        commentText.setText(raw.getComment());
        countText.setText(String.valueOf(raw.getCount()));
        valueText.setText(String.valueOf(raw.getValue()));
        serialText.setText(raw.getSerialNumber());

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String dateString = format.format(raw.getAcquisitionDate().toDate());

        dateText.setText(dateString);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // update the toolbar menu
        MenuHost menuHost = (MenuHost) requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.toolbar_view_item_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int menuID = menuItem.getItemId();

                // navigate to edit item fragment
                if (menuID == R.id.view_item_edit) {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Do you want to edit item details?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Navigation.findNavController(view).navigate(R.id.viewItemFragment_to_editItemDetails);
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }

                // delete item
                else if (menuID == R.id.view_item_delete) {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Are you sure you want to delete this item?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                deleteItem();
                                Navigation.findNavController(view).navigate(R.id.viewItemFragment_to_itemsFragment);
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void deleteItem() {
        FirebaseBundle fb = new FirebaseBundle();
        ItemDAO itemDAO = new ItemDAO(fb);
        ImageDAO imageDAO = new ImageDAO(fb);
        RawUserDAO rawUserDAO = new RawUserDAO(fb);

        User currUser = userManager.getUser();

        List<Item> itemList = userManager.getUser().getItemList();
        itemList.remove(itemModel.getValue());
        List<String> itemStr = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());

        List<String> tagIds = currUser.getItemList()
                .getTags()
                .stream()
                .map(tag -> tag.getUid())
                .collect(Collectors.toList());

        rawUserDAO.update(currUser.getUid(), new RawUserDAO.RawUser(currUser.getUserName(), (ArrayList<String>) tagIds, (ArrayList<String>) itemStr))
                .continueWith(t -> {
                    Item item = itemModel.getValue();
                    itemDAO.delete(item.getId());

                    item.getImages().forEach(image -> {
                        imageDAO.delete(image.getId());
                    });

                    userManager.getUser()
                            .getItemList()
                            .remove(item);

                    viewModel.synchronizeItems(userManager.getUser().getItemList());
                    viewpagerAdapter.notifyDataSetChanged();
                    return null;
                });
    }
}