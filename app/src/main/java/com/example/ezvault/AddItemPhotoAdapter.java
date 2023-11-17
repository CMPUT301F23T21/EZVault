package com.example.ezvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddItemPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // class for each type of different view layout
    public static class PlaceHolder extends RecyclerView.ViewHolder {
        public PlaceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
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
    private ArrayList<MockClass> imageList;
    public AddItemPhotoAdapter(Context context, ArrayList<MockClass> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate layout based on which type of image is to be displayed
        switch (viewType) {
            case MockClass.TYPE_PLACEHOLDER:
                view = inflater.inflate(R.layout.placeholder_layout, parent, false);
                return new PlaceHolder(view);
            case MockClass.TYPE_PICTURE:
                view = inflater.inflate(R.layout.photo_layout, parent, false);
                return new ImageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MockClass mockClass = imageList.get(position);
        if (mockClass != null) {
            switch (mockClass.getType()) {
                case MockClass.TYPE_PLACEHOLDER:
                    break;
                case MockClass.TYPE_PICTURE:
                    // set the image view of the image
                    ((ImageHolder) holder).image.setImageResource(R.drawable.logo);

                    // set the click listener for the button
                    ((ImageHolder) holder).deletePhotoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }
    }

    // returns what type of image layout to be displayed
    @Override
    public int getItemViewType(int position) {
        switch (imageList.get(position).getType()) {
            case 0:
                return MockClass.TYPE_PLACEHOLDER;
            case 1:
                return MockClass.TYPE_PICTURE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
