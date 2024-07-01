package com.example.suitcase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ItemDetails extends AppCompatActivity {
    TextView itemName, itemDescription;
    ConstraintLayout editBtn, deleteBtn;
    TextView itemPriceView;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView itemImage;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private Uri filepath;
    String itemNameString, itemDescriptionString, itemPrice;
    StorageReference storageReference;
    private String downloadUrl;
    String itemImageString;
    String itemID;

    // ActivityResultLauncher for picking images
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    onActivityResult(PICK_IMAGE_REQUEST, RESULT_OK, result.getData());
                }
            }
    );

    // ProgressBar for upload progress
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);
        itemName = findViewById(R.id.textViewName);
        itemDescription = findViewById(R.id.textViewDescription);
        itemImage = findViewById(R.id.itemImage);
        itemPriceView = findViewById(R.id.itemPriceView);
        deleteBtn = findViewById(R.id.delete);
        editBtn = findViewById(R.id.save);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = new ProgressBar(this);

        // Clicking on the imageView will open the gallery if permission is granted
        itemImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ItemDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                    Toast.makeText(ItemDetails.this, "Please Activate Permissions First 😃", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(ItemDetails.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Toast.makeText(ItemDetails.this, "Hooray🎉🎊,Choose An image", Toast.LENGTH_SHORT).show();
                    openFileChooser();
                }
            } else {
                openFileChooser();
            }
        });

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            itemNameString = extras.getString("name");
            itemDescriptionString = extras.getString("description");
            itemImageString = extras.getString("image");
            itemPrice = extras.getString("price");
            itemID = extras.getString("id");
        }
        itemName.setText(itemNameString);
        itemDescription.setText(itemDescriptionString);
        Picasso.get().load(itemImageString).into(itemImage);
        itemPriceView.setText(itemPrice);

        // Edit Button, saves the changes made to the item when clicked, including the image, and updates the database
        editBtn.setOnClickListener(v -> {
            fStore.collection("items").whereEqualTo("userId", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uploadImageToStorage();

                    for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++) {
                        if (Objects.requireNonNull(task.getResult().getDocuments().get(i).getString("id")).equals(itemID)) {
                            task.getResult().getDocuments().get(i).getReference().update("name", itemName.getText().toString());
                            task.getResult().getDocuments().get(i).getReference().update("description", itemDescription.getText().toString());
                            task.getResult().getDocuments().get(i).getReference().update("price", itemPriceView.getText().toString());
                            Intent intent = new Intent(ItemDetails.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        });

        // Delete Button, deletes the item when clicked
        deleteBtn.setOnClickListener(v -> {
            fStore.collection("items").whereEqualTo("userId", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++) {
                        if (Objects.requireNonNull(task.getResult().getDocuments().get(i).getString("id")).equals(itemID)) {
                            task.getResult().getDocuments().get(i).getReference().delete();
                            Intent intent = new Intent(ItemDetails.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        });
    }

    // Opens the file chooser
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

    // Gets the image from the file chooser and displays it in the imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                itemImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Show ProgressBar using AlertDialog
    private void showProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(progressBar);
        builder.setCancelable(false);
        builder.show();
    }

    // Update ProgressBar
    private void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
    }

    // Hide ProgressBar
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    // Uploads the image to storage
    private void uploadImageToStorage() {
        if (filepath != null) {
            // Initialize ProgressBar
            showProgressBar();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filepath).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                downloadUrl = Objects.requireNonNull(uriTask.getResult()).toString();
                if (uriTask.isSuccessful()) {
                    clickUpdate(downloadUrl);
                }
                // Dismiss ProgressBar
                hideProgressBar();
            }).addOnFailureListener(e -> {
                // Dismiss ProgressBar
                hideProgressBar();
                Toast.makeText(ItemDetails.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                // Update ProgressBar
                updateProgressBar((int) progress);
            });
        }
    }

    // Updating the image URL in Firestore
    private void clickUpdate(String downloadUrl) {
        fStore.collection("items").whereEqualTo("userId", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++) {
                    if (Objects.requireNonNull(task.getResult().getDocuments().get(i).getString("id")).equals(itemID)) {
                        task.getResult().getDocuments().get(i).getReference().update("image", downloadUrl);
                        Intent intent = new Intent(ItemDetails.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
