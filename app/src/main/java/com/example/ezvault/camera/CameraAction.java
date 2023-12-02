package com.example.ezvault.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.ezvault.model.ActivityTaskChunk;
import com.example.ezvault.model.Image;
import com.example.ezvault.utils.UserManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A class that encapsulates the behaviour of actions related to the camera.
 */

public abstract class CameraAction<T,S> implements DefaultLifecycleObserver {

    /**
     * Activity used for content resolving
     */
    protected final ComponentActivity componentActivity;

    /**
     * Registry for our ActivityResults
     */
    protected final ActivityResultRegistry registry;

    /**
     * Chunk for creating singular resolve tasks
     */
    protected ActivityTaskChunk<T, S> resolveTaskChunk;

    /**
     * Chunk for creating plural resolve tasks
     */
    protected ActivityTaskChunk<List<T>, S> resolveAllTaskChunk;

    /**
     * Constructor for CameraAction
     * @param componentActivity Activity used for content resolving
     */
    public CameraAction (@NonNull ComponentActivity componentActivity){
        this.componentActivity = componentActivity;
        this.registry = componentActivity.getActivityResultRegistry();

        resolveTaskChunk = new ActivityTaskChunk<T, S>();
        resolveAllTaskChunk = new ActivityTaskChunk<List<T>, S>();
    }

    @Override
    final public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
        register(owner);
    }

    /**
     * Used to register ActivityResults
     * @param owner LifecycleOwner of class to observe
     */
    abstract void register(LifecycleOwner owner);

    /**
     * Used to retrieve a single user-selected image from the Camera Action
     * @return A Task containing the Image
     */
    public abstract Task<T> resolve();

    /**
     * Used to retrieve multiple user-selected images from the Camera Action
     * @return A Task containing the List of Images
     */
    public abstract Task<List<T>> resolveAll();

    /**
     * Generates a random key for result registries
     * @return a tagged and timestamped String
     */
    protected final String getRandomKey(){
        return "EZ_" + UUID.randomUUID().toString();
    }
}
