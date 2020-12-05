package com.zanhd.mimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import Util.UserApi;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null) {
                    String currentUserId = currentUser.getUid();
                    LoginUser(currentUserId);
                }
            }
        };

        emailText = findViewById(R.id.email_edittext);
        passwordText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        signupButton = findViewById(R.id.signup_text);
        signupButton.setOnClickListener(this);
        progressBar = findViewById(R.id.login_progressBar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button :
                loginAccount(emailText.getText().toString().trim(),passwordText.getText().toString().trim(),view);
                break;
            case R.id.signup_text :
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
                break;
        }

    }

    private void loginAccount(String email, String password, final View view) {

        if(email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view,"Empty Fields Not Allowed",Snackbar.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Log.d("Login", "signInWithEmail: Succcess");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                LoginUser(user.getUid());
                            } else {
                                Log.d("Login", "signInWithEmail : failure");
                                Snackbar.make(view,"Authentication failed",Snackbar.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void LoginUser(String currentUserId) {
        collectionReference.whereEqualTo("userId",currentUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if(error != null) return;

                        assert queryDocumentSnapshots != null;
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                UserApi userApi = UserApi.getInstance();
                                userApi.setUserId(snapshot.getString("userId"));

                                userApi.setUsername(snapshot.getString("username"));
                                userApi.setName(snapshot.getString("name"));
                                userApi.setEmail(snapshot.getString("email"));

                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this,UserActivity.class));
                                finish();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}