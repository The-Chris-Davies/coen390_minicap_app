package com.minicap.collarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private EditText loginEmail;
    private EditText loginPass;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPass = findViewById(R.id.loginPass);
        userEmail = new String();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //todo: if user is logged in, forward to splash page
        if(mAuth.getCurrentUser() != null)
            goToSplashPage();
    }

    public void createAccount(View v) {
        String email = loginEmail.getText().toString();
        userEmail = email;
        String password = loginEmail.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            failMessageSignUp();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //entry in firestore at /users/{userID} is created when the user adds their first dog
                            goToSplashPage();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            failMessageSignUp();
                        }
                    }
                });
    }

    public void signIn(View V) {
        String email = loginEmail.getText().toString();
        userEmail = email;
        String password = loginEmail.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            failMessageLogIn();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToSplashPage();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            failMessageLogIn();
                        }
                    }
                });
    }

    private void failMessageSignUp() {
        Toast.makeText(LoginActivity.this, "Sign up failed.",
                Toast.LENGTH_SHORT).show();
    }

    private void failMessageLogIn() {
        Toast.makeText(LoginActivity.this, "Log in failed.",
                Toast.LENGTH_SHORT).show();
    }

    private void goToSplashPage() {
        //go to the splash page when authenticated
        Intent intent = new Intent(LoginActivity.this, SplashPage.class);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    //On phone back button pressed return to MainActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
