package com.example.ezvault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezvault.model.Item;
import com.example.ezvault.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Displays items in a list as well as total value and total quantity
 */
@AndroidEntryPoint
public class ItemsFragment extends Fragment {

    private FloatingActionButton floatbtn;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Inject
    public UserManager userManager;

    // Item adapter for the recycler view
    private ItemAdapter mItemAdapter;
    private RecyclerView mRecyclerView;

    public ItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment items_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemsFragment newInstance(String param1, String param2) {
        ItemsFragment fragment = new ItemsFragment();
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
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        TextView filter = view.findViewById(R.id.text_filterSort);
        filter.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_filterFragment);
        });


        floatbtn = view.findViewById(R.id.button_add_item);

        floatbtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.itemsFragment_to_addItemFragment);
        });

        mItemAdapter = new ItemAdapter(view.getContext(), new ArrayList<Item>(), new ItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        // Setup recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setAdapter(mItemAdapter);

        //mItemAdapter.setItems(userManager.getUser().getItemList().getItems());

        // hide the empty items text if items exist
        TextView no_item_warning = view.findViewById(R.id.empty_items);
        if (mItemAdapter.getItemCount() != 0) {
            no_item_warning.setVisibility(View.GONE);
        }
        else {
            no_item_warning.setVisibility(View.VISIBLE);
        }

        // set the total quantity and total cost text
        TextView numItemsView = view.findViewById(R.id.text_number_of_items);
        numItemsView.setText(String.valueOf(mItemAdapter.getItemCount()));
        TextView totalItemValueView = view.findViewById(R.id.text_total_value);
        //double totalItemValue = userManager.getUser().getItemList().getTotalValue();
        //totalItemValueView.setText(String.valueOf(totalItemValue));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}