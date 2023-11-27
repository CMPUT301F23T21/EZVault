package com.example.ezvault.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.R;
import com.example.ezvault.model.Tag;

import java.util.List;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                Toast.makeText(v.getContext(), "Clicked on position " + position, Toast.LENGTH_SHORT).show();
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

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagIdentifierView = itemView.findViewById(R.id.Tag_name);
        }

        public void bind(Tag tag) {
            tagIdentifierView.setText(tag.getIdentifier());
        }
    }
}
