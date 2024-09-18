package com.example.beacon_making_kotlin.Menu;

import android.graphics.drawable.Drawable;

public class MainMenuGroup {

    public String menu;
    public Drawable icon;

    public MainMenuGroup(String menu, Drawable icon){
        this.menu = menu;
        this.icon = icon;
    }

    public String getMenu(){
        return this.menu;
    }

    public Drawable getIcon(){
        return this.icon;
    }
}
