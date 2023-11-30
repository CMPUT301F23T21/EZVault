package com.example.ezvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import com.example.ezvault.model.Image;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.utils.ItemListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ItemListView itemListView;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;

    public boolean editMode;

    // Interface for click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongClick(View view, int position);
    }

    // Constructor
    public ItemAdapter(@NonNull Context context, @NonNull ItemListView itemListView, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.itemListView = itemListView;
        this.itemClickListener = listener;
        this.editMode = false;
    }



    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        Item currentItem = itemListView.get(position);

        // Construct the item name from make and model
        String itemName = currentItem.getMake() + " " + currentItem.getModel();
        holder.itemName.setText(itemName);

        // Set the items count
        holder.itemCount.setText(String.valueOf(currentItem.getCount()) + " Units");

        // Set the items cost
        holder.itemAmount.setText(String.format("$%.2f", currentItem.getValue()));

        // Get all the items images
        List<Image> itemImages = currentItem.getImages();

        // Set the thumbnail to the first image, if there are any
        if (itemImages.size() > 0) {
            byte[] imageContent = currentItem.getImages().get(0).getContents();
            Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

            holder.itemImage.setImageBitmap(imageBmp);
        }

        holder.itemView.setBackgroundColor(currentItem.isSelected() ? 0xff0000ff : 0xffd7c3b8);

        // Set the click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current clicked position
                int adapterPosition = holder.getAdapterPosition();
                if (!editMode) {
                    // Check if a click listener is set and the position is valid
                    if (itemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(v, adapterPosition);
                    }
                } else{
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        Item selectedItem = itemListView.get(adapterPosition);
                        selectedItem.setSelected(!selectedItem.isSelected());

                        holder.itemView.setBackgroundColor(selectedItem.isSelected() ? 0xff0000ff : 0xffd7c3b8);
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                if (itemClickListener != null && adapterPosition != RecyclerView.NO_POSITION){
                    itemClickListener.onLongClick(v, adapterPosition);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemListView.size();
    }

    // ViewHolder class
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemImage;
        TextView itemCount;
        TextView itemAmount;


        ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemImage = itemView.findViewById(R.id.item_image);
            itemCount = itemView.findViewById(R.id.quantity_text);
            itemAmount = itemView.findViewById(R.id.cost_text);
        }
    }

    public void clearSelected(){
        itemListView.forEach(item -> {
            if (item.isSelected()){
                item.setSelected(false);
            }
        });
    }

    public List<Item> getSelectedItems(){
        List<Item> selected = new ArrayList<>();

        itemListView.forEach(item -> {
            if (item.isSelected()){
                selected.add(item);
            }
        });

        return selected;

    }

    public List<Item> getUnselectedItems(){
        List<Item> unselected = new ArrayList<>();

        itemListView.forEach(item -> {
            if (!item.isSelected()){
                unselected.add(item);
            }
        });

        return unselected;

    }

    // Allows external callers to set a new dataset
    public void setItems(ItemListView items) {
        this.itemListView = items;
        notifyDataSetChanged();
    }
}
