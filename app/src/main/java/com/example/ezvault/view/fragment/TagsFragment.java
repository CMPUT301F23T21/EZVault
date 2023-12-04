package com.example.ezvault.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezvault.R;
import com.example.ezvault.view.adapter.TagRecyclerAdapter;
import com.example.ezvault.viewmodel.TagsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays all of the current tags and is responsible for adding new ones
 */
@AndroidEntryPoint
public class TagsFragment extends Fragment{
    public TagsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tags, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TagsViewModel viewModel = new ViewModelProvider(requireActivity()).get(TagsViewModel.class);

        FloatingActionButton addTag = view.findViewById(R.id.button_add_tag);
        addTag.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.tagDialogue));

        RecyclerView recyclerView = view.findViewById(R.id.tag_recycler);
        TagRecyclerAdapter adapter = new TagRecyclerAdapter();
        viewModel.getTags().observe(getViewLifecycleOwner(), adapter::setTagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        TextView no_tags = view.findViewById(R.id.empty_tags);
        viewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            if (tags != null && !tags.isEmpty()){
                no_tags.setVisibility(View.GONE);
            }
            else{
                no_tags.setVisibility(View.VISIBLE);
            }
        });
    }
}