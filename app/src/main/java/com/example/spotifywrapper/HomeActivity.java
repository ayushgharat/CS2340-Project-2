package com.example.spotifywrapper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.spotifywrapper.fragments.app.HomePageFragment;

import okhttp3.OkHttpClient;

import okhttp3.Call;

public class HomeActivity extends AppCompatActivity {

    private Call mCall;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static final String TAG = "HomeActivity";
    private TextView followerCount;
    private String token;
    private Button Signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        loadHomePageFragment();
    }

    private void loadHomePageFragment() {
        // Create an instance of HomePageFragment
        HomePageFragment homePageFragment = HomePageFragment.newInstance(token);

        // Get the fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment or add it to the container
        fragmentTransaction.replace(R.id.fragment_container_home_page, homePageFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }
}