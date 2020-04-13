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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SplashPage extends AppCompatActivity {
    private final String TAG = "StartingPage";

    private CollectionReference mUserDogRef;
    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");
    FirebaseUser user;

    private RecyclerView dogList;
    private RecyclerView.Adapter dogAdapter;
    private RecyclerView.LayoutManager dogLayoutManager;

    ArrayList<Dog> dogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

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

    protected void setupUI()
    {
        dogList = findViewById(R.id.dogList);
        dogLayoutManager = new LinearLayoutManager(this);

        mUserDogRef = FirebaseFirestore.getInstance().collection("users/" + user.getUid() + "/allowedDogs");
        final ArrayList<String> allowedDogs = new ArrayList();
        mUserDogRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "got user dogs: ");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        allowedDogs.add((String) document.getData().get("ref"));
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        mDogRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in snapshotListener: ", e);
                    return;
                }

                //add the positions to the array
                dogs = new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    dogs.add(new Dog(documentSnapshot.getString("name"), documentSnapshot.getId()));
                Log.d(TAG, "Found " + dogs.size() + " dogs in the firebase");
                for(Dog dog: dogs) Log.d(TAG, "Dog: " + dog.getName() + " id: " + dog.getId());

                //if no positions available, continue
                if (dogs.isEmpty()) {
                    Toast.makeText(SplashPage.this, "Please activate a collar", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No collars detected for this account");
                    return;
                }

                //add the dogs to the arrayList
                dogAdapter = new DogListAdapter(SplashPage.this, dogs);
                dogList.setAdapter(dogAdapter);
                dogList.setLayoutManager(dogLayoutManager);
                dogList.getAdapter().notifyDataSetChanged();   //probably not necessary
            }
        });
    }
    public void signOut(View view) {
        //sign user out and go to signin activity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SplashPage.this, LoginActivity.class);
        startActivity(intent);
    }
}