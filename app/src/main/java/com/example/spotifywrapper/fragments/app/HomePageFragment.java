package com.example.spotifywrapper.fragments.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapper.R;
import com.example.spotifywrapper.utils.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePageFragment extends Fragment {

    private Call mCall;
    private TextView tv_username;
    private String token;
    private ApiClient apiClient;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(String token) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString("token");
        }
        apiClient = new ApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        tv_username = rootView.findViewById(R.id.tv_user_name);

        // Fetch and display user information
        getUserProfile();

        return rootView;
    }

    private void getUserProfile() {
        apiClient.getUserProfile(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                showToast("Failed to fetch data, watch Logcat for more details");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject userJSON = new JSONObject(response.body().string());
                    displayUserInfo(userJSON);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    showToast("Failed to parse data, watch Logcat for more details");
                }
            }
        });
    }

    private void displayUserInfo(JSONObject userJSON) throws JSONException {
        // Update UI on the main thread
        requireActivity().runOnUiThread(() -> {
            try {
                tv_username.setText(userJSON.getString("display_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showToast(String message) {
        // Show Toast on the main thread
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        apiClient.cancelCall(mCall);
        super.onDestroy();
    }
}
