package com.zanhd.mimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import Util.UserApi;
import model.User;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signupButton;
    private TextView alreadyHaveAccountButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = findViewById(R.id.signup_name_edittext);
        usernameText = findViewById(R.id.signup_username_edittext);
        emailText = findViewById(R.id.signup_email_edittext);
        passwordText = findViewById(R.id.signup_password_edittext);
        signupButton = findViewById(R.id.signup_signup_button);
        alreadyHaveAccountButton = findViewById(R.id.already_have_account_text);
        progressBar = findViewById(R.id.signup_progressBar);

        alreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUserAccount(emailText.getText().toString().trim(),passwordText.getText().toString().trim(),view);
            }
        });
    }

    private void CreateUserAccount(final String email, String password, final View view) {

        final String name = nameText.getText().toString().trim();
        final String username = usernameText.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || username.isEmpty()) {
            Snackbar.make(view,"Empty Fields Not Allowed",Snackbar.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Log.d("Signup", "createUserWithEmail:success");
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                CreateAccount(currentUser.getUid(),view,name,username,email);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.w("Signup", "createUserWithEmail:failure", task.getException());
                                Snackbar.make(view,"Authentication failed.",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void CreateAccount(final String currentUserId, final View view, final String name, final String username, final String email) {
        final User user = new User();
        user.setUserId(currentUserId);
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        collectionReference.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        UserApi userApi = UserApi.getInstance();
                        userApi.setUserId(currentUserId);

                        userApi.setName(name);
                        userApi.setUsername(username);
                        userApi.setEmail(email);

                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(view,"Account Created",Snackbar.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SignupActivity.this,UserActivity.class));
                                finish();
                            }
                        },1000);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,"Something went wrong",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


}