package com.tangramfactory.smartweight.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.tangramfactory.smartweight.utility.ParserUtils;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class BootloaderScannerJB implements BootloaderScanner, BluetoothAdapter.LeScanCallback {
	private boolean mFound;
	private static final boolean DEVICE_NOT_BONDED = false;
	public static final int NO_RSSI = -90;
	public String targetAddress;
	BluetoothAdapter adapter;
//	boolean isDFU = false;
	int currentScanMode = 0;
	@Override
	public void beginScanning(int scanMode, String deviceAddress) {
		stopScanning();
		mFound = false;
		currentScanMode = scanMode;
		targetAddress = deviceAddress;
		adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter != null)
			adapter.startLeScan(this);
	}

	@Override
	public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
		if (device != null) {
			String deviceName = "";
			targetAddress = targetAddress.toUpperCase(Locale.getDefault());
			if (TextUtils.isEmpty(device.getName())) {
				deviceName = ParserUtils.parseAdertisedData(scanRecord);
			} else {
				deviceName = device.getName();
			}
			String deviceAddress = device.getAddress().toUpperCase(Locale.getDefault());
			if (!mFound) {
//				if (isDFU) {
//					if (deviceName != null && rssi > NO_RSSI && deviceName.indexOf("UPDATE") > -1) {
//						onResult(device, deviceName, rssi);
//					}
//				} else {
//					if (targetAddress.equals(deviceAddress)) {
//						onResult(device, deviceName, rssi);
//					} else if (deviceName != null && rssi > NO_RSSI && ((deviceName.indexOf("SmartRope") > -1))) {
//						onResult(device, deviceName, rssi);
//					}
//				}
				
				switch (currentScanMode) {
				case BootloaderScanner.SCAN_MODE_RSSI:
					if (deviceName != null && (deviceName.indexOf("TG") > -1)) {
						onResult(device, deviceName, rssi);
					}
					break;
				case BootloaderScanner.SCAN_MODE_TARGET:
					if (targetAddress.equals(device.getAddress())) {
						onResult(device, deviceName, rssi);
					}
					break;
				case BootloaderScanner.SCAN_MODE_DFU:
					if (deviceName != null && deviceName.indexOf("UPDATE") > -1) {
						onResult(device, deviceName, rssi);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private void onResult(BluetoothDevice device, String deviceName, int rssi) {
		stopScanning();
//		isDFU = false;
		bootloaderScannerResult.onScannedDevices(device, deviceName, rssi, DEVICE_NOT_BONDED);
	}

	public BootloaderScannerResult bootloaderScannerResult;

	public void setBLEScanCallback(BootloaderScannerResult bootloaderScannerResult) {
		this.bootloaderScannerResult = bootloaderScannerResult;
	}

	@Override
	public void stopScanning() {
		mFound = true;
		if (adapter != null)
			adapter.stopLeScan(this);
	}

//	@Override
//	public void setDFUMode(boolean isDFU) {
//		this.isDFU = isDFU;
//
//	}
}
