package com.example.beacon_making_kotlin.db.database;

import com.example.beacon_making_kotlin.db.api.TimetableAPI;
import com.example.beacon_making_kotlin.db.api.TransferAPI;
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
                JSONArray coordinateArray = jsonObject.getJSONArray("coordinate");
                Vector<Object> test = TransferAPI.loadTransferData("오산", "신림", 2);
                Log.d("경로안내 data", "결과 " + test);

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
                JSONArray stationArray = jsonObject.getJSONArray("station");

                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject stationObject = stationArray.getJSONObject(i);

                    String stationID = stationObject.getString("stationID");
                    String stationName = stationObject.getString("stationName");
                    String line = stationObject.getString("line");

                    Station station = new Station(stationID, stationName, line);
                    stationDao.insertStation(station);
                    infoReset(context, stationID, stationName, line);

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
                JSONArray stationArray = jsonObject.getJSONArray("info");

                for (int i = 0; i < stationArray.length(); i++) {
                    JSONObject stationObject = stationArray.getJSONObject(i);

                    String jsonstationName = stationObject.getString("역명");
                    String jsonLine = stationObject.getString("호선");
                    String address = stationObject.getString("도로명주소");
                    String tel = stationObject.getString("역전화번호");

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
                JSONArray timetableArray = jsonObject.getJSONArray("timetable");

                for (int i = 0; i < timetableArray.length(); i++) {
                    JSONObject timetableObject = timetableArray.getJSONObject(i);

                    String stationID = timetableObject.getString("stationID");
                    String day = timetableObject.getString("day");
                    String updown = timetableObject.getString("updown");
                    String time = timetableObject.getString("time");

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

