package com.example.spotifywrapped.spotifyAPI;

import static com.example.spotifywrapped.spotifyAPI.APIHandler.makeRequest;
import com.example.spotifywrapped.spotifyAPI.data.*;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParser {

    // In RequestParser.java
    public static void topArtistsRequest(Activity callingActivity, APIHandler.ResponsePropagator<JSONArray> responsePropagator) {
        makeRequest("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=5", callingActivity, JSONResult -> {
            try {
                JSONArray arr = JSONResult.getJSONArray("items");
                responsePropagator.propagateResponse(arr);
            } catch (JSONException e) {
                System.out.println("topArtistsRequest FAILED - JSONException: " + e.getMessage());
            }
        });
    }

    public static void fetchTopGenresBasedOnTopArtists(Activity callingActivity, APIHandler.ResponsePropagator<List<String>> responsePropagator) {
        topArtistsRequest(callingActivity, artistJSONArray -> {
            try {
                Map<String, Integer> genreCount = new HashMap<>();
                for (int i = 0; i < artistJSONArray.length(); i++) {
                    JSONObject artist = artistJSONArray.getJSONObject(i);
                    JSONArray genres = artist.getJSONArray("genres");
                    for (int j = 0; j < genres.length(); j++) {
                        String genre = genres.getString(j);
                        genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                    }
                }

                // Sort genres by frequency and pick the top ones
                List<String> topGenres = genreCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(5) // Adjust the limit as needed
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                responsePropagator.propagateResponse(topGenres);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle error
            }
        });
    }

    // parsing top song request
    public static void songsRequest(Activity callingActivity, APIHandler.ResponsePropagator<String[]> responsePropagator) {
        makeRequest("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=5&offset=0", callingActivity, (JSONResult) -> {

            try {
                JSONArray arr = JSONResult.getJSONArray("items");

                ArrayList<Song> songArrList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {
                    String name = arr.getJSONObject(i).getString("name");
                    String artist = arr.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
                    Song object = new Song(name, artist, i);
                    songArrList.add(object);
                }

                Song[] songArr = new Song[songArrList.size()];
                songArr = songArrList.toArray(songArr);


                //just testing out parsing
                String[] stringArr = new String[songArr.length];
                for (int i = 0; i < songArr.length; i++) {
                    stringArr[i] = songArr[i].getName();
                }

                responsePropagator.propagateResponse(stringArr);

            } catch (JSONException e) {
                System.out.println("songsRequest FAILED - JSONException: " + e.getMessage());
            }
        });
    }

    //parsing music history (last 10 songs)
    public static void lastPlayedSongs(Activity callingActivity, APIHandler.ResponsePropagator<String[]> responsePropagator) {
        makeRequest("https://api.spotify.com/v1/me/player/recently-played?before=" + System.currentTimeMillis() , callingActivity, (JSONResult) -> {

            try {

                JSONArray arr = JSONResult.getJSONArray("items");
                System.out.println(arr);

                ArrayList<Song> songArrList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {
                    String name = arr.getJSONObject(i).getJSONObject("track").getString("name");

                    Song object = new Song(name, "testing", 1);
                    songArrList.add(object);
                }

                Song[] songArr = new Song[songArrList.size()];
                songArr = songArrList.toArray(songArr);


                //just testing out parsing
                String[] stringArr = new String[songArr.length];
                for (int i = 0; i < songArr.length; i++) {
                    stringArr[i] = songArr[i].getName();
                }

                responsePropagator.propagateResponse(stringArr);

            } catch (JSONException e) {
                System.out.println("lastPlayedSongs FAILED - JSON Exception: " + e.getMessage());
            }

        });
    }

    // parsing user holiday songs


    // parsing user general info

}
