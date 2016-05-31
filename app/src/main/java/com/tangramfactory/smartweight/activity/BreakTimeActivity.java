package com.tangramfactory.smartweight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;

public class BreakTimeActivity extends BaseAppCompatActivity {
    private final static int MSG_ID_TIME_COUNT = 0x01;
    private final static int READY_TIME = 60;

    private int time_count = READY_TIME;

    public Toolbar toolbar;
    TextView timeCountTextView;
    ImageButton NextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_time);
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
        timeCountTextView = (TextView)findViewById(R.id.break_time_sec);
        NextButton = (ImageButton)findViewById(R.id.next_btn);

        timeCountTextView.setText(String.valueOf(time_count));
        Message msg = Message.obtain();
        msg.what = MSG_ID_TIME_COUNT;
        mTimeCheck.sendMessageDelayed(msg, 1000);
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
                        startActivity(new Intent(mContext, WokoutActivity.class));
                        finish();
                    }
                    break;
            }
            return false;
        }
    });

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.next_btn:
                mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
//                startActivity(new Intent(mContext, WokoutActivity.class));
                startActivity(new Intent(mContext, WorkoutResultActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCheck.removeMessages(MSG_ID_TIME_COUNT);
        mTimeCheck = null;
    }
}
