package com.reb.ble.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reb.light.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class DeviceAdapter extends BaseAdapter {

    private List<ExtendedBluetoothDevice> devices = new ArrayList<>();
    private Context mContext;

    public DeviceAdapter(Context context) {
        this.mContext = context;
    }

    public void clearDevices() {
        devices.clear();
        notifyDataSetChanged();
    }

//    public void updateDeviceRssi(BluetoothDevice device, int rssi) {
//		comparator.address = device.getAddress();
//		final int indexInBonded = devices.indexOf(comparator);
//		if (indexInBonded >= 0) {
//			ExtendedBluetoothDevice previousDevice = devices.get(indexInBonded);
//			previousDevice.rssi = rssi;
//			notifyDataSetChanged();
//		}
//    }

    public void addOrUpdateDevice(ExtendedBluetoothDevice device) {
        final int indexInNotBonded = devices.indexOf(device);
        if (indexInNotBonded >= 0) {
            // update
            ExtendedBluetoothDevice previousDevice = devices.get(indexInNotBonded);
            previousDevice.rssi = device.rssi;
        } else {
            // add
            devices.add(device);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            vh = new ViewHolder();
            vh.mNameView = convertView.findViewById(R.id.item_device_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final ExtendedBluetoothDevice device = devices.get(i);
        String name = device.name;
        if ("Meyra Light".equals(name) || "DSD Relay".equals(name)) {
            name = "Meyra Light";
        } else {
            name = mContext.getString(R.string.unknown);
        }
//        if (TextUtils.isEmpty(device.name)) {
//            name = mContext.getString(R.string.unknown);
//        } else if ("DSD Relay".equals(name)) {
//            name = "Meyra Light";
//        }
        vh.mNameView.setText(name);
        return convertView;
    }

    private class ViewHolder {
        TextView mNameView;
    }
}
