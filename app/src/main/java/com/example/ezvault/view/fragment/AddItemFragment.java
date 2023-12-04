package com.example.ezvault.view.fragment;




import static com.example.ezvault.utils.FragmentUtils.getTextParentLayout;
import static com.example.ezvault.utils.FragmentUtils.textLayoutHasNoErrors;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.view.adapter.PhotoAdapter;
import com.example.ezvault.R;
import com.example.ezvault.camera.GalleryAction;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.SerialPrediction;
import com.example.ezvault.model.SerialPredictor;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;
import com.example.ezvault.textwatchers.NonEmptyTextWatcher;
import com.example.ezvault.upcAPI;
import com.example.ezvault.utils.FileUtils;
import com.example.ezvault.utils.TaskUtils;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * fragment class that collects the information of a new item
 */
@AndroidEntryPoint
public class AddItemFragment extends Fragment {

    private final com.example.ezvault.upcAPI upcLookup = new upcAPI();
    private Button createButton;

    private AutoCompleteTextView itemSerial;
    private String lastScan;

    private GalleryAction galleryAction;

    @Inject
    protected UserManager userManager;

    private ArrayList<Image> images;

    private PhotoAdapter photoAdapter;

    private ContentResolver contentResolver;

    private boolean canInteract;

    private ArrayAdapter<String> serialAdapter;

    private EditText itemMake;
    private EditText itemModel;
    private EditText itemDescription;
    private EditText itemComments;
    private EditText itemQuantity;
    private EditText itemValue;
    private EditText itemDate;

    private Calendar calendar;

    private upcAPI upcAPI;


    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendar = Calendar.getInstance();

        canInteract = true;

        contentResolver = requireContext().getContentResolver();

        images = new ArrayList<>();
        photoAdapter = new PhotoAdapter(requireContext(), images);

        galleryAction = new GalleryAction(requireActivity());
        getLifecycle().addObserver(galleryAction);

