package com.example.beacon_making_kotlin.pathfinding.calculator;

import static java.lang.Math.pow;

import android.util.Log;

import com.example.beacon_making_kotlin.beaconfind.BeaconData;
import com.example.beacon_making_kotlin.pathfinding.dto.BeaconDto;
import com.example.beacon_making_kotlin.pathfinding.service.RetrofitInstance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class DistanceCalculator {
    private final RetrofitInstance retrofitInstance = new RetrofitInstance();
    public static Map<String, BeaconDto> map;

    public String distance(BeaconData[] data){

        // HashMap 형식 으로 비콘 정보 저장
        map = new HashMap<>();

        for(int i = 0; i < data.length; i++){
            retrofitInstance.beaconCoordinate(data[i].getName());
            BeaconDto beaconDto = RetrofitInstance.beaconDto;
            map.put(data[i].getName(), beaconDto);
        }


        double x1, x2, y1, y2, answerX, answerY;

        x1 = x2 = y1 = y2 = answerX = answerY = 0d;

        if(map.size() == 2){
            // 삼각 측량

            Log.d("beacon_name : " + data[0].getName(),"GetX : " + map.get(data[0].getName()).getX());
            Log.d("beacon_name : " + data[0].getName(),"GetY : " + map.get(data[1].getName()).getY());
            Log.d("beacon_name : " + data[1].getName(),"GetX : " + map.get(data[0].getName()).getX());
            Log.d("beacon_name : " + data[1].getName(),"GetY : " + map.get(data[1].getName()).getY());

            // HashMap에서 좌표 꺼내기
            x1 = (double) (map.get(data[0].getName()).getX());
            x2 = (double) (map.get(data[1].getName()).getX());
            y1 = (double) (map.get(data[0].getName()).getY());
            y2 = (double) (map.get(data[1].getName()).getY());

            // 각 비콘 으로 부터 사용자 까지의 거리 계산
            double distanceA, distanceB;

            distanceA = pow(10, (double) (-50 - Integer.parseInt(data[0].getRssi())) / (10 * 2));
            distanceB = pow(10, (double) (-50 - Integer.parseInt(data[1].getRssi())) / (10 * 2));


            //
            if(x1 == x2)
                answerY = (distanceB * (y2 - y1)) / (distanceA + distanceB);
            else
                answerX = (distanceB * (x2 - x1)) / (distanceA + distanceB);

        }
        else{
            // 삼변 측량
        }

        if(x1 == x2)
            return x1 + ", 0, " + (y1 + answerY);
        else
            return (x1 + answerX) + ", 0, " + y1;
    }
}
