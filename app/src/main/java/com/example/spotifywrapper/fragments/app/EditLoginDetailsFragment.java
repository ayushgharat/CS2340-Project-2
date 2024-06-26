package com.example.spotifywrapper.fragments.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotifywrapper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditLoginDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditLoginDetailsFragment extends Fragment {

    private static final String TAG = "EditLoginDetailsFragment";

    String oldEmail, oldName;

    private EditText et_name, et_email, et_password;
    private Button bt_save_credentials;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Map<String, Object> userDetails;

    public EditLoginDetailsFragment(Map<String, Object> userDetails) {
        // Required empty public constructor
        this.userDetails = userDetails;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_login_details, container, false);
        et_name = rootView.findViewById(R.id.et_edit_name_field);
        et_email = rootView.findViewById(R.id.et_edit_email_field);
        et_password = rootView.findViewById(R.id.et_edit_name_password);
        bt_save_credentials = rootView.findViewById(R.id.bt_save_details);

        oldName = userDetails.get("display_name").toString();
        oldEmail = userDetails.get("email").toString();

        et_name.setText(oldName);
        et_email.setText(oldEmail);
        
        bt_save_credentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();
            }
        });

        return rootView;
    }

    private void updateDetails() {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        String uid = mAuth.getCurrentUser().getUid();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(!name.equals(oldName)) {
            db.collection("users").document(uid).update("display_name", name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            });
        }

        if(!email.equals((oldEmail))) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        db.collection("users").document(uid).update("email", email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireActivity(), "Name updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        if(!password.isEmpty()) {
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                            }
                        }
                    });
        }
    }
}