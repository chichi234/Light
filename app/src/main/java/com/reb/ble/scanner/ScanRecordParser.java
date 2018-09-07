/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 *
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package com.reb.ble.scanner;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.util.Log;

import com.reb.ble.profile.utility.BleConfiguration;
import com.reb.ble.ui.ExtendedBluetoothDevice;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * ScannerServiceParser is responsible to parse scanning data and it check if scanned device has required service in it.
 */
public class ScanRecordParser {
    private static final String TAG = "ScannerServiceParser";

    private static final int FLAGS_BIT = 0x01;
    private static final int SERVICES_MORE_AVAILABLE_16_BIT = 0x02;
    private static final int SERVICES_COMPLETE_LIST_16_BIT = 0x03;
    private static final int SERVICES_MORE_AVAILABLE_32_BIT = 0x04;
    private static final int SERVICES_COMPLETE_LIST_32_BIT = 0x05;
    private static final int SERVICES_MORE_AVAILABLE_128_BIT = 0x06;
    private static final int SERVICES_COMPLETE_LIST_128_BIT = 0x07;
    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;

    private static final byte LE_LIMITED_DISCOVERABLE_MODE = 0x01;
    private static final byte LE_GENERAL_DISCOVERABLE_MODE = 0x02;
    private static final byte SUPPORT_BR_EDR_MODE = 0x04;

    /**
     * Checks if device is connectable (as Android cannot get this information directly we just check if it has GENERAL DISCOVERABLE or LIMITED DISCOVERABLE flag set) and has required service UUID in
     * the advertising packet. The service UUID may be <code>null</code>.
     * <p>
     * For further details on parsing BLE advertisement packet data see https://developer.bluetooth.org/Pages/default.aspx Bluetooth Core Specifications Volume 3, Part C, and Section 8
     * </p>
     */
    public static boolean decodeDeviceAdvData(byte[] data, UUID requiredUUID) {
        final String uuid = requiredUUID != null ? requiredUUID.toString() : null;
        if (data != null) {
            boolean connectable = false;
            boolean valid = uuid == null;
            int fieldLength, fieldName;
            int packetLength = data.length;
            for (int index = 0; index < packetLength; index++) {
                fieldLength = data[index];
                if (fieldLength == 0) {
                    return connectable && valid;
                }
                fieldName = data[++index];

                if (uuid != null) {
                    if (fieldName == SERVICES_MORE_AVAILABLE_16_BIT || fieldName == SERVICES_COMPLETE_LIST_16_BIT) {
                        for (int i = index + 1; i < index + fieldLength - 1; i += 2)
                            valid = valid || decodeService16BitUUID(uuid, data, i, 2);
                    } else if (fieldName == SERVICES_MORE_AVAILABLE_32_BIT || fieldName == SERVICES_COMPLETE_LIST_32_BIT) {
                        for (int i = index + 1; i < index + fieldLength - 1; i += 4)
                            valid = valid || decodeService32BitUUID(uuid, data, i, 4);
                    } else if (fieldName == SERVICES_MORE_AVAILABLE_128_BIT || fieldName == SERVICES_COMPLETE_LIST_128_BIT) {
                        for (int i = index + 1; i < index + fieldLength - 1; i += 16)
                            valid = valid || decodeService128BitUUID(uuid, data, i, 16);
                    }
                }
                if (fieldName == FLAGS_BIT) {
                    int flags = data[index + 1];
                    connectable = (flags & (LE_GENERAL_DISCOVERABLE_MODE | LE_LIMITED_DISCOVERABLE_MODE)) > 0;
                }
                index += fieldLength - 1;
            }
            return connectable && valid;
        }
        return false;
    }

    /**
     * Decodes the device name from Complete Local Name or Shortened Local Name field in Advertisement packet. Ususally if should be done by {@link BluetoothDevice#getName()} method but some phones
     * skips that, f.e. Sony Xperia Z1 (C6903) with Android 4.3 where getName() always returns <code>null</code>. In order to show the device name correctly we have to parse it manually :(
     */
    public static String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    /**
     * Decodes the local name
     */
    public static String decodeLocalName(final byte[] data, final int start, final int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to convert the complete local name to UTF-8", e);
            return null;
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Error when reading complete local name", e);
            return null;
        }
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService16BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition));
        Log.d(TAG, serviceUUID);
        serviceUUID = String.format("%04x", decodeUuid16(data, startPosition));
        Log.d(TAG, serviceUUID);
        String requiredUUID = uuid.substring(4, 8);
        Log.d(TAG, requiredUUID);
        return serviceUUID.equals(requiredUUID);
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService32BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition + serviceDataLength - 4));
        Log.d(TAG, serviceUUID);
//		serviceUUID = String.format("%04x", decodeUuid16(data, startPosition + serviceDataLength - 4));
//		Log.d(TAG, serviceUUID);
        String requiredUUID = uuid.substring(4, 8);

        return serviceUUID.equals(requiredUUID);
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService128BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition + serviceDataLength - 4));
        Log.d(TAG, serviceUUID);
