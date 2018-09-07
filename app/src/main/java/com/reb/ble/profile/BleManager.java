/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package com.reb.ble.profile;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public interface BleManager {

	/**
	 * Connects to the Bluetooth Smart device
	 * 
	 * @param context
	 *            this must be an application context, not the Activity. Call {@link Activity#getApplicationContext()} to get one.
	 * @param device
	 *            a device to connect to
	 */
	public boolean connect(final Context context, final BluetoothDevice device);
	
	/**
	 * Connects to the Bluetooth Smart device
	 * 
	 * @param context
	 * 				this must be an application context, not the Activity. Call {@link Activity#getApplicationContext()} to get one.
	 * @param address
	 * 				the macAddress of a device to connect to
	 */
	public boolean connect(final Context context, final String address);

	/**
	 * Disconnects from the device. Does nothing if not connected.
	 */
	public void disconnect();

	/**
	 * Sets the manager callback listener
	 * 
	 * @param callbacks
	 *            the callback listener
	 */
	public void setGattCallbacks(BleManagerCallbacks callbacks);

	/**
	 * Closes and releases resources. May be also used to unregister broadcast listeners.
	 */
	public void closeBluetoothGatt();
	
	/**
	 * Write data.
	 */
	public boolean write(byte[] data);
	
	
}
