package com.example.beacon_making_kotlin;

public class BeaconData{

    private String Name;
    private String UUID;
    private String Major;
    private String Minor;
    private String timeData;
    private String Distance;
    private String Rssi;

    public BeaconData(){

        this.Name = "Null";
        this.UUID = "Null";
        this.Major = "Null";
        this.Minor = "Null";
        this.timeData = "Null";
        this.Distance = "Null";
        this.Rssi = "Null";
    }

    public String getName() {
        return Name;
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


    public void setName(String Name) {
        this.Name = Name;
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

    public void setRssi(String rssi) {
        this.Rssi = rssi;
    }

}