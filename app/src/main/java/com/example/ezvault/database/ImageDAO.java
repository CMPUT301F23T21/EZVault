package com.example.ezvault.database;

import com.example.ezvault.model.Image;
import com.google.android.gms.tasks.Task;

import java.util.UUID;

public class ImageDAO extends AbstractDAO<Image, String>{
    /**
     * The maximum allowed file download size in bytes (1000000 = 1mb)
     */
    private static final int MAX_DOWNLOAD = 1000000;

    /**
     * Constructs an ImageDAO
     *
     * @param firebase The firebase bundle that this DAO uses
     */
    public ImageDAO(FirebaseBundle firebase) {
        super(firebase);
    }

    /**
     * Adds a new image to the user's storage bucket.
     * @param image Tag to be saved.
     * @return Task with the ID of the new image.
     */
    @Override
    public Task<String> create(Image image) {
        byte[] contents = image.getContents();
        if (contents.length > MAX_DOWNLOAD){
            throw new RuntimeException("Image file size too big.");
        }

        String imageId = UUID.randomUUID().toString();
        String uid = firebase.getAuth().getUid();

        return firebase.getStorage()
                .getReference()
                .child(uid + "/" + imageId + ".jpg")
                .putBytes(contents)
                .continueWith(task -> imageId);
    }

    /**
     * Fetches an image from the user's storage bucket.
     * @param id String of the image to fetch
     * @return Task with the desired Image.
     */
    @Override
    public Task<Image> read(String id) {
        String uid = firebase.getAuth().getUid();
        return firebase.getStorage()
                .getReference()
                .child(uid + "/" + id + ".jpg")
                .getBytes(MAX_DOWNLOAD)
                .continueWith(imageTask -> {
                    byte[] content = imageTask.getResult();

                    // Create our Image object
                    Image image = new Image();
                    image.setId(id);
                    image.setContents(content);

                    return image;
                });
    }

    @Override
    public Task<Void> update(String id, Image image) {
        throw new UnsupportedOperationException("Image does not support update operations.");
    }

    /**
     * Deletes an Image from the database
     * @param id String of the image to fetch
     * @return Task with the desired Image.
     */
    @Override
    public Task<Void> delete(String id) {
        String uid = firebase.getAuth().getUid();
        return firebase.getStorage()
                .getReference()
                .child(uid + "/" + id + ".jpg")
                .delete();
    }
}
