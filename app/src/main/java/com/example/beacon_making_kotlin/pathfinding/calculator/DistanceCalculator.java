package com.example.beacon_making_kotlin.pathfinding.calculator;

import static java.lang.Math.pow;

import android.util.Log;
import android.util.Pair;

import com.example.beacon_making_kotlin.beaconfind.BeaconData;
import com.example.beacon_making_kotlin.pathfinding.dto.BeaconDto;
import com.example.beacon_making_kotlin.pathfinding.service.RetrofitInstance;

import java.util.List;


public class DistanceCalculator {
    private final RetrofitInstance retrofitInstance = new RetrofitInstance();

    public String distance(BeaconData[] data){

        for (BeaconData beaconData : data) {
            retrofitInstance.beaconCoordinate(beaconData);
        }

        List<Pair<String, BeaconDto>> beaconDto = RetrofitInstance.beaconDto;

        int a = -50; // 1m당 RSSI 신호값
        double n = 2; // 손실지수 (2 ~ 4)
        if(!beaconDto.isEmpty()) {
            if (beaconDto.size() == 2) {

                double x1, x2, y1, y2, answerX, answerY;

                x1 = x2 = y1 = y2 = answerX = answerY = 0;

                // 삼각 측량
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetX : " + beaconDto.get(0).second.getX());
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetY : " + beaconDto.get(0).second.getY());
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetRssi : " + beaconDto.get(0).second.getRssi());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetX : " + beaconDto.get(1).second.getX());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetY : " + beaconDto.get(1).second.getY());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetRssi : " + beaconDto.get(1).second.getRssi());

                // HashMap에서 좌표 꺼내기
                x1 = (double) (beaconDto.get(0).second.getX());
                y1 = (double) (beaconDto.get(0).second.getY());
                x2 = (double) (beaconDto.get(1).second.getX());
                y2 = (double) (beaconDto.get(1).second.getY());

                // 각 비콘 으로 부터 사용자 까지의 거리 계산
                double distanceA, distanceB;

                distanceA = pow(10, (double) (a - Integer.parseInt(beaconDto.get(0).second.getRssi())) / (10 * n));
                distanceB = pow(10, (double) (a - Integer.parseInt(beaconDto.get(1).second.getRssi())) / (10 * n));

                RetrofitInstance.beaconDto.clear();


                // 좌표 반환

                if (x1 == x2) {
                    answerY = (distanceA * (y2 - y1)) / (distanceA + distanceB);
                    Log.d("반환 좌표", x1 + "," + (y1 + answerY));
                    return x1 + "," + (y1 + answerY);
                } else {
                    answerX = (distanceA * (x2 - x1)) / (distanceA + distanceB);
                    Log.d("반환 좌표", (x1 + answerX) + "," + y1);
                    return (x1 + answerX) + "," + y1;
                }

//            Log.d("x좌표 거리 비율", "" + answerX);
//            Log.d("y좌표 거리 비율", "" + answerY);


            } else if(beaconDto.size() >= 3) {
                // 삼변 측량
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetX : " + beaconDto.get(0).second.getX());
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetY : " + beaconDto.get(0).second.getY());
                Log.d("beacon_name : " + beaconDto.get(0).first, "GetRssi : " + beaconDto.get(0).second.getRssi());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetX : " + beaconDto.get(1).second.getX());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetY : " + beaconDto.get(1).second.getY());
                Log.d("beacon_name : " + beaconDto.get(1).first, "GetRssi : " + beaconDto.get(1).second.getRssi());
                Log.d("beacon_name : " + beaconDto.get(2).first, "GetX : " + beaconDto.get(2).second.getX());
                Log.d("beacon_name : " + beaconDto.get(2).first, "GetY : " + beaconDto.get(2).second.getY());
                Log.d("beacon_name : " + beaconDto.get(2).first, "GetRssi : " + beaconDto.get(2).second.getRssi());

                double x1, y1, d1;
                x1 = beaconDto.get(0).second.getX();
                y1 = beaconDto.get(0).second.getY();
                d1 = pow(10, (double) (a - Integer.parseInt(beaconDto.get(0).second.getRssi())) / (10 * n));

                double x2, y2, d2;
                x2 = beaconDto.get(1).second.getX();
                y2 = beaconDto.get(1).second.getY();
                d2 = pow(10, (double) (a - Integer.parseInt(beaconDto.get(1).second.getRssi())) / (10 * n));

                double x3, y3, d3;
                x3 = beaconDto.get(2).second.getX();
                y3 = beaconDto.get(2).second.getY();
                d3 = pow(10, (double) (a - Integer.parseInt(beaconDto.get(2).second.getRssi())) / (10 * n));


                double S = (pow(x3, 2) - pow(x2, 2) + pow(y3, 2) - pow(y2, 2) + pow(d2, 2) - pow(d3, 2)) / 2.0;
                double T = (pow(x1, 2) - pow(x2, 2) + pow(y1, 2) - pow(y2, 2) + pow(d2, 2) - pow(d1, 2)) / 2.0;


                double y = ((T * (x2 - x3)) - (S * (x2 - x1))) / (((y1 - y2) * (x2 - x3)) - ((y3 - y2) * (x2 - x1)));
                double x = ((y * (y1 - y2)) - T) / (x2 - x1);


                String returnCoordinate = x + "," + y + "," + 0;
                Log.d("삼변 반환 좌표", returnCoordinate);

                return returnCoordinate;
            }

        }
        return "0,0,0";
    }
}

