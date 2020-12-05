package com.zanhd.mimi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Util.UserApi;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 2;
    private ImageView profilepicImageView;
    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView signOutTextView;
    private ImageButton editUserDetailsImageButton;
    private ImageButton goBackImageButton;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profilepicImageView = findViewById(R.id.settings_profilepic_imageview);
        usernameTextView = findViewById(R.id.settings_username_textview);
        nameTextView = findViewById(R.id.settings_name_textview);
        emailTextView = findViewById(R.id.settings_email_textview);
        signOutTextView = findViewById(R.id.settings_signout_textview);
        editUserDetailsImageButton = findViewById(R.id.settings_editUserDetails_imageButton);
        goBackImageButton = findViewById(R.id.settings_goback_imageButton);

        profilepicImageView.setOnClickListener(this);
        signOutTextView.setOnClickListener(this);
        editUserDetailsImageButton.setOnClickListener(this);
        goBackImageButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null) {
            //contradiction
            startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
            finish();
        }

        fillAllUserDetails();

    }

    private void fillAllUserDetails() {
        //TODO: How to set previus saved Profile Pic
        usernameTextView.setText(UserApi.getInstance().getUsername());
        nameTextView.setText(UserApi.getInstance().getName());
        emailTextView.setText(UserApi.getInstance().getEmail());
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.settings_profilepic_imageview :
                Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,GALLERY_CODE);
                break;
            case R.id.settings_signout_textview :
                SignOut(view);
                break;
            case R.id.settings_editUserDetails_imageButton :
                editUserDetails(view);
                break;
            case R.id.settings_goback_imageButton :
                startActivity(new Intent(SettingsActivity.this,UserActivity.class));
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                profilepicImageView.setImageURI(imageUri);
            }
        }
    }

    private void SignOut(View view) {
        if(currentUser != null  && firebaseAuth != null) {
            firebaseAuth.signOut();
            startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
            finish();
        } else {
            Snackbar.make(view,"Something went wrong",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void editUserDetails(View view) {
        //TODO: complete this function
    }


}