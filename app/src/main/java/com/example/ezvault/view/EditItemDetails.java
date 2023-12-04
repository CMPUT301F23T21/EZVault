package com.example.ezvault.view;

import static com.example.ezvault.utils.FragmentUtils.getTextParentLayout;
import static com.example.ezvault.utils.FragmentUtils.textLayoutHasNoErrors;

import android.app.DatePickerDialog;
import android.content.ContentResolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.ezvault.viewmodel.SelectedItemViewModel;
import com.example.ezvault.view.adapter.PhotoAdapter;
import com.example.ezvault.R;
import com.example.ezvault.camera.GalleryAction;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.SerialPrediction;
import com.example.ezvault.model.SerialPredictor;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 */
@AndroidEntryPoint
public class EditItemDetails extends Fragment {

    private Button saveButton;

    /**
     * View model of item that is being currently edited
     */
    private MutableLiveData<Item> itemModel;

    /**
     * Adapter for slideshow recycler view
     */
    private PhotoAdapter photoAdapter;

    /**
     * Images to be shown in the recycler view slideshow
     */
    private ArrayList<Image> images;

    private ContentResolver contentResolver;

    @Inject
    protected UserManager userManager;

    /**
     * Allows for the selection of photos from the photo gallery
     */
    private GalleryAction galleryAction;
    private ArrayAdapter<String> serialAdapter;

    private boolean canInteract;

    private EditText makeText;
    private EditText modelText;
    private EditText descText;
    private EditText commentText;
    private EditText countText;
    private EditText valueText;

    public EditItemDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        canInteract = true;

        itemModel = new ViewModelProvider(requireActivity()).get(SelectedItemViewModel.class).get();

        images = new ArrayList<>();
        images.addAll(itemModel.getValue().getImages());

        contentResolver = requireContext().getContentResolver();
        photoAdapter = new PhotoAdapter(requireContext(), images);

        galleryAction = new GalleryAction(requireActivity());
        getLifecycle().addObserver(galleryAction);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTextWatchers();

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

        userManager.synchronizeToAdapter(images, photoAdapter);

        RecyclerView photoRecyclerView = view.findViewById(R.id.edit_details_image_recycler);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        ImageButton takePhotoButton = view.findViewById(R.id.edit_take_pic);
        ImageButton galleryButton = view.findViewById(R.id.edit_gallery);

        takePhotoButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_editItemDetails_to_cameraFragment);
        });

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

        saveButton = view.findViewById(R.id.edit_details_save);
        makeText = view.findViewById(R.id.edit_details_make);
        modelText = view.findViewById(R.id.edit_details_model);
        descText = view.findViewById(R.id.edit_details_description);
        commentText = view.findViewById(R.id.edit_details_comment);
        countText = view.findViewById(R.id.edit_details_count);
        valueText = view.findViewById(R.id.edit_details_value);
        AutoCompleteTextView serialText = view.findViewById(R.id.edit_details_serial_number);
        EditText dateText = view.findViewById(R.id.edit_details_date);

        Item raw = itemModel.getValue();
        Log.v("EZVault", "Editing item: " + raw.getId());

        //set original details
        makeText.setText(raw.getMake());
        modelText.setText(raw.getModel());
        descText.setText(raw.getDescription());
        commentText.setText(raw.getComment());
        countText.setText(String.valueOf(raw.getCount()));
        valueText.setText(String.valueOf(raw.getValue()));
        serialText.setText(raw.getSerialNumber());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = format.format(raw.getAcquisitionDate().toDate());

        dateText.setText(dateString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(raw.getAcquisitionDate().toDate());
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

                dateText.setText(format.format(calendar.getTime()));
            });
            dialog.show();
        });


        serialText.setOnClickListener(v -> serialText.showDropDown());
        serialText.setOnFocusChangeListener((v,f) -> serialText.showDropDown());
        serialAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_singlechoice);
        serialText.setAdapter(serialAdapter);
        TextInputLayout serialLayout = view.findViewById(R.id.edit_details_serial_layout);
        serialLayout.setEndIconOnClickListener(new View.OnClickListener() {
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
        });

        TextInputLayout descriptionLayout = view.findViewById(R.id.edit_details_desc_layout);
        descriptionLayout.setEndIconOnClickListener(barcodeListener);


        //update to new values on save
        saveButton.setOnClickListener(v -> {
            if(!hasValidEdits()){
                Toast.makeText(requireContext(),
                        "Please make sure all fields are valid",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            toggleInteractable();

            String make = makeText.getText().toString();
            String model = modelText.getText().toString();
            String desc = descText.getText().toString();
            String comment = commentText.getText().toString();
            double count = Double.parseDouble(countText.getText().toString());
            double value = Double.parseDouble(valueText.getText().toString());
            String serial = serialText.getText().toString();
            Timestamp date = new Timestamp(calendar.getTime());

            raw.setMake(make);
            raw.setModel(model);
            raw.setDescription(desc);
            raw.setComment(comment);
            raw.setCount(count);
            raw.setValue(value);
            raw.setSerialNumber(serial);
            raw.setAcquisitionDate(date);

            FirebaseBundle fb = new FirebaseBundle();
            ImageDAO imageDAO = new ImageDAO(fb);

            // Images that exist in the DB that the user removed
            List<Image> removedImages = raw.getImages().stream()
                    .filter(im -> !images.contains(im))
                    .collect(Collectors.toList());

            // Newly added images
            List<Image> newImages = images.stream()
                    .filter(im -> im.getId() == null)
                    .collect(Collectors.toList());


            List<Task<Void>> imageDeleteTasks = new ArrayList<>();
            List<Task<String>> imageUploadTasks = new ArrayList<>();

            if (!removedImages.isEmpty()){
                removedImages.forEach(im -> imageDeleteTasks.add(imageDAO.delete(im.getId())));
            }

            if (!newImages.isEmpty()) {
                newImages.forEach(im -> imageUploadTasks.add(imageDAO.create(im)));
            }

            Tasks.whenAllSuccess(imageDeleteTasks)
                    .onSuccessTask(t -> Tasks.whenAllSuccess(imageUploadTasks)
                            .onSuccessTask(uploadedImageTasks -> {
                                for (int i = 0; i < newImages.size(); i++) {
                                    newImages.get(i).setId((String) uploadedImageTasks.get(i));
                                }

                                raw.setImages((ArrayList<Image>) images.clone());

                                new ItemDAO(fb)
                                        .update(raw.getId(), raw)
                                        .addOnSuccessListener(x -> {
                                            userManager.clearLocalImages();
                                            toggleInteractable();

                                            Toast.makeText(requireContext(),
                                                    "Item has been updated!",
                                                    Toast.LENGTH_SHORT).show();
                                        });

                                return TaskUtils.drop(Tasks.forResult(null));
                            }));
        });

        return view;
    }

    private void setupTextWatchers(){
        makeText.addTextChangedListener(new NonEmptyTextWatcher(makeText, getTextParentLayout(makeText)));
        modelText.addTextChangedListener(new NonEmptyTextWatcher(modelText, getTextParentLayout(modelText)));
        descText.addTextChangedListener(new NonEmptyTextWatcher(descText, getTextParentLayout(descText)));
        commentText.addTextChangedListener(new NonEmptyTextWatcher(commentText, getTextParentLayout(commentText)));
        countText.addTextChangedListener(new NonEmptyTextWatcher(countText, getTextParentLayout(countText)));
        valueText.addTextChangedListener(new NonEmptyTextWatcher(valueText, getTextParentLayout(valueText)));
    }

    private boolean hasValidEdits(){
        return textLayoutHasNoErrors(getTextParentLayout(makeText),
                getTextParentLayout(modelText),
                getTextParentLayout(descText),
                getTextParentLayout(commentText),
                getTextParentLayout(countText),
                getTextParentLayout(valueText));
    }

    private void toggleInteractable(){
        canInteract = !canInteract;

        saveButton.setEnabled(canInteract);
        saveButton.setClickable(canInteract);
        saveButton.getBackground().setAlpha(canInteract ? 255 : 112);
    }

    protected View.OnClickListener barcodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .enableAutoZoom()
                    .build();
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(EditItemDetails.this.getActivity(), options);
            scanner.startScan().addOnSuccessListener(
                    barcode -> {
                        upcAPI api = new upcAPI();
                        api.upcLookup(barcode.getRawValue(), getView().findViewById(R.id.edit_details_description), getActivity());
                    }
            );
        }
    };

}