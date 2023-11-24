package com.example.ezvault;

import android.content.ContentResolver;
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
import androidx.appcompat.widget.AppCompatImageButton;

import android.widget.ImageButton;

import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.camera.GalleryAction;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.FileUtils;
import com.example.ezvault.utils.TaskUtils;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
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
    AppCompatImageButton serialScan, descriptionScan;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String lastScan;

    private GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder().enableAutoZoom().build();

    private GalleryAction galleryAction;

    @Inject
    protected UserManager userManager;

    private ArrayList<Image> images;

    private AddItemPhotoAdapter photoAdapter;

    private ContentResolver contentResolver;

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
        contentResolver = requireContext().getContentResolver();

        images = new ArrayList<>();
        photoAdapter = new AddItemPhotoAdapter(requireContext(), images, userManager);

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

        syncImages(contentResolver);

        RecyclerView photoRecyclerView = view.findViewById(R.id.add_item_recyclerview);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        // Get all of our text fields
        EditText itemValue = view.findViewById(R.id.edittext_item_value);
        EditText itemQuantity = view.findViewById(R.id.edittext_item_quantity);
        EditText itemMake = view.findViewById(R.id.edittext_item_make);
        EditText itemModel = view.findViewById(R.id.edittext_item_model);
        EditText itemSerial = view.findViewById(R.id.edittext_item_serial);
        EditText itemDescription = view.findViewById(R.id.edittext_item_description);
        EditText itemComments = view.findViewById(R.id.edittext_item_comment);
        EditText itemDate = view.findViewById(R.id.edittext_item_date);

        addItem = view.findViewById(R.id.button_confirm_add_item);
        addItem.setOnClickListener(v -> {
            FirebaseBundle fb = new FirebaseBundle();
            ItemDAO itemDAO = new ItemDAO(fb);
            ImageDAO imageDAO = new ImageDAO(fb);

            List<Task<String>> imageTasks = new ArrayList<>();

            images.forEach(image -> {
                imageTasks.add(imageDAO.create(image));
            });

            Tasks.whenAllSuccess(imageTasks).onSuccessTask(t -> {
                for (int i = 0; i < images.size(); i++) {
                    images.get(i).setId((String) t.get(i));
                }

                return TaskUtils.drop(Tasks.forResult(null));

            }).continueWith(itemCreateTask -> {

                String dateText = itemDate.getText().toString();
                Date realItemDate;

                if (dateText.isEmpty()){
                    realItemDate = new Date();
                } else {
                    realItemDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateText);
                }

                // Construct our item
                ItemBuilder itemBuilder = new ItemBuilder()
                        .setMake(itemMake.getText().toString())
                        .setModel(itemModel.getText().toString())
                        .setCount(Double.valueOf(itemQuantity.getText().toString()))
                        .setValue(Double.valueOf(itemValue.getText().toString()))
                        .setAcquisitionDate(new Timestamp(realItemDate))
                        .setDescription(itemDescription.getText().toString())
                        .setSerialNumber(itemSerial.getText().toString())
                        .setComment(itemComments.getText().toString())
                        .setTags(new ArrayList<>())
                        .setImages((ArrayList<Image>)images.clone());

                // Add the new item to our database
                itemDAO.create(itemBuilder.build()).continueWith(idTask -> {
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
                                .continueWith(updateUserTask -> {
                                    images.clear();
                                    userManager.clearUriCache();
                                    Navigation.findNavController(view).popBackStack();
                                    return null;
                                });

                        return null;
                    });

                    return null;
                });
            return null;
            });
        });

        // Handle opening the camera
        ImageButton cameraButton = view.findViewById(R.id.imageButton);
        cameraButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_addItemFragment_to_cameraFragment);
        });

        // Handle opening the gallery
        ImageButton galleryButton = view.findViewById(R.id.imageButton2);
        galleryButton.setOnClickListener(v -> {
            galleryAction.resolveAll().continueWith(imTask -> {
                userManager.getUriCache().addAll(imTask.getResult());
                syncImages(contentResolver);
                return null;
            });
        });

        serialScan = view.findViewById(R.id.button_serial_scan);
        descriptionScan = view.findViewById(R.id.button_description_scan);

        serialScan.setOnClickListener(listener);
        descriptionScan.setOnClickListener(listener);

        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(AddItemFragment.this.getActivity(), options);
            if (v.getId() == serialScan.getId()) lastScan = "serial";
            else lastScan = "desc";
            scanner.startScan().addOnSuccessListener(
                    barcode -> {
                        if (lastScan == "serial") {
                            EditText SerialText = getView().findViewById(R.id.edittext_item_serial);
                            SerialText.setText(barcode.getRawValue());
                        } else {
                            EditText DescriptionText = getView().findViewById(R.id.edittext_item_description);
                            DescriptionText.setText(barcode.getRawValue());
                        }
                    }
            );
        }
    };

    /**
     * Synchronizes the recycler view of Images with the stae of the user's uri cache
     * @param contentResolver Content resolver used for reading media
     */
    private void syncImages(ContentResolver contentResolver){
        int imLen = images.size();
        int cacheLen = userManager.getUriCache().size();

        if (imLen == cacheLen) return;
        else if (imLen < cacheLen) {
            int numPhotosAdded = cacheLen - imLen;
            int addedStart = cacheLen - numPhotosAdded;

            for (int i = addedStart; i < cacheLen; i++){
                Image image = FileUtils
                        .imageFromUri(userManager.getUriCache().get(i), contentResolver);
                images.add(image);
                photoAdapter.notifyItemInserted(i);
            }
            photoAdapter.notifyItemRangeInserted(addedStart, cacheLen - 1);
        }
    }
}