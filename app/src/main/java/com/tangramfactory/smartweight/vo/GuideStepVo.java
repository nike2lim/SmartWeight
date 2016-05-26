package com.tangramfactory.smartweight.vo;

/**
 * Created by Shlim on 2016-05-24.
 */
public class GuideStepVo {

    boolean mIsCurrentStep;
    String mStepCount;
    String mBageCount;
    String mRepsCount;

    int excerciseCount;
    String[] exerciseList;

    public GuideStepVo(String stepCount, String bageCount, String repsCount, boolean isCurrentStep) {
        mStepCount = stepCount;
        mBageCount = bageCount;
        mRepsCount = repsCount;
        mIsCurrentStep = isCurrentStep;
    }

    public String getStepCount() {
        return mStepCount;
    }

    public String getBageCount() {
        return mBageCount;
    }

    public String getRepsCount() {
        return mRepsCount;
    }

    public boolean isCurrentStep() {
        return mIsCurrentStep;
    }



}
