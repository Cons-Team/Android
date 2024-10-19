package com.example.beacon_making_kotlin.MetroMap;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.api.RealTimeAPI;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.entity.Info;
import com.example.beacon_making_kotlin.db.entity.Station;
import com.example.beacon_making_kotlin.db.entity.Timetable;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class Metro_info_fragment extends Fragment {

    ConsDatabase db;

    String temp = "";

    ConstraintLayout metroTimeView;
    ImageButton infoBtn;
    LinearLayout btnList;

    TimeTableHandler handler = new TimeTableHandler();
    MainHandler mainHandler = new MainHandler();
    TextView telText;
    TextView locationText;
    Metro_time_view metro_time_view;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_info_fragment, container, false);

        db = ConsDatabase.getDatabase(getContext());

        metroTimeView = (ConstraintLayout) view.findViewById(R.id.include);
        infoBtn = (ImageButton) view.findViewById(R.id.metroInfoBtn);
        btnList = (LinearLayout) view.findViewById(R.id.btnList);
        infoBtn.setVisibility(View.GONE);
        btnList.setVisibility(View.GONE);

        telText = (TextView) view.findViewById(R.id.metro_info_num);
        locationText = (TextView) view.findViewById(R.id.metro_info_location);
        metro_time_view = new Metro_time_view(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        class SelectTimeTable extends Thread implements Runnable {

            String stationName = "";

            @Override
            public void run() {
                BackgroundThread thread = new BackgroundThread();
                thread.name = stationName;
                Log.v("StationName", stationName);
                thread.start();

                List<String> stationId = db.stationDao().getStationID(stationName);
                Log.v("stationID", "" + stationId.get(0));

                if(!stationId.isEmpty()){
                    List<Info> infoTables = db.infoDao().getInfo(stationId.get(0));
                    Log.v("infoTables", infoTables.size() + " ");

                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("tel", infoTables.get(0).getTel());
                    bundle.putString("address", infoTables.get(0).getAddress());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
                else{
                    //toast 굽기
                }
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

    class TimeTableHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String tel = bundle.getString("tel");
            String addresses = bundle.getString("address");

            telText.setText(tel);
            telText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent numberIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                    Metro_info_fragment.this.getContext().startActivity(numberIntent);
                }
            });
            locationText.setText(addresses);
        }
    }

    public class BackgroundThread extends Thread{

        String name;
        public void run(){
            HashMap<String, Metro_time_info> map;
            try {
                map = Metro_time_view.insertInfo(RealTimeAPI.loadRealTimeData(name));
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            Message msg = mainHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("value", map.keySet().size()-1);
            Log.v("mapKeyTest", map.keySet().size()+"");
            msg.setData(bundle);
            mainHandler.sendMessage(msg);
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Metro_time_view.settingView(bundle.getInt("value"));
        }
    }
}
