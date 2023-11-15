package com.example.ezvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.ezvault.camera.GalleryAction;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * fragment class that collects the information of a new item
 */
@AndroidEntryPoint
public class AddItemFragment extends Fragment {

    Button addItem;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GalleryAction galleryAction;

    @Inject
    protected UserManager userManager;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        galleryAction = new GalleryAction(requireActivity());
        getLifecycle().addObserver(galleryAction);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = (MenuHost) requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        EditText itemName = view.findViewById(R.id.edittext_item_name);
        EditText itemDate = view.findViewById(R.id.edittext_item_date);
        EditText itemValue = view.findViewById(R.id.edittext_item_value);
        EditText itemQuantity = view.findViewById(R.id.edittext_item_quantity);
        EditText itemMake = view.findViewById(R.id.edittext_item_make);
        EditText itemModel = view.findViewById(R.id.edittext_item_model);
        EditText itemSerial = view.findViewById(R.id.edittext_item_serial);
        EditText itemDescription = view.findViewById(R.id.edittext_item_description);
        EditText itemComments = view.findViewById(R.id.edittext_item_comment);


        addItem = view.findViewById(R.id.button_confirm_add_item);
        addItem.setOnClickListener(v -> {
            FirebaseBundle fb = new FirebaseBundle();
            ItemDAO itemDAO = new ItemDAO(fb);

            // Construct our item
            ItemBuilder itemBuilder = new ItemBuilder()
                    .setMake(itemMake.getText().toString())
                    .setModel(itemModel.getText().toString())
                    .setCount(Double.valueOf(itemQuantity.getText().toString()))
                    .setValue(Double.valueOf(itemValue.getText().toString()))
                    .setAcquisitionDate(Timestamp.now())
                    .setDescription(itemDescription.getText().toString())
                    .setSerialNumber(itemSerial.getText().toString())
                    .setComment(itemComments.getText().toString())
                    .setTags(new ArrayList<>())
                    .setImages(new ArrayList<>());

            // Add the new item to our database
            itemDAO.create(itemBuilder.build()).continueWith(idTask ->{
                String itemId = idTask.getResult();

                // User info
                User localUser = userManager.getUser();
                String uid = localUser.getUid();

                // Add the item to our local store
                itemBuilder.setId(itemId);
                localUser.getItemList().add(itemBuilder.build());

                RawUserDAO rawUserDAO = new RawUserDAO(fb);

                // Fetch our raw local user
                // TODO: Expose raw user so we dont have to fetch every tag/item creation or update
                rawUserDAO.read(uid).continueWith(rawUserTask -> {
                    RawUserDAO.RawUser rawUser = rawUserTask.getResult();
                    rawUser.getItemids().add(itemId); // Add the item to our user database reference

                    rawUserDAO.update(uid, rawUser)
                            .continueWith(updateUserTask -> Navigation.findNavController(view).popBackStack());

                    return null;
                });

                return null;
            });

        });

        return view;
    }

}