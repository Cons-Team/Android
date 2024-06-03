package com.example.beacon_making_kotlin.pathfinding.calculator;

import com.example.beacon_making_kotlin.beaconfind.BeaconData;

public class FloorCalculator {

    public String floorCalculator(BeaconData beaconData){
        String minor = beaconData.getMinor();

        String floor = "";
        if(minor.charAt(1) == '0'){
            floor = "B" + minor.charAt(0) + "F";
        }
        else{
            floor = minor.charAt(0) + "F";
        }

        return floor;
    }
}