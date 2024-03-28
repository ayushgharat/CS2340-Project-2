package com.example.spotifywrapper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifywrapper.databinding.ActivityStoryBinding;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private ImageView iv_background;
    private static final String TAG = "StoryActivity";
    private int content;

    long pressTime = 0L;

    private final int[] resources = new int[]{
            R.drawable.black_bg,
            R.drawable.dark_purple_bg,
            R.drawable.light_purple_bg,
            R.drawable.light_purple_bg,
            R.drawable.orange_bg,
            R.drawable.teal_bg,
    };
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
        iv_background = findViewById(R.id.image);

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

    @Override
    public void onNext() {
        iv_background.setImageResource(resources[++content]);
        //Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrev() {
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show();
        Intent intent =  new Intent(StoryActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}