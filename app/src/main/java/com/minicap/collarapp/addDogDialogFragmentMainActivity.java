package com.minicap.collarapp;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addDogDialogFragmentMainActivity extends DialogFragment {

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

        getCurrentUserEmail();  //Get email ONLY when user is passing from log in page to splash page
        setupUserList();    //Set up the user in the user/ database collection
        confirmDog();     //When button entered, dog added to dogs/ and reference added to users/

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

    //Set up the user in the user/ database collection
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

    //When button entered, dog added to dogs/ and reference added to users/
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

                                DocumentReference allowedDog = db.collection("dogs").document(documentReference.getId());
                                //String refDogPath = "/dogs/" + documentReference.getId();   //Reference path for allowedDogs
                                HashMap<String, DocumentReference> allowedDogMap = new HashMap<>();
                                allowedDogMap.put("ref", allowedDog);

                                db.collection("users").document(currentUserId).collection("allowedDogs")
                                        .add(allowedDogMap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "Reference added for dog: " + dogName);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding reference to database", e);
                                            }
                                        });

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

                                //Reload activity to generate new dog to list on
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
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
