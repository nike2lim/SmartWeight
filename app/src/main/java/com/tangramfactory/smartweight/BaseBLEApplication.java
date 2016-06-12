package com.tangramfactory.smartweight;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.tangramfactory.smartweight.activity.device.CmdConst;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScanner;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScannerFactory;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScannerResult;
import com.tangramfactory.smartweight.ble.service.BleManagerCallbacks;
import com.tangramfactory.smartweight.ble.service.BleProfileService;
import com.tangramfactory.smartweight.ble.service.UARTService;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SharedPreference;

import java.util.Arrays;

/**
 * Created by B on 2016-06-01.
 */
public class BaseBLEApplication<E extends BleProfileService.LocalBinder> extends MultiDexApplication implements BleManagerCallbacks {
    protected String TAG = SmartWeightApplication.BASE_TAG + "BaseBLEApplication";

    protected Context mContext;
    public String mDeviceName;
    private Intent serviceIntent;

    BootloaderScanner bootloaderScanner;
    private UARTService.UARTBinder mServiceBinder;

    public boolean isUserForceDisconnected = false;
    public boolean isShutDown = false;
    static public boolean isDFU = false;


    public void send(String msg) {
        if (mServiceBinder != null && mServiceBinder.isConnected())
            mServiceBinder.send(msg);
    }

    byte[] requestBuffer = new byte[CmdConst.REQUEST_CMD_SIZE];

    public void send(byte cmd, byte cmdLen, byte[] payload) {
        Arrays.fill( requestBuffer, (byte) 0);
        byte[] commandByte = new byte[3 + cmdLen + 1];

        byte req = (byte)(0<<7);
        byte reqCmd = (byte) ((byte)cmd & CmdConst.MASK_COMMAND);
        req = (byte)(req|reqCmd);
        commandByte[0] = req;

        DebugLogger.d(TAG, "send comd = " + Integer.toBinaryString(commandByte[0]));


        commandByte[2] = (byte)(commandByte[2]|cmdLen);
//        arraycopy(Object src, int srcPos,
//        Object dst, int dstPos, int length);
        if(null != payload && cmdLen > 0 ) {
            System.arraycopy(payload,0, commandByte, 3, cmdLen);
        }

        byte cheksum = 0;
        for(int i=0; i< commandByte.length; i++) {
            cheksum = (byte) (cheksum + commandByte[i]);
        }

        commandByte[commandByte.length-1] = cheksum;

//        requestBuffer[19] = 0;

        if (mServiceBinder != null && mServiceBinder.isConnected())
            mServiceBinder.send(commandByte);

        //req[1] : sequence Number
        //req[2] : sequence Number(5bit) & cmd length(3bit)


        //req[3]
        //req[4]
        //req[5]
        //req[6]
        //req[7]
        //req[8]
        //req[9]
        //req[10]
        //req[11]
        //req[12]

        //req[18]   payload
        //req[19] checksum

    }

