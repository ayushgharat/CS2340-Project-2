package com.example.spotifywrapper.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Wrapped {

    private JsonArray favoriteArtists;
    private JsonArray previewTracks;

    public void setPreviewTracks(JsonArray previewTracks) {
        this.previewTracks = previewTracks;
    }

    public JsonArray getPreviewTracks() {
        return previewTracks;
    }

    public JsonArray getFavoriteArtists() {
        return favoriteArtists;
    }

    public void setFavoriteArtists(JsonArray favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    public JsonArray getFavoriteTracks() {
        return favoriteTracks;
    }

    public void setFavoriteTracks(JsonArray favoriteTracks) {
        this.favoriteTracks = favoriteTracks;
    }

    public JSONObject getArtistRecommendations() {
        return artistRecommendations;
    }

    public void setArtistRecommendations(JSONObject artistRecommendations) {
        this.artistRecommendations = artistRecommendations;
    }

    public JsonObject getTracksSaved() {
        return tracksSaved;
    }

    public void setTracksSaved(JsonObject tracksSaved) {
        this.tracksSaved = tracksSaved;
    }

    private JsonArray favoriteTracks;
    private JSONObject artistRecommendations;
    private JsonObject tracksSaved;
    private Date savedDate;

    public Date getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(Date savedDate) {
        this.savedDate = savedDate;
    }
}
