package com.tangramfactory.smartweight.vo;

import java.io.Serializable;

/**
 * Created by B on 2016-06-03.
 */
public class WorkoutVo implements Serializable {

    int totalSetNum;
    int currentSetNum;
    String exerciseNames;
    String equipment;
    int reps;
    String weight;
    String weightUnit;
    int breakTime;
    String videoUrl;

    public WorkoutVo(String exerciseName, String  equipment, int reps, String weight, String weightUnit, int breakTime, String videoUrl, int totalSetNum) {

        this.exerciseNames = exerciseName;
        this.equipment = equipment;
        this.reps = reps;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.breakTime = breakTime;
        this.videoUrl = videoUrl;
        this.totalSetNum = totalSetNum;
    }

    public void setTotalSetNum(int setNum) {
        this.totalSetNum = setNum;
    }

    public int getTotalSetNum() {
        return totalSetNum;
    }

    public void setCurrentSetNum(int setNum) {
        this.currentSetNum = setNum;
    }

    public int getCurrentSetNum() {
        return currentSetNum;
    }

    public String getExerciseName() {
        return exerciseNames;
    }

    public String getEquipment() {
        return equipment;
    }

    public int getReps() {
        return reps;
    }

    public String getWeight() {
        return weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }



}
