package com.example.beacon_making_kotlin.MetroMap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.Menu.MainMenuAdapter;
import com.example.beacon_making_kotlin.Menu.SettingMenuAdapter;
import com.example.beacon_making_kotlin.Menu.SettingMenuGroup;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.db.api.TimetableAPI;
import com.example.beacon_making_kotlin.db.api.TransferAPI;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.entity.Timetable;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Metro_transfer_fragment extends Fragment {
    ExpandableListView transferView;
    ArrayList<Transfer_Group> transferGroups;

    TransferInfoHandler transferInfoHandler = new TransferInfoHandler();

    //Metro Header part
    TextView station_departure;
    TextView via_station_text;
    TextView station_via;
    TextView station_arrival;
    TextView station_via_num;
    TextView station_total_time;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_transfer_fragment, container, false);

        //transfer info view
        transferView = (ExpandableListView) view.findViewById(R.id.transferView);
        context = getContext();
        //header part
        station_departure = view.findViewById(R.id.metro_departure);
        via_station_text = view.findViewById(R.id.via_station);
        station_via = view.findViewById(R.id.metro_via);
        station_arrival = view.findViewById(R.id.metro_arrival);
        station_via_num = view.findViewById(R.id.metro_via_num);
        station_total_time = view.findViewById(R.id.metro_via_total);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        class SelectTransfer extends Thread implements Runnable {

            String sName;
            String viaName;
            String eName;
            Vector<Object> list;
            @Override
            public void run() {
                try {
                    list = TransferAPI.loadTransferData("오산", "신림", 1);
                } catch (IOException | ParseException | JSONException e) {
                    throw new RuntimeException(e);
                }

                Log.v("TransferDataTest", list.size()+"");
                Vector<String> viaList = (Vector<String>) list.get(6);
                StringBuilder sb = new StringBuilder();
                for(String via : viaList){
                    sb.append(via).append(", ");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.deleteCharAt(sb.length()-1);

                Vector<String> stationNames = (Vector<String>) list.get(7);
                Vector<Vector<String>> metroList = new Vector<>();
                for(int i = 0; i <= viaList.size(); i++){
                    metroList.add(new Vector());
                }
                Log.v("list", viaList.size()+"");
                Log.v("stationSize()", ""+stationNames.size());

                String[][] temp = {{"1호선", "#004B85"}, {"4호선", "#039CCE"}, {"2호선", "#01A13F"}};
                transferGroups = new ArrayList<>();
                int index = 0;
                transferGroups.add(new Transfer_Group(stationNames.get(0), 0, temp[index][0], temp[index][1]));
                for(int i = 1; i < stationNames.size(); i++) {
                    if(index < viaList.size() && stationNames.get(i).equals(viaList.get(index))){
                        transferGroups.add(new Transfer_Group(stationNames.get(i), 2, temp[index][0], temp[index][1], "", "", ""));
                        index++;
                        transferGroups.add(new Transfer_Group(stationNames.get(i), 0, temp[index][0], temp[index][1]));
                    }
                    else{
                        transferGroups.add(new Transfer_Group(stationNames.get(i), 1, temp[index][0], temp[index][1]));
                    }
                }
                transferGroups.add(new Transfer_Group("신림", 2, temp[index][0], temp[index][1], "", "", ""));
                transferGroups.add(new Transfer_Group("신림", 2, temp[index][0], temp[index][1], "", "", ""));

                TransferAdapter transferAdapter = new TransferAdapter(getContext(), R.layout.metro_transfer_departure, R.layout.metro_transfer_via, R.layout.metro_transfer_arrival, transferGroups);
                transferView.setGroupIndicator(null);
                transferView.setAdapter(transferAdapter);

                Message msg = transferInfoHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("출발지", "오산대");
                bundle.putString("경유지", "금정");
                bundle.putString("도착지", "신림");
                bundle.putString("환승역", sb.toString());
                bundle.putString("소요시간", String.valueOf(list.get(2)));
                msg.setData(bundle);
                transferInfoHandler.sendMessage(msg);
            }
        }

        SelectTransfer transferClass = new SelectTransfer();
        Bundle bundle = getArguments();
        if(bundle != null){
            transferClass.sName = "오산";
            transferClass.viaName = bundle.getString("");
            transferClass.eName = bundle.getString("신림");
        }

        Thread t = new Thread(transferClass);
        t.start();
    }

    class TransferInfoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            station_departure.setText(bundle.getString("출발지"));
            if(bundle.getString("경유지").equals("")){
                via_station_text.setVisibility(View.GONE);
                station_via.setVisibility(View.GONE);
            }
            else{
                station_via.setText(bundle.getString("경유지"));
                via_station_text.setVisibility(View.VISIBLE);
                station_via.setVisibility(View.VISIBLE);
            }
            station_arrival.setText(bundle.getString("도착지"));
            station_via_num.setText(bundle.getString("환승역"));
            station_total_time.setText(bundle.getString("소요시간") + "분");
        }
    }
}
