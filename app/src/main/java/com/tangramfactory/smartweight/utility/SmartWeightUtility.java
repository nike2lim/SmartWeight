package com.tangramfactory.smartweight.utility;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.tangramfactory.smartweight.R;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.List;

public class SmartWeightUtility {
	static public int getBatteryIconState(boolean isConnected, int batteryValue) {
		int batteryIconRes;

		if(isConnected) {
			if (batteryValue <= 100 && batteryValue > 80) {
				batteryIconRes = R.drawable.ic_title_battery_100;
			} else if (batteryValue <= 80 && batteryValue > 50) {
				batteryIconRes = R.drawable.ic_title_battery_75;
			} else if (batteryValue <= 50 && batteryValue > 25) {
				batteryIconRes = R.drawable.ic_title_battery_50;
			} else if (batteryValue <= 25 && batteryValue > 0) {
				batteryIconRes = R.drawable.ic_title_battery_25;
			}else{
				batteryIconRes = R.drawable.ic_title_connect_no;
			}
		}else {
			batteryIconRes = R.drawable.ic_title_connect_no;
		}
		return batteryIconRes;
	}

	static public int[] getTimeHMS(long duration) {
		int[] result = new int[3];
		int hours = (int) (duration / (3600 * 1000));
		int remaining = (int) (duration % (3600 * 1000));
		int minutes = (int) (remaining / (60 * 1000));
		remaining = (int) (remaining % (60 * 1000));
		int seconds = (int) (remaining / 1000);
		remaining = (int) (remaining % (1000));
		result[0] = hours;
		result[1] = minutes;
		result[2] = seconds;
		return result;
	}
	
	public static char[] c = new char[] { 'K', 'M', 'B', 'T' };
	private static DecimalFormat commaFormat = new DecimalFormat("#,###");
	public static String coolFormat(double n, int iteration) {
		if(n < 1000){
			return commaFormat.format(n);
		}
		double d = ((long) n / 100) / 10.0;
		boolean isRound = (d * 10) % 10 == 0;
		String result = (d < 1000 ? ((d > 99.9 || isRound || (!isRound && d > 9.99) ? (int) d * 10 / 10 : d + "") + "" + c[iteration]) : coolFormat(
				d,
				iteration + 1));
		
		return result;
	}
	
	public static String coolFormatFloat(double n, int iteration) {
		if(n < 1000){
			return commaFormat.format(n);
		}
		double d = ((long) n / 100) / 10.0;
		boolean isRound = (d * 10) % 10 == 0;
		String result = (d < 1000 ? ((d > 99.9 || isRound || (!isRound && d > 9.99) ? (float) d * 10 / 10 : d + "") + "" + c[iteration]) : coolFormat(
				d,
				iteration + 1));
		
		return result;
	}
	
	public static int getWeekNumber(DateTime date){
		int day = date.getDayOfMonth();
		if(day >= 1 && day <= 7){
			return 0;
		} else if(day >= 8 && day <= 14){
			return 1;
		} else if(day >= 15 && day <= 21){
			return 2;
		} else if(day >= 22 && day <= 28){
			return 3;
		} else {
			return 4;
		}
	}
	
	public static int getRecommandGoalCount(int age) {
		if (age >= 6 && age <= 10) {
			return 1800;
		} else if (age >= 11 && age <= 17) {
			return 2400;
		} else if (age >= 18 && age <= 29) {
			return 3600;
		} else if (age >= 30 && age <= 39) {
			return 3300;
		} else if (age >= 40 && age <= 49) {
			return 2700;
		} else if (age >= 50) {
			return 2100;
		}
		return 1800;
	}

	public static float getLBStoKG(int kg) {
		float weight = (float)(kg * 2.20462262);
		return weight;
	}


	static byte[] exerciseCode = new byte[5];
	static byte[] arr1 = intToByteArray(1);
	static byte[] arr2 = intToByteArray(2);
	static byte[] arr3 = intToByteArray(3);
	static byte[] arr4 = intToByteArray(4);

