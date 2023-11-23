package com.example.ezvault.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.renderscript.ScriptGroup;

import androidx.camera.core.impl.utils.Exif;

import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    @SuppressLint("RestrictedApi")
    public static Image imageFromUri(Uri contentUri, ContentResolver contentResolver){
        InputStream imageInput = null;
        InputStream exifInput = null;

        try {
            imageInput = contentResolver.openInputStream(contentUri);
            exifInput = contentResolver.openInputStream(contentUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Exif imgExif = null;
        try {
            imgExif = Exif.createFromInputStream(exifInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap imageBmp = BitmapFactory.decodeStream(imageInput);
        if (imgExif.getRotation() != 0){
            Matrix mat = new Matrix();
            mat.postRotate(90);

            imageBmp = Bitmap.createBitmap(imageBmp, 0, 0, imageBmp.getWidth(), imageBmp.getHeight(), mat, true );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageContent = baos.toByteArray();
        Image image = new Image();
        image.setContents(imageContent);

        return image;
    }
}
