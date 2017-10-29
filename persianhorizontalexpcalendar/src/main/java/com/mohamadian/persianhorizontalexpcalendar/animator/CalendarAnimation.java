package com.mohamadian.persianhorizontalexpcalendar.animator;

import android.animation.ValueAnimator;

import com.mohamadian.persianhorizontalexpcalendar.listener.SmallAnimationListener;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class CalendarAnimation extends ValueAnimator {
    public void setListener(SmallAnimationListener smallAnimationListener) {
        addUpdateListener(smallAnimationListener);
        addListener(smallAnimationListener);
        start();
    }
}

