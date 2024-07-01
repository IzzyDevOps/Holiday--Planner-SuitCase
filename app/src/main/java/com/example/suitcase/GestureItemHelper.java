package com.example.suitcase;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom ItemTouchHelper for handling swipe and drag gestures in a RecyclerView.
 * Used in the context of a holiday shopping app.
 */
public class GestureItemHelper extends ItemTouchHelper.SimpleCallback {

    private GestureItemHelperListener listener;

    /**
     * Constructor for GestureItemHelper.
     *
     * @param dragDirs  Directions in which the items can be dragged.
     * @param swipeDirs Directions in which the items can be swiped.
     * @param listener  Listener to handle swipe events.
     */
    public GestureItemHelper( int dragDirs, int swipeDirs, GestureItemHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    /**
     * Attaches the GestureItemHelper to a RecyclerView.
     *
     * @param recyclerView The RecyclerView to attach to.
     */
    public void attachToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Called when items are moved/dragged.
     *
     * @param recyclerView The RecyclerView.
     * @param viewHolder   The ViewHolder of the item being moved.
     * @param target       The ViewHolder of the target item.
     * @return true if the move is allowed, false otherwise.
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    /**
     * Called when an item is selected.
     *
     * @param viewHolder  The ViewHolder of the selected item.
     * @param actionState The current state of the action.
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            // Highlight the selected item.
            View foregroundView = ((ShoppingItemAdapter.ViewHolder) viewHolder).displayLayout;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    /**
     * Called while an item is being moved or swiped, to draw over the item.
     *
     * @param c                The Canvas on which to draw.
     * @param recyclerView     The RecyclerView.
     * @param viewHolder       The ViewHolder of the item being moved or swiped.
     * @param dX               The horizontal displacement.
     * @param dY               The vertical displacement.
     * @param actionState      The current state of the action.
     * @param isCurrentlyActive True if the item is currently being manipulated.
     */
    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        // Draw over the item during the interaction.
        View foregroundView = ((ShoppingItemAdapter.ViewHolder) viewHolder).displayLayout;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    /**
     * Called when the interaction with the item is completed, to clear any changes made during the move or swipe.
     *
     * @param recyclerView The RecyclerView.
     * @param viewHolder   The ViewHolder of the item.
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Clear any changes made during the interaction.
        View foregroundView = ((ShoppingItemAdapter.ViewHolder) viewHolder).displayLayout;
        getDefaultUIUtil().clearView(foregroundView);
    }

    /**
     * Called while an item is being moved or swiped, to draw the item.
     *
     * @param c                The Canvas on which to draw.
     * @param recyclerView     The RecyclerView.
     * @param viewHolder       The ViewHolder of the item being moved or swiped.
     * @param dX               The horizontal displacement.
     * @param dY               The vertical displacement.
     * @param actionState      The current state of the action.
     * @param isCurrentlyActive True if the item is currently being manipulated.
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        // Draw the item during the interaction.
        View foregroundView = ((ShoppingItemAdapter.ViewHolder) viewHolder).displayLayout;
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    /**
     * Convert relative direction flags to absolute ones.
     *
     * @param flags           The direction flags.
     * @param layoutDirection The layout direction.
     * @return The absolute direction flags.
     */
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    /**
     * Called when an item is swiped.
     *
     * @param viewHolder The ViewHolder of the swiped item.
     * @param direction  The swipe direction.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Notify the listener about the swipe event.
        listener.onSwipe(viewHolder, direction, viewHolder.getAbsoluteAdapterPosition());
    }

    /**
     * Interface to handle swipe events.
     */
    public interface GestureItemHelperListener {
        void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}