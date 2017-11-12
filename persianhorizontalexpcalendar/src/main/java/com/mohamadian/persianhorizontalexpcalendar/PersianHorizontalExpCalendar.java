package com.mohamadian.persianhorizontalexpcalendar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.animator.CalendarAnimation;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianCustomMarks;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;
import com.mohamadian.persianhorizontalexpcalendar.listener.AnimationsListener;
import com.mohamadian.persianhorizontalexpcalendar.listener.PageViewListener;
import com.mohamadian.persianhorizontalexpcalendar.listener.SmallAnimationListener;
import com.mohamadian.persianhorizontalexpcalendar.listener.SmallPageChangeListener;
import com.mohamadian.persianhorizontalexpcalendar.model.MarkSetup;
import com.mohamadian.persianhorizontalexpcalendar.model.CustomGradientDrawable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Weeks;
import org.joda.time.chrono.PersianChronologyKhayyam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class PersianHorizontalExpCalendar extends LinearLayout implements PageViewListener, AnimationsListener {

    //config data
    protected int CELL_TEXT_CURRENT_MONTH_COLOR = Color.BLACK;
    protected int CELL_TEXT_ANOTHER_MONTH_COLOR = Color.LTGRAY;

    protected CustomGradientDrawable todayCustomGradientDrawableMark = null;
    protected CustomGradientDrawable selectedCustomGradientDrawableMark = null;

    protected PersianViewPagerType INIT_VIEW = PersianViewPagerType.MONTH;
    public int RANGE_MONTHS_BEFORE_INIT = 12 * 100;
    public int RANGE_MONTHS_AFTER_INIT = 12 * 100;
    public DateTime INIT_DATE = new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran")));
    public FirstDay FIRST_DAY_OF_WEEK = FirstDay.SATDAY;
    public int CELL_WEEKEND_BACKGROUND = Color.TRANSPARENT;
    public int CELL_NON_WEEKEND_BACKGROUND = Color.TRANSPARENT;
    public int CELL_TEXT_CURRENT_MONTH_TODAY_COLOR = Color.WHITE;
    public boolean CELL_TEXT_SHOW_MARKS_ANOTHER_MONTH = false;
    public boolean USE_DAY_LABELS = true;
    public boolean SCROLL_TO_SELECTED_AFTER_COLLAPSE = true;

    protected DateTime START_DATE = getStartDate(RANGE_MONTHS_BEFORE_INIT);
    protected DateTime END_DATE = getEndDate(RANGE_MONTHS_AFTER_INIT);

    protected int MONTH_ROWS = 6;
    protected int WEEK_ROWS = 1;
    protected int COLUMNS = 7;

    protected PersianViewPagerType currentViewPager = INIT_VIEW;
    protected DateTime scrollDate = INIT_DATE;
    protected DateTime selectionDate = INIT_DATE;
    protected int cellWidth = 0;
    protected int cellHeight = 0;
    protected int monthViewPagerHeight;
    protected int weekViewPagerHeight;

    public float[] ANIMATION_INCREASING_VALUES = new float[]{0.0f, 1.0f};
    public float[] ANIMATION_DECREASING_VALUES = new float[]{1.0f, 0.0f};
    public int ANIMATION_ALPHA_DURATION = 0; //300
    public int ANIMATION_SIZE_DURATION = 0; //200

    public String[] NAME_OF_DAYS_RTL = new String[]{"ش", "ی", "د", "س", "چ", "پ", "ج"};
    public String[] NAME_OF_DAYS_LTR = new String[]{"SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI"};

    public enum FirstDay {
        SATDAY,
        SUNDAY,
        MONDAY
    }

    public enum DayType {
        WEEKEND,
        NO_WEEKEND
    }

    public enum TimeType {
        PAST,
        CURRENT,
        FUTURE
    }
    //

    protected boolean ifExpand = true;
    protected TextView titleTextView;
    protected TextView todayButton;
    protected RelativeLayout centerContainer;
    protected GridLayout animateContainer;

    protected int expandResID = R.drawable.icon_arrow_down;
    protected int collapseResID = R.drawable.icon_arrow_up;

    protected ViewPager monthViewPager;
    protected CalendarAdapter monthPagerAdapter = null;

    protected ViewPager weekViewPager;
    protected CalendarAdapter weekPagerAdapter = null;

    protected Animations animations = null;
    protected PersianHorizontalExpCalListener persianHorizontalExpCalListener;
    protected Marks marks = new Marks();
    protected Utils utils = new Utils();

    protected Typeface dayLablesTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/GanjNamehSans-Regular.ttf");
    protected int dayLablesTextColor = Color.parseColor("#b9b9b9");
    protected int dayLablesTextSize = -1;

    protected Typeface daysTypeface = null;
    protected int daysTextColorCurrentMonth = CELL_TEXT_CURRENT_MONTH_COLOR;
    protected int daysTextColorAnotherMonth = CELL_TEXT_ANOTHER_MONTH_COLOR;
    protected int daysTextSize = -1;

    protected boolean use_RTL_direction = true;
    protected boolean can_mark_today = true;
    protected boolean can_mark_selected_day = true;

    protected boolean lock;
    protected boolean needToFireOnDateSelectedEventAfterScrollToDate = false;

    public PersianHorizontalExpCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        initTopContainer();
        setRootLayoutDirection();
    }

    public PersianHorizontalExpCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        initTopContainer();
        setRootLayoutDirection();
    }

    protected DateTime getStartDate(int RANGE_MONTHS_BEFORE_INIT) {
        DateTime START_BACK_BY_RANGE = INIT_DATE.plusMonths(-RANGE_MONTHS_BEFORE_INIT);
        DateTime START_BACK_TO_FIRST_DAY_OF_MONTH = START_BACK_BY_RANGE.plusDays(-START_BACK_BY_RANGE.getDayOfMonth() + 1);
        return START_BACK_TO_FIRST_DAY_OF_MONTH.plusDays(-START_BACK_TO_FIRST_DAY_OF_MONTH.getDayOfWeek() + 1);
    }

    protected DateTime getEndDate(int RANGE_MONTHS_AFTER_INIT) {
        DateTime END_FORWARD_BY_RANGE = INIT_DATE.plusMonths(RANGE_MONTHS_AFTER_INIT + 1);
        DateTime END_BACK_TO_FIRST_DAY_OF_MONTH = END_FORWARD_BY_RANGE.plusDays(-END_FORWARD_BY_RANGE.getDayOfMonth() + 1);
        return END_BACK_TO_FIRST_DAY_OF_MONTH.plusDays(7 - END_BACK_TO_FIRST_DAY_OF_MONTH.getDayOfWeek() + 1);
    }


    public PersianHorizontalExpCalendar setPersianHorizontalExpCalListener(PersianHorizontalExpCalListener persianHorizontalExpCalListener) {
        this.persianHorizontalExpCalListener = persianHorizontalExpCalListener;
        return this;
    }

    protected void setRootLayoutDirection() {
        if (android.os.Build.VERSION.SDK_INT >= 17)
            findViewById(R.id.root_linearlayout).setLayoutDirection(use_RTL_direction ? LAYOUT_DIRECTION_RTL : LAYOUT_DIRECTION_LTR);
    }

    public PersianHorizontalExpCalendar removeHorizontalExpCalListener() {
        this.persianHorizontalExpCalListener = null;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        animations.unbind();
        marks.clear();
        super.onDetachedFromWindow();
    }

    protected void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.persian_horizontal_exp_calendar, this);

        centerContainer = (RelativeLayout) findViewById(R.id.center_container);
        lock = false;

        setValuesFromAttr(attributeSet);
        setupCellWidth();

        marks.init();
        if (can_mark_today)
            marks.markToday();
        marks.refreshMarkSelected(selectionDate, can_mark_selected_day);

        initAnimation();
    }

    protected void setCellHeight() {
        cellHeight = monthViewPagerHeight / (MONTH_ROWS + utils.dayLabelExtraRow());
    }

    protected void setupCellWidth() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PersianHorizontalExpCalendar.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cellWidth = getMeasuredWidth() / COLUMNS;
                setupViews();
            }
        });
    }

    protected void setValuesFromAttr(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PersianHorizontalExpCalendar);
        if (typedArray != null) {
            setupUseRTLDirectionFromAttr(typedArray);
            setupRangeMonthsBeforeAfterFromAttr(typedArray);
            setupMarksFromAttr(typedArray);
            setupInitialViewFromAttr(typedArray);
            setupTopContainerFromAttr(typedArray);
            setupMiddleContainerFromAttr(typedArray);
            setupBottomContainerFromAttr(typedArray);
            typedArray.recycle();
        }

        setHeightToCenterContainer(utils.isMonthView() ? monthViewPagerHeight : weekViewPagerHeight);
    }

    @SuppressLint("WrongViewCast")
    protected void setupBottomContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_bottom_container_height)) {
            ((LinearLayout.LayoutParams) findViewById(R.id.bottom_container).getLayoutParams()).height =
                    typedArray.getDimensionPixelSize(R.styleable.PersianHorizontalExpCalendar_bottom_container_height,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    protected void setupMiddleContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_center_container_expanded_height)) {
            monthViewPagerHeight = typedArray.getDimensionPixelSize(
                    R.styleable.PersianHorizontalExpCalendar_center_container_expanded_height, LinearLayout.LayoutParams.WRAP_CONTENT);
            setCellHeight();
            weekViewPagerHeight = cellHeight * (USE_DAY_LABELS ? 2 : 1);
        }
    }

    protected void setupInitialViewFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_initial_view)) {
            PersianViewPagerType nextView = typedArray.getString(R.styleable.PersianHorizontalExpCalendar_initial_view).equalsIgnoreCase("WEEK") ? PersianViewPagerType.WEEK : PersianViewPagerType.MONTH;
            currentViewPager = nextView;
            ifExpand = currentViewPager == PersianViewPagerType.MONTH;
            ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
            expandImage.setImageResource(ifExpand ? collapseResID : expandResID);
        }
    }

    protected void setupRangeMonthsBeforeAfterFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_range_months_before_init))
            if (typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_before_init, 12 * 100) > 0)
                START_DATE = getStartDate(typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_before_init, 12 * 100));

        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_range_months_after_init))
            if (typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_after_init, 12 * 100) > 0)
                END_DATE = getEndDate(typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_after_init, 12 * 100));
    }

    protected void setupMarksFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_mark_today))
            can_mark_today = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_mark_today, true);

        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_mark_selected_day))
            can_mark_selected_day = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_mark_selected_day, true);
    }

    protected void setupUseRTLDirectionFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_use_RTL_direction))
            use_RTL_direction = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_use_RTL_direction, true);
    }

    @SuppressLint("WrongViewCast")
    protected void setupTopContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_top_container_height)) {
            ((LinearLayout.LayoutParams) findViewById(R.id.top_container).getLayoutParams()).height =
                    typedArray.getDimensionPixelSize(R.styleable.PersianHorizontalExpCalendar_top_container_height,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    protected void initAnimation() {
        animations = new Animations(getContext(), PersianHorizontalExpCalendar.this, utils.animateContainerExtraTopOffset(getResources()));
    }

    protected void setupViews() {
        initCenterContainer();
        initBottomContainer();
        initAnimateContainer();
        refreshTitleTextView();
        refreshTodayButtonTitle();
    }

    protected void initAnimateContainer() {
        animateContainer = (GridLayout) findViewById(R.id.animate_container);
        animateContainer.getLayoutParams().height = cellHeight;
        int sideMargin = utils.animateContainerExtraSideOffset(getResources());
        animateContainer.setPadding(sideMargin, 0, sideMargin, 0);
    }

    protected void initTopContainer() {
        titleTextView = (TextView) findViewById(R.id.title);
        todayButton = (Button) findViewById(R.id.scroll_to_today_button);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GanjNamehSans-Regular.ttf");
        titleTextView.setTypeface(font);
        todayButton.setTypeface(font);

        findViewById(R.id.scroll_to_today_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocked()) {
                    lock();
                    DateTime now = new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran")));

                    marks.refreshMarkSelected(now, can_mark_selected_day);
                    updateMarks();

                    selectionDate = now;
                    scrollToDate(now, true, true, true);
                    unlock();
                }
            }
        });
    }

    protected void initCenterContainer() {
        initMonthViewPager();
        initWeekViewPager();
    }

    protected void initBottomContainer() {
        final ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
        expandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ifExpand)
                    expand();
                else
                    collapse();
            }
        });
    }

    protected void switchToView(final PersianViewPagerType switchTo) {
        currentViewPager = switchTo;
        if (ANIMATION_ALPHA_DURATION + ANIMATION_SIZE_DURATION > 0) {
            animations.clearAnimationsListener();
            animations.startHidePagerAnimation();
        } else
            animations.swithWithoutAnimation();
    }

    protected void initMonthViewPager() {
        monthViewPager = (ViewPager) findViewById(R.id.month_view_pager);
        if (use_RTL_direction)
            monthViewPager.setRotationY(180);
        monthPagerAdapter = new CalendarAdapter(getContext(), PersianViewPagerType.MONTH, this);
        monthViewPager.setAdapter(monthPagerAdapter);
        monthViewPager.setCurrentItem(utils.monthPositionFromDate(INIT_DATE));
        monthViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (utils.isMonthView()) {
                    scrollDate = utils.getDateByMonthPosition(monthViewPager.getCurrentItem());
                    if (utils.isTheSameMonthToScrollDate(selectionDate))
                        scrollDate = selectionDate.toDateTime();

                    refreshTitleTextView();
                    if (persianHorizontalExpCalListener != null) {
                        persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfMonth(1));
                        if (needToFireOnDateSelectedEventAfterScrollToDate) {
                            persianHorizontalExpCalListener.onDateSelected(selectionDate);
                            needToFireOnDateSelectedEventAfterScrollToDate = false;
                        }
                    }
                    unlock();
                }
            }
        });
        monthViewPager.setVisibility(utils.isMonthView() ? VISIBLE : GONE);
    }

    protected void initWeekViewPager() {
        weekViewPager = (ViewPager) findViewById(R.id.week_view_pager);
        if (use_RTL_direction)
            weekViewPager.setRotationY(180);
        weekPagerAdapter = new CalendarAdapter(getContext(), PersianViewPagerType.WEEK, this);
        weekViewPager.setAdapter(weekPagerAdapter);
        setWeekViewPagerPosition(utils.weekPositionFromDate(INIT_DATE), false);
        weekViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (!utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (!utils.isMonthView()) {
                    scrollDate = utils.getDateByWeekPosition(weekViewPager.getCurrentItem());
                    if (utils.weekPositionFromDate(scrollDate) == utils.weekPositionFromDate(selectionDate)) {
                        scrollDate = selectionDate;
                    }
                    refreshTitleTextView();
                    if (persianHorizontalExpCalListener != null) {
                        persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfWeek(1));
                        if (needToFireOnDateSelectedEventAfterScrollToDate) {
                            persianHorizontalExpCalListener.onDateSelected(selectionDate);
                            needToFireOnDateSelectedEventAfterScrollToDate = false;
                        }
                    }
                    unlock();
                }
            }
        });
        weekViewPager.setVisibility(!utils.isMonthView() ? VISIBLE : GONE);
    }

    protected void setWeekViewPagerPosition(int position, boolean animate) {
        weekViewPager.setCurrentItem(position, animate);
    }

    protected void setMonthViewPagerPosition(int position, boolean animate) {
        monthViewPager.setCurrentItem(position, animate);
    }

    protected void refreshTitleTextView() {
        DateTime titleDate = scrollDate;
        if (currentViewPager == PersianViewPagerType.MONTH) {
            if (utils.isTheSameMonthToScrollDate(selectionDate)) {
                titleDate = selectionDate;
            }
        } else {
            if (utils.isTheSameWeekToScrollDate(selectionDate))
                titleDate = selectionDate;
            else
                titleDate = titleDate.minusDays(6);//go to first day of week
        }
        refreshTitleTextView(titleDate);
    }

    protected void refreshTitleTextView(DateTime dt) {
        titleTextView.setText(String.format("%s %s", use_RTL_direction ? getMonthString_RTL(dt.getMonthOfYear()) : getMonthString_LTR(dt.getMonthOfYear()), dt.getYear()));
        refreshTodayButtonTitle();
    }

    protected void refreshTodayButtonTitle() {
        todayButton.setText(Integer.toString(new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"))).getDayOfMonth()));
    }

    public String getMonthString_RTL(int mothOfYear) {
        switch (mothOfYear) {
            case 1:
                return "فروردین";
            case 2:
                return "اردیبهشت";
            case 3:
                return "خرداد";
            case 4:
                return "تیر";
            case 5:
                return "مرداد";
            case 6:
                return "شهریور";
            case 7:
                return "مهر";
            case 8:
                return "آبان";
            case 9:
                return "آذر";
            case 10:
                return "دی";
            case 11:
                return "بهمن";
            case 12:
                return "اسفند";
            default:
                return "";
        }
    }

    public String getMonthString_LTR(int mothOfYear) {
        switch (mothOfYear) {
            case 1:
                return "Farvardin";
            case 2:
                return "Ordibehesht";
            case 3:
                return "Khordad";
            case 4:
                return "Tir";
            case 5:
                return "Mordad";
            case 6:
                return "Shahrivar";
            case 7:
                return "Mehr";
            case 8:
                return "Aban";
            case 9:
                return "Azar";
            case 10:
                return "Day";
            case 11:
                return "Bahman";
            case 12:
                return "Esfand";
            default:
                return "";
        }
    }

    protected void lock() {
        lock = true;
    }

    protected void unlock() {
        lock = false;
    }

    protected boolean isLocked() {
        return lock;
    }

    @Override
    public PersianHorizontalExpCalendar scrollToDate(DateTime dateTime, boolean scrollMonthPager, boolean scrollWeekPager, boolean animate) {
        if (scrollMonthPager)
            setMonthViewPagerPosition(utils.monthPositionFromDate(dateTime), animate);
        if (scrollWeekPager)
            setWeekViewPagerPosition(utils.weekPositionFromDate(dateTime), animate);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar animateContainerAddView(View view) {
        animateContainer.addView(view);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar animateContainerRemoveViews() {
        animateContainer.removeAllViews();
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar updateMarks() {
        if (currentViewPager == PersianViewPagerType.MONTH)
            monthPagerAdapter.updateMarks();
        else
            weekPagerAdapter.updateMarks();
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar changeViewPager(PersianViewPagerType persianViewPagerType) {
        if (persianHorizontalExpCalListener != null)
            persianHorizontalExpCalListener.onChangeViewPager(persianViewPagerType);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar onDayClick(DateTime dateTime) {
        scrollToDate(dateTime, true);

        marks.refreshMarkSelected(dateTime, can_mark_selected_day);
        updateMarks();

        refreshTitleTextView();

        if (persianHorizontalExpCalListener != null) {
            persianHorizontalExpCalListener.onDateSelected(dateTime);
        }
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setHeightToCenterContainer(int height) {
        ((LinearLayout.LayoutParams) centerContainer.getLayoutParams()).height = height;
        centerContainer.requestLayout();
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setTopMarginToAnimContainer(int margin) {
        ((RelativeLayout.LayoutParams) animateContainer.getLayoutParams()).topMargin = margin;
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setWeekPagerVisibility(int visibility) {
        weekViewPager.setVisibility(visibility);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setMonthPagerVisibility(int visibility) {
        monthViewPager.setVisibility(visibility);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setAnimatedContainerVisibility(int visibility) {
        animateContainer.setVisibility(visibility);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setMonthPagerAlpha(float alpha) {
        monthViewPager.setAlpha(alpha);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar setWeekPagerAlpha(float alpha) {
        weekViewPager.setAlpha(alpha);
        return this;
    }

    public interface PersianHorizontalExpCalListener {
        void onCalendarScroll(DateTime dateTime);

        void onDateSelected(DateTime dateTime);

        void onChangeViewPager(PersianViewPagerType persianViewPagerType);
    }

    public PersianHorizontalExpCalendar scrollToDate(DateTime dateTime) {
        return scrollToDate(dateTime, true);
    }

    public PersianHorizontalExpCalendar scrollToDate(DateTime dateTime, boolean animate) {
        if ((currentViewPager == PersianViewPagerType.MONTH && utils.isTheSameMonthToScrollDate(dateTime))
                ||
                (currentViewPager == PersianViewPagerType.WEEK && utils.isTheSameWeekToScrollDate(dateTime) && utils.isTheSameMonthToScrollDate(dateTime))) {
            if (!isEqualDate(selectionDate, dateTime)) {
                marks.refreshMarkSelected(dateTime, can_mark_selected_day);
                updateMarks();

                selectionDate = dateTime;
                persianHorizontalExpCalListener.onDateSelected(selectionDate);
            }
            return this;
        }

        boolean isMonthView = utils.isMonthView();
        marks.refreshMarkSelected(dateTime, can_mark_selected_day);
        updateMarks();
        selectionDate = dateTime;
        needToFireOnDateSelectedEventAfterScrollToDate = true;
        scrollToDate(dateTime, isMonthView, !isMonthView, animate);

        return this;
    }

    protected boolean isEqualDate(DateTime dt1, DateTime dt2) {
        int int_dt1 = dt1.getYear() * 10000 + dt1.getMonthOfYear() * 100 + dt1.getDayOfMonth();
        int int_dt2 = dt1.getYear() * 10000 + dt1.getMonthOfYear() * 100 + dt1.getDayOfMonth();
        return int_dt1 == int_dt2;
    }

    public PersianHorizontalExpCalendar setTopContainerBackground(Drawable background) {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.top_container);
        container.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setTopContainerBackgroundColor(int color) {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.top_container);
        container.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTypeface(Typeface font) {
        titleTextView.setTypeface(font);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTypeface(Typeface font) {
        todayButton.setTypeface(font);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTextColor(int color) {
        titleTextView.setTextColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTextColor(int color) {
        todayButton.setTextColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTextSize(float Size) {
        titleTextView.setTextSize(Size);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTextSize(float Size) {
        todayButton.setTextSize(Size);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonBackground(Drawable background) {
        todayButton.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setBottomContainerBackground(Drawable background) {
        View line_view = (View) findViewById(R.id.line_view);
        line_view.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setBottomContainerBackgroundColor(int color) {
        View line_view = (View) findViewById(R.id.line_view);
        line_view.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setExpandImageResource(int resID) {
        expandResID = resID;

        ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
        expandImage.setImageResource(ifExpand ? collapseResID : expandResID);

        return this;
    }

    public PersianHorizontalExpCalendar setCollapseImageResource(int resID) {
        collapseResID = resID;

        ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
        expandImage.setImageResource(ifExpand ? collapseResID : expandResID);

        return this;
    }

    public PersianHorizontalExpCalendar setCenterContainerBackground(Drawable background) {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.center_container);
        container.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setCenterContainerBackgroundColor(int color) {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.center_container);
        container.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTypeface(Typeface font) {
        dayLablesTypeface = font;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysTypeface(Typeface font) {
        daysTypeface = font;
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTextColor(int color) {
        dayLablesTextColor = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysCurrentMonthTextColor(int color) {
        daysTextColorCurrentMonth = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysAnotherMonthTextColor(int color) {
        daysTextColorAnotherMonth = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTextSize(int size) {
        dayLablesTextSize = size;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysTextSize(int size) {
        daysTextSize = size;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineVisibility(int visibility) {
        findViewById(R.id.lable_cell_horizontal_line).setVisibility(visibility);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineBackground(Drawable background) {
        findViewById(R.id.lable_cell_horizontal_line).setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineBackgroundColor(int color) {
        findViewById(R.id.lable_cell_horizontal_line).setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineHeight(int height) {
        View rl = (View) findViewById(R.id.lable_cell_horizontal_line);
        rl.getLayoutParams().height = height;
        return this;
    }

    public PersianHorizontalExpCalendar expand() {
        if (!ifExpand) {
            lock();
            ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
            if (currentViewPager != PersianViewPagerType.MONTH)
                switchToView(PersianViewPagerType.MONTH);
            unlock();
            expandImage.setImageResource(collapseResID);
        }
        ifExpand = !ifExpand;
        refreshTitleTextView();

        return this;
    }

    public PersianHorizontalExpCalendar collapse() {
        if (ifExpand) {
            if (!isLocked()) {
                lock();
                ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
                if (currentViewPager != PersianViewPagerType.WEEK)
                    switchToView(PersianViewPagerType.WEEK);
                unlock();
                expandImage.setImageResource(expandResID);
            }
        }
        ifExpand = !ifExpand;
        refreshTitleTextView();

        return this;
    }

    public PersianHorizontalExpCalendar markToday() {
        marks.markToday();
        return this;
    }

    public PersianHorizontalExpCalendar setMarkTodayCustomGradientDrawable(CustomGradientDrawable customGradientDrawable) {
        todayCustomGradientDrawableMark = customGradientDrawable;
        return this;
    }

    public PersianHorizontalExpCalendar setMarkSelectedDateCustomGradientDrawable(CustomGradientDrawable customGradientDrawable) {
        selectedCustomGradientDrawableMark = customGradientDrawable;
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, CustomGradientDrawable drawable) {
        marks.refreshCustomMark(dateTime, PersianCustomMarks.Custom, true, 0, drawable);
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, PersianCustomMarks persianCustomMarks, int color) {
        if (persianCustomMarks != PersianCustomMarks.Custom)
            marks.refreshCustomMark(dateTime, persianCustomMarks, true, color, null);
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, PersianCustomMarks persianCustomMarks) {
        if (persianCustomMarks != PersianCustomMarks.Custom)
            marks.refreshCustomMark(dateTime, persianCustomMarks, true,
                    persianCustomMarks == PersianCustomMarks.SmallOval_Bottom ? Color.parseColor("#AAFF3333") : Color.parseColor("#0095f3"), null);
        return this;
    }

    public PersianHorizontalExpCalendar clearMarks() {
        marks.clear();
        return this;
    }

    ///------------------------ Animations Class start ---------------------------
    protected class Animations {

        protected CalendarAnimation decreasingAlphaAnimation;
        protected CalendarAnimation increasingAlphaAnimation;
        protected CalendarAnimation decreasingSizeAnimation;
        protected CalendarAnimation increasingSizeAnimation;
        protected int expandedTopMargin;
        protected int collapsedTopMargin;

        protected AnimationsListener animationsListener;
        protected Context context;
        protected int extraTopMarginOffset;

        public Animations(Context context, AnimationsListener animationsListener, int extraTopMarginOffset) {
            this.context = context;
            this.animationsListener = animationsListener;
            this.extraTopMarginOffset = extraTopMarginOffset;
            initAnimation();
        }

        public void unbind() {
            this.context = null;
            this.animationsListener = null;
        }

        public void initAnimation() {
            decreasingAlphaAnimation = new CalendarAnimation();
            decreasingAlphaAnimation.setFloatValues(ANIMATION_DECREASING_VALUES[0], ANIMATION_DECREASING_VALUES[1]);
            decreasingAlphaAnimation.setDuration(ANIMATION_ALPHA_DURATION);

            increasingAlphaAnimation = new CalendarAnimation();
            increasingAlphaAnimation.setFloatValues(ANIMATION_INCREASING_VALUES[0], ANIMATION_INCREASING_VALUES[1]);
            increasingAlphaAnimation.setDuration(ANIMATION_ALPHA_DURATION);

            decreasingSizeAnimation = new CalendarAnimation();
            decreasingSizeAnimation.setFloatValues(ANIMATION_DECREASING_VALUES[0], ANIMATION_DECREASING_VALUES[1]);
            decreasingSizeAnimation.setDuration(ANIMATION_SIZE_DURATION);

            increasingSizeAnimation = new CalendarAnimation();
            increasingSizeAnimation.setFloatValues(ANIMATION_INCREASING_VALUES[0], ANIMATION_INCREASING_VALUES[1]);
            increasingSizeAnimation.setDuration(ANIMATION_SIZE_DURATION);

            expandedTopMargin = 0;
            collapsedTopMargin = 0;
        }

        public void startHidePagerAnimation() {
            if (animationsListener == null) {
                return;
            }
            decreasingAlphaAnimation.setListener(new SmallAnimationListener() {
                @Override
                public void animationStart(Animator animation) {
                    if (utils.isMonthView()) {
                        animationsListener.setMonthPagerVisibility(View.GONE);
                        animationsListener.setWeekPagerVisibility(View.VISIBLE);
                    } else {
                        animationsListener.setMonthPagerVisibility(View.VISIBLE);
                        animationsListener.setWeekPagerVisibility(View.GONE);
                    }

                    animationsListener.setAnimatedContainerVisibility(View.VISIBLE);
                    addCellsToAnimateContainer();
                    expandedTopMargin = cellHeight * (utils.getWeekOfMonth(getAnimateContainerDate()) -
                            1 + utils.dayLabelExtraRow()) + extraTopMarginOffset;
                    collapsedTopMargin = cellHeight * (utils.dayLabelExtraRow());
                    animationsListener.setTopMarginToAnimContainer((utils.isMonthView() ? collapsedTopMargin : expandedTopMargin));
                }

                @Override
                public void animationEnd(Animator animation) {
                    animationsListener.setMonthPagerVisibility(View.GONE);
                    animationsListener.setWeekPagerVisibility(View.GONE);
                    clearAnimationsListener();
                    if (utils.isMonthView()) {
                        startIncreaseSizeAnimation();
                    } else {
                        startDecreaseSizeAnimation();
                    }
                }

                @Override
                public void animationUpdate(Object value) {
                    if (utils.isMonthView()) {
                        animationsListener.setWeekPagerAlpha((float) value);
                    } else {
                        animationsListener.setMonthPagerAlpha((float) value);
                    }
                }
            });
        }

        public void startShowPagerAnimation() {
            increasingAlphaAnimation.setListener(new SmallAnimationListener() {
                @Override
                public void animationStart(Animator animation) {
                    if (utils.isMonthView()) {
                        animationsListener.setMonthPagerVisibility(View.VISIBLE);
                        animationsListener.setWeekPagerVisibility(View.GONE);
                    } else {
                        animationsListener.setMonthPagerVisibility(View.GONE);
                        animationsListener.setWeekPagerVisibility(View.VISIBLE);
                    }

                    if (utils.isMonthView()) {
                        if (utils.isTheSameWeekToScrollDate(selectionDate))
                            scrollDate = selectionDate;
                        else
                            persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfMonth(1));
                    }else{
                        if (SCROLL_TO_SELECTED_AFTER_COLLAPSE && utils.isTheSameMonthToScrollDate(selectionDate))
                            scrollDate = selectionDate;
                        else {
                            scrollDate = scrollDate.withDayOfMonth(1);
                            persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfWeek(1));
                        }
                    }

                    if (utils.isMonthView()) {
                        animationsListener.scrollToDate(scrollDate, true, false, false);
                        animationsListener.setHeightToCenterContainer(monthViewPagerHeight);
                        animationsListener.changeViewPager(PersianViewPagerType.MONTH);
                    } else {
                        animationsListener.scrollToDate(scrollDate, false, true, false);
                        animationsListener.setHeightToCenterContainer(weekViewPagerHeight);
                        animationsListener.changeViewPager(PersianViewPagerType.WEEK);
                    }
                    animationsListener.updateMarks();
                }


                @Override
                public void animationEnd(Animator animation) {
                    clearAnimationsListener();
                    animationsListener.setAnimatedContainerVisibility(View.GONE);
                    animationsListener.animateContainerRemoveViews();
                    if (utils.isMonthView()) {
                        animationsListener.setMonthPagerVisibility(View.VISIBLE);
                        animationsListener.setWeekPagerVisibility(View.GONE);
                    } else {
                        animationsListener.setMonthPagerVisibility(View.GONE);
                        animationsListener.setWeekPagerVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void animationUpdate(Object value) {
                    if (utils.isMonthView()) {
                        animationsListener.setMonthPagerAlpha((float) value);
                    } else {
                        animationsListener.setWeekPagerAlpha((float) value);
                    }
                }
            });
        }

        public void startDecreaseSizeAnimation() {
            decreasingSizeAnimation.setListener(new SmallAnimationListener() {
                @Override
                public void animationStart(Animator animation) {
                    animationsListener.setHeightToCenterContainer(monthViewPagerHeight);
                }

                @Override
                public void animationEnd(Animator animation) {
                    animationsListener.setHeightToCenterContainer(weekViewPagerHeight);
                    clearAnimationsListener();
                    startShowPagerAnimation();
                }

                @Override
                public void animationUpdate(Object value) {
                    animationsListener.setHeightToCenterContainer(getAnimationCenterContainerHeight((float) value));
                    animationsListener.setTopMarginToAnimContainer(
                            (int) ((expandedTopMargin - collapsedTopMargin) * (float) value) + collapsedTopMargin);
                }
            });
        }

        public void startIncreaseSizeAnimation() {
            increasingSizeAnimation.setListener(new SmallAnimationListener() {
                @Override
                public void animationStart(Animator animation) {
                    animationsListener.setHeightToCenterContainer(weekViewPagerHeight);
                }

                @Override
                public void animationEnd(Animator animation) {
                    animationsListener.setHeightToCenterContainer(monthViewPagerHeight);
                    clearAnimationsListener();
                    startShowPagerAnimation();
                }

                @Override
                public void animationUpdate(Object value) {
                    animationsListener.setHeightToCenterContainer(getAnimationCenterContainerHeight((float) value));
                    animationsListener.setTopMarginToAnimContainer(
                            (int) ((expandedTopMargin - collapsedTopMargin) * (float) value) + collapsedTopMargin);
                }
            });
        }

        public void swithWithoutAnimation() {
            animationsListener.setMonthPagerVisibility(utils.isMonthView() ? View.VISIBLE : View.GONE);
            animationsListener.setWeekPagerVisibility(utils.isMonthView() ? View.GONE : View.VISIBLE);

            if (utils.isMonthView()) {
                if (utils.isTheSameWeekToScrollDate(selectionDate))
                    scrollDate = selectionDate;
                else
                    persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfMonth(1));
            } else {
                if (SCROLL_TO_SELECTED_AFTER_COLLAPSE && utils.isTheSameMonthToScrollDate(selectionDate))
                    scrollDate = selectionDate;
                else {
                    scrollDate = scrollDate.withDayOfMonth(1);
                    persianHorizontalExpCalListener.onCalendarScroll(scrollDate.withDayOfWeek(1));
                }
            }

            if (utils.isMonthView()) {
                animationsListener.setHeightToCenterContainer(monthViewPagerHeight);
                animationsListener.scrollToDate(scrollDate, true, false, false);
                animationsListener.changeViewPager(PersianViewPagerType.MONTH);
            } else {
                animationsListener.setHeightToCenterContainer(weekViewPagerHeight);
                animationsListener.scrollToDate(scrollDate, false, true, false);
                animationsListener.changeViewPager(PersianViewPagerType.WEEK);
            }
            animationsListener.updateMarks();
        }

        public int getAnimationCenterContainerHeight(float value) {
            return (int) ((((monthViewPagerHeight - weekViewPagerHeight) * value)) + weekViewPagerHeight);
        }

        public void clearAnimationsListener() {
            decreasingAlphaAnimation.removeAllListeners();
            increasingAlphaAnimation.removeAllListeners();
            decreasingSizeAnimation.removeAllListeners();
            increasingSizeAnimation.removeAllListeners();
        }

        protected DateTime getAnimateContainerDate() {
            if (!utils.isMonthView()) {
                if (utils.isTheSameMonthToScrollDate(selectionDate)) {
                    return selectionDate;
                } else {
                    return scrollDate;
                }
            } else {
                if (utils.isTheSameWeekToScrollDate(selectionDate)) {
                    return selectionDate;
                } else {
                    return scrollDate;
                }
            }
        }

        public void addCellsToAnimateContainer() {
            animationsListener.animateContainerRemoveViews();

            DateTime animateInitDate = getAnimateContainerDate().minusDays(utils.firstDayOffset()).withDayOfWeek(1);

            for (int d = 0; d < 7; d++) {
                DateTime cellDate = animateInitDate.plusDays(d + utils.firstDayOffset());

                DayCellView dayCellView = new DayCellView(context);

                GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(d));
                cellParams.height = cellHeight;
                cellParams.width = cellWidth;
                dayCellView.setLayoutParams(cellParams);
                dayCellView.setDayNumber(cellDate.getDayOfMonth());
                dayCellView.setDayType(utils.isWeekendByColumnNumber(d) ? DayType.WEEKEND : DayType.NO_WEEKEND);
                dayCellView.setTimeType(getTimeType(cellDate));
                dayCellView.setMark(marks.getMark(cellDate), cellHeight);

                animationsListener.animateContainerAddView(dayCellView);
            }
        }

        protected TimeType getTimeType(DateTime cellTime) {
            if (cellTime.getMonthOfYear() < scrollDate.getMonthOfYear()) {
                return TimeType.PAST;
            } else if (cellTime.getMonthOfYear() > scrollDate.getMonthOfYear()) {
                return TimeType.FUTURE;
            } else {
                return TimeType.CURRENT;
            }
        }
    }
    ///------------------------ Animations Class end ---------------------------

    ///------------------------ CalendarAdapter Class start ---------------------------
    protected class CalendarAdapter extends PagerAdapter {

        protected PageViewListener pageViewListener;
        protected PersianViewPagerType persianViewPagerType;
        protected List<PageView> visiblePages;
        protected Context context;

        public CalendarAdapter(Context context, PersianViewPagerType persianViewPagerType, PageViewListener pageViewListener) {
            this.pageViewListener = pageViewListener;
            this.persianViewPagerType = persianViewPagerType;
            this.visiblePages = new ArrayList<>();
            this.context = context;
        }

        @Override
        public int getCount() {
            switch (persianViewPagerType) {
                case MONTH:
                    return utils.monthPositionFromDate(END_DATE);
                case WEEK:
                    return utils.weekPositionFromDate(END_DATE);
                default:
                    return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageView pageView = new PageView(context, persianViewPagerType, pageViewListener);
            if (use_RTL_direction)
                pageView.setRotationY(180);

            visiblePages.add(pageView);

            container.addView(pageView, 0);

            switch (persianViewPagerType) {
                case MONTH:
                    pageView.setup(utils.getDateByMonthPosition(position));
                    break;
                case WEEK:
                    pageView.setup(utils.getDateByWeekPosition(position));
                    break;
                default:
                    Log.e(CalendarAdapter.class.getName(), "instantiateItem, unknown view pager type");
            }

            return pageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            visiblePages.remove(object);
            container.removeView((PageView) object);
        }

        public void updateMarks() {
            for (PageView pageView : visiblePages) {
                pageView.updateMarks();
            }
        }
    }
    ///------------------------ CalendarAdapter Class end ---------------------------

    ///------------------------ PageView Class start ---------------------------
    protected class PageView extends FrameLayout implements View.OnClickListener {

        protected PageViewListener pageViewListener;
        protected GridLayout gridLayout;
        protected DateTime pageDate;

        protected PersianViewPagerType persianViewPagerType;
        protected int rows;

        public PageView(Context context) {
            this(context, null, null);
        }

        public PageView(Context context, PersianViewPagerType persianViewPagerType, PageViewListener pageViewListener) {
            super(context);
            if (pageViewListener != null) {
                this.pageViewListener = pageViewListener;
            }
            if (persianViewPagerType != null) {
                this.persianViewPagerType = persianViewPagerType;
                this.rows = persianViewPagerType == PersianViewPagerType.MONTH ? MONTH_ROWS : WEEK_ROWS;
                init();
            }
        }

        protected void init() {
            initViews();
        }

        protected void initViews() {
            inflate(getContext(), R.layout.page_view, this);
            gridLayout = (GridLayout) findViewById(R.id.grid_layout);
            gridLayout.setColumnCount(COLUMNS);
            gridLayout.setRowCount(rows + (utils.dayLabelExtraRow()));
        }

        public void setup(DateTime pageDate) {
            this.pageDate = pageDate;
            addCellsToGrid();
        }

        protected void addCellsToGrid() {
            DateTime cellDate;
            if (persianViewPagerType == PersianViewPagerType.MONTH) {
                cellDate = pageDate.withDayOfMonth(1).plusDays(-pageDate.withDayOfMonth(1).getDayOfWeek() + 1 + utils.firstDayOffset());
            } else {
                cellDate = pageDate.plusDays(-pageDate.getDayOfWeek() + 1 + utils.firstDayOffset());
            }
            addLabels();
            addDays(cellDate);
        }

        protected void addDays(DateTime cellDate) {
            for (int r = utils.dayLabelExtraRow(); r < rows + (utils.dayLabelExtraRow()); r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    DayCellView dayCellView = new DayCellView(getContext());

                    GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));
                    cellParams.height = cellHeight;
                    cellParams.width = cellWidth;
                    dayCellView.setTag(cellDate);
                    dayCellView.setLayoutParams(cellParams);
                    dayCellView.setDayNumber(cellDate.getDayOfMonth());
                    dayCellView.setDayType(utils.isWeekendByColumnNumber(c) ? DayType.WEEKEND : DayType.NO_WEEKEND);
                    dayCellView.setOnClickListener(this);
                    if (persianViewPagerType == PersianViewPagerType.MONTH)
                        dayCellView.setTimeType(getTimeType(cellDate));
                    else
                        ((TextView) dayCellView.findViewById(R.id.text)).setTextColor(daysTextColorCurrentMonth);

                    dayCellView.setMark(marks.getMark(cellDate), cellHeight);

                    if (daysTypeface != null)
                        ((TextView) dayCellView.findViewById(R.id.text)).setTypeface(daysTypeface);
                    if (daysTextSize > 0)
                        ((TextView) dayCellView.findViewById(R.id.text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, daysTextSize);


                    gridLayout.addView(dayCellView);
                    cellDate = cellDate.plusDays(1);
                }
            }
        }

        protected void addLabels() {
            if (USE_DAY_LABELS) {
                LinearLayout lable_cell_linearlayout = (LinearLayout) getRootView().findViewById(R.id.lable_cell_linearlayout);
                lable_cell_linearlayout.setVisibility(VISIBLE);

                GridLayout lable_gridLayout = (GridLayout) getRootView().findViewById(R.id.lable_cell_grid_layout);
                lable_gridLayout.setColumnCount(COLUMNS);
                lable_gridLayout.setRowCount(1);

                for (int l = 0; l < COLUMNS; l++) {
                    LabelCellView label = new LabelCellView(getContext());

                    GridLayout.LayoutParams labelParams = new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(l));
                    labelParams.height = cellHeight;
                    labelParams.width = cellWidth;
                    label.setLayoutParams(labelParams);
                    label.setText(use_RTL_direction ? NAME_OF_DAYS_RTL[(l + utils.firstDayOffset()) % 7] : NAME_OF_DAYS_LTR[(l + utils.firstDayOffset()) % 7]);
                    label.setDayType(utils.isWeekendByColumnNumber(l) ? DayType.WEEKEND : DayType.NO_WEEKEND);

                    ((TextView) label.findViewById(R.id.text)).setTypeface(dayLablesTypeface);
                    ((TextView) label.findViewById(R.id.text)).setTextColor(dayLablesTextColor);
                    if (dayLablesTextSize > 0)
                        ((TextView) label.findViewById(R.id.text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, dayLablesTextSize);

                    lable_gridLayout.addView(label);
                }
            }
        }

        protected TimeType getTimeType(DateTime cellTime) {
            if (cellTime.getMonthOfYear() < pageDate.getMonthOfYear()) {
                return TimeType.PAST;
            } else if (cellTime.getMonthOfYear() > pageDate.getMonthOfYear()) {
                return TimeType.FUTURE;
            } else {
                return TimeType.CURRENT;
            }
        }

        public void updateMarks() {
            for (int c = utils.dayLabelExtraChildCount(); c < gridLayout.getChildCount(); c++) {
                DayCellView dayCellView = (DayCellView) gridLayout.getChildAt(c);
                dayCellView.setMarkSetup(marks.getMark((DateTime) dayCellView.getTag()));
            }
        }

        @Override
        public void onClick(View view) {
            if (pageViewListener != null) {
                pageViewListener.onDayClick((DateTime) view.getTag());
            }
        }
    }
    ///------------------------ PageView Class end ---------------------------

    ///------------------------ DayCellView Class start ---------------------------
    protected class DayCellView extends BaseCellView {
        protected TextView text;
        protected TimeType timeType;
        protected FrameLayout markContainer;

        protected MarkSetup markSetup;
        protected View markToday;
        protected View markSelected;
        protected View markSmallOval_Bottom;
        protected View markVerticalLine_Right;
        protected View newView = null;

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

        protected void init() {
            initView();
        }

        protected void initView() {
            inflate(getContext(), R.layout.day_cell_view, this);

            text = (TextView) findViewById(R.id.text);
            markContainer = (FrameLayout) findViewById(R.id.mark_container);
            markToday = findViewById(R.id.mark_today_view);
            markSelected = findViewById(R.id.mark_selected_view);
            markSmallOval_Bottom = findViewById(R.id.mark_custom1);
            markVerticalLine_Right = findViewById(R.id.mark_custom2);

            text.setTextColor(daysTextColorCurrentMonth);
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

        protected void setTextColorByTimeType(int currentMonthColor, int anotherMonthColor) {
            if (this.timeType == TimeType.CURRENT)
                text.setTextColor(currentMonthColor);
            else
                text.setTextColor(anotherMonthColor);
        }

        public void setMark(MarkSetup markSetup, int size) {
            setSize(size);
            setMarkSetup(markSetup);
        }

        protected void setSize(int size) {
            LayoutParams markParams = (LayoutParams) markContainer.getLayoutParams();
            markParams.height = size;
            markParams.width = size;

            setupSmallOval_BottomMark(size);
            setupVerticalLine_RightMark(size);
        }

        protected void setupSmallOval_BottomMark(int size) {
            LayoutParams markCustomParams = (LayoutParams) markSmallOval_Bottom.getLayoutParams();
            int markCustomPercentSize = (int) (size * marks.MARK_SmallOval_Bottom_SIZE_PROPORTION_TO_CELL);
            markCustomParams.height = markCustomPercentSize;
            markCustomParams.width = markCustomPercentSize;
        }

        protected void setupVerticalLine_RightMark(int size) {
            LayoutParams markCustomParams = (LayoutParams) markVerticalLine_Right.getLayoutParams();
            markCustomParams.height = (int) (size * marks.MARK_VerticalLine_Right_HEIGHT_PROPORTION_TO_CELL);
            markCustomParams.width = (int) (size * marks.MARK_VerticalLine_Right_WIDTH_PROPORTION_TO_CELL);
        }

        public void setMarkSetup(MarkSetup markSetup) {
            this.markSetup = markSetup;
            setMarkToView();
        }

        protected void setMarkToView() {
            boolean is_CurrentTime = this.timeType == TimeType.CURRENT | CELL_TEXT_SHOW_MARKS_ANOTHER_MONTH | currentViewPager == PersianViewPagerType.WEEK;

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
                    text.setTextColor(CELL_TEXT_CURRENT_MONTH_TODAY_COLOR);

                if (markToday.getVisibility() == VISIBLE && todayCustomGradientDrawableMark != null) {
                    markToday.setBackground(todayCustomGradientDrawableMark);
                    text.setTextColor(todayCustomGradientDrawableMark.getTextColor());
                }

                if (markSelected.getVisibility() == VISIBLE && selectedCustomGradientDrawableMark != null) {
                    markSelected.setBackground(selectedCustomGradientDrawableMark);
                    text.setTextColor(selectedCustomGradientDrawableMark.getTextColor());
                }

                if (markSetup.isCustomGradientDrawable() && is_CurrentTime && markSetup.getCustomGradientDrawableMark() != null)
                    addNewView(markSetup.getCustomGradientDrawableMark());

                if (markSetup.isSmallOval_Bottom())
                    changeSmallOval_BottomBackgroundColor(markSetup.getSmallOval_BottomColor());

                if (markSetup.isVerticalLine_Right())
                    changeVerticalLine_RightBackgroundColor(markSetup.getVerticalLine_RightColor());
            }
        }

        protected void addNewView(CustomGradientDrawable customGradientDrawable) {
            if (newView != null)
                markContainer.removeView(newView);

            newView = new View(getContext());
            newView.setLayoutParams(
                    new LayoutParams(
                            customGradientDrawable.getLayoutWidth(),
                            customGradientDrawable.getLayoutHeight(),
                            customGradientDrawable.getLayoutGravity()));

            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) newView.getLayoutParams();
            int[] margins = customGradientDrawable.getMargins();
            p.setMargins(intToDp(margins[0]), intToDp(margins[1]), intToDp(margins[2]), intToDp(margins[3]));

            text.setTextColor(customGradientDrawable.getTextColor());

            newView.setBackground(customGradientDrawable);
            markContainer.addView(newView);
        }

        protected int intToDp(int value) {
            float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (value * scale + 0.5f);
        }

        protected void changeSmallOval_BottomBackgroundColor(int color) {
            markSmallOval_Bottom.setBackground(new CustomGradientDrawable(GradientDrawable.OVAL, color));
        }

        protected void changeVerticalLine_RightBackgroundColor(int color) {
            markVerticalLine_Right.setBackground(new CustomGradientDrawable(GradientDrawable.RECTANGLE, color));
        }
    }
    ///------------------------ DayCellView Class end ---------------------------

    ///------------------------ Marks Class start ---------------------------
    protected class Marks {

        protected Map<String, MarkSetup> marksMap;
        protected boolean locked = false;

        public final float MARK_SmallOval_Bottom_SIZE_PROPORTION_TO_CELL = 0.15f;
        public final float MARK_VerticalLine_Right_HEIGHT_PROPORTION_TO_CELL = 0.4f;
        public final float MARK_VerticalLine_Right_WIDTH_PROPORTION_TO_CELL = 0.08f;

        public void init() {
            marksMap = new HashMap<>();
        }

        public void markToday() {
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

        public void refreshMarkSelected(DateTime newSelection, boolean can_mark_selected_day) {
            if (isLocked())
                return;
            lock();
            MarkSetup oldSelectionSetup = getMark(selectionDate);
            if (oldSelectionSetup != null) {
                oldSelectionSetup.setSelected(false);
                if (oldSelectionSetup.canBeDeleted())
                    marksMap.remove(dateTimeToStringKey(selectionDate));
            }

            if (can_mark_selected_day) {
                MarkSetup newSelectionSetup = getMark(newSelection);
                if (newSelectionSetup == null) {
                    newSelectionSetup = new MarkSetup();
                    marksMap.put(dateTimeToStringKey(newSelection), newSelectionSetup);
                }
                newSelectionSetup.setSelected(true);
            }

            selectionDate = newSelection;
            unlock();
        }

        public void refreshCustomMark(DateTime dateTime, PersianCustomMarks persianCustomMarks, boolean mark, int color, CustomGradientDrawable customGradientDrawableMark) {
            if (isLocked())
                return;
            lock();
            MarkSetup markSetup = getMark(dateTime);
            if (markSetup == null) {
                markSetup = new MarkSetup();
                addNewMark(dateTime, markSetup);
            }
            switch (persianCustomMarks) {
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

        public void clear() {
            marksMap.clear();
        }

        protected void addNewMark(DateTime dateTime, MarkSetup markSetup) {
            marksMap.put(dateTimeToStringKey(dateTime), markSetup);
        }

        public MarkSetup getMark(DateTime dateTime) {
            return marksMap.get(dateTimeToStringKey(dateTime));
        }

        protected String dateTimeToStringKey(DateTime dateTime) {
            return dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth();
        }

        public void lock() {
            locked = true;
        }

        public void unlock() {
            locked = false;
        }

        public boolean isLocked() {
            return locked;
        }
    }
    ///------------------------ Marks Class end ---------------------------

    ///------------------------ Utils Class start ---------------------------
    protected class Utils {

        public int monthPositionFromDate(DateTime dateTo) {
            DateTime dateFrom = START_DATE.withDayOfWeek(7);
            return ((dateTo.getYear() - dateFrom.getYear()) * 12) + (dateTo.getMonthOfYear() - dateFrom.getMonthOfYear());
        }

        public DateTime getDateByMonthPosition(int position) {
            return START_DATE.withDayOfWeek(7 + firstDayOffset()).plusMonths(position);
        }

        public int weekPositionFromDate(DateTime dateTo) {
            DateTime dt = dateTo.minusDays(firstDayOffset())
                    .withHourOfDay(START_DATE.getHourOfDay())
                    .withMinuteOfHour(START_DATE.getMinuteOfHour())
                    .withSecondOfMinute(START_DATE.getSecondOfMinute())
                    .withMillisOfSecond(START_DATE.getMillisOfSecond());

            return Weeks.weeksBetween(START_DATE, dt).getWeeks();
        }

        public DateTime getDateByWeekPosition(int position) {
            return START_DATE.withDayOfWeek(7 + firstDayOffset()).plusWeeks(position);
        }

        public boolean isWeekendByColumnNumber(int column) {
            switch (FIRST_DAY_OF_WEEK) {
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

        public int getRandomColor() {
            return new Random().nextInt(40) + 215;
        }

        public int dayLabelExtraRow() {
            return USE_DAY_LABELS ? 1 : 0;
        }

        public int dayLabelExtraChildCount() {
            return 0;//USE_DAY_LABELS ? 7 : 0;
        }

        public boolean isMonthView() {
            return currentViewPager == PersianViewPagerType.MONTH;
        }

        public boolean isTheSameMonthToScrollDate(DateTime dateTime) {
            return isTheSameMonth(scrollDate, dateTime);
        }

        public boolean isTheSameMonth(DateTime dateTime1, DateTime dateTime2) {
            return (dateTime1.getYear() == dateTime2.getYear()) && (dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear());
        }

        public boolean isTheSameWeekToScrollDate(DateTime dateTime) {
            return isTheSameWeek(scrollDate, dateTime);
        }

        public boolean isTheSameWeek(DateTime dateTime1, DateTime dateTime2) {
            DateTime firstDateMovedByFirstDayOfWeek = dateTime1.minusDays(firstDayOffset());
            DateTime secondDateMovedByFirstDayOfWeek = dateTime2.minusDays(firstDayOffset());
            return (firstDateMovedByFirstDayOfWeek.getYear() == secondDateMovedByFirstDayOfWeek.getYear()) &&
                    (firstDateMovedByFirstDayOfWeek.getWeekOfWeekyear() == secondDateMovedByFirstDayOfWeek.getWeekOfWeekyear());
        }

        public int firstDayOffset() {
            switch (FIRST_DAY_OF_WEEK) {
                case SATDAY:
                    return 0;
                case SUNDAY:
                    return -1;
                case MONDAY:
                    return 0;
            }
            return 0;
        }

        public int getWeekOfMonth(DateTime dateTime) {
            return ((dateTime.getDayOfMonth() + dateTime.withDayOfMonth(1).getDayOfWeek() - 2 - firstDayOffset()) / 7) + 1;
        }

        public int animateContainerExtraTopOffset(Resources resources) {
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

        public int animateContainerExtraSideOffset(Resources resources) {
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
    ///------------------------ Utils Class end ---------------------------

    ///------------------------ BaseCellView Class start ---------------------------
    protected abstract class BaseCellView extends FrameLayout {

        protected DayType dayType;

        public BaseCellView(Context context) {
            super(context);
        }

        public BaseCellView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BaseCellView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        protected void setTextBackgroundByDayType() {
            if (this.dayType == DayType.WEEKEND) {
                setBackgroundColor(CELL_WEEKEND_BACKGROUND);
            } else {
                setBackgroundColor(CELL_NON_WEEKEND_BACKGROUND);
            }
        }
    }
    ///------------------------ BaseCellView Class end ---------------------------

    ///------------------------ LabelCellView Class start ---------------------------
    protected class LabelCellView extends BaseCellView {

        protected TextView text;

        public LabelCellView(Context context) {
            super(context);
            init();
        }

        public LabelCellView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public LabelCellView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        protected void init() {
            initView();
        }

        protected void initView() {
            inflate(getContext(), R.layout.label_cell_view, this);
            text = (TextView) findViewById(R.id.text);
        }

        public void setText(String text) {
            this.text.setText(text);
        }

        public void setDayType(DayType dayType) {
            this.dayType = dayType;
            setTextBackgroundByDayType();
        }
    }
    ///------------------------ LabelCellView Class end ---------------------------
}