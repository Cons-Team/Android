package com.example.beacon_making_kotlin;

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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BeaconBackgroundService extends Service implements BeaconConsumer {

    public static final String CHANNEL_ID = "BeaconBackgroundServiceChannel";
    protected static final String TAG1 = "::MonitoringActivity::";
    protected static final String TAG2 = "::RangingActivity::";
    private BeaconManager beaconManager;

    boolean scan_check = false;

    //for HTTP request
    private final String BASEURL = "https://gpbl.lemondouble.com";
    private Identifier uuid = new Identifier(Identifier.fromUuid(UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825")));
    private Identifier major = new Identifier(Identifier.fromInt(11111));
    private Identifier minor = new Identifier(Identifier.fromInt(1000));
    public static BeaconData[] beaconData;

    public static String coordinate;

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.removeAllRangeNotifiers();

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG1, "::: we find at least 1 beacon:::" );
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG1, ":::we cannot find beacons:::");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                if(state == 0){
                    Log.d(TAG1, "::: WE CAN SEE BEACON. state : "+state + " ::: ");
                }else{
                    Log.d(TAG2, "::: WE CANNOT SEE BEACON. state : " + state + ":::");
                }
            }
        });

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
                    beaconData[i].setDistance(String.valueOf(beaconList.get(i).getDistance()));
                    beaconData[i].setRssi(String.valueOf(beaconList.get(i).getRssi()));

                    //Toast.makeText(BeaconBackgroundService.this, "name : " + beaconData[i].getName() +
                            //"\nrssi : " + beaconData[i].getRssi(), Toast.LENGTH_SHORT).show();
                }

                // 비콘 2개 이상 탐색시 좌표 계산
                if (beaconList.size() >= 2) {
                    //beaconManager.unbindInternal(BeaconBackgroundService.this);
                    //beaconManager.stopRangingBeacons(new Region("test", uuid, null, null));

                    // 좌표 계산 및 유니티로 보낼 좌표
                    DistanceCalculator distanceCalculator = new DistanceCalculator();
                    coordinate = distanceCalculator.distance(beaconData);

                }

                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

            }
        });

    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @SuppressLint("FOREGROUND_SERVICE_CONNECTED_DEVICE")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        createNotificationChannel();
        Intent notificationIntent = new Intent(this,activeBluetooth.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("station-program")
                .setContentText("automatic check program running in background...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);
        scan_check = !scan_check;
        //running data
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){}
        
        // 탐색 시작 코드
        if(scan_check) {
            Toast.makeText(BeaconBackgroundService.this, "scan_on", Toast.LENGTH_SHORT).show();

            beaconManager = BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

            // 비콘 탐색 주기 2초
            beaconManager.setForegroundScanPeriod(2000);

            //binding BeaconService to Android Activity or service
            beaconManager.startRangingBeacons(new Region("test", uuid, null, null));
            beaconManager.bindInternal(this);

        }
        // 탐색 종료 코드
        else{
            beaconManager.unbindInternal(BeaconBackgroundService.this);
            beaconManager.stopRangingBeacons(new Region("test", uuid, null, null));

            Toast.makeText(BeaconBackgroundService.this, "scan_off", Toast.LENGTH_SHORT).show();

        }
        //for http request

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