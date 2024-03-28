package com.example.spotifywrapper.fragments.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapper.R;
import com.example.spotifywrapper.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 *This fragment will enable the user to create a new account if they have never created an account
 * before.
 */
public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    private EditText et_email, et_password, et_name;
    private TextView tv_login;
    private Button bt_create_account;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_sign_up, container, false);

        et_email = rootview.findViewById(R.id.et_email_field);
        et_password = rootview.findViewById(R.id.et_password_field);
        et_name = rootview.findViewById(R.id.et_name_field);
        tv_login = rootview.findViewById(R.id.tv_login_instead);

        bt_create_account = rootview.findViewById(R.id.create_account_button);

        bt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();

                // Get the fragment manager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Begin a transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the login fragment
                fragmentTransaction.replace(R.id.fragment_container_login, loginFragment);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return rootview;
    }

    /**
     * Creates a new account for the user and logs them in on the device so that their credentials
     * are locally stored
     */
    private void createUserAccount() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String name = et_name.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User();
                            user.setDisplay_name(name);
                            user.setEmail(email);

                            // We save the user's information in the database so that we can
                            // store the user's past wrapped and other information over there
                            saveUserInDb(user);

                            // Switch to SpotifyAuthorizationFragment
                            authorizeSpotify();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    /**
     * This function is responsible for storing the user's information in the database.
     * @param user
     */
    private void saveUserInDb(User user) {
        // When the user is created, Firebase automatically generates a unique ID for the user.
        // We will use the same ID to index the user in our database
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * Similarly, once the user has been created, we then move them to the spotify authorization fragment
     * where they will log into spotify
     */
    private void authorizeSpotify() {
        // Create a new instance of the SpotifyAuthorizationFragment
        SpotifyAuthorizationFragment spotifyFragment = new SpotifyAuthorizationFragment();

        // Get the fragment manager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the SpotifyAuthorizationFragment
        fragmentTransaction.replace(R.id.fragment_container_login, spotifyFragment);

        // Add the transaction to the back stack so the user can navigate back
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }
}