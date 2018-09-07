package com.reb.ble.scanner;

import java.util.UUID;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-5 16:57
 * @package_name com.reb.light.ble.scanner
 * @project_name Light
 * @history At 2018-9-5 16:57 created by Reb
 */
public class ScanFilter {
    private String mDeviceName;
    private String mDeviceAddress;
    private UUID mServiceUuid;

    public ScanFilter(String mDeviceName, String mDeviceAddress, UUID mServiceUuid) {
        this.mDeviceName = mDeviceName;
        this.mDeviceAddress = mDeviceAddress;
        this.mServiceUuid = mServiceUuid;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public UUID getServiceUuid() {
        return mServiceUuid;
    }
}
