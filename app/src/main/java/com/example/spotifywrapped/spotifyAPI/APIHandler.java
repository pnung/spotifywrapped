package com.example.spotifywrapped.spotifyAPI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIHandler {

    public static final String CLIENT_ID = "eb741705a92544428bf927e8a7e27814";
    public static final String REDIRECT_URI = "spotifywrapped://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static String mAccessToken, mAccessCode;
    private static Call mCall;



    // PUBLIC INTERFACE METHODS

    /**
     * Generic method for making request to a server and asynchronously handling response.
     * @param requestURL url request to api server
     * @param callingActivity activity from which request is being made (use getActivity())
     * @param responsePropagator interface that propagates response into calling activity
     */
    public static void makeRequest(String requestURL, Activity callingActivity, ResponsePropagator<JSONObject> responsePropagator) {
        if (mAccessToken == null) {
            callingActivity.runOnUiThread( () -> Toast.makeText(callingActivity, "Request failed, no access token", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url(requestURL)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callingActivity.runOnUiThread( () -> Toast.makeText(callingActivity, "HTTP request failed", Toast.LENGTH_SHORT).show()
                );
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBodyString = response.body().string();
                    JSONObject JSONresult = new JSONObject(responseBodyString);
                    callingActivity.runOnUiThread(() -> responsePropagator.propagateResponse(JSONresult));
                } catch (Exception e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public static void getToken(Activity callingActivity) { // GET USER TOKEN, PROMPTS USER TO LOGIN
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(callingActivity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode(Activity callingActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(callingActivity, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * Handles onActivityResult, which occurs when an activity returns from accessing something
     * external, in this case Spotify's login service
     * @param requestCode code used to request service
     * @param resultCode code returned by service
     * @param data data returned by service?
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            System.out.println("accessToken: " + mAccessToken);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            System.out.println("accessCode: " + mAccessCode);
        }
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private static void setTextAsync(final String text, TextView textView, Activity callingActivity) {
        callingActivity.runOnUiThread(() -> textView.setText(text));
    }





    // PRIVATE HELPER METHODS

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private static AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read", "user-read-recently-played" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }


    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private static Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private static void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    /**
     * Interface for propagating response from onResponse in http callback. When http request is
     * fulfilled, propagateResponse() will be called and whatever code is specified there
     * will be run.
     */
    public interface ResponsePropagator<T> {
        void propagateResponse(T responseResult);
    }

}
