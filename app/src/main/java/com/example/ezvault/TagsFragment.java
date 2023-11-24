package com.example.ezvault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;


import com.example.ezvault.database.FirebaseBundle;
import com.example.ezvault.database.TagDAO;
import com.example.ezvault.model.Tag;
import com.example.ezvault.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.example.ezvault.TagsFragmentArgs;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays all of the current tags and is responsible for adding new ones
 */

public class TagsFragment extends Fragment {

    View view;
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

    protected UserManager userManager;




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
        //datalist = new ArrayList<>();
        //arrayAdapter = new com.example.ezvault.TagsRecyclerAdapter(getContext(), datalist);
        //recyclerView.setAdapter(arrayAdapter);

        if(datalist == null) {
            datalist = new ArrayList<>();
            arrayAdapter = new com.example.ezvault.TagsRecyclerAdapter(getContext(), datalist);
            recyclerView.setAdapter(arrayAdapter);
        }

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (datalist.isEmpty()) {
            fetchTagDataFromFirebase();
        }

        getParentFragmentManager().setFragmentResultListener("tagResult", this, ((requestKey, result) -> {
            if (requestKey.equals("tagResult")){
                Tag tag = (Tag) result.getSerializable("tagData");
                if (tag != null){
                datalist.add(tag);
                arrayAdapter.notifyDataSetChanged();

                updateNoItemVisibility();
                }
            }
        }));

        fetchTagDataFromFirebase();
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
    private void fetchTagDataFromFirebase(){
        FirebaseBundle firebaseBundle = new FirebaseBundle();
        TagDAO tagDAO = new TagDAO(firebaseBundle);

        tagDAO.getAllTags().addOnSuccessListener(tags -> {
            datalist.clear();
            datalist.addAll(tags);
            arrayAdapter.notifyDataSetChanged();
            updateNoItemVisibility();
        }).addOnFailureListener(e -> {
            Log.e("TagsFragment", "Failed to fetch data: " + e.getMessage());
        });

    }

}