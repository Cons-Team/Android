package com.example.beacon_making_kotlin.beaconfind;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.beacon_making_kotlin.R;

public class LoadingDialog extends Dialog {

    ImageView loadingImage;
    Button tempBtn1;
    Button tempBtn2;
    public LoadingDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);

        loadingImage = (ImageView) findViewById(R.id.loadingImage);

        tempBtn1 = (Button) findViewById(R.id.tempBtn1);
        tempBtn2 = (Button) findViewById(R.id.tempBtn2);

        tempBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tempBtn2.getText().equals("확인")){
                    success();
                }
                else{
                    dismiss_loading_dialog();
                }
            }
        });

        tempBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempBtn2.getText().equals("연동 실패")){
                    fail();
                }
                else{
                    dismiss_loading_dialog();
                }
            }
        });

    }


    public void onStart(){

        loadingImage.setImageResource(R.drawable.loading);
        tempBtn1.setVisibility(View.VISIBLE);
        tempBtn2.setText("연동 실패");
    }

    public void success(){
        loadingImage.setImageResource(R.drawable.success);
        tempBtn1.setVisibility(View.INVISIBLE);
        tempBtn2.setText("확인");
    }

    public void fail(){
        loadingImage.setImageResource(R.drawable.fail);
        tempBtn1.setVisibility(View.INVISIBLE);
        tempBtn2.setText("닫기");
    }

    public void dismiss_loading_dialog(){
        dismiss();
    }
}
