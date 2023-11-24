package com.example.ezvault;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezvault.model.Tag;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class TagsRecyclerAdapter extends RecyclerView.Adapter<TagsRecyclerAdapter.MyViewHolder>{

    private LayoutInflater inflater;
    private Context context;

    //private String[] array = new String[1000];
    private ArrayList<Tag> array = new ArrayList<>();
    private AdapterView.OnItemClickListener itemClickListener;

    public TagsRecyclerAdapter(Context context, ArrayList<Tag> array) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.array = array;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tag_adapter_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tag currentTag = array.get(position);
        holder.tagName.setText(currentTag.getIdentifier());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                Toast.makeText(v.getContext(), "Clicked on position " + position, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tagName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.Tag_name);
        }
    }
}