//		serviceUUID = String.format("%04x", decodeUuid16(data, startPosition + serviceDataLength - 4));
//		Log.d(TAG, serviceUUID);
        String requiredUUID = uuid.substring(4, 8);
        return serviceUUID.equals(requiredUUID);
    }

    private static int decodeUuid16(final byte[] data, final int start) {
        final int b1 = data[start] & 0xff;
        final int b2 = data[start + 1] & 0xff;

        return (b2 << 8 | b1 << 0);
    }

    public static ExtendedBluetoothDevice decodeDeviceAdvData(byte[] data, BluetoothDevice device, int rssi) {
        ExtendedBluetoothDevice exDevice = null;
        boolean isValid = BleConfiguration.SERVICE_UUID_OF_SCAN_FILTER1 == null;
        String type = "";//
        String name = device.getName();
        if (isBeacon(data)) {
            type = "IBeacon";
        }
        if (data != null) {
            int fieldLength, fieldName;
            int packetLength = data.length;
            for (int index = 0; index < packetLength; index++) {
                fieldLength = data[index];
                if (fieldLength == 0) {
                    // 解析完毕
                    break;
                }
                fieldName = data[++index];
                switch (fieldName) {
                    case SERVICES_MORE_AVAILABLE_16_BIT:
                    case SERVICES_COMPLETE_LIST_16_BIT:
                    case SERVICES_MORE_AVAILABLE_32_BIT:
                    case SERVICES_COMPLETE_LIST_32_BIT:
                    case SERVICES_MORE_AVAILABLE_128_BIT:
                    case SERVICES_COMPLETE_LIST_128_BIT:
                        // 过滤服务
                        isValid |= decodeFilterService(fieldName, fieldLength, data, index);
                        break;
                    case FLAGS_BIT:
                        // 设备类型
                        type = decodeDeviceType(type, data[index + 1], data);
                        break;
                    case COMPLETE_LOCAL_NAME:
                    case SHORTENED_LOCAL_NAME:
                        name = decodeLocalName(data, index + 1, fieldLength - 1);
                        break;
                }
                index += fieldLength - 1;
            }
            if (!isValid) {
                // 服务过滤未通过
                exDevice = null;
            } else {
                if (TextUtils.isEmpty(type)) {
                    type = "<Unknown>";
                }
                if (TextUtils.isEmpty(name)) {
                    name = "<Unknown>";
                }
                exDevice = new ExtendedBluetoothDevice(device, name, rssi, type);
            }
        }
        return exDevice;
    }

    private static String decodeDeviceType(String beacon, byte flags, byte[] data) {
        StringBuilder type = new StringBuilder();
        if (!"IBeacon".equals(beacon)) {
            if ((flags & (LE_GENERAL_DISCOVERABLE_MODE | LE_LIMITED_DISCOVERABLE_MODE)) > 0) {
                if ((flags & SUPPORT_BR_EDR_MODE) > 0) {
                    // 不支持BR/EDR
                    type.append("ONLY BLE");
                } else {
                    // 支持BLE
                    type.append("BLE/SPP");
                }
            }
            return type.toString();
        } else {
            return "IBeacon";
        }
    }

    private static boolean isBeacon(byte[] scanRecord) {
        int startByte = 2;
        boolean patternFound = false;
        // 寻找ibeacon
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && // Identifies
                    // an
                    // iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { // Identifies
                // correct
                // data
                // length
                patternFound = true;
                break;
            }
            startByte++;
        }
        return patternFound;
    }

    ;

    private static boolean decodeFilterService(int fieldName, int fieldLength, byte[] data, int index) {
        boolean valid = false;
        if (BleConfiguration.SERVICE_UUID_OF_SCAN_FILTER1 != null) {
            String uuid = BleConfiguration.SERVICE_UUID_OF_SCAN_FILTER1.toString();
            if (fieldName == SERVICES_MORE_AVAILABLE_16_BIT || fieldName == SERVICES_COMPLETE_LIST_16_BIT) {
                for (int i = index + 1; i < index + fieldLength - 1; i += 2)
                    valid = valid || decodeService16BitUUID(uuid, data, i, 2);
            } else if (fieldName == SERVICES_MORE_AVAILABLE_32_BIT || fieldName == SERVICES_COMPLETE_LIST_32_BIT) {
                for (int i = index + 1; i < index + fieldLength - 1; i += 4)
                    valid = valid || decodeService32BitUUID(uuid, data, i, 4);
            } else if (fieldName == SERVICES_MORE_AVAILABLE_128_BIT || fieldName == SERVICES_COMPLETE_LIST_128_BIT) {
                for (int i = index + 1; i < index + fieldLength - 1; i += 16)
                    valid = valid || decodeService128BitUUID(uuid, data, i, 16);
            }
        }
        return valid;
    }

}
