package com.mohamadian.persianhorizontalexpcalendar.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mohamadian.persianhorizontalexpcalendar.common.Config;
import com.mohamadian.persianhorizontalexpcalendar.common.Utils;
import com.mohamadian.persianhorizontalexpcalendar.view.page.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/27/2017.
 */

public class CalendarAdapter extends PagerAdapter {

    private PageView.PageViewListener pageViewListener;
    private Config.ViewPagerType viewPagerType;
    private List<PageView> visiblePages;
    private Context context;

    public Typeface dayLablesTypeface;
    public int dayLablesTextColor;
    public int dayLablesTextSize;

    public Typeface daysTypeface;
    public int daysTextColorCurrentMonth;
    public int daysTextColorAnotherMonth;
    public int daysTextSize;

    public boolean use_RTL_direction;

    public CalendarAdapter(Context context, Config.ViewPagerType viewPagerType, PageView.PageViewListener pageViewListener) {
        this.pageViewListener = pageViewListener;
        this.viewPagerType = viewPagerType;
        this.visiblePages = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        switch (viewPagerType) {
            case MONTH:
                return Utils.monthPositionFromDate(Config.END_DATE);
            case WEEK:
                return Utils.weekPositionFromDate(Config.END_DATE);
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
        PageView pageView = new PageView(context, viewPagerType, pageViewListener);

        pageView.dayLablesTypeface = dayLablesTypeface;
        pageView.dayLablesTextColor = dayLablesTextColor;
        pageView.dayLablesTextSize = dayLablesTextSize;

        pageView.daysTypeface = daysTypeface;
        pageView.daysTextColorCurrentMonth = daysTextColorCurrentMonth;
        pageView.daysTextColorAnotherMonth = daysTextColorAnotherMonth;
        pageView.daysTextSize = daysTextSize;
        pageView.use_RTL_direction = use_RTL_direction;

        if (use_RTL_direction)
            pageView.setRotationY(180);

        visiblePages.add(pageView);

        container.addView(pageView, 0);

        switch (viewPagerType) {
            case MONTH:
                pageView.setup(Utils.getDateByMonthPosition(position));
                break;
            case WEEK:
                pageView.setup(Utils.getDateByWeekPosition(position));
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
