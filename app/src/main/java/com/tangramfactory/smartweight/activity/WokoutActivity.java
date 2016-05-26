package com.tangramfactory.smartweight.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;

public class WokoutActivity extends BaseAppCompatActivity {
    public Toolbar toolbar;

    ImageView mAngleNiddle;
    ImageView mAngleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokout);

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
        mAngleNiddle = (ImageView)findViewById(R.id.angle_niddle);
        mAngleBar = (ImageView)findViewById(R.id.angle_bar);

        Rect r = new Rect();
        r.set(0,0,30,30);
        mAngleBar.setClipBounds(r);


//        mAngleNiddle.setClipBounds(new R);
        mAngleNiddle.setRotation(40);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                break;
        }
    }
}
