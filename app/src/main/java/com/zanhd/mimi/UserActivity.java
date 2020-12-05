package com.zanhd.mimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Util.UserApi;
import model.User;
import ui.HomeRecyclerAdapter;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton addNewMemeImageButton;
    private ImageButton settingsImageButton;

    private List<User> memeList;
    private RecyclerView recyclerView;
    private HomeRecyclerAdapter homeRecyclerAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Memes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        addNewMemeImageButton = findViewById(R.id.add_new_meme_imagebutton);
        settingsImageButton = findViewById(R.id.settings_user_imagebutton);

        memeList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addNewMemeImageButton.setOnClickListener(this);
        settingsImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_new_meme_imagebutton :
                startActivity(new Intent(UserActivity.this,PostNewMemeActivity.class));
                break;
            case R.id.settings_user_imagebutton :
                startActivity(new Intent(UserActivity.this,SettingsActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference //.whereEqualTo("userId", UserApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {

                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                User user = snapshot.toObject(User.class);
                                memeList.add(user);
                            }

                            homeRecyclerAdapter = new HomeRecyclerAdapter(UserActivity.this, memeList);
                            recyclerView.setAdapter(homeRecyclerAdapter);
                            homeRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UserActivity", "onFailure: " + e.toString());
                    }
                });
    }
}