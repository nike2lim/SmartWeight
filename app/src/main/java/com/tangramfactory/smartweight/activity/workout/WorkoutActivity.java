package com.tangramfactory.smartweight.activity.workout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.breaktime.BreakTimeActivity;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.ble.service.UARTService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.view.CircleClipView;

import java.util.Timer;
import java.util.TimerTask;

public class WorkoutActivity extends BaseAppCompatActivity {
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

//                        if(mGuageBar.getClippingAngle() >= (135 + 270)) {
//                            mGuageBar.setClippingAngle(135);
//                            mGuageBar.setStartAngle(135);
//                            mGuageNiddle.setRotation(45);
//                            maskImageView.setRotation(45);
//                        }
//
//                        mGuageNiddle.setRotation(mGuageNiddle.getRotation()+1);
//                        mGuageBar.setClippingAngle(mGuageBar.getClippingAngle() + 1);
//                        maskImageView.setRotation(maskImageView.getRotation()+1);


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

        registerReceiver(mMotionBroadcastReceiver, makeWorkoutDataIntentFilter());
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                startActivity(new Intent(this, BreakTimeActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMotionBroadcastReceiver);
    }

    private IntentFilter makeWorkoutDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_WEIGHT_DATA);
        return intentFilter;
    }


    boolean isFirstData = false;
    int firstZvalue = 0;
    protected BroadcastReceiver mMotionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int x = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_XVALUE, 0);
                    int y = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_YVALUE, 0);
                    int z = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ZVALUE, 0);

                    if(mGuageNiddle.getRotation() < 45 || z == 0) {
                        mGuageNiddle.setRotation(45);
                        mGuageBar.setClippingAngle(135);
                    }

                    if(mGuageNiddle.getRotation() > 135) {
                        mGuageNiddle.setRotation(135);
                        mGuageBar.setClippingAngle(305);
                    }

                    mGuageNiddle.setRotation(45 + z);
                    mGuageBar.setClippingAngle(135+ z);

                    DebugLogger.d(TAG, "mMotionBroadcastReceiver x = " + x + ", y = " +y + ",z = "+z);
                    DebugLogger.d(TAG, "mMotionBroadcastReceiver x = " + x + ", y = " +y + ",z = "+z);

            }
            });
        }
    };

}