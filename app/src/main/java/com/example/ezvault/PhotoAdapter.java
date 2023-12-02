package com.example.ezvault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.model.Image;
import com.example.ezvault.utils.UserManager;

import java.util.List;

/**
 * Recyclerview adapter for displaying photos
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ImageHolder> {


    public static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton deletePhotoButton;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageview_photo);
            deletePhotoButton = itemView.findViewById(R.id.add_item_remove_photo);
        }
    }

    private final Context context;
    private List<Image> imageList;

    private UserManager userManager;

    public PhotoAdapter(Context context, List<Image> imageList, UserManager userManager) {
        this.context = context;
        this.imageList = imageList;
        this.userManager = userManager;
    }

    @NonNull
    @Override
    public PhotoAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate layout based on which type of image is to be displayed
        view = inflater.inflate(R.layout.photo_layout, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ImageHolder holder, int position) {
        Image image = imageList.get(position);

        // Set the thumbnail to the first image, if there are any
        byte[] imageContent = image.getContents();
        Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

        holder.image.setImageBitmap(imageBmp);

        holder.deletePhotoButton.setOnClickListener(v -> {
            int updatedPosition = holder.getAdapterPosition();

            Image removedImage = imageList.remove(updatedPosition);
            if (removedImage.getId() == null) {
                userManager.getUriCache().remove(updatedPosition);
            }
            notifyItemRemoved(updatedPosition);
            notifyItemRangeChanged(updatedPosition, imageList.size() - updatedPosition);
        });
    }

    // returns what type of image layout to be displayed
    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
