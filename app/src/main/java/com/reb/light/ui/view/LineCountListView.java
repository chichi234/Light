package com.reb.light.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.reb.ble.util.DebugLog;

/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2018-9-10 11:30
 * @package_name com.reb.light.ui
 * @project_name Light
 * @history At 2018-9-10 11:30 created by Reb
 */
public class LineCountListView extends ListView {
    private int mLineCount;
    private int mItemHeight;

    public LineCountListView(Context context) {
        this(context, null);
    }

    public LineCountListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineCountListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxLineCount(int line) {
        this.mLineCount = line;
        post(new Runnable() {
            @Override
            public void run() {
                View child = getChildAt(0);
                if (child != null && child.getMeasuredHeight() > 0) {
                    mItemHeight = child.getMeasuredHeight();
                    requestLayout();
                } else {
                    post(this);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = mItemHeight * mLineCount;
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
