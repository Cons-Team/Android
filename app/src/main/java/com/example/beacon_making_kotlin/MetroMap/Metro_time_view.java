package com.example.beacon_making_kotlin.MetroMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.api.RealTimeAPI;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Metro_time_view {

    static Vector<Vector<String>> realTime;

    static HashMap<String, Metro_time_info> metroHash;
    ConstraintLayout include;

    LinearLayout lineListLayout;
    ImageButton refreshBtn;
    ImageButton infoBtn;
    ImageButton cancelBtn;

    static TextView stationName;
    static TextView leftStation;
    static TextView leftStationTime;
    static TextView rightStation;
    static TextView rightStationTime;

    static int test = 100;

    Button departureStation;
    Button transitStation;
    Button arrivalStation;
    Button timeTable;

    public Metro_time_view(View view) {
        realTime = new Vector<>();
        metroHash = new HashMap<>();

        include = (ConstraintLayout) view.findViewById(R.id.include);
        lineListLayout = (LinearLayout) view.findViewById(R.id.routeMapList);
        refreshBtn = (ImageButton) view.findViewById(R.id.refreshBtn);
        infoBtn = (ImageButton) view.findViewById(R.id.metroInfoBtn);
        cancelBtn = (ImageButton) view.findViewById(R.id.cancelBtn);

        stationName = (TextView) view.findViewById(R.id.stationName);
        leftStation = (TextView) view.findViewById(R.id.leftStation);
        leftStationTime = (TextView) view.findViewById(R.id.leftStationTime);
        rightStation = (TextView) view.findViewById(R.id.rightStation);
        rightStationTime = (TextView) view.findViewById(R.id.rightStationTime);

        departureStation = (Button) view.findViewById(R.id.departureStation);
        transitStation = (Button) view.findViewById(R.id.transitStation);
        arrivalStation = (Button) view.findViewById(R.id.arrivalStation);
        timeTable = (Button) view.findViewById(R.id.timeTable);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                include.setVisibility(View.GONE);
            }
        });
    }

    public static HashMap<String, Metro_time_info> insertInfo(Vector<Vector<String>> realTime) {
        metroHash.clear();
        Log.v("realTimeListValue", "" + realTime.size());
        for(int i = 0; i < realTime.size(); i++){
            String lineInfo = realTime.get(0).get(0) + "1호선";
            Log.v("realTimeListValue", "" + realTime.get(i).get(1));
            Log.v("realTimeListValue", "" + realTime.get(i).get(2));
            Log.v("realTimeListValue", "" + realTime.get(i).get(3));
            if(metroHash.containsKey(lineInfo) && !realTime.get(i).get(2).equals("N/A") && !realTime.get(i).get(1).equals("N/A")){
                if(Integer.parseInt(realTime.get(i).get(2)) < Integer.parseInt(realTime.get(i).get(1)) && metroHash.get(lineInfo).getMetro_name_right().isEmpty()){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_right(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_right(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_right(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_right(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_right(changeTime(realTime.get(i).get(9)));
                }
                else if(Integer.parseInt(realTime.get(i).get(2)) > Integer.parseInt(realTime.get(i).get(1)) && metroHash.get(lineInfo).getMetro_name_left().isEmpty()){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_left(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_left(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_left(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_left(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_left(changeTime(realTime.get(i).get(9)));
                }
            }
            else if(!realTime.get(i).get(2).equals("N/A") && !realTime.get(i).get(1).equals("N/A")){
                Log.v("putMap", "?");
                metroHash.put(lineInfo, new Metro_time_info());

                metroHash.get(lineInfo).setMetro_name(realTime.get(i).get(4));

                if(Integer.parseInt(realTime.get(i).get(2)) < Integer.parseInt(realTime.get(i).get(1))){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_right(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_right(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_right(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_right(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_right(changeTime(realTime.get(i).get(9)));
                }
                else if(Integer.parseInt(realTime.get(i).get(2)) > Integer.parseInt(realTime.get(i).get(1))){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_left(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_left(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_left(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_left(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_left(changeTime(realTime.get(i).get(9)));
                }
            }
        }
        Log.v("putMap", "" + metroHash.keySet().size());
        return metroHash;
    }

    @SuppressLint("SetTextI18n")
    static void settingView(int value) {
        stationName.setText("" + Metro_time_view.test);
//        Metro_time_info temp = Metro_time_view.metroHash.get("1호선");
//        stationName.setText(temp.getMetro_name());
//        leftStation.setText(temp.getMetro_name_left());
//        if(temp.getBtrainSttus_left().equals("급행")){
//            String tempTime = temp.getDestination_left() + " " + temp.getTime_left() + "(" + temp.getBtrainSttus_left() + ")";
//            leftStationTime.setText(tempTime);
//        }
//        else{
//            String tempTime = temp.getDestination_left() + " " + temp.getTime_left();
//            leftStationTime.setText(tempTime);
//        }
//
//        rightStation.setText(temp.getMetro_name_right());
//        if(temp.getBtrainSttus_right().equals("급행")){
//            String tempTime = temp.getDestination_right() + " " + temp.getTime_right() + "(" + temp.getBtrainSttus_right() + ")";
//            rightStationTime.setText(tempTime);
//        }
//        else{
//            String tempTime = temp.getDestination_right() + " " + temp.getTime_right();
//            rightStationTime.setText(tempTime);
//        }
    }


    private static String changeTime(String s) {
        String time = "";

        int temp = Integer.parseInt(s);

        time = (temp / 3600) + "분 " + ((temp % 3600) / 60) + "초";

        return time;
    }

}

