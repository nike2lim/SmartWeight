package com.tangramfactory.smartweight.activity.workout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tangramfactory.smartweight.vo.GuideResultVo;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;

public class WorkoutAnaliticActivity extends BaseAppCompatActivity {
    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;

    ImageView greenImageView;
    ImageView yellowImageView;
    ImageView redImageView;

    ImageView maskLoadingImageView;
    TextView countTextView;
    TextView exerciseInfoTextView;
    ImageView notExerciseImageView;

    private int stepNum = 0;
    private int setNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();

    private SoundPool soundPool;
    private int soundID;

    Animation blinkAnimation;
    Animation fadeInAnimation;
    Animation fadeOutAnimation;

    boolean isWrongWorkOut = false;
    boolean isCountRecevied = false;
    boolean isNotWorkout = false;
    int mCount;
    int mAccuracy;

    DateTime startTime;
    DateTime endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_analitic);

        setToolbar();
        loadCodeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_list_btn:
                onBackPressed();
                break;

            case R.id.next_btn:
                nextStep();
                break;
        }
    }

    private void setToolbar() {
        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);
        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        WorkoutVo data = mWorkoutList.get(exerciseNum);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(data.getExerciseName());
//        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ScanActivity.class);
//                intent.putExtra("isUpdate", true);
//                startActivity(intent);
//            }
//        });
        setSupportActionBar(toolbar);
    }

    protected void loadCodeView() {
        startWorkoutCmd();

        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        countTextView = (TextView)findViewById(R.id.count);
        exerciseInfoTextView = (TextView)findViewById(R.id.exercise_infoText);
        notExerciseImageView = (ImageView)findViewById(R.id.not_exercise_imageview);

        greenImageView = (ImageView) findViewById(R.id.green_imageview);
        yellowImageView = (ImageView) findViewById(R.id.yellow_imageview);
        redImageView = (ImageView) findViewById(R.id.red_imageview);
        maskLoadingImageView = (ImageView)findViewById(R.id.maskView);

        blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
        fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);

        startTime = DateTime.now(DateTimeZone.UTC);

        setSoundPool();
        registerReceiver(mExerciseBroadcastReceiver, makeExerciseDataIntentFilter());
        registerReceiver(mAngleBroadcastReceiver, makeAngletDataIntentFilter());
        registerReceiver(mCountBroadcastReceiver, makeCountDataIntentFilter());
        registerReceiver(mNotWorkoutBroadcastReceiver, makeNotWorkoutIntentFilter());
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

    private IntentFilter makeNotWorkoutIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_NOT_WORKOUT);
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

                    DebugLogger.d(TAG, "WorkoutData receive exerciseName = " + exerciseName);
                    DebugLogger.d(TAG, "mExerciseBroadcastReceiver receive exerciseName = " + exerciseName);

//                    if(false == data.getExerciseName().equals(exerciseName)) {
                    if(exerciseName.equals(getString(R.string.text_shoulder_press))) {
                        isWrongWorkOut = true;
                        stopNotWorkAnimation();
                        countTextView.clearAnimation();

                        notExerciseImageView.setVisibility(View.VISIBLE);
                        exerciseInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        exerciseInfoTextView.setText(Html.fromHtml(getString(R.string.text_not_correct_workout)));
                    }else if(exerciseName.equals(getString(R.string.text_lateral_raises))) {
//                        DebugLogger.d(TAG, "TextColor 노란색");

//                        notExerciseImageView.setVisibility(View.GONE);
//                        exerciseInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
//                        exerciseInfoTextView.setText("-");
                        isWrongWorkOut = false;
                        countTextView.setTextColor(Color.parseColor("#FFFF00"));
                    }else {
//                        notExerciseImageView.setVisibility(View.GONE);
//                        exerciseInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
//                        exerciseInfoTextView.setText("-");
                            isWrongWorkOut = false;
//                            DebugLogger.d(TAG, "TextColor 흰색");
                            countTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        }
                    }
            });
        }
    };

    protected BroadcastReceiver mAngleBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int x = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_XVALUE, 0);
                    int y = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_YVALUE, 0);
                    int z = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ZVALUE, 0);
                    int angle = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ANGLE, 0);

                    if(false == isCountRecevied)            return;
                    if(true == isNotWorkout)                return;
                    if(true == isWrongWorkOut)              return;

                    DebugLogger.d(TAG, "WorkoutData X = " +x + ", Y = " + y + ", Z = " + z);
                    DebugLogger.d(TAG, "WorkoutData Angle = " + angle);

                    stopNotWorkAnimation();
                    countTextView.clearAnimation();
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

                    int count = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_COUNT, 0);
                    int accuracy = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_ACCURACY, 0);

                    DebugLogger.d(TAG, "WorkoutData count = " +  count + ", accuracy = " + accuracy );

                    if(count < 3) {
                        return;
                    }

                    notExerciseImageView.setVisibility(View.GONE);
                    isCountRecevied = true;
                    isNotWorkout = false;

                    stopNotWorkAnimation();
                    countTextView.clearAnimation();


                    WorkoutVo data = mWorkoutList.get(exerciseNum);
                    mCount = count;

