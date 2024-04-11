package com.example.spotifywrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.spotifywrapper.fragments.app.HomePageFragment;
import com.example.spotifywrapper.fragments.app.HistoryFragment;
import com.example.spotifywrapper.fragments.app.ProfileFragment;
import com.example.spotifywrapper.model.Wrapped;
import com.example.spotifywrapper.utils.ApiClient;
import com.example.spotifywrapper.utils.SharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private String token;
    private ApiClient apiClient;
    private SharedViewModel viewModel;
    private ArrayList<Wrapped> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        gson = new Gson();
        list = new ArrayList<>();

        // This code is used to receive the accessToken for the user from the previous activity
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        /**
         * Since data needs to be shared across multiple fragments, we use the ViewModel class
         * to maintain the data needed for the homepage, history and profile fragments
         */

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setToken(token);

        // The apiclient can be used to make API requests
        apiClient = new ApiClient();

        getUserProfile();
        getPastWrapped();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //assigns a listener that will update the fragment whenever a new item is selected
        bottomNavigationView.setOnItemSelectedListener(navListener);
    }

    /**
     * Calls the user profile function in the apiClient class, which retrieves the user's profile from Spotify
     */
    private void getUserProfile() {

        apiClient.getUserProfile(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.d(TAG, "onResponse: Token used: " + token);
                    JSONObject userJSON = new JSONObject(response.body().string());
                    viewModel.setUserJson(userJSON.toString());
                    loadHomePageFragment();
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }

    /**
     * When the activity is loaded for the first time, since no option has been selected on the bottom tab
     * navigator, we render the homePageFragment by default
     */
    private void loadHomePageFragment() {
        // Create an instance of HomePageFragment
        HomePageFragment homePageFragment = HomePageFragment.newInstance();

        // Get the fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment or add it to the container
        fragmentTransaction.replace(R.id.fragment_container_home_page, homePageFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void getPastWrapped() {
        String uid = mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("wrapped_info"));
                        JsonElement element = JsonParser.parseString(document.getData().get("wrapped_info").toString());

                        for(int i = 0; i < element.getAsJsonArray().size(); i++) {
                            list.add(gson.fromJson(element.getAsJsonArray().get(i).getAsJsonObject(), Wrapped.class));
                        }

                        viewModel.setSaved_wrapped(list);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * This code is used to assign the options on the bottomNavigationView so that when a tab is
     * selected, the appropriate fragment is shown
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        // assigns the fragment based on the id of the menu item
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            selectedFragment = new HomePageFragment();
        } else if (itemId == R.id.navigation_history) {
            selectedFragment = new HistoryFragment();
        } else if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        }


        if (selectedFragment != null) {
            // updates the fragment when a new item is selected
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home_page, selectedFragment).commit();
        }
        return true;
    };
}