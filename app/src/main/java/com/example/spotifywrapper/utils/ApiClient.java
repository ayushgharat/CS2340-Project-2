package com.example.spotifywrapper.utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * This class creates an instance of the ApiClient, which will be used to make API calls to spotify
 * to retrieve information from their servers
 */

public class ApiClient {

    private final OkHttpClient httpClient;

    public ApiClient() {
        httpClient = new OkHttpClient();
    }
    public void getTrackDetails(String token, String trackId, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/tracks/" + trackId)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public void getUserProfile(String token, Callback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call apiCall = httpClient.newCall(request);
        apiCall.enqueue(callback);
    }

    public void getFavoriteArtists(String token, Callback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call apiCall = httpClient.newCall(request);
        apiCall.enqueue(callback);
    }

    public void getFavoriteTracks(String token, Callback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call apiCall = httpClient.newCall(request);
        apiCall.enqueue(callback);
    }

    public void getPopularLikedSong(String token, Callback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/tracks?limit=50")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call apiCall = httpClient.newCall(request);
        apiCall.enqueue(callback);
    }
    public void cancelCall(Call call) {
        if (call != null) {
            call.cancel();
        }
    }
}
