package com.reb.light.ui.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reb.light.R;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-10 10:23
 * @package_name com.reb.light.ui
 * @project_name Light
 * @history At 2018-9-10 10:23 created by Reb
 */
public class ConnectingFragment extends BaseFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.frag_wait, null);
        return mRootView;
    }
}
