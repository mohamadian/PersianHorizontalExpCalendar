package com.mohamadian.persianhorizontalexpcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.adapter.CalendarAdapter;
import com.mohamadian.persianhorizontalexpcalendar.common.Animations;
import com.mohamadian.persianhorizontalexpcalendar.common.Config;
import com.mohamadian.persianhorizontalexpcalendar.common.Constants;
import com.mohamadian.persianhorizontalexpcalendar.common.Marks;
import com.mohamadian.persianhorizontalexpcalendar.common.Utils;
import com.mohamadian.persianhorizontalexpcalendar.listener.SmallPageChangeListener;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.CustomGradientDrawable;
import com.mohamadian.persianhorizontalexpcalendar.view.page.PageView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class PersianHorizontalExpCalendar extends LinearLayout implements PageView.PageViewListener, Animations.AnimationsListener {

    private boolean ifExpand = true;
    private TextView titleTextView;
    private TextView todayButton;
    private RelativeLayout centerContainer;
    private GridLayout animateContainer;

    private int expandResID = R.drawable.icon_arrow_down;
    private int collapseResID = R.drawable.icon_arrow_up;

    private ViewPager monthViewPager;
    private CalendarAdapter monthPagerAdapter;

    private ViewPager weekViewPager;
    private CalendarAdapter weekPagerAdapter;

    private Animations animations;
    private PersianHorizontalExpCalListener persianHorizontalExpCalListener;

    private Typeface dayLablesTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/GanjNamehSans-Regular.ttf");
    private int dayLablesTextColor = Color.parseColor("#b9b9b9");
    private int dayLablesTextSize = -1;

    private Typeface daysTypeface = null;
    private int daysTextColorCurrentMonth = Config.CELL_TEXT_CURRENT_MONTH_COLOR;
    private int daysTextColorAnotherMonth = Config.CELL_TEXT_ANOTHER_MONTH_COLOR;
    private int daysTextSize = -1;

    private boolean use_RTL_direction = true;
    private boolean can_mark_today = true;
    private boolean can_mark_selected_day = true;

    private boolean lock;
    private boolean needToFireOnDateSelectedEventAfterScrollToDate = false;

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

    public PersianHorizontalExpCalendar setPersianHorizontalExpCalListener(PersianHorizontalExpCalListener persianHorizontalExpCalListener) {
        this.persianHorizontalExpCalListener = persianHorizontalExpCalListener;
        return this;
    }

    private void setRootLayoutDirection(){
        if (android.os.Build.VERSION.SDK_INT >= 17)
            findViewById(R.id.root_linearlayout).setLayoutDirection(use_RTL_direction? LAYOUT_DIRECTION_RTL : LAYOUT_DIRECTION_LTR);
    }

    public PersianHorizontalExpCalendar removeHorizontalExpCalListener() {
        this.persianHorizontalExpCalListener = null;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        animations.unbind();
        Marks.clear();
        super.onDetachedFromWindow();
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.persian_horizontal_exp_calendar, this);

        centerContainer = (RelativeLayout) findViewById(R.id.center_container);
        lock = false;

        setValuesFromAttr(attributeSet);
        setupCellWidth();

        Marks.init();
        if (can_mark_today)
            Marks.markToday();
        Marks.refreshMarkSelected(Config.selectionDate, can_mark_selected_day);

        initAnimation();
    }

    private void setCellHeight() {
        Config.cellHeight = Config.monthViewPagerHeight / (Config.MONTH_ROWS + Utils.dayLabelExtraRow());
    }

    private void setupCellWidth() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PersianHorizontalExpCalendar.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Config.cellWidth = getMeasuredWidth() / Config.COLUMNS;
                setupViews();
            }
        });
    }

    private void setValuesFromAttr(AttributeSet attributeSet) {
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

        setHeightToCenterContainer(Utils.isMonthView() ? Config.monthViewPagerHeight : Config.weekViewPagerHeight);
    }

    @SuppressLint("WrongViewCast")
    private void setupBottomContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_bottom_container_height)) {
            ((LinearLayout.LayoutParams) findViewById(R.id.bottom_container).getLayoutParams()).height =
                    typedArray.getDimensionPixelSize(R.styleable.PersianHorizontalExpCalendar_bottom_container_height,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private void setupMiddleContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_center_container_expanded_height)) {
            Config.monthViewPagerHeight = typedArray.getDimensionPixelSize(
                    R.styleable.PersianHorizontalExpCalendar_center_container_expanded_height, LinearLayout.LayoutParams.WRAP_CONTENT);
            setCellHeight();
            Config.weekViewPagerHeight = Config.cellHeight * (Config.USE_DAY_LABELS ? 2 : 1);
        }
    }

    private void setupInitialViewFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_initial_view)) {
            Config.ViewPagerType nextView = typedArray.getString(R.styleable.PersianHorizontalExpCalendar_initial_view).equalsIgnoreCase("WEEK") ? Config.ViewPagerType.WEEK : Config.ViewPagerType.MONTH;;
            Config.currentViewPager = nextView;
            ifExpand = Config.currentViewPager == Config.ViewPagerType.MONTH;
            ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
            expandImage.setImageResource(ifExpand ? collapseResID : expandResID);
        }
    }

    private void setupRangeMonthsBeforeAfterFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_range_months_before_init))
            if (typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_before_init,12*100) > 0)
                Config.START_DATE = Config.getStartDate(typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_before_init,12*100));

        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_range_months_after_init))
            if (typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_after_init,12*100) > 0)
                Config.END_DATE = Config.getEndDate(typedArray.getInteger(R.styleable.PersianHorizontalExpCalendar_range_months_after_init,12*100));
    }

    private void setupMarksFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_mark_today))
            can_mark_today = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_mark_today, true);

        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_mark_selected_day))
            can_mark_selected_day = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_mark_selected_day, true);
    }

    private void setupUseRTLDirectionFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_use_RTL_direction))
            use_RTL_direction = typedArray.getBoolean(R.styleable.PersianHorizontalExpCalendar_use_RTL_direction, true);
    }

    private void setupTopContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PersianHorizontalExpCalendar_top_container_height)) {
            ((LinearLayout.LayoutParams) findViewById(R.id.top_container).getLayoutParams()).height =
                    typedArray.getDimensionPixelSize(R.styleable.PersianHorizontalExpCalendar_top_container_height,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initAnimation() {
        animations = new Animations(getContext(), PersianHorizontalExpCalendar.this, Utils.animateContainerExtraTopOffset(getResources()));
    }

    private void setupViews() {
        initCenterContainer();
        initBottomContainer();
        initAnimateContainer();
        refreshTitleTextView();
        refreshTodayButtonTitle();
    }

    private void initAnimateContainer() {
        animateContainer = (GridLayout) findViewById(R.id.animate_container);
        animateContainer.getLayoutParams().height = Config.cellHeight;
        int sideMargin = Utils.animateContainerExtraSideOffset(getResources());
        animateContainer.setPadding(sideMargin, 0, sideMargin, 0);
    }

    private void initTopContainer() {
        titleTextView = (TextView) findViewById(R.id.title);
        todayButton = (Button)findViewById(R.id.scroll_to_today_button);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/GanjNamehSans-Regular.ttf");
        titleTextView.setTypeface(font);
        todayButton.setTypeface(font);

        findViewById(R.id.scroll_to_today_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocked()) {
                    lock();
                    DateTime now = new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran")));

                    Marks.refreshMarkSelected(now,can_mark_selected_day);
                    updateMarks();

                    Config.selectionDate = now;
                    scrollToDate(now, true, true, true);
                    unlock();
                }
            }
        });
    }

    private void initCenterContainer() {
        initMonthViewPager();
        initWeekViewPager();
    }

    private void initBottomContainer() {
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

    private void switchToView(final Config.ViewPagerType switchTo) {
        Config.currentViewPager = switchTo;
        if (Constants.ANIMATION_ALPHA_DURATION+ Constants.ANIMATION_SIZE_DURATION > 0){
            animations.clearAnimationsListener();
            animations.startHidePagerAnimation();
        }else
            animations.swithWithoutAnimation();
    }

    private void setStyle(CalendarAdapter adapter)
    {
        adapter.dayLablesTypeface = dayLablesTypeface;
        adapter.dayLablesTextColor = dayLablesTextColor;
        adapter.dayLablesTextSize = dayLablesTextSize;

        adapter.daysTypeface = daysTypeface;
        adapter.daysTextColorCurrentMonth = daysTextColorCurrentMonth;
        adapter.daysTextColorAnotherMonth = daysTextColorAnotherMonth;
        adapter.daysTextSize = daysTextSize;

        adapter.use_RTL_direction = use_RTL_direction;
    }

    private void initMonthViewPager() {
        monthViewPager = (ViewPager) findViewById(R.id.month_view_pager);
        if (use_RTL_direction)
            monthViewPager.setRotationY(180);
        monthPagerAdapter = new CalendarAdapter(getContext(), Config.ViewPagerType.MONTH, this);
        setStyle(monthPagerAdapter);
        monthViewPager.setAdapter(monthPagerAdapter);
        monthViewPager.setCurrentItem(Utils.monthPositionFromDate(Config.INIT_DATE));
        monthViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (Utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (Utils.isMonthView()) {
                    Config.scrollDate = Utils.getDateByMonthPosition(monthViewPager.getCurrentItem());
                    if (Utils.isTheSameMonthToScrollDate(Config.selectionDate))
                        Config.scrollDate = Config.selectionDate.toDateTime();

                    refreshTitleTextView();
                    if (persianHorizontalExpCalListener != null) {
                        persianHorizontalExpCalListener.onCalendarScroll(Config.scrollDate.withDayOfMonth(1));
                        if (needToFireOnDateSelectedEventAfterScrollToDate) {
                            persianHorizontalExpCalListener.onDateSelected(Config.selectionDate);
                            needToFireOnDateSelectedEventAfterScrollToDate = false;
                        }
                    }
                    unlock();
                }
            }
        });
        monthViewPager.setVisibility(Utils.isMonthView() ? VISIBLE : GONE);
    }

    private void initWeekViewPager() {
        weekViewPager = (ViewPager) findViewById(R.id.week_view_pager);
        if (use_RTL_direction)
            weekViewPager.setRotationY(180);
        weekPagerAdapter = new CalendarAdapter(getContext(), Config.ViewPagerType.WEEK, this);
        setStyle(weekPagerAdapter);
        weekViewPager.setAdapter(weekPagerAdapter);
        setWeekViewPagerPosition(Utils.weekPositionFromDate(Config.INIT_DATE), false);
        weekViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (!Utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (!Utils.isMonthView()) {
                    Config.scrollDate = Utils.getDateByWeekPosition(weekViewPager.getCurrentItem());
                    if (Utils.weekPositionFromDate(Config.scrollDate) == Utils.weekPositionFromDate(Config.selectionDate)) {
                        Config.scrollDate = Config.selectionDate;
                    }
                    refreshTitleTextView();
                    if (persianHorizontalExpCalListener != null) {
                        persianHorizontalExpCalListener.onCalendarScroll(Config.scrollDate.withDayOfWeek(1));
                        if (needToFireOnDateSelectedEventAfterScrollToDate){
                            persianHorizontalExpCalListener.onDateSelected(Config.selectionDate);
                            needToFireOnDateSelectedEventAfterScrollToDate = false;
                        }
                    }
                    unlock();
                }
            }
        });
        weekViewPager.setVisibility(!Utils.isMonthView() ? VISIBLE : GONE);
    }

    private void setWeekViewPagerPosition(int position, boolean animate) {
        weekViewPager.setCurrentItem(position, animate);
    }

    private void setMonthViewPagerPosition(int position, boolean animate) {
        monthViewPager.setCurrentItem(position, animate);
    }

    private void refreshTitleTextView() {
        DateTime titleDate = Config.scrollDate;
        if (Config.currentViewPager == Config.ViewPagerType.MONTH) {
            if (Utils.isTheSameMonthToScrollDate(Config.selectionDate)) {
                titleDate = Config.selectionDate;
            }
        } else {
            if (Utils.isTheSameWeekToScrollDate(Config.selectionDate))
                titleDate = Config.selectionDate;
            else
                titleDate = titleDate.minusDays(6);//go to first day of week
        }
        refreshTitleTextView(titleDate);
    }

    private void refreshTitleTextView(DateTime dt) {
        titleTextView.setText(String.format("%s %s", use_RTL_direction ? getMonthString_RTL(dt.getMonthOfYear()) : getMonthString_LTR(dt.getMonthOfYear()), dt.getYear()));
        refreshTodayButtonTitle();
    }

    private void refreshTodayButtonTitle() {
        todayButton.setText(Integer.toString(new DateTime(PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"))).getDayOfMonth()));
    }

    public String getMonthString_RTL(int mothOfYear) {
        switch (mothOfYear) {
            case 1:
                return "فروردین";
            case 2 :
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
            case 2 :
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

    private void lock() {
        lock = true;
    }

    private void unlock() {
        lock = false;
    }

    private boolean isLocked() {
        return lock;
    }

    @Override
    public PersianHorizontalExpCalendar scrollToDate(DateTime dateTime, boolean scrollMonthPager, boolean scrollWeekPager, boolean animate) {
        if (scrollMonthPager)
            setMonthViewPagerPosition(Utils.monthPositionFromDate(dateTime), animate);
        if (scrollWeekPager)
            setWeekViewPagerPosition(Utils.weekPositionFromDate(dateTime), animate);
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
        if (Config.currentViewPager == Config.ViewPagerType.MONTH)
            monthPagerAdapter.updateMarks();
        else
            weekPagerAdapter.updateMarks();
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar changeViewPager(Config.ViewPagerType viewPagerType) {
        if (persianHorizontalExpCalListener != null)
            persianHorizontalExpCalListener.onChangeViewPager(viewPagerType);
        return this;
    }

    @Override
    public PersianHorizontalExpCalendar onDayClick(DateTime dateTime) {
        scrollToDate(dateTime, true);

        Marks.refreshMarkSelected(dateTime,can_mark_selected_day);
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

        void onChangeViewPager(Config.ViewPagerType viewPagerType);
    }

    public PersianHorizontalExpCalendar scrollToDate(DateTime dateTime) {
        return scrollToDate(dateTime, true);
    }

    private PersianHorizontalExpCalendar scrollToDate(DateTime dateTime, boolean animate) {
        if ((Config.currentViewPager == Config.ViewPagerType.MONTH && Utils.isTheSameMonthToScrollDate(dateTime))
                ||
                (Config.currentViewPager == Config.ViewPagerType.WEEK && Utils.isTheSameWeekToScrollDate(dateTime) && Utils.isTheSameMonthToScrollDate(dateTime))){
            if (Config.selectionDate != dateTime){
                Marks.refreshMarkSelected(dateTime,can_mark_selected_day);
                updateMarks();

                Config.selectionDate = dateTime;
                persianHorizontalExpCalListener.onDateSelected(Config.selectionDate);
            }
            return this;
        }

        boolean isMonthView = Utils.isMonthView();
        Marks.refreshMarkSelected(dateTime,can_mark_selected_day);
        updateMarks();
        Config.selectionDate = dateTime;
        needToFireOnDateSelectedEventAfterScrollToDate = true;
        scrollToDate(dateTime, isMonthView, !isMonthView, animate);

        return this;
    }

    public PersianHorizontalExpCalendar setTopContainerBackground(Drawable background){
        RelativeLayout container = (RelativeLayout) findViewById(R.id.top_container);
        container.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setTopContainerBackgroundColor(int color){
        RelativeLayout container = (RelativeLayout) findViewById(R.id.top_container);
        container.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTypeface(Typeface font){
        titleTextView.setTypeface(font);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTypeface(Typeface font){
        todayButton.setTypeface(font);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTextColor(int color){
        titleTextView.setTextColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTextColor(int color){
        todayButton.setTextColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setTitleTextSize(float Size){
        titleTextView.setTextSize(Size);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonTextSize(float Size){
        todayButton.setTextSize(Size);
        return this;
    }

    public PersianHorizontalExpCalendar setTodayButtonBackground(Drawable background){
        todayButton.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setBottomContainerBackground(Drawable background){
        View line_view = (View) findViewById(R.id.line_view);
        line_view.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setBottomContainerBackgroundColor(int color){
        View line_view = (View) findViewById(R.id.line_view);
        line_view.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setExpandImageResource(int resID){
        expandResID = resID;

        ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
        expandImage.setImageResource(ifExpand ? collapseResID : expandResID);

        return this;
    }

    public PersianHorizontalExpCalendar setCollapseImageResource(int resID){
        collapseResID = resID;

        ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
        expandImage.setImageResource(ifExpand ? collapseResID : expandResID);

        return this;
    }

    public PersianHorizontalExpCalendar setCenterContainerBackground(Drawable background){
        RelativeLayout container = (RelativeLayout) findViewById(R.id.center_container);
        container.setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setCenterContainerBackgroundColor(int color){
        RelativeLayout container = (RelativeLayout) findViewById(R.id.center_container);
        container.setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTypeface(Typeface font){
        dayLablesTypeface = font;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysTypeface(Typeface font){
        daysTypeface = font;
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTextColor(int color){
        dayLablesTextColor = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysCurrentMonthTextColor(int color){
        daysTextColorCurrentMonth = color;
        animations.daysTextColorCurrentMonth = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysAnotherMonthTextColor(int color){
        daysTextColorAnotherMonth = color;
        animations.daysTextColorAnotherMonth = color;
        return this;
    }

    public PersianHorizontalExpCalendar setDayLabelsTextSize(int size){
        dayLablesTextSize = size;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysTextSize(int size){
        daysTextSize = size;
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineVisibility(int visibility){
        findViewById(R.id.lable_cell_horizontal_line).setVisibility(visibility);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineBackground(Drawable background){
        findViewById(R.id.lable_cell_horizontal_line).setBackground(background);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineBackgroundColor(int color){
        findViewById(R.id.lable_cell_horizontal_line).setBackgroundColor(color);
        return this;
    }

    public PersianHorizontalExpCalendar setDaysLabelsHorizontalLineHeight(int height){
        View rl = (View) findViewById(R.id.lable_cell_horizontal_line);
        rl.getLayoutParams().height = height;
        return this;
    }

    public PersianHorizontalExpCalendar expand()
    {
        if (!ifExpand) {
            lock();
            ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
            if (Config.currentViewPager != Config.ViewPagerType.MONTH)
                switchToView(Config.ViewPagerType.MONTH);
            unlock();
            expandImage.setImageResource(collapseResID);
        }
        ifExpand = !ifExpand;
        refreshTitleTextView();

        return this;
    }

    public PersianHorizontalExpCalendar collapse()
    {
        if (ifExpand) {
            if (!isLocked()) {
                lock();
                ImageView expandImage = (ImageView) findViewById(R.id.expandImage);
                if (Config.currentViewPager != Config.ViewPagerType.WEEK)
                    switchToView(Config.ViewPagerType.WEEK);
                unlock();
                expandImage.setImageResource(expandResID);
            }
        }
        ifExpand = !ifExpand;
        refreshTitleTextView();

        return this;
    }

    public PersianHorizontalExpCalendar markToday(){
        Marks.markToday();
        return this;
    }

    public PersianHorizontalExpCalendar setMarkTodayCustomGradientDrawable(CustomGradientDrawable customGradientDrawable){
        Config.todayCustomGradientDrawableMark = customGradientDrawable;
        return this;
    }

    public PersianHorizontalExpCalendar setMarkSelectedDateCustomGradientDrawable(CustomGradientDrawable customGradientDrawable){
        Config.selectedCustomGradientDrawableMark = customGradientDrawable;
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, CustomGradientDrawable drawable){
        Marks.refreshCustomMark(dateTime, Marks.CustomMarks.Custom, true, 0, drawable);
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, Marks.CustomMarks customMarks, int color){
        if (customMarks != Marks.CustomMarks.Custom)
            Marks.refreshCustomMark(dateTime, customMarks, true, color, null);
        return this;
    }

    public PersianHorizontalExpCalendar markDate(DateTime dateTime, Marks.CustomMarks customMarks){
        if (customMarks != Marks.CustomMarks.Custom)
            Marks.refreshCustomMark(dateTime, customMarks, true,
                    customMarks == Marks.CustomMarks.SmallOval_Bottom ? Color.parseColor("#AAFF3333") : Color.parseColor("#0095f3"), null);
        return this;
    }

    public PersianHorizontalExpCalendar clearMarks(){
        Marks.clear();
        return this;
    }
}