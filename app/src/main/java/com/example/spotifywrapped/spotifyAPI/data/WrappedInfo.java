package com.example.spotifywrapped.spotifyAPI.data;

import java.util.List;

public class WrappedInfo {
    public List<String> topSongs;
    public List<String> topArtists;
    public List<String> topGenres;

    // No-argument constructor needed for Firebase deserialization
    public WrappedInfo() {
    }

    public WrappedInfo(List<String> topSongs, List<String> topArtists, List<String> topGenres) {
        this.topSongs = topSongs;
        this.topArtists = topArtists;
        this.topGenres = topGenres;
    }
}
