package com.example.beacon_making_kotlin.MetroMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    Metro_timeTable_fragment metroTimeTableFragment;

    private ConsDatabase db;

    Context mContext;

    MainHandler handler = new MainHandler();

    //ImageView
    SubsamplingScaleImageView metro_map;
    Bitmap bitmap;
    Bitmap resized;

    public static LoadingDialog loadingDialog;
    public static Activity currentActivity;

    //Button
    static FloatingActionButton nav_btn_left;
    static FloatingActionButton nav_btn_right;

    SharedPreferences preferces;
    ConstraintLayout include;

    FragmentTransaction transaction;
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_map_fragment, container, false);
        db = ConsDatabase.getDatabase(getContext());

        //Fragment 전환
        transaction = getParentFragmentManager().beginTransaction();
        metroTimeTableFragment = new Metro_timeTable_fragment();

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
//                navBtnClick();
                MainActivity.toolbar.setVisibility(View.GONE);
                Metro_transfer_fragment metroTransferFragment = new Metro_transfer_fragment();
                transaction.replace(R.id.fragment_container_view, metroTransferFragment).commitAllowingStateLoss();
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

        ImageButton metro_info = view.findViewById(R.id.metroInfoBtn);
        TextView stationName = (TextView) view.findViewById(R.id.stationName);
        metro_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("metro_name", (String) stationName.getText());
                Metro_info_fragment metroInfoFragment = new Metro_info_fragment();
                metroInfoFragment.setArguments(bundle);
                transaction.setCustomAnimations(R.anim.from_left, 0);
                transaction.replace(R.id.fragment_container_view, metroInfoFragment).commitAllowingStateLoss();
                MainActivity.title.setText("역사 정보");
                MainActivity.subToolBar.setVisibility(View.VISIBLE);
                MainActivity.subToolBar.setTranslationX(-100);
                MainActivity.subToolBar.setAlpha(0f);
                MainActivity.subToolBar.animate().translationX(0).alpha(1f).setDuration(300).start();
                MainActivity.mainToolBar.setVisibility(View.GONE);
            }
        });

        Button timeTable = view.findViewById(R.id.timeTable);
        timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("metro_name", (String) stationName.getText());
                metroTimeTableFragment.setArguments(bundle);
                transaction.setCustomAnimations(R.anim.from_left, 0);
                transaction.replace(R.id.fragment_container_view, metroTimeTableFragment).commitAllowingStateLoss();
                MainActivity.subToolBar.setVisibility(View.VISIBLE);
                MainActivity.subToolBar.setTranslationX(-100);
                MainActivity.subToolBar.setAlpha(0f);
                MainActivity.subToolBar.animate().translationX(0).alpha(1f).setDuration(300).start();
                MainActivity.mainToolBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v("Metro_map_fragment", "onStart");

        MainActivity.mainToolBar.setVisibility(View.VISIBLE);
        MainActivity.mainToolBar.setTranslationX(90);
        MainActivity.mainToolBar.setAlpha(0f);
        MainActivity.mainToolBar.animate().translationX(0).alpha(1f).setDuration(300).start();

        metro_map.post(new Runnable() {
            @Override
            public void run() {
                PointF center = new PointF(2500, 2500);
                metro_map.setMaxScale(2.0f);
                metro_map.setMinScale(1.0f);
                metro_map.setScaleAndCenter(1.0f, center);
            }
        });
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
            Log.v("handModeTest", "left");
        }
        else{
            editor.putString("hand", "오른손 모드");
            nav_btn_right.setVisibility(View.VISIBLE);
            nav_btn_left.setVisibility(View.GONE);
            Log.v("handModeTest", "right");
        }
        editor.commit();
    }

    public class BackgroundThread extends Thread{
        String name;
        public void run(){
            HashMap<String, Metro_time_info> map = new HashMap<>();
            try {
                 map = Metro_time_view.insertInfo(RealTimeAPI.loadRealTimeData(name));
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            if(!map.isEmpty()){
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", 0);
                msg.setData(bundle);
                handler.sendMessage(msg);    
            }
            else {
                //Toast 출력
//                Toast.makeText("입력된 정보가 없습니다.", null);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", -1);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
            
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if(bundle.getInt("value") == -1){
                include.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                include.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }
            else{
                Metro_time_view.settingBtn();
                Metro_time_view.settingView(bundle.getInt("value"));
                if(include.getVisibility() == View.GONE){
                    include.setVisibility(View.VISIBLE);
                    include.setAlpha(0f);
                    include.animate().alpha(1f).setDuration(300).start();
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            currentActivity = (Activity) context; // 현재 Activity 참조 저장
        }
    }
}
