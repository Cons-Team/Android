package com.example.beacon_making_kotlin.db.database;

import com.example.beacon_making_kotlin.db.dao.*;
import com.example.beacon_making_kotlin.db.entity.*;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Coordinate.class, Station.class, Info.class, Timetable.class}, version = 1)
public abstract class ConsDatabase extends RoomDatabase {

    public abstract CoordinateDao coordinateDao();
    public abstract StationDao stationDao();
    public abstract InfoDao infoDao();
    public abstract TimetableDao timetableDao();
    private static volatile ConsDatabase INSTANCE;

    public static ConsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ConsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ConsDatabase.class, "cons_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
