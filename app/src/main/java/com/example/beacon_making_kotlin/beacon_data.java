package com.example.beacon_making_kotlin;

public class beacon_data {

    private String UUID;
    private String Major;
    private String Minor;
    private String timeData;
    private String Distance;
    private String Rssi;

    public void beacondata(){

        // Beacon data
        this.UUID = "NULL";
        this.Major = "NULL";
        this.Minor = "NULL";
        this.timeData = "NULL";
        this.Distance = "NULL";
        this.Rssi = "NULL";
    }

    public String getUUID() {
        return UUID;
    }

    public String getMajor() {
        return Major;
    }

    public String getMinor() {
        return Minor;
    }

    public String getTimeData() {
        return timeData;
    }

    public String getDistance() {
        return Distance;
    }

    public String getRssi() {
        return Rssi;
    }


    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setMajor(String major) {
        this.Major = major;
    }

    public void setMinor(String minor) {
        this.Minor = minor;
    }

    public void setTimeData(String timeData) {
        this.timeData = timeData;
    }

    public void setDistance(String distance) {
        this.Distance = distance;
    }

    public void setRssi(String rssi) { this.Rssi = rssi; }
}
