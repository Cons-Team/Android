package com.example.beacon_making_kotlin.MetroMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beacon_making_kotlin.R;

import java.util.ArrayList;

public class TransferAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int departureLay = 0;
    private int viaLay = 0;
    private int arrivalLay = 0;
    private ArrayList<Transfer_Group> listView;
    private LayoutInflater myInf = null;

    public TransferAdapter(Context context, int departureLay, int viaLay, int arrivalLay, ArrayList<Transfer_Group> listView){
        this.context = context;
        this.departureLay = departureLay;
        this.viaLay = viaLay;
        this.arrivalLay = arrivalLay;
        this.listView = listView;
        this.myInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SharedPreferences preferences = context.getSharedPreferences("Setting", 0);
        String theme = preferences.getString("theme", "Day");
        if(listView.get(groupPosition).departure == 0){
            convertView = myInf.inflate(this.departureLay, parent, false);
            TextView time = convertView.findViewById(R.id.transfer_departure_time);
            TextView line = convertView.findViewById(R.id.transfer_lineInfo);
            ImageView lineImage = convertView.findViewById(R.id.line_image);
            TextView departure = convertView.findViewById(R.id.transfer_departure_dire);

            departure.setText(listView.get(groupPosition).name);
            line.setText(listView.get(groupPosition).lineInfo);
            line.setTextColor(theme.equals("Day") ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
            lineImage.setColorFilter(Color.parseColor(listView.get(groupPosition).lineColor));
        }
        else if(listView.get(groupPosition).departure == 2){
            convertView = myInf.inflate(this.arrivalLay, parent, false);
            TextView time = convertView.findViewById(R.id.transfer_arrival_time);
            TextView line = convertView.findViewById(R.id.transfer_lineInfo_arrival);
            ImageView lineImage = convertView.findViewById(R.id.line_image);
            TextView arrival = convertView.findViewById(R.id.transfer_arrival_dire);
            ImageView walk = convertView.findViewById(R.id.transfer_walk);
            TextView door = convertView.findViewById(R.id.door_direction);
            TextView getOff = convertView.findViewById(R.id.quick_get_off);

            arrival.setText(listView.get(groupPosition).name);
            line.setText(listView.get(groupPosition).lineInfo);
            line.setTextColor(theme.equals("Day") ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
            lineImage.setColorFilter(Color.parseColor(listView.get(groupPosition).lineColor));
            Log.v("parentCheck", parent + "");
        }
        else{
            convertView = myInf.inflate(this.viaLay, parent, false);

            ImageView line = convertView.findViewById(R.id.via_image);
            TextView via = convertView.findViewById(R.id.station_via);

            line.setColorFilter(Color.parseColor(listView.get(groupPosition).lineColor));
            via.setText(listView.get(groupPosition).name);
            via.setTextColor(theme.equals("Day") ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
        }
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = myInf.inflate(this.viaLay, parent, false);
        Log.v("viaLoad", childPosition + "");

//        ImageView line = convertView.findViewById(R.id.via_image);
//        TextView via = convertView.findViewById(R.id.station_via);
//
//        line.setBackgroundColor(Color.parseColor(listView.get(groupPosition).lineColor));
//        line.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(listView.get(groupPosition).lineColor)));
//        via.setText(listView.get(groupPosition).via_list.get(childPosition));

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return listView.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listView.get(groupPosition);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 0;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
