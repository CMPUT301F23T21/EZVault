package com.example.ezvault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class fragment_tagger_item extends Fragment {
    FloatingActionButton taggerbtn;

    private FragmentTaggerItemViewModel mViewModel;

    public static fragment_tagger_item newInstance() {
        return new fragment_tagger_item();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_tagger_item, container, false);

        taggerbtn = view.findViewById(R.id.add_item_tagger_button);
        /*
        Bundle bundle = getArguments();
        if(bundle != null){
            String tagName = bundle.getString("Tag");

            if (getActivity() != null && getActivity() instanceof AppCompatActivity){
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tagName);
            }

        }


        taggerbtn.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),"Working", Toast.LENGTH_SHORT).show();
        });
*/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentTaggerItemViewModel.class);
        // TODO: Use the ViewModel
    }

}