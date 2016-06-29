package com.tangramfactory.smartweight.activity.workout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.breaktime.BreakTimeActivity;
import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.activity.device.ScanActivity;
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

public class WorkoutActivity extends BaseAppCompatActivity {
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "WorkoutActivity";

    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;


    FrameLayout mRootContentLayout;

    ImageView mAngleNiddle;
    ImageView mGuageNiddle;

    ImageView maskLoadingImageView;
    ImageView maskLoadingGaugeImageView;

    ImageView mGaugeBg;
    CircleClipView mGuageBar;
    CircleClipView mAngleBar;
    ImageView setImageView;

    TextView equipmentTextView;
    TextView repsTextView;
    TextView weightTextView;
    TextView countTextView;

    LinearLayout mAngleLayout;
    TextView angleLeftRightTextView;
    TextView angleTextView;

    LinearLayout dimLayout;

    LinearLayout mNotCorrectWorkoutLayout;
    TextView mNotCorrectWorkoutTextView;

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
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra("isUpdate", true);
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
    }

    private Timer timer;

    Animation startAnimation;

    Animation fadeInAnimation;
    Animation fadeOutAnimation;

    protected void loadCodeView() {
        startWorkoutCmd();
        mRootContentLayout = (FrameLayout)findViewById(R.id.root_content_layout);

        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        setImageView = (ImageView)findViewById(R.id.setImage);
        equipmentTextView = (TextView)findViewById(R.id.equipment);
        repsTextView = (TextView)findViewById(R.id.reps_count);
        weightTextView = (TextView)findViewById(R.id.weight);
        countTextView = (TextView)findViewById(R.id.count);

        mAngleLayout = (LinearLayout)findViewById(R.id.angle_layout);
        angleLeftRightTextView = (TextView)findViewById(R.id.angle_left_right);
        angleTextView = (TextView)findViewById(R.id.angle_text);

//        countTextView.setTextColor(Color.parseColor("#1E1E1E"));
        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
//        countTextView.startAnimation(startAnimation);

        WorkoutVo data = mWorkoutList.get(exerciseNum);
        setNum = data.getCurrentSetNum();
        mAccuracy = new int[data.getReps()];
        Arrays.fill(mAccuracy, 0);

        setImageView.setBackgroundResource(getResources().getIdentifier("workout_set_0" + (setNum + 1), "drawable", getPackageName()));

        equipmentTextView.setText(data.getEquipment());
        repsTextView.setText(String.valueOf(data.getReps()));
        weightTextView.setText(data.getWeight() + data.getWeightUnit());

        maskLoadingImageView = (ImageView)findViewById(R.id.maskView);
        maskLoadingImageView.setVisibility(View.GONE);

        maskLoadingGaugeImageView = (ImageView)findViewById(R.id.maskGaugeView);
        maskLoadingImageView.setVisibility(View.GONE);

        mAngleNiddle = (ImageView)findViewById(R.id.angle_niddle);
        mGuageNiddle = (ImageView)findViewById(R.id. guage_niddle);

        mGaugeBg = (ImageView)findViewById(R.id.gauge_bg);
        mGuageBar = (CircleClipView)findViewById(R.id.guage_bar);
        mAngleBar = (CircleClipView)findViewById(R.id.angle_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mGuageBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mAngleBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else {
            mGuageBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mAngleBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mNotCorrectWorkoutLayout = (LinearLayout)findViewById(R.id.not_correct_workout_layout);
        mNotCorrectWorkoutTextView = (TextView) findViewById(R.id.text_not_correct_workout);

        dimLayout = (LinearLayout)findViewById(R.id.dim_layout);

        startTime = DateTime.now(DateTimeZone.UTC);

        angleTextView.setText(getString(R.string.text_angle, 0));

        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
        fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);

//        setSoundPool();
        registerReceiver(mExerciseBroadcastReceiver, makeExerciseDataIntentFilter());
        registerReceiver(mAngleBroadcastReceiver, makeAngletDataIntentFilter());
        registerReceiver(mCountBroadcastReceiver, makeCountDataIntentFilter());
        registerReceiver(mNotWorkoutBroadcastReceiver, makeNotWorkoutIntentFilter());
    }

    int maskState = 0;
    Animation.AnimationListener mFadeInAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationStart");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            maskLoadingImageView.setAnimation(fadeOutAnimation);
            fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);
            fadeOutAnimation.start();
            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationEnd");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            DebugLogger.d(TAG, "mFadeInAnimationListener onAnimationRepeat");
        }
    };

    Animation.AnimationListener mFadeOutAnimationListener = new Animation.AnimationListener() {


        @Override
        public void onAnimationStart(Animation animation) {
            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationStart");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            maskState++;
            if(maskState > 2) {
                maskState = 1;
            }
            int resId = getResources().getIdentifier("mask_loading" + maskState, "drawable", mContext. getPackageName());
            maskLoadingImageView.setBackgroundResource(resId);
            maskLoadingImageView.setAnimation(fadeInAnimation);
            fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
            fadeInAnimation.start();
            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationEnd");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            DebugLogger.d(TAG, "mFadeOutAnimationListener onAnimationRepeat");
        }
    };

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

        int resId = getResources().getIdentifier("go", "raw", mContext. getPackageName());
        soundID = soundPool.load(mContext, resId, 1);

    }


    private void saveWorkoutData() {
        WorkoutVo data = mWorkoutList.get(exerciseNum);
        GuideResultVo resultVo = null;
//        int progress =(int) (((float)mCount / ((float)data.getReps() * data.getTotalSetNum())) * 100);
        int progress = 0;

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
        }
        totalCount = totalCount + mCount;
        progress =(int) (((float)totalCount / ((float)data.getReps() * data.getTotalSetNum())) * 100);
        resultVo.setProgress(progress);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(null != timer) {
//            timer.cancel();
//            timer = null;
//        }

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


    boolean isWrongWorkOut = false;
    protected BroadcastReceiver mExerciseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String exerciseName = intent.getStringExtra(UARTService.ACTION_RECEIVE_EXERCISE_DATA_CODE);
                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    isNotWorkout = false;
                    dimLayout.setVisibility(View.GONE);

                    DebugLogger.d(TAG, "mExerciseBroadcastReceiver receive exerciseName = " + exerciseName);

//                    if(false == data.getExerciseName().equals(exerciseName)) {
                    if(exerciseName.equals(getString(R.string.text_shoulder_press))) {
                        isWrongWorkOut = true;

                        initAngleUI();
                        stopNotWorkAnimation();
                        showNotCorrectWorkout();

//                        countTextView.setTextColor(ContextCompat.getColor(mContext,  R.color.red_80));

//                        if(false == SmartWeightUtility.currentActivityName(mContext).equals(WarningActivity.WARNING_ACTIVITY_PACKAGENAE)) {
//
//                            initAngleUI();
//
//                            Intent startIntent = new Intent(mContext, WarningActivity.class);
//                            startIntent.putExtra("exerciseName", exerciseName);
//                            startIntent.putExtra(WarningActivity.WARNING_TYPE, WarningActivity.WARNING_TYPE_WRONG_EXERCISE);
//                            startActivityForResult(startIntent, 0x99);
//                        }
                    }
                    else if(mCount == 0) {
//                        countTextView.setTextColor(Color.parseColor("#1E1E1E"));
                    }
                    else if(exerciseName.equals(getString(R.string.text_lateral_raises))) {
                        DebugLogger.d(TAG, "TextColor 노란색");
                        isWrongWorkOut = false;
                        showAngleLayout();
                        countTextView.setTextColor(Color.parseColor("#FFFF00"));
                    }
                    else {
                        isWrongWorkOut = false;
                        showAngleLayout();
                        if(false == isTextColorLock) {
                            DebugLogger.d(TAG, "TextColor 흰색");
                            countTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        }
                    }
                }
            });
        }
    };

    boolean isCountRecevied = false;
    int prevZ = 0;
    boolean isRising = false;
    boolean isLower = false;
    int savedCount = 0;

    boolean isTextColorLock = false;

    protected BroadcastReceiver mAngleBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if(false == isCountRecevied)            return;
                    if(true == isNotWorkout)                return;
                    if(true == isWrongWorkOut)              return;

                    int x = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_XVALUE, 0);
                    int y = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_YVALUE, 0);
                    int z = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ZVALUE, 0);
                    int angle = intent.getIntExtra(UARTService.ACTION_RECEIVE_WEIGHT_DATA_ANGLE, 0);

                    if(prevZ < z) {
                        isRising = true;
                        isLower = false;
                    }else if(prevZ > z){
                        isRising = false;
                        isLower = true;
                    }else {
                        isRising = false;
                        isLower = false;
                    }

                    DebugLogger.d(TAG, "TextColor prevZ = " + prevZ + ", Z = " + z );
                    DebugLogger.d(TAG, "TextColor savedCount = " + savedCount + ", mCount = " + mCount );
