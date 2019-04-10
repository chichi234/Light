package com.reb.light.ui.frag;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.reb.ble.ui.BaseScannerFragment;
import com.reb.ble.ui.ExtendedBluetoothDevice;
import com.reb.light.R;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-10 9:30
 * @package_name com.reb.light.ui
 * @project_name Light
 * @history At 2018-9-10 9:30 created by Reb
 */
public class ScannerFragment extends BaseFragment implements BaseScannerFragment.BleScanListener {

    private Button mScanBtn;
    private BaseScannerFragment mScannerFragment;
    private OnDeviceSelectedListener mOnDeviceSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.frag_scanner, null);
        mScanBtn = mRootView.findViewById(R.id.scan_button);
        mScannerFragment = (BaseScannerFragment) getChildFragmentManager().findFragmentById(R.id.devices_frag);
        mScannerFragment.setBleScanStateListener(this);
        mScanBtn.post(new Runnable() {
            @Override
            public void run() {
                if (ScannerFragment.this.mScannerFragment.isScanning())
                    mScanBtn.setText(R.string.scanning);
                else
                    mScanBtn.setText(R.string.scan);
            }
        });
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        return mRootView;
    }

    public void scan() {
        if (this.mScannerFragment.isScanning()) {
            this.mScannerFragment.stopScan();
        } else {
            this.mScannerFragment.startScan();
        }
    }


    @Override
    public void onScanStart() {
        mScanBtn.setText(R.string.scanning);
    }

    @Override
    public void onScanStop() {
        mScanBtn.setText(R.string.scan);
    }

    @Override
    public void onDeviceSelect(ExtendedBluetoothDevice device) {
        if (this.mOnDeviceSelectedListener != null) {
            mOnDeviceSelectedListener.onDeviceSelect(device);
        }
    }

    public void setOnDeviceSelectedListener(OnDeviceSelectedListener deviceSelectedListener) {
        this.mOnDeviceSelectedListener = deviceSelectedListener;
    }

    public interface OnDeviceSelectedListener {
        void onDeviceSelect(ExtendedBluetoothDevice device);
    }
}
