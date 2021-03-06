package com.tangramfactory.smartweight.activity.workoutready;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.device.ScanActivity;
import com.tangramfactory.smartweight.activity.workout.WorkoutActivity;
import com.tangramfactory.smartweight.activity.workout.WorkoutAnaliticActivity;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import java.util.ArrayList;

public class WorkoutReadyActivity extends BaseAppCompatActivity {

    private final static int MSG_ID_TIME_COUNT = 0x01;
    private final static int READY_TIME = 5;

    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;

    TextView timeCountTextView;
    TextView secUnitTextView;
    ImageButton NextButton;
    TextView exerciseNumTextView;

    private int time_count = READY_TIME;

    private int stepNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();

    private SoundPool soundPool;
    private int soundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_ready);

        stepNum = getIntent().getIntExtra("stepNum", 0);

        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.title_step, stepNum));
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

    protected void loadCodeView() {
        deviceBatteryStateImage = (ImageButton) findViewById(R.id.deviceBatteryState);
        timeCountTextView = (TextView)findViewById(R.id.second);
        secUnitTextView = (TextView)findViewById(R.id.secUnit);
        NextButton = (ImageButton)findViewById(R.id.next_btn);


//        timeCountTextView.setText(String.valueOf(time_count));
        Message msg = Message.obtain();
        msg.what = MSG_ID_TIME_COUNT;
        mTimeCheck.sendMessageDelayed(msg, 1500);

        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);
        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        exerciseNumTextView = (TextView)findViewById(R.id.exerciseNum);
        exerciseNumTextView.setText(getString(R.string.text_per_exercise, exerciseNum+1, mWorkoutList.size()));

        setSoundPool();
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

        if(soundID != 0) {
            soundPool.stop(soundID);
        }

        int resId = getResources().getIdentifier("ready", "raw", mContext. getPackageName());
        soundID = soundPool.load(this, resId, 1);
    }

    Handler mTimeCheck = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_TIME_COUNT:
                    if(time_count > 0) {
                        int resId = getResources().getIdentifier("count_" + time_count, "raw", mContext. getPackageName());
                        soundID = soundPool.load(mContext, resId, 1);

                        timeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 93);
                        if(secUnitTextView.getVisibility() != View.VISIBLE) {
                            secUnitTextView.setVisibility(View.VISIBLE);
                        }
                        timeCountTextView.setText(String.valueOf(time_count));
                        Message sendMsg = Message.obtain();
                        sendMsg.what = MSG_ID_TIME_COUNT;
                        mTimeCheck.sendMessageDelayed(sendMsg, 1000);
                        time_count--;
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
            case R.id.exercise_list_btn :
                finish();
                break;

            case R.id.next_btn:
                mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
                startWorkoutActivity();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != soundPool) {
            soundPool.release();
            soundPool = null;
        }

        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
        mTimeCheck = null;
    }

    private void startWorkoutActivity() {
        Intent intent = new Intent(mContext, WorkoutActivity.class);
//        Intent intent = new Intent(mContext, WorkoutAnaliticActivity.class);
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
        mApplication.mGuideResultVo.clear();
        super.onBackPressed();
    }
}
