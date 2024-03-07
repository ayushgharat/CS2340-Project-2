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
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
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
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
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
                // Create a new instance of the SpotifyAuthorizationFragment
                LoginFragment loginFragment = new LoginFragment();

                // Get the fragment manager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Begin a transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the SpotifyAuthorizationFragment
                fragmentTransaction.replace(R.id.fragment_container_login, loginFragment);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return rootview;
    }

    private void createUserAccount() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String name = et_name.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            User user = new User();
                            user.setDisplay_name(name);
                            user.setEmail(email);
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

    private void saveUserInDb(User user) {
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

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