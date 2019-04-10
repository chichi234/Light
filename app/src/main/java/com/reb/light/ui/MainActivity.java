package com.reb.light.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.reb.ble.profile.BleCore;
import com.reb.ble.profile.BleManagerCallbacks;
import com.reb.ble.ui.ExtendedBluetoothDevice;
import com.reb.ble.util.BluetoothUtil;
import com.reb.ble.util.DebugLog;
import com.reb.light.R;
import com.reb.light.ui.frag.ConnectingFragment;
import com.reb.light.ui.frag.LightFragment;
import com.reb.light.ui.frag.ScannerFragment;
import com.reb.light.util.UIUtil;

import java.util.Arrays;

public class MainActivity extends BaseFragmentActivity implements ScannerFragment.OnDeviceSelectedListener, BleManagerCallbacks {

    private ScannerFragment mScannerFragment;
    private ConnectingFragment mConnectingFragment;
    private LightFragment mLightFragment;

    private static final int MSG_CONNECT_SUCCESS = 0x10001;
    private static final int MSG_DISCONNECT = 0x10002;
    private static final int MSG_ERROR = 0x10003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIUtil.setStatusBarColor(Color.WHITE, this);
        BleCore.getInstances().setGattCallbacks(this);
        initFragment(savedInstanceState);
        DebugLog.i(getResources().getDisplayMetrics().density + ":dpi");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        BleCore.getInstances().disconnect();
    }

    private void initFragment(Bundle savedInstanceState) {
        String currentFragTag = null;
        if (savedInstanceState != null) {
            currentFragTag = savedInstanceState.getString("mCurrentFragTag");
            FragmentManager fm = getSupportFragmentManager();
            mScannerFragment = (ScannerFragment) fm.findFragmentByTag(ScannerFragment.class.getSimpleName());
            mConnectingFragment = (ConnectingFragment) fm.findFragmentByTag(ConnectingFragment.class.getSimpleName());
            mLightFragment = (LightFragment) fm.findFragmentByTag(LightFragment.class.getSimpleName());
        }
        if (mScannerFragment == null)
            mScannerFragment = new ScannerFragment();
        mScannerFragment.setOnDeviceSelectedListener(this);
        if (mConnectingFragment == null)
            mConnectingFragment = new ConnectingFragment();
        if (mLightFragment == null)
            mLightFragment = new LightFragment();
        if (ScannerFragment.class.getSimpleName().equals(currentFragTag)) {
            changeFragment(mScannerFragment);
        } else if (ConnectingFragment.class.getSimpleName().equals(currentFragTag)) {
            changeFragment(mConnectingFragment);
        } else if (LightFragment.class.getSimpleName().equals(currentFragTag)) {
            changeFragment(mLightFragment);
        } else {
            changeFragment(mScannerFragment);
        }
    }

    @Override
    public void onDeviceSelect(ExtendedBluetoothDevice device) {
        if (BleCore.getInstances().connect(getApplicationContext(), device.device)) {
            // 尝试连接成功，转到正在连接界面
            changeFragment(mConnectingFragment);
        }
    }

    @Override
    public void onDeviceConnected() {

    }

    @Override
    public void onDeviceDisconnected() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DISCONNECT);
        }
    }

    @Override
    public void onServicesDiscovered() {

    }

    @Override
    public void onNotifyEnable() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
        }
    }

    @Override
    public void onLinklossOccur(String macAddress) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DISCONNECT);
        }
    }

    @Override
    public void onDeviceNotSupported() {

    }

    @Override
    public void onWriteSuccess(byte[] data, boolean success) {
        DebugLog.i(Arrays.toString(data) + "success:" + success);
        if (mCurrentFrag == mLightFragment) {
            mLightFragment.onWriteSuccess(data, success);
        }
    }

    @Override
    public void onRecive(byte[] data) {
        DebugLog.i(Arrays.toString(data));
    }

    @Override
    public void onReadRssi(int rssi) {

    }

    @Override
    public void onError(String msg, int code) {
        DebugLog.e("code:" + code + ",msg:" + msg);
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_ERROR, code, 0, msg));
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS:
                    changeFragment(mLightFragment);
                    break;
                case MSG_DISCONNECT:
                    if (mCurrentFrag == mLightFragment) {
                        mLightFragment.toggleLight(false);
                        Toast.makeText(MainActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
                    } else if (mCurrentFrag == mConnectingFragment) {
                        Toast.makeText(MainActivity.this, R.string.alert_connect_failed, Toast.LENGTH_SHORT).show();
                        changeFragment(mScannerFragment);
                    }
                    break;
                case MSG_ERROR:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
//                    if (msg.arg1 == 133) {
//                        BleCore.getInstances().closeBluetoothGatt();
//                        BluetoothUtil.reopenBt(MainActivity.this);
//                    }
                    changeFragment(mScannerFragment);
                    break;
            }
        }
    };
}
