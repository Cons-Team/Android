package com.example.beacon_making_kotlin.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "timetable", foreignKeys = @ForeignKey(entity = Station.class, parentColumns = "stationID", childColumns = "stationID"), indices = {@Index(value = {"stationID"})})
public class Timetable {
    @PrimaryKey(autoGenerate = true)
    public int timetableID;

    @NonNull
    public String stationID;

    @NonNull
    public String day;

    @NonNull
    public String updown;

    @NonNull
    public String time;

    public Timetable() {}

    @Ignore
    public Timetable(@NonNull String stationID, @NonNull String day, @NonNull String updown, @NonNull String time) {
        this.stationID = stationID;
        this.day = day;
        this.updown = updown;
        this.time = time;
    }

    public int getTimetableID() {
        return timetableID;
    }

    public void setTimetableID(int timetableID) {
        this.timetableID = timetableID;
    }

    @NonNull
    public String getStationID() {
        return stationID;
    }

    public void setStationID(@NonNull String stationID) {
        this.stationID = stationID;
    }

    @NonNull
    public String getDay() {
        return day;
    }

    public void setDay(@NonNull String day) {
        this.day = day;
    }

    @NonNull
    public String getUpdown() {
        return updown;
    }

    public void setUpdown(@NonNull String updown) {
        this.updown = updown;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

}
