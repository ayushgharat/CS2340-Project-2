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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This fragment will be used to enable users to log in to the app
 * if they already have an existing account
 */
public class LoginFragment extends Fragment {

    private EditText et_email, et_password;
    private TextView tv_sign_up;
    private Button bt_create_account;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginFragment";


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        et_email = rootView.findViewById(R.id.et_email_field);
        et_password = rootView.findViewById(R.id.et_password_field);
        tv_sign_up = rootView.findViewById(R.id.tv_sign_up_instead);

        bt_create_account = rootView.findViewById(R.id.bt_login);

        bt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // if the user has not yet created an account, this option enables them to quickly navigate to
        // the sign up page
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of the SpotifyAuthorizationFragment
                SignUpFragment signUpFragment = new SignUpFragment();

                // Get the fragment manager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Begin a transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the SpotifyAuthorizationFragment
                fragmentTransaction.replace(R.id.fragment_container_login, signUpFragment);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    /**
     * This function signs the user into the app, so that their information gets stored even when
     * they exit the app.
     */
    private void loginUser() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Switch to SpotifyAuthorizationFragment
                            authorizeSpotify();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                            Toast.makeText(requireActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    /**
     * If the sign in was successful, the user is then guided to the spotify sign in page where
     * they will sign into their spotify account
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