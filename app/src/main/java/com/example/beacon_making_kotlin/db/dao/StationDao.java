package com.example.beacon_making_kotlin.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.beacon_making_kotlin.db.entity.Station;

import java.util.List;

@Dao
public interface StationDao {
    @Insert
    void insertStation(Station station);

    @Query("SELECT * FROM station")
    List<Station> getAllStations();

}