//                    if(count > data.getReps()) {
//                        unregisterReceiver(mCountBroadcastReceiver);
//                        mCountBroadcastReceiver = null;
//                        nextStep();
//                        return;
//                    }
//
//                    int tmp = count%10;
//                    if(count == 10 ) {
//                        tmp = 10;
//                    }
//
//                    int resId = getResources().getIdentifier("count_" + tmp, "raw", mContext. getPackageName());
//                    soundID = soundPool.load(mContext, resId, 1);

//                    tmp = 0;
//                    if(count -1 <= 0) {
//                        tmp = 1;
//                    }else {
//                        tmp = count;
//                    }
//
//                    if(tmp >= (data.getReps()-1)) {
//                        tmp = data.getReps() -1;
//                    }

//                    mAccuracy[tmp-1] = accuracy;
                        mAccuracy = accuracy;

                    exerciseInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
                    if(accuracy > 90) {
                        exerciseInfoTextView.setText("GREAT");
                        greenImageView.setColorFilter(Color.parseColor("#00000000"));
                        yellowImageView.setColorFilter(Color.parseColor("#99000000"));
                        redImageView.setColorFilter(Color.parseColor("#99000000"));

                    }else if(accuracy > 70 && accuracy <= 89) {
                        exerciseInfoTextView.setText("GOOD");
                        greenImageView.setColorFilter(Color.parseColor("#99000000"));
                        yellowImageView.setColorFilter(Color.parseColor("#00000000"));
                        redImageView.setColorFilter(Color.parseColor("#99000000"));
                    }else {
                        exerciseInfoTextView.setText("BAD");
                        greenImageView.setColorFilter(Color.parseColor("#99000000"));
                        yellowImageView.setColorFilter(Color.parseColor("#99000000"));
                        redImageView.setColorFilter(Color.parseColor("#00000000"));
                    }

                    countTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 120);
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

    protected BroadcastReceiver mNotWorkoutBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mGaugeBg.setVisibility(View.GONE);

                    DebugLogger.d(TAG, "WorkoutData Not during exercise!!!!!!!!!");

//                        exerciseInfoTextView.setText("-");
//                        exerciseInfoTextView.startAnimation(blinkAnimation);

                    notExerciseImageView.setVisibility(View.GONE);
                    exerciseInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
                    exerciseInfoTextView.setText("-");

                    if(mCount == 0) {
                        countTextView.startAnimation(blinkAnimation);
                        exerciseInfoTextView.setText("운동을 시작해 주세요.");
                    }else {
                        exerciseInfoTextView.setText("멈추지 말고 운동을 시작해 주세요.");
                    }

                    maskLoadingImageView.startAnimation(fadeInAnimation);
                    fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
                    fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);

//                    if(false ==  SmartWeightUtility.currentActivityName(mContext).equals(WarningActivity.WARNING_ACTIVITY_PACKAGENAE)) {
//                        initAngleUI();
//
//                        Intent startIntent = new Intent(mContext, WarningActivity.class);
//                        startIntent.putExtra(WarningActivity.WARNING_TYPE, WarningActivity.WARNING_TYPE_NOT_WORKING);
//                        startActivityForResult(startIntent, 0x99);
//                    }
                }
            });
        }
    };


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

        if(soundID != 0) {
            soundPool.stop(soundID);
        }
        int resId = getResources().getIdentifier("go", "raw", mContext. getPackageName());
        soundID = soundPool.load(mContext, resId, 1);

    }

    int maskState = 0;
    Animation.AnimationListener mFadeInAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
//            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationStart");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            maskLoadingImageView.setAnimation(fadeOutAnimation);
            fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);
            fadeOutAnimation.start();
