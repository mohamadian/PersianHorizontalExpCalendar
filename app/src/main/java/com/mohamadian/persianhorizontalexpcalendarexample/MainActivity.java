package com.mohamadian.persianhorizontalexpcalendarexample;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.common.Config;
import com.mohamadian.persianhorizontalexpcalendar.common.Marks;
import com.mohamadian.persianhorizontalexpcalendar.view.cell.CustomGradientDrawable;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private Button todayButton;

    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    private Chronology perChr = PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"));
    private DateTime now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeRTL(this);
        now = new DateTime(perChr);
        setCustomActionBar();


        persianHorizontalExpCalendar = (PersianHorizontalExpCalendar)findViewById(R.id.persianCalendar);

        persianHorizontalExpCalendar
                .setPersianHorizontalExpCalListener(new PersianHorizontalExpCalendar.PersianHorizontalExpCalListener() {
                    @Override
                    public void onCalendarScroll(DateTime dateTime) {
                        title.setText(persianHorizontalExpCalendar.getMonthString_RTL(dateTime.getMonthOfYear())+" "+dateTime.getYear());
                    }

                    @Override
                    public void onDateSelected(DateTime dateTime) {
                        title.setText(persianHorizontalExpCalendar.getMonthString_RTL(dateTime.getMonthOfYear())+" "+dateTime.getYear());
                    }

                    @Override
                    public void onChangeViewPager(Config.ViewPagerType viewPagerType) {

                    }
                });

        title.setText(persianHorizontalExpCalendar.getMonthString_RTL(now.getMonthOfYear())+" "+now.getYear());
        setOnClicks();
    }

    public void setOnClicks(){
        findViewById(R.id.mark_some_days).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markSomeDays();
            }
        });
        findViewById(R.id.custom_mark_today_selected_days).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cutomMarkTodaySelectedDay();
            }
        });
        findViewById(R.id.clear_marks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMarks();
            }
        });

        findViewById(R.id.scroll_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToToday();
            }
        });
        findViewById(R.id.scroll_next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToNextMonth();
            }
        });
        findViewById(R.id.scroll_specific_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToSpecificDate();
            }
        });

        findViewById(R.id.custom_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBackground();
            }
        });
    }

    public void markSomeDays(){
        persianHorizontalExpCalendar
                .markDate(new DateTime(perChr).plusDays(7),
                        new CustomGradientDrawable(GradientDrawable.RECTANGLE, Color.BLACK)
                                .setViewLayoutSize(ViewGroup.LayoutParams.MATCH_PARENT,10)
                                .setViewLayoutGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                                .setcornerRadius(5)
                                .setTextColor(Color.BLUE))

                .markDate(new DateTime(perChr).plusDays(10),
                        new CustomGradientDrawable(GradientDrawable.OVAL, Color.BLACK)
                                .setViewLayoutSize(20,20)
                                .setViewLayoutGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
                                .setTextColor(Color.BLUE))

                .markDate(new DateTime(1396,8,7,0,0,perChr), Marks.CustomMarks.VerticalLine_Right, Color.parseColor("#b4e391"))

                .markDate(new DateTime(1396,8,5,0,0,perChr),
                        new CustomGradientDrawable(GradientDrawable.OVAL, new int[] {Color.parseColor("#35b4e391"), Color.parseColor("#5561c419"), Color.parseColor("#35b4e391")})
                                .setstroke(1,Color.parseColor("#62E200"))
                                .setcornerRadius(20)
                                .setTextColor(Color.parseColor("#000000")))

                .markDate(new DateTime(1396,8,15,0,0,perChr),
                        new CustomGradientDrawable(GradientDrawable.OVAL, Color.parseColor("#35a677bd"))
                                .setstroke(1,Color.parseColor("#a677bd")))

                .markDate(new DateTime(1396,8,23,0,0,perChr), Marks.CustomMarks.SmallOval_Bottom, Color.GREEN)
                .markDate(new DateTime(perChr).plusDays(14), Marks.CustomMarks.SmallOval_Bottom)
                .markDate(new DateTime(perChr).plusDays(15), Marks.CustomMarks.VerticalLine_Right)
                .updateMarks();

        scrollToToday();
    }

    public void cutomMarkTodaySelectedDay(){
        persianHorizontalExpCalendar
                .setMarkTodayCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[] {Color.parseColor("#55fefcea"), Color.parseColor("#55f1da36"), Color.parseColor("#55fefcea")})
                        .setstroke(2,Color.parseColor("#EFCF00"))
                        .setTextColor(Color.parseColor("#E88C02")))

                .setMarkSelectedDateCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[] {Color.parseColor("#55f3e2c7"), Color.parseColor("#55b68d4c"), Color.parseColor("#55e9d4b3")})
                        .setstroke(2,Color.parseColor("#E89314"))
                        .setTextColor(Color.parseColor("#E88C02")))
                .updateMarks();

        scrollToToday();
    }

    public void clearMarks(){
        persianHorizontalExpCalendar
                .clearMarks()
                .markToday()
                .updateMarks();

        scrollToToday();
    }

    public void scrollToToday(){
        persianHorizontalExpCalendar
                .scrollToDate(now);
    }

    public void scrollToNextMonth(){
        persianHorizontalExpCalendar
                .scrollToDate(now.plusMonths(1).plusDays(3));
    }

    public void scrollToSpecificDate(){
        DateTime dt = new DateTime(1396,12,1,0,0,0,0,perChr);
        persianHorizontalExpCalendar
                .scrollToDate(dt);
    }

    public void customBackground(){
        persianHorizontalExpCalendar
                .setCenterContainerBackgroundColor(Color.parseColor("#a50095f3"))
                .setDaysLabelsHorizontalLineBackgroundColor(Color.parseColor("#0095f3"))
                .setBottomContainerBackgroundColor(Color.parseColor("#a50095f3"));
    }

    public void makeRTL(AppCompatActivity activity)
    {
        if (android.os.Build.VERSION.SDK_INT >= 17)
        {
            Configuration configuration = activity.getResources().getConfiguration();
            configuration.setLayoutDirection(new Locale("fa"));
            activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
        }
    }


    private void setCustomActionBar(){
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.myactionbar, null);

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/GanjNamehSans-Regular.ttf");
        title = (TextView)v.findViewById(R.id.title);
        title.setText(this.getTitle());
        title.setTypeface(font);

        todayButton = (Button) v.findViewById(R.id.today_button);
        todayButton.setText(Integer.toString(now.getDayOfMonth()));
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToToday();
            }
        });

        getSupportActionBar().setCustomView(v);

    }

}
