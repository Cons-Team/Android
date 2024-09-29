package com.example.beacon_making_kotlin.db.dao;

import com.example.beacon_making_kotlin.db.entity.Station;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStation(Station station);

    @Query("SELECT * FROM station")
    List<Station> getAllStations();

    @Query("SELECT stationID FROM station where stationName = :name")
    List<String> getStationID(String name);

}

