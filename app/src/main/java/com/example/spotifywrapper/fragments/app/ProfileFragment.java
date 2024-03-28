package com.example.spotifywrapper.fragments.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.spotifywrapper.HomeActivity;
import com.example.spotifywrapper.MainActivity;
import com.example.spotifywrapper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This fragment will show the user's profile details and enable them to
 * edit their information, sign out or delete their account
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private Button bt_delete, bt_sign_out;
    private FirebaseAuth mAuth;
    private ImageButton bt_edit;
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


    private void deleteAccount() {
        String id = mAuth.getCurrentUser().getUid();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
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
                });


    }
}