package com.reb.light.ui.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.reb.ble.profile.BleCore;
import com.reb.ble.profile.utility.BleConfiguration;
import com.reb.light.R;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-10 10:42
 * @package_name com.reb.light.ui.frag
 * @project_name Light
 * @history At 2018-9-10 10:42 created by Reb
 */
public class LightFragment extends BaseFragment {

    private ImageButton mToggleBtn;
    private boolean mIsTurnOn = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToggleBtn.setImageResource(msg.what);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.frag_light, null);
        mToggleBtn = mRootView.findViewById(R.id.light_toggle);
        mToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsTurnOn) {
                    BleCore.getInstances().sendData(new byte[]{(byte)0xA0, 0x01, 0x00, (byte)0xA1});
                } else {
                    BleCore.getInstances().sendData(new byte[]{(byte)0xA0, 0x01, 0x01, (byte)0xA2});
                }
            }
        });
//        mToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    BleCore.getInstances().sendData(new byte[]{(byte)0xA0, 0x01, 0x01, (byte)0xA2});
//                } else {
//                    BleCore.getInstances().sendData(new byte[]{(byte)0xA0, 0x01, 0x00, (byte)0xA1});
//                }
//            }
//        });
        toggleLight(true);
        return mRootView;
    }

    public void toggleLight(boolean open) {
//        mToggleBtn.setChecked(open);
        if (mIsTurnOn != open) {
            mIsTurnOn = !open;
            mToggleBtn.performClick();
        }
    }

    public void onWriteSuccess(byte[] data, boolean success) {
        if (success) {
            if (data[2] == 0x00) {
                mHandler.sendEmptyMessage(R.drawable.light_off);
                mIsTurnOn = false;
            } else if (data[2] == 0x01) {
                mIsTurnOn = true;
                mHandler.sendEmptyMessage(R.drawable.light_on);
            }
        }
    }
}
