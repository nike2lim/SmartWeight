package com.tangramfactory.smartweight.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tangramfactory.smartweight.SmartWeightApplication;

/**
 * Created by Shlim on 2016-05-23.
 */
public class BaseAppCompatActivity extends AppCompatActivity {
    public static Context mContext;
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "_BaseAppCompatActivity";
    protected SmartWeightApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }
}
