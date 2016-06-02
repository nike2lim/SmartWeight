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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;


import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.utility.ParserUtils;

import java.util.Queue;
import java.util.UUID;

import no.nordicsemi.android.error.GattError;


public abstract class BleManager<E extends BleManagerCallbacks> {
    final static String TAG = SmartWeightApplication.BASE_TAG + "BleManager";
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    
    //6e400003-b5a3-f393-e0a9-e50e24dcca9e
//    private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    private final static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private final static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");

    private final static String ERROR_CONNECTION_STATE_CHANGE = "Error on connection state change";
    private final static String ERROR_DISCOVERY_SERVICE = "Error on discovering services";
    private final static String ERROR_AUTH_ERROR_WHILE_BONDED = "Phone has lost bonding information";
    private final static String ERROR_WRITE_DESCRIPTOR = "Error on writing descriptor";
    private final static String ERROR_READ_CHARACTERISTIC = "Error on reading characteristic";

    protected E mCallbacks;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private Context mContext;
    private boolean mUserDisconnected;
    private boolean mConnected;

    private BroadcastReceiver mBondingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

            // Skip other devices
            if (mBluetoothGatt == null || !device.getAddress().equals(mBluetoothGatt.getDevice().getAddress()))
                return;

            DebugLogger.d(TAG, "[Broadcast] Action received: " + BluetoothDevice.ACTION_BOND_STATE_CHANGED + ", bond state changed to: " + bondStateToString(bondState) + " (" + bondState + ")");
            DebugLogger.i(TAG, "Bond state changed for: " + device.getName() + " new state: " + bondState + " previous: " + previousBondState);

