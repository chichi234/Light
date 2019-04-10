package com.reb.light.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.reb.light.R;
import com.reb.light.ui.frag.BaseFragment;


/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-1-11 18:48
 * @package_name com.reb.dsd_ble.ui.act
 * @project_name DSD_BLE
 * @history At 2018-1-11 18:48 created by Reb
 */

public class BaseFragmentActivity extends AppCompatActivity {

    protected BaseFragment mCurrentFrag;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentFrag != null) {
            outState.putString("mCurrentFragTag", mCurrentFrag.getClass().getSimpleName());
        }
    }

    protected void changeFragment(BaseFragment target) {
        if (target != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            if (mCurrentFrag != null) {
                fragmentTransaction.hide(mCurrentFrag);
            }
            if (target.isAdded()) {
                fragmentTransaction.show(target);
            } else {
                fragmentTransaction.add(R.id.container, target, target.getClass().getSimpleName());
            }
            mCurrentFrag = target;
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

}
