package com.example.ezvault.view.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.R;
import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.RawUserDAO;
import com.example.ezvault.database.TagDAO;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;



public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.TagViewHolder> {
    
    private List<Tag> tagList = null;

    @SuppressLint("NotifyDataSetChanged")
    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_adapter_layout, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.bind(tag);
/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                Tag clickedTag = tagList.get(adapterPosition);
                Bundle bundle = new Bundle();
                bundle.putString("tag_name", clickedTag.getIdentifier());
                Toast.makeText(v.getContext(), "Clicked on position " + position, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_tagsFragment_to_fragment_tagger_item, bundle);
                

                
            }
        });
   */
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                Toast.makeText(v.getContext(), "Clicked on position " + position, Toast.LENGTH_SHORT).show();
                return false;


            }
        });
        holder.tagDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                //Tag clickedTag = tagList.get(adapterPosition);
                //Bundle bundle = new Bundle();
                //bundle.putString("tag_name", clickedTag.getIdentifier());
                //Toast.makeText(v.getContext(), "Clicked on position " + position, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_tagsFragment_to_fragment_tagger_item);


            }
        });



    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        private final TextView tagIdentifierView;
        private final ImageButton tagDetailButton;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagIdentifierView = itemView.findViewById(R.id.Tag_name);
            tagDetailButton = itemView.findViewById(R.id.tag_detail_button);
        }

        public void bind(Tag tag) {
            tagIdentifierView.setText(tag.getContents());
        }
    }

    public void deleteItem(int position){
        FirebaseBundle fb = new FirebaseBundle();
        TagDAO tagDAO = new TagDAO(fb);

        Tag TagPosition = tagList.get(position);


        tagList.remove(position);
        notifyItemRemoved(position);
        tagDAO.delete(TagPosition.getUid());



    }
    public void moveItem(int fromPosition, int toPosition){
        Collections.swap(tagList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);


    }
    public Tag getTagAtPosition(int position){
        if(tagList != null && position >= 0 && position < tagList.size()){
            return tagList.get(position);
        }
        return null;
    }

    public void restoreItem(Tag tag, int position){
        tagList.add(position, tag);
        notifyItemInserted(position);
    }

}
