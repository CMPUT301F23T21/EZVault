package com.example.ezvault.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.R;
import com.example.ezvault.model.Image;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ImageView;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Image> imageList;


    public ViewPagerAdapter(Context context, ArrayList<Image> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.viewpager_photo_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        // bind the image to the layout
        Image image = imageList.get(position);
        byte[] imageContent = image.getContents();
        Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);
        holder.imageView.setImageBitmap(imageBmp);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setImages(ArrayList<Image> newImages){
        imageList = newImages;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.viewpager_photo);
        }
    }
}
