package com.example.spotifywrapped.spotifyAPI.data;

public class Song {
    private String name;
    private String artist;
    private int number;

    public Song() {
    }

    public Song(String name, String artist, int number) {
        this.name = name;
        this.artist = artist;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
