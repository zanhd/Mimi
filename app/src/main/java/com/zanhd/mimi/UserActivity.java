package com.zanhd.mimi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton addNewMemeImageButton;
    ImageButton settingsImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        addNewMemeImageButton = findViewById(R.id.add_new_meme_imagebutton);
        settingsImageButton = findViewById(R.id.settings_user_imagebutton);
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
}