package com.example.spotifywrapper.fragments.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.spotifywrapper.HomeActivity;
import com.example.spotifywrapper.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashScreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "SplashScreenFragment";
    private static final String ARG_PARAM2 = "param2";
    private TextView tokenTextView, codeTextView, profileTextView;
    private Button signUpButton;
    public static final String CLIENT_ID = "68de614511f343f1915588825ec74154";
    public static final String REDIRECT_URI = "spotifywrapper://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplashScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashScreenFragment newInstance(String param1, String param2) {
        SplashScreenFragment fragment = new SplashScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_splash_screen, container, false);

        // Initialize the views
        tokenTextView = rootview.findViewById(R.id.token_text_view);

        // Initialize the buttons
        Button tokenBtn = rootview.findViewById(R.id.token_btn);
        signUpButton = rootview.findViewById(R.id.sign_up_button);

        // Set the click listeners for the buttons

        tokenBtn.setOnClickListener((v) -> {
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // Replace the current fragment with the destination fragment
            transaction.replace(R.id.fragment_container_login, loginFragment);

            // Add the transaction to the back stack (optional, allows going back)
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment destinationFragment = new SignUpFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

                // Replace the current fragment with the destination fragment
                transaction.replace(R.id.fragment_container_login, destinationFragment);

                // Add the transaction to the back stack (optional, allows going back)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return rootview;
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(requireActivity(), AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            // setTextAsync(mAccessToken, tokenTextView);

            Intent intent = new Intent(requireActivity(), HomeActivity.class);
            Log.d(TAG, "onActivityResult: accesstoken:" + mAccessToken);
            intent.putExtra("token", mAccessToken);
            startActivity(intent);

        }
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}