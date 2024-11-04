package com.example.beacon_making_kotlin.db.api;

import static com.example.beacon_making_kotlin.db.apiKey.serviceKey.transfer_key;

import android.util.Log;

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
import java.util.Vector;

public class TransferAPI {

    private static String serviceKey = transfer_key;

    public static Vector<Object> loadTransferData(String sName, String eName, int sopt) throws IOException, ParseException, JSONException {
        Log.d("지하철 환승 안내", "시작");
        String sCode = stationCode(sName);
        Log.d("지하철 환승 안내", "sCode 완료");
        String eCode = stationCode(eName);
        Log.d("지하철 환승 안내", "eCode 완료");
        Vector<Object> result = transferApi(sCode, eCode, sopt);
//        Vector<Object> result = transferApi("188", "11718", sopt);  // 오산대 - 신림 길찾기 테스트

        return result;
    }

    public static Vector<Object> transferApi(String sid, String eid, int sopt) throws IOException, ParseException, JSONException {
        String urlInfo = "https://api.odsay.com/v1/api/subwayPath?lang=0&CID=1000&SID=" + sid + "&EID=" + eid + "&Sopt=1&apiKey="
                + URLEncoder.encode(serviceKey, "UTF-8");

        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        conn.disconnect();

        Vector<Object> resultVector = new Vector<>();

        JSONObject jsonObject = new JSONObject(sb.toString());
        if (jsonObject.has("result")){
            try {
                JSONObject result = jsonObject.getJSONObject("result");

                String globalStartName = result.optString("globalStartName", "");   // 출발역 명
                String globalEndName = result.optString("globalEndName", "");   // 도착역 명
                int globalTravelTime = result.optInt("globalTravelTime", 0);   // 소요 시간(분)
                int globalStationCount = result.optInt("globalStationCount", 0);   // 정차역 수
                int fare = result.optInt("fare", 0);   // 성인 요금 (카드)
                int cashFare = result.optInt("cashFare", 0);   // 성인 요금 (현금)

                JSONArray exChangeInfoArray = result.getJSONObject("exChangeInfoSet").getJSONArray("exChangeInfo"); // 환승역 명
                Vector<String> exNames = new Vector<>();
                for (int i = 0; i < exChangeInfoArray.length(); i++) {
                    String startName = exChangeInfoArray.getJSONObject(i).optString("exName", "");
                    exNames.add(startName);
                }

                JSONArray stationsArray = result.getJSONObject("stationSet").getJSONArray("stations");  // 정차역 명
                Vector<String> stationNames = new Vector<>();
                for (int i = 0; i < stationsArray.length(); i++) {
                    String startName = stationsArray.getJSONObject(i).optString("startName", "");
                    stationNames.add(startName);
                }

                resultVector.add(globalStartName);
                resultVector.add(globalEndName);
                resultVector.add(globalTravelTime);
                resultVector.add(globalStationCount);
                resultVector.add(fare);
                resultVector.add(cashFare);
                resultVector.add(exNames);
                resultVector.add(stationNames);
            } catch (JSONException e){
                Log.d("JSONException", "transferApi Error");
            }
        }


        return resultVector;
    }

    // 역명을 검색하면, 그에 해당하는 역코드를 반환함
    public static String stationCode(String stationName) throws IOException, JSONException {
        String urlInfo = "https://api.odsay.com/v1/api/searchStation?lang=0&stationName=" + URLEncoder.encode(stationName, "UTF-8") +"&CID=1000&stationClass=2&displayCnt=1&startNO=1" + "&apiKey="
                + serviceKey;

        URL url = new URL(urlInfo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Log.d("지하철 환승 안내", "" + "response 시작");
        int responseCode = con.getResponseCode();
        Log.d("지하철 환승 안내", "" + responseCode);
        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        con.disconnect();

        JSONObject jsonObject = new JSONObject(sb.toString());
        JSONArray stationArray = jsonObject.getJSONObject("result").getJSONArray("station");

        if (stationArray.length() > 0) {
            return stationArray.getJSONObject(0).optString("stationID", "");
        }
        return null;
    }

    // 도시이름을 입력하면, 그에 해당하는 도시코드를 반환함(API 이슈로 서울 도시코드인 1000이 고정적으로 입력되는 중이라 현재는 사용하지 않는 메서드)
    public static String cityCode(String cityName) throws IOException, JSONException {

        String urlInfo = "https://api.odsay.com/v1/api/searchCID?lang=0&cityName=" + URLEncoder.encode(cityName, "UTF-8") + "&apiKey="
                + serviceKey;

        URL url = new URL(urlInfo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        con.disconnect();

        JSONObject jsonObject = new JSONObject(sb.toString());
        JSONArray cidArray = jsonObject.getJSONObject("result").getJSONArray("CID");

        if (cidArray.length() > 0) {
            return cidArray.getJSONObject(0).optString("cityCode", "");
        }

        return null;
    }


}
