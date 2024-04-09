package com.example.spotifywrapper;

import android.content.Intent;
import android.os.Bundle;

import com.example.spotifywrapper.model.Wrapped;
import com.example.spotifywrapper.utils.ApiClient;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private ImageView iv_background;
    private static final String TAG = "StoryActivity";
    private int content;
    private String token;
    private ApiClient client;
    private Wrapped wrapped_info;
    private TextView tv_story_text;

    long pressTime = 0L;

    private final int[] resources = new int[]{
            R.drawable.black_bg,
            R.drawable.dark_purple_bg,
            R.drawable.light_purple_bg,
            R.drawable.light_purple_bg,
            R.drawable.orange_bg,
            R.drawable.teal_bg,
    };

    private ArrayList<JSONObject> string_resources;
    long limit = 500L;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        client = new ApiClient();
        wrapped_info = new Wrapped();
        string_resources = new ArrayList<>();

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        Log.d(TAG, "onCreate: Token" + token);

        getWrappedData();

        iv_background = findViewById(R.id.image);
        tv_story_text = findViewById(R.id.tv_story_test);

        storiesProgressView = findViewById(R.id.stories);
        Log.d(TAG, "onCreate: " + storiesProgressView);
        storiesProgressView.setStoriesCount(resources.length); // <- set stories
        storiesProgressView.setStoryDuration(2000L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress

        content = 0;
        iv_background.setImageResource(resources[content]);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    private void getWrappedData() {
        client.getFavoriteArtists(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject favoriteArtists = new JSONObject(response.body().string());
                    Log.d(TAG, "onResponse: " + favoriteArtists.getJSONArray("items").get(0));


                    JSONArray userDisplayFavouriteArtists = new JSONArray();

                    for (int i = 0; i < Math.min(3, favoriteArtists.getJSONArray("items").length()); i++) {
                        userDisplayFavouriteArtists.put(favoriteArtists.getJSONArray("items").getJSONObject(i));
                    }


                    wrapped_info.setFavoriteArtists(new JSONObject().put("items", userDisplayFavouriteArtists));
                    string_resources.add(new JSONObject().put("items", userDisplayFavouriteArtists));

                } catch (JSONException e) {
                    Log.e("JSON", "Failed to parse data: " + e);
                }
            }
        });

        client.getFavoriteTracks(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject favoriteTracks = new JSONObject(response.body().string());
                    Log.d(TAG, "onResponse: " + favoriteTracks);
                    wrapped_info.setFavoriteTracks(favoriteTracks);
                } catch (JSONException e) {
                    Log.e("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }

    @Override
    public void onNext() {
        iv_background.setImageResource(resources[++content]);
        //Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
        try {
            displayUserInfo(string_resources.get(++content));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

        private void displayUserInfo(JSONObject jsonObject) throws JSONException {
            this.runOnUiThread(() -> {
                try {
                    tv_story_text.setText(jsonObject.getJSONArray("items").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }


    @Override
    public void onPrev() {
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}