package com.example.beacon_making_kotlin.MetroMap;

import java.util.ArrayList;

public class Transfer_Group {

    String name;
    boolean departure;
    ArrayList<String> via_list;
    String lineInfo;
    String lineColor;
    String time;
    String door_dire;
    String get_off;

    public Transfer_Group(String name, Boolean departure, String lineInfo, String lineColor){
        this.name = name;
        this.departure = departure;
        this.lineInfo = lineInfo;
        this.lineColor = lineColor;
        via_list = new ArrayList<>();
    }

    public Transfer_Group(String name, Boolean departure, String lineInfo, String lineColor, String time, String door_dire, String get_off){
        this.name = name;
        this.departure = departure;
        this.lineInfo = lineInfo;
        this.lineColor = lineColor;
        this.time = time;
        this.door_dire = door_dire;
        this.get_off = get_off;
    }
}
