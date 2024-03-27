package com.example.spotifywrapped.spotifyAPI.data;

import java.util.List;

public class Wrap {
    private String id;
    private List<Song> topSongs;
    private List<Artist> topArtists;
    private List<String> topGenres;

    // No-argument constructor required for Firebase
    public Wrap() {
    }

    // Constructor with all arguments
    public Wrap(String id, List<Song> topSongs, List<Artist> topArtists, List<String> topGenres) {
        this.id = id;
        this.topSongs = topSongs;
        this.topArtists = topArtists;
        this.topGenres = topGenres;
    }

    // Constructor without id (useful for creating new instances before getting an ID from Firebase)
    public Wrap(List<Song> topSongs, List<Artist> topArtists, List<String> topGenres) {
        this.topSongs = topSongs;
        this.topArtists = topArtists;
        this.topGenres = topGenres;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Song> getTopSongs() {
        return topSongs;
    }

    public void setTopSongs(List<Song> topSongs) {
        this.topSongs = topSongs;
    }

    public List<Artist> getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(List<Artist> topArtists) {
        this.topArtists = topArtists;
    }

    public List<String> getTopGenres() {
        return topGenres;
    }

    public void setTopGenres(List<String> topGenres) {
        this.topGenres = topGenres;
    }
}
