package com.example.beacon_making_kotlin.pathfinding;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.TedPermission;
import com.example.beacon_making_kotlin.beaconfind.ActiveBluetooth;
import com.example.tedpermission.PermissionListener;
import com.example.tedpermission.TedPermissionUtil;

import java.util.List;


public class PathFindingActivity extends AppCompatActivity {

    int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_path_finding);


        // layout 폴더에 속한 activity_main.xml파일에 button
        Button btn = (Button) findViewById(R.id.button);
        // bluetooth on button click event
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                Intent intent;

                if (btadapter.isEnabled()) {
                    Log.d("ble_stat", "on_device");
                    Toast.makeText(PathFindingActivity.this, "on_device", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ble_stat", "꺼져있거나 블루투스 기능이 없습니다.");
                    Toast.makeText(PathFindingActivity.this, "꺼져있거나 블루투스 기능이 없습니다.", Toast.LENGTH_SHORT).show();

                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(PathFindingActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    startActivityForResult(intent, 1);
                }


            }
        });


        // 클릭시 Beacon Searching event 실행
        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPrivacyPolicy(view);
            }
        });


        // Unity로 넘어가는 부분
        Button unity = (Button) findViewById(R.id.unity);
        unity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PathFindingActivity.this, UnityPlayerActivity.class);
//                intent.putExtra("result", "80.0,1.52,0.0");
//                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);

            }
        });

        // 권한 부여 요청
        Log.d("sdk_ver", "" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 31) {

            permission_setting(Manifest.permission.BLUETOOTH_SCAN); // 스캔 권한
            permission_setting(Manifest.permission.BLUETOOTH_CONNECT); // 연결 권한
            permission_setting(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
        } else if (Build.VERSION.SDK_INT >= 29) {

            permission_setting(Manifest.permission.BLUETOOTH); // 블루투스 연결 요청 및 수락, 데이터 전송 등에 필요
            permission_setting(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
            // permission_setting(Manifest.permission.ACCESS_BACKGROUND_LOCATION); // 백그라운드에서 스캔해야 할 경우
        } else if (Build.VERSION.SDK_INT >= 23) {

            permission_setting(Manifest.permission.ACCESS_FINE_LOCATION); // 유저의 위치를 포함해야 할 경우
        }

        permission_setting(Manifest.permission.CAMERA);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    // Unity로 부터 값 받아오는 부분
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == RESULT_OK) {
                String value = data.getStringExtra("result");
                Toast.makeText(PathFindingActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex){
            Toast.makeText(PathFindingActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // 권한 부여 함수
    private void permission_setting(String permission_name) {
        boolean isAlertBlePermissonGranted = TedPermissionUtil.isGranted(permission_name);
        Log.d("ted", permission_name + ": " + isAlertBlePermissonGranted);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(PathFindingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(PathFindingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void goToPrivacyPolicy(View view){
        SharedPreferences userDataGetter = getSharedPreferences("userdata", MODE_PRIVATE);
        String userName = userDataGetter.getString("userName","");

        Intent intent;

        intent = new Intent(this, ActiveBluetooth.class);

        startActivity(intent);
    }

}