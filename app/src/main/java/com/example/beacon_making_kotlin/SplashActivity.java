package com.example.beacon_making_kotlin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.tedpermission.PermissionListener;
import com.example.tedpermission.TedPermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.splash);

        SharedPreferences preferces = getSharedPreferences("Setting", 0);

        Resources res = getResources();
        BitmapDrawable bitmap;
        ImageView splashImage = (ImageView) findViewById(R.id.splashImage);

        if (preferces.getString("theme", "Day").equals("Day")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            bitmap = (BitmapDrawable) res.getDrawable(R.drawable.splashimage, null);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            bitmap = (BitmapDrawable) res.getDrawable(R.drawable.splashimage_night, null);
        }

        splashImage.setImageDrawable(bitmap);

        // Permission Request
        Log.d("sdk_ver", "" + Build.VERSION.SDK_INT);
        ArrayList<String> requestList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 31) {

            requestList.add(android.Manifest.permission.BLUETOOTH_SCAN); // 스캔 권한
            requestList.add(android.Manifest.permission.BLUETOOTH_CONNECT); // 연결 권한
            requestList.add(android.Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우

        } else if (Build.VERSION.SDK_INT >= 29) {

            requestList.add(android.Manifest.permission.BLUETOOTH); // 블루투스 연결 요청 및 수락, 데이터 전송 등에 필요
            requestList.add(android.Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
            requestList.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION); // 백그라운드에서 스캔해야 할 경우

        } else if (Build.VERSION.SDK_INT >= 23) {

            requestList.add(android.Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
        }

        requestList.add(android.Manifest.permission.CAMERA);

        for(int i = 0 ; i < requestList.size(); i++){
            permission_setting(requestList.get(i), requestList);
        }

    }

    private class SplashHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish();
        }
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
            Handler hd = new Handler();
            hd.postDelayed(new SplashHandler(), 2000);
        }
    }

    /** 권한 부여 함수 */
    private void permission_setting(String permission_name, ArrayList<String> requestList) {
        boolean isAlertBlePermissonGranted = TedPermissionUtil.isGranted(permission_name);
        Log.d("ted", permission_name + ": " + isAlertBlePermissonGranted);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(SplashActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                permissionCheck(requestList);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(SplashActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read contact, find your location and system alert window")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(permission_name)
                .check();

    }

}
