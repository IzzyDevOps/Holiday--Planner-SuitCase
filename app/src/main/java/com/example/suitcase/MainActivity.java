package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureItemHelper.GestureItemHelperListener {

    private RecyclerView recyclerView;
    private ShoppingItemAdapter shoppingItemAdapter;
    private List<item> itemList;
    private LinearLayoutManager linearLayoutManager;
    FloatingActionButton fab;
    FirebaseAuth fAuth;
    private ConstraintLayout logout;
    FirebaseUser user;
    FirebaseFirestore fStore;
    item item;
    TextView loggedIn;
    String userName;
    String lastName;
    final String TAG = "";



    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fab = findViewById(R.id.fab);
        logout = findViewById(R.id.logOutBtn);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        loggedIn = findViewById(R.id.loggedInUserEmailTextView);

        fStore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);

        fStore.collection("users").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override

            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        userName = document.getString("fName");
                        lastName = document.getString("lName");
                        Log.d(TAG, "onComplete: " + userName);
                    }
                    loggedIn.setText(userName + " " + lastName);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddItem.class);
                startActivity(i);
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new GestureItemHelper(0, ItemTouchHelper.LEFT, (GestureItemHelper.GestureItemHelperListener) MainActivity.this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        //GestureItemHelper gestureItemHelper = new GestureItemHelper(this, recyclerView, this);
        //gestureItemHelper.attachToRecyclerView(recyclerView);

        fStore.collection("items").whereEqualTo("userId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    itemList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        item = new item();
                        item.setItemName(document.getData().get("name").toString());
                        item.setItemPrice(document.getData().get("price").toString());
                        item.setItemImage(document.getData().get("image").toString());
                        item.setItemDescription(document.getData().get("description").toString());
                        item.setItemID(document.getData().get("id").toString());
                        item.setChecked(Boolean.parseBoolean(document.getData().get("checkBoxValue").toString()));
                        itemList.add(item);

                    }
                    shoppingItemAdapter = new ShoppingItemAdapter(getApplicationContext(), itemList);
                    recyclerView.setAdapter(shoppingItemAdapter);
                    shoppingItemAdapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        item = itemList.get(position);

        fStore.collection("items").whereEqualTo("userId", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("id").equals(item.getItemID())) {
                            fStore.collection("items").document(document.getId()).delete();
                            shoppingItemAdapter.removeItem(position);
                            Toast.makeText(MainActivity.this, "Data Deleted !!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
