package com.example.beacon_making_kotlin.MetroMap;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.entity.Info;
import com.example.beacon_making_kotlin.db.entity.Timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlinx.coroutines.MainCoroutineDispatcher;

public class Metro_timeTable_fragment extends Fragment {

    ConsDatabase db;
    TimeTableHandler handler = new TimeTableHandler();
    LinearLayout upLayout;
    LinearLayout downLayout;

    static String textColor;
    static TextView title;
    SharedPreferences preferces;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_timetable_fragmennt, container, false);

        //Fragment 전환
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        db = ConsDatabase.getDatabase(getContext());
        upLayout = view.findViewById(R.id.upLine);
        downLayout = view.findViewById(R.id.downLine);

        preferces = this.getActivity().getSharedPreferences("Setting", 0);
        textColor = preferces.getString("theme", "Day").equals("Day") ? "#000000" : "#ffffff";
        return view;
    }

    public void onStart() {
        super.onStart();
        class SelectTimeTable extends Thread implements Runnable {

            String stationName = "";

            @Override
            public void run() {
                Log.v("stationIdText", stationName);
                List<String> stationId = db.stationDao().getStationID(stationName);
                Log.v("stationIdTest", stationId.size() + "");

                List<Timetable> timeTables = db.timetableDao().getAllTimetables();
                Log.v("TimeTable", timeTables.size() +"");

//                List<Info> infoTables = db.infoDao().getInfo(stationId.get(0));

                String up = "";
                String down = "";

                for(int i = 0; i < timeTables.size(); i++){
                    Log.v("timeTableID", "id : " + timeTables.get(i).getStationID() + " " + i);
                    if(timeTables.get(i).getStationID().equals(stationId.get(0))){
                        Log.v("timeTable", "Day : " + timeTables.get(i).getDay());
                        Log.v("timeTable", "Updown : " + timeTables.get(i).getUpdown());
                        Log.v("timeTable", "Time : " + timeTables.get(i).getTime());
                        if(timeTables.get(i).getDay().equals("01") && timeTables.get(i).getUpdown().equals("U")){
                            up = timeTables.get(i).getTime();
                        }
                        else if(timeTables.get(i).getDay().equals("01") && timeTables.get(i).getUpdown().equals("D")){
                            down = timeTables.get(i).getTime();
                        }
                    }
                }

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("상행", up);
                bundle.putString("하행", down);
                bundle.putString("역명", stationName);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }

        SelectTimeTable tableClass = new SelectTimeTable();
        Bundle bundle = getArguments();
        if(bundle != null){
            tableClass.stationName = bundle.getString("metro_name");
        }

        Thread t = new Thread(tableClass);
        t.start();
    }

    class TimeTableHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String[] upTime = bundle.getString("상행").split("@");
            String[] downTime = bundle.getString("하행").split("@");
            
            MainActivity.title.setText(bundle.getString("역명"));

            upLayout.removeAllViews();
            downLayout.removeAllViews();

            String[] upTimeSetting = timeSetting(upTime);
            String[] downTimeSetting = timeSetting(downTime);

            for(String s : upTimeSetting){
                TextView up = new TextView(getContext());
                up.setPadding(15, 15, 15, 15);
                up.setText(s);
                up.setTextColor(Color.parseColor(textColor));
                up.setTextSize(14);
                upLayout.addView(up);
            }

            for(String s : downTimeSetting){
                TextView down = new TextView(getContext());
                down.setPadding(15, 15, 15, 15);
                down.setText(s);
                down.setTextColor(Color.parseColor(textColor));
                down.setTextSize(14);
                downLayout.addView(down);
            }
        }

        private String[] timeSetting(String[] time) {
            String[] value = new String[time.length];
            ArrayList<String> list = new ArrayList<>();
            int index = 0;
            for(String s : time){
                String[] temp = s.split("-");
                System.out.println(s);
                if(temp[0].equals("0")){
                   continue;
                }
                else if(Integer.parseInt(temp[0]) < 20000){
                    String timeValue = "" + temp[0].charAt(0) + temp[0].charAt(1) + "시 " +
                            temp[0].charAt(2) + temp[0].charAt(3) + "분\n" + (temp.length != 1 ? temp[1] : "");
                    list.add(timeValue);
                }
                else{
                    String timeValue = "" + temp[0].charAt(0) + temp[0].charAt(1) + "시 " +
                            temp[0].charAt(2) + temp[0].charAt(3) + "분\n" + (temp.length != 1 ? temp[1] : "");
                    value[index++] = timeValue;
                }
            }

            for(String s : list){
                value[index++] = s;
            }

            return value;
        }
    }
}
