package com.tangramfactory.smartweight.activity.device;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.tangramfactory.smartweight.BaseBLEApplication;
import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScanner;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScannerFactory;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScannerResult;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.SharedPreference;
import com.tangramfactory.smartweight.view.ProgressWheel;

import java.io.File;
import java.io.FilenameFilter;

import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class LocalFileUpdateActivity extends BaseAppCompatActivity {

    final static String TAG = SmartWeightApplication.BASE_TAG + "DeviceFirmwareUpdateActivity";
    public Toolbar toolbar;
    private TextView mTextPercentage;
    private TextView mTextUploading;
    private ProgressWheel progressWheelLinear;
    String firmwarePath;

    int serverVersion = 0;
    String deviceName;
    String deviceAddress;
    BootloaderScanner bootloaderScanner;
    boolean isCompleted = false;

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("LocalFile Update");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file_update);
        setStatusBarColor(R.color.colorPrimary);
        setToolbar();
        loadCodeView();

        progressWheelLinear.setProgress(0.0f);
        progressWheelLinear.setCallback(new ProgressWheel.ProgressCallback() {
            @Override
            public void onProgressUpdate(float progress) {
                mTextPercentage.setText(String.valueOf((int) (progress * 100)));
            }
        });
        progressWheelLinear.setCircleRadius(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 828, getResources().getDisplayMetrics()));
        progressWheelLinear.setRimWidth(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        progressWheelLinear.setBarWidth(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        progressWheelLinear.setBarColor(getResources().getColor(R.color.point_color));
        progressWheelLinear.setRimColor(Color.parseColor("#242527"));

        mApplication.send("dfu");

        BaseBLEApplication.isDFU = true;
        startInitDFU();
    }

    public void startInitDFU() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generat
                bootloaderScanner = BootloaderScannerFactory.getScanner();
                bootloaderScanner.setBLEScanCallback(new BootloaderScannerResult() {

                    @Override
                    public void onScannedDevices(final BluetoothDevice device, String name, int rssi,
                                                 boolean isBonded) {
                        DebugLogger.d(TAG, "name : " + name);
                        final DfuServiceInitiator starter = new DfuServiceInitiator(device.getAddress())
                                .setDeviceName(name).setKeepBond(true);
                        startDFU(starter, device);
                    }
                });
                deviceAddress = SharedPreference.getInstance(mContext).getValue("DeviceAddress", "NONE");
//				bootloaderScanner.setDFUMode(true);
                bootloaderScanner.beginScanning(BootloaderScanner.SCAN_MODE_DFU, deviceAddress);
            }
        }, 1000);
    }

    private void loadCodeView() {
        mTextPercentage = (TextView) findViewById(R.id.percentText);
        mTextUploading = (TextView) findViewById(R.id.listenerText);
        progressWheelLinear = (ProgressWheel) findViewById(R.id.ProgressWheel);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isCompleted)
            finish();

        isCompleted = false;
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            DebugLogger.d(TAG, "onDeviceConnecting()");
            mTextUploading.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            DebugLogger.d(TAG, "onDfuProcessStarting()");
            mTextUploading.setText(R.string.dfu_status_starting);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            DebugLogger.d(TAG, "onEnablingDfuMode()");
            mTextUploading.setText(R.string.dfu_status_switching_to_dfu);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            DebugLogger.d(TAG, "onFirmwareValidating()");
            mTextUploading.setText(R.string.dfu_status_validating);
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            DebugLogger.d(TAG, "onDeviceDisconnecting()");
            mTextUploading.setText(R.string.dfu_status_disconnecting);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            DebugLogger.d(TAG, "onDfuCompleted()");

            isCompleted = true;
            if(serverVersion != 0) {
                SharedPreference.getInstance(mContext).put("DeviceFirmwareVersion", serverVersion);
            }
            finish();
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            DebugLogger.d(TAG, "onDfuAborted()");
            mTextPercentage.setText(R.string.dfu_status_aborted);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextUploading.setText("onUploadCanceled");
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed,
                                      final float avgSpeed, final int currentPart, final int partsTotal) {
            progressWheelLinear.setProgress(percent / 100f);
            mTextPercentage.setText(String.valueOf(percent));
            if (partsTotal > 1)
                mTextUploading.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                mTextUploading.setText(R.string.dfu_status_uploading);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            DebugLogger.d(TAG, "onError() : error " + error+" : "+message);
            switch (error) {
                case DfuBaseService.ERROR_DEVICE_DISCONNECTED:
                case DfuBaseService.ERROR_SERVICE_DISCOVERY_NOT_STARTED:
                case DfuBaseService.ERROR_SERVICE_NOT_FOUND:
                case 0x08:																			// Gatt Timeout Error
                    isCompleted = true;
                    Toast.makeText(mContext, "Firmware Update Error =" + error, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    mTextUploading.setText(message+"\nError Code : "+error);
                    bootloaderScanner.stopScanning();
                    startInitDFU();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        BaseBLEApplication.isDFU = false;
        mApplication.startScan(BootloaderScanner.SCAN_MODE_TARGET);
        super.onDestroy();
    }

    private void startDFU(final DfuServiceInitiator starter, final BluetoothDevice device) {
        DebugLogger.d(TAG, "getName : " + (deviceName = device.getName()));
        DebugLogger.d(TAG, "getAddress : " + (deviceAddress = device.getAddress()));
        deviceDisconnect();

//        String firmwareRoot = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator
//                + "SmartRopeFirmware" + File.separator;

        String firmwareRoot = Environment.getExternalStorageDirectory() + "/Download/";

        File file = new File(firmwareRoot);
        String[] list = file.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.indexOf("firmware_") > -1) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        DebugLogger.d(TAG, "firmwareRoot : " + firmwareRoot);
        DebugLogger.d(TAG, "list : " + list);
        if (list != null && list.length > 0) {
            DebugLogger.d(TAG, "FileName : " + list[0]);
            final String firmwarePath = firmwareRoot + list[0];
            starter.setZip(firmwarePath);
            starter.start(mContext, DfuService.class);
        } else {

        }
    }

    public void deviceDisconnect() {
        SharedPreference.getInstance(mContext).put("DeviceName", "NONE");
        SharedPreference.getInstance(mContext).put("DeviceAddress", "NONE");
        SmartWeightApplication.batteryState = 0;
        mApplication.disconnect();
    }

    @Override
    public void onBackPressed() {
    }

}
