package com.example.spotifywrapper.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.spotifywrapper.MainActivity;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

// SpotifyAuthorizationManager.java
public class SpotifyAuthorizationManager {

    public static final String CLIENT_ID = "68de614511f343f1915588825ec74154";
    public static final String REDIRECT_URI = "spotifywrapper://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private String mAccessToken;

    public SpotifyAuthorizationManager() {
        // Default constructor
    }

    public void requestAccessToken(Activity activity, AuthorizationCallback callback) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request);

        // Pass the callback to handle the result
        callback.onAuthorizationStarted();
    }

    public void requestAccessCode(Activity activity, AuthorizationCallback callback) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    public void handleAuthorizationResult(int requestCode, int resultCode, Intent data, AuthorizationCallback callback) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            // Handle the access token, for example, pass it to the callback.
            callback.onAuthorizationCompleted(mAccessToken);
        } else {
            // Handle other cases if needed
            callback.onAuthorizationFailed(response.getError());
        }
    }

    // Callback interface to handle authorization results
    public interface AuthorizationCallback {
        void onAuthorizationStarted();
        void onAuthorizationCompleted(String accessToken);
        void onAuthorizationFailed(String errorMessage);
    }
}