            switch (bondState) {
                case BluetoothDevice.BOND_BONDING:
                    mCallbacks.onBondingRequired();
                    break;
                case BluetoothDevice.BOND_BONDED:
                    DebugLogger.i(TAG, "Device bonded");
                    mCallbacks.onBonded();

                    // Start initializing again.
                    // In fact, bonding forces additional, internal service discovery (at least on Nexus devices), so this method may safely be used to start this process again.
                    DebugLogger.v(TAG, "Discovering Services...");
                    DebugLogger.d(TAG, "gatt.discoverServices()");
                    mBluetoothGatt.discoverServices();
                    break;
            }
        }
    };

    private final BroadcastReceiver mPairingRequestBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // Skip other devices
            if (mBluetoothGatt == null || !device.getAddress().equals(mBluetoothGatt.getDevice().getAddress()))
                return;

            // String values are used as the constants are not available for Android 4.3.
            final int variant = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT"/*BluetoothDevice.EXTRA_PAIRING_VARIANT*/, 0);
            DebugLogger.d(TAG, "[Broadcast] Action received: android.bluetooth.device.action.PAIRING_REQUEST"/*BluetoothDevice.ACTION_PAIRING_REQUEST*/ +
                    ", pairing variant: " + pairingVariantToString(variant) + " (" + variant + ")");

            // The API below is available for Android 4.4 or newer.

            // An app may set the PIN here or set pairing confirmation (depending on the variant) using:
            // device.setPin(new byte[] { '1', '2', '3', '4', '5', '6' });
            // device.setPairingConfirmation(true);
        }
    };

    public BleManager(final Context context) {
        mContext = context;
        mHandler = new Handler();
        mUserDisconnected = false;

        // Register bonding broadcast receiver
        context.registerReceiver(mBondingBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        context.registerReceiver(mPairingRequestBroadcastReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST"/*BluetoothDevice.ACTION_PAIRING_REQUEST*/));
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract BleManagerGattCallback getGattCallback();

    protected boolean shouldAutoConnect() {
        return false;
    }

    public void connect(final BluetoothDevice device) {
        DebugLogger.d(TAG, "BleManager connect!");
        if (mConnected) {
            DebugLogger.d(TAG, "BleManager is  already connected!!!");
            return;
        }

        if (mBluetoothGatt != null) {
            DebugLogger.d(TAG, "mBluetoothGatt is not null and gatt.close()");
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        final boolean autoConnect = shouldAutoConnect();
        mUserDisconnected = !autoConnect; // We will receive Linkloss events only when the device is connected with autoConnect=true
        DebugLogger.v(TAG, "Connecting...");
        DebugLogger.d(TAG, "gatt = device.connectGatt(autoConnect = " + autoConnect + ")");
        
        new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				try {
					mBluetoothGatt = device.connectGatt(mContext, false, getGattCallback());
                    DebugLogger.d(TAG, "BleManager Gatt Connect!!!!!!!!!!!!!!!!!!!!!!!!");
				} catch (Exception e) {
					DebugLogger.d(TAG, "BleManager Gatt connect exception and gatt.close()");
					DebugLogger.d(TAG, "gatt.close()");
					mBluetoothGatt.close();
					mBluetoothGatt = null;
				}
			}
		});
    }

    public boolean disconnect() {
        DebugLogger.d(TAG, "BleManager disconnect");
        mUserDisconnected = true;

        if (mConnected && mBluetoothGatt != null) {
            DebugLogger.v(TAG, "Disconnecting...");
            mCallbacks.onDeviceDisconnecting();
            DebugLogger.d(TAG, "gatt.disconnect()");
            mBluetoothGatt.disconnect();
            return true;
        }
        DebugLogger.d(TAG, "BleManager disconnect fail mConnected =" + mConnected);
        return false;
    }

    public void close() {
        DebugLogger.d(TAG, "Gatt close!!!!!!!!!!!!!!!!!!!!!!!!");
        try {
            mContext.unregisterReceiver(mBondingBroadcastReceiver);
            mContext.unregisterReceiver(mPairingRequestBroadcastReceiver);
        } catch (Exception e) {
            // the receiver must have been not registered or unregistered before
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mUserDisconnected = false;
    }


    public void setGattCallbacks(E callbacks) {
        mCallbacks = callbacks;
    }

    private boolean isServiceChangedCCCD(final BluetoothGattDescriptor descriptor) {
        if (descriptor == null)
            return false;

        return SERVICE_CHANGED_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid());
    }

    private boolean isBatteryLevelCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (characteristic == null)
            return false;

        return BATTERY_LEVEL_CHARACTERISTIC.equals(characteristic.getUuid());
    }

    private boolean isBatteryLevelCCCD(final BluetoothGattDescriptor descriptor) {
        if (descriptor == null)
            return false;

        return BATTERY_LEVEL_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid());
    }

    private boolean ensureServiceChangedEnabled(final BluetoothGatt gatt) {
        DebugLogger.d(TAG, "ensureServiceChangedEnabled ");
        if (gatt == null) {
            DebugLogger.d(TAG, "ensureServiceChangedEnabled  gatt = null!");
            return false;
        }

        final BluetoothDevice device = gatt.getDevice();
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            DebugLogger.d(TAG, "device.getBondState() not BluetoothDevice.BOND_BONDED!");
            return false;
        }

        final BluetoothGattService gaService = gatt.getService(GENERIC_ATTRIBUTE_SERVICE);
        if (gaService == null) {
            DebugLogger.d(TAG, "ensureServiceChangedEnabled  gatt = null!");
            return false;
        }

        final BluetoothGattCharacteristic scCharacteristic = gaService.getCharacteristic(SERVICE_CHANGED_CHARACTERISTIC);
        if (scCharacteristic == null) {
            return false;
        }

        DebugLogger.i(TAG, "Service Changed characteristic found on a bonded device");
        return enableIndications(scCharacteristic);
    }

    protected final boolean enableNotifications(final BluetoothGattCharacteristic characteristic) {
        DebugLogger.d(TAG, "enableNotifications ");
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null) {
            DebugLogger.d(TAG, "enableNotifications gatt or characteristic is null");
            return false;
        }

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            DebugLogger.d(TAG, "enableNotifications properties & BluetoothGattCharacteristic.PROPERTY_INDICATE == 0");
            return false;
        }

        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            DebugLogger.d(TAG, "enableNotifications descriptor not null");
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            DebugLogger.v(TAG, "Enabling notifications for " + characteristic.getUuid());
            DebugLogger.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x01-00)");
            return gatt.writeDescriptor(descriptor);
        }else {
            DebugLogger.d(TAG, "enableNotifications descriptor is null");
        }

        return false;
    }

    protected final boolean enableIndications(final BluetoothGattCharacteristic characteristic) {
        DebugLogger.d(TAG, "enableIndications ");
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null) {
            DebugLogger.d(TAG, "enableIndications gatt or characteristic is null");
            return false;
        }

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0) {
            DebugLogger.d(TAG, "enableIndications properties & BluetoothGattCharacteristic.PROPERTY_INDICATE == 0");
            return false;
        }

        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            DebugLogger.d(TAG, "enableIndications descriptor not null");
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            DebugLogger.v(TAG, "Enabling indications for " + characteristic.getUuid());
            DebugLogger.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x02-00)");
            return gatt.writeDescriptor(descriptor);
        }else {
            DebugLogger.d(TAG, "enableIndications descriptor is null");
        }
        return false;
    }

    protected final boolean readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0)
            return false;

        DebugLogger.v(TAG, "Reading characteristic " + characteristic.getUuid());
        DebugLogger.d(TAG, "gatt.readCharacteristic(" + characteristic.getUuid() + ")");
        return gatt.readCharacteristic(characteristic);
    }

    protected final boolean writeCharacteristic(final BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0)
            return false;

        DebugLogger.v(TAG, "Writing characteristic " + characteristic.getUuid());
        DebugLogger.d(TAG, "gatt.writeCharacteristic(" + characteristic.getUuid() + ")");
        return gatt.writeCharacteristic(characteristic);
    }

    public final boolean readBatteryLevel() {
        DebugLogger.d(TAG, "readBatteryLevel");
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null) {
            DebugLogger.d(TAG, "readBatteryLevel gatt is null");
            return false;
        }

        final BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE);
        if (batteryService == null) {
            DebugLogger.d(TAG, "readBatteryLevel batteryService null");
            return false;
        }

        final BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
        if (batteryLevelCharacteristic == null) {
            DebugLogger.d(TAG, "readBatteryLevel batteryLevelCharacteristic null");
            return false;
        }


        // Check characteristic property
        final int properties = batteryLevelCharacteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
            DebugLogger.d(TAG, "readBatteryLevel properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0");
            return setBatteryNotifications(true);
        }

        DebugLogger.d(TAG, "Reading battery level...");
        return readCharacteristic(batteryLevelCharacteristic);
    }

    public boolean setBatteryNotifications(final boolean enable) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null) {
            DebugLogger.d(TAG, "setBatteryNotifications gatt is null");
            return false;
        }

        final BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE);
        if (batteryService == null) {
            DebugLogger.d(TAG, "setBatteryNotifications batteryService is null");
            return false;
        }

        final BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
        if (batteryLevelCharacteristic == null) {
            DebugLogger.d(TAG, "setBatteryNotifications batteryLevelCharacteristic is null");
            return false;
        }

        // Check characteristic property
        final int properties = batteryLevelCharacteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            DebugLogger.d(TAG, "setBatteryNotifications properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0");
            return false;
        }

        gatt.setCharacteristicNotification(batteryLevelCharacteristic, enable);
        final BluetoothGattDescriptor descriptor = batteryLevelCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            DebugLogger.d(TAG, "setBatteryNotifications descriptor not null and enable = " + enable);
            if (enable) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                DebugLogger.d(TAG, "Enabling battery level notifications...");
                DebugLogger.v(TAG, "Enabling notifications for " + BATTERY_LEVEL_CHARACTERISTIC);
                DebugLogger.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x01-00)");
            } else {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                DebugLogger.d(TAG, "Disabling battery level notifications...");
                DebugLogger.v(TAG, "Disabling notifications for " + BATTERY_LEVEL_CHARACTERISTIC);
                DebugLogger.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x00-00)");
            }
            return gatt.writeDescriptor(descriptor);
        }else {
            DebugLogger.d(TAG, "setBatteryNotifications descriptor is null");
        }
        return false;
    }

    protected static final class Request {
        private enum Type {
            WRITE,
            READ,
            ENABLE_NOTIFICATIONS,
            ENABLE_INDICATIONS
        }

        private final Type type;
        private final BluetoothGattCharacteristic characteristic;
        private final byte[] value;

        private Request(final Type type, final BluetoothGattCharacteristic characteristic) {
            this.type = type;
            this.characteristic = characteristic;
            this.value = null;
        }

        private Request(final Type type, final BluetoothGattCharacteristic characteristic, final byte[] value) {
            this.type = type;
            this.characteristic = characteristic;
            this.value = value;
        }

        public static Request newReadRequest(final BluetoothGattCharacteristic characteristic) {
            return new Request(Type.READ, characteristic);
        }

        public static Request newWriteRequest(final BluetoothGattCharacteristic characteristic, final byte[] value) {
            return new Request(Type.WRITE, characteristic, value);
        }

        public static Request newEnableNotificationsRequest(final BluetoothGattCharacteristic characteristic) {
            return new Request(Type.ENABLE_NOTIFICATIONS, characteristic);
        }

        public static Request newEnableIndicationsRequest(final BluetoothGattCharacteristic characteristic) {
            return new Request(Type.ENABLE_INDICATIONS, characteristic);
        }
    }

    protected abstract class BleManagerGattCallback extends BluetoothGattCallback {
        private Queue<Request> mInitQueue;
        private boolean mInitInProgress;

        protected abstract boolean isRequiredServiceSupported(final BluetoothGatt gatt);

        protected boolean isOptionalServiceSupported(final BluetoothGatt gatt) {
            return false;
        }

        protected abstract Queue<Request> initGatt(final BluetoothGatt gatt);

        protected void onDeviceReady() {
            mCallbacks.onDeviceReady();
        }

        protected abstract void onDeviceDisconnected();

        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // do nothing
        }

        protected void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // do nothing
        }

        protected void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // do nothing
        }

        protected void onCharacteristicIndicated(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // do nothing
        }

        private void onError(final String message, final int errorCode) {
            DebugLogger.e(TAG, "Error (0x" + Integer.toHexString(errorCode) + "): " + GattError.parse(errorCode));
            mCallbacks.onError(message, errorCode);
        }

        @Override
        public final void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            DebugLogger.d(TAG, "[Callback] Connection state changed with status: " + status + " and new state: " + newState + " (" + stateToString(newState) + ")");

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                // Notify the parent activity/service
                DebugLogger.i(TAG, "Connected to " + gatt.getDevice().getAddress());
                mConnected = true;
                mCallbacks.onDeviceConnected();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Some proximity tags (e.g. nRF PROXIMITY) initialize bonding automatically when connected.
                        if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDING) {
                            DebugLogger.v(TAG, "Discovering Services...");
                            DebugLogger.d(TAG, "gatt.discoverServices()");
                            gatt.discoverServices();
                        }
                    }
                }, 600);
            } else {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (status != BluetoothGatt.GATT_SUCCESS)
                        DebugLogger.w(TAG, "Error: (0x" + Integer.toHexString(status) + "): " + GattError.parseConnectionError(status));

                    onDeviceDisconnected();
                    mConnected = false;
                    if (mUserDisconnected) {
                        DebugLogger.i(TAG, "onConnectionStateChange Disconnected");
                        mCallbacks.onDeviceDisconnected();
                        close();
                    } else {
                        DebugLogger.w(TAG, "onConnectionStateChange Connection lost");
                        mCallbacks.onLinklossOccur();
                    }
                    return;
                }

                DebugLogger.e(TAG, "Error (0x" + Integer.toHexString(status) + "): " + GattError.parseConnectionError(status));
                mCallbacks.onError(ERROR_CONNECTION_STATE_CHANGE, status);
            }
        }

        @Override
        public final void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                DebugLogger.i(TAG, "Services Discovered");
                if (isRequiredServiceSupported(gatt)) {
                    DebugLogger.v(TAG, "Primary service found");
                    final boolean optionalServicesFound = isOptionalServiceSupported(gatt);
                    if (optionalServicesFound)
                        DebugLogger.v(TAG, "Secondary service found");

                    mCallbacks.onServicesDiscovered(optionalServicesFound);

                    mInitInProgress = true;
                    mInitQueue = initGatt(gatt);

                    if (ensureServiceChangedEnabled(gatt)) {
                        DebugLogger.v(TAG, "onServicesDiscovered ensureServiceChangedEnabled is false & return");
                        return;
                    }

                    DebugLogger.v(TAG, "onServicesDiscovered readBatteryLevel");
                    if (!readBatteryLevel()) {
                        DebugLogger.v(TAG, "onServicesDiscovered readBatteryLevel return false");
                        nextRequest();
                    }else {
                        DebugLogger.v(TAG, "onServicesDiscovered readBatteryLevel return true");
                    }

                } else {
                    DebugLogger.w(TAG, "Device is not supported");
                    mCallbacks.onDeviceNotSupported();
                    disconnect();
                }
            } else {
                DebugLogger.e(TAG, "onServicesDiscovered error " + status);
                onError(ERROR_DISCOVERY_SERVICE, status);
            }
        }

        @Override
        public final void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            DebugLogger.e(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                DebugLogger.i(TAG, "Read Response received from " + characteristic.getUuid() + ", value: " + ParserUtils.parse(characteristic));

                if (isBatteryLevelCharacteristic(characteristic)) {
                    final int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    DebugLogger.d(TAG, "Battery level received: " + batteryValue + "%");
                    mCallbacks.onBatteryValueReceived(batteryValue);

                    if (!setBatteryNotifications(true))
                        nextRequest();
                } else {
                    // The value has been read. Notify the manager and proceed with the initialization queue.
                    onCharacteristicRead(gatt, characteristic);
                    nextRequest();
                }
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    DebugLogger.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                DebugLogger.e(TAG, "onCharacteristicRead error " + status);
                onError(ERROR_READ_CHARACTERISTIC, status);
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                DebugLogger.i(TAG, "Data written to " + characteristic.getUuid() + ", value: " + ParserUtils.parse(characteristic.getValue()));
                // The value has been written. Notify the manager and proceed with the initialization queue.
                onCharacteristicWrite(gatt, characteristic);
                nextRequest();
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    DebugLogger.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                DebugLogger.e(TAG, "onCharacteristicRead error " + status);
                onError(ERROR_READ_CHARACTERISTIC, status);
            }
        }

        @Override
        public final void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                DebugLogger.i(TAG, "Data written to descr. " + descriptor.getUuid() + ", value: " + ParserUtils.parse(descriptor));

                if (isServiceChangedCCCD(descriptor)) {
                    DebugLogger.d(TAG, "Service Changed notifications enabled");
                    if (!readBatteryLevel())
                        nextRequest();
                } else if (isBatteryLevelCCCD(descriptor)) {
                    final byte[] value = descriptor.getValue();
                    if (value != null && value.length > 0 && value[0] == 0x01) {
                        DebugLogger.d(TAG, "Battery Level notifications enabled");
                        nextRequest();
                    } else
                        DebugLogger.d(TAG, "Battery Level notifications disabled");
                } else {
                    nextRequest();
                }
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    DebugLogger.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                DebugLogger.e(TAG, "onDescriptorWrite error " + status);
                onError(ERROR_WRITE_DESCRIPTOR, status);
            }
        }

        @Override
        public final void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            DebugLogger.v(TAG, "onCharacteristicChanged");
            final String data = ParserUtils.parse(characteristic);
