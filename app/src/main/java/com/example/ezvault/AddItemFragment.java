package com.example.ezvault;

import android.content.ContentResolver;
import android.app.DatePickerDialog;

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

import androidx.annotation.NonNull;

import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.example.ezvault.model.SerialPrediction;
import com.example.ezvault.model.SerialPredictor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * fragment class that collects the information of a new item
 */
@AndroidEntryPoint
public class AddItemFragment extends Fragment {

    private Button createButton;

    private AutoCompleteTextView itemSerial;
    private static final String TAG = "AddItem";
    private Button addItem;

    private String lastScan;

    private GmsBarcodeScannerOptions options;

    private GalleryAction galleryAction;

    @Inject
    protected UserManager userManager;

    private ArrayList<Image> images;

    private PhotoAdapter photoAdapter;

    private ContentResolver contentResolver;

    private boolean canInteract;

    private ArrayAdapter<String> serialAdapter;

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        canInteract = true;

        contentResolver = requireContext().getContentResolver();

        images = new ArrayList<>();
        photoAdapter = new PhotoAdapter(requireContext(), images);

        galleryAction = new GalleryAction(requireActivity());
        getLifecycle().addObserver(galleryAction);

        options = new GmsBarcodeScannerOptions.Builder()
                .enableAutoZoom()
                .build();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Updates the toolbar menu
        MenuHost menuHost = (MenuHost) requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
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

        EditText itemMake = view.findViewById(R.id.edit_details_make);
        EditText itemModel = view.findViewById(R.id.edit_details_model);
        EditText itemDescription = view.findViewById(R.id.edit_details_description);
        EditText itemComments = view.findViewById(R.id.edit_details_comment);
        EditText itemQuantity = view.findViewById(R.id.edit_details_count);
        EditText itemValue = view.findViewById(R.id.edit_details_value);
        itemSerial = view.findViewById(R.id.edit_details_serial_number);
        EditText itemDate = view.findViewById(R.id.edit_details_date);

        userManager.synchronizeToAdapter(images, photoAdapter);

        RecyclerView photoRecyclerView = view.findViewById(R.id.edit_details_image_recycler);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String dateString = format.format(new Date());

        itemDate.setText(dateString);

        Calendar calendar = Calendar.getInstance();
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
        itemSerial.setOnFocusChangeListener((v,f) -> itemSerial.showDropDown());

        itemSerial.setAdapter(serialAdapter);

        TextInputLayout serialLayout = view.findViewById(R.id.edit_details_serial_layout);
        TextInputLayout descriptionLayout = view.findViewById(R.id.edit_details_desc_layout);


        serialLayout.setEndIconOnClickListener(serialListener);
        descriptionLayout.setEndIconOnClickListener(listener);

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
                        .setCount(Double.valueOf(itemQuantity.getText().toString()))
                        .setValue(Double.valueOf(itemValue.getText().toString()))
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

                    // Add the item to our local store
                    itemBuilder.setId(itemId);
                    localUser.getItemList().add(itemBuilder.build());

                    RawUserDAO rawUserDAO = new RawUserDAO(fb);

                    List<String> itemIds = localUser.getItemList()
                            .stream()
                            .map(item -> item.getId())
                            .collect(Collectors.toList());

                    List<String> tagIds = localUser
                            .getItemList()
                            .getTags()
                            .stream()
                            .map(tag -> tag.getUid())
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

                if (uris != null && uris.size() > 0){
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


    private View.OnClickListener serialListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            galleryAction.resolve().continueWithTask(uriTask-> {
                Uri uri = uriTask.getResult();

                if (uri == null) { return null; } // Null if the user didn't select an image

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

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(AddItemFragment.this.getActivity(), options);
            if (v.getId() == itemSerial.getId()) lastScan = "serial";
            else lastScan = "desc";
            scanner.startScan().addOnSuccessListener(
                    barcode -> {
                        if (lastScan.equals("serial")) {
                            EditText SerialText = getView().findViewById(R.id.edittext_item_serial);
                            SerialText.setText(barcode.getRawValue());
                        } else {
                            updateDescription(barcode.getRawValue());
                        }
                    }
            );
        }
    };




private void toggleInteractable(){
        canInteract = !canInteract;

        createButton.setEnabled(canInteract);
        createButton.setClickable(canInteract);
        createButton.getBackground().setAlpha(canInteract ? 255 : 112);
    }

    public void updateDescription(String UPC) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String UPCurl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + URLEncoder.encode(UPC);
                    URL url = null;

                    // Store exit status for processing - 0 is normal, 2 is network error, 1 is item not found
                    int code = 0;

                    // Create the URL object
                    try {
                        url = new URL(UPCurl);
                    } catch (MalformedURLException e) {
                        code = 2;
                        Log.e(TAG, "Malformed URL");
                    }

                    URLConnection connection;
                    String itemname = "";
                    try {
                        connection = url.openConnection();
                        connection.connect();
                        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) connection.getContent()));
                        Log.i(TAG, root.toString());
                        JsonObject rootobj = root.getAsJsonObject();
                        if (rootobj.isEmpty() || rootobj.get("items").getAsJsonArray().isEmpty()) {
                            code = 1;
                            Log.i(TAG, "No items found");
                        } else {
                            itemname = rootobj.get("items").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                        }
                    } catch (IOException e) {
                        code = 2;
                        Log.e("TAG", e.toString());
                    }

                    String finalItemname = itemname;
                    Log.i(TAG, itemname);
                    int finalCode = code;
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            EditText DescriptionText = getView().findViewById(R.id.edittext_item_description);

                            switch (finalCode) {
                                case 0:
                                    DescriptionText.setText(finalItemname);
                                    break;
                                case 1:
                                    DescriptionText.setText("No items found");
                                    break;
                                case 2:
                                    DescriptionText.setText("Network error");
                                    break;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }
}
