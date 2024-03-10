package com.example.spotifywrapped.spotifyAPI;

import static com.example.spotifywrapped.spotifyAPI.APIHandler.makeRequest;

import android.app.Activity;

import org.json.JSONObject;

public class RequestParser {

    // parsing top song request
    public static void testRequest(Activity callingActivity, APIHandler.ResponsePropagator<String[]> responsePropagator) {
        makeRequest("https://api.spotify.com/v1/me", callingActivity, (JSONResult) -> {
            String JSONstring = JSONResult.toString();
            String[] arr = new String[]{JSONstring};
            responsePropagator.propagateResponse(arr);
        });
    }

    // parsing user holiday songs


    // parsing user general info

}