    public void disconnect() {
        DebugLogger.d(TAG, "BaseBLEApplication disconnect()");
        if (mServiceBinder != null && mServiceBinder.isConnected()) {
            DebugLogger.d(TAG, "BaseBLEApplication mServiceBinder.disconnect()");
            mServiceBinder.disconnect();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (mServiceBinder == null || !mServiceBinder.isBinderAlive() || !mServiceBinder.isConnected()) {
            serviceIntent = new Intent(this, getServiceClass());
            if (bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE))
                DebugLogger.d(TAG, "Binding to the service...");
        }

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mCommonBroadcastReceiver, makeCommonIntentFilter());
    }

    public void serviceRelease() {
        isShutDown = true;
        stopScan();
        if (serviceIntent != null) {
            if (mServiceBinder != null)
                mServiceBinder.disconnect();
            stopService(serviceIntent);
        }

    }

    // public void startScanByNormal() {
    // String deviceAddress =
    // SharedPreference.getInstance(mContext).getValue("DeviceAddress", "NONE");
    // if (BluetoothAdapter.getDefaultAdapter() != null &&
    // BluetoothAdapter.getDefaultAdapter().isEnabled() &&
    // !TextUtils.isEmpty(deviceAddress)
    // && !deviceAddress.equals("NONE") && !isConnected()) {
    // startScan();
    // }
    // }

    public void startScan(final int scanMode) {
        if (!isShutDown && !isUserForceDisconnected) {

            String deviceAddress = SharedPreference.getInstance(mContext).getValue("DeviceAddress", "NONE");

            switch (scanMode) {
                case BootloaderScanner.SCAN_MODE_RSSI:
                    SharedPreference.getInstance(mContext).put("DeviceAddress", "NONE");
                    break;
                case BootloaderScanner.SCAN_MODE_TARGET:

                    break;
                case BootloaderScanner.SCAN_MODE_DFU:

                    break;
                default:
                    break;
            }

            if (!(scanMode == BootloaderScanner.SCAN_MODE_TARGET && "NONE".equals(deviceAddress))) {
                if (bootloaderScanner == null)
                    bootloaderScanner = BootloaderScannerFactory.getScanner();
                bootloaderScanner.setBLEScanCallback(new BootloaderScannerResult() {

                    @Override
                    public void onScannedDevices(BluetoothDevice device, String name, int rssi, boolean isBonded) {
                        if (serviceIntent == null)
                            serviceIntent = new Intent(mContext, getServiceClass());

                        serviceIntent.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, device.getAddress());
                        startService(serviceIntent);

                        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
                        if (scanMode == BootloaderScanner.SCAN_MODE_RSSI) {
                            SharedPreference.getInstance(mContext).put("DeviceName", device.getName());
                            SharedPreference.getInstance(mContext).put("DeviceAddress", device.getAddress());
                        }

                    }
                });
                bootloaderScanner.beginScanning(scanMode, deviceAddress);
            }
        }
    }

    private IntentFilter makeCommonIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleProfileService.BROADCAST_CONNECTION_STATE);
        intentFilter.addAction(BleProfileService.BROADCAST_SERVICES_DISCOVERED);
        intentFilter.addAction(BleProfileService.BROADCAST_DEVICE_READY);
        intentFilter.addAction(BleProfileService.BROADCAST_BOND_STATE);
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
                            mDeviceName = intent.getStringExtra(BleProfileService.EXTRA_DEVICE_NAME);
                            onDeviceConnected();
                            break;
                        }
                        case BleProfileService.STATE_DISCONNECTED: {
                            onDeviceDisconnected();
                            mDeviceName = null;
                            break;
                        }
                        case BleProfileService.STATE_LINK_LOSS: {
                            onLinklossOccur();
                            break;
                        }
                        case BleProfileService.STATE_CONNECTING:
                            break;
                        case BleProfileService.STATE_DISCONNECTING:
                            onDeviceDisconnecting();
                            break;
                        default:
                            // there should be no other actions
                            break;
                    }
                    break;
                }
                case BleProfileService.BROADCAST_SERVICES_DISCOVERED: {
                    final boolean primaryService = intent.getBooleanExtra(BleProfileService.EXTRA_SERVICE_PRIMARY, false);
                    final boolean secondaryService = intent.getBooleanExtra(BleProfileService.EXTRA_SERVICE_SECONDARY, false);

                    if (primaryService) {
                        onServicesDiscovered(secondaryService);
                    } else {
                        onDeviceNotSupported();
                    }
                    break;
                }
                case BleProfileService.BROADCAST_DEVICE_READY: {
                    onDeviceReady();
                    break;
                }
                case BleProfileService.BROADCAST_BOND_STATE: {
                    final int state = intent.getIntExtra(BleProfileService.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    switch (state) {
                        case BluetoothDevice.BOND_BONDING:
                            onBondingRequired();
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            onBonded();
                            break;
                    }
                    break;
                }
                case BleProfileService.BROADCAST_BATTERY_LEVEL: {
                    final int value = intent.getIntExtra(BleProfileService.EXTRA_BATTERY_LEVEL, -1);
                    if (value > 0)
                        onBatteryValueReceived(value);
                    break;
                }
                case BleProfileService.BROADCAST_ERROR: {
                    final String message = intent.getStringExtra(BleProfileService.EXTRA_ERROR_MESSAGE);
                    final int errorCode = intent.getIntExtra(BleProfileService.EXTRA_ERROR_CODE, 0);
                    onError(message, errorCode);
                    break;
                }
            }
        }
    };

    public boolean isConnected() {
        if (mServiceBinder != null)
            DebugLogger.d(TAG, "isConnected mService isConnected : " + mServiceBinder.isConnected());
        return mServiceBinder == null ? false : mServiceBinder.isConnected();
    }

    @Override
    public void onDeviceConnected() {

    }

    @Override
    public void onDeviceDisconnecting() {
        SmartWeightApplication.batteryState = 0;
        initDeviceData();
//        if (!isDFU) {
//            if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled())
//                startScan(BootloaderScanner.SCAN_MODE_RSSI);
//        }
    }

    @Override
    public void onDeviceDisconnected() {
        SmartWeightApplication.batteryState = 0;
        initDeviceData();
//        if (!isDFU) {
//            if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled())
//                startScan(BootloaderScanner.SCAN_MODE_RSSI);
//        }
    }

    @Override
    public void onLinklossOccur() {

    }

    @Override
    public void onServicesDiscovered(boolean optionalServicesFound) {

    }

    @Override
    public void onDeviceReady() {

    }

    @Override
    public void onBatteryValueReceived(int value) {

    }

    @Override
    public void onBondingRequired() {

    }

    @Override
    public void onBonded() {

    }

    @Override
    public void onError(String message, int errorCode) {

    }

    @Override
    public void onDeviceNotSupported() {

    }

    protected void onServiceBinded(UARTService.UARTBinder binder) {
        mServiceBinder = binder;
    }

    protected void onServiceUnbinded() {
        mServiceBinder = null;
    }


    /**
     * device disconnect. init device data
     */
    public void initDeviceData() {
        SharedPreference.getInstance(mContext).put("DeviceName", "NONE");
        SharedPreference.getInstance(mContext).put("DeviceSID", "");
        SharedPreference.getInstance(mContext).put("DeviceBattery", 0);
        SharedPreference.getInstance(mContext).put("DeviceLBG", 0);
        SharedPreference.getInstance(mContext).put("DeviceFirmwareVersion", 0);
    }

    protected Class<? extends BleProfileService> getServiceClass() {
        return UARTService.class;
    }

    public void stopScan() {
        if (bootloaderScanner != null)
            bootloaderScanner.stopScanning();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final E bleService = (E) service;
            onServiceBinded((UARTService.UARTBinder) bleService);
            mDeviceName = bleService.getDeviceName();
            if (bleService.isConnected()) {
                onDeviceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mServiceBinder = null;
            mDeviceName = null;
            onServiceUnbinded();
        }
    };

}
