package com.example.beacon_making_kotlin.db.dao;

import com.example.beacon_making_kotlin.db.entity.Info;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface InfoDao {
    @Insert
    void insertInfo(Info info);

    @Query("SELECT * FROM info where stationID = :id")
    List<Info> getInfo(String id);

}
