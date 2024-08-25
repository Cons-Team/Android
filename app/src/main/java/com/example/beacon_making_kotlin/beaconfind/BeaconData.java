package com.example.beacon_making_kotlin.beaconfind;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BeaconData{

    private String Name;
    private String UUID;
    private String Major;
    private String Minor;
    private int Rssi;
}