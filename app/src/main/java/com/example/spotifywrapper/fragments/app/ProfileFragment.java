package com.example.spotifywrapper.fragments.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spotifywrapper.HomeActivity;
import com.example.spotifywrapper.MainActivity;
import com.example.spotifywrapper.R;
import com.example.spotifywrapper.utils.SharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This fragment will show the user's profile details and enable them to
 * edit their information, sign out or delete their account
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private Button bt_delete, bt_sign_out;
    private FirebaseAuth mAuth;
    private ImageButton bt_edit;
    private ImageView iv_profile;
    private SharedViewModel viewModel;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        bt_delete = rootView.findViewById(R.id.bt_delete_account);
        bt_edit = rootView.findViewById(R.id.bt_edit_profile);
        bt_sign_out = rootView.findViewById(R.id.bt_sign_out);
        iv_profile = rootView.findViewById(R.id.iv_profile_profile_picture);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe changes in data from the viewModel class
        viewModel.getUserJSON().observe(getViewLifecycleOwner(), userJSONData -> {
            // Handle updated data
            // This code will be executed when data changes
            // Update UI or perform any other actions

            try {
                JSONObject userJSON = new JSONObject(userJSONData);
                displayUserInfo(userJSON);
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Failed to parse user information");
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        bt_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserOut();
            }
        });

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of EditLoginDetailsFragment
                EditLoginDetailsFragment fragment = new EditLoginDetailsFragment();

                // Get the fragment manager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Begin a transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the existing fragment or add it to the container
                fragmentTransaction.replace(R.id.fragment_container_home_page, fragment);
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    private void signUserOut() {
        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }

    private void showConfirmationDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

// 2. Chain together various setter methods to set the dialog characteristics.
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone")
                .setTitle("Deleting account?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
                dialog.dismiss();
            }
        });

// 3. Get the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This function takes the updated information, stored as a JSON object, and adds the text to the
     * components in the fragment. Since it is an asynchronous function, it has to run on the main UI thread
     * as opposed to a background thread
     * @param userJSON
     * @throws JSONException
     */
    private void displayUserInfo(JSONObject userJSON) throws JSONException {

        requireActivity().runOnUiThread(() -> {
            try {

                // Picasso is an external library that I am using to render the images into the imageview quickly
                Picasso.get().load(userJSON.getJSONArray("images").getJSONObject(0).getString("url")).into(iv_profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Again, because of the asynchronous thing, I was using this function to render toasts
     * @param message
     */
    private void showToast(String message) {
        // Show Toast on the main thread
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }


    private void deleteAccount() {
        String id = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "deleteAccount: Deleting" + id);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: User has been removed from firebaseAuth");
                            //This code removes the user from the database
                            db.collection("users").document(id)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(requireActivity(), "This account should be deleted", Toast.LENGTH_SHORT).show();
                                            signUserOut();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireActivity(), "Unable to Delete Account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "User needs to relogin to delete the account", Toast.LENGTH_SHORT).show();
                        signUserOut();
                    }
                });


    }
}