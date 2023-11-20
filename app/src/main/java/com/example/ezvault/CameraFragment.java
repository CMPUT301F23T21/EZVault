package com.example.ezvault;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

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

    LifecycleCameraController cameraController;

    private ExecutorService cameraExecutor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cameraController = new LifecycleCameraController(requireContext());
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private File createTempFile(Activity activity){
        try {
            String fileName = "EZVAULT_" + System.currentTimeMillis();
            return File.createTempFile(fileName, ".jpg", activity.getExternalFilesDir("app_photos"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void bindUseCases(@NonNull ProcessCameraProvider cameraProvider, View view) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraProviderFuture.addListener(() -> {
            try {
                bindUseCases(cameraProviderFuture.get(), view);
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

    private void takePicture(View v) {
        // Specify where we want to save the file
        ImageCapture.OutputFileOptions outputOpts =
                new ImageCapture.OutputFileOptions.Builder(createTempFile(requireActivity())).build();

        // Request for the camera to take a photo
        imageCapture.takePicture(outputOpts, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        userManager.addUri(outputFileResults.getSavedUri());
                        Log.v("EEp", outputFileResults.getSavedUri().toString());
                    }

                    @Override
                    public void onError(ImageCaptureException error) {

                    }
                }
        );
    }
}
