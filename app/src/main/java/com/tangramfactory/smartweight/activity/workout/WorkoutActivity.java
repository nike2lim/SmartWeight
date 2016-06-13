package com.tangramfactory.smartweight.activity.workout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.breaktime.BreakTimeActivity;
import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.activity.workoutresult.WorkoutResultActivity;
import com.tangramfactory.smartweight.ble.service.UARTService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;
import com.tangramfactory.smartweight.view.CircleClipView;
import com.tangramfactory.smartweight.vo.GuideResultVo;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class WorkoutActivity extends BaseAppCompatActivity {
    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;


    FrameLayout mRootContentLayout;

    ImageView mAngleNiddle;
    ImageView mGuageNiddle;
    ImageView maskImageView;

    CircleClipView mGuageBar;
    CircleClipView mAngleBar;
    ImageView setImageView;

    TextView equipmentTextView;
    TextView repsTextView;
    TextView weightTextView;
    TextView countTextView;

    TextView angleLeftRightTextView;
    TextView angleTextView;

    private int stepNum = 0;
    private int setNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();

    DateTime startTime;
    DateTime endTime;

    int mCount;
    int[] mAccuracy;

    private SoundPool soundPool;
    private int soundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokout);

        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);
        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        WorkoutVo data = mWorkoutList.get(exerciseNum);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(data.getExerciseName());
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
        startWorkoutCmd();

        mRootContentLayout = (FrameLayout)findViewById(R.id.root_content_layout);

        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        setImageView = (ImageView)findViewById(R.id.setImage);
        equipmentTextView = (TextView)findViewById(R.id.equipment);
        repsTextView = (TextView)findViewById(R.id.reps_count);
        weightTextView = (TextView)findViewById(R.id.weight);
        countTextView = (TextView)findViewById(R.id.count);
        angleLeftRightTextView = (TextView)findViewById(R.id.angle_left_right);
        angleTextView = (TextView)findViewById(R.id.angle_text);

        WorkoutVo data = mWorkoutList.get(exerciseNum);
        setNum = data.getCurrentSetNum();
        mAccuracy = new int[data.getReps()];
        Arrays.fill(mAccuracy, 0);

        setImageView.setBackgroundResource(getResources().getIdentifier("workout_set_0" + (setNum + 1), "drawable", getPackageName()));

        equipmentTextView.setText(data.getEquipment());
        repsTextView.setText(String.valueOf(data.getReps()));
        weightTextView.setText(data.getWeight() + data.getWeightUnit());

        maskImageView = (ImageView)findViewById(R.id.maskView);
        maskImageView.setVisibility(View.VISIBLE);
        mAngleNiddle = (ImageView)findViewById(R.id.angle_niddle);
        mGuageNiddle = (ImageView)findViewById(R.id. guage_niddle);

        mGuageBar = (CircleClipView)findViewById(R.id.guage_bar);
        mAngleBar = (CircleClipView)findViewById(R.id.angle_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mGuageBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mAngleBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else {
            mGuageBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mAngleBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        startTime = DateTime.now(DateTimeZone.UTC);

        angleTextView.setText(getString(R.string.text_angle, 0));

//        mGuageBar.setClippingAngle(135);
//        mGuageNiddle.setRotation(60);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float rotation = maskImageView.getRotation()+ 20;
                        if(rotation > 315) {
                            rotation = 65;
                        }
                        maskImageView.setRotation(rotation);

//                        if(mGuageBar.getClippingAngle() >= (135 + 270)) {
//                            mGuageBar.setClippingAngle(135);
//                            mGuageBar.setStartAngle(135);
//                            mGuageNiddle.setRotation(45);
//                            maskImageView.setRotation(45);
//                            testZ = 0;
//                        }
//
//                        DebugLogger.d(TAG, "test testZ = " +  testZ);
//                        DebugLogger.d(TAG, "test mGuageBar.getClippingAngle() = " +  mGuageBar.getClippingAngle());
//                        DebugLogger.d(TAG, "test mGuageNiddle.getRotation() = " +  mGuageNiddle.getRotation());
//
//                        float t = (float)(testZ * 1.35);
//
//                        mGuageNiddle.setRotation(mGuageNiddle.getRotation() + t);
//                        mGuageBar.setClippingAngle(mGuageBar.getClippingAngle() + t);
//                        maskImageView.setRotation(maskImageView.getRotation() + t);
//
//                        testZ++;

//                        if(mAngleBar.getClippingAngle() >= 180) {
//                            mAngleBar.setClippingAngle(0);
//                            mAngleNiddle.setRotation(0);
//                        }
//
//                        mAngleBar.setClippingAngle(mAngleBar.getClippingAngle() + 1);
//                        mAngleNiddle.setRotation(mAngleNiddle.getRotation()+1);
                    }
                });
            }
        }, 300, 100);

        setSoundPool();
        registerReceiver(mExerciseBroadcastReceiver, makeExerciseDataIntentFilter());
        registerReceiver(mAngleBroadcastReceiver, makeAngletDataIntentFilter());
        registerReceiver(mCountBroadcastReceiver, makeCountDataIntentFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                nextStep();
                break;
        }
    }

    private void setSoundPool() {

        if (Build.VERSION.SDK_INT < 21 /* Android 5.0 */) {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                float actualVolume = (float) audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = (float) audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = actualVolume / maxVolume;
                soundPool.play(soundID, volume, volume, 1, 0, 1f);
            }
        });
    }


    private void saveWorkoutData() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);

