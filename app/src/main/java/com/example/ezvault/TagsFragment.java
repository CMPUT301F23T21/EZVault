package com.example.ezvault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezvault.model.Tag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.example.ezvault.TagsFragmentArgs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsFragment extends Fragment {

    FloatingActionButton addTag;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Tag tag;
    private RecyclerView recyclerView;
    //private String[] datalist;
    private ArrayList<Tag> datalist;
    private com.example.ezvault.TagsRecyclerAdapter arrayAdapter;

    private TagsFragmentArgs args;



    public TagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsFragment newInstance(String param1, String param2) {
        TagsFragment fragment = new TagsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tags, container, false);


        addTag = view.findViewById(R.id.button_add_tag);
        recyclerView = view.findViewById(R.id.recyclerView_tags);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        datalist = new ArrayList<>();
        arrayAdapter = new com.example.ezvault.TagsRecyclerAdapter(getContext(), datalist);
        recyclerView.setAdapter(arrayAdapter);

        getParentFragmentManager().setFragmentResultListener("tagInput", getViewLifecycleOwner(), (requestKey, result) -> {

            if (requestKey.equals("tagInput")){
                Tag newTag = (Tag) result.getSerializable("tag");
                if (newTag != null){
                    datalist.add(newTag);
                    arrayAdapter.notifyDataSetChanged();
                }

        }});

        addTag.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.tagDialogue);
        });
/*
        TextView noItem = view.findViewById(R.id.empty_tags);
        if (arrayAdapter.getItemCount() != 0) {
            noItem.setVisibility(View.GONE);
        }
        else{
            noItem.setVisibility(View.VISIBLE);
        }
*/
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("tagResult", this, ((requestKey, result) -> {
            if (requestKey.equals("tagResult")){
                Tag tag = (Tag) result.getSerializable("tagData");

                datalist.add(tag);
                arrayAdapter.notifyDataSetChanged();

                updateNoItemVisibility();
            }
        }));
    }
    private void updateNoItemVisibility(){
        TextView noItem = requireView().findViewById(R.id.empty_tags);
        if (datalist.isEmpty()){
            noItem.setVisibility(View.VISIBLE);
        }
        else{
            noItem.setVisibility(View.GONE);
        }
    }

}