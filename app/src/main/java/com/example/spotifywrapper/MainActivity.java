package com.example.spotifywrapper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.spotifywrapper.fragments.login.SplashScreenFragment;
import com.example.spotifywrapper.fragments.login.SpotifyAuthorizationFragment;
import com.example.spotifywrapper.utils.SpotifyAuthorizationManager;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;

    private static final String TAG = "MainActivity";
    private SpotifyAuthorizationManager authorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SpotifyAuthorizationManager
        authorizationManager = new SpotifyAuthorizationManager();

        // Get the fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            Log.d(TAG, "onCreate: There is a user");
            
            // Add the fragment to the container
            SpotifyAuthorizationFragment spotifyAuthorizationFragment = new SpotifyAuthorizationFragment();
            fragmentTransaction.replace(R.id.fragment_container_login, spotifyAuthorizationFragment);

            // Commit the transaction
            fragmentTransaction.commit();
        } else {
            // Add the fragment to the container
            SplashScreenFragment yourFragment = new SplashScreenFragment();
            fragmentTransaction.replace(R.id.fragment_container_login, yourFragment);

            // Commit the transaction
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the result to SpotifyAuthorizationManager
        authorizationManager.handleTokenAuthorizationResult(requestCode, resultCode, data, new SpotifyAuthorizationManager.AuthorizationCallback() {
            @Override
            public void onAuthorizationStarted() {
                // Handle authorization started
            }

            @Override
            public void onAuthorizationCompleted(String accessToken) {
                // Handle authorization completed
                Log.d(TAG, "onAuthorizationCompleted: Access Token received");
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("token", accessToken);
                startActivity(intent);
            }

            @Override
            public void onAuthorizationFailed(String errorMessage) {
                // Handle authorization failure if needed
                Log.e(TAG, "onAuthorizationFailed: " + errorMessage);
            }
        });
    }
}