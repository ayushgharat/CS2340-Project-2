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

    private static final String TAG = "MainActivity";

    /**
     * This authorization manager is used to carry out the spotify authorization
     * and import the user's details from spotify
     */
    private SpotifyAuthorizationManager authorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SpotifyAuthorizationManager class
        authorizationManager = new SpotifyAuthorizationManager();

        // Get the fragment manager which will be used to decide which fragment populates the activity
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // This checks if an app user is already logged in
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            Log.d(TAG, "onCreate: There is a user");
            
            // Add the spotify authorization fragment to the container, since we need to renew their spotify credentials
            SpotifyAuthorizationFragment spotifyAuthorizationFragment = new SpotifyAuthorizationFragment();
            fragmentTransaction.replace(R.id.fragment_container_login, spotifyAuthorizationFragment);

            // Commit the transaction
            fragmentTransaction.commit();
        } else {
            // Add the splash screen fragment to the container, where the user can either log in or sign up
            SplashScreenFragment yourFragment = new SplashScreenFragment();
            fragmentTransaction.replace(R.id.fragment_container_login, yourFragment);

            // Commit the transaction
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         *
         Pass the result to SpotifyAuthorizationManager once the user has logged in, where it will ensure that
         the app has received the access code and will take the user to the home page
         */

        authorizationManager.handleTokenAuthorizationResult(requestCode, resultCode, data, new SpotifyAuthorizationManager.AuthorizationCallback() {
            @Override
            public void onAuthorizationStarted() {
                // Handle authorization started
            }

            @Override
            public void onAuthorizationCompleted(String accessToken) {
                // Handle authorization completed
                Log.d(TAG, "onAuthorizationCompleted: Access Token received");

                // The intent is used to take the user to the home page
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("token", accessToken);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthorizationFailed(String errorMessage) {
                // Handle authorization failure if needed
                Log.e(TAG, "onAuthorizationFailed: " + errorMessage);
            }
        });
    }
}