package com.example.ezvault;

import android.content.ContentResolver;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * fragment class that collects the information of a new item
 */
@AndroidEntryPoint
public class AddItemFragment extends Fragment {

    private Button createButton;

    private GalleryAction galleryAction;

    @Inject
    protected UserManager userManager;

    private ArrayList<Image> images;

    private PhotoAdapter photoAdapter;

    private ContentResolver contentResolver;

    private boolean canInteract;

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
        EditText itemSerial = view.findViewById(R.id.edit_details_serial_number);
        EditText itemDate = view.findViewById(R.id.edit_details_date);

        userManager.synchronizeToAdapter(images, photoAdapter);

        RecyclerView photoRecyclerView = view.findViewById(R.id.edit_details_image_recycler);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
            Navigation.findNavController(view).navigate(R.id.action_addItemFragment_to_cameraFragment);
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

    private void toggleInteractable(){
        canInteract = !canInteract;

        createButton.setEnabled(canInteract);
        createButton.setClickable(canInteract);
        createButton.getBackground().setAlpha(canInteract ? 255 : 112);
    }
}