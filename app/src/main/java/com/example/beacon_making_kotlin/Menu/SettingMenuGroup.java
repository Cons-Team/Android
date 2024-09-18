package com.example.beacon_making_kotlin.Menu;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class SettingMenuGroup {

    public ArrayList<String> child;
    public String groupName;
    public Drawable icon;

    public SettingMenuGroup(String name, Drawable icon){
        groupName = name;
        this.icon = icon;
        child = new ArrayList<String>();
    }
}
