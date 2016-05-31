package com.tangramfactory.smartweight.vo;

import java.util.ArrayList;

/**
 * Created by shlim on 2016-05-28.
 */
public class GuideResultVo {
    int progress;
    String exerciseType;
    ArrayList<SetInfo> setInfoList;
    public GuideResultVo(int progress, String type) {
        this.progress = progress;
        this.exerciseType = type;
        setInfoList = new ArrayList<>();
    }

    public void addSetInfo( String setNum, String weight,
                            String weightUnit, String totalReps, String resultReps, String accuracy) {
        SetInfo info = new SetInfo();
        info.setNum(setNum);
        info.setWeight(weight);
        info.setWeightUnit(weightUnit);
        info.setTotalReps(totalReps);
        info.setResultReps(resultReps);
        info.setAccuracy(accuracy);
        setInfoList.add(info);
    }


    public int getProgress() {
        return progress;
    }

    public String getExerciseType() {
        return exerciseType;
    }


    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }


    public ArrayList<SetInfo> getSetInfoList() {
        return setInfoList;
    }

    public class SetInfo {
        String setNum;
        String weight;
        String weightUnit;
        String totalReps;
        String resultReps;
        String accuracy;


        public String getSetNum() {
            return setNum;
        }

        public String getWeight() {
            return weight;
        }

        public String getWeightUnit() {
            return weightUnit;
        }

        public String getTotalReps() {
            return totalReps;
        }

        public String getResultReps() {
            return resultReps;
        }

        public String getAccuracy() {
            return accuracy;
        }

        public void setNum(String setNum) {
            this.setNum = setNum;
        }
        public void setWeight(String weight) {
            this.weight = weight;
        }

        public void setWeightUnit(String weightUnit) {
            this.weightUnit = weightUnit;
        }

        public void setTotalReps(String totalReps) {
            this.totalReps = totalReps;
        }

        public void setResultReps(String resultReps) {
            this.resultReps = resultReps;
        }

        public void setAccuracy(String accuracy) {
            this.accuracy = accuracy;
        }

    }


}
