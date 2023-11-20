package com.example.ezvault.camera;

import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * An action that allows the user to select photos from their gallery
 */
public class GalleryAction extends CameraAction<Image, PickVisualMediaRequest>{
    public GalleryAction(@NonNull ComponentActivity componentActivity) {
        super(componentActivity);
    }


    /**
     * Register the classes gallery activity launchers
     * @param owner LifecycleOwner of class to observe
     */
    @Override
    void register(LifecycleOwner owner) {
        resolveTaskChunk.setArLauncher(registry.register(getRandomKey(), owner, new ActivityResultContracts.PickVisualMedia(), uri -> {
            imageFromUri(uri).continueWith(imageTask -> {
                resolveTaskChunk.getTcSource().setResult(imageTask.getResult());
                return null;
            });

        }));

        resolveAllTaskChunk.setArLauncher(registry.register(getRandomKey(), owner, new ActivityResultContracts.PickMultipleVisualMedia(), uris -> {
            List<Task<Image>> imageTasks = new ArrayList<>();

            uris.forEach(uri -> {
                imageTasks.add(imageFromUri(uri));
            });

            // Wait for all images to be processed
            Tasks.whenAllSuccess(imageTasks).continueWith(allImages -> {
                Log.v("EEEDED", "ededede");
                resolveAllTaskChunk.getTcSource().setResult((List<Image>)(List)allImages.getResult());
                return null;
            });

        }));
    }

    /**
     * Resolves a singular image from the user's gallery
     * @return A Task containing the selected Image
     */
    @Override
    public Task<Image> resolve() {
        resolveTaskChunk.setTcSource(new TaskCompletionSource<>());

        // Launch the gallery
        resolveTaskChunk.getArLauncher().launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        return resolveTaskChunk.getTcSource().getTask();
    }

    /**
     * Resolves multiple images from the user's gallery
     * @return A Task containing the selected Images
     */
    @Override
    public Task<List<Image>> resolveAll() {
        resolveAllTaskChunk.setTcSource(new TaskCompletionSource<>());

        // Launch the gallery
        resolveAllTaskChunk.getArLauncher().launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        return resolveAllTaskChunk.getTcSource().getTask();
    }
}
