package com.example.spotifywrapper.fragments.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.spotifywrapper.HomeActivity;
import com.example.spotifywrapper.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * This fragment
 */
public class SplashScreenFragment extends Fragment {

    private static final String TAG = "SplashScreenFragment";
    private Button signUpButton;

    private String mAccessToken;

    public SplashScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_splash_screen, container, false);


        // Initialize the buttons
        Button bt_login = rootview.findViewById(R.id.token_btn);
        signUpButton = rootview.findViewById(R.id.sign_up_button);

        // Set the click listeners for the buttons
        bt_login.setOnClickListener((v) -> {
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // Replace the current fragment with the destination fragment
            transaction.replace(R.id.fragment_container_login, loginFragment);

            // Add the transaction to the back stack (optional, allows going back)
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment destinationFragment = new SignUpFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

                // Replace the current fragment with the destination fragment
                transaction.replace(R.id.fragment_container_login, destinationFragment);

                // Add the transaction to the back stack (optional, allows going back)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return rootview;
    }


}