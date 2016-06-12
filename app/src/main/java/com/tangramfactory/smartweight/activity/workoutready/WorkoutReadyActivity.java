package com.tangramfactory.smartweight.activity.workoutready;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.activity.workout.WorkoutActivity;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import java.util.ArrayList;

public class WorkoutReadyActivity extends BaseAppCompatActivity {

    private final static int MSG_ID_TIME_COUNT = 0x01;
    private final static int READY_TIME = 5;

    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;

    TextView timeCountTextView;
    ImageButton NextButton;
    TextView exerciseNumTextView;

    private int time_count = READY_TIME;

    private int stepNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_ready);
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

    protected void loadCodeView() {
        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        timeCountTextView = (TextView)findViewById(R.id.second);
        NextButton = (ImageButton)findViewById(R.id.next_btn);


        timeCountTextView.setText(String.valueOf(time_count));
        Message msg = Message.obtain();
        msg.what = MSG_ID_TIME_COUNT;
        mTimeCheck.sendMessageDelayed(msg, 1000);

        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);
        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        exerciseNumTextView = (TextView)findViewById(R.id.exerciseNum);
        exerciseNumTextView.setText(getString(R.string.text_per_exercise, exerciseNum+1, mWorkoutList.size()));
    }

    Handler mTimeCheck = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_TIME_COUNT:
                    time_count--;
                    if(time_count > 0) {
                        timeCountTextView.setText(String.valueOf(time_count));
                        Message sendMsg = Message.obtain();
                        sendMsg.what = MSG_ID_TIME_COUNT;
                        mTimeCheck.sendMessageDelayed(sendMsg, 1000);
                    }else {
                        timeCountTextView.setText(String.valueOf(time_count));
                        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
                        startWorkoutActivity();
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.next_btn:
                mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
                startWorkoutActivity();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
        mTimeCheck = null;
    }

    private void startWorkoutActivity() {
        Intent intent = new Intent(mContext, WorkoutActivity.class);
        intent.putExtra("stepNum", stepNum);
        intent.putExtra("exerciseNum", exerciseNum);
        intent.putExtra("exerciseList", mWorkoutList);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDeviceReady() {
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
        super.onDeviceReady();
    }

    @Override
    protected void onBatteryValueReceived(int value) {
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
        super.onBatteryValueReceived(value);
    }

    @Override
    protected void onDeviceConnected() {
        super.onDeviceConnected();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    @Override
    protected void onDeviceDisconnected() {
        super.onDeviceDisconnected();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    @Override
    protected void onDeviceLinkLoss() {
        super.onDeviceLinkLoss();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    @Override
    public void onBackPressed() {
        stopWorkoutCmd();
        super.onBackPressed();
    }

    public  void stopWorkoutCmd() {
        mApplication.send(CmdConst.CMD_REQUEST_STOP, (byte)0, null);
    }
}