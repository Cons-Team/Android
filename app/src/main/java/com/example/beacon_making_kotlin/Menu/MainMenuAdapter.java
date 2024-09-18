package com.example.beacon_making_kotlin.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beacon_making_kotlin.R;

import java.util.ArrayList;

public class MainMenuAdapter extends BaseAdapter{

    Context context;
    LayoutInflater myInf;
    ArrayList<MainMenuGroup> mainMenuList;

    public MainMenuAdapter(Context context, ArrayList<MainMenuGroup> mainMenuList){
        this.context = context;
        this.mainMenuList = mainMenuList;
        myInf = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        return mainMenuList.size();
    }

    @Override
    public MainMenuGroup getItem(int position) {
        return mainMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = myInf.inflate(R.layout.main_menu, null);

        TextView text = (TextView) view.findViewById(R.id.menu_text);
        ImageView icon = (ImageView) view.findViewById(R.id.menu_icon);
        text.setText(mainMenuList.get(position).getMenu());
        icon.setImageDrawable(mainMenuList.get(position).getIcon());

        return view;
    }
}
