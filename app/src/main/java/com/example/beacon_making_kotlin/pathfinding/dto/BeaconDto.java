package com.example.beacon_making_kotlin.pathfinding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeaconDto {
    private int x;
    private int y;

    @Override
    public String toString(){
        // json 반환 형식(자세한건 자료 링크 확인)
        return "beacon{" +
                "x = " + x +
                ", y = " + y +
                "}";
    }
}
