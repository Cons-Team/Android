package com.example.beacon_making_kotlin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class activeBluetooth extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_bluetooth);
        try {
//            Toast.makeText(activeBluetooth.this, "data_size : " + beaconData.length, Toast.LENGTH_SHORT).show();

            TextView test1 = (TextView) findViewById(R.id.test1);
            TextView test2 = (TextView) findViewById(R.id.test2);
            TextView test3 = (TextView) findViewById(R.id.test3);

            Button reset_btn = (Button) findViewById(R.id.button3);

            reset_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    beacon_data[] beaconData = BeaconBackgroundService.beaconData;
//                    if (beaconData != null) {
                        if (beaconData.length > 2) {
                            test3.setText("Name : " + beaconData[2].getName() +
                                    "\nDistance : " + beaconData[2].getDistance() +
                                    "\nRssi : " + beaconData[2].getRssi());
                        }
                        if (beaconData.length > 1) {
                            test2.setText("Name : " + beaconData[1].getName() + "\nDistance : " + beaconData[1].getDistance() + "\nRssi : " + beaconData[1].getRssi());
                        }
                        if (beaconData.length > 0) {
                            test1.setText("Name : " + beaconData[0].getName() + "\nDistance : " + beaconData[0].getDistance() + "\nRssi : " + beaconData[0].getRssi());
                        }
//                    }

                }
            });
        }
        catch (NullPointerException e){
            Log.d("null_pointer", "beacon_not_find");
        }
        Intent serviceIntent = new Intent(this, BeaconBackgroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}