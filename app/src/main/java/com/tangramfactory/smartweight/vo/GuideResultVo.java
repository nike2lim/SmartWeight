package com.tangramfactory.smartweight.vo;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by shlim on 2016-05-28.
 */
public class GuideResultVo {
    int progress;
    String exerciseName;

    DateTime startTime;
    String restTime;
    String totalTime;

    ArrayList<SetInfo> setInfoList;
    public GuideResultVo(int progress, String exerciseName, DateTime time) {
        this.progress = progress;
        this.exerciseName = exerciseName;
        this.startTime = time;
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

    public void setInfo(int index, SetInfo set) {
        setInfoList.set(index, set);
    }


    public int getProgress() {
        return progress;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setStartTime(DateTime time) {
        startTime = time;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    public String getRestTime() {
        return this.restTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalTime() {
        return this.totalTime;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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
        long setRestTime;


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

        public long getSetRestTime() { return setRestTime; }

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

        public void setRestTime(long restTime) {
            this.setRestTime = restTime;
        }

    }


}
