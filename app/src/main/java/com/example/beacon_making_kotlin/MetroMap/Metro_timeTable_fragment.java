package com.example.beacon_making_kotlin.MetroMap;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.entity.Info;
import com.example.beacon_making_kotlin.db.entity.Timetable;

import java.util.List;

public class Metro_timeTable_fragment extends Fragment {

    ConsDatabase db;
    TimeTableHandler handler = new TimeTableHandler();
    LinearLayout upLayout;
    LinearLayout downLayout;

    static String textColor;

    SharedPreferences preferces;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_timetable_fragmennt, container, false);

        db = ConsDatabase.getDatabase(getContext());
        upLayout = view.findViewById(R.id.upLine);
        downLayout = view.findViewById(R.id.downLine);

        preferces = this.getActivity().getSharedPreferences("Setting", 0);
        textColor = preferces.getString("theme", "Day").equals("Day") ? "#000000" : "#ffffff";

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        class SelectTimeTable extends Thread implements Runnable {

            String stationName = "";

            @Override
            public void run() {
                try {
                    sleep(400000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.v("stationIdText", stationName);
                List<String> stationId = db.stationDao().getStationID(stationName);
                Log.v("stationIdTest", stationId.size() + "");

                List<Timetable> timeTables = db.timetableDao().getAllTimetables();
                Log.v("TimeTable", timeTables.size() +"");

//                List<Info> infoTables = db.infoDao().getInfo(stationId.get(0));

                String up = "";
                String down = "";

                for(int i = 0; i < timeTables.size(); i++){
                    Log.v("timeTableID", "id : " + timeTables.get(i).getStationID());
//                    if(timeTables.get(i).getStationID().equals(stationId.get(0))){
                    if(timeTables.get(i).getStationID().equals("MTRS12249")){
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
//                Log.v("stationId", stationId.get(0));
//                Log.v("inftoTableSize", "" + infoTables.size());
//                for(int i = 0; i < infoTables.size(); i++){
//                    Log.v("infoTable", "Address : " + infoTables.get(i).getAddress());
//                    Log.v("infoTable", "tel : " + infoTables.get(i).getTel());
//                }

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("상행", up);
                bundle.putString("하행", down);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }

        SelectTimeTable tableClass = new SelectTimeTable();
        tableClass.stationName = "병점";

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

            upLayout.removeAllViews();
            downLayout.removeAllViews();

            for(int i = 0; i < upTime.length; i++){
                TextView up = new TextView(getContext());
                up.setPadding(15, 15, 15, 15);
                up.setText(upTime[i]);
                up.setTextColor(Color.parseColor(textColor));
                up.setTextSize(14);
                upLayout.addView(up);

                TextView down = new TextView(getContext());
                down.setPadding(15, 15, 15, 15);
                down.setText(downTime[i]);
                down.setTextColor(Color.parseColor(textColor));
                down.setTextSize(14);
                downLayout.addView(down);
            }
        }
    }
}
