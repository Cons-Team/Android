package com.example.beacon_making_kotlin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.beacon_making_kotlin.db.api.TransferAPI;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.example.beacon_making_kotlin.db.database.ResetData;
import com.example.beacon_making_kotlin.db.helper.DatabaseHelper;
import com.example.tedpermission.PermissionListener;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SplashActivity extends AppCompatActivity {

    private ConsDatabase db;
    ProgressBar progressBar;
    ImageView metro;
    int progressStatus = 0;

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        metro = (ImageView) findViewById(R.id.metro_icon);
        float progressBarX = progressBar.getX();

        SharedPreferences preferces = getSharedPreferences("Setting", 0);
        if (preferces.getString("theme", "Day").equals("Day")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // DB
        DatabaseHelper.deleteDatabase(this, "cons_database");

        db = ConsDatabase.getDatabase(this);

        ResetData resetData = new ResetData(this);
        resetData.populateDatabaseIfEmpty(this);

        // Permission Request
        Log.d("sdk_ver", "" + Build.VERSION.SDK_INT);
        ArrayList<String> requestList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 31) {

            requestList.add(Manifest.permission.BLUETOOTH_SCAN); // 스캔 권한
            requestList.add(Manifest.permission.BLUETOOTH_CONNECT); // 연결 권한
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우

        } else if (Build.VERSION.SDK_INT >= 29) {

            requestList.add(Manifest.permission.BLUETOOTH); // 블루투스 연결 요청 및 수락, 데이터 전송 등에 필요
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
            requestList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION); // 백그라운드에서 스캔해야 할 경우

        } else if (Build.VERSION.SDK_INT >= 23) {

            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
        }
        
        // 카메라 사용 권한
        requestList.add(Manifest.permission.CAMERA);
        
        // 문자 전송 권한
        requestList.add(Manifest.permission.SEND_SMS);
        requestList.add(Manifest.permission.RECEIVE_SMS);
        requestList.add(Manifest.permission.READ_SMS);
        requestList.add(Manifest.permission.RECEIVE_MMS);
        requestList.add(Manifest.permission.VIBRATE);
        requestList.add(Manifest.permission.INTERNET);
        requestList.add(Manifest.permission.ACCESS_WIFI_STATE);
        requestList.add(Manifest.permission.CHANGE_WIFI_STATE);
        requestList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requestList.add(Manifest.permission.CHANGE_NETWORK_STATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
        else{
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // 전화 권환
        requestList.add(Manifest.permission.READ_PHONE_STATE);
        requestList.add(Manifest.permission.CALL_PHONE);

        // Contact 권한
        requestList.add(Manifest.permission.WRITE_CONTACTS);
        requestList.add(Manifest.permission.READ_CONTACTS);

        permissionRequest(requestList);

    }

    private class SplashHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish();
        }
    }

    private void updateProgress() {
        SharedPreferences preferences = getSharedPreferences("Setting", 0);
        int totalDuration = preferences.getInt("DBCheck", 60000);
        int updateInterval = 10;
        int totalSteps = totalDuration / updateInterval; // 총 스텝 수

        Handler handler = new Handler();
        int[] progress = {0}; // 진행 상태
        float trainWidth = 30 * getResources().getDisplayMetrics().density; // 기차 이미지의 실제 너비 (dp -> px)
        float progressBarWidth = 240 * getResources().getDisplayMetrics().density; // ProgressBar의 실제 너비 (dp -> px)

        // 총 이동 거리 계산
        float totalDistance = progressBarWidth - trainWidth; // 기차가 이동해야 할 총 거리
        float distancePerStep = totalDistance / totalSteps; // 각 스텝마다 이동할 거리

        // 기차 이미지 초기 위치 설정
        metro.setTranslationX(0);
        SharedPreferences.Editor editor = preferences.edit();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] < totalSteps) {
                    progress[0]++;
                    progressBar.setProgress((int)((progress[0] / (float)totalSteps) * 100)); // ProgressBar 진행률 업데이트

                    // 기차 이미지 이동
                    metro.setTranslationX(distancePerStep * progress[0]);
                    handler.postDelayed(this, updateInterval);
                } else {
                    // 작업 완료 후 Activity 전환
                    editor.putInt("DBCheck", 2000);
                    editor.apply();
                    editor.commit();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        handler.post(runnable);
    }

    private void permissionCheck(ArrayList<String> requestList){
        boolean check = false;
        for(String permissionName : requestList){
            if(ContextCompat.checkSelfPermission(SplashActivity.this, permissionName) == PackageManager.PERMISSION_GRANTED){
                check = true;
            }
            else{
                check = false;
                break;
            }
        }

        if(check){
//            Handler hd = new Handler();
//            hd.postDelayed(new SplashHandler(), 2000);
            updateProgress();
        }
    }

    /** 권한 부여 함수 */
    private void permissionRequest(ArrayList<String> requestList) {


            String[] permissionList = new String[requestList.size()];
            for(int i = 0; i < requestList.size(); i++){
                permissionList[i] = requestList.get(i);
            }

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toast.makeText(SplashActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.d("permissionCheck", "로그가 몇번 출력 되는지 확인 필요 All Granted");
                    permissionCheck(requestList);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(SplashActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("해당 권한은 필수 권한 입니다. setting 버튼을 통해 권한을 부여해주세요.")
                    .setGotoSettingButtonText("setting")
                    .setPermissions(permissionList)
                    .check();

    }

}
