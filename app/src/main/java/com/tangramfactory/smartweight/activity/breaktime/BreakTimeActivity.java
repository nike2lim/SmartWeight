package com.tangramfactory.smartweight.activity.breaktime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.activity.workout.WorkoutActivity;
import com.tangramfactory.smartweight.activity.workoutready.WorkoutReadyActivity;
import com.tangramfactory.smartweight.activity.workoutresult.WorkoutResultActivity;
import com.tangramfactory.smartweight.ble.service.UARTService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;
import com.tangramfactory.smartweight.vo.GuideResultVo;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Arrays;

public class BreakTimeActivity extends BaseAppCompatActivity {
    private final static int MSG_ID_TIME_COUNT = 0x01;
    private final static int READY_TIME = 10;

    private int time_count = READY_TIME;

    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;

    TextView timeCountTextView;
    ImageButton NextButton;

    TextView perSetTextView;
    TextView nextExerciseTextView;
    TextView nextEquipmentTextView;


    private int stepNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();

    DateTime startTime;
    DateTime endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_time);

        startTime = DateTime.now(DateTimeZone.UTC);
        setToolbar();
        loadCodeView();

    }

    private void setToolbar() {
        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);
        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        time_count = mWorkoutList.get(exerciseNum).getBreakTime();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_per_exercise, exerciseNum+1, mWorkoutList.size()));
        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startActivity(new Intent(mContext, DeviceSettingActivity.class));
            }
        });
        setSupportActionBar(toolbar);
    }

    protected void loadCodeView() {
        breakTimeCommnad();
        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        timeCountTextView = (TextView)findViewById(R.id.break_time_sec);
        NextButton = (ImageButton)findViewById(R.id.next_btn);

        WorkoutVo data = null;

        if((exerciseNum+1) >= (mWorkoutList.size())-1) {
            data = mWorkoutList.get(exerciseNum);
        }else {
            data = mWorkoutList.get(exerciseNum+1);
        }

        perSetTextView = (TextView)findViewById(R.id.text_per_set);
        nextExerciseTextView = (TextView)findViewById(R.id.next_exercise);
        nextEquipmentTextView = (TextView)findViewById(R.id.next_equipment);
        perSetTextView.setText(getString(R.string.text_per_set, data.getCurrentSetNum()+1, data.getTotalSetNum()));
        nextExerciseTextView.setText(data.getExerciseName());
        nextEquipmentTextView.setText(data.getEquipment());

        timeCountTextView.setText(String.valueOf(time_count));
//        Message msg = Message.obtain();
//        msg.what = MSG_ID_TIME_COUNT;
//        mTimeCheck.sendMessageDelayed(msg, 1000);

        registerReceiver(mBreakTimeBroadcastReceiver, makeBreakTimeDataIntentFilter());

    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    //    Handler mTimeCheck = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_ID_TIME_COUNT:
//                    time_count--;
//                    if(time_count > 0) {
//                        timeCountTextView.setText(String.valueOf(time_count));
//                        Message sendMsg = Message.obtain();
//                        sendMsg.what = MSG_ID_TIME_COUNT;
//                        mTimeCheck.sendMessageDelayed(sendMsg, 1000);
//                    }else {
//                        timeCountTextView.setText(String.valueOf(time_count));
//                        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
//
//                        startNextActivity();
//                    }
//                    break;
//            }
//            return false;
//        }
//    });

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.next_btn:
//                mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
                startNextActivity();
                break;
        }
    }

    private void startNextActivity() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);

        breakTimeEndCommnad();

        GuideResultVo resultVo = mApplication.mGuideResultVo.get(exerciseNum);
        endTime = DateTime.now(DateTimeZone.UTC);


        ArrayList setInfoList = resultVo.getSetInfoList();
        GuideResultVo.SetInfo setInfo = (GuideResultVo.SetInfo) setInfoList.get(data.getCurrentSetNum());
        setInfo.setRestTime(endTime.getMillis() - startTime.getMillis());
        resultVo.setInfo(data.getCurrentSetNum(), setInfo);

