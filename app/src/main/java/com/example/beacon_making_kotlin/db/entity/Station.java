package com.example.beacon_making_kotlin.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "station")
public class Station {
    @PrimaryKey
    @NonNull
    private String stationID;

    @NonNull
    private String stationName;

    @NonNull
    private String line;

    public Station() {}

    @Ignore
    public Station(@NonNull String stationID, @NonNull String stationName, @NonNull String line) {
        this.stationID = stationID;
        this.stationName = stationName;
        this.line = line;
    }

    @NonNull
    public String getStationID() {
        return stationID;
    }

    public void setStationID(@NonNull String stationID) {
        this.stationID = stationID;
    }

    @NonNull
    public String getStationName() {
        return stationName;
    }

    public void setStationName(@NonNull String stationName) {
        this.stationName = stationName;
    }

    @NonNull
    public String getLine() {
        return line;
    }

    public void setLine(@NonNull String line) {
        this.line = line;
    }

}
