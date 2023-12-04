package com.example.ezvault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.model.Image;
import com.example.ezvault.model.Item;
import com.example.ezvault.model.utils.ItemListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Recyclerview adapter for itemsFragment
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ItemListView itemListView;
    private final LayoutInflater inflater;
    private final ItemClickListener itemClickListener;
    public boolean deleteMode;

    // Interface for click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Constructor
    public ItemAdapter(@NonNull Context context, @NonNull ItemListView itemListView, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.itemListView = itemListView;
        this.itemClickListener = listener;
        this.deleteMode = false;
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

        CheckBox checkBox = holder.itemView.findViewById(R.id.item_checkbox);

        // Construct the item name from make and model
        String itemName = currentItem.getMake() + " " + currentItem.getModel();
        holder.itemName.setText(itemName);

        // Set the items count
        holder.itemCount.setText(currentItem.getCount() + " Units");

        // Set the items cost
        holder.itemAmount.setText(String.format("$%.2f", currentItem.getValue()));

        // Get all the items images
        List<Image> itemImages = currentItem.getImages();

        // Set the thumbnail to the first image, if there are any
        holder.itemImage.setImageDrawable(null);
        if (itemImages.size() > 0) {
            byte[] imageContent = currentItem.getImages().get(0).getContents();
            Bitmap imageBmp = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

            holder.itemImage.setImageBitmap(imageBmp);
        }

        // show or hide checkbox if in delete mode
        if (deleteMode) {
            holder.itemView.findViewById(R.id.view_details_image).setVisibility(View.GONE);
            checkBox.setVisibility(View.VISIBLE);
        }
        else {
            holder.itemView.findViewById(R.id.view_details_image).setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }

        // Set the click listener for the item if not in delete mode
        if (!deleteMode) {
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

        // item is checked if checkbox is checked
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get the current clicked position
                int adapterPosition = holder.getAdapterPosition();

                itemListView.get(adapterPosition).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemListView.size();
    }

    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemImage;
        TextView itemCount;
        TextView itemAmount;

        CheckBox checkBox;
        ImageView imageView;
        ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemImage = itemView.findViewById(R.id.item_image);
            itemCount = itemView.findViewById(R.id.quantity_text);
            itemAmount = itemView.findViewById(R.id.cost_text);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            imageView = itemView.findViewById(R.id.view_details_image);
        }
    }

    /**
     * Clears the isSelected field of each item
     */
    public void clearSelected(){
        itemListView.forEach(item -> {
            if (item.isSelected()){
                item.setSelected(false);
            }
        });
        notifyDataSetChanged();
    }

    /**
     * Gets a list of the selected items
     * @return
     *      list of selected items
     */
    public List<Item> getSelectedItems(){
        List<Item> selected = new ArrayList<>();

        itemListView.forEach(item -> {
            if (item.isSelected()){
                selected.add(item);
            }
        });

        return selected;
    }

    /**
     * Gets a list of the unselected items
     * @return
     *      list of unselected items
     */
    public List<Item> getUnselectedItems(){
        List<Item> unselected = new ArrayList<>();

        itemListView.forEach(item -> {
            if (!item.isSelected()){
                unselected.add(item);
            }
        });

        return unselected;

    }

    /**
     * Gets the number of selected items
     * @return
     *      integer count
     */
    public int getSelectedCount(){
        return getSelectedItems().size();
    }

    /**
     * Allows external callers to set a new dataset
     * @param items
     *      an iterable object
     */
    public void setItems(ItemListView items) {
        this.itemListView = items;
        notifyDataSetChanged();
    }

}
