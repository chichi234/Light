package com.reb.ble.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.reb.ble.profile.BleCore;
import com.reb.light.R;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-6 10:27
 * @package_name com.reb.ble.ui
 * @project_name Light
 * @history At 2018-9-6 10:27 created by Reb
 */
public class DeviceListFragment extends BaseScannerFragment {
    private DeviceAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.frag_devices, null);
        ListView deviceList = mRootView.findViewById(R.id.devices_frag);
        mAdapter = new DeviceAdapter(getContext());
        deviceList.setAdapter(mAdapter);
        deviceList.setOnItemClickListener(mOnItemClickListener);
        return mRootView;
    }

    @Override
    public void startScan() {
        if (mAdapter != null) {
            mAdapter.clearDevices();
        }
        super.startScan();
    }

    @Override
    public void onScanResult(int callbackType, ExtendedBluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mAdapter != null) {
            mAdapter.addOrUpdateDevice(device);
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            BleCore.getInstances().connect(getContext().getApplicationContext(), );
        }
    };
}
