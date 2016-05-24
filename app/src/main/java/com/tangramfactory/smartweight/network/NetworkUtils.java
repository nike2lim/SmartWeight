package com.tangramfactory.smartweight.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tangramfactory.smartweight.utility.DebugLogger;


public class NetworkUtils {
	static public boolean validateNetwork(Context mContext){
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		try {
			if ((null != mobile && mobile.isConnected()) || wifi.isConnected()) {
				DebugLogger.d("SmartGym", "Network connect success");
				return true;
			} else {
				DebugLogger.d("SmartGym", "Network connect fail");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
