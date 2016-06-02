package com.tangramfactory.smartweight.activity.base;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScanner;
import com.tangramfactory.smartweight.ble.service.BleProfileService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SharedPreference;

/**
 * Created by Shlim on 2016-05-23.
 */
public class BaseAppCompatActivity extends AppCompatActivity {
    public static Context mContext;
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "_BaseAppCompatActivity";
    protected SmartWeightApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApplication = (SmartWeightApplication) getApplication();

        registerReceiver(mBluetoothBroadcastReceiver, makeBluetoothIntentFilter());
        LocalBroadcastManager.getInstance(this).registerReceiver(mCommonBroadcastReceiver, makeCommonIntentFilter());
    }


    public void applicationExit(){
        mApplication.serviceRelease();
        sendBroadcast(new Intent("com.tangramfactory.smartgym.FINISH"));
        finish();
    }

    @SuppressLint("NewApi")
    public void setStatusBarColor(int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(colorResId));
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBluetoothBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCommonBroadcastReceiver);
        super.onDestroy();
    }


    private static IntentFilter makeCommonIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleProfileService.BROADCAST_CONNECTION_STATE);
        intentFilter.addAction(BleProfileService.BROADCAST_DEVICE_READY);
        intentFilter.addAction(BleProfileService.BROADCAST_BATTERY_LEVEL);
        intentFilter.addAction(BleProfileService.BROADCAST_ERROR);
        return intentFilter;
    }

    private final BroadcastReceiver mCommonBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BleProfileService.BROADCAST_CONNECTION_STATE: {
                    final int state = intent.getIntExtra(BleProfileService.EXTRA_CONNECTION_STATE, BleProfileService.STATE_DISCONNECTED);

                    switch (state) {
                        case BleProfileService.STATE_CONNECTED: {
                            onDeviceConnected();
                            break;
                        }
                        case BleProfileService.STATE_DISCONNECTED: {
                            onDeviceDisconnected();
                            break;
                        }
                        case BleProfileService.STATE_LINK_LOSS: {
                            onDeviceLinkLoss();
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }
                case BleProfileService.BROADCAST_DEVICE_READY: {
                    onDeviceReady();
                    break;
                }
                case BleProfileService.BROADCAST_BATTERY_LEVEL: {
                    final int value = intent.getIntExtra(BleProfileService.EXTRA_BATTERY_LEVEL, -1);
                    if (value > 0)
                        onBatteryValueReceived(value);
                    break;
                }
                case BleProfileService.BROADCAST_ERROR: {
//				final String message = intent.getStringExtra(BleProfileService.EXTRA_ERROR_MESSAGE);
//				final int errorCode = intent.getIntExtra(BleProfileService.EXTRA_ERROR_CODE, 0);
                    break;
                }
            }
        }
    };



    private static IntentFilter makeBluetoothIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }

    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch(state) {
                case BluetoothAdapter.STATE_OFF:
                    DebugLogger.d(TAG, "mBluetoothBroadcastReceiver BluetoothAdapter.STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    DebugLogger.d(TAG, "mBluetoothBroadcastReceiver BluetoothAdapter.STATE_TURNING_OFF");
                    break;

                case BluetoothAdapter.STATE_ON:
                    DebugLogger.d(TAG, "mBluetoothBroadcastReceiver BluetoothAdapter.STATE_ON");
                    String deviceAddress = SharedPreference.getInstance(mContext).getValue("DeviceAddress", "NONE");
                    if("NONE".equals(deviceAddress)) {
                        mApplication.startScan(BootloaderScanner.SCAN_MODE_RSSI);
                    }else {
                        mApplication.startScan(BootloaderScanner.SCAN_MODE_TARGET);
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    DebugLogger.d(TAG, "mBluetoothBroadcastReceiver BluetoothAdapter.STATE_TURNING_ON");
                    break;
            }
        }
    };

    protected void onBatteryValueReceived(int value) {}
    protected void onDeviceReady() {}

    protected void onDeviceLinkLoss() {}

    protected void onDeviceDisconnected() {}

    protected void onDeviceConnected() {}
}
