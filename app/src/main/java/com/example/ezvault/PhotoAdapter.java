package com.example.ezvault;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.model.Image;
import com.example.ezvault.utils.FileUtils;
import com.example.ezvault.utils.UserManager;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ImageHolder> {

    // class for each type of different view layout
    public static class PlaceHolder extends RecyclerView.ViewHolder {
        public PlaceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton deletePhotoButton;

        /**
         * Constructor for ImageHolder
         * @param itemView the view to be used
         */
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageview_photo);
            deletePhotoButton = itemView.findViewById(R.id.add_item_remove_photo);
        }
    }

    private final Context context;
    private List<Image> imageList;

    public PhotoAdapter(Context context, List<Image> imageList) {
        this.context = context;
        this.imageList = imageList;
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

    /**
     * Binds the data to the view
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ImageHolder holder, int position) {
        Image image = imageList.get(position);

        // Set the thumbnail to the first image, if there are any
        byte[] imageContent = image.getContents();
        Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

        holder.image.setImageBitmap(imageBmp);

        holder.deletePhotoButton.setOnClickListener(v -> {
            int updatedPosition = holder.getAdapterPosition();

            if (updatedPosition == RecyclerView.NO_POSITION){
                return;
            }

            imageList.remove(updatedPosition);

            notifyItemRemoved(updatedPosition);
            notifyItemRangeChanged(updatedPosition, imageList.size() - updatedPosition);
        });
    }

    // returns what type of image layout to be displayed

    /**
     * Returns the number of images in the ImageHolder
     * @return
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
