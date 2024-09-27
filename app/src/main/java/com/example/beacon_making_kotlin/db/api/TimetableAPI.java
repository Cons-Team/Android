package com.example.beacon_making_kotlin.db.api;

import static com.example.beacon_making_kotlin.db.apiKey.serviceKey.timetable_key;
import static com.example.beacon_making_kotlin.db.apiKey.serviceKey.timetable_key;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;

public class TimetableAPI {

    private static String serviceKey = timetable_key;

    public static String loadTimetableData(String subwayStationId, String dailyTypeCode, String upDownTypeCode) throws IOException, ParseException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/SubwayInfoService/getSubwaySttnAcctoSchdulList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("200", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터 타입(xml, json)*/
        urlBuilder.append("&" + URLEncoder.encode("subwayStationId","UTF-8") + "=" + URLEncoder.encode(subwayStationId, "UTF-8")); /*지하철역ID [상세기능1. 지하철역 목록조회]에서 조회 가능*/
        urlBuilder.append("&" + URLEncoder.encode("dailyTypeCode","UTF-8") + "=" + URLEncoder.encode(dailyTypeCode, "UTF-8")); /*요일구분코드(01:평일, 02:토요일, 03:일요일)*/
        urlBuilder.append("&" + URLEncoder.encode("upDownTypeCode","UTF-8") + "=" + URLEncoder.encode(upDownTypeCode, "UTF-8")); /*상하행구분코드(U:상행, D:하행)*/

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

    public static String parsing(String text) {
        StringBuilder resultBuilder = new StringBuilder();

        try {
            JSONObject jsonObject1 = new JSONObject(text);
            JSONObject jsonObject2 = jsonObject1.getJSONObject("response");
            JSONObject jsonObject3 = jsonObject2.getJSONObject("body");
            JSONObject jsonObject4 = jsonObject3.getJSONObject("items");

            JSONArray jsonArray = jsonObject4.getJSONArray("item");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String depTime = item.optString("arrTime", "N/A");
                String endStationName = item.optString("endSubwayStationNm", "N/A");

                if (i > 0) {
                    resultBuilder.append("@");
                }
                resultBuilder.append(depTime).append("-").append(endStationName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultBuilder.toString();
    }

}
