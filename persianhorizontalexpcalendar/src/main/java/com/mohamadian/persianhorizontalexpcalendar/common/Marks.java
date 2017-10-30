package com.mohamadian.persianhorizontalexpcalendar.common;

import com.mohamadian.persianhorizontalexpcalendar.model.MarkSetup;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.CustomGradientDrawable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class Marks {

    private static final String TAG = Marks.class.getName();
    private static Map<String, MarkSetup> marksMap;
    private static boolean locked = false;

    public static final float MARK_SmallOval_Bottom_SIZE_PROPORTION_TO_CELL = 0.15f;
    public static final float MARK_VerticalLine_Right_HEIGHT_PROPORTION_TO_CELL = 0.4f;
    public static final float MARK_VerticalLine_Right_WIDTH_PROPORTION_TO_CELL = 0.08f;

    public static void init() {
        marksMap = new HashMap<>();
    }

    public static void markToday() {
        if (isLocked())
            return;
        lock();
        MarkSetup markSetup = getMark(new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"))));
        if (markSetup == null)
            addNewMark(new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"))), new MarkSetup(true, false));
        else
            markSetup.setToday(true);
        unlock();
    }

    public static void refreshMarkSelected(DateTime newSelection, boolean can_mark_selected_day) {
        if (isLocked())
            return;
        lock();
        MarkSetup oldSelectionSetup = getMark(Config.selectionDate);
        if (oldSelectionSetup != null) {
            oldSelectionSetup.setSelected(false);
            if (oldSelectionSetup.canBeDeleted())
                marksMap.remove(dateTimeToStringKey(Config.selectionDate));
        }

        if (can_mark_selected_day){
            MarkSetup newSelectionSetup = getMark(newSelection);
            if (newSelectionSetup == null) {
                newSelectionSetup = new MarkSetup();
                marksMap.put(dateTimeToStringKey(newSelection), newSelectionSetup);
            }
            newSelectionSetup.setSelected(true);
        }

        Config.selectionDate = newSelection;
        unlock();
    }

    public static void refreshCustomMark(DateTime dateTime, CustomMarks customMarks, boolean mark, int color, CustomGradientDrawable customGradientDrawableMark) {
        if (isLocked())
            return;
        lock();
        MarkSetup markSetup = getMark(dateTime);
        if (markSetup == null) {
            markSetup = new MarkSetup();
            addNewMark(dateTime, markSetup);
        }
        switch (customMarks) {
            case SmallOval_Bottom:
                markSetup.setSmallOval_Bottom(mark, color);
                break;
            case VerticalLine_Right:
                markSetup.setVerticalLine_Right(mark, color);
                break;
            default:
                markSetup.setCustomGradientDrawable(mark, customGradientDrawableMark);
        }

        if (mark)
            if (markSetup.canBeDeleted())
                marksMap.remove(dateTimeToStringKey(dateTime));
        unlock();
    }

    public static void clear() {
        marksMap.clear();
    }

    private static void addNewMark(DateTime dateTime, MarkSetup markSetup) {
        marksMap.put(dateTimeToStringKey(dateTime), markSetup);
    }

    public static MarkSetup getMark(DateTime dateTime) {
        return marksMap.get(dateTimeToStringKey(dateTime));
    }

    private static String dateTimeToStringKey(DateTime dateTime) {
        return dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth();
    }

    public static void lock() {
        locked = true;
    }

    public static void unlock() {
        locked = false;
    }

    public static boolean isLocked() {
        return locked;
    }

    public enum CustomMarks {
        SmallOval_Bottom,
        VerticalLine_Right,
        Custom
    }
}
