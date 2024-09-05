package com.example.beacon_making_kotlin.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.beacon_making_kotlin.db.entity.Info;

import java.util.List;

@Dao
public interface InfoDao {
    @Insert
    void insertInfo(Info info);

    @Query("SELECT * FROM info")
    List<Info> getAllInfos();

}
