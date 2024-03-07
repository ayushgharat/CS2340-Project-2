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
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotifyAuthorizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotifyAuthorizationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private SpotifyAuthorizationManager authorizationManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "SpotifyAuthorizationFragment";

    public SpotifyAuthorizationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpotifyAuthorizationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotifyAuthorizationFragment newInstance(String param1, String param2) {
        SpotifyAuthorizationFragment fragment = new SpotifyAuthorizationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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