package com.example.beacon_making_kotlin.MetroMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metro_time_info {
    private String metro_name;

    private String metro_name_left = "";
    private String destination_left = "               ";
    private String time_left = "";
    private String btrainSttus_left = "";
    private String barvlDt_left = "";

    private String metro_name_right = "";
    private String destination_right = "               ";
    private String time_right = "";
    private String btrainSttus_right = "";
    private String barvlDt_right = "";
    public Metro_time_info(){}
}
