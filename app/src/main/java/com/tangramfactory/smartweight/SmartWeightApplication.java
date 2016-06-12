package com.tangramfactory.smartweight;

import com.tangramfactory.smartweight.vo.GuideResultVo;

import java.util.ArrayList;

/**
 * Created by Shlim on 2016-05-23.
 */
public class SmartWeightApplication extends BaseBLEApplication{

    public static final String BASE_TAG = "SmartWeight";
    private static final String LOG_TAG = BASE_TAG + "Application";

    public static int batteryState = 0;

    public ArrayList<GuideResultVo> mGuideResultVo = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
