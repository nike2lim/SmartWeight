package com.tangramfactory.smartweight.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;


import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.utility.DebugLogger;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BootloaderScannerLollipop extends ScanCallback implements BootloaderScanner {
	final static String TAG = SmartWeightApplication.BASE_TAG + "BootloaderScannerLollipop";

	private boolean mFound;
	private static final boolean DEVICE_NOT_BONDED = false;
	public static final int NO_RSSI = -90;
	public String targetAddress;
	BluetoothAdapter adapter;
	BluetoothLeScanner scanner;
//	boolean isDFU = false;
	int currentScanMode = 0;

	@Override
	public void beginScanning(int scanMode, String deviceAddress) {
		stopScanning();
		targetAddress = deviceAddress;
		currentScanMode = scanMode;
		DebugLogger.d(TAG, "beginScanning scanMode = " + scanMode + ", targetAddress = " + targetAddress);
		mFound = false;
		adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter != null){
			scanner = adapter.getBluetoothLeScanner();
			if(scanner != null){
				ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(SMART_ROPE_UUID)).build();
				List<ScanFilter> filterList = new ArrayList<ScanFilter>();
				filterList.add(filter);
				final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
				scanner.startScan(/* filters */null, settings, this);
			}
		}
	}

	@Override
	public void onScanResult(final int callbackType, final ScanResult result) {
		BluetoothDevice device = result.getDevice();
		ScanRecord record = result.getScanRecord();
		if (device != null) {
			int rssi = result.getRssi();

			if (!mFound) {
				switch (currentScanMode) {
				case BootloaderScanner.SCAN_MODE_RSSI:
					String deviceName = device.getName();
					String localDeviceName = record.getDeviceName();

					if((null == deviceName || deviceName.length() == 0) &&  (null != localDeviceName && ((localDeviceName.indexOf("M_TEST") > -1)))) {
						onResult(device, rssi);
					}else if(null != device.getName() &&  rssi > NO_RSSI && ((device.getName().indexOf("SmartRope") > -1))) {
						onResult(device, rssi);
					}else {
						if(null != record.getServiceUuids() && null != record.getServiceUuids().get(0) && record.getServiceUuids().get(0).toString().equals(SMART_ROPE_UUID.toString())) {
							onResult(device, rssi);
						}
					}
					break;
				case BootloaderScanner.SCAN_MODE_TARGET:
					DebugLogger.d(TAG, "BootloaderScanner.SCAN_MODE_TARGET");
					if (targetAddress.equals(device.getAddress())) {
						onResult(device, rssi);
					}
					break;
				case BootloaderScanner.SCAN_MODE_DFU:
					if (device.getName() != null && device.getName().indexOf("DfuTarg") > -1) {
						onResult(device, rssi);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private void onResult(BluetoothDevice device, int rssi) {
		try {
			mFound = true;
//			isDFU = false;
			bootloaderScannerResult.onScannedDevices(device, device.getName(), rssi, DEVICE_NOT_BONDED);
			DebugLogger.d(TAG, "onResult stopScanning!!!!!!!!");
			scanner.stopScan(this);
		} catch (Exception e) {
			DebugLogger.e("BootloaderScannerLollipop", e.getLocalizedMessage(), e);
		}
	}

	public BootloaderScannerResult bootloaderScannerResult;

	public void setBLEScanCallback(BootloaderScannerResult bootloaderScannerResult) {
		this.bootloaderScannerResult = bootloaderScannerResult;
	}

	@Override
	public void stopScanning() {
		DebugLogger.d(TAG, "stopScanning!!!!!!!!");
		mFound = true;
		if (scanner != null) {
			try {
				scanner.stopScan(this);
			} catch (Exception e) {
			}
		}
	}

}