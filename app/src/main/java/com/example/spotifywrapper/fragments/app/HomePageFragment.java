package com.example.spotifywrapper.fragments.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapper.R;
import com.example.spotifywrapper.StoryActivity;
import com.example.spotifywrapper.model.Wrapped;
import com.example.spotifywrapper.utils.ApiClient;
import com.example.spotifywrapper.utils.RecommendationAdapter;
import com.example.spotifywrapper.utils.SharedViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePageFragment extends Fragment {

    private TextView tv_username, tv_follower_count;
    private RecyclerView rv_recommendation;
    private ArrayList<String> track_id;
    private ArrayList<String> previewURL;
    private ImageView iv_profile_picture;
    private Button bt_generate_insights, bt_generate_recommendations;
    private SharedViewModel viewModel;
    private String token;
    private ApiClient client;
    private Wrapped wrapped_info;
    private Gson gson;
    private RecommendationAdapter adapter;
    private static final String TAG = "HomePageFragment";

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * This function just basically return an instance of the fragment
     * @return
     */
    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gson = new Gson();
        track_id = new ArrayList<>();
        previewURL = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        tv_username = rootView.findViewById(R.id.tv_user_name);
        tv_follower_count = rootView.findViewById(R.id.tv_follower_count);
        iv_profile_picture = rootView.findViewById(R.id.profile_picture);
        bt_generate_insights = rootView.findViewById(R.id.bt_generate_insights);
        bt_generate_recommendations = rootView.findViewById(R.id.bt_generate_recommendations);
        rv_recommendation = rootView.findViewById(R.id.rv_recommendation);



        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        client = new ApiClient();
        wrapped_info = new Wrapped();

        viewModel.getToken().observe(getViewLifecycleOwner(), token -> {
            this.token = token;
        });

        // Observe changes in data from the viewModel class
        viewModel.getUserJSON().observe(getViewLifecycleOwner(), userJSONData -> {
            // Handle updated data
            // This code will be executed when data changes
            // Update UI or perform any other actions

            try {
                JSONObject userJSON = new JSONObject(userJSONData);
                displayUserInfo(userJSON);
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Failed to parse user information");
            }
        });

        bt_generate_insights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWrappedData();
            }
        });

        bt_generate_recommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRecommendation();
            }
        });

        return rootView;
    }

    private void generateRecommendation() {
        // Create a StringBuilder to construct the result
        StringBuilder sb = new StringBuilder();

        // Append each element of the ArrayList to StringBuilder
        for (int i = 0; i < track_id.size(); i++) {
            sb.append(track_id.get(i));
            if (i < track_id.size() - 1) {
                sb.append(","); // Append comma if it's not the last element
            }
        }

        // Convert StringBuilder to String
        String seed = sb.toString();

        client.getRecommendations(token, seed, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string(); // Get the raw JSON response
                    JsonObject recommendedTracks = JsonParser.parseString(responseData).getAsJsonObject();

                    JsonArray userRecommendedTracks = new JsonArray();

                    for (int i = 0; i < Math.min(5, recommendedTracks.getAsJsonArray("tracks").size()); i++) {
                        JsonObject object = new JsonObject();
                        object.add("name", recommendedTracks.getAsJsonArray("tracks").get(i).getAsJsonObject().get("name"));
                        object.add("artist", recommendedTracks.getAsJsonArray("tracks").get(i).getAsJsonObject().getAsJsonArray("artists").get(0).getAsJsonObject().get("name"));
                        object.add("url", recommendedTracks.getAsJsonArray("tracks").get(i).getAsJsonObject().getAsJsonObject("album").getAsJsonArray("images").get(0).getAsJsonObject().get("url"));
                        userRecommendedTracks.add(object);
                    }

                    requireActivity().runOnUiThread(() -> {
                        adapter = new RecommendationAdapter(userRecommendedTracks);
                        rv_recommendation.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        rv_recommendation.setAdapter(adapter);
                    });



                    Log.d(TAG, "onResponse: " + userRecommendedTracks);
                } else {
                    Log.e(TAG, "onResponse: " + response.message() );
                }
            }
        });
    }

    private void getWrappedData() {
        client.getFavoriteArtists(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JsonParser jsonParser = new JsonParser();
                JsonObject favoriteArtists = (JsonObject) jsonParser.parse(response.body().string());
                //Log.d(TAG, "onResponse: " + favoriteArtists.getJSONArray("items").get(0));


                JsonArray userDisplayFavouriteArtists = new JsonArray();


                for (int i = 0; i < Math.min(5, favoriteArtists.getAsJsonArray("items").size()); i++) {
                    JsonObject object = new JsonObject();
                    object.add("name", favoriteArtists.getAsJsonArray("items").get(i).getAsJsonObject().get("name"));
                    object.add("url", favoriteArtists.getAsJsonArray("items").get(i).getAsJsonObject().getAsJsonArray("images").get(0).getAsJsonObject().get("url"));

                    userDisplayFavouriteArtists.add(object);
                }

                wrapped_info.setFavoriteArtists(userDisplayFavouriteArtists);
                //string_resources.add(new JSONObject().put("items", userDisplayFavouriteArtists));

                client.getFavoriteTracks(token, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("HTTP", "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {

                            JsonParser jsonParser = new JsonParser();
                            JsonObject favoriteTracks = (JsonObject) jsonParser.parse(response.body().string());
                            Log.d(TAG, "onResponse: " + favoriteTracks);

                            JsonArray userDisplayFavouriteTracks = new JsonArray();

                            for (int i = 0; i < Math.min(5, favoriteTracks.getAsJsonArray("items").size()); i++) {
                                JsonObject object = new JsonObject();
                                object.add("name", favoriteTracks.getAsJsonArray("items").get(i).getAsJsonObject().get("name"));
                                object.add("artist", favoriteTracks.getAsJsonArray("items").get(i).getAsJsonObject().getAsJsonArray("artists").get(0).getAsJsonObject().get("name"));
                                object.add("url", favoriteTracks.getAsJsonArray("items").get(i).getAsJsonObject().getAsJsonObject("album").getAsJsonArray("images").get(0).getAsJsonObject().get("url"));
                                userDisplayFavouriteTracks.add(object);
                            }

                            // save spotify ID for the most played track
                            track_id.add(0, favoriteTracks.getAsJsonArray("items").get(1).getAsJsonObject().get("id").getAsString());
                            track_id.add(1, favoriteTracks.getAsJsonArray("items").get(0).getAsJsonObject().get("id").getAsString());


                            //string_resources.add(new JSONObject().put("items", userDisplayFavouriteTracks));
                            wrapped_info.setFavoriteTracks(userDisplayFavouriteTracks);

                            client.getPopularLikedSong(token, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e("HTTP", "Failed to fetch data: " + e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    try {

                                        JsonParser jsonParser = new JsonParser();
                                        JsonObject allTracks = (JsonObject) jsonParser.parse(response.body().string());

                                        JsonObject userDisplayFavouriteTracks = new JsonObject();

                                        int popularity = 0, maxIndex = 0;

                                        for (int i = 0; i < allTracks.getAsJsonArray("items").size(); i++) {
                                            if(allTracks.getAsJsonArray("items").get(i).getAsJsonObject().getAsJsonObject("track").get("popularity").getAsInt() > popularity) {
                                                maxIndex = i;
                                                popularity = allTracks.getAsJsonArray("items").get(i).getAsJsonObject().getAsJsonObject("track").get("popularity").getAsInt();
                                            }
                                        }

                                        userDisplayFavouriteTracks.addProperty("name", allTracks.getAsJsonArray("items").get(maxIndex).getAsJsonObject().getAsJsonObject("track").get("name").getAsString());
                                        userDisplayFavouriteTracks.addProperty("album", allTracks.getAsJsonArray("items").get(maxIndex).getAsJsonObject().getAsJsonObject("track").getAsJsonObject("album").get("name").getAsString());
                                        userDisplayFavouriteTracks.addProperty("artist", allTracks.getAsJsonArray("items").get(maxIndex).getAsJsonObject().getAsJsonObject("track").getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString());
                                        userDisplayFavouriteTracks.addProperty("url", allTracks.getAsJsonArray("items").get(maxIndex).getAsJsonObject().getAsJsonObject("track").getAsJsonObject("album").getAsJsonArray("images").get(0).getAsJsonObject().get("url").getAsString());

                                        //save id for preview
                                        track_id.add(2, allTracks.getAsJsonArray("items").get(maxIndex).getAsJsonObject().getAsJsonObject("track").get("id").getAsString());


                                        wrapped_info.setTracksSaved(userDisplayFavouriteTracks);

                                        client.getTrackDetails(token, track_id.get(0), new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    String responseData = response.body().string(); // Get the raw JSON response
                                                    JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
                                                    String previewUrl = jsonResponse.get("preview_url").getAsString(); // Extract the preview URL
                                                    // Do something with the preview URL, e.g., update the UI or store it for later use
                                                    //Log.d("Preview URL", "Track Preview URL: " + previewUrl);

                                                    previewURL.add(0, previewUrl);

                                                    client.getTrackDetails(token, track_id.get(1), new Callback() {
                                                        @Override
                                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                            Log.e(TAG, "onFailure: " + e.getMessage());
                                                        }

                                                        @Override
                                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                            if (response.isSuccessful()) {
                                                                String responseData = response.body().string(); // Get the raw JSON response
                                                                JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
                                                                String previewUrl = jsonResponse.get("preview_url").getAsString(); // Extract the preview URL
                                                                // Do something with the preview URL, e.g., update the UI or store it for later use
                                                                Log.d("Preview URL", "Track Preview URL: " + previewUrl);

                                                                previewURL.add(1, previewUrl);
                                                                client.getTrackDetails(token, track_id.get(2), new Callback() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                                        Log.e(TAG, "onFailure: " + e.getMessage());
                                                                    }

                                                                    @Override
                                                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                                        if (response.isSuccessful()) {
                                                                            String responseData = response.body().string(); // Get the raw JSON response
                                                                            JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
                                                                            String previewUrl = jsonResponse.get("preview_url").getAsString(); // Extract the preview URL
                                                                            // Do something with the preview URL, e.g., update the UI or store it for later use
                                                                            Log.d("Preview URL", "Track Preview URL: " + previewUrl);

                                                                            previewURL.add(2, previewUrl);

                                                                            String jsonString = gson.toJson(previewURL);
                                                                            JsonArray previewTracks = gson.fromJson(jsonString, JsonArray.class);
                                                                            wrapped_info.setPreviewTracks(previewTracks);

                                                                            initiateStoryActivity();
                                                                        } else {
                                                                            // Handle the case where the server response was not successful
                                                                            Log.e("HTTP error", "Failed to fetch track details. Response code: " + response.code());}

                                                                    }
                                                                });

                                                                //initiateStoryActivity();
                                                            } else {
                                                                // Handle the case where the server response was not successful
                                                                Log.e("HTTP error", "Failed to fetch track details. Response code: " + response.code());}

                                                        }
                                                    });

                                                    //initiateStoryActivity();
                                                } else {
                                                    // Handle the case where the server response was not successful
                                                    Log.e("HTTP error", "Failed to fetch track details. Response code: " + response.code());}
                                                
                                            }
                                        });


                                    } catch (Exception e) {
                                        Log.e("JSON", "Failed to parse data: " + e);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.e("JSON", "Failed to parse data: " + e);
                        }
                    }
                });

            }
        });




    }

    private void initiateStoryActivity() {
        String my_wrapped = gson.toJson(wrapped_info);
        Intent intent = new Intent(requireActivity(), StoryActivity.class);
        intent.putExtra("wrapped_info", my_wrapped);
        intent.putExtra("toBeSaved", true);
        startActivity(intent);
    }


    /**
     * This function takes the updated information, stored as a JSON object, and adds the text to the
     * components in the fragment. Since it is an asynchronous function, it has to run on the main UI thread
     * as opposed to a background thread
     * @param userJSON
     * @throws JSONException
     */
    private void displayUserInfo(JSONObject userJSON) throws JSONException {

        requireActivity().runOnUiThread(() -> {
            try {
                tv_username.setText(userJSON.getString("display_name"));
                tv_follower_count.setText(String.valueOf(userJSON.getJSONObject("followers").getInt("total")));

                // Picasso is an external library that I am using to render the images into the imageview quickly
                Picasso.get().load(userJSON.getJSONArray("images").getJSONObject(0).getString("url")).into(iv_profile_picture);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Again, because of the asynchronous thing, I was using this function to render toasts
     * @param message
     */
    private void showToast(String message) {
        // Show Toast on the main thread
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}
