package com.example.ezvault.utils.camera;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.ezvault.utils.ActivityTaskChunk;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.UUID;

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


    public CameraAction (@NonNull ComponentActivity componentActivity){
        this.componentActivity = componentActivity;
        this.registry = componentActivity.getActivityResultRegistry();

        resolveTaskChunk = new ActivityTaskChunk<>();
        resolveAllTaskChunk = new ActivityTaskChunk<>();
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
