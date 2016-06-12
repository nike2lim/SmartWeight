package com.tangramfactory.smartweight.activity.workoutpreview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.device.ScanActivity;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import java.util.ArrayList;

public class WorkoutPreviewActivity extends BaseAppCompatActivity implements View.OnTouchListener {
    public Toolbar toolbar;
    ImageButton deviceBatteryStateImage;

    protected int[][] viewFlipperList = {
            {R.string.text_flipper_bench_press, 0},
            {R.string.text_flipper_cable_pushdown, 0},
            {R.string.text_flipper_plank, 0},
            {R.string.text_flipper_kick_back, 0},
            {R.string.text_flipper_fly, 0},
            {R.string.text_flipper_curl, 0},
            {R.string.text_flipper_lunge, 0},
            {R.string.text_flipper_row, 0},
            {R.string.text_flipper_sit_up, 0},
            {R.string.text_flipper_squat, 0},
            {R.string.text_flipper_lateral_raises, 0},
            {R.string.text_flipper_deadlift, 0},
            {R.string.text_flipper_back_extension,0},
            {R.string.text_flipper_lat_pulldown, 0},
            {R.string.text_flipper_shoulder_press, 0} };

    protected String[] realViewFlipperList;

    protected ImageButton PrevButton;
    protected ImageButton NextButton;
    protected ViewFlipper viewFlipper;
    protected TextView exerciseNumTextView;
    protected TextView equipmentTextView;
    protected TextView repsTextView;
    protected TextView breakTimeTextView;
    protected TextView weightTextView;
    protected TextView weightUnitTextView;

    protected int beforePosition = 0;
    private int stepNum = 0;
    private int exerciseNum;
    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_preview);

        stepNum = getIntent().getIntExtra("stepNum", 0);
        exerciseNum = getIntent().getIntExtra("exerciseNum", 0);

        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_step, stepNum));
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
        PrevButton = (ImageButton) findViewById(R.id.PrevButton);
        NextButton = (ImageButton) findViewById(R.id.NextButton);
        PrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrevious();
            }
        });
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNext();
            }
        });

        mWorkoutList = (ArrayList)getIntent().getSerializableExtra("exerciseList");

        viewFlipper = (ViewFlipper)findViewById(R.id.viewflipper);
        exerciseNumTextView = (TextView)findViewById(R.id.exerciseNum);
        exerciseNumTextView.setText(getString(R.string.text_per_exercise, exerciseNum+1, mWorkoutList.size()));
        equipmentTextView = (TextView)findViewById(R.id.equipment);
        repsTextView = (TextView)findViewById(R.id.reps);
        breakTimeTextView = (TextView)findViewById(R.id.breaktime);
        weightTextView = (TextView)findViewById(R.id.weight);
        weightUnitTextView = (TextView)findViewById(R.id.weightunit);

        WorkoutVo data = mWorkoutList.get(exerciseNum);
        equipmentTextView.setText(data.getEquipment());
        repsTextView.setText(String.valueOf(data.getReps()));
        breakTimeTextView.setText(String.valueOf(data.getBreakTime()));
        weightTextView.setText(data.getWeight());
        weightUnitTextView.setText(data.getWeightUnit());

        viewFlipper.setOnTouchListener(this);
        realViewFlipperList = new String[mWorkoutList.size()];

        setFlipperArray();

