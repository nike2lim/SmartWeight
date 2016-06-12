package com.tangramfactory.smartweight.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shlim on 2016-05-24.
 */
public class GuideStepVo {

    boolean mIsCurrentStep;
    String mStepCount;
    String mBageCount;
    String mRepsCount;

    ArrayList<WorkoutVo> mWorkoutList = new ArrayList<WorkoutVo>();

    public GuideStepVo(String stepCount, String bageCount, String repsCount, boolean isCurrentStep, List<WorkoutVo> list) {
        mStepCount = stepCount;
        mBageCount = bageCount;
        mRepsCount = repsCount;
        mIsCurrentStep = isCurrentStep;

        mWorkoutList.addAll(list);
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

    public ArrayList<WorkoutVo> getWorkoutVo() {
        return mWorkoutList;
    }

}
