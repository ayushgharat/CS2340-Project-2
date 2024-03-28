package com.example.spotifywrapper.fragments.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotifywrapper.HomeActivity;
import com.example.spotifywrapper.MainActivity;
import com.example.spotifywrapper.R;
import com.example.spotifywrapper.utils.SpotifyAuthorizationManager;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;

/**
 * This fragment carries out the authorization with spotify to retreive the user's spotify
 * profile.
 */
public class SpotifyAuthorizationFragment extends Fragment {

    private SpotifyAuthorizationManager authorizationManager;

    private static final String TAG = "SpotifyAuthorizationFragment";

    public SpotifyAuthorizationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authorizationManager = new SpotifyAuthorizationManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_spotify_authorization, container, false);
        requestAccessToken();
        return rootView;
    }


    /**
     * This function requests the access token from spotify which is required for all API calls
     * related to that user
     */
    private void requestAccessToken() {
        authorizationManager.requestAccessToken(getActivity(), new SpotifyAuthorizationManager.AuthorizationCallback() {
            @Override
            public void onAuthorizationStarted() {
                // Handle the start of authorization if needed
            }

            @Override
            public void onAuthorizationCompleted(String accessToken) {
                // Handle the completed authorization, e.g., navigate to the next screen
                //((MainActivity) requireActivity()).handleAccessToken(accessToken);
                Log.d(TAG, "onAuthorizationCompleted: Login Successful");
            }

            @Override
            public void onAuthorizationFailed(String errorMessage) {
                // Handle authorization failure if needed
                Log.e(TAG, "onAuthorizationFailed: " + errorMessage );
            }
        });
    }
}