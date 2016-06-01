package com.tangramfactory.smartweight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.view.CircleClipView;

import java.util.Timer;
import java.util.TimerTask;

public class WokoutActivity extends BaseAppCompatActivity {
    public Toolbar toolbar;

    ImageView mAngleNiddle;
    ImageView mGuageNiddle;
    ImageView maskImageView;

//    ImageView mAngleBar;
//    DonutProgress mAngleBar;
    CircleClipView mGuageBar;
    CircleClipView mAngleBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokout);

        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_step, 1));
        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startActivity(new Intent(mContext, DeviceSettingActivity.class));
            }
        });
        setSupportActionBar(toolbar);
    }

    private Timer timer;
    protected void loadCodeView() {

        maskImageView = (ImageView)findViewById(R.id.maskView);
        mAngleNiddle = (ImageView)findViewById(R.id.angle_niddle);
        mGuageNiddle = (ImageView)findViewById(R.id. guage_niddle);


//        mAngleBar = (DonutProgress)findViewById(R.id.angle_bar);
        mGuageBar = (CircleClipView)findViewById(R.id.guage_bar);
        mAngleBar = (CircleClipView)findViewById(R.id.angle_bar);


        mGuageBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mAngleBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        mGuageBar.setClippingAngle(135);
//        mGuageNiddle.setRotation(60);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mGuageBar.getClippingAngle() >= (135 + 270)) {
                            mGuageBar.setClippingAngle(135);
                            mGuageBar.setStartAngle(135);
                            mGuageNiddle.setRotation(45);
                            maskImageView.setRotation(45);
                        }

                        mGuageNiddle.setRotation(mGuageNiddle.getRotation()+1);
                        mGuageBar.setClippingAngle(mGuageBar.getClippingAngle() + 1);
                        maskImageView.setRotation(maskImageView.getRotation()+1);


                        if(mAngleBar.getClippingAngle() >= 180) {
                            mAngleBar.setClippingAngle(0);
                            mAngleNiddle.setRotation(0);
                        }

                        mAngleBar.setClippingAngle(mAngleBar.getClippingAngle() + 1);
                        mAngleNiddle.setRotation(mAngleNiddle.getRotation()+1);
                    }
                });
            }
        }, 1000, 100);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                startActivity(new Intent(this, BreakTimeActivity.class));
                finish();
                break;
        }
    }
}