//                    if((savedCount == mCount &&  (isRising || isLower)) || (savedCount != mCount && isLower)) {
//                        DebugLogger.d(TAG, "TextColor isTextColorLock true!!!!!!!!!!!");
//                        isTextColorLock = true;
//                    }else {
//                        DebugLogger.d(TAG, "TextColor isTextColorLock false!!!!!!!!!!!");
//                        isTextColorLock = false;
//                        DebugLogger.d(TAG, "TextColor 흰색");
//                        countTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//                    }

                    prevZ = z;

                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    if(true == SmartWeightUtility.currentActivityName(mContext).equals(WarningActivity.WARNING_ACTIVITY_PACKAGENAE)) {
                        mGuageNiddle.setRotation(45);
                        mGuageBar.setClippingAngle(135);
                        mAngleBar.setStartAngle(0);
                        mAngleBar.setClippingAngle(0);
                        return;
                    }

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


//                    mRootContentLayout.setBackgroundColor(ContextCompat.getColor(mContext,  R.color.black));
//                    if(angle ==0) {
//                        mRootContentLayout.setBackgroundColor(ContextCompat.getColor(mContext,  R.color.black));
//                    }else {
//                        mRootContentLayout.setBackgroundColor(ContextCompat.getColor(mContext,  R.color.red_30));
//                    }

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
//                    isCountRecevied = true;
                    isNotWorkout = true;