//        int[] restTimeVal = SmartWeightUtility.getTimeHMS(endTime.getMillis() - startTime.getMillis());
//
//        String restTime = "-";
//        if(restTimeVal[1] > 0) {
//            restTime = String.valueOf(restTimeVal[1]) + "min";
//        }
//
//        if(restTimeVal[2] > 0) {
//            if(restTime.contains("-")) {
//                restTime = "";
//            }
//            restTime = restTime + String.valueOf(restTimeVal[2]) + "sec";
//        }

        String totalTime ="-";
        int[] totalTimeVal = SmartWeightUtility.getTimeHMS(endTime.getMillis() - resultVo.getStartTime().getMillis());

        if(totalTimeVal[1] > 0) {

            totalTime = String.valueOf(totalTimeVal[1]) + "min";
        }

        if(totalTimeVal[2] > 0) {
            if(totalTime.contains("-")) {
                totalTime = "";
            }
            totalTime = totalTime + String.valueOf(totalTimeVal[2]) + "sec";
        }

//        resultVo.setRestTime(restTime);
        resultVo.setTotalTime(totalTime);
        mApplication.mGuideResultVo.set(exerciseNum, resultVo);

        if(data.getCurrentSetNum() == (data.getTotalSetNum() - 1) && exerciseNum == (mWorkoutList.size() - 1)) {
            startActivity(new Intent(mContext, WorkoutResultActivity.class));
        }else if(data.getCurrentSetNum() != (data.getTotalSetNum() - 1)) {
            int setNum = data.getCurrentSetNum() +1;
            data.setCurrentSetNum(setNum);
            mWorkoutList.set(exerciseNum , data);

//            Intent intent = new Intent(mContext, WorkoutActivity.class);
            Intent intent = new Intent(mContext, WorkoutReadyActivity.class);
            intent.putExtra("stepNum", stepNum);
            intent.putExtra("exerciseNum", exerciseNum);
            intent.putExtra("exerciseList", mWorkoutList);
            startActivity(intent);
        }else {
            exerciseNum++;
            Intent intent = new Intent(mContext, WorkoutReadyActivity.class);
            intent.putExtra("stepNum", stepNum);
            intent.putExtra("exerciseNum", exerciseNum);
            intent.putExtra("exerciseList", mWorkoutList);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mBreakTimeBroadcastReceiver) {
            unregisterReceiver(mBreakTimeBroadcastReceiver);
        }

//        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
//        mTimeCheck = null;
    }

    public void breakTimeCommnad() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);
        int time = data.getBreakTime();

        byte[] timeArr = SmartWeightUtility.intToByteArray(time);
        mApplication.send(CmdConst.CMD_REQUEST_BREAK, (byte)4, timeArr);
    }

    public void breakTimeEndCommnad() {
        final byte[] blankByte = new byte[4];
        Arrays.fill(blankByte, (byte)0);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mApplication.send(CmdConst.CMD_REQUEST_BREAK, (byte)4, blankByte);
            }
        }, 400);

    }

    private IntentFilter makeBreakTimeDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_BREAK_TIME);
        return intentFilter;
    }

    protected BroadcastReceiver mBreakTimeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int breakTime = intent.getIntExtra(UARTService.ACTION_RECEIVE_BREAK_TIME_DATA, 0);

                    timeCountTextView.setText(String.valueOf(breakTime));
                    DebugLogger.d(TAG, "mBreakTimeBroadcastReceiver breakTime = " + breakTime);

                    if(breakTime <= 0 ){
                        unregisterReceiver(mBreakTimeBroadcastReceiver);
                        mBreakTimeBroadcastReceiver = null;
                        startNextActivity();
                    }
                }
            });
        }
    };

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
        mApplication.mGuideResultVo.clear();
        super.onBackPressed();
    }
}
