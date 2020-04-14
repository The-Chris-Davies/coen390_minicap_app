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

    private final String TAG = "Change Dog Fragment";

    private CollectionReference mDogRef = FirebaseFirestore.getInstance().collection("dogs");
    private CollectionReference mUserRef = FirebaseFirestore.getInstance().collection("users");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Add dog objects
    protected EditText addDogNameEditText;
    protected Button confirmDogButton;
    protected SharedPreferenceHelper sharedPreferenceHelper;

    //Data to pass to database
    private String dogName;
    private String userEmail;
    private String currUserEmail;
    private String currentUserId;

    //private FirebaseAuth mAuth;

    ArrayList<Dog> dogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_dog, container, false);

        //Initialize dog name text and confirmation button
        addDogNameEditText = view.findViewById(R.id.addDogNameEditText);
        confirmDogButton = view.findViewById(R.id.confirmDogButton);

        sharedPreferenceHelper = new SharedPreferenceHelper(getActivity());

//        mAuth = FirebaseAuth.getInstance();
//        currentUserId = mAuth.getCurrentUser().toString();
//        Log.i(TAG, "User ID:" + currentUserId);

        getCurrentUserEmail();
        setupUI();
        return view;
    }

    //Get dog from main activity and pass to position activity
    public void getCurrentUserEmail() {
        userEmail = new String();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userEmail = bundle.getString("userEmail");
            Toast.makeText(getContext(), "User email: " + userEmail, Toast.LENGTH_SHORT).show();
        }
    }

    protected void setupUI() {
        //dogName = addDogNameEditText.getText().toString();  //Get name input on edit text

        //if (!dogName.isEmpty()) {
            DocumentReference docEmailRef = db.collection("users").document(userEmail);
            docEmailRef
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (!document.exists()) {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("email", userEmail);
                                    db.collection("users").document(userEmail).set(data);
                                    Toast.makeText(getContext(), "Account added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Account exists", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Account already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        //}

//        Map<String, String> data = new HashMap<>();
//        data.put("email", userEmail);
//        db.collection("users")
//                .add(data)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });




//        CollectionReference userCollRef = db.collection("users");
//        Query query = userCollRef.whereEqualTo("yourPropery", "yourValue");
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });




    }

    public void confirmDog() {
        //Save button after edit text
        confirmDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set initial values for name, age and id
                String dogName = addDogNameEditText.getText().toString();

                //Put values in editor to save later
                //sharedPreferenceHelper.setProfileName()

                //If inputs to name, age, and id are invalid
                if (dogName.length() == 0) {
                    addDogNameEditText.setError("Input valid name");
                }



                //After applying changes
                //Save if inputs are valid
                if (dogName.length() != 0) {
                    //Save added values from editor
                    //sharedPreferenceHelper.save();
                    Toast toast = Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_SHORT);
                    toast.show();
                }
                //Do not save is incorrect inputs
                else {
                    Toast toast = Toast.makeText(getActivity(), "Dog not added", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


}
