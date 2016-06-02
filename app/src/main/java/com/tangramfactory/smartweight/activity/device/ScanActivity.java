package com.tangramfactory.smartweight.activity.device;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.ble.scanner.BootloaderScanner;
import com.tangramfactory.smartweight.view.RippleBackground;

import org.w3c.dom.Text;

public class ScanActivity extends BaseAppCompatActivity {
    final static String TAG = SmartWeightApplication.BASE_TAG + "ScanActivity";

    private final static int PERMISSIONS_REQUEST_READ_CONTACTS = 0x99;

    static final int REQUEST_ENABLE_BT = 0x99;

    public Toolbar toolbar;
    RippleBackground rippleBackground;

    ImageView connectCompleteImageView;
    TextView connectTextView;
    TextView deviceIdTextView;
    Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setToolbar();
        loadCodeView();
        mApplication.isUserForceDisconnected = false;
        mApplication.stopScan();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.disconnect();
            }
        });
        setSupportActionBar(toolbar);
    }

    protected void loadCodeView() {
        rippleBackground=(RippleBackground)findViewById(R.id.ripplebackground);

        connectCompleteImageView = (ImageView)findViewById(R.id.connect_complete);
        connectTextView =  (TextView)findViewById(R.id.text_connected);
        deviceIdTextView =  (TextView)findViewById(R.id.device_id);
        connectButton =  (Button) findViewById(R.id.connectButton);

        if(mApplication.isConnected()) {
            connectedUI();
        }else {
            disconnectedUI();
        }

        int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_READ_CONTACTS);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.connectButton:
                if(mApplication.isConnected()) {
                    startActivity(new Intent(mContext, LocalFileUpdateActivity.class));
                    finish();
                }else {
                    rippleBackground.startRippleAnimation();
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    if (!adapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        mApplication.startScan(BootloaderScanner.SCAN_MODE_RSSI);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDeviceConnected() {
        super.onDeviceConnected();
        rippleBackground.stopRippleAnimation();
        connectedUI();
    }

    @Override
    protected void onDeviceDisconnected() {
        super.onDeviceDisconnected();
        disconnectedUI();
    }

    @Override
    protected void onDeviceLinkLoss() {
        super.onDeviceLinkLoss();
        disconnectedUI();
    }

    private void connectedUI() {
        connectButton.setText(getString(R.string.text_start));
        connectCompleteImageView.setVisibility(View.VISIBLE);
        connectTextView.setVisibility(View.VISIBLE);
        deviceIdTextView.setVisibility(View.VISIBLE);
        deviceIdTextView.setText(mApplication.mDeviceName);
    }

    private void disconnectedUI() {
        connectButton.setText(getString(R.string.text_connect));
        connectCompleteImageView.setVisibility(View.GONE);
        connectTextView.setVisibility(View.GONE);
        deviceIdTextView.setVisibility(View.GONE);
    }
}
