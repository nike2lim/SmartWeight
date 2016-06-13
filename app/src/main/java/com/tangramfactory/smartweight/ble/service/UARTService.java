/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tangramfactory.smartweight.ble.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SharedPreference;
import com.tangramfactory.smartweight.utility.SmartWeightUtility;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UARTService extends BleProfileService implements UARTManagerCallbacks {
	final static String TAG = SmartWeightApplication.BASE_TAG + "UARTService";
	public static final String UART_SERVICE_NAME = "com.tangram.application.smartweight.ble.service.UARTService";
	public static final String BROADCAST_UART_TX = "com.tangram.application.smartweight.uart.BROADCAST_UART_TX";
	public static final String BROADCAST_UART_RX = "com.tangram.application.smartweight.uart.BROADCAST_UART_RX";
	public static final String EXTRA_DATA = "com.tangram.application.smartweight.uart.EXTRA_DATA";

	/**
	 * A broadcast message with this action and the message in
	 * {@link Intent#EXTRA_TEXT} will be sent t the UART device.
	 */
	private final static String ACTION_SEND = "com.tangram.application.smartweight.uart.ACTION_SEND";
	/**
	 * A broadcast message with this action is triggered when a message is
	 * received from the UART device.
	 */
	public final static String ACTION_RECEIVE = "com.tangram.application.smartweight.uart.ACTION_RECEIVE";
	/**
	 * Action send when user press the DISCONNECT button on the notification.
	 */
	private final static String ACTION_DISCONNECT = "com.tangram.application.smartweight.uart.ACTION_DISCONNECT";

	public final static String ACTION_RECEIVE_CRI = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI";
	public final static String ACTION_RECEIVE_CRI_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI_COUNT";
	public final static String ACTION_RECEIVE_CRI_RPM = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI_RPM";
	public final static String ACTION_RECEIVE_CRI_CALORIE = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI_CALORIE";
	public final static String ACTION_RECEIVE_CRI_DURATION = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI_DURATION";
	public final static String ACTION_RECEIVE_CRI_DIRECTION = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_CRI_DIRECTION";

	public final static String ACTION_RECEIVE_SID = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_SID";
	public final static String ACTION_RECEIVE_VER = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_VER";
	public final static String ACTION_RECEIVE_LBG = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_LBG";
	public final static String ACTION_RECEIVE_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_DATA";
	public final static String ACTION_RECEIVE_DCLK = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_DCLK";
	public final static String ACTION_RECEIVE_ACT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_ACT";
	public final static String ACTION_RECEIVE_LED = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_LED";


	public final static String ACTION_RECEIVE_HCOUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HCOUNT";
	public final static String ACTION_RECEIVE_HCOUNT_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HCOUNT_COUNT";
	public final static String ACTION_RECEIVE_HISTORY = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY";
	public final static String ACTION_RECEIVE_HISTORY_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY_COUNT";
	public final static String ACTION_RECEIVE_HISTORY_DURATION = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY_DURATION";
	public final static String ACTION_RECEIVE_HISTORY_CAALORIE = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY_CAALORIE";
	public final static String ACTION_RECEIVE_HISTORY_STARTTIME = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY_STARTTIME";
	public final static String ACTION_RECEIVE_HISTORY_ENDTIME = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_HISTORY_ENDTIME";

	public final static String ACTION_SEND_AUTO_BASICCOUNT = "com.tangram.application.smartweight.uart.ACTION_SEND_AUTO_BASICCOUNT";
	public final static String ACTION_SEND_AUTO_BASICCOUNT_TMP  = "com.tangram.application.smartweight.uart.ACTION_SEND_AUTO_BASICCOUNT_TMP";

//	public final static String ACTION_SEND_TMP_COUNT= "com.tangram.application.smartweight.uart.ACTION_SEND_TMP_COUNT";

	public final static String ACTION_RECEIVE_INTERVAL_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA";
	public final static String ACTION_RECEIVE_INTERVAL_DATA_INDEX = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA_INDEX";
	public final static String ACTION_RECEIVE_INTERVAL_DATA_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA_COUNT";
	public final static String ACTION_RECEIVE_INTERVAL_DATA_DURATION = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA_DURATION";
	public final static String ACTION_RECEIVE_INTERVAL_DATA_CALORIE = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA_CALORIE";
	public final static String ACTION_RECEIVE_INTERVAL_DATA_RPM = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_DATA_RPM";

	public final static String ACTION_RECEIVE_COUNT_FINISH = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_FINISH";

	public final static String ACTION_RECEIVE_INTERVAL_REST_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_REST_DATA";
	public final static String ACTION_RECEIVE_INTERVAL_REST_DATA_INDEX = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_REST_DATA_INDEX";
	public final static String ACTION_RECEIVE_INTERVAL_REST_DATA_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_REST_DATA_COUNT";
	public final static String ACTION_RECEIVE_INTERVAL_REST_DATA_BREAKTIME = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_INTERVAL_RESTDATA_BREAKTIME";



	public final static String ACTION_RECEIVE_ANGLE_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_ANGLE_DATA";
	public final static String ACTION_RECEIVE_WEIGHT_DATA_XVALUE = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_WEIGHT_DATA_XVALUE ";
	public final static String ACTION_RECEIVE_WEIGHT_DATA_YVALUE  = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_WEIGHT_DATA_YVALUE ";
	public final static String ACTION_RECEIVE_WEIGHT_DATA_ZVALUE  = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_WEIGHT_DATA_ZVALUE ";
	public final static String ACTION_RECEIVE_WEIGHT_DATA_ANGLE  = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_WEIGHT_DATA_ANGLE ";

	public final static String ACTION_RECEIVE_COUNT_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_COUNT_DATA";
	public final static String ACTION_RECEIVE_COUNT_DATA_COUNT = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_COUNT_DATA_COUNT";
	public final static String ACTION_RECEIVE_COUNT_DATA_ACCURACY = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_COUNT_DATA_ACCURACY";

	public final static String ACTION_RECEIVE_BREAK_TIME = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_BREAK_TIME";
	public final static String ACTION_RECEIVE_BREAK_TIME_DATA = "com.tangram.application.smartweight.uart.ACTION_RECEIVE_BREAK_TIME_DATA";


	public final static String ACTION_RECEIVE_EXERCISE_DATA = "com.tangram.application.smartweight.uart.EXERCISE_DATA";
	public final static String ACTION_RECEIVE_EXERCISE_DATA_CODE= "com.tangram.application.smartweight.uart.EXERCISE_DATA_CODE";


	private final static int NOTIFICATION_ID = 349; // random
	private final static int MSG_ID_ECOCHECK = 0x01;
	// private final static int OPEN_ACTIVITY_REQ = 67; // random
	// private final static int DISCONNECT_REQ = 97; // random

	private UARTManager mManager;

	private final LocalBinder mBinder = new UARTBinder();

	private int ecoCount = 0;
	private TimerTask mEcoSendTimerTask;
	private Timer mEcoSendTimer;
	private boolean isEcoFlag = true;
	private boolean isHclearSended = false;

	private String msgTmpData = null;
	private int mHistoryCount = 0;

	public class UARTBinder extends LocalBinder implements UARTInterface {
		@Override
		public void send(final String text) {
			mManager.send(text);
		}

		@Override
		public void send(byte[] request) {
			mManager.send(request);
		}
	}

	@Override
	protected LocalBinder getBinder() {
		return mBinder;
	}

	@Override
	protected BleManager<UARTManagerCallbacks> initializeManager() {
		return mManager = new UARTManager(this);
	}

	// SoundPool pool;
	// int helloSound;
	// int disconnectedSound;
	@Override
	public void onCreate() {
		super.onCreate();
		DebugLogger.d(TAG, "onCreate");
		// pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		// helloSound = pool.load(this, R.raw.s001, 1);
		// disconnectedSound = pool.load(this, R.raw.s002, 1);

		registerReceiver(mDisconnectActionBroadcastReceiver, new IntentFilter(ACTION_DISCONNECT));
		registerReceiver(mIntentBroadcastReceiver, new IntentFilter(ACTION_SEND));
	}

	@Override
	public void onDestroy() {
		DebugLogger.d(TAG, "onDestroy");
		// when user has disconnected from the sensor, we have to cancel the
		// notification that we've created some milliseconds before using
		// unbindService
		msgTmpData = "";
		isHclearSended = false;
		cancelNotification();
		unregisterReceiver(mDisconnectActionBroadcastReceiver);
		unregisterReceiver(mIntentBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onRebind() {
		DebugLogger.d(TAG, "onRebind");
		// when the activity rebinds to the service, remove the notification
		cancelNotification();
	}

	@Override
	protected void onUnbind() {
		DebugLogger.d(TAG, "onUnbind");
		// when the activity closes we need to show the notification that user
		// is connected to the sensor
		// createNotification(R.string.uart_notification_connected_message, 0);
	}

	@Override
	public void onDeviceDisconnected() {
		super.onDeviceDisconnected();
		// pool.play(disconnectedSound, 1, 1, 1, 0, 1);
		SmartWeightApplication.batteryState = 0;
		DebugLogger.d(TAG, "onDeviceDisconnected");
	}

	@Override
	protected void onServiceStarted() {
		DebugLogger.d(TAG, "onServiceStarted");
		// logger is now available. Assign it to the manager
		// mManager.setLogger(getLogSession());
	}

	@Override
	public void onDataReceived(final String data) {
		DebugLogger.d(TAG, "\"" + data + "\" received");

		final Intent broadcast = new Intent(BROADCAST_UART_RX);
		broadcast.putExtra(EXTRA_DATA, data);
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

		Intent receivedDataBroadcast = null;

		msgTmpData += data;
		if(!msgTmpData.contains(";") && !msgTmpData.contains("DCLK")) {
			return;
		}

		String[] receivedData = msgTmpData.replace(";", "").split(":");
		String key;
		int deviceVersion = SharedPreference.getInstance(this).getValue("DeviceFirmwareVersion", 0);

		if (receivedData.length >= 1) {
			key = receivedData[0];
			receivedDataBroadcast = new Intent(ACTION_RECEIVE);
			if ("CRI".equals(key)) {


				Long duration = Long.valueOf(receivedData[2]);
				if (duration < 0 || duration > 5000)
					duration = 5000l;

				Long rpm = 0L;

				if(deviceVersion > 12) {
					int count  = Integer.parseInt(receivedData[1]);
					if(count >= 5) {
						receivedDataBroadcast = new Intent(ACTION_SEND_AUTO_BASICCOUNT);
						receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_COUNT, receivedData[1]);
						receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_DURATION, String.valueOf(duration));
						receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_RPM, String.valueOf(rpm));
						if(deviceVersion > 15) {
							receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_CALORIE, receivedData[3]);
						}else {
							receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_DIRECTION, receivedData[3]);
						}
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(receivedDataBroadcast);
					}
				}

				receivedDataBroadcast = new Intent(ACTION_RECEIVE_CRI);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_COUNT, receivedData[1]);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_DURATION, String.valueOf(duration));
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_RPM, String.valueOf(rpm));
				if(deviceVersion > 15) {
					receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_CALORIE, receivedData[3]);
				}else {
					receivedDataBroadcast.putExtra(ACTION_RECEIVE_CRI_DIRECTION, receivedData[3]);
				}
			} else if ("BAT".equals(key)) {
				if("OFF".equals(receivedData[1])) {
					isHclearSended = true;
					receivedDataBroadcast = new Intent(ACTION_RECEIVE_COUNT_FINISH);
				}else {
					SmartWeightApplication.batteryState = Integer.parseInt(receivedData[1]);
					SharedPreference.getInstance(this).put("DeviceBattery", Integer.parseInt(receivedData[1]));
					super.onBatteryValueReceived(Integer.parseInt(receivedData[1]));
				}
			} else if ("SID".equals(key)) {
				isHclearSended = false;
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_SID);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_DATA, receivedData[1]);
				SharedPreference.getInstance(this).put("DeviceSID", receivedData[1]);
			} else if ("VER".equals(key)) {
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_VER);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_DATA, receivedData[1]);
				SharedPreference.getInstance(this).put("DeviceFirmwareVersion", Integer.parseInt(receivedData[1]));
				if(!(Integer.parseInt(receivedData[1]) > 11))
					new Handler(getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
							mManager.send("LBG?");
						}
					}, 400);
				
			} else if ("LBG".equals(key)) {
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_LBG);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_DATA, receivedData[1]);
				SharedPreference.getInstance(this).put("DeviceLBG", Integer.parseInt(receivedData[1]));
			}else if("WEI".equals(key)) {
				new Handler(getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						mManager.send("HCOUNT?");
					}
				}, 400);
				msgTmpData = "";

			} else if ("ECO".equals(key)) {
				isEcoFlag = true;
			} else if("DCLK".equals(key)) {
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_DCLK);
			} else if("ACT".equals(key)) {
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_ACT);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_DATA, receivedData[1]);
				SharedPreference.getInstance(this).put("DeviceACT", Integer.parseInt(receivedData[1]));
				DebugLogger.d("SmartGymBaseBasicCountActivity", "DeviceACT = " + receivedData[1]);
				msgTmpData = "";
			} else if("HCOUNT".equals(key)) {
				int historyCount = Integer.parseInt(receivedData[1]);
				mHistoryCount = historyCount;
				DebugLogger.d(TAG, "Histroy Count = " + historyCount);

				if(historyCount > 0) {

					receivedDataBroadcast = new Intent(ACTION_RECEIVE_HCOUNT);
					receivedDataBroadcast.putExtra(ACTION_RECEIVE_HCOUNT_COUNT, mHistoryCount);
					DebugLogger.d(TAG, "UARTService  ACTION_RECEIVE_HCOUNT call!!!!!");

//					mManager.send("HISTORY?");

//					new Handler(getMainLooper()).postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							mManager.send("HISTORY?");
//						}
//					}, 400);
				}else {
					new Handler(getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
							mManager.send("HCLEAR!");
//							isHclearSended = true;
						}
					}, 800);
				}
			} else if("TMP".equals(key)) {

			}else if("INT".equals(key)) { 			//msgTmpData.contains("INT:")) {
			}else if("BRK".equals(key)) {
			}else if("MODE".equals(key)) {
			}else if("H".equals(key)) {

			}else if("LED".equals(key)) {
			}

			msgTmpData = "";
			sendBroadcast(receivedDataBroadcast);
		}
	}

	@Override
	public void onDataSent(final String data) {
		DebugLogger.d(TAG, "\"" + data + "\" sent");

		final Intent broadcast = new Intent(BROADCAST_UART_TX);
		broadcast.putExtra(EXTRA_DATA, data);
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
	}


	byte[] squence = new byte[2];

	byte[] recvExerciseCode = new byte[5];

	byte[] convertData = new byte[4];
	int[] value = new int[20];

	@Override
	public void onDataReceived(byte[] data) {
		Arrays.fill(value, 0);

		for(int i=0; i < data.length; i++) {
			if(data[i] < 0) {
				value[i] = data[i] &0xFF;
			}else {
				value[i] = data[i];
			}
		}

		Intent receivedDataBroadcast = null;

		int cmdIsResponse = value[0] >> 7;
		int cmd = value[0] & 0x7F;

		squence[0] = (byte)value[1];
		squence[1] = (byte)(value[2]>>3);

		int sequence = (value[1]<< 3)+ ((value[2] >>5) &0x7);
		int cmdLen = (value[2] & 0x1F);

		DebugLogger.d(TAG, "onDataReceived cmdIsResponse = " + cmdIsResponse);
		DebugLogger.d(TAG, "onDataReceived cmd = " + cmd);
		DebugLogger.d(TAG, "onDataReceived sequence = " + sequence);
		DebugLogger.d(TAG, "onDataReceived sequence binary = " + Integer.toBinaryString(sequence));
		DebugLogger.d(TAG, "onDataReceived sequence value[1]= " + value[1]);
		DebugLogger.d(TAG, "onDataReceived sequence value[2] = " + value[2]);


		DebugLogger.d(TAG, "onDataReceived data[1] = " + data[1] + ", data[2] = " + data[2]);
		DebugLogger.d(TAG, "onDataReceived value[1] = " + value[1] + ", value[2] = " + value[2]);
		DebugLogger.d(TAG, "onDataReceived cmdLen = " + cmdLen);

		switch(cmd) {
			case CmdConst.CMD_REQUEST_START:
				DebugLogger.d(TAG, "CmdConst.CMD_REQUEST_START ack recevied!");
				recvExerciseCode[0] = (byte)value[3];
				recvExerciseCode[1] = (byte)value[4];
				recvExerciseCode[2] = (byte)value[5];
				recvExerciseCode[3] = (byte)value[6];
				recvExerciseCode[4] = (byte)value[7];

				String exerciseName = SmartWeightUtility.getExerciseName(this, recvExerciseCode);

				receivedDataBroadcast = new Intent(ACTION_RECEIVE_EXERCISE_DATA);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_EXERCISE_DATA_CODE, exerciseName);
				break;

			case CmdConst.CMD_RESPONSE_ANGLE_DATA:
				Arrays.fill( convertData, (byte) 0);
				convertData[0] = (byte)value[7];
				convertData[1] = (byte)value[8];
				convertData[2] = (byte)value[9];
				convertData[3] = (byte)value[10];

				int x1 = java.nio.ByteBuffer.wrap(convertData).order(ByteOrder.LITTLE_ENDIAN).getInt();


				convertData[0] = (byte)value[11];
				convertData[1] = (byte)value[12];
				convertData[2] = (byte)value[13];
				convertData[3] = (byte)value[14];

				int y1 = java.nio.ByteBuffer.wrap(convertData).order(ByteOrder.LITTLE_ENDIAN).getInt();

				convertData[0] = (byte)value[15];
				convertData[1] = (byte)value[16];
				convertData[2] = (byte)value[17];
				convertData[3] = (byte)value[18];

				int z1 = java.nio.ByteBuffer.wrap(convertData).order(ByteOrder.LITTLE_ENDIAN).getInt();

				convertData[0] = (byte)value[3];
				convertData[1] = (byte)value[4];
				convertData[2] = (byte)value[5];
				convertData[3] = (byte)value[6];

				int angle = java.nio.ByteBuffer.wrap(convertData).order(ByteOrder.LITTLE_ENDIAN).getInt();


				DebugLogger.d(TAG, "onDataReceived x = " + x1 + ", y = " + y1 + ", z = " + z1);
				DebugLogger.d(TAG, "onDataReceived angle = " + angle);

				receivedDataBroadcast = new Intent(ACTION_RECEIVE_ANGLE_DATA);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_WEIGHT_DATA_XVALUE, x1);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_WEIGHT_DATA_YVALUE, y1);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_WEIGHT_DATA_ZVALUE, z1);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_WEIGHT_DATA_ANGLE, angle);
				break;

			case CmdConst.CMD_RESPONSE_COUNT_DATA:
				int count = (value[4] << 8) | value[3];
				int accuracy = (value[6] << 8) | value[5];

				DebugLogger.d(TAG, "CmdConst.CMD_RESPONSE_COUNT_DATA count = " + count + ", accuracy = " + accuracy);

				receivedDataBroadcast = new Intent(ACTION_RECEIVE_COUNT_DATA);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_COUNT_DATA_COUNT, count);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_COUNT_DATA_ACCURACY, accuracy);
				break;

			case CmdConst.CMD_RESPONSE_BATTERY :
				int batteryinfo = value[3];
				SmartWeightApplication.batteryState = batteryinfo;
				SharedPreference.getInstance(this).put("DeviceBattery", batteryinfo);
				super.onBatteryValueReceived(batteryinfo);
				DebugLogger.d(TAG, "onDataReceived batteryinfo = " + batteryinfo);
				break;

			case CmdConst.CMD_RESPONSE_BREAKTIME_DATA:
				convertData[0] = (byte)value[3];
				convertData[1] = (byte)value[4];
				convertData[2] = (byte)value[5];
				convertData[3] = (byte)value[6];

				int breakTime = java.nio.ByteBuffer.wrap(convertData).order(ByteOrder.LITTLE_ENDIAN).getInt();
				DebugLogger.d(TAG, "CmdConst.CMD_RESPONSE_BREAKTIME_DATA breakTime = " + breakTime);
				receivedDataBroadcast = new Intent(ACTION_RECEIVE_BREAK_TIME);
				receivedDataBroadcast.putExtra(ACTION_RECEIVE_BREAK_TIME_DATA, breakTime);
				break;
		}

		if(null != receivedDataBroadcast) {
			sendBroadcast(receivedDataBroadcast);
		}
	}

	public int byteToint2(byte[] arr){
		return (arr[3] & 0xff)<<24 | (arr[2] & 0xff)<<16 |
				(arr[1] & 0xff)<<8 | (arr[0] & 0xff);
	}

	/**
	 * Creates the notification
	 *
	 * @param messageResId
	 *            message resource id. The message must have one String
	 *            parameter,<br />
	 *            f.e.
	 *            <code>&lt;string name="name"&gt;%s is connected&lt;/string&gt;</code>
	 * @param defaults
	 *            signals that will be used to notify the user
	 */
	@SuppressWarnings("unused")
	private void createNotification(final int messageResId, final int defaults) {
		// final Intent parentIntent = new Intent(this, FeaturesActivity.class);
		// parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// final Intent targetIntent = new Intent(this, UARTActivity.class);
		//
		// final Intent disconnect = new Intent(ACTION_DISCONNECT);
		// final PendingIntent disconnectAction =
		// PendingIntent.getBroadcast(this, DISCONNECT_REQ, disconnect,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		//
		// // both activities above have launchMode="singleTask" in the
		// AndroidManifest.xml file, so if the task is already running, it will
		// be resumed
		// final PendingIntent pendingIntent = PendingIntent.getActivities(this,
		// OPEN_ACTIVITY_REQ, new Intent[] { parentIntent, targetIntent },
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// final Notification.Builder builder = new
		// Notification.Builder(this).setContentIntent(pendingIntent);
		// builder.setContentTitle(getString(R.string.app_name)).setContentText(getString(messageResId,
		// getDeviceName()));
		// builder.setSmallIcon(R.drawable.ic_stat_notify_uart);
		// builder.setShowWhen(defaults !=
		// 0).setDefaults(defaults).setAutoCancel(true).setOngoing(true);
		// builder.addAction(R.drawable.ic_action_bluetooth,
		// getString(R.string.uart_notification_action_disconnect),
		// disconnectAction);
		//
		// final Notification notification = builder.build();
		// final NotificationManager nm = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// nm.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * Cancels the existing notification. If there is no active notification
	 * this method does nothing
	 */
	private void cancelNotification() {
		final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}

	/**
	 * This broadcast receiver listens for {@link #ACTION_DISCONNECT} that may
	 * be fired by pressing Disconnect action button on the notification.
	 */
	private final BroadcastReceiver mDisconnectActionBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			DebugLogger.d(TAG, "[Notification] Disconnect action pressed");
			if (isConnected())
				getBinder().disconnect();
			else
				stopSelf();
		}
	};

	/**
	 * Broadcast receiver that listens for {@link #ACTION_SEND} from other apps.
	 * Sends the String or int content of the {@link Intent#EXTRA_TEXT} extra to
	 * the remote device. The integer content will be sent as String (65 ->
	 * "65", not 65 -> "A").
	 */
	private BroadcastReceiver mIntentBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final boolean hasMessage = intent.hasExtra(Intent.EXTRA_TEXT);
			if (hasMessage) {
				String message = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (message == null) {
					final int intValue = intent.getIntExtra(Intent.EXTRA_TEXT, Integer.MIN_VALUE);
					if (intValue != Integer.MIN_VALUE)
						message = String.valueOf(intValue);
				}

				if (message != null) {
					DebugLogger.d(TAG, "[Broadcast] " + ACTION_SEND + " broadcast received with data: \"" + message + "\"");
					mManager.send(message);
					return;
				}
			}
			// No data od incompatible type of EXTRA_TEXT
			if (!hasMessage)
				DebugLogger.d(TAG, "[Broadcast] " + ACTION_SEND + " broadcast received no data.");
			else
				DebugLogger.d(TAG, "[Broadcast] " + ACTION_SEND + " broadcast received incompatible data type. Only String and int are supported.");
		}
	};

	@Override
	public void onDeviceReady() {
		super.onDeviceReady();
	}

}
