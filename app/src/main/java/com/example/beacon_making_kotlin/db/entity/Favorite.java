package com.example.beacon_making_kotlin.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite", foreignKeys = @ForeignKey(entity = Station.class, parentColumns = "stationID", childColumns = "stationID"), indices = {@Index(value = {"stationID"})})
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    private int favoriteID;

    @NonNull
    private String stationID;

    private Favorite() {}

    public Favorite(@NonNull String stationID) {
        this.stationID = stationID;
    }

    public int getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(int favoriteID) {
        this.favoriteID = favoriteID;
    }

    @NonNull
    public String getStationID() {
        return stationID;
    }

    public void setStationID(@NonNull String stationID) {
        this.stationID = stationID;
    }

}