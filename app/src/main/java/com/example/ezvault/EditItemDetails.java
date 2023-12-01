package com.example.ezvault;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ezvault.camera.GalleryAction;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.ImageDAO;
import com.example.ezvault.database.ItemDAO;
import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.SerialPrediction;
import com.example.ezvault.model.SerialPredictor;
import com.example.ezvault.utils.FileUtils;
import com.example.ezvault.utils.TaskUtils;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

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

    public EditItemDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class).get();

        images = new ArrayList<>();
        images.addAll(itemModel.getValue().getImages());

        contentResolver = requireContext().getContentResolver();
        photoAdapter = new PhotoAdapter(requireContext(), images, userManager);

        galleryAction = new GalleryAction(requireActivity());
        getLifecycle().addObserver(galleryAction);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_item_details, container, false);

        syncImages(contentResolver);

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
                userManager.getUriCache().addAll(imTask.getResult());
                syncImages(contentResolver);
                return null;
            });
        });

        Button saveButton = view.findViewById(R.id.edit_details_save);
        EditText makeText = view.findViewById(R.id.edit_details_make);
        EditText modelText = view.findViewById(R.id.edit_details_model);
        EditText descText = view.findViewById(R.id.edit_details_description);
        EditText commentText = view.findViewById(R.id.edit_details_comment);
        EditText countText = view.findViewById(R.id.edit_details_count);
        EditText valueText = view.findViewById(R.id.edit_details_value);
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
        serialLayout.setEndIconOnClickListener(v -> {
            Log.d("EZVAultT", "Serial edit layout thing");
            Task<Image> imageTask = TaskUtils.onSuccess(galleryAction.resolve(), uri ->
                    FileUtils.imageFromUri(uri, contentResolver));
            imageTask.onSuccessTask(image -> {
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
        });


        //update to new values on save
        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);

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
                                            userManager.clearUriCache();
                                            saveButton.setEnabled(true);
                                        });

                                return TaskUtils.drop(Tasks.forResult(null));
                            }));


        });

        return view;
    }

    /**
     * Synchronizes the recycler view of Images with the state of the user's uri cache
     * @param contentResolver Content resolver used for reading media
     */
    private void syncImages(ContentResolver contentResolver){
        int nonDbImLen = (int) images.stream()
                .filter(im -> im.getId() == null)
                .count();

        int imLen = images.size();
        int lenDiff = imLen - nonDbImLen;
        int cacheLen = userManager.getUriCache().size();

        if (nonDbImLen < cacheLen) {
            int numPhotosAdded = cacheLen - nonDbImLen;
            int addedStart = cacheLen - numPhotosAdded;

            for (int i = addedStart; i < cacheLen; i++){
                Image image = FileUtils
                        .imageFromUri(userManager.getUriCache().get(i), contentResolver);
                images.add(image);
                photoAdapter.notifyItemInserted(i + lenDiff);
            }
            photoAdapter.notifyItemRangeInserted(addedStart + lenDiff, cacheLen + lenDiff - 1);
        }
    }
}