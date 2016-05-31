package com.tangramfactory.smartweight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;

public class WorkoutPreviewActivity extends BaseAppCompatActivity implements View.OnTouchListener {
    public Toolbar toolbar;

    protected int[] viewFlipperList = {
            R.string.text_flipper_bench_press,
            R.string.text_flipper_cable_pushdown,
            R.string.text_flipper_plank,
            R.string.text_flipper_kick_back,
            R.string.text_flipper_fly,
            R.string.text_flipper_curl,
            R.string.text_flipper_lunge,
            R.string.text_flipper_row,
            R.string.text_flipper_sit_up,
            R.string.text_flipper_squat,
            R.string.text_flipper_lateral_raises,
            R.string.text_flipper_deadlift,
            R.string.text_flipper_back_extension,
            R.string.text_flipper_lat_pulldown,
            R.string.text_flipper_shoulder_press };

    protected ImageButton PrevButton;
    protected ImageButton NextButton;
    protected ViewFlipper viewFlipper;
    protected int beforePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_preview);

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
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        viewFlipper.setOnTouchListener(this);
    }

    private void showNext() {
        int temp = 0;
        temp = beforePosition + 1 > 14 ? 14 : beforePosition + 1;
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
        if (position == 14) {
            NextButton.setEnabled(false);
        } else {
            NextButton.setEnabled(true);
        }
        ((TextView) viewFlipper.getCurrentView()).setText(viewFlipperList[position]);
        int size = getFilpperTextSize(viewFlipperList[position]);

        for(int i=0; i < viewFlipper.getChildCount(); i++) {
            ((TextView)viewFlipper.getChildAt(i)).setTextSize(size);
        }
//        ((TextView) viewFlipper.getCurrentView()).setTextSize(size);
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

    private int getFilpperTextSize(int resId) {

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
                startActivity(new Intent(this, VideoPlayerActivity.class));
                break;

            case R.id.startButton:
                startActivity(new Intent(this, WorkoutReadyActivity.class));
                finish();
                break;
        }
    }
}
