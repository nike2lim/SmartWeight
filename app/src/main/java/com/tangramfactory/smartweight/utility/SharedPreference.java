package com.tangramfactory.smartweight.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
	private static String PREF_NAME = "";
	private Context mContext;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
	
	final static String TAG = "SharedPreference";
	private volatile static SharedPreference instance;

	public static SharedPreference getInstance(Context context) {
		if (instance == null) {
			synchronized (SharedPreference.class) {
				if (instance == null) {
					instance = new SharedPreference(context);
				}
			}
		}
		return instance;
	}
	
	public void deleteAll(){
		editor.clear().commit();
		instance = null;
	}
	
	public SharedPreference(Context context) {
		PREF_NAME = context.getPackageName()+".pref";
		mContext = context;
		pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		editor = pref.edit();
	}

	public void put(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void put(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void put(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void put(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void put(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public String getValue(String key, String dftValue) {
		try {
			return pref.getString(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public int getValue(String key, int dftValue) {
		try {
			return pref.getInt(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public boolean getValue(String key, boolean dftValue) {
		try {
			return pref.getBoolean(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}
	}

	public long getValue(String key, long dftValue) {
		try {
			return pref.getLong(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}
	}
	
	public float getValue(String key, float dftValue) {
		try {
			return pref.getFloat(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}
	}
}
