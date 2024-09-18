package com.example.beacon_making_kotlin.Menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beacon_making_kotlin.R;

import java.util.ArrayList;

public class SettingMenuAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int groupLay = 0;
    private int childLay = 0;
    private ArrayList<SettingMenuGroup> listView;
    private LayoutInflater myInf = null;

    public SettingMenuAdapter(Context context, int groupLay, int childLay, ArrayList<SettingMenuGroup> listView){
        this.context = context;
        this.groupLay = groupLay;
        this.childLay = childLay;
        this.listView = listView;
        this.myInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = myInf.inflate(this.groupLay, parent, false);
        TextView groupName = (TextView) convertView.findViewById(R.id.setting_menu_text);
        ImageView icon = (ImageView) convertView.findViewById(R.id.setting_menu_icon);
        groupName.setText(listView.get(groupPosition).groupName);
        icon.setImageDrawable(listView.get(groupPosition).icon);

        String color = getColor();
        groupName.setTextColor(Color.parseColor(color));
        icon.setColorFilter(Color.parseColor(color));

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = myInf.inflate(this.childLay, parent, false);
        ImageView childSelected = (ImageView) convertView.findViewById(R.id.child_selected);
        childSelected.setImageResource(R.drawable.icon_selected);

        TextView childName = (TextView) convertView.findViewById(R.id.setting_menu_child);
        String childText = listView.get(groupPosition).child.get(childPosition);
        childName.setText(childText);

        String color = getColor();
        childName.setTextColor(Color.parseColor(color));
        childSelected.setColorFilter(Color.parseColor(color));

        SharedPreferences preferences = context.getSharedPreferences("Setting", 0);
        String fontSize = preferences.getString("fontSize", "보통 사이즈");
        String mode = preferences.getString("mode", "오른손 모드");

        Log.v("TextCheck1", fontSize);
        Log.v("TextCheck2", childText);
        Log.v("???", " ");

        if(childText.equals(fontSize) || childText.equals(mode)){
            childSelected.setVisibility(View.VISIBLE);
        }
        else{
            childSelected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    public String getColor(){
        String color;

        SharedPreferences preferences = context.getSharedPreferences("Setting", 0);
        if(preferences.getString("theme", "Day").equals("Day")){
            color = "#000000";
        }
        else{
            color = "#ffffff";
        }
        return color;
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
        return listView.get(groupPosition).child.get(childPosition);
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
        return listView.get(groupPosition).child.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
