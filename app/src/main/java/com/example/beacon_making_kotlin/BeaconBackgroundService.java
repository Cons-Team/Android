package com.example.beacon_making_kotlin;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static java.lang.Thread.sleep;

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
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;

public class BeaconBackgroundService extends Service implements BeaconConsumer {

    public static final String CHANNEL_ID = "BeaconBackgroundServiceChannel";
    protected static final String TAG1 = "::MonitoringActivity::";
    protected static final String TAG2 = "::RangingActivity::";
    private BeaconManager beaconManager;

    //for HTTP request
    private final String BASEURL = "https://gpbl.lemondouble.com";

    public static beacon_data[] beaconData;

    @Override
    public void onBeaconServiceConnect(){

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
                try{
                    sleep(5000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                Log.d("beacon_count", beacons.size() + "개");
                Toast.makeText(BeaconBackgroundService.this, "beacon_count : " + beacons.size() + "개", Toast.LENGTH_SHORT).show();


                beaconData = new beacon_data[beacons.size()];
                for(int i = 0; i <beacons.size(); i++){
                    beaconData[i] = new beacon_data();
                    beaconData[i].setUUID(beacons.iterator().next().getId1().toString());
                    beaconData[i].setMajor(beacons.iterator().next().getId2().toString());
                    beaconData[i].setMinor(beacons.iterator().next().getId3().toString());
                    beaconData[i].setDistance(String.valueOf(beacons.iterator().next().getDistance()));
                    beaconData[i].setRssi(String.valueOf(beacons.iterator().next().getRssi()));


                    //Toast.makeText(BeaconBackgroundService.this, "distance : " + beaconData[i].getDistance() + "\nrssi : " + beaconData[i].getRssi(), Toast.LENGTH_SHORT).show();

                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

                     Log.d(TAG2, ":::::This :: U U I D :: of beacon   :  "+ beaconData[i].getUUID() + ":::::");
                     Log.d(TAG2, ":::::This :: M a j o r :: of beacon   :  "+ beaconData[i].getMajor() + ":::::");
                     Log.d(TAG2, ":::::This :: M i n o r :: of beacon   :  "+ beaconData[i].getMinor() + ":::::");
                     Log.d(TAG2, ":::::This :: D I S T A N C E :: of beacon   :  "+ beaconData[i].getDistance() + ":::::");
                     Log.d(TAG2, ":::::This :: R S S I :: of beacon   :  "+ beaconData[i].getRssi() + ":::::");
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("test", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
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
        Intent notificationIntent = new Intent(this,activeBluetooth.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Covid-check-program")
                .setContentText("automatic check program running in background...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        //running data
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){}

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        //binding BeaconService to Android Activity or service
        beaconManager.bind(this);

        //for http request

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        beaconManager.unbind(this);
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