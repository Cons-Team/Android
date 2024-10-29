package com.example.beacon_making_kotlin.beaconfind;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.beacon_making_kotlin.MetroMap.Metro_map_fragment;
import com.example.beacon_making_kotlin.R;

public class LoadingDialog extends Dialog {

    ImageView loadingImage;
    ProgressBar progressBar;
    TextView loadingText;

    public LoadingDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
    }

    @Override
    public void show() {
        super.show();
        loadingImage = (ImageView) findViewById(R.id.loadingImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingText = (TextView) findViewById(R.id.loadingText);
        loadingImage.setImageResource(R.drawable.loading);

        startProgressBar(Metro_map_fragment.currentActivity);
    }

    public void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    public void startProgressBar(Activity activity) {
        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress++) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int currentProgress = progress;

                // Activity를 통해 UI 업데이트
                activity.runOnUiThread(() -> setProgress(currentProgress));
            }
        }).start();
    }

    public void dismiss_loading_dialog(){
        dismiss();
    }
}
