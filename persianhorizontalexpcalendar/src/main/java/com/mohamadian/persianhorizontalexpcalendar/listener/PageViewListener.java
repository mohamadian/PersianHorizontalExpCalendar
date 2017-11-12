package com.mohamadian.persianhorizontalexpcalendar.listener;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;

import org.joda.time.DateTime;

/**
 * Created by Tasnim on 11/12/2017.
 */

public interface PageViewListener {
    PersianHorizontalExpCalendar onDayClick(DateTime dateTime);
}
