package com.minicap.collarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.concurrent.Executor;

public class changeDogDialogFragment extends DialogFragment {

    private final String TAG = "Change Dog Fragment";

    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");
    private CollectionReference mUserDogRef;
    private FirebaseUser user;


    private RecyclerView dogList;
    private RecyclerView.Adapter dogAdapter;
    private RecyclerView.LayoutManager dogLayoutManager;

    ArrayList<Dog> dogs;
    ArrayList<Dog> allowedDogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_dog, container, false);
        dogList = view.findViewById(R.id.dogList);
        dogLayoutManager = new LinearLayoutManager(getContext());         //Here i use getActivity()

        //Get user instance for id
        user = FirebaseAuth.getInstance().getCurrentUser();

        setupUI();
        return view;
    }

    protected void setupUI()
    {
        dogLayoutManager = new LinearLayoutManager(getContext());
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
                                            dogAdapter = new DogListAdapter(getContext(), allowedDogs);
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



//        mDogRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    //if error has occurred
//                    Log.e(TAG, "Error in snapshotListener: ", e);
//                    return;
//                }
//
//                //add the positions to the array
//                dogs = new ArrayList();
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
//                    dogs.add(new Dog(documentSnapshot.getString("name"), documentSnapshot.getId()));
//                Log.d(TAG, "Found " + dogs.size() + " dogs in the firebase");
//                for(Dog dog: dogs) Log.d(TAG, "Dog: " + dog.getName() + " id: " + dog.getId());
//
//                //if no positions available, continue
//                if (dogs.isEmpty()) {
//                    Toast.makeText(getContext(), "Please activate a collar", Toast.LENGTH_LONG).show();        //Here i use getActivity()
//                    Log.i(TAG, "No collars detected for this account");
//                    return;
//                }
//
//                //add the dogs to the arrayList
//                dogAdapter = new DogListAdapter(getContext(), dogs);        //getContext() needs to be swapped for getActivity(), but issues still arise
//                dogList.setAdapter(dogAdapter);
//                dogList.setLayoutManager(dogLayoutManager);
//                dogList.getAdapter().notifyDataSetChanged();   //probably not necessary
//            }
//        });
    }
}
