package com.example.beacon_making_kotlin.db.api;

import static com.example.beacon_making_kotlin.db.apiKey.serviceKey.realTime_key;
import static com.example.tedpermission.provider.TedPermissionProvider.context;
import com.example.beacon_making_kotlin.db.helper.JsonHelper;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

public class RealTimeAPI {

    private static String serviceKey = realTime_key;

    public static Vector<Vector<String>> loadRealTimeData(String subwayStationName) throws IOException, ParseException{
        StringBuilder urlBuilder = new StringBuilder("http://swopenAPI.seoul.go.kr/api/subway"); /*URL*/
        urlBuilder.append("/" +  URLEncoder.encode(serviceKey,"UTF-8") ); /*인증키*/
        urlBuilder.append("/" +  URLEncoder.encode("json","UTF-8") ); /*요청파일타입 (xml,xmlf,xls,json)*/
        urlBuilder.append("/" + URLEncoder.encode("realtimeStationArrival","UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
        urlBuilder.append("/" + URLEncoder.encode("1","UTF-8")); /*요청시작위치*/
        urlBuilder.append("/" + URLEncoder.encode("10","UTF-8")); /*요청종료위치*/
        urlBuilder.append("/" + URLEncoder.encode(subwayStationName,"UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        return parsing(sb.toString());
    }

    public static Vector<Vector<String>> parsing(String text) {
        Vector<Vector<String>> realTimeData = new Vector<>();

        try {
            JSONObject jsonObject1 = new JSONObject(text);
            JSONArray jsonArray = jsonObject1.getJSONArray("realtimeArrivalList");

            for (int i = 0; i < jsonArray.length(); i++) {
                Vector<String> data = new Vector<>();
                JSONObject item = jsonArray.getJSONObject(i);

                String subwayId = item.optString("subwayId", "N/A");
                String statnld = item.optString("statnId", "N/A");
                String statnFid = item.optString("statnFid", "N/A");
                String statnTid = item.optString("statnTid", "N/A");
                String statnNm = item.optString("statnNm", "N/A");
                String btrainSttus = item.optString("btrainSttus", "N/A");
                String trainLineNm = item.optString("trainLineNm", "N/A");
                String updnLine = item.optString("updnLine", "N/A");
                String arvlMsg2 = item.optString("arvlMsg2", "N/A");
                String barvlDt = item.optString("barvlDt", "N/A");

                data.add(findStationLine(subwayId));
                data.add(statnld);
                data.add(statnFid);
                data.add(statnTid);
                data.add(statnNm);
                data.add(btrainSttus);
                data.add(trainLineNm);
                data.add(updnLine);
                data.add(arvlMsg2);
                data.add(barvlDt);

                Log.d("realTimeData", statnNm + i);

                realTimeData.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return realTimeData;
    }

    public static String findStationLine(String id){
        String line = "";
        String jsonString1 = JsonHelper.loadJSONFromAsset(context, "realTimeStationID.json");

        if (jsonString1 != null) {
            try {
                JSONObject jsonObject1 = new JSONObject(jsonString1);
                JSONArray realTimeArray = jsonObject1.getJSONArray("Data");

                for (int i = 0; i < realTimeArray.length(); i++) {
                    JSONObject realTimeObject = realTimeArray.getJSONObject(i);
                    if (realTimeObject.getString("SUBWAY_ID").equals(id)){
                        line = realTimeObject.getString("호선이름");
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return line;
    }
}

