package com.example.beacon_making_kotlin.MetroMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.beaconfind.BeaconBackgroundService;
import com.example.beacon_making_kotlin.beaconfind.LoadingDialog;
import com.example.beacon_making_kotlin.db.api.RealTimeAPI;
import com.example.beacon_making_kotlin.db.dao.CoordinateDao;
import com.example.beacon_making_kotlin.db.dao.CoordinateDao_Impl;
import com.example.beacon_making_kotlin.db.dao.FavoriteDao;
import com.example.beacon_making_kotlin.db.dao.InfoDao;
import com.example.beacon_making_kotlin.db.dao.StationDao;
import com.example.beacon_making_kotlin.db.dao.TimetableDao;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.entity.Coordinate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class Metro_map_fragment extends Fragment {

    //View
    Metro_time_view metro_time_view;

    private ConsDatabase db;
    List<String> coordinationList;
    Context mContext;

    MainHandler handler = new MainHandler();

    //ImageView
    SubsamplingScaleImageView metro_map;
    Bitmap bitmap;
    Bitmap resized;

    public static LoadingDialog loadingDialog;

    //Button
    FloatingActionButton nav_btn_left;
    FloatingActionButton nav_btn_right;

    SharedPreferences preferces;
    ConstraintLayout include;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_map_fragment, container, false);
        db = ConsDatabase.getDatabase(getContext());

        //Fragment 전환
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.replace(R.id.frameLayout, memo).commitAllowingStateLoss();

        metro_time_view = new Metro_time_view(view);
        include = (ConstraintLayout) view.findViewById(R.id.include);

        preferces = requireActivity().getSharedPreferences("theme", 0);
        mContext = requireContext().getApplicationContext();

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.metro_map2);
        resized = Bitmap.createScaledBitmap(bitmap, 5000, 5000, true);
        metro_map = (SubsamplingScaleImageView) view.findViewById(R.id.metro_map);
        metro_map.setImage(ImageSource.bitmap(resized));

        metro_map.setMaxScale(2.0f);
        metro_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN && metro_map.getScale() >= 1.0f && metro_map.getScale() <= 2.0f){
                    class SelectCoordination implements Runnable{
                        int x;
                        int y;
                        @Override
                        public void run() {
                            List<String> coordinationList = ConsDatabase.getDatabase(mContext).coordinateDao().getStationName(x, y);
                            if(!coordinationList.isEmpty()){
                                BackgroundThread thread = new BackgroundThread();
                                thread.name = coordinationList.get(0);
                                thread.start();
                            }
                        }
                    }
                    PointF sCoord  =  metro_map.viewToSourceCoord(event.getX(),  event.getY());

                    SelectCoordination insertRunnable = new SelectCoordination();
                    insertRunnable.x = (int)  sCoord.x;
                    insertRunnable.y = (int)  sCoord.y;
                    Thread t = new Thread(insertRunnable);
                    t.start();
                }

                if(event.getAction() == MotionEvent.ACTION_MOVE){

                }

                return false;
            }
        });

        nav_btn_left = (FloatingActionButton) view.findViewById(R.id.nav_button_left);
        nav_btn_right = (FloatingActionButton) view.findViewById(R.id.nav_button_right);

        nav_btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBtnClick();
            }
        });

        nav_btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBtnClick();
            }
        });

        navBtnChange(preferces.getString("hand", "오른손 모드"));

        loadingDialog = new LoadingDialog(getContext());

        return view;
    }

    public void navBtnClick(){
        if(bluetoothStateCheck()){
            loadingDialog.show();

            // Beacon Searching
            Intent serviceIntent = new Intent(getContext(), BeaconBackgroundService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        }
    }

    /** bluetooth state check */
    private boolean bluetoothStateCheck(){
        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();

        if (btadapter.isEnabled()) {
            Log.d("ble_stat", "on_device");
            //Toast.makeText(MainActivity.this, "on_device", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            Log.d("ble_stat", "꺼져있거나 블루투스 기능이 없습니다.");
            //Toast.makeText(MainActivity.this, "꺼져있거나 블루투스 기능이 없습니다.", Toast.LENGTH_SHORT).show();

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
            }

            return false;
        }
    }


    public void navBtnChange(String mode){
        SharedPreferences.Editor editor = preferces.edit();

        if(mode.equals("왼손 모드")){
            editor.putString("hand", "왼손 모드");
            nav_btn_left.setVisibility(View.VISIBLE);
            nav_btn_right.setVisibility(View.GONE);
        }
        else{
            editor.putString("hand", "오른손 모드");
            nav_btn_right.setVisibility(View.VISIBLE);
            nav_btn_left.setVisibility(View.GONE);
        }

        editor.commit();
    }

    public class BackgroundThread extends Thread{
        String name;
        public void run(){
            try {
                 Metro_time_view.insertInfo(RealTimeAPI.loadRealTimeData(name));
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("value", 0);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Metro_time_view.settingBtn();
            Metro_time_view.settingView(bundle.getInt("value"));
        }
    }
}