	public static byte[] getExerciseCode(Context cxt, String exerciseName) {
		if(exerciseName.equals(cxt.getString(R.string.text_bench_press)) || exerciseName.equals(cxt.getString(R.string.text_fly)) ) {
			exerciseCode[0] = 'A';
			if(exerciseName.equals(cxt.getString(R.string.text_bench_press))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}
		}else if(exerciseName.equals(cxt.getString(R.string.text_shoulder_press)) || exerciseName.equals(cxt.getString(R.string.text_lateral_raises)) ) {
			exerciseCode[0] = 'B';
			if(exerciseName.equals(cxt.getString(R.string.text_lateral_raises))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}
		}else if(exerciseName.equals(cxt.getString(R.string.text_curl))) {
			exerciseCode[0] = 'C';
			System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
		}else if(exerciseName.equals(cxt.getString(R.string.text_cable_pushdown)) || exerciseName.equals(cxt.getString(R.string.text_kick_back)) ) {
			exerciseCode[0] = 'D';
			if(exerciseName.equals(cxt.getString(R.string.text_cable_pushdown))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}
		}else if(exerciseName.equals(cxt.getString(R.string.text_lat_pulldown)) || exerciseName.equals(cxt.getString(R.string.text_row))) {
			exerciseCode[0] = 'E';
			if(exerciseName.equals(cxt.getString(R.string.text_lat_pulldown))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}

		}else if(exerciseName.equals(cxt.getString(R.string.text_sit_up)))  {
			exerciseCode[0] = 'F';
			System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
		}else if(exerciseName.equals(cxt.getString(R.string.text_back_extension)) || exerciseName.equals(cxt.getString(R.string.text_deadlift))) {
			exerciseCode[0] = 'G';
			if(exerciseName.equals(cxt.getString(R.string.text_back_extension))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else if(exerciseName.equals(cxt.getString(R.string.text_deadlift))) {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}
		}else if(exerciseName.equals(cxt.getString(R.string.text_lunge)) || exerciseName.equals(cxt.getString(R.string.text_squat))) {
			exerciseCode[0] = 'H';
			if(exerciseName.equals(cxt.getString(R.string.text_lunge))) {
				System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
			}else {
				System.arraycopy(arr2, 0,  exerciseCode, 1, arr2.length);
			}
		}else if(exerciseName.equals(cxt.getString(R.string.text_plank))) {
			exerciseCode[0] = 'Z';
			System.arraycopy(arr1, 0,  exerciseCode, 1, arr1.length);
		}else {
			//none
		}
		return exerciseCode;
	}

	public static String getExerciseName(Context context, byte[] exerciseCode) {
		String exerciseName = "";
		if(null == exerciseCode)		return  null;

		if(exerciseCode[0] == 'A') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_bench_press);
			}else {
				exerciseName = context.getString(R.string.text_fly);
			}
		}else if(exerciseCode[0] == 'B') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_lateral_raises);
			}else {
				exerciseName = context.getString(R.string.text_shoulder_press);
			}
		}else if(exerciseCode[0] == 'C') {
			exerciseName = context.getString(R.string.text_curl);
		}else if(exerciseCode[0] == 'D') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_cable_pushdown);
			}else {
				exerciseName = context.getString(R.string.text_kick_back);
			}
		}else if(exerciseCode[0] == 'E') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_row);
			}else {
				exerciseName = context.getString(R.string.text_lat_pulldown);
			}
		}else if(exerciseCode[0] == 'F') {
			exerciseName = context.getString(R.string.text_sit_up);
		}else if(exerciseCode[0] == 'G') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_back_extension);
			}else {
				exerciseName = context.getString(R.string.text_deadlift);
			}
		}else if(exerciseCode[0] == 'H') {
			if(exerciseCode[1] == 1) {
				exerciseName = context.getString(R.string.text_lunge);
			}else {
				exerciseName = context.getString(R.string.text_squat);
			}
		}else if(exerciseCode[0] == 'Z') {
			exerciseName = context.getString(R.string.text_plank);
		}
		return exerciseName;
	}


	public static byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[3] = (byte)(value >> 24);
		byteArray[2] = (byte)(value >> 16);
		byteArray[1] = (byte)(value >> 8);
		byteArray[0] = (byte)(value);

		return byteArray;
	}

	public static String currentActivityName(Context context) {
		String activityName = null;
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(100);
			for(ActivityManager.RunningTaskInfo info : taskInfo) {
				if(null != info.topActivity & info.topActivity.getClassName().contains(context.getPackageName())) {
					activityName = info.topActivity.getClassName();
					break;
				}
			}
//			Log.d("topActivity", "CURRENT Activity ::" + activityName);
		} catch (Exception e) {
		}
		return activityName;
	}
}