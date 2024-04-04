package com.example.spotifywrapper.model;

import org.json.JSONObject;

public class Wrapped {

    private JSONObject favoriteArtists;

    public JSONObject getFavoriteArtists() {
        return favoriteArtists;
    }

    public void setFavoriteArtists(JSONObject favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    public JSONObject getFavoriteTracks() {
        return favoriteTracks;
    }

    public void setFavoriteTracks(JSONObject favoriteTracks) {
        this.favoriteTracks = favoriteTracks;
    }

    public JSONObject getArtistRecommendations() {
        return artistRecommendations;
    }

    public void setArtistRecommendations(JSONObject artistRecommendations) {
        this.artistRecommendations = artistRecommendations;
    }

    public JSONObject getTracksSaved() {
        return tracksSaved;
    }

    public void setTracksSaved(JSONObject tracksSaved) {
        this.tracksSaved = tracksSaved;
    }

    private JSONObject favoriteTracks;
    private JSONObject artistRecommendations;
    private JSONObject tracksSaved;


}
