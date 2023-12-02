package com.example.ezvault.view.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezvault.model.Tag;
import com.google.android.material.snackbar.Snackbar;

public class SwipeToDeleteTags extends ItemTouchHelper.Callback {

    private final TagRecyclerAdapter adapter;

    public SwipeToDeleteTags(TagRecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        adapter.moveItem(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        /* old
        int position = viewHolder.getAdapterPosition();
        adapter.deleteItem(position);
        */

        int deletedTagPosition = viewHolder.getAdapterPosition();
        final Tag deletedTag = adapter.getTagAtPosition(deletedTagPosition);

        adapter.deleteItem(deletedTagPosition);

        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Tag deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> adapter.restoreItem(deletedTag, deletedTagPosition));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();



    }
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState){
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder != null){
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }
}
