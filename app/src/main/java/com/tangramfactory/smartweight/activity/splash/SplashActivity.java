package com.tangramfactory.smartweight.activity.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.activity.main.MainActivity;
import com.tangramfactory.smartweight.utility.UUIDCryptoUtils;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
