package com.example.beacon_making_kotlin.MetroMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beacon_making_kotlin.R;

public class Metro_transfer_fragment extends Fragment {

    ExpandableListView transferView;

    //Metro Header part
    TextView station_departure;
    TextView station_via;
    TextView station_arrival;
    TextView station_via_num;
    TextView station_total_time;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_transfer_fragment, container, false);

        //transfer info view
        transferView = view.findViewById(R.id.transferView);

        //header part
        station_departure = view.findViewById(R.id.metro_departure);
        station_via = view.findViewById(R.id.metro_via);
        station_arrival = view.findViewById(R.id.metro_arrival);
        station_via_num = view.findViewById(R.id.metro_via_num);
        station_total_time = view.findViewById(R.id.metro_via_total);

        return view;
    }
}
