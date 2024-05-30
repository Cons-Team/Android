package com.example.beacon_making_kotlin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.beacon_making_kotlin.pathfinding.PathFindingActivity;

public class MainActivity extends AppCompatActivity {

    View menu;
    ImageButton menuBtn;
    Button lostThingBtn;
    Button fontBtn;
    Button handModeBtn;
    Button themeBtn;
    ImageButton searchBtn;
    ImageButton[] navigationBtn;

    Button testBtn;

    EditText searchText;

    Dialog fontSettingDialog;

    SharedPreferences preferces;
    ConstraintLayout profileLayout;

    int fontSizeValue;
    String handValue;
    String themeValue;

    SubsamplingScaleImageView metro_map;
    Bitmap bitmap;
    Bitmap resized;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferces = getSharedPreferences("Setting", 0);

        menu = findViewById(R.id.menuLayout);
        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        lostThingBtn = (Button) findViewById(R.id.lostThingBtn);
        fontBtn = (Button) findViewById(R.id.fontBtn);
        handModeBtn = (Button) findViewById(R.id.handModeBtn);
        themeBtn = (Button) findViewById(R.id.themeBtn);
        searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        navigationBtn = new ImageButton[]{
                (ImageButton) findViewById(R.id.navigationBtnRight),
                (ImageButton) findViewById(R.id.navigationBtnLeft)};

        Button testBtn = (Button) findViewById(R.id.testButton);

        fontSettingDialog = new Dialog(this);
        fontSettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fontSettingDialog.setContentView(R.layout.font_setting_dialog);

        Resources res = getResources();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.metro_map);
        resized = Bitmap.createScaledBitmap(bitmap, 5000, 5000, true);
        metro_map = (SubsamplingScaleImageView) findViewById(R.id.metro_map);
        metro_map.setImage(ImageSource.bitmap(resized));

        int x = resized.getHeight();
        int y = resized.getWidth();
        Log.v("size", "size : "+ x + "," + y);

        int x2 = metro_map.getSHeight();
        int y2 = metro_map.getSWidth();
        Log.v("size", "size : "+ x2 + "," + y2);

        metro_map.setMinScale(1.0f);
        metro_map.setMaxScale(2.0f);

        metro_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    PointF sCoord  =  metro_map.viewToSourceCoord(event.getX(),  event.getY());
                    int  x_cor  =  (int)  sCoord.x;
                    int  y_cor  =  (int)  sCoord.y;
                    Log.v("location", "x : " + x_cor + ", y : " + y_cor );
                    float scaleValue = metro_map.getScale();

                    Log.v("scale", "scale : " + scaleValue);

//                    testBtn.setVisibility(View.VISIBLE);
//                    testBtn.setX(event.getX());
//                    testBtn.setY(event.getY());
                }

                return false;
            }
        });

