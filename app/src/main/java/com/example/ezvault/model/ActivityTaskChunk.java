package com.example.ezvault.model;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;

import com.google.android.gms.tasks.TaskCompletionSource;

/**
 * Storage class for the creation of ActivityResults
 * @param <T> Class type of result from the completion of an ActivityResult
 */
public class ActivityTaskChunk<T> {

    /**
     * Keeps track of a Tasks completion status
     */
    private TaskCompletionSource<T> tcSource;

    /**
     * Launch for a Visual Media activity result
     */
    private ActivityResultLauncher<PickVisualMediaRequest> arLauncher;

    public ActivityTaskChunk(TaskCompletionSource<T> tcSource, ActivityResultLauncher<PickVisualMediaRequest> arLauncher) {
        this.tcSource = tcSource;
        this.arLauncher = arLauncher;
    }

    public ActivityTaskChunk(){}

    public TaskCompletionSource<T> getTcSource() {
        return tcSource;
    }

    public void setTcSource(TaskCompletionSource<T> tcSource) {
        this.tcSource = tcSource;
    }

    public ActivityResultLauncher<PickVisualMediaRequest> getArLauncher() {
        return arLauncher;
    }

    public void setArLauncher(ActivityResultLauncher<PickVisualMediaRequest> arLauncher) {
        this.arLauncher = arLauncher;
    }

}
