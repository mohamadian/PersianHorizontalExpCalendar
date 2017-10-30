package com.mohamadian.persianhorizontalexpcalendar.view.cell;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.R;
import com.mohamadian.persianhorizontalexpcalendar.common.Config;
import com.mohamadian.persianhorizontalexpcalendar.common.Marks;
import com.mohamadian.persianhorizontalexpcalendar.model.MarkSetup;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class DayCellView extends BaseCellView {
    private TextView text;
    private TimeType timeType;
    private FrameLayout markContainer;

    private MarkSetup markSetup;
    private View markToday;
    private View markSelected;
    private View markSmallOval_Bottom;
    private View markVerticalLine_Right;
    private View newView = null;

    private int daysTextColorCurrentMonth = Config.CELL_TEXT_CURRENT_MONTH_COLOR;
    private int daysTextColorAnotherMonth = Config.CELL_TEXT_ANOTHER_MONTH_COLOR;

    public DayCellView(Context context) {
        super(context);
        init();
    }

    public DayCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.day_cell_view, this);

        text = (TextView) findViewById(R.id.text);
        markContainer = (FrameLayout) findViewById(R.id.mark_container);
        markToday = findViewById(R.id.mark_today_view);
        markSelected = findViewById(R.id.mark_selected_view);
        markSmallOval_Bottom = findViewById(R.id.mark_custom1);
        markVerticalLine_Right = findViewById(R.id.mark_custom2);

        text.setTextColor(daysTextColorCurrentMonth);
    }

    public void setDayTextColor(int daysTextColorCurrentMonth, int daysTextColorAnotherMonth){
        this.daysTextColorCurrentMonth = daysTextColorCurrentMonth;
        this.daysTextColorAnotherMonth = daysTextColorAnotherMonth;
    }
    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
        setTextColorByTimeType(daysTextColorCurrentMonth, daysTextColorAnotherMonth);
    }

    public void setDayNumber(int dayNumber) {
        this.text.setText(String.valueOf(dayNumber));
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
        setTextBackgroundByDayType();
    }

    private void setTextColorByTimeType(int currentMonthColor, int anotherMonthColor) {
        if (this.timeType == TimeType.CURRENT)
            text.setTextColor(currentMonthColor);
        else
            text.setTextColor(anotherMonthColor);
    }

    public void setMark(MarkSetup markSetup, int size) {
        setSize(size);
        setMarkSetup(markSetup);
    }

    private void setSize(int size) {
        LayoutParams markParams = (LayoutParams) markContainer.getLayoutParams();
        markParams.height = size;
        markParams.width = size;

        setupSmallOval_BottomMark(size);
        setupVerticalLine_RightMark(size);
    }

    private void setupSmallOval_BottomMark(int size) {
        LayoutParams markCustomParams = (LayoutParams) markSmallOval_Bottom.getLayoutParams();
        int markCustomPercentSize = (int) (size * Marks.MARK_SmallOval_Bottom_SIZE_PROPORTION_TO_CELL);
        markCustomParams.height = markCustomPercentSize;
        markCustomParams.width = markCustomPercentSize;
    }

    private void setupVerticalLine_RightMark(int size) {
        LayoutParams markCustomParams = (LayoutParams) markVerticalLine_Right.getLayoutParams();
        markCustomParams.height = (int) (size * Marks.MARK_VerticalLine_Right_HEIGHT_PROPORTION_TO_CELL);
        markCustomParams.width = (int) (size * Marks.MARK_VerticalLine_Right_WIDTH_PROPORTION_TO_CELL);
    }

    public void setMarkSetup(MarkSetup markSetup) {
        this.markSetup = markSetup;
        setMarkToView();
    }

    private void setMarkToView() {
        boolean is_CurrentTime = this.timeType == TimeType.CURRENT | Config.CELL_TEXT_SHOW_MARKS_ANOTHER_MONTH | Config.currentViewPager == Config.ViewPagerType.WEEK;

        if (markSetup == null) {
            if (newView != null)
                markContainer.removeView(newView);

            markContainer.setVisibility(GONE);
            if (is_CurrentTime)
                text.setTextColor(daysTextColorCurrentMonth);
            else
                text.setTextColor(daysTextColorAnotherMonth);

        } else {
            markContainer.setVisibility(VISIBLE);
            markToday.setVisibility(markSetup.isToday() & is_CurrentTime ? VISIBLE : GONE);
            markSelected.setVisibility(markSetup.isSelected() & !markSetup.isToday() & is_CurrentTime ? VISIBLE : GONE);
            markSmallOval_Bottom.setVisibility(markSetup.isSmallOval_Bottom() & is_CurrentTime ? VISIBLE : GONE);
            markVerticalLine_Right.setVisibility(markSetup.isVerticalLine_Right() & is_CurrentTime ? VISIBLE : GONE);

            if (markSmallOval_Bottom.getVisibility() == VISIBLE || markVerticalLine_Right.getVisibility() == VISIBLE)
                if (is_CurrentTime)
                    text.setTextColor(daysTextColorCurrentMonth);
                else
                    text.setTextColor(daysTextColorAnotherMonth);

            if (markSetup.isToday() && is_CurrentTime)
                text.setTextColor(Config.CELL_TEXT_CURRENT_MONTH_TODAY_COLOR);

            if (markToday.getVisibility() == VISIBLE && Config.todayCustomGradientDrawableMark != null) {
                markToday.setBackground(Config.todayCustomGradientDrawableMark);
                text.setTextColor(Config.todayCustomGradientDrawableMark.getTextColor());
            }

            if (markSelected.getVisibility() == VISIBLE && Config.selectedCustomGradientDrawableMark != null){
                markSelected.setBackground(Config.selectedCustomGradientDrawableMark);
                text.setTextColor(Config.selectedCustomGradientDrawableMark.getTextColor());
            }

            if (markSetup.isCustomGradientDrawable() && is_CurrentTime && markSetup.getCustomGradientDrawableMark() != null)
                addNewView(markSetup.getCustomGradientDrawableMark());

            if (markSetup.isSmallOval_Bottom())
                changeSmallOval_BottomBackgroundColor(markSetup.getSmallOval_BottomColor());

            if (markSetup.isVerticalLine_Right())
                changeVerticalLine_RightBackgroundColor(markSetup.getVerticalLine_RightColor());
        }
    }

    private void addNewView(CustomGradientDrawable customGradientDrawable){
        if (newView != null)
            markContainer.removeView(newView);

        newView = new View(getContext());
        newView.setLayoutParams(
                new LayoutParams(
                        customGradientDrawable.getLayoutWidth(),
                        customGradientDrawable.getLayoutHeight(),
                        customGradientDrawable.getLayoutGravity()));

        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)newView.getLayoutParams();
        int[] margins = customGradientDrawable.getMargins();
        p.setMargins(intToDp(margins[0]),intToDp(margins[1]),intToDp(margins[2]),intToDp(margins[3]));

        text.setTextColor(customGradientDrawable.getTextColor());

        newView.setBackground(customGradientDrawable);
        markContainer.addView(newView);
    }

    private int intToDp(int value){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private void changeSmallOval_BottomBackgroundColor(int color){
        markSmallOval_Bottom.setBackground(new CustomGradientDrawable(GradientDrawable.OVAL, color));
    }

    private void changeVerticalLine_RightBackgroundColor(int color){
        markVerticalLine_Right.setBackground(new CustomGradientDrawable(GradientDrawable.RECTANGLE, color));
    }
}
