package com.example.beacon_making_kotlin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {
    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.splash);

        SharedPreferences preferces = getSharedPreferences("Setting", 0);

        Resources res = getResources();
        BitmapDrawable bitmap;
        ImageView splashImage = (ImageView) findViewById(R.id.splashImage);

        if(preferces.getString("theme", "Day").equals("Day")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            bitmap = (BitmapDrawable) res.getDrawable(R.drawable.splashimage, null);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            bitmap = (BitmapDrawable) res.getDrawable(R.drawable.splashimage_night, null);
        }

        splashImage.setImageDrawable(bitmap);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 2000);
    }

    private class SplashHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish();
        }
    }


}
