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

    private final CoordinateDao coordinateDao;
    private final StationDao stationDao;
    private final InfoDao infoDao;
    private final TimetableDao timetableDao;
    private final FavoriteDao favoriteDao;
    private final ExecutorService executorService;

    private static final String[] dailyTypeCode = {"01", "03"};
    private static final String[] upDownTypeCode = {"D", "U"};

    public ResetData(Context context) {
        ConsDatabase db = ConsDatabase.getDatabase(context);
        coordinateDao = db.coordinateDao();
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
                coordinateReset(context);
                stationReset(context);
            }
        executor.shutdown();
        });
    }

    private void coordinateReset(Context context){
        String jsonString1 = JsonHelper.loadJSONFromAsset(context, "coordinate.json");
        if (jsonString1 != null) {
            try {
                JSONObject jsonObject1 = new JSONObject(jsonString1);
                JSONArray coordinateArray = jsonObject1.getJSONArray("coordinate");

                for (int i = 0; i < coordinateArray.length(); i++) {
                    JSONObject coordinateObject = coordinateArray.getJSONObject(i);

                    String name = coordinateObject.getString("name");
                    int x1 = coordinateObject.getInt("x1");
                    int y1 = coordinateObject.getInt("y1");
                    int x2 = coordinateObject.getInt("x2");
                    int y2 = coordinateObject.getInt("y2");

                    Coordinate coordinate = new Coordinate(name, x1, y1, x2, y2);
                    coordinateDao.insertCoordinate(coordinate);

                    Log.d("coordinate data", "coordinate " + i);
                }
                Log.d("coordinate end", "coordinate end");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void stationReset(Context context){
        String jsonString2 = JsonHelper.loadJSONFromAsset(context, "stationIDTest.json");
        if (jsonString2 != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString2);
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

                    Log.d("station data", "station " + i);
                }
                Log.d("station end", "station end");
            } catch (JSONException | IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
