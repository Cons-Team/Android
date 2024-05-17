package com.example.beacon_making_kotlin;

import static java.lang.Math.pow;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DistanceCalculator {

    public String distance(BeaconData[] data){
        Arrays.sort(data, new Comparator<BeaconData>() {
            @Override
            public int compare(BeaconData o1, BeaconData o2) {
                return o2.getRssi().compareTo(o1.getRssi());
            }
        });

        Map<String, Integer> map = new HashMap<>();

        map.put("Holy-IOT-65", 1090);
        map.put("Holy-IOT-f7", 9090);
        map.put("Holy-IOT-73", 1010);
        map.put("Holy-IOT-37", 9010);

        Double x1, x2, z1, z2, answerX, answerZ;

        x1 = x2 = z1 = z2 = answerX = answerZ = 0d;

        if(data.length == 2){
            // 삼각 측량

            // 좌표 가져 오는 부분
            x1 = (double) (map.get(data[0].getName()) / 100);
            x2 = (double) (map.get(data[1].getName()) / 100);
            z1 = (double) (map.get(data[0].getName()) % 100);
            z2 = (double) (map.get(data[1].getName()) % 100);

            // 각 비콘 으로 부터 사용자 까지의 거리 계산
            Double distanceA = pow(10, (-50 - Integer.parseInt(data[0].getRssi())) / 10 * 2);
            Double distanceB = pow(10, (-50 - Integer.parseInt(data[1].getRssi())) / 10 * 2);

            //
            if(x1 == x2)
                answerZ = (distanceB * (z2 - z1)) / (distanceA + distanceB);
            else
                answerX = (distanceB * (x2 - x1)) / (distanceA + distanceB);

        }
        else{
            // 삼변 측량
        }

        if(x1 == x2)
            return x1 + ", 0, " + (z1 + answerZ);
        else
            return (x1 + answerX) + ", 0, " + z1;
    }
}
