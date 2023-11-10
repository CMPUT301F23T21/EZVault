package com.example.ezvault.camera;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * An action that allows the user to select photos from their gallery
 */
public class GalleryAction extends CameraAction{
    public GalleryAction(ComponentActivity componentActivity) {
        super(componentActivity);
    }

    /**
     * Resolves a singular image from the user's gallery
     * @return A Task containing the selected Image
     */
    @Override
    public Task<Image> resolve() {
        TaskCompletionSource<Image> tcs = new TaskCompletionSource<>();
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = componentActivity.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            imageFromUri(uri).continueWith(imageTask -> {
                tcs.setResult(imageTask.getResult());
                return null;
            });

        });

        // Launch the gallery
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        return tcs.getTask();
    }

    /**
     * Resolves multiple images from the user's gallery
     * @return A Task containing the selected Images
     */
    @Override
    public Task<List<Image>> resolveAll() {
        TaskCompletionSource<List<Image>> tcs = new TaskCompletionSource<>();
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = componentActivity.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(), uris -> {
            List<Task<Image>> imageTasks = new ArrayList<>();

            uris.forEach(uri -> {
                imageTasks.add(imageFromUri(uri));
            });

            // Wait for all images to be processed
            Tasks.whenAllSuccess(imageTasks).continueWith(allImages -> {
                tcs.setResult((List<Image>)(List)allImages.getResult());
                return null;
            });

        });

        // Launch the gallery
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        return tcs.getTask();
    }
}