//                    if(null != timer) {
//                        timer.cancel();
//                        timer = null;
//                        maskImageView.setVisibility(View.GONE);
//                    }

//                    if(true == SmartWeightUtility.currentActivityName(mContext).equals(WarningActivity.WARNING_ACTIVITY_PACKAGENAE)) {
//                        return;
//                    }

                    stopNotWorkAnimation();

                    if(mGaugeBg.getVisibility() != View.VISIBLE) {
                        mGaugeBg.setVisibility(View.VISIBLE);
                    }

                    countTextView.clearAnimation();

                    int count = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_COUNT, 0);
                    int accuracy = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_ACCURACY, 0);

                    DebugLogger.d(TAG, "mCountBroadcastReceiver count = " + count);
                    DebugLogger.d(TAG, "mMotionBroadcastReceiver accuracy = " + accuracy);

                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    countTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 120);
                    countTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    savedCount = mCount;
                    mCount = count;

//                    if(count > data.getReps()) {
//                        return;
//                    }

                    int tmp = count%10;

//                    int resId = getResources().getIdentifier("count_" + tmp, "raw", mContext. getPackageName());
//                    soundID = soundPool.load(mContext, resId, 1);

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

//                    countTextView.setTextColor(Color.WHITE);

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

    boolean isNotWorkout = false;
    protected BroadcastReceiver mNotWorkoutBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mGaugeBg.setVisibility(View.GONE);

                    initAngleUI();
                    isNotWorkout = true;
                    maskState = 0;

                    if(mCount == 0) {
                        countTextView.startAnimation(startAnimation);
                    }

                    dimLayout.setVisibility(View.VISIBLE);
                    maskLoadingImageView.setVisibility(View.VISIBLE);
                    maskLoadingGaugeImageView.setVisibility(View.VISIBLE);

                    maskLoadingImageView.startAnimation(fadeInAnimation);
                    fadeInAnimation.setAnimationListener(mFadeInAnimationListener);
                    fadeOutAnimation.setAnimationListener(mFadeOutAnimationListener);

                    showAngleLayout();

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

    private void initAngleUI() {
        mGuageNiddle.setRotation(45);
        mGuageBar.setClippingAngle(135);
        mAngleBar.setStartAngle(0);
        mAngleBar.setClippingAngle(0);
        mAngleNiddle.setRotation(0);
        angleTextView.setText(getString(R.string.text_angle, 0));
    }

    private void stopNotWorkAnimation() {
        fadeInAnimation.cancel();
        fadeOutAnimation.cancel();
        fadeInAnimation.setAnimationListener(null);
        fadeOutAnimation.setAnimationListener(null);

        maskLoadingImageView.clearAnimation();
        maskLoadingImageView.setVisibility(View.GONE);

        maskLoadingGaugeImageView.clearAnimation();
        maskLoadingGaugeImageView.setVisibility(View.GONE);
    }

    private void showNotCorrectWorkout() {
        mNotCorrectWorkoutLayout.setVisibility(View.VISIBLE);
        mNotCorrectWorkoutTextView.setText(Html.fromHtml(getString(R.string.text_not_correct_workout)));

        removeAngleLayout();
    }

    private void showAngleLayout() {
        mAngleLayout.setVisibility(View.VISIBLE);
        mAngleNiddle.setVisibility(View.VISIBLE);
        mAngleBar.setVisibility(View.VISIBLE);
        removeNotCorrectLayout();
    }

    private void removeAngleLayout() {
        mAngleLayout.setVisibility(View.GONE);
        mAngleNiddle.setVisibility(View.GONE);
        mAngleBar.setVisibility(View.GONE);
    }

    private void removeNotCorrectLayout() {
        mNotCorrectWorkoutLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(0x99 == requestCode) {
//            int count = data.getIntExtra("count", 0);
//            int accuracy = data.getIntExtra("accuracy", 0);
//
//            if(count > 0 && count > mCount) {
//                mCount = count;
//                countTextView.setTextColor(Color.WHITE);
//                countTextView.setText(String.valueOf(count));
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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