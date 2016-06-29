package com.tangramfactory.smartweight.activity.workout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.ble.service.UARTService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;

public class WarningActivity extends BaseAppCompatActivity {
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "WarningActivity";
    public static final String WARNING_ACTIVITY_PACKAGENAE = "com.tangramfactory.smartweight.activity.workout.WarningActivity";

    public final static String WARNING_TYPE = "WarningActivity_WARNING_TYPE";

    public final static int WARNING_TYPE_WRONG_EXERCISE     = 0x01;
    public final static int WARNING_TYPE_NOT_WORKING        = 0x02;

    TextView msgTextView;

    String exerciseName;
    int warningType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        exerciseName = getIntent().getStringExtra("exerciseName");
        warningType = getIntent().getIntExtra(WARNING_TYPE, WARNING_TYPE_WRONG_EXERCISE);

        loadCodeView();
        registerReceiver(mExerciseBroadcastReceiver, makeExerciseDataIntentFilter());
        registerReceiver(mCountBroadcastReceiver, makeCountDataIntentFilter());
        registerReceiver(mNotWorkoutBroadcastReceiver, makeNotWorkoutIntentFilter());
    }

    protected void loadCodeView() {
        msgTextView = (TextView)findViewById(R.id.waraning_message);

        if(warningType == WARNING_TYPE_NOT_WORKING) {
            Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            msgTextView.setText("Detect Exercise!");
            msgTextView.startAnimation(startAnimation);
        }else {
//            msgTextView.setText("Wrong Exercise : \n" + exerciseName);
            msgTextView.setText("잘못된 운동입니다. \n 컬운동을 해주세요.");
        }
    }

    private IntentFilter makeCountDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_COUNT_DATA);
        return intentFilter;
    }

    protected BroadcastReceiver mCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgTextView.clearAnimation();

                    int count = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_COUNT, 0);
                    int accuracy = intent.getIntExtra(UARTService.ACTION_RECEIVE_COUNT_DATA_ACCURACY, 0);

                    DebugLogger.d(TAG, "WarningActivity mCountBroadcastReceiver count = " + count);
                    DebugLogger.d(TAG, "WarningActivity mMotionBroadcastReceiver accuracy = " + accuracy);

                    finishActivity(count, accuracy);
                }
            });
        }
    };


    private IntentFilter makeExerciseDataIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_EXERCISE_DATA);
        return intentFilter;
    }

    protected BroadcastReceiver mExerciseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgTextView.clearAnimation();

                    String exerciseName = intent.getStringExtra(UARTService.ACTION_RECEIVE_EXERCISE_DATA_CODE);
//                    WorkoutVo data = mWorkoutList.get(exerciseNum);

                    DebugLogger.d(TAG, "WarningActivity mExerciseBroadcastReceiver receive exerciseName = " + exerciseName);


                    if(exerciseName.equals(getString(R.string.text_curl))){
                        finishActivity(-1, -1);
                        return;
                    }

//                    msgTextView.setText("Wrong Exercise : \n"+ exerciseName);
                    msgTextView.setText("잘못된 운동입니다. \n 컬운동을 해주세요.");

//                    if(false == data.getExerciseName().equals(exerciseName)) {
////                        countTextView.setTextColor(ContextCompat.getColor(mContext,  R.color.red_80));
//                        if(false == SmartWeightUtility.currentActivityName(mContext).equals(WarningActivity.WARNING_ACTIVITY_PACKAGENAE)) {
//                            Intent startIntent = new Intent(mContext, WarningActivity.class);
//                            startIntent.putExtra("exerciseName", exerciseName);
//                            startIntent.putExtra(WarningActivity.WARNING_TYPE, WarningActivity.WARNING_TYPE_WRONG_EXERCISE);
//                            startActivityForResult(startIntent, 0x99);
//                        }
//                    }else if(mCount == 0) {
//                        countTextView.setTextColor(Color.parseColor("#1E1E1E"));
//                    }else {
//                        countTextView.setTextColor(ContextCompat.getColor(mContext,  R.color.white));
//                    }
                }
            });
        }
    };


    private IntentFilter makeNotWorkoutIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UARTService.ACTION_RECEIVE_NOT_WORKOUT);
        return intentFilter;
    }


    protected BroadcastReceiver mNotWorkoutBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgTextView.clearAnimation();

                    Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
                    msgTextView.setText("Detect Exercise!");
                    msgTextView.startAnimation(startAnimation);
                }
            });
        }
    };


    private void finishActivity(int count, int accuracy){
        DebugLogger.d(TAG, "finishActivity count = " + count + "accuracy = " + accuracy);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("count", count);
        resultIntent.putExtra("accuracy", accuracy);
        if(null != mCountBroadcastReceiver) {
            unregisterReceiver(mCountBroadcastReceiver);
            mCountBroadcastReceiver = null;
        }

        if(null != mExerciseBroadcastReceiver) {
            unregisterReceiver(mExerciseBroadcastReceiver);
            mExerciseBroadcastReceiver = null;
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @Override
    protected void onDestroy() {
        if(null != mCountBroadcastReceiver) {
            unregisterReceiver(mCountBroadcastReceiver);
        }

        if(null != mExerciseBroadcastReceiver) {
            unregisterReceiver(mExerciseBroadcastReceiver);
        }

        if(null != mNotWorkoutBroadcastReceiver) {
            unregisterReceiver(mNotWorkoutBroadcastReceiver);
        }

        super.onDestroy();
    }
}