//            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationEnd");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
//            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationRepeat");
        }
    };

    Animation.AnimationListener mFadeOutAnimationListener = new Animation.AnimationListener() {


        @Override
        public void onAnimationStart(Animation animation) {
//            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationStart");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            maskState++;
            if(maskState > 2) {
                maskState = 1;
            }
            int resId = getResources().getIdentifier("mask_loading" + maskState, "drawable", mContext. getPackageName());
//            maskLoadingImageView.setBackgroundResource(resId);
            maskLoadingImageView.setImageResource(resId);
            maskLoadingImageView.setAnimation(fadeInAnimation);
            fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
            fadeInAnimation.start();
//            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationEnd");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
//            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationRepeat");
        }
    };

    private void stopNotWorkAnimation() {
        fadeInAnimation.cancel();
        fadeOutAnimation.cancel();
        fadeInAnimation.setAnimationListener(null);
        fadeOutAnimation.setAnimationListener(null);

        maskLoadingImageView.clearAnimation();
//        maskLoadingImageView.setVisibility(View.GONE);

    }



    public  void startWorkoutCmd() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);
        String exerciseName = data.getExerciseName();

        final byte[] exerciseCode = SmartWeightUtility.getExerciseCode(mContext, exerciseName);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mApplication.send(CmdConst.CMD_REQUEST_START, (byte)5, exerciseCode);
            }
        }, 400);
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
        mApplication.mGuideResultVo.clear();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != mExerciseBroadcastReceiver) {
            unregisterReceiver(mExerciseBroadcastReceiver);
        }

        if(null != mAngleBroadcastReceiver) {
            unregisterReceiver(mAngleBroadcastReceiver);
        }
        if(null != mCountBroadcastReceiver) {
            unregisterReceiver(mCountBroadcastReceiver);
        }

        if(null != mNotWorkoutBroadcastReceiver) {
            unregisterReceiver(mNotWorkoutBroadcastReceiver);
        }

        if(null != soundPool) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void saveWorkoutData() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);
        GuideResultVo resultVo = null;
//        int progress =(int) (((float)mCount / ((float)data.getReps() * data.getTotalSetNum())) * 100);
        int progress = 0;
        long totalRestTime = 0;

        boolean isExistData = false;
        for(GuideResultVo vo : mApplication.mGuideResultVo) {
            if(vo.getExerciseName().equals(data.getExerciseName())) {
                resultVo = vo;
                isExistData = true;
            }
        }

        if(false == isExistData) {
            resultVo = new GuideResultVo(progress, data.getExerciseName(), startTime);
        }

        ArrayList<GuideResultVo.SetInfo> setInfoList = resultVo.getSetInfoList();
        int totalCount = 0;
        for(GuideResultVo.SetInfo info : setInfoList) {
            totalCount = totalCount + Integer.valueOf(info.getResultReps());
            totalRestTime = totalRestTime + info.getSetRestTime();
        }
        totalCount = totalCount + mCount;
        progress =(int) (((float)totalCount / ((float)data.getReps() * data.getTotalSetNum())) * 100);
        resultVo.setProgress(progress);

        int accuracy = 0;

//        for(int i=0; i < mAccuracy.length; i++) {
//            accuracy = accuracy + mAccuracy[i];
//        }
//        accuracy = (accuracy / mAccuracy.length);

        accuracy = mAccuracy;

        resultVo.addSetInfo(String.valueOf(data.getCurrentSetNum()+1), data.getWeight(), data.getWeightUnit(), String.valueOf(data.getReps()),  String.valueOf(mCount), String.valueOf(accuracy));

//        DebugLogger.d(TAG, "saveWorkoutData exerciseName = " + data.getExerciseName() + ", progress = " + progress);
//        DebugLogger.d(TAG, "saveWorkoutData set = " + data.getCurrentSetNum()+1 + ", data.getWeight() = " + data.getWeight());
//        DebugLogger.d(TAG, "saveWorkoutData mCount = " + mCount + ", accuracy = " + accuracy);

        if(exerciseNum == (mWorkoutList.size() - 1)) {
            int[] restTimeVal = SmartWeightUtility.getTimeHMS(totalRestTime);
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

//        for(GuideResultVo vo : mApplication.mGuideResultVo) {
//
//                resultVo = vo;
//                isExistData = true;
//            }
//        }

        isExistData = false;
        for(int i=0; i <  mApplication.mGuideResultVo.size() ; i++) {
            GuideResultVo vo =  mApplication.mGuideResultVo.get(i);
            if(vo.getExerciseName().equals(data.getExerciseName())) {
                mApplication.mGuideResultVo.set(i, vo);
                isExistData = true;
            }
        }

        if(false == isExistData) {
            mApplication.mGuideResultVo.add(resultVo);
        }
    }

    private void nextStep() {
        endTime = DateTime.now(DateTimeZone.UTC);
        saveWorkoutData();

        WorkoutVo data = mWorkoutList.get(exerciseNum);

//        DebugLogger.d(TAG, "nextStep data.getCurrentSetNum() = " + data.getCurrentSetNum());
//        DebugLogger.d(TAG, "nextStep data.getTotalSetNum()  = " + data.getTotalSetNum() );
//        DebugLogger.d(TAG, "nextStep exerciseNum  = " + exerciseNum);
//        DebugLogger.d(TAG, "nextStep mWorkoutList.size()  = " + mWorkoutList.size());

        if(data.getCurrentSetNum() == (data.getTotalSetNum() -1) && exerciseNum == (mWorkoutList.size()-1)) {
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
}
