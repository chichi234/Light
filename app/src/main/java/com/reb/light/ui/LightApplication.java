package com.reb.light.ui;

import android.app.Application;

import com.reb.ble.profile.utility.BleConfiguration;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-10 10:37
 * @package_name com.reb.light.ui
 * @project_name Light
 * @history At 2018-9-10 10:37 created by Reb
 */
public class LightApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BleConfiguration.init(this);
    }
}
