package com.example.ezvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import com.bumptech.glide.Glide;
import com.example.ezvault.model.Image;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<com.example.ezvault.model.Item> itemList;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;

    // Interface for click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Constructor
    public ItemAdapter(Context context, List<com.example.ezvault.model.Item> itemList, ItemClickListener listener) {
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
        com.example.ezvault.model.Item currentItem = itemList.get(position);

        // Construct the item name from make and model
        String itemName = currentItem.getMake() + " " + currentItem.getModel();
        holder.itemName.setText(itemName);

        // Set the items count
        holder.itemCount.setText(String.valueOf(currentItem.getCount()) + " Units");

        // Set the items cost
        holder.itemAmount.setText( "$" + String.valueOf(currentItem.getValue()));

        // Get all the items images
        List<Image> itemImages = currentItem.getImages();

        // Set the thumbnail to the first image, if there are any
        if (itemImages.size() > 0) {
            byte[] imageContent = currentItem.getImages().get(0).getContents();
            Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

            holder.itemImage.setImageBitmap(imageBmp);
        }

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

    // Allows external callers to set a new dataset
    public void setItems(List<com.example.ezvault.model.Item> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }
}
