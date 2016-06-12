package com.tangramfactory.smartweight.activity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.main.MainActivity;


public class SplashActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mApplication.isShutDown = false;

//        if (!UUIDCryptoUtils.hasUUIDFile()) {
//            UUIDCryptoUtils.makeDeviceUUID();
//        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 2000);
    }


}
