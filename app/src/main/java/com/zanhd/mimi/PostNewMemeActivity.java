package com.zanhd.mimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Util.UserApi;
import model.User;

public class PostNewMemeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLARY_CODE = 1;
    private static final String TAG = "PostNewMemeActivity";
    private ImageView uploadedImage;
    private ImageButton addMemeButton;
    private Button postMemeButton;
    private TextView goBackText;
    private ProgressBar progressBar;

    private Uri imageUri;

    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =  db.collection("Memes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_meme);

        if(UserApi.getInstance().getUserId() == null) {
            //no user is logged in => contradiction
            startActivity(new Intent(PostNewMemeActivity.this,LoginActivity.class));
            finish();
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        uploadedImage = findViewById(R.id.image_added_imageView);
        addMemeButton = findViewById(R.id.camera_imagebutton);
        postMemeButton = findViewById(R.id.post_button);
        goBackText = findViewById(R.id.gotoHome_text);
        progressBar = findViewById(R.id.post_meme_progressBar);

        addMemeButton.setOnClickListener(this);
        postMemeButton.setOnClickListener(this);
        goBackText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.camera_imagebutton :
                //get image from gallary/phone
                Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,GALLARY_CODE);
                break;

            case R.id.post_button :
                PostMeme(view);
                break;

            case R.id.gotoHome_text :
                startActivity(new Intent(PostNewMemeActivity.this,UserActivity.class));
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLARY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                uploadedImage.setImageURI(imageUri);
            }
        }
    }

    private void PostMeme(final View view) {
        progressBar.setVisibility(View.VISIBLE);

        if(imageUri != null) {

            final StorageReference filepath = storageReference
                    .child("Meme_images")
                    .child("Meme : " + Timestamp.now().getSeconds());

            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filepath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String imageUrl = uri.toString();
                                            User user = new User();
                                            user.setUserId(UserApi.getInstance().getUserId());
                                            user.setImageUrl(imageUrl);
                                            user.setUsername(UserApi.getInstance().getUsername());
                                            user.setName(UserApi.getInstance().getName());
                                            user.setEmail(UserApi.getInstance().getEmail());

                                            collectionReference.add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            progressBar.setVisibility(View.GONE);

                                                            Snackbar.make(view,"Meme Uploaded Successfully",Snackbar.LENGTH_SHORT).show();

                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startActivity(new Intent(PostNewMemeActivity.this,UserActivity.class));
                                                                    finish();
                                                                }
                                                            },1000);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: " + e.toString());
                                                            Snackbar.make(view,"Something went wrong",Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            //i dont know what to do here(any suggestions?
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onFailure: " + e.toString());
                            Snackbar.make(view,"Something went wrong",Snackbar.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Snackbar.make(view,"Select a Image",Snackbar.LENGTH_SHORT).show();
        }
    }

}