//            DebugLogger.v(TAG, "onCharacteristicChanged data = " + data);

            if (isBatteryLevelCharacteristic(characteristic)) {
                DebugLogger.i(TAG, "Notification received from " + characteristic.getUuid() + ", value: " + data);
                final int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                DebugLogger.d(TAG, "Battery level received: " + batteryValue + "%");
                mCallbacks.onBatteryValueReceived(batteryValue);
            } else {
                final BluetoothGattDescriptor cccd = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
                final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;

                DebugLogger.v(TAG, "onCharacteristicChanged notifications = " + notifications);
                if (notifications) {
                    //DebugLogger.i(TAG, "Notification received from " + characteristic.getUuid() + ", value: " + data);
                    onCharacteristicNotified(gatt, characteristic);
                } else { // indications
                    //DebugLogger.i(TAG, "Indication received from " + characteristic.getUuid() + ", value: " + data);
                    onCharacteristicIndicated(gatt, characteristic);
                }
            }
        }

        private void nextRequest() {
            DebugLogger.v(TAG, "nextRequest");
            final Queue<Request> requests = mInitQueue;

            // Get the first request from the queue
            final Request request = requests.poll();

            // Are we done?
            if (request == null) {
                if (mInitInProgress) {
                    mInitInProgress = false;
                    DebugLogger.v(TAG, "nextRequest onDeviceReady");
                    onDeviceReady();
                }
                return;
            }

            switch (request.type) {
                case READ: {
                    DebugLogger.v(TAG, "nextRequest request.type is READ");
                    readCharacteristic(request.characteristic);
                    break;
                }
                case WRITE: {
                    DebugLogger.v(TAG, "nextRequest request.type is WRITE");
                    final BluetoothGattCharacteristic characteristic = request.characteristic;
                    characteristic.setValue(request.value);
                    writeCharacteristic(characteristic);
                    break;
                }
                case ENABLE_NOTIFICATIONS: {
                    DebugLogger.v(TAG, "nextRequest request.type is ENABLE_NOTIFICATIONS");
                    enableNotifications(request.characteristic);
                    break;
                }
                case ENABLE_INDICATIONS: {
                    DebugLogger.v(TAG, "nextRequest request.type is ENABLE_INDICATIONS");
                    enableIndications(request.characteristic);
                    break;
                }
            }
        }

        private String stateToString(final int state) {
            switch (state) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "CONNECTED";
                case BluetoothProfile.STATE_CONNECTING:
                    return "CONNECTING";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "DISCONNECTING";
                default:
                    return "DISCONNECTED";
            }
        }
    }

    private static final int PAIRING_VARIANT_PIN = 0;
    private static final int PAIRING_VARIANT_PASSKEY = 1;
    private static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
    private static final int PAIRING_VARIANT_CONSENT = 3;
    private static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    private static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    private static final int PAIRING_VARIANT_OOB_CONSENT = 6;

    private String pairingVariantToString(final int variant) {
        switch (variant) {
            case PAIRING_VARIANT_PIN:
                return "PAIRING_VARIANT_PIN";
            case PAIRING_VARIANT_PASSKEY:
                return "PAIRING_VARIANT_PASSKEY";
            case PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                return "PAIRING_VARIANT_PASSKEY_CONFIRMATION";
            case PAIRING_VARIANT_CONSENT:
                return "PAIRING_VARIANT_CONSENT";
            case PAIRING_VARIANT_DISPLAY_PASSKEY:
                return "PAIRING_VARIANT_DISPLAY_PASSKEY";
            case PAIRING_VARIANT_DISPLAY_PIN:
                return "PAIRING_VARIANT_DISPLAY_PIN";
            case PAIRING_VARIANT_OOB_CONSENT:
                return "PAIRING_VARIANT_OOB_CONSENT";
            default:
                return "UNKNOWN";
        }
    }

    private String bondStateToString(final int state) {
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            default:
                return "UNKNOWN";
        }
    }
}
