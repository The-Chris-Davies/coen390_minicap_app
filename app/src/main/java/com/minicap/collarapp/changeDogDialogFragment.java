package com.minicap.collarapp;

import android.app.Activity;
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

import com.google.firebase.firestore.CollectionReference;
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

    private RecyclerView dogList;
    private RecyclerView.Adapter dogAdapter;
    private RecyclerView.LayoutManager dogLayoutManager;

    ArrayList<Dog> dogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_dog, container, false);
        dogList = view.findViewById(R.id.dogList);
        dogLayoutManager = new LinearLayoutManager(getContext());         //Here i use getActivity()

        setupUI();
        return view;
    }

    protected void setupUI()
    {
        mDogRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    Toast.makeText(getContext(), "Please activate a collar", Toast.LENGTH_LONG).show();        //Here i use getActivity()
                    Log.i(TAG, "No collars detected for this account");
                    return;
                }

                //add the dogs to the arrayList
                dogAdapter = new DogListAdapter(getContext(), dogs);        //getContext() needs to be swapped for getActivity(), but issues still arise
                dogList.setAdapter(dogAdapter);
                dogList.setLayoutManager(dogLayoutManager);
                dogList.getAdapter().notifyDataSetChanged();   //probably not necessary
            }
        });
    }
}