//        switch(exerciseNum) {
//            case 0: count = 5; accuracy = "80";     break;
//            case 1: count = 6; accuracy = "90";     break;
//            case 2: count = 7; accuracy = "100";    break;
//            case 3: count = 2; accuracy = "80";     break;
//            case 4: count = 3; accuracy = "100";    break;
//        }

        int progress =(int) (((float)mCount / (float)data.getReps()) * 100);
        GuideResultVo resultVo = new GuideResultVo(progress, data.getExerciseName(), startTime);

        int accuracy = 0;

        for(int i=0; i < mAccuracy.length; i++) {
            accuracy = accuracy + mAccuracy[i];
        }
        accuracy = (accuracy / mAccuracy.length);

        resultVo.addSetInfo(String.valueOf(data.getCurrentSetNum()+1), data.getWeight(), data.getWeightUnit(), String.valueOf(data.getReps()),  String.valueOf(mCount), String.valueOf(accuracy));

        DebugLogger.d(TAG, "saveWorkoutData exerciseName = " + data.getExerciseName() + ", progress = " + progress);
        DebugLogger.d(TAG, "saveWorkoutData set = " + data.getCurrentSetNum()+1 + ", data.getWeight() = " + data.getWeight());
        DebugLogger.d(TAG, "saveWorkoutData mCount = " + mCount + ", accuracy = " + accuracy);

        if(exerciseNum == (mWorkoutList.size() - 1)) {
            int[] restTimeVal = SmartWeightUtility.getTimeHMS(endTime.getMillis() - startTime.getMillis());
            String restTime = "-";
            if(restTimeVal[1] > 0) {
                restTime = String.valueOf(restTimeVal[1]) + "min";
            }

            if(restTimeVal[2] > 0) {
                if(restTime.contains("-")) {
                    restTime = "";
                }
                restTime = restTime + String.valueOf(restTimeVal[2]) + "sec";
            }

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

            resultVo.setRestTime(restTime);
            resultVo.setTotalTime(totalTime);
        }

        mApplication.mGuideResultVo.add(resultVo);
    }

    private void nextStep() {
        endTime = DateTime.now(DateTimeZone.UTC);
        saveWorkoutData();

        if(exerciseNum == (mWorkoutList.size()-1)) {
            startActivity(new Intent(this, WorkoutResultActivity.class));
            finish();
        } else {
            Intent intent = new Intent(mContext, BreakTimeActivity.class);
            intent.putExtra("stepNum", stepNum);
            intent.putExtra("exerciseNum", exerciseNum);
            intent.putExtra("exerciseList", mWorkoutList);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != timer) {
            timer.cancel();
            timer = null;
        }

        if(null != mExerciseBroadcastReceiver) {
            unregisterReceiver(mExerciseBroadcastReceiver);
        }

        if(null != mAngleBroadcastReceiver) {
            unregisterReceiver(mAngleBroadcastReceiver);
        }
        if(null != mCountBroadcastReceiver) {
            unregisterReceiver(mCountBroadcastReceiver);
        }

        if(null != soundPool) {
            soundPool.release();
            soundPool = null;
        }
    }

    private IntentFilter makeExerciseDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_EXERCISE_DATA);
        return intentFilter;
    }


    private IntentFilter makeAngletDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_ANGLE_DATA);
        return intentFilter;
    }

    private IntentFilter makeCountDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_COUNT_DATA);
        return intentFilter;
    }


    protected BroadcastReceiver mExerciseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String exerciseName = intent.getStringExtra(UARTService.ACTION_RECEIVE_EXERCISE_DATA_CODE);
                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    DebugLogger.d(TAG, "mExerciseBroadcastReceiver receive exerciseName = " + exerciseName);

                    if(false == data.getExerciseName().equals(exerciseName)) {
                        countTextView.setTextColor(ContextCompat.getColor(mContext,  R.color.red_80));
                    }else {
                        countTextView.setTextColor(ContextCompat.getColor(mContext,  R.color.white));
                    }
                }
            });
        }
    };

    boolean isCountRecevied = false;
    protected BroadcastReceiver mAngleBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(false == isCountRecevied)            return;

                    int x = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_XVALUE, 0);
                    int y = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_YVALUE, 0);
                    int z = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ZVALUE, 0);
                    int angle = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ANGLE, 0);

                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    int gague = 0;
                    if(data.getExerciseName().equals(getString(R.string.text_curl)) || data.getExerciseName().equals(getString(R.string.text_lunge))
                        || data.getExerciseName().equals(getString(R.string.text_shoulder_press))) {
                        gague = z;
                    }else if(data.getExerciseName().equals(getString(R.string.text_lateral_raises)) | data.getExerciseName().equals(getString(R.string.text_kick_back))) {
                        gague = y;
                    }else {
                        gague = x;
                    }

                   if(mGuageNiddle.getRotation() < 45 || z == 0) {
                        mGuageNiddle.setRotation(45);
                        mGuageBar.setClippingAngle(135);
                    }

                    if(mGuageNiddle.getRotation() > 315) {
                        mGuageNiddle.setRotation(315);
                        mGuageBar.setClippingAngle(405);
                    }

