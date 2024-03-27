package com.example.spotifywrapped.spotifyAPI.data;

import java.util.List;

public class Artist {
    private String name;
    private String imageUrl;
    private List<String> genres;

    public Artist() {
    }

    public Artist(String name, String imageUrl, List<String> genres) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.genres = genres;
    }

    // Getters and setters
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

    public List<String> getGenres() { // Getter for genres
        return genres;
    }

    public void setGenres(List<String> genres) { // Setter for genres
        this.genres = genres;
    }
}
