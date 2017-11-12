package com.mohamadian.persianhorizontalexpcalendarexample;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

import java.util.Locale;

public class AnotherActivity extends AppCompatActivity {

    private TextView title;
    private Button todayButton;

    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    private Chronology perChr = PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"));
    private DateTime now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

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
                    public void onChangeViewPager(PersianViewPagerType persianViewPagerType) {

                    }
                });

        title.setText(persianHorizontalExpCalendar.getMonthString_RTL(now.getMonthOfYear())+" "+now.getYear());
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
                persianHorizontalExpCalendar.scrollToDate(now);
            }
        });

        getSupportActionBar().setCustomView(v);
    }

}
