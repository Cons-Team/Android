package com.example.beacon_making_kotlin.MetroMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.api.RealTimeAPI;

import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

public class Metro_time_view {

    Vector<Vector<String>> realTime;

    public Metro_time_view(View view) {
        realTime = new Vector<>();
    }

    public void setting() throws IOException, ParseException {
        new Thread(() -> {
            try {
                realTime = RealTimeAPI.loadRealTimeData("서울");
                Log.v("success log", String.valueOf(realTime.size()));
            } catch (IOException | ParseException e) {
//                throw new RuntimeException(e);
                Log.v("error Log", "error");
            }
        }).start();
    }
}
