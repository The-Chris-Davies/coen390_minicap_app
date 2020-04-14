package com.minicap.collarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SplashPage extends AppCompatActivity {
    private final String TAG = "SplashPage";

    private CollectionReference mUserDogRef;
    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");
    FirebaseUser user;

    private RecyclerView dogList;
    private RecyclerView.Adapter dogAdapter;
    private RecyclerView.LayoutManager dogLayoutManager;
    //private FirebaseAuth mAuth;
    private String currentUser;

    protected FloatingActionButton addDogFloatingActionButton;

    ArrayList<Dog> dogs;
    ArrayList<Dog> allowedDogs;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        //Get email for user
        userEmail = new String();
        getCurrentUserEmail();

        //Add dog fragment
        addDogFloatingActionButton = findViewById(R.id.addDogFloatingActionButton);
        addDogFragment(addDogFloatingActionButton);

//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser().getUid().toString();
//        Log.i(TAG, "User ID:" + currentUser);

        //if no user is signed in, go to sign in page
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(SplashPage.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        setupUI();
    }

    //Todo: Create background colour change when selecting dog

    //Get dog from main activity and pass to position activity
    public void getCurrentUserEmail() {
        userEmail = new String();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userEmail = bundle.getString("userEmail");
            Toast.makeText(this, "User email: " + userEmail, Toast.LENGTH_SHORT).show();
        }
    }


    protected void setupUI()
    {
        dogList = findViewById(R.id.dogList);
        dogLayoutManager = new LinearLayoutManager(this);

        mUserDogRef = FirebaseFirestore.getInstance().collection("users/" + user.getUid() + "/allowedDogs");
        mUserDogRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "got user dogs: ");
                    allowedDogs = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ((DocumentReference) document.getData().get("ref")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            //if (documentSnapshot.exists()) {
                                                Log.d(TAG, "adding dog: " + documentSnapshot.getString("name"));
                                                allowedDogs.add(new Dog(documentSnapshot.getString("name"), documentSnapshot.getId()));
                                                dogAdapter = new DogListAdapter(SplashPage.this, allowedDogs);
                                                dogList.setAdapter(dogAdapter);
                                                dogList.setLayoutManager(dogLayoutManager);
                                                dogList.getAdapter().notifyDataSetChanged();   //probably not necessary
                                            //}
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                        });

                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }

                    Log.d(TAG, "dogs: " + allowedDogs);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
    public void signOut(View view) {
        //sign user out and go to signin activity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SplashPage.this, LoginActivity.class);
        startActivity(intent);
    }

    public void addDogFragment(FloatingActionButton addDogFloatingActionButton) {
        addDogFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDogDialogFragment dialog = new addDogDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userEmail", userEmail);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "addDogDialogFragment");
            }
        });
    }
}