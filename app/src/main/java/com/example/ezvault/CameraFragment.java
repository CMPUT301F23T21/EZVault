package com.example.ezvault;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.ezvault.model.Image;
import com.example.ezvault.utils.FileUtils;
import com.example.ezvault.utils.UserManager;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CameraFragment extends Fragment {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private PreviewView camPreview;

    @Inject
    protected UserManager userManager;
    private ExecutorService cameraExecutor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        camPreview = view.findViewById(R.id.viewFinder);

        Button captureImageButton = view.findViewById(R.id.image_capture_button);
        captureImageButton.setOnClickListener(this::takePicture);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


        cameraProviderFuture.addListener(() -> {
            try {
                bindUseCases(cameraProviderFuture.get());
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(requireContext()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
    }

    /**
     * Initializes the camera and binds it to various use cases
     * @param cameraProvider Camera to access
     */
    private void bindUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(camPreview.getSurfaceProvider());

        ViewPort viewPort = camPreview.getViewPort();

        if (viewPort != null) {
            imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .build();

            UseCaseGroup useCases = new UseCaseGroup.Builder()
                    .addUseCase(preview)
                    .addUseCase(imageCapture)
                    .setViewPort(viewPort)
                    .build();

            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, useCases);
        }

    }

    /**
     * Creates a temp file to store an image in
     * @param activity Activity to read an external files path from
     * @return
     */
    private File createTempFile(Activity activity){
        try {
            String fileName = "EZVAULT_" + System.currentTimeMillis();

            File tempImgFile = File.createTempFile(fileName, ".jpg", activity.getExternalFilesDir("app_photos"));
            tempImgFile.deleteOnExit();

            return tempImgFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes a photo and adds the uri to the user's URI cache
     * @param view
     */
    private void takePicture(View view) {
        // Specify where we want to save the file
        ImageCapture.OutputFileOptions outputOpts =
                new ImageCapture.OutputFileOptions.Builder(createTempFile(requireActivity())).build();

        // Request for the camera to take a photo
        imageCapture.takePicture(outputOpts, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                       Image newImage =  FileUtils.imageFromUri(outputFileResults.getSavedUri(),
                               requireContext().getContentResolver());

                       userManager.addLocalImage(newImage);
                    }

                    @Override
                    public void onError(ImageCaptureException error) {

                    }
                }

        );

        view.getRootView().setForeground(new ColorDrawable(Color.WHITE));
        view.getRootView().animate()
                .setDuration(350)
                .alpha(0.80f)
                .withEndAction(() -> {
                    view.getRootView().setAlpha(1.0f);
                    view.getRootView().setForeground(null);
                }).start();

    }
}
