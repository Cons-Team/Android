package com.example.beacon_making_kotlin.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "info", foreignKeys = @ForeignKey(entity = Station.class, parentColumns = "stationID", childColumns = "stationID"), indices = {@Index(value = {"stationID"})})
public class Info {
    @PrimaryKey(autoGenerate = true)
    private int infoID;

    @NonNull
    private String stationID;

    private String address;

    private String tel;

    public Info() {}

    @Ignore
    public Info(@NonNull String stationID, String address, String tel) {
        this.stationID = stationID;
        this.address = address;
        this.tel = tel;
    }

    public int getInfoID() {
        return infoID;
    }

    public void setInfoID(int infoID) {
        this.infoID = infoID;
    }

    @NonNull
    public String getStationID() {
        return stationID;
    }

    public void setStationID(@NonNull String stationID) {
        this.stationID = stationID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
