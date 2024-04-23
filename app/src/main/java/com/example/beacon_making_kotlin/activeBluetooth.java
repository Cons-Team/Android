package com.example.beacon_making;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class activeBluetooth extends AppCompatActivity {

    beacon_data[] beaconData = BeaconBackgroundService.beaconData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_bluetooth);

        Toast.makeText(activeBluetooth.this, "data_size : " + beaconData.length, Toast.LENGTH_SHORT).show();

        TextView test1 = findViewById(R.id.test1);
        TextView test2 = findViewById(R.id.test2);
        TextView test3 = findViewById(R.id.test3);

        Button reset_btn = findViewById(R.id.button3);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(beaconData.length > 2) {
                    test3.setText("UUID : " + beaconData[2].getUUID() +
                            "\nDistance : " + beaconData[2].getDistance() +
                            "\nRssi : " + beaconData[2].getRssi());
                }
                if(beaconData.length > 1) {
                    test2.setText("UUID : " + beaconData[1].getUUID() + "\nDistance : " + beaconData[1].getDistance() + "\nRssi : " + beaconData[1].getRssi());
                }
                if(beaconData.length > 0) {
                    test1.setText("UUID : " + beaconData[0].getUUID() + "\nDistance : " + beaconData[0].getDistance() + "\nRssi : " + beaconData[0].getRssi());
                }

            }
        });

        Intent serviceIntent = new Intent(this, BeaconBackgroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}