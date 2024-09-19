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
}
