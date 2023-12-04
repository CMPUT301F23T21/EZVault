package com.example.ezvault.utils.camera;

import android.net.Uri;

import androidx.activity.ComponentActivity;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;

/**
 * An action that allows the user to select photos from their gallery
 */

public class GalleryAction extends CameraAction<Uri, PickVisualMediaRequest>{
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
            resolveTaskChunk.getTcSource().setResult(uri);
        }));

        resolveAllTaskChunk.setArLauncher(registry.register(getRandomKey(), owner, new ActivityResultContracts.PickMultipleVisualMedia(), uris -> {
            resolveAllTaskChunk.getTcSource().setResult(uris);
        }));
    }

    /**
     * Resolves a singular image from the user's gallery
     * @return A Task containing the selected Image
     */
    @Override
    public Task<Uri> resolve() {
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
    public Task<List<Uri>> resolveAll() {
        resolveAllTaskChunk.setTcSource(new TaskCompletionSource<>());

        // Launch the gallery
        resolveAllTaskChunk.getArLauncher().launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        return resolveAllTaskChunk.getTcSource().getTask();
    }
}
