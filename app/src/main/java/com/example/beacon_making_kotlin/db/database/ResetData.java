package com.example.beacon_making_kotlin.db.database;

import android.content.Context;
import android.util.Log;

import com.example.beacon_making_kotlin.db.api.TimetableAPI;
import com.example.beacon_making_kotlin.db.dao.*;
import com.example.beacon_making_kotlin.db.entity.*;
import com.example.beacon_making_kotlin.db.helper.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetData {
    private final StationDao stationDao;
    private final InfoDao infoDao;
    private final TimetableDao timetableDao;
    private final FavoriteDao favoriteDao;
    private final ExecutorService executorService;

    private static final String[] dailyTypeCode = {"01", "03"};
    private static final String[] upDownTypeCode = {"D", "U"};

    public ResetData(Context context) {
        ConsDatabase db = ConsDatabase.getDatabase(context);
        stationDao = db.stationDao();
        infoDao = db.infoDao();
        timetableDao = db.timetableDao();
        favoriteDao = db.favoriteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void populateDatabaseIfEmpty(final Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (stationDao.getAllStations().isEmpty()) {
                String jsonString = JsonHelper.loadJSONFromAsset(context, "stationID.json");
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray stationArray = jsonObject.getJSONArray("station");

                        for (int i = 0; i < stationArray.length(); i++) {
                            JSONObject stationObject = stationArray.getJSONObject(i);

                            String stationID = stationObject.getString("stationID");
                            String stationName = stationObject.getString("stationName");
                            String line = stationObject.getString("line");

                            Station station = new Station(stationID, stationName, line);
                            stationDao.insertStation(station);

                            for (String day : dailyTypeCode) {
                                for (String updown : upDownTypeCode) {
                                    Timetable timetable = new Timetable(stationID, day, updown, TimetableAPI.loadTimetableData(stationID, day, updown));
                                    timetableDao.insertTimetable(timetable);
                                }
                            }

                            Log.d("end", i + "");
                        }
                        Log.d("end", "end");
                    } catch (JSONException | IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        executor.shutdown();
        });
    }
}
