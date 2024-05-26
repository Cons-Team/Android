package com.example.beacon_making_kotlin.pathfinding.calculator;

import com.example.beacon_making_kotlin.beaconfind.BeaconData;

public class FloorCalculator {

    private String floor;
    public String floorCalculator(BeaconData beaconData){
        String minor = beaconData.getMinor();
        if(minor.substring(1, 2) == "0"){
            floor = "B" + minor.substring(0, 1);
        }
        else{
            floor = minor.substring(0, 1);
        }

        return floor;
    }
}
