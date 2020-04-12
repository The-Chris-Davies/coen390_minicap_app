package com.minicap.collarapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SplashPage extends AppCompatActivity {
    private final String TAG = "StartingPage";

    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");

    private RecyclerView dogList;
    private RecyclerView.Adapter dogAdapter;
    private RecyclerView.LayoutManager dogLayoutManager;

    ArrayList<Dog> dogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        setupUI();
    }

    //Todo: Create background colour change when selecting dog

    protected void setupUI()
    {
        dogList = findViewById(R.id.dogList);
        dogLayoutManager = new LinearLayoutManager(this);

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
}