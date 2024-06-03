package com.example.beacon_making_kotlin.pathfinding.calculator;

import com.example.beacon_making_kotlin.beaconfind.BeaconData;

public class FloorCalculator {

    private String floor;
    public String floorCalculator(BeaconData beaconData){
        String minor = beaconData.getMinor();
        if(minor.charAt(1) == '0'){
            floor = "B" + minor.substring(1, floor.length() - 1);
        }
        else{
            floor = minor.substring(0, floor.length() - 1);
        }

        return floor;
    }
}