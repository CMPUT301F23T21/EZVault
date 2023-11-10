package com.example.ezvault;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;

    // Interface for click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Constructor
    public ItemAdapter(Context context, List<Item> itemList, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        Item currentItem = itemList.get(position);

        // Construct the item name from make and model
        String itemName = currentItem.getMake() + " " + currentItem.getModel();
        holder.itemName.setText(itemName);

        // Use Glide to load the image. Assuming there's a method getItemImageUrl() in Item class.
//        Glide.with(holder.itemView.getContext())
//                .load(currentItem.getItemImageUrl())  //?
//                .into(holder.itemImage);

        // Set the click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current clicked position
                int adapterPosition = holder.getAdapterPosition();
                // Check if a click listener is set and the position is valid
                if (itemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(v, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemImage;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }

    // Allows external callers to set a new dataset
    public void setItems(List<Item> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }
}