        upcAPI = new upcAPI();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupTextWatchers();
        setupCreateButton(view);
        setupToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_item_details, container, false);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !canInteract;
            }
        });

        createButton = view.findViewById(R.id.edit_details_save);
        createButton.setText("Create");

        itemMake = view.findViewById(R.id.edit_details_make);
        itemModel = view.findViewById(R.id.edit_details_model);
        itemDescription = view.findViewById(R.id.edit_details_description);
        itemComments = view.findViewById(R.id.edit_details_comment);
        itemQuantity = view.findViewById(R.id.edit_details_count);
        itemValue = view.findViewById(R.id.edit_details_value);
        itemSerial = view.findViewById(R.id.edit_details_serial_number);
        itemDate = view.findViewById(R.id.edit_details_date);

        userManager.synchronizeToAdapter(images, photoAdapter);

        RecyclerView photoRecyclerView = view.findViewById(R.id.edit_details_image_recycler);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String dateString = format.format(new Date());

        itemDate.setText(dateString);

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        TextInputLayout dateLayout = view.findViewById(R.id.edit_details_date_layout);
        dateLayout.setEndIconOnClickListener(v -> {
            Log.d("EZVault", "Setting new date.");

            DatePickerDialog dialog = new DatePickerDialog(requireContext());
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            dialog.updateDate(y, m, d);
            dialog.setOnDateSetListener((datePicker, year, month, day) -> {
                calendar.set(year, month, day);

                itemDate.setText(format.format(calendar.getTime()));
            });
            dialog.show();
        });

        serialAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.select_dialog_singlechoice);

        itemSerial.setOnClickListener(v -> itemSerial.showDropDown());
        itemSerial.setOnFocusChangeListener((v, f) -> itemSerial.showDropDown());

        itemSerial.setAdapter(serialAdapter);

        TextInputLayout serialLayout = view.findViewById(R.id.edit_details_serial_layout);
        TextInputLayout descriptionLayout = view.findViewById(R.id.edit_details_desc_layout);


        serialLayout.setEndIconOnClickListener(serialListener);

        descriptionLayout.setErrorIconDrawable(R.drawable.icon_barcode_scan);
        descriptionLayout.setErrorIconOnClickListener(barcodeListener);
        descriptionLayout.setEndIconOnClickListener(barcodeListener);

        createButton.setOnClickListener(v -> {
            toggleInteractable();

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
                // Construct our item
                ItemBuilder itemBuilder = new ItemBuilder()
                        .setMake(itemMake.getText().toString())
                        .setModel(itemModel.getText().toString())
                        .setCount(Double.parseDouble(itemQuantity.getText().toString()))
                        .setValue(Double.parseDouble(itemValue.getText().toString()))
                        .setAcquisitionDate(new Timestamp(calendar.getTime()))
                        .setDescription(itemDescription.getText().toString())
                        .setSerialNumber(itemSerial.getText().toString())
                        .setComment(itemComments.getText().toString())
                        .setTags(new ArrayList<>())
                        .setImages((ArrayList<Image>) images.clone());

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

                    List<String> itemIds = localUser.getItemList()
                            .stream()
                            .map(Item::getId)
                            .collect(Collectors.toList());

                    List<String> tagIds = localUser
                            .getItemList()
                            .getTags()
                            .stream()
                            .map(Tag::getUid)
                            .collect(Collectors.toList());

                    // Update raw user
                    RawUserDAO.RawUser rawUser = new RawUserDAO.RawUser(localUser.getUserName(),
                            (ArrayList<String>) tagIds,
                            (ArrayList<String>) itemIds);

                    rawUserDAO.update(uid, rawUser)
                            .continueWith(updateUserTask -> {
                                images.clear();
                                userManager.clearLocalImages();
                                Navigation.findNavController(view).popBackStack();
                                return null;
                            });

                    return null;
                });
                return null;
            });
        });

        // Handle opening the camera
        ImageButton cameraButton = view.findViewById(R.id.edit_take_pic);
        cameraButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.addItemFragment_to_cameraFragment);
        });

        // Handle opening the gallery
        ImageButton galleryButton = view.findViewById(R.id.edit_gallery);
        galleryButton.setOnClickListener(v -> {
            galleryAction.resolveAll().continueWith(imTask -> {
                List<Uri> uris = imTask.getResult();

                if (uris != null && uris.size() > 0) {
                    uris.forEach(uri -> {
                        userManager.addLocalImage(FileUtils.imageFromUri(uri, contentResolver));
                    });

                    userManager.synchronizeToAdapter(images, photoAdapter);
                }

                return null;
            });
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Thread upcThread = upcAPI.getBgThread();
        if (upcThread != null){
            try {
                upcThread.join();
            } catch (InterruptedException e) {
                Log.e("EZVAULT", "Could not join upc thread");
            }
        }
    }

    private View.OnClickListener serialListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            galleryAction.resolve().continueWithTask(uriTask -> {
                Uri uri = uriTask.getResult();

                if (uri == null) {
                    return null;
                } // Null if the user didn't select an image

                Image image = FileUtils.imageFromUri(uri, contentResolver);
                Bitmap bmp = BitmapFactory.decodeByteArray(image.getContents(), 0, image.getContents().length);

                return TaskUtils.onSuccessProc(new SerialPredictor().predict(bmp, 0),
                        predictions -> {
                            serialAdapter.addAll(predictions
                                    .stream()
                                    .sorted(Comparator.comparing(SerialPrediction::getConfidence)
                                            .reversed())
                                    .map(SerialPrediction::getContents)
                                    .collect(Collectors.toList()));

                            serialAdapter.notifyDataSetChanged();
                        });
            });
        }
    };

    protected View.OnClickListener barcodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .enableAutoZoom()
                    .build();
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(requireActivity(), options);
            if (v.getId() == itemSerial.getId()) lastScan = "serial";
            else lastScan = "desc";
            scanner.startScan().addOnSuccessListener(
                    barcode -> {
                        if (lastScan.equals("serial")) {
                            itemSerial.setText(barcode.getRawValue());
                        } else {
                            upcAPI.upcLookup(barcode.getRawValue(), itemDescription, requireActivity());
                        }
                    }
            );
        }
    };

    private void setupTextWatchers(){
        itemMake.addTextChangedListener(new NonEmptyTextWatcher(itemMake, getTextParentLayout(itemMake)));
        itemModel.addTextChangedListener(new NonEmptyTextWatcher(itemModel, getTextParentLayout(itemModel)));
        itemDescription.addTextChangedListener(new NonEmptyTextWatcher(itemDescription, getTextParentLayout(itemDescription)));
        itemComments.addTextChangedListener(new NonEmptyTextWatcher(itemComments, getTextParentLayout(itemComments)));
        itemQuantity.addTextChangedListener(new NonEmptyTextWatcher(itemQuantity, getTextParentLayout(itemQuantity)));
        itemValue.addTextChangedListener(new NonEmptyTextWatcher(itemValue, getTextParentLayout(itemValue)));
    }

    private boolean isValidItem(){
        return textLayoutHasNoErrors(getTextParentLayout(itemMake),
                getTextParentLayout(itemModel),
                getTextParentLayout(itemDescription),
                getTextParentLayout(itemComments),
                getTextParentLayout(itemQuantity),
                getTextParentLayout(itemValue));
    }

    private void toggleInteractable(){
        canInteract = !canInteract;

        createButton.setEnabled(canInteract);
        createButton.setClickable(canInteract);
        createButton.getBackground().setAlpha(canInteract ? 255 : 112);
    }

    private void setupCreateButton(View view){
        createButton.setOnClickListener(v -> {
            if(!isValidItem()){
                Toast.makeText(requireContext(),
                        "Please make sure all fields are valid",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            toggleInteractable();

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
                // Construct our item
                ItemBuilder itemBuilder = new ItemBuilder()
                        .setMake(itemMake.getText().toString())
                        .setModel(itemModel.getText().toString())
                        .setCount(Double.parseDouble(itemQuantity.getText().toString()))
                        .setValue(Double.parseDouble(itemValue.getText().toString()))
                        .setAcquisitionDate(new Timestamp(calendar.getTime()))
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
                    ItemList itemsList = localUser.getItemList();

                    // Add the item to our local store
                    itemBuilder.setId(itemId);
                    itemsList.add(itemBuilder.build());

                    RawUserDAO rawUserDAO = new RawUserDAO(fb);

                    // Update raw user
                    RawUserDAO.RawUser rawUser = new RawUserDAO.RawUser(localUser.getUserName(),
                            (ArrayList<String>) itemsList.getTagIds(),
                            (ArrayList<String>) itemsList.getItemIds());

                    rawUserDAO.update(uid, rawUser)
                            .continueWith(updateUserTask -> {
                                images.clear();
                                userManager.clearLocalImages();
                                Navigation.findNavController(view).popBackStack();
                                return null;
                            });

                    return null;
                });
                return null;
            });
        });
    }

    private void setupToolbar(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return !canInteract;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

}