//                    if(mGuageNiddle.getRotation() > 135) {
//                        mGuageNiddle.setRotation(135);
//                        mGuageBar.setClippingAngle(405);
//                    }

                    if(z < 0) {
                        z = 0;
                    }
//                    mGuageNiddle.setRotation(45 + z);
//                    mGuageBar.setClippingAngle(135+ z);

                    //z가 100이 들어오면 clip은 270
                    //z가 200이 들어오면 clip은 405

                    float clip = (float)(z * 1.35);

                    //z가 100이 들어오면 angle은 180
                    //z가 200이 들어오면 angle은 315
//                    float angle =(float)(z * 1.35);

                    mGuageNiddle.setRotation(45 + clip);
                    mGuageBar.setClippingAngle(135 + clip);

                    if(angle > 0) {
                        angleLeftRightTextView.setText(getString(R.string.text_angle_right));
                        mAngleBar.setStartAngle(0);
                        mAngleBar.setClippingAngle(angle);
                    }else {
                        angleLeftRightTextView.setText(getString(R.string.text_angle_left));
                        mAngleBar.setStartAngle(180);
                        mAngleBar.setClippingAngle(180 - Math.abs(angle));
                    }
                    angleTextView.setText(getString(R.string.text_angle, angle));
                    mAngleNiddle.setRotation(angle);

                    DebugLogger.d(TAG, "mMotionBroadcastReceiver x = " + x + ", y = " +y + ",z = "+z);
                    DebugLogger.d(TAG, "mMotionBroadcastReceiver x = " + x + ", y = " +y + ",z = "+z);
            }
            });
        }
    };


    protected BroadcastReceiver mCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isCountRecevied = true;
                    if(null != timer) {
                        timer.cancel();
                        timer = null;
                        maskImageView.setVisibility(View.GONE);
                    }

                    int count = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_COUNT, 0);
                    int accuracy = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_ACCURACY, 0);

                    DebugLogger.d(TAG, "mCountBroadcastReceiver count = " + count);
                    DebugLogger.d(TAG, "mMotionBroadcastReceiver accuracy = " + accuracy);

                    WorkoutVo data = mWorkoutList.get(exerciseNum);
                    mCount = count;

                    int tmp = count%10;

                    int resId = getResources().getIdentifier("count_" + tmp, "raw", mContext. getPackageName());
                    soundID = soundPool.load(mContext, resId, 1);

                    tmp = 0;
                    if(count -1 <= 0) {
                        tmp = 1;
                    }else {
                        tmp = count;
                    }

                    if(tmp >= (data.getReps()-1)) {
                        tmp = data.getReps() -1;
                    }

//                    mAccuracy[tmp-1] = accuracy;
                    countTextView.setTextColor(Color.WHITE);
                    countTextView.setText(String.valueOf(count));

//                    if(mCount >= data.getReps()) {
//                        unregisterReceiver(mCountBroadcastReceiver);
//                        mCountBroadcastReceiver = null;
//                        nextStep();
//                    }
                }
            });
        }
    };

    public  void startWorkoutCmd() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);
        String exerciseName = data.getExerciseName();

        byte[] exerciseCode = SmartWeightUtility.getExerciseCode(mContext, exerciseName);
        mApplication.send(CmdConst.CMD_REQUEST_START, (byte)5, exerciseCode);
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

}