//        Button pathFinding = (Button) findViewById(R.id.pathfinding);
//
//        pathFinding.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, PathFindingActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onStart() {
        super.onStart();

        fontSizeValue = preferces.getInt("fontSize", 14);
        handValue = preferces.getString("handMode", "right");
        themeValue = preferces.getString("theme", "Day");

        profileLayout = (ConstraintLayout) findViewById(R.id.profileLayout);
        profileLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6A72FF")));

        if(handValue.equals("right")){
            handModeBtn.setText("왼손모드");

            navigationBtn[1].setVisibility(View.INVISIBLE);
            navigationBtn[0].setVisibility(View.VISIBLE);
        }
        else {
            handModeBtn.setText("오른손모드");

            navigationBtn[0].setVisibility(View.INVISIBLE);
            navigationBtn[1].setVisibility(View.VISIBLE);
        }

        if(themeValue.equals("Day")){
            Drawable[] imgs = {this.getResources().getDrawable(R.drawable.lost_property_day, null),
                    this.getResources().getDrawable(R.drawable.font_day, null),
                    this.getResources().getDrawable(R.drawable.day_mode, null)};

            Button[] buttons = {lostThingBtn, fontBtn, themeBtn};
            for(int i = 0; i < imgs.length; i++){
                imgs[i].setBounds(0, 0, 60, 60);
                buttons[i].setCompoundDrawables(imgs[i], null, null, null);
            }

            Drawable handImg;
            if(handValue.equals("right")){
                handImg = this.getResources().getDrawable(R.drawable.left_mode_day, null);
            }
            else{
                handImg = this.getResources().getDrawable(R.drawable.right_mode_day, null);
            }
            handImg.setBounds(0, 0, 60, 60);
            handModeBtn.setCompoundDrawables(handImg, null, null, null);
        }
        else{
            Drawable[] imgs = {this.getResources().getDrawable(R.drawable.lost_property_night, null),
                    this.getResources().getDrawable(R.drawable.font_night, null),
                    this.getResources().getDrawable(R.drawable.dark_mode, null)};

            Button[] buttons = {lostThingBtn, fontBtn, themeBtn};
            for(int i = 0; i < imgs.length; i++){
                imgs[i].setBounds(0, 0, 60, 60);
                buttons[i].setCompoundDrawables(imgs[i], null, null, null);
            }

            Drawable handImg;
            if(handValue.equals("right")){
                handImg = this.getResources().getDrawable(R.drawable.left_mode_night, null);
            }
            else{
                handImg = this.getResources().getDrawable(R.drawable.right_mode_night, null);
            }
            handImg.setBounds(0, 0, 60, 60);
            handModeBtn.setCompoundDrawables(handImg, null, null, null);
        }
    }

    public void onClick(View view) {
        //menuBtn
        if(view.getId() == R.id.menuBtn){
            if(menu.getVisibility() == View.VISIBLE){
                menu.setVisibility(View.INVISIBLE);
            }
            else{
                menu.setVisibility(View.VISIBLE);
            }
        }

        //lost and find thing
        if(view.getId() == R.id.lostThingBtn){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.lost112.go.kr/lost/lostList.do"));
            startActivity(intent);
        }

        //change fontSize button
        if(view.getId() == R.id.fontBtn){
            showFontSettingDialog(fontSizeValue);
        }

        //change rightHand or leftHand mode
        if(view.getId() == R.id.handModeBtn){
            SharedPreferences.Editor editor = preferces.edit();

            if(handValue.equals("right")){
                handModeBtn.setText("오른손 모드");
                handValue = "left";

                navigationBtn[0].setVisibility(View.INVISIBLE);
                navigationBtn[1].setVisibility(View.VISIBLE);

                Drawable handImg;
                if(themeValue.equals("Day")){
                    handImg = this.getResources().getDrawable(R.drawable.right_mode_day, null);
                }
                else{
                    handImg = this.getResources().getDrawable(R.drawable.right_mode_night, null);
                }
                handImg.setBounds(0, 0, 60, 60);
                handModeBtn.setCompoundDrawables(handImg, null, null, null);
            }
            else{
                handModeBtn.setText("왼손 모드");
                handValue = "right";

                navigationBtn[1].setVisibility(View.INVISIBLE);
                navigationBtn[0].setVisibility(View.VISIBLE);

                Drawable handImg;
                if(themeValue.equals("Day")){
                    handImg = this.getResources().getDrawable(R.drawable.left_mode_day, null);
                }
                else{
                    handImg = this.getResources().getDrawable(R.drawable.left_mode_night, null);
                }
                handImg.setBounds(0, 0, 60, 60);
                handModeBtn.setCompoundDrawables(handImg, null, null, null);
            }
            editor.putString("handMode", handValue);
            editor.commit();

        }

        //change DayTheme or DarkTheme
        if(view.getId() == R.id.themeBtn){
            SharedPreferences.Editor editor = preferces.edit();
            if(themeValue.equals("Day")){
                themeValue = "Night";
            }
            else {
                themeValue = "Day";
            }
            editor.putString("theme", themeValue);
            editor.commit();

            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            this.startActivity(mainIntent);
            System.exit(0);
        }

        //Navigation Button - Unity
        if(view.getId() == R.id.navigationBtnRight || view.getId() == R.id.navigationBtnLeft){
            Intent intent = new Intent(MainActivity.this, PathFindingActivity.class);
            startActivity(intent);
        }
    }

    private void showFontSettingDialog(int value) {
        fontSettingDialog.show();
        TextView fontTest =(TextView) fontSettingDialog.findViewById(R.id.fontSize);
        Button setBtn = (Button) fontSettingDialog.findViewById(R.id.cancelBtn);
        Button cancelBtn = (Button) fontSettingDialog.findViewById(R.id.setBtn);
        SeekBar seekBar = (SeekBar) fontSettingDialog.findViewById(R.id.seekBar);

        seekBar.setProgress(value);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("?", "" + seekBar.getProgress());
                fontTest.setTextSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fontSizeValue = seekBar.getProgress();
                fontSettingDialog.dismiss();

                SharedPreferences.Editor editor = preferces.edit();
                editor.putInt("fontSize", fontSizeValue);
                editor.commit();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fontSettingDialog.dismiss();
            }
        });
    }
}