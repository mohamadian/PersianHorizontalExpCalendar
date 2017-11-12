package com.mohamadian.persianhorizontalexpcalendar.listener;

import android.view.View;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;

import org.joda.time.DateTime;

/**
 * Created by Tasnim on 11/12/2017.
 */

public interface AnimationsListener {
    PersianHorizontalExpCalendar setHeightToCenterContainer(int height);

    PersianHorizontalExpCalendar setTopMarginToAnimContainer(int margin);

    PersianHorizontalExpCalendar setWeekPagerVisibility(int visibility);

    PersianHorizontalExpCalendar setMonthPagerVisibility(int visibility);

    PersianHorizontalExpCalendar setAnimatedContainerVisibility(int visibility);

    PersianHorizontalExpCalendar setMonthPagerAlpha(float alpha);

    PersianHorizontalExpCalendar setWeekPagerAlpha(float alpha);

    PersianHorizontalExpCalendar scrollToDate(DateTime dateTime, boolean scrollMonthPager, boolean scrollWeekPager, boolean animate);

    PersianHorizontalExpCalendar animateContainerAddView(View view);

    PersianHorizontalExpCalendar animateContainerRemoveViews();

    PersianHorizontalExpCalendar updateMarks();

    PersianHorizontalExpCalendar changeViewPager(PersianViewPagerType persianViewPagerType);
}
