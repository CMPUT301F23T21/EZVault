package com.example.ezvault.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {
    public static Image imageFromUri(Uri contentUri, ContentResolver contentResolver){
        InputStream imageInput = null;

        try {
            imageInput = contentResolver.openInputStream(contentUri);
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

        return image;
    }
}
