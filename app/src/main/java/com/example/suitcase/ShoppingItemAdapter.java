package com.example.suitcase;

import static android.content.Intent.createChooser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {

    private Context context;
    private List<item> mitems;
    Intent intent;
    private String price;
    private String productName;
    item itemDetails;

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    /**
     * Constructor for the ShoppingItemAdapter.
     *
     * @param context The context of the app.
     * @param items   The list of items to be displayed.
     */
    public ShoppingItemAdapter(Context context, List<item> items) {
        this.mitems = items;
        this.context = context;
    }

    /**
     * Removes an item from the list at the specified position.
     *
     * @param position The position of the item to be removed.
     */
    public void removeItem(int position) {
        mitems.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item view and create a new ViewHolder.
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.shopping_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data from the Item object to the views in the ViewHolder.
        item upload = mitems.get(position);
        Glide.with(context).load(upload.getItemImage()).into(holder.imageView);
        holder.nameTextView.setText(upload.getItemDescription());
        holder.nameTextView2.setText(upload.getItemName());
        holder.textViewPrice.setText("P" + "" + upload.getItemPrice());
        holder.checkBox.setChecked(upload.isChecked());
        price = upload.getItemPrice();
        productName = upload.getItemName();
    }

    @Override
    public int getItemCount() {
        return mitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView nameTextView2;
        public TextView textViewPrice;
        public ImageView imageView;
        public RelativeLayout displayLayout;
        public CheckBox checkBox;
        public ImageView shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views in the item layout.
            nameTextView = itemView.findViewById(R.id.textViewDescription);
            nameTextView2 = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            displayLayout = itemView.findViewById(R.id.layoutDisplay);
            checkBox = itemView.findViewById(R.id.checkBox);
            shareButton = itemView.findViewById(R.id.share);

            itemView.setOnClickListener(this);

            // Set click listeners for the checkbox and share button.
            checkBox.setOnClickListener(v -> {
                // Inserts the checkbox value into the database where the user id is equal to the current user id.
                fStore.collection("items").whereEqualTo("userId", fAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                item item = mitems.get(getBindingAdapterPosition());
                                for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++) {
                                    if (Objects.requireNonNull(task.getResult().getDocuments().get(i).getString("id"))
                                            .equals(item.getItemID())) {
                                        task.getResult().getDocuments().get(i).getReference()
                                                .update("checkBoxValue", checkBox.isChecked());
                                    }
                                }
                            }
                        });
            });

            // Share button click listener.
            shareButton.setOnClickListener(v -> {
                // Use getBindingAdapterPosition() instead of getAdapterPosition()
                int position = getBindingAdapterPosition();

                // Check if the position is valid (not NO_POSITION)
                if (position != RecyclerView.NO_POSITION) {
                    // Retrieve item details at the correct adapter position
                    itemDetails = mitems.get(position);

                    // Create an intent for sharing
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    // Compose the message with item details
                    String shareBody = "Hello there, I came across this fantastic holiday shopping item" +
                            " " + itemDetails.getItemName() + " " + "and the price is P" +
                            itemDetails.getItemPrice() + ". Could you please assist me in acquiring this item? Thank you!";

                    // Set the subject for the sharing
                    String shareSub = "Holiday Shopping Item";
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);

                    // Set the message body for sharing
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                    // Start the sharing activity
                    v.getContext().startActivity(createChooser(intent, "Share Using"));
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Handle click events on the entire item.
            int position = getBindingAdapterPosition();
            item items = mitems.get(position);
            intent = new Intent(v.getContext(), ItemDetails.class);
            intent.putExtra("name", items.getItemName());
            intent.putExtra("description", items.getItemDescription());
            intent.putExtra("image", items.getItemImage());
            intent.putExtra("price", items.getItemPrice());
            intent.putExtra("id", items.getItemID());
            v.getContext().startActivity(intent);
        }
    }
}
