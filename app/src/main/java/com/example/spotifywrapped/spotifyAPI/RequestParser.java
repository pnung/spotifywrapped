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

    // Method to request and parse top artists from the Spotify API.
    public static void topArtistsRequest(Activity callingActivity, APIHandler.ResponsePropagator<List<Artist>> responsePropagator) {
        // Perform the API request to get the user's top artists with a specific time range and limit.
        makeRequest("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=5", callingActivity, JSONResult -> {
            try {
                // Parse the JSON response to extract artist details.
                JSONArray arr = JSONResult.getJSONArray("items");
                List<Artist> artists = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject artistJson = arr.getJSONObject(i);
                    // Extract artist name, image URL, and genres.
                    String name = artistJson.getString("name");
                    String imageUrl = artistJson.getJSONArray("images").getJSONObject(0).getString("url");
                    JSONArray genreArray = artistJson.getJSONArray("genres");
                    List<String> genres = new ArrayList<>();
                    for (int j = 0; j < genreArray.length(); j++) {
                        genres.add(genreArray.getString(j));
                    }
                    // Add the artist to the list of artists.
                    artists.add(new Artist(name, imageUrl, genres));
                }
                // Propagate the list of artists to the response handler.
                responsePropagator.propagateResponse(artists);
            } catch (JSONException e) {
                // Log an error if there's an issue with JSON parsing.
                System.out.println("topArtistsRequest FAILED - JSONException: " + e.getMessage());
            }
        });
    }

    // Method to fetch and rank the user's top genres based on their top artists.
    public static void fetchTopGenresBasedOnTopArtists(Activity callingActivity, APIHandler.ResponsePropagator<List<String>> responsePropagator) {
        // First, fetch the top artists to determine top genres.
        topArtistsRequest(callingActivity, artistsList -> {
            try {
                Map<String, Integer> genreCount = new HashMap<>();
                for (Artist artist : artistsList) {
                    // Count the occurrence of each genre across all top artists.
                    for (String genre : artist.getGenres()) {
                        genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                    }
                }
                // Sort genres by frequency and extract the top genres.
                List<String> topGenres = genreCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(5)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                // Propagate the list of top genres to the response handler.
                responsePropagator.propagateResponse(topGenres);
            } catch (Exception e) {
                // Handle any exceptions.
                e.printStackTrace();
            }
        });
    }


    // parsing top song request
    public static void songsRequest(Activity callingActivity, APIHandler.ResponsePropagator<List<Song>> responsePropagator) {
        makeRequest("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=5", callingActivity, JSONResult -> {
            try {
                JSONArray arr = JSONResult.getJSONArray("items");
                List<Song> songs = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject songJson = arr.getJSONObject(i);
                    String name = songJson.getString("name");
                    String artist = songJson.getJSONArray("artists").getJSONObject(0).getString("name");
                    // Assuming 'number' is not needed, or handle accordingly
                    songs.add(new Song(name, artist, i)); // Use actual index or another identifier as needed
                }
                responsePropagator.propagateResponse(songs);
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
