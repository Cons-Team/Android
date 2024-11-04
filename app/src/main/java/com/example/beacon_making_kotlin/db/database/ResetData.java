package com.example.beacon_making_kotlin.db.database;

import com.example.beacon_making_kotlin.db.api.*;
import com.example.beacon_making_kotlin.db.dao.*;
import com.example.beacon_making_kotlin.db.entity.*;
import com.example.beacon_making_kotlin.db.helper.JsonHelper;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetData {

    private final CoordinateDao coordinateDao;
    private final StationDao stationDao;
    private final InfoDao infoDao;
    private final TimetableDao timetableDao;
    private final ExecutorService executorService;

    private static final String[] dailyTypeCode = {"01", "03"};
    private static final String[] upDownTypeCode = {"D", "U"};

    public ResetData(Context context) {
        ConsDatabase db = ConsDatabase.getDatabase(context);
        coordinateDao = db.coordinateDao();
        stationDao = db.stationDao();
        infoDao = db.infoDao();
        timetableDao = db.timetableDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void populateDatabaseIfEmpty(final Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (stationDao.getAllStations().isEmpty()) {
                coordinateReset(context);
                stationReset(context);
                timetableReset(context);
            }
        executor.shutdown();
        });
    }

    private void coordinateReset(Context context){
        String jsonString = JsonHelper.loadJSONFromAsset(context, "coordinate.json");
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray coordinateArray = jsonObject.optJSONArray("coordinate");
                Vector<Object> test = TransferAPI.loadTransferData("오산", "신림", 2);
                Log.d("경로안내 data", "결과 " + test);

                for (int i = 0; i < coordinateArray.length(); i++) {
                    JSONObject coordinateObject = coordinateArray.optJSONObject(i);

                    String name = coordinateObject.optString("name");
                    int x1 = coordinateObject.optInt("x1");
                    int y1 = coordinateObject.optInt("y1");
                    int x2 = coordinateObject.optInt("x2");
                    int y2 = coordinateObject.optInt("y2");

                    Coordinate coordinate = new Coordinate(name, x1, y1, x2, y2);
                    coordinateDao.insertCoordinate(coordinate);

                    Log.d("coordinate data", "coordinate " + i);
                }
                Log.d("coordinate end", "coordinate end");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void stationReset(Context context){
        String jsonString = JsonHelper.loadJSONFromAsset(context, "stationID.json");
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray stationArray = jsonObject.optJSONArray("station");

                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject stationObject = stationArray.optJSONObject(i);

                    String stationID = stationObject.optString("stationID");
                    String stationName = stationObject.optString("stationName");
                    String line = stationObject.optString("line");

                    Station station = new Station(stationID, stationName, line);
                    stationDao.insertStation(station);
                    infoReset(context, stationID, stationName, line);
//
//                    for (String day : dailyTypeCode) {
//                        for (String updown : upDownTypeCode) {
//                            Timetable timetable = new Timetable(stationID, day, updown, TimetableAPI.loadTimetableData(stationID, day, updown));
//                            timetableDao.insertTimetable(timetable);
//                        }
//                    }

                    Log.d("station data", "station " + i);
                }
                Log.d("station end", "station end");
            } catch (JSONException e) {
                e.printStackTrace();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
            }
        }
    }

    private void infoReset(Context context, String stationID, String stationName, String line){
        String jsonString = JsonHelper.loadJSONFromAsset(context, "info.json");
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray stationArray = jsonObject.optJSONArray("info");

                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject stationObject = stationArray.optJSONObject(i);

                    String jsonstationName = stationObject.optString("역명");
                    String jsonLine = stationObject.optString("호선");
                    String address = stationObject.optString("도로명주소");
                    String tel = stationObject.optString("역전화번호");

                    if (stationName.equals(jsonstationName) && line.equals(jsonLine + "호선")){
                        Info info = new Info(stationID, address, tel);
                        infoDao.insertInfo(info);
                        Log.d("info data", "info : " + jsonstationName + "역 " + tel);
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void timetableReset(Context context){
        String jsonString = JsonHelper.loadJSONFromAsset(context, "timetable.json");
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray timetableArray = jsonObject.optJSONArray("timetable");

                for (int i = 0; i < timetableArray.length(); i++) {
                    JSONObject timetableObject = timetableArray.optJSONObject(i);

                    String stationID = timetableObject.optString("stationID");
                    String day = timetableObject.optString("day");
                    String updown = timetableObject.optString("updown");
                    String time = timetableObject.optString("time");

                    Timetable timetable = new Timetable(stationID, day, updown, time);
                    timetableDao.insertTimetable(timetable);

                    Log.d("timetable data", "timetable " + i);
                }
                Log.d("timetable end", "timetable end");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

