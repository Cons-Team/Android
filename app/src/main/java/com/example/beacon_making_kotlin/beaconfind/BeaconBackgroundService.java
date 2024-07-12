package com.example.beacon_making_kotlin.beaconfind;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.pathfinding.calculator.DistanceCalculator;
import com.example.beacon_making_kotlin.pathfinding.calculator.FloorCalculator;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BeaconBackgroundService extends Service implements BeaconConsumer {

    private final DistanceCalculator distanceCalculator = new DistanceCalculator();
    private final FloorCalculator floorCalculator = new FloorCalculator();

    public static final String CHANNEL_ID = "BeaconBackgroundServiceChannel";

    private BeaconManager beaconManager;

    private Identifier uuid = new Identifier(Identifier.fromUuid(UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825")));
    public static BeaconData[] beaconData;

    private static ArrayList<String> coor = new ArrayList<>();
    public static String coordinate;
    public static String floor;
    private int roopCount = 0;

    LoadingDialog loadingDialog = MainActivity.loadingDialog;
    @Override
    public void onBeaconServiceConnect() {

        beaconManager.removeAllRangeNotifiers();

        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                String test = "";
                beaconData = new BeaconData[beacons.size()];
                List<Beacon> beaconList = new ArrayList<>(beacons);
                if (!beacons.isEmpty()) {
                    beaconList.clear();
                    beaconList.addAll(beacons);
                }

                Log.d("beacon_count", beacons.size() + "개");
                //Toast.makeText(BeaconBackgroundService.this, "beacon_count : " + beacons.size() + "개", Toast.LENGTH_SHORT).show();

                for (int i = 0; i < beaconList.size(); i++) {
                    beaconData[i] = new BeaconData();
                    beaconData[i].setName(beaconList.get(i).getBluetoothName());
                    beaconData[i].setUUID(beaconList.get(i).getId1().toString());
                    beaconData[i].setMajor(beaconList.get(i).getId2().toString());
                    beaconData[i].setMinor(beaconList.get(i).getId3().toString());
                    beaconData[i].setRssi(String.valueOf(beaconList.get(i).getRssi()));

                    //Toast.makeText(BeaconBackgroundService.this, "name : " + beaconData[i].getName() +
                    //"\nrssi : " + beaconData[i].getRssi(), Toast.LENGTH_SHORT).show();
                }

                Arrays.sort(beaconData, new Comparator<BeaconData>() {
                    @Override
                    public int compare(BeaconData o1, BeaconData o2) {
                        return o1.getRssi().compareTo(o2.getRssi());
                    }
                });


                roopCount++;
                roopCheck();


            }
        });

    }


    /** beacon Connection Check */
    public void roopCheck(){
        if(roopCount < 5){
            if(beaconData.length >= 2){
                roopCount = 4;
            }
            else if(roopCount == 4){
                //beaconUnBind();
            }
        }
        else{
            // 비콘 2개 이상 탐색시 좌표 계산
            if (beaconData.length >= 2) {
                // 좌표 계산 및 유니티로 보낼 좌표
                coor.add(distanceCalculator.distance(beaconData));
            }
        }

        if(roopCount == 10){

//            double[] num = new double[2];
//            for(int i = 0; i < coor.size(); i++){
//                String[] str = coor.get(i).split(",");
//                num[0] += Double.parseDouble(str[0]);
//                num[1] += Double.parseDouble(str[1]);
//            }
//
//            coordinateAddFloor(num);

            roopCount = 0;
            coor.clear();

            beaconUnBind();
            Toast.makeText(BeaconBackgroundService.this, "loading dismiss success", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(BeaconBackgroundService.this, PathFindingActivity.class);
//            intent.putExtra("result", coordinate);
//            startActivity(intent);

        }
    }


    /** Beacon UnBind */
    public void beaconUnBind(){
        beaconManager.unbindInternal(BeaconBackgroundService.this);
        beaconManager.stopRangingBeacons(new Region("test", uuid, null, null));
        loadingDialog.dismiss_loading_dialog();
    }


    /** floor Calculation */
    public void coordinateAddFloor(double[] num){
        // 층 계산
        floor = floorCalculator.floorCalculator(beaconData[0]);

        Log.d("floor", floor);
        if(floor.charAt(0) == 'B'){
            coordinate = num[0] / coor.size() + "," + -7.71 * Integer.parseInt(floor.substring(1, 2)) + "," + num[1] / coor.size();
        }
        else{
            coordinate = num[0] / coor.size() + "," + 7.71 * (Integer.parseInt(floor.substring(0, 1)) - 1) + "," + num[1] / coor.size();
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @SuppressLint("FOREGROUND_SERVICE_CONNECTED_DEVICE")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, ActiveBluetooth.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("station-program")
                .setContentText("automatic check program running in background...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        //running data
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){}
        
        // 탐색 시작 코드
        Toast.makeText(BeaconBackgroundService.this, "scan_on", Toast.LENGTH_SHORT).show();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // 비콘 탐색 주기 1초
        beaconManager.setForegroundScanPeriod(1000);

        //binding BeaconService to Android Activity or service
        beaconManager.startRangingBeacons(new Region("test", uuid, null, null));
        beaconManager.bindInternal(this);


        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        beaconManager.unbindInternal(this);
        beaconManager.stopRangingBeacons(new Region("test", uuid, null, null));

        Toast.makeText(BeaconBackgroundService.this, "scan_off", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "BeaconBackgroundServiceChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}