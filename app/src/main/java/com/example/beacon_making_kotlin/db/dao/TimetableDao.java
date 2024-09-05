package com.example.beacon_making_kotlin.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.beacon_making_kotlin.db.entity.Timetable;

import java.util.List;

@Dao
public interface TimetableDao {
    @Insert
    void insertTimetable(Timetable timetable);

    @Query("SELECT * FROM timetable")
    List<Timetable> getAllTimetables();

}
