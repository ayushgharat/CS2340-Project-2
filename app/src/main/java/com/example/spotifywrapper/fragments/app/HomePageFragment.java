package com.example.spotifywrapper.fragments.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapper.R;
import com.example.spotifywrapper.utils.ApiClient;
import com.example.spotifywrapper.utils.SharedViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePageFragment extends Fragment {

    private Call mCall;
    private TextView tv_username, tv_follower_count;
    private ImageView iv_profile_picture;
    private Button bt_generate_insights;
    private String userJSONString;
    private ApiClient apiClient;
    private SharedViewModel viewModel;
    private static final String TAG = "HomePageFragment";

    public HomePageFragment() {
        // Required empty public constructor
    }

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
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        tv_username = rootView.findViewById(R.id.tv_user_name);
        tv_follower_count = rootView.findViewById(R.id.tv_follower_count);
        iv_profile_picture = rootView.findViewById(R.id.profile_picture);
        bt_generate_insights = rootView.findViewById(R.id.bt_generate_insights);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe changes in data
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

            }
        });

        return rootView;
    }

    private void displayUserInfo(JSONObject userJSON) throws JSONException {
        // Update UI on the main thread
        Log.d(TAG, "displayUserInfo: " + userJSON.getJSONObject("followers").getInt("total"));

        requireActivity().runOnUiThread(() -> {
            try {
                tv_username.setText(userJSON.getString("display_name"));
                tv_follower_count.setText(String.valueOf(userJSON.getJSONObject("followers").getInt("total")));
                Picasso.get().load(userJSON.getJSONArray("images").getJSONObject(0).getString("url")).into(iv_profile_picture);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showToast(String message) {
        // Show Toast on the main thread
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}
