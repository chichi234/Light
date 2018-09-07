package com.reb.light.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.reb.ble.ui.BaseScannerFragment;
import com.reb.ble.util.DebugLog;
import com.reb.light.R;
import com.reb.light.util.UIUtil;

public class MainActivity extends AppCompatActivity implements BaseScannerFragment.BleScanStateListener {

    private Button mScanBtn;
    private BaseScannerFragment mScannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIUtil.setStatusBarColor(Color.WHITE, this);
        mScanBtn = findViewById(R.id.scan_button);
        mScannerFragment = (BaseScannerFragment) getSupportFragmentManager().findFragmentById(R.id.devices_frag);
        mScannerFragment.setBleScanStateListener(this);
        mScanBtn.post(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.mScannerFragment.isScanning())
                    mScanBtn.setText(R.string.scanning);
                else
                    mScanBtn.setText(R.string.scan);
            }
        });
    }

    public void scan(View view) {
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
}
