package com.example.beacon_making_kotlin.MetroMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.beacon_making_kotlin.MainActivity;
import com.example.beacon_making_kotlin.R;
import com.example.beacon_making_kotlin.beaconfind.BeaconBackgroundService;
import com.example.beacon_making_kotlin.beaconfind.LoadingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class Metro_map_fragment extends Fragment {

    //ImageView
    SubsamplingScaleImageView metro_map;
    Bitmap bitmap;
    Bitmap resized;

    public static LoadingDialog loadingDialog;

    //Button
    FloatingActionButton nav_btn_left;
    FloatingActionButton nav_btn_right;

    SharedPreferences preferces;

    Button testBtn;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.metro_map_fragment, container, false);

        preferces = requireActivity().getSharedPreferences("theme", 0);
        testBtn = (Button) view.findViewById(R.id.testButton);

        Resources res = getResources();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.metro_map);
        resized = Bitmap.createScaledBitmap(bitmap, 5000, 5000, true);
        metro_map = (SubsamplingScaleImageView) view.findViewById(R.id.metro_map);
        metro_map.setImage(ImageSource.bitmap(resized));

        int x = resized.getHeight();
        int y = resized.getWidth();
        Log.v("size", "size : "+ x + "," + y);

        int x2 = metro_map.getSHeight();
        int y2 = metro_map.getSWidth();
        Log.v("size", "size : "+ x2 + "," + y2);

        metro_map.setMaxScale(2.0f);

        metro_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){
                    PointF sCoord  =  metro_map.viewToSourceCoord(event.getX(),  event.getY());
                    int  x_cor  =  (int)  sCoord.x;
                    int  y_cor  =  (int)  sCoord.y;
                    Log.v("location", "x : " + x_cor + ", y : " + y_cor );
                    float scaleValue = metro_map.getScale();

                    Log.v("scale", "scale : " + scaleValue);

//                    testBtn.setVisibility(View.VISIBLE);
//                    testBtn.setX(event.getX());
//                    testBtn.setY(event.getY());

                    String[] stationList = {"신창", "873,4339,922,4396",
                            "온양온천", "956,4337,1008,4393",
                            "배방", "1043,4339,1092,4393",
                            "탕정", "1124,4336,1170,4395",
                            "아산", "1208,4340,1262,4395",
                            "쌍용", "1299,4338,1340,4394",
                            "봉명", "1376,4335,1421,4395",
                            "천안", "1460,4339,1511,4398",
                            "두정", "1547,4338,1597,4394",
                            "직산", "1630,4337,1676,4393",
                            "성환", "1712,4340,1763,4394",
                            "평택", "1797,4335,1844,4395",
                            "평택지제", "1881,4334,1929,4397",
                            "서정리", "1961,4335,2012,4395",
                            "송탄", "2041,4336,2085,4397",
                            "진위", "2100,4334,2138,4372",
                            "오산", "2127,4301,2180,4336",
                            "오산대", "2166,4260,2217,4298",
                            "세마", "2198,4217,2254,4258",
                            "병점", "2244,4178,2297,4217",
                            "서동탄", "2358,4175,2418,4220",
                            "세류", "2297,4134,2349,4171",
                            "수원", "2334,4063,2401,4117",
                            "화서", "2282,4008,2325,4053",
                            "고색", "2279,4068,2316,4110",
                            "성균관대", "2225,3958,2278,4003",
                            "의왕", "2175,3904,2221,3953",
                            "당정", "2123,3854,2173,3901",
                            "군포", "2070,3805,2127,3850",
                            "금정", "1983,3703,2040,3753",
                            "산본", "1940,3763,1992,3805",
                            "명학", "1905,3638,1953,3682",
                            "안양", "1845,3576,1895,3624",
                            "관악", "1789,3519,1834,3564",
                            "석수", "1727,3456,1780,3508",
                            "금천구청", "1694,3337,1744,3392",
                            "광명", "1612,3339,1659,3387",
                            "독산", "1691,3261,1741,3307",
                            "가산디지털단지", "1691,3143,1745,3211",
                            "구로", "1692,3033,1744,3088",
                            "신도림", "1780,3017,1836,3086",
                            "영등포", "1874,2948,1919,2996",
                            "신길", "1974,2850,2023,2918",
                            "대방", "2144,2867,2202,2931",
                            "노량진", "2283,2753,2326,2815",
                            "노들", "2334,2754,2377,2797",
                            "흑석", "2391,2755,2424,2799",
                            "동작", "2435,2756,2481,2815",
                            "용산", "2276,2563,2330,2623",
                            "남영", "2389,2369,2429,2412",
                            "숙대입구", "2443,2327,2480,2368",
                            "서울역", "2372,2173,2485,2236",
                            "회현", "2434,2108,2480,2155",
                            "명동", "2433,2038,2480,2084",
                            "충무로", "2461,1959,2515,2025",
                            "시청", "2119,1804,2174,1882",
                            "종각", "2122,1722,2173,1771",
                            "종로3가", "2306,1611,2375,1706",
                            "종로5가", "2464,1631,2511,1685",
                            "동대문", "2624,1616,2669,1681",
                            "혜화", "2674,1568,2718,1611",
                            "한성대입구", "2720,1521,2771,1565",
                            "성신여대입구", "2776,1459,2828,1512",
                            "동묘앞", "2808,1636,2857,1675",
                            "창신", "2840,1595,2872,1625",
                            "보문", "2868,1540,2911,1587",
                            "안암", "2913,1515,2954,1554",
                            "신설동", "2939,1621,2996,1708",
                            "제기동", "3066,1637,3111,1691",
                            "청량리", "3158,1632,3242,1718",
                            "회기", "3239,1562,3308,1624",
                            "외대앞", "3239,1503,3278,1543",
                            "신이문", "3235,1446,3280,1489",
                            "석계", "3230,1371,3286,1426",
                            "고려대", "2957,1465,2995,1518",
                            "월곡", "3004,1421,3045,1465",
                            "상월곡", "3091,1384,3136,1434",
                            "돌곶이", "3164,1383,3207,1433",
                            "광운대", "3226,1288,3288,1336",
                            "월계", "3239,1240,3278,1278",
                            "녹천", "3237,1182,3280,1222",
                            "창동", "3236,1117,3279,1164",
                            "방학", "3281,1069,3319,1111",
                            "도봉", "3322,1031,3355,1066",
                            "도봉산", "3358,976,3409,1027",
                            "수락산", "3419,1021,3449,1060",
                            "마들", "3453,1065,3486,1093",
                            "망월사", "3421,925,3464,966",
                            "회룡", "3487,866,3516,910",
                            "발곡", "3515,913,3544,947",
                            "범골", "3448,851,3482,889",
                            "경전철의정부", "3419,820,3449,855",
                            "의정부시청", "3470,822,3432,793",
                            "흥선", "3512,794,3543,825",
                            "의정부", "3536,824,3568,860",
                            "가능", "3610,751,3635,777",
                            "녹양", "3637,724,3666,748",
                            "양주", "3665,688,3700,723",
                            "덕계", "3700,657,3734,684",
                            "덕정", "3733,619,3769,654",
                            "지행", "3789,586,3835,630",
                            "동두천중앙", "3859,585,3902,624",
                            "보산", "3922,580,3963,624",
                            "동두천", "3987,581,4025,627",
                            "소요산", "4055,585,4089,629",
                            "초성리", "4119,585,4156,625",
                            "전곡", "4185,580,4220,625",
                            "연천", "4251,583,4287,625",
                            "구일", "1642,3046,1676,3086",
                            "개봉", "1582,3044,1620,3085",
                            "오류동", "1531,3048,1564,3085",
                            "온수", "1459,3048,1507,3093",
                            "천왕", "1509,3098,1553,3133",
                            "역곡", "1397,3045,1435,3084",
                            "소사", "1340,3050,1383,3098",
                            "부천", "1249,3048,1280,3084",
                            "중동", "1160,3047,1193,3080",
                            "송내", "1067,3038,1106,3082",
                            "부개", "976,3050,1011,3082",
                            "부평", "895,3047,944,3095",
                            "백운", "840,3088,872,3125",
                            "동암", "836,3135,871,3173",
                            "간석", "839,3185,877,3222",
                            "주안", "838,3262,879,3306",
                            "도화", "838,3355,876,3387",
                            "제물포", "837,3405,872,3439",
                            "도원", "839,3445,872,3485",
                            "동인천", "839,3499,876,3537",
                            "인천", "829,3561,884,3618"};

                    for(int i = 1; i < stationList.length; i += 2){
                        String[] temp = stationList[i].split(",");
                        int[] temp2 = new int[4];
                        for(int j = 0; j < temp.length; j++){
                            temp2[j] = Integer.parseInt(temp[j]);
                        }

                        if(x_cor >= temp2[0] && x_cor <= temp2[2] && y_cor >= temp2[1] && y_cor <= temp2[3] && metro_map.getScale() >= 1.0f && metro_map.getScale() <= 2.0f){
                            Log.v("station", stationList[i-1]);

                            float xValue = (float)(temp2[2] + temp2[0])/2;
                            float yValue = (float)(temp2[3] + temp2[1])/2;
                            float width =  xValue - x_cor;
                            float height = yValue - y_cor;
                            Log.v("test", "" + (event.getRawX() + (width * metro_map.getScale())));
                            if(width <= 0){
                                testBtn.setX(event.getRawX() + (width * metro_map.getScale()));
                            }
                            else{
                                testBtn.setX(event.getRawX() - (width * metro_map.getScale()));
                            }

                            if(height <= 0){
                                testBtn.setY(event.getRawY() + (height * metro_map.getScale()));
                            }
                            else{
                                testBtn.setY(event.getRawY() - (height * metro_map.getScale()));
                            }

                            testBtn.setVisibility(View.VISIBLE);
                            testBtn.setText(stationList[i-1]);

                            testBtn.setY(event.getRawY());
                            break;
                        }
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    testBtn.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });

        nav_btn_left = (FloatingActionButton) view.findViewById(R.id.nav_button_left);
        nav_btn_right = (FloatingActionButton) view.findViewById(R.id.nav_button_right);

        nav_btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBtnClick();
            }
        });

        nav_btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBtnClick();
            }
        });

        navBtnChange(preferces.getString("hand", "오른손 모드"));

        loadingDialog = new LoadingDialog(getContext());

        return view;
    }

    public void navBtnClick(){
        if(bluetoothStateCheck()){
            loadingDialog.show();

            // Beacon Searching
            Intent serviceIntent = new Intent(getContext(), BeaconBackgroundService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        }
    }

    /** bluetooth state check */
    private boolean bluetoothStateCheck(){
        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();

        if (btadapter.isEnabled()) {
            Log.d("ble_stat", "on_device");
            //Toast.makeText(MainActivity.this, "on_device", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            Log.d("ble_stat", "꺼져있거나 블루투스 기능이 없습니다.");
            //Toast.makeText(MainActivity.this, "꺼져있거나 블루투스 기능이 없습니다.", Toast.LENGTH_SHORT).show();

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
            }

            return false;
        }
    }


    public void navBtnChange(String mode){
        SharedPreferences.Editor editor = preferces.edit();
        
        if(mode.equals("왼손 모드")){
            editor.putString("hand", "왼손 모드");
            nav_btn_left.setVisibility(View.VISIBLE);
            nav_btn_right.setVisibility(View.GONE);
        }
        else{
            editor.putString("hand", "오른손 모드");
            nav_btn_right.setVisibility(View.VISIBLE);
            nav_btn_left.setVisibility(View.GONE);
        }
        
        editor.commit();
    }

}
