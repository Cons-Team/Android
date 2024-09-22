package com.example.beacon_making_kotlin.pathfinding;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.beaconfind.ActiveBluetooth;
//import com.unity3d.player.UnityPlayerActivity;
//import com.unity3d.player.UnityPlayerActivity;


public class PathFindingActivity extends AppCompatActivity {

    int LAUNCH_SECOND_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_path_finding);


        // Unity로 넘어가는 부분
        String coordinate = getIntent().getStringExtra("result");
//        Toast.makeText(PathFindingActivity.this, "coordinate sending : " + coordinate, Toast.LENGTH_SHORT).show();
        Log.d("PathFindingActivity getCoordinate", coordinate);

//        Intent intent = new Intent(PathFindingActivity.this, UnityPlayerActivity.class);
//        intent.putExtra("result", coordinate);
//        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);

    }


    // Unity로 부터 값 받아오는 부분
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == RESULT_OK) {
//                String value = data.getStringExtra("result");
//                Toast.makeText(PathFindingActivity.this, value, Toast.LENGTH_SHORT).show();

                String phone_num = data.getStringExtra("phone_num");
                Log.d("receive_phone_num", phone_num + "");


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                        Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SmsManager sms_manager = SmsManager.getDefault();
                    sms_manager.sendTextMessage("+82" + phone_num, null, "도움이 요청되었습니다. ", null, null);
                    Log.d("sending_text", "success");
                }
            }
        } catch (Exception ex){
            Toast.makeText(PathFindingActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void goToPrivacyPolicy(View view){

        Intent intent;
        intent = new Intent(this, ActiveBluetooth.class);

        startActivity(intent);
    }

}