package com.example.beacon_making_kotlin.MetroMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.api.RealTimeAPI;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Metro_time_view {

    static Vector<String> lineInfo;

    static HashMap<String, Metro_time_info> metroHash;
    static HashMap<String, String> metroLineColor;
    static ConstraintLayout include;

    static LinearLayout lineListLayout;
    static LinearLayout stationNameListLayout;
    static Button[] lineBtnList;
    ImageButton refreshBtn;
    ImageButton infoBtn;
    ImageButton cancelBtn;

    static TextView stationName;
    static TextView leftStation;
    static TextView leftStationTime;
    static TextView rightStation;
    static TextView rightStationTime;

    Button departureStation;
    Button transitStation;
    Button arrivalStation;
    Button timeTable;

    public Metro_time_view(View view) {
        lineInfo = new Vector<>();
        metroHash = new HashMap<>();

        include = (ConstraintLayout) view.findViewById(R.id.include);
        lineListLayout = (LinearLayout) include.findViewById(R.id.routeMapList);
        refreshBtn = (ImageButton) view.findViewById(R.id.refreshBtn);
        infoBtn = (ImageButton) view.findViewById(R.id.metroInfoBtn);
        cancelBtn = (ImageButton) view.findViewById(R.id.cancelBtn);

        stationNameListLayout = (LinearLayout) view.findViewById(R.id.stationNameList);
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

        timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        metroLineColor = new HashMap<>();
        settingColor();
    }

    private void settingColor() {
        metroLineColor.put("1호선", "#004B85");
        metroLineColor.put("2호선", "#01A13F");
        metroLineColor.put("3호선", "#ED6D00");
        metroLineColor.put("4호선", "#039CCE");
        metroLineColor.put("5호선", "#794598");
        metroLineColor.put("6호선", "#7C4A33v");
        metroLineColor.put("7호선", "#6D7E30");
        metroLineColor.put("8호선", "#D01870v");
        metroLineColor.put("9호선", "#A49E88");
        metroLineColor.put("경의중앙선", "#69C3B1");
        metroLineColor.put("공항철도", "#0079AC");
        metroLineColor.put("경춘선", "#007A63");
        metroLineColor.put("수인분당선", "#ECA50E");
        metroLineColor.put("신분당선", "#B71B30");
        metroLineColor.put("우의신설선", "#B9CB03");
        metroLineColor.put("서해선", "#70B22C");
        metroLineColor.put("경강선", "#063190");

    }

    public static HashMap<String, Metro_time_info> insertInfo(Vector<Vector<String>> realTime) {
        metroHash.clear();
        lineInfo.clear();

        for(int i = 0; i < realTime.size(); i++){
            String lineInfo = realTime.get(i).get(0);
            Log.v("lineInfo", lineInfo);
            if(metroHash.containsKey(lineInfo) && !realTime.get(i).get(2).equals("N/A") && !realTime.get(i).get(1).equals("N/A")){
                if(Integer.parseInt(realTime.get(i).get(2)) > Integer.parseInt(realTime.get(i).get(1)) && metroHash.get(lineInfo).getMetro_name_right().isEmpty()){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_right(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_right(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_right(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_right(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_right(changeTime(realTime.get(i).get(9)));
                }
                else if(Integer.parseInt(realTime.get(i).get(2)) < Integer.parseInt(realTime.get(i).get(1)) && metroHash.get(lineInfo).getMetro_name_left().isEmpty()){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_left(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_left(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_left(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_left(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_left(changeTime(realTime.get(i).get(9)));
                }
            }
            else if(!realTime.get(i).get(2).equals("N/A") && !realTime.get(i).get(1).equals("N/A")){
                metroHash.put(lineInfo, new Metro_time_info());

                metroHash.get(lineInfo).setMetro_name(realTime.get(i).get(4));

                if(Integer.parseInt(realTime.get(i).get(2)) > Integer.parseInt(realTime.get(i).get(1))){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_right(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_right(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_right(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_right(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_right(changeTime(realTime.get(i).get(9)));
                }
                else if(Integer.parseInt(realTime.get(i).get(2)) < Integer.parseInt(realTime.get(i).get(1))){
                    String[] tempTrain = realTime.get(i).get(6).split(" - ");

                    metroHash.get(lineInfo).setMetro_name_left(tempTrain[1].split("방면")[0]);
                    metroHash.get(lineInfo).setDestination_left(tempTrain[0]);
                    metroHash.get(lineInfo).setTime_left(realTime.get(i).get(8));
                    metroHash.get(lineInfo).setBtrainSttus_left(realTime.get(i).get(5));
                    metroHash.get(lineInfo).setBarvlDt_left(changeTime(realTime.get(i).get(9)));
                }
            }
        }

        lineInfo.addAll(metroHash.keySet());
        return metroHash;
    }

    static void settingBtn(){
        lineBtnList = new Button[lineInfo.size()];
        lineListLayout.removeAllViews();
        Log.v("lineInfoSize", lineInfo.size() + " ");
        for(int i = 0; i < lineInfo.size(); i++){
            lineBtnList[i] = new Button(include.getContext());
            lineListLayout.addView(lineBtnList[i]);
            int value = i;
            lineBtnList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingView(value);
                }
            });

        }
    }


    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    static void settingView(int value) {
        Metro_time_info temp = Metro_time_view.metroHash.get(lineInfo.get(value));

        for(int i = 0; i < lineBtnList.length; i++){
            lineBtnList[i].setTextColor(Color.parseColor(metroLineColor.get(lineInfo.get(i))));
            if(i == value){
                SpannableString content = new SpannableString(lineInfo.get(value));
                content.setSpan(new UnderlineSpan(), 0, lineInfo.get(value).length(), 5);
                lineBtnList[i].setText(content);
            }
            else{
                SpannableString content = new SpannableString(lineInfo.get(i));
                content.setSpan(new UnderlineSpan(), 0, 0, 5);
                lineBtnList[i].setText(content);
            }
        }

        stationName.setText(temp.getMetro_name());
        leftStation.setText(temp.getMetro_name_left());
        if(temp.getBtrainSttus_left().equals("급행")){
            String tempTime = temp.getDestination_left() + " " + temp.getTime_left() + "(" + temp.getBtrainSttus_left() + ")";
            leftStationTime.setText(tempTime);
        }
        else{
            String tempTime = temp.getDestination_left() + " " + temp.getTime_left();
            leftStationTime.setText(tempTime);
        }

        rightStation.setText(temp.getMetro_name_right());
        if(temp.getBtrainSttus_right().equals("급행")){
            String tempTime = temp.getDestination_right() + " " + temp.getTime_right() + "(" + temp.getBtrainSttus_right() + ")";
            rightStationTime.setText(tempTime);
        }
        else{
            String tempTime = temp.getDestination_right() + " " + temp.getTime_right();
            rightStationTime.setText(tempTime);
        }

        stationName.setBackgroundColor(Color.parseColor(metroLineColor.get(lineInfo.get(value))));
        stationName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(metroLineColor.get(lineInfo.get(value)))));
        stationNameListLayout.setBackgroundColor(Color.parseColor(metroLineColor.get(lineInfo.get(value))));
        stationNameListLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(metroLineColor.get(lineInfo.get(value)))));

        include.setVisibility(View.VISIBLE);
    }


    private static String changeTime(String s) {
        String time = "";

        int temp = Integer.parseInt(s);

        time = (temp / 3600) + "분 " + ((temp % 3600) / 60) + "초";

        return time;
    }

}

