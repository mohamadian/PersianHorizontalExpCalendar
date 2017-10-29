package com.mohamadian.persianhorizontalexpcalendar.view.page;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.R;
import com.mohamadian.persianhorizontalexpcalendar.common.Config;
import com.mohamadian.persianhorizontalexpcalendar.common.Constants;
import com.mohamadian.persianhorizontalexpcalendar.common.Marks;
import com.mohamadian.persianhorizontalexpcalendar.common.Utils;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.BaseCellView;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.DayCellView;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.LabelCellView;

import org.joda.time.DateTime;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class PageView extends FrameLayout implements View.OnClickListener {

    private PageViewListener pageViewListener;
    private GridLayout gridLayout;
    private DateTime pageDate;

    private Config.ViewPagerType viewPagerType;
    private int rows;

    public Typeface dayLablesTypeface;
    public int dayLablesTextColor;
    public int dayLablesTextSize;

    public Typeface daysTypeface;
    public int daysTextColorCurrentMonth;
    public int daysTextColorAnotherMonth;
    public int daysTextSize;

    public boolean use_RTL_direction;

    public PageView(Context context) {
        this(context, null, null);
    }

    public PageView(Context context, Config.ViewPagerType viewPagerType, PageViewListener pageViewListener) {
        super(context);
        if (pageViewListener != null) {
            this.pageViewListener = pageViewListener;
        }
        if (viewPagerType != null) {
            this.viewPagerType = viewPagerType;
            this.rows = viewPagerType == Config.ViewPagerType.MONTH ? Config.MONTH_ROWS : Config.WEEK_ROWS;
            init();
        }
    }

    private void init() {
        initViews();
    }

    private void initViews() {
        inflate(getContext(), R.layout.page_view, this);
        gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(Config.COLUMNS);
        gridLayout.setRowCount(rows + (Utils.dayLabelExtraRow()));
    }

    public void setup(DateTime pageDate) {
        this.pageDate = pageDate;
        addCellsToGrid();
    }

    private void addCellsToGrid() {
        DateTime cellDate;
        if (viewPagerType == Config.ViewPagerType.MONTH) {
            cellDate = pageDate.withDayOfMonth(1).plusDays(-pageDate.withDayOfMonth(1).getDayOfWeek() + 1 + Utils.firstDayOffset());
        } else {
            cellDate = pageDate.plusDays(-pageDate.getDayOfWeek() + 1 + Utils.firstDayOffset());
        }
        addLabels();
        addDays(cellDate);
    }

    private void addDays(DateTime cellDate) {
        for (int r = Utils.dayLabelExtraRow(); r < rows + (Utils.dayLabelExtraRow()); r++) {
            for (int c = 0; c < Config.COLUMNS; c++) {
                DayCellView dayCellView = new DayCellView(getContext());

                GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));
                cellParams.height = Config.cellHeight;
                cellParams.width = Config.cellWidth;
                dayCellView.setTag(cellDate);
                dayCellView.setLayoutParams(cellParams);
                dayCellView.setDayNumber(cellDate.getDayOfMonth());
                dayCellView.setDayType(Utils.isWeekendByColumnNumber(c) ? BaseCellView.DayType.WEEKEND : BaseCellView.DayType.NO_WEEKEND);
                dayCellView.setOnClickListener(this);
                dayCellView.setDayTextColor(daysTextColorCurrentMonth, daysTextColorAnotherMonth);
                if (viewPagerType == Config.ViewPagerType.MONTH)
                    dayCellView.setTimeType(getTimeType(cellDate));
                else
                    ((TextView) dayCellView.findViewById(R.id.text)).setTextColor(daysTextColorCurrentMonth);

                dayCellView.setMark(Marks.getMark(cellDate), Config.cellHeight);

                if (daysTypeface != null)
                    ((TextView) dayCellView.findViewById(R.id.text)).setTypeface(daysTypeface);
                if (daysTextSize > 0)
                    ((TextView) dayCellView.findViewById(R.id.text)).setTextSize(TypedValue.COMPLEX_UNIT_SP,daysTextSize);


                gridLayout.addView(dayCellView);
                cellDate = cellDate.plusDays(1);
            }
        }
    }

    private void addLabels() {
        if (Config.USE_DAY_LABELS) {
            LinearLayout lable_cell_linearlayout = (LinearLayout) getRootView().findViewById(R.id.lable_cell_linearlayout) ;
            lable_cell_linearlayout.setVisibility(VISIBLE);

            GridLayout lable_gridLayout = (GridLayout) getRootView().findViewById(R.id.lable_cell_grid_layout);
            lable_gridLayout.setColumnCount(Config.COLUMNS);
            lable_gridLayout.setRowCount(1);

            for (int l = 0; l < Config.COLUMNS; l++) {
                LabelCellView label = new LabelCellView(getContext());

                GridLayout.LayoutParams labelParams = new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(l));
                labelParams.height = Config.cellHeight;
                labelParams.width = Config.cellWidth;
                label.setLayoutParams(labelParams);
                label.setText(use_RTL_direction ? Constants.NAME_OF_DAYS_RTL[(l + Utils.firstDayOffset()) % 7] : Constants.NAME_OF_DAYS_LTR[(l + Utils.firstDayOffset()) % 7]);
                label.setDayType(Utils.isWeekendByColumnNumber(l) ? BaseCellView.DayType.WEEKEND : BaseCellView.DayType.NO_WEEKEND);

                ((TextView) label.findViewById(R.id.text)).setTypeface(dayLablesTypeface);
                ((TextView) label.findViewById(R.id.text)).setTextColor(dayLablesTextColor);
                if (dayLablesTextSize > 0)
                    ((TextView) label.findViewById(R.id.text)).setTextSize(TypedValue.COMPLEX_UNIT_SP,dayLablesTextSize);

                lable_gridLayout.addView(label);
            }
        }
    }

    private DayCellView.TimeType getTimeType(DateTime cellTime) {
        if (cellTime.getMonthOfYear() < pageDate.getMonthOfYear()) {
            return DayCellView.TimeType.PAST;
        } else if (cellTime.getMonthOfYear() > pageDate.getMonthOfYear()) {
            return DayCellView.TimeType.FUTURE;
        } else {
            return DayCellView.TimeType.CURRENT;
        }
    }

    public void updateMarks() {
        for (int c = Utils.dayLabelExtraChildCount(); c < gridLayout.getChildCount(); c++) {
            DayCellView dayCellView = (DayCellView) gridLayout.getChildAt(c);
            dayCellView.setMarkSetup(Marks.getMark((DateTime) dayCellView.getTag()));
        }
    }

    @Override
    public void onClick(View view) {
        if (pageViewListener != null) {
            pageViewListener.onDayClick((DateTime) view.getTag());
        }
    }

    public interface PageViewListener {
        PersianHorizontalExpCalendar onDayClick(DateTime dateTime);
    }
}