//        setDimFlipperText();
        setflipperText(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceBatteryStateImage.setImageResource(SmartWeightUtility.getBatteryIconState(mApplication.isConnected(), SmartWeightApplication.batteryState));
    }

    private void showNext() {
        int temp = 0;
        temp = beforePosition + 1 > (mWorkoutList.size() -1) ? (mWorkoutList.size() -1) : beforePosition + 1;
//		temp = beforePosition == 0 ? 1 : 1;
        beforePosition = temp;
        showNextMaster();
        setflipperText(temp);
    }

    private void showPrevious() {
        int temp = 0;
        temp = beforePosition - 1 < 0 ? 0 : beforePosition - 1;
        beforePosition = temp;
        showPreviousMaster();
        setflipperText(temp);

    }

    protected void showNextMaster() {
        viewFlipper.setInAnimation(mContext, R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(mContext, R.anim.slide_out_to_left);
        viewFlipper.showPrevious();
    }

    protected void showPreviousMaster() {
        viewFlipper.setInAnimation(mContext, R.anim.slide_in_from_left);
        viewFlipper.setOutAnimation(mContext, R.anim.slide_out_to_right);
        viewFlipper.showNext();
    }

    protected void setflipperText(int position) {
        if (position == 0) {
            PrevButton.setEnabled(false);
        } else {
            PrevButton.setEnabled(true);
        }
        if (position == (mWorkoutList.size() -1)) {
            NextButton.setEnabled(false);
        } else {
            NextButton.setEnabled(true);
        }

        exerciseNumTextView.setText(getString(R.string.text_per_exercise, position+1, mWorkoutList.size()));

        ((TextView) viewFlipper.getCurrentView()).setText(realViewFlipperList[position]);

        int size = getFlipperTextSize(realViewFlipperList[position]);
//        ((TextView) viewFlipper.getCurrentView()).setText(viewFlipperList[position][0]);
//
//        if(viewFlipperList[position][1] == 1) {
//            ((TextView) viewFlipper.getCurrentView()).setTextColor(Color.WHITE);
//        }else {
//            ((TextView) viewFlipper.getCurrentView()).setTextColor(Color.parseColor("#AAAAAA"));
//        }

//        int size = getFlipperTextSize(viewFlipperList[position][0]);

        for(int i=0; i < viewFlipper.getChildCount(); i++) {
            ((TextView)viewFlipper.getChildAt(i)).setTextSize(size);
        }
    }

    private float lastX;
    private float lastY;

    @Override
    public boolean onTouch(View v, MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                lastY = touchevent.getY();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();
                float currentY = touchevent.getY();
                if (Math.abs(lastX - currentX) > 200 && Math.abs(lastY - currentY) < 100) {
                    if (lastX < currentX) {
                        if (PrevButton.isEnabled())
                            showPrevious();
                        else
                            return false;
                    }

                    if (lastX > currentX) {
                        if (NextButton.isEnabled())
                            showNext();
                        else
                            return false;
                    }

                    return true;
                }
                break;
        }
        return false;
    }


    private int getFlipperTextSize(String exerciseName) {
        int [] excercise_font_size_50 = {R.string.text_flipper_lateral_raises, R.string.text_flipper_deadlift};
        int [] excercise_font_size_43 = {R.string.text_flipper_back_extension, R.string.text_flipper_cable_pushdown, R.string.text_flipper_lat_pulldown, R.string.text_flipper_shoulder_press};

        int size = 60;

        String exerName = exerciseName.replace("\n", "").toLowerCase();
        exerName = exerName.replace(" ", "");

        for(int i=0; i < excercise_font_size_50.length; i++) {
            String exercise_font_50_name =  getString(excercise_font_size_50[i]);
            exercise_font_50_name = exercise_font_50_name.toLowerCase().trim();
            exercise_font_50_name = exercise_font_50_name.replace("\n", "");
            if(exerName.equals(exercise_font_50_name)) {
                size = 50;
                break;
            }
        }

        for(int i=0; i < excercise_font_size_43.length; i++) {
            String exercise_font_43_name =  getString(excercise_font_size_43[i]);
            exercise_font_43_name = exercise_font_43_name.toLowerCase().trim();
            exercise_font_43_name = exercise_font_43_name.replace("\n", "");
            if(exerName.equals(exercise_font_43_name)) {
                size = 43;
                break;
            }
        }
        return size;
    }

    private int getFlipperTextSize(int resId) {

        //60 : else
        //50 : lateral raises, deadlift
        //43 : back extension, cable pushdown, lat pulldown, shoulder press

        int [] excerise_font_size_50 = {R.string.text_flipper_lateral_raises, R.string.text_flipper_deadlift};
        int [] excerise_font_size_43 = {R.string.text_flipper_back_extension, R.string.text_flipper_cable_pushdown, R.string.text_flipper_lat_pulldown, R.string.text_flipper_shoulder_press};

        int size = 60;

        for(int i=0; i < excerise_font_size_50.length; i++) {
            if(resId == excerise_font_size_50[i]) {
                size = 50;
                break;
            }
        }

        for(int i=0; i < excerise_font_size_43.length; i++) {
            if(resId == excerise_font_size_43[i]) {
                size = 43;
                break;
            }
        }
        return size;
    }

    public void onViewClick(View view) {
        switch(view.getId()) {
            case R.id.videoPlayButton:
//                startActivity(new Intent(this, VideoPlayerActivity.class));
                break;

            case R.id.startButton:
                if(mApplication.isConnected() == false) {
                    Intent intent = new Intent(mContext, ScanActivity.class);
                    intent.putExtra("stepNum", stepNum);
                    intent.putExtra("exerciseNum", exerciseNum);
                    intent.putExtra("exerciseList", mWorkoutList);
                    startActivity(intent);
                    finish();
                }else {
//                    Intent intent = new Intent(this, WorkoutReadyActivity.class);
                    Intent intent = new Intent(mContext, ScanActivity.class);
                    intent.putExtra("isConnected", mApplication.isConnected());
                    intent.putExtra("stepNum", stepNum);
                    intent.putExtra("exerciseNum", exerciseNum);
                    intent.putExtra("exerciseList", mWorkoutList);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void setFlipperArray() {
        for(int i=0; i < mWorkoutList.size(); i++) {
            WorkoutVo workoutData = mWorkoutList.get(i);
            String exerciseName = workoutData.getExerciseName().toLowerCase().trim();
            exerciseName = exerciseName.replace(" ", "");
            for(int j=0; j < viewFlipperList.length; j ++) {
                int resId = viewFlipperList[j][0];
                String exerList = getString(resId);
                exerList = exerList.toLowerCase();
                exerList = exerList.trim();
                exerList = exerList.replace("\n", "");
                if(exerciseName.equals(exerList)) {
//                    viewFlipperList[j][1] = 1;
                    realViewFlipperList[i] = getString(viewFlipperList[j][0]);
                    break;
                }
            }

        }
    }

    private void setDimFlipperText() {
        for(int i=0; i < mWorkoutList.size(); i++) {
            WorkoutVo workout = mWorkoutList.get(i);
            String exerciseName = workout.getExerciseName();
            exerciseName = exerciseName.toLowerCase();
            exerciseName  = exerciseName.trim();

            for(int j=0; j < viewFlipperList.length; j ++) {
                int resId = viewFlipperList[j][0];
                String exerList = getString(resId);
                exerList = exerList.toLowerCase();
                exerList = exerList.trim();
                exerList = exerList.replace("\n", "");
                if(exerciseName.equals(exerList)) {
                    viewFlipperList[j][1] = 1;
                }
            }
        }
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
}
