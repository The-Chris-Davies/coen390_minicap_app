package com.minicap.collarapp;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addDogDialogFragment extends DialogFragment {

    private final String TAG = "addDogFragment";

    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");
    private CollectionReference mUserRef = FirebaseFirestore.getInstance().collection("users");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Add dog objects
    protected EditText addDogNameEditText;
    protected Button confirmDogButton;

    //Data to pass to database
    private String dogName;
    private String userEmail;
    private String currUserEmail;
    private String currentUserId;

    private FirebaseAuth mAuth;

    ArrayList<Dog> dogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_dog, container, false);

        //Initialize dog name text and confirmation button
        addDogNameEditText = view.findViewById(R.id.addDogNameEditText);
        confirmDogButton = view.findViewById(R.id.confirmDogButton);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "User ID:" + currentUserId);

        getCurrentUserEmail();
        setupUserList();
        confirmDog();


        confirmDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dogName = addDogNameEditText.getText().toString();
                Log.d(TAG, "Dog name:" + dogName);

                Map<String, String> data = new HashMap<>();
                data.put("name", dogName);
                data.put("battery", String.valueOf(100));

                //See if user exists first, if not, add to list of users, if he does, then skip
                db.collection("dogs")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                //Todo: Add base values and timestamps for each collection using a hashmap of data for each
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("external_temperature");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("temperature");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("heartrate");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("position");
                                Toast.makeText(getContext(), "Dog added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding dog to database", e);
                            }
                        });
                }
        });

        return view;
    }

    //Get dog from main activity and pass to position activity
    public void getCurrentUserEmail() {
        userEmail = new String();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userEmail = bundle.getString("userEmail");
            if (userEmail == null) {
                Log.d(TAG, "Email:" + userEmail);
            }
            else {
                Log.d(TAG, "Email not found.");
            }
        }
    }

    protected void setupUserList() {

        //See if user exists first, if not, add to list of users, if he does, then skip
        DocumentReference docEmailRef = db.collection("users").document(currentUserId);
        docEmailRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                Map<String, String> data = new HashMap<>();
                                data.put("User ID: ", currentUserId);
                                db.collection("users").document(currentUserId).set(data);
                                Log.d(TAG, "Account added");
                            } else {
                                Log.d(TAG, "Account exists");
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
    }

    public void confirmDog() {
        //Save button after edit text
        confirmDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dogName = addDogNameEditText.getText().toString();
                Log.d(TAG, "Dog name:" + dogName);

                Map<String, String> data = new HashMap<>();
                data.put("name", dogName);
                data.put("battery", String.valueOf(100));

                //See if user exists first, if not, add to list of users, if he does, then skip
                db.collection("dogs")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                //Todo: Add base values and timestamps for each collection using a hashmap of data for each
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("external_temperature");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("temperature");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("heartrate");
                                db.collection("dogs").document(documentReference.getId())
                                        .collection("position");
                                Toast.makeText(getContext(), "Dog added", Toast.LENGTH_SHORT).show();


                                //Todo: Add reference of dog to user collection "allowedDogs"



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding dog to database", e);
                            }
                        });
                    }
            });
        }


}
