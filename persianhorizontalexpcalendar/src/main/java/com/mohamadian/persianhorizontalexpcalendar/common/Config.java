package com.mohamadian.persianhorizontalexpcalendar.common;

import android.graphics.Color;

import com.mohamadian.persianhorizontalexpcalendar.view.cell.CustomGradientDrawable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class Config {

    /* CONFIGURATION */
    private static final ViewPagerType INIT_VIEW = ViewPagerType.MONTH;
    private static final int RANGE_MONTHS_BEFORE_INIT = 12*100;
    private static final int RANGE_MONTHS_AFTER_INIT = 12*100;
    public static final DateTime INIT_DATE = new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran")));
    public static final FirstDay FIRST_DAY_OF_WEEK = FirstDay.SATDAY;
    public static final int CELL_WEEKEND_BACKGROUND = Color.TRANSPARENT;
    public static final int CELL_NON_WEEKEND_BACKGROUND = Color.TRANSPARENT;
    public static final int CELL_TEXT_CURRENT_MONTH_COLOR = Color.BLACK;
    public static final int CELL_TEXT_ANOTHER_MONTH_COLOR = Color.LTGRAY;
    public static final int CELL_TEXT_CURRENT_MONTH_TODAY_COLOR = Color.WHITE;
    public static final boolean CELL_TEXT_SHOW_MARKS_ANOTHER_MONTH = false;
    public static final boolean USE_DAY_LABELS = true;
    public static final boolean SCROLL_TO_SELECTED_AFTER_COLLAPSE = true;

    public static CustomGradientDrawable todayCustomGradientDrawableMark = null;
    public static CustomGradientDrawable selectedCustomGradientDrawableMark = null;

  /* END CONFIGURATION */

    public static DateTime getStartDate(int RANGE_MONTHS_BEFORE_INIT) {
        DateTime START_BACK_BY_RANGE = INIT_DATE.plusMonths(-RANGE_MONTHS_BEFORE_INIT);
        DateTime START_BACK_TO_FIRST_DAY_OF_MONTH = START_BACK_BY_RANGE.plusDays(-START_BACK_BY_RANGE.getDayOfMonth() + 1);
        return START_BACK_TO_FIRST_DAY_OF_MONTH.plusDays(-START_BACK_TO_FIRST_DAY_OF_MONTH.getDayOfWeek() + 1);
    }

    public static DateTime getEndDate(int RANGE_MONTHS_AFTER_INIT) {
        DateTime END_FORWARD_BY_RANGE = INIT_DATE.plusMonths(RANGE_MONTHS_AFTER_INIT + 1);
        DateTime END_BACK_TO_FIRST_DAY_OF_MONTH = END_FORWARD_BY_RANGE.plusDays(-END_FORWARD_BY_RANGE.getDayOfMonth() + 1);
        return END_BACK_TO_FIRST_DAY_OF_MONTH.plusDays(7 - END_BACK_TO_FIRST_DAY_OF_MONTH.getDayOfWeek() + 1);
    }

    public static DateTime START_DATE = getStartDate(RANGE_MONTHS_BEFORE_INIT);
    public static DateTime END_DATE = getEndDate(RANGE_MONTHS_AFTER_INIT);

    public static final int MONTH_ROWS = 6;
    public static final int WEEK_ROWS = 1;
    public static final int COLUMNS = 7;

    public static ViewPagerType currentViewPager = INIT_VIEW;
    public static DateTime scrollDate = INIT_DATE;
    public static DateTime selectionDate = new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran")));
    public static int cellWidth = 0;
    public static int cellHeight = 0;
    public static int monthViewPagerHeight;
    public static int weekViewPagerHeight;

    public enum ViewPagerType {
        MONTH,
        WEEK
    }

    public enum FirstDay {
        SATDAY,
        SUNDAY,
        MONDAY
    }
}
