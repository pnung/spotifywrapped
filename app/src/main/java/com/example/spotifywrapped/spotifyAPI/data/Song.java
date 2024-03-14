package com.example.spotifywrapped.spotifyAPI.data;

public class Song {

// class uses information from request:
// https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=5&offset=0
// top 5 songs
    private String name;
    private String artist;
    private int number;

    public Song(String name, String artist, int number) {
        this.name = name;
        this.artist = artist;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }
}

