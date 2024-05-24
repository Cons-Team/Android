package com.example.beacon_making_kotlin.beaconfind;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.beacon_making_kotlin.R;

public class ActiveBluetooth extends AppCompatActivity {

    public TextView beacon_count;
    public TextView beacon_coordinate;
    public TextView test1;
    public TextView test2;
    public TextView test3;

    public Button text_btn;
    public Button beacon_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_bluetooth);
        try {
//            Toast.makeText(activeBluetooth.this, "data_size : " + beaconData.length, Toast.LENGTH_SHORT).show();

            beacon_count = (TextView) findViewById(R.id.beacon_count);
            beacon_coordinate = (TextView) findViewById(R.id.coordinate);

            test1 = (TextView) findViewById(R.id.test1);
            test2 = (TextView) findViewById(R.id.test2);
            test3 = (TextView) findViewById(R.id.test3);

            text_btn = (Button) findViewById(R.id.text_button);
            beacon_btn = (Button) findViewById(R.id.beacon_button);


            text_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    BeaconData[] beaconData = BeaconBackgroundService.beaconData;
                    String coordinate = BeaconBackgroundService.coordinate;

                    if (beaconData != null) {
                        beacon_count.setText("beacon count : " + beaconData.length + "개");
                        beacon_coordinate.setText(coordinate);
                        if (beaconData.length > 2) {
                            test3.setText("Name : " + beaconData[2].getName() +
                                    "\nRssi : " + beaconData[2].getRssi());
                        }
                        if (beaconData.length > 1) {
                            test2.setText("Name : " + beaconData[1].getName() +
                                    "\nRssi : " + beaconData[1].getRssi());
                        }
                        if (beaconData.length > 0) {
                            test1.setText("Name : " + beaconData[0].getName() +
                                    "\nRssi : " + beaconData[0].getRssi());
                        }
                    }

                    else{
                        beacon_count.setText("beacon count : 0개");
                        beacon_coordinate.setText("xyz : 0, 0, 0");
                    }
                }
            });

            beacon_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent serviceIntent = new Intent(ActiveBluetooth.this, BeaconBackgroundService.class);
                    ContextCompat.startForegroundService(ActiveBluetooth.this, serviceIntent);
                }
            });

        }
        catch (NullPointerException e){
            Log.d("null_pointer", "beacon_not_find");
        }

    }
}