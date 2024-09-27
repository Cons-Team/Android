package com.example.beacon_making_kotlin.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.beacon_making_kotlin.db.entity.Coordinate;

import java.util.List;

@Dao
public interface CoordinateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoordinate(Coordinate coordinate);

    @Query("SELECT * FROM coordinate")
    List<Coordinate> getAllCoordinates();

    @Query("SELECT name FROM coordinate WHERE :x >= x1 AND :x <= x2 AND :y >= y1 AND :y <= y2")
    List<String> getStationName(int x, int y);
}
