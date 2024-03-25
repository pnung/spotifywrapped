package com.example.spotifywrapped.spotifyAPI.data;

public class Artist {
    private String name;
    private String imageUrl;

    public Artist() {
        // Default constructor required for calls to DataSnapshot.getValue(Artist.class)
    }

    public Artist(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
