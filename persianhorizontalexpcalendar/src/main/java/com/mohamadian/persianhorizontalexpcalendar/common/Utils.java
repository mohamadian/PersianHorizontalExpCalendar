package com.mohamadian.persianhorizontalexpcalendar.common;

import android.content.res.Resources;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;

import java.util.Random;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class Utils {

    public static int monthPositionFromDate(DateTime dateTo) {
        DateTime dateFrom = Config.START_DATE.withDayOfWeek(7);
        return ((dateTo.getYear() - dateFrom.getYear()) * 12) + (dateTo.getMonthOfYear() - dateFrom.getMonthOfYear());
    }

    public static DateTime getDateByMonthPosition(int position) {
        return Config.START_DATE.withDayOfWeek(7 + firstDayOffset()).plusMonths(position);
    }

    public static int weekPositionFromDate(DateTime dateTo) {
        DateTime dt = dateTo.minusDays(firstDayOffset())
                .withHourOfDay(Config.START_DATE.getHourOfDay())
                .withMinuteOfHour(Config.START_DATE.getMinuteOfHour())
                .withSecondOfMinute(Config.START_DATE.getSecondOfMinute())
                .withMillisOfSecond(Config.START_DATE.getMillisOfSecond());

        return Weeks.weeksBetween(Config.START_DATE,dt).getWeeks();
    }

    public static DateTime getDateByWeekPosition(int position) {
        return Config.START_DATE.withDayOfWeek(7 + firstDayOffset()).plusWeeks(position);
    }

    public static boolean isWeekendByColumnNumber(int column) {
        switch (Config.FIRST_DAY_OF_WEEK) {
            case SATDAY:
                return (column == 5 || column == 6);
            case SUNDAY:
                return (column == 0 || column == 6);
            case MONDAY:
                return (column == 5 || column == 6);
            default:
                return false;
        }
    }

    public static int getRandomColor() {
        return new Random().nextInt(40) + 215;
    }

    public static int dayLabelExtraRow() {
        return Config.USE_DAY_LABELS ? 1 : 0;
    }

    public static int dayLabelExtraChildCount() {
        return 0;//Config.USE_DAY_LABELS ? 7 : 0;
    }

    public static boolean isMonthView() {
        return Config.currentViewPager == Config.ViewPagerType.MONTH;
    }

    public static boolean isTheSameMonthToScrollDate(DateTime dateTime) {
        return isTheSameMonth(Config.scrollDate, dateTime);
    }

    public static boolean isTheSameMonth(DateTime dateTime1, DateTime dateTime2) {
        return (dateTime1.getYear() == dateTime2.getYear()) && (dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear());
    }

    public static boolean isTheSameWeekToScrollDate(DateTime dateTime) {
        return isTheSameWeek(Config.scrollDate, dateTime);
    }

    public static boolean isTheSameWeek(DateTime dateTime1, DateTime dateTime2) {
        DateTime firstDateMovedByFirstDayOfWeek = dateTime1.minusDays(firstDayOffset());
        DateTime secondDateMovedByFirstDayOfWeek = dateTime2.minusDays(firstDayOffset());
        return (firstDateMovedByFirstDayOfWeek.getYear() == secondDateMovedByFirstDayOfWeek.getYear()) &&
                (firstDateMovedByFirstDayOfWeek.getWeekOfWeekyear() == secondDateMovedByFirstDayOfWeek.getWeekOfWeekyear());
    }

    public static int firstDayOffset() {
        switch (Config.FIRST_DAY_OF_WEEK) {
            case SATDAY:
                return 0;
            case SUNDAY:
                return -1;
            case MONDAY:
                return 0;
        }
        return 0;
    }

    public static int getWeekOfMonth(DateTime dateTime) {
        return ((dateTime.getDayOfMonth() + dateTime.withDayOfMonth(1).getDayOfWeek() - 2 - firstDayOffset()) / 7) + 1;
    }

    public static int animateContainerExtraTopOffset(Resources resources) {
        float density = resources.getDisplayMetrics().density;
        if (density >= 4.0) {
            return 0;
        }
        if (density >= 3.0) {
            return 0;
        }
        if (density >= 2.0) {
            return 1;
        }
        if (density >= 1.5) {
            return 2;
        }
        if (density >= 1.0) {
            return 2;
        }
        return 0;
    }

    public static int animateContainerExtraSideOffset(Resources resources) {
        float density = resources.getDisplayMetrics().density;
        if (density >= 4.0) {
            return 2;
        }
        if (density >= 3.0) {
            return 2;
        }
        if (density >= 2.0) {
            return 2;
        }
        if (density >= 1.5) {
            return 2;
        }
        if (density >= 1.0) {
            return 0;
        }
        return 0;
    }
}
