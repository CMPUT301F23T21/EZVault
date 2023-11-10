package com.example.ezvault.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.activity.ComponentActivity;
import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * A class that encapsulates the behaviour of actions related to the camera.
 */
public abstract class CameraAction {

    /**
     * Activity used for content resolving
     */
    protected final ComponentActivity componentActivity;

    public CameraAction (ComponentActivity componentActivity){
        this.componentActivity = componentActivity;
    }

    /**
     * Creates an image from a local file URI
     * @param contentUri Uri to load image data from
     * @return A Task containing the Image
     */
    protected final Task<Image> imageFromUri(Uri contentUri){
        InputStream imageInput = null;

        try {
            imageInput = componentActivity.getContentResolver().openInputStream(contentUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Compress the image while retaining the most detail we can
        Bitmap imageBmp = BitmapFactory.decodeStream(imageInput);
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageContent = baos.toByteArray();
        Image image = new Image();
        image.setContents(imageContent);

        return Tasks.forResult(image);
    }

    /**
     * Used to retrieve a single user-selected image from the Camera Action
     * @return A Task containing the Image
     */
    public abstract Task<Image> resolve();

    /**
     * Used to retrieve multiple user-selected images from the Camera Action
     * @return A Task containing the List of Images
     */
    public abstract Task<List<Image>> resolveAll();
}
