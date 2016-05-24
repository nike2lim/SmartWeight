package com.tangramfactory.smartweight.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tangramfactory.smartweight.utility.DebugLogger;


public class ConnReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//			Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
//			Toast.makeText(context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();

			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if ((null != mobile && mobile.isConnected()) || wifi.isConnected()) {
//				Log.d(TAG, "Network connect success");
				DebugLogger.d("SmartGym", "Network connect success");
			} else {
//				Log.d(TAG, "Network connect fail");
				DebugLogger.d("SmartGym", "Network connect fail");
			}
		}
	}
}
