package com.example.suitcase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddItem extends AppCompatActivity {

    EditText itemName, itemPrice, itemDescription;
    ImageView itemImage;
    ConstraintLayout uploadInfo;
    String name, price, description;
    String user_id;
    private  static final int PICK_IMAGE_REQUEST = 1;
    private Uri filepath; // Uri points to the location of the imagepublic StorageReference storageReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String downloadUrl;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemDescription = findViewById(R.id.itemDescription);
        itemImage = findViewById(R.id.imageButton);
        uploadInfo = findViewById(R.id.Upload);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid(); //get id of the logged in user
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);


        System.out.print("THIS IS THE IMAGE URL: " + downloadUrl);


        uploadInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                name = itemName.getText().toString();
                price = itemPrice.getText().toString();
                description = itemDescription.getText().toString();

                //check if the user has entered all the details
                if(!name.isEmpty() || !price.isEmpty() || !description.isEmpty() || filepath != null){
                    //If the user has entered all details,try catch block is used to handle any errors
                    // get the image path,
                    // store in file and compress image
                    // then upload to firebase
                    uploadImageToStorage();


                }else{
                    Toast.makeText(AddItem.this, "Please fill in all the details🛑🛑", Toast.LENGTH_SHORT).show();
                }
            }

        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            // When the user clicks on the image button,
            // the user will either accept or deny the permission to access the gallery
            // If the user accepts, the user will be able to access the gallery
            // If the user denies, the user will not be able to access the gallery
            public void onClick(View v) {   //
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddItem.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                        Toast.makeText(AddItem.this, "Please Activate Permissions First 😃", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AddItem.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else{
                        Toast.makeText(AddItem.this, "Hooray🎉🎊,Choose An image", Toast.LENGTH_SHORT).show();
                        imageChooser();
                    }

                }else{
                    imageChooser();

                }

            }
        });
    }


    private void imageChooser() { //chooses image from gallery

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){ //if the request to choose image is true we are going to get the data


            filepath = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                itemImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //upload image to firebase storage
    private void uploadImageToStorage() {
        if (filepath != null) {
            ProgressDialog progressDialog = new ProgressDialog(AddItem.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload or failure of image
            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL directly from the taskSnapshot
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    // Call a method to save the URL to Firestore
                                    SavingToFirestore(downloadUrl);
                                    Toast.makeText(AddItem.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddItem.this, MainActivity.class);
                                    startActivity(intent);

                                }
                            });

                            progressDialog.dismiss(); // Dismiss dialog
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(AddItem.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    //saving the image details url to firestore and user id to be able to retrieve items of a particular user
    private void SavingToFirestore(String url){
        String uniqueID = UUID.randomUUID().toString(); //generate unique id for each item
        // to be able to perform edit or delete on each item
        Map<String,String> userMap = new HashMap<>();
        boolean isChecked = false ;
        userMap.put("userId",user_id);
        userMap.put("name",name);
        userMap.put("price",price);
        userMap.put("description",description);
        userMap.put("image",downloadUrl.toString());
        userMap.put("id", uniqueID);
        userMap.put("checkBoxValue", String.valueOf(isChecked));
        firebaseFirestore.collection("items").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                itemName.setText("");
                itemPrice.setText("");
                itemDescription.setText("");
                itemImage.setImageResource(R.drawable.itemphoto);
                Toast.makeText(AddItem.this,"Item Upload successfully😃🎉",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddItem.this,MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddItem.this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

}



