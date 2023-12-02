package com.example.ezvault.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ezvault.R;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.UserManager;
import com.example.ezvault.view.adapter.SwipeToDeleteTags;
import com.example.ezvault.view.adapter.TagRecyclerAdapter;
import com.example.ezvault.viewmodel.TagsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays all of the current tags and is responsible for adding new ones
 */
@AndroidEntryPoint
public class TagsFragment extends Fragment{
    @Inject
    public UserManager userManager;


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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);


        //Let's you swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteTags(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);




        TextView no_tags = view.findViewById(R.id.empty_tags);
        viewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            if (tags != null && !tags.isEmpty()) {
                no_tags.setVisibility(View.GONE);
            }
            else {
                no_tags.setVisibility(View.VISIBLE);
            }

        });

        FloatingActionButton sorting_tag_button = view.findViewById(R.id.sort_tag_button);

        sorting_tag_button.setOnClickListener(v->{
            //Gets the list of tags
            List<Tag> tags = userManager.getUser().getItemList().getTags();

            boolean isSorted = viewModel.toggleSortingOrder();
            List<Tag> sortedTags;

            if(isSorted){
                sortedTags = tags.stream()
                        .sorted(Comparator.comparing(Tag -> Tag.getUid().toLowerCase()))
                        .collect(Collectors.toList());
            }
            else{
                sortedTags = tags.stream()
                        .sorted((tag1, tag2) -> tag2.getUid().toLowerCase().compareTo(tag1.getUid().toLowerCase()))
                        .collect(Collectors.toList());
            }
            //Sorts the list of tags
            //Sets the placements for the tag list
            viewModel.setTags(sortedTags);
        });

    }
}