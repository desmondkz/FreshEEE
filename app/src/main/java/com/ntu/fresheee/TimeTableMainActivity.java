package com.ntu.fresheee;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ntu.fresheee.ui.main.SectionsPagerAdapter;

public class TimeTableMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        String pasteTimetable = getIntent().getStringExtra("pasteTimetable");
        System.out.println(pasteTimetable);
        TimetableParser timetableParser = new TimetableParser();
        timetableParser.buildTimetable(pasteTimetable);

        for (Course course : timetableParser.courses) {
            System.out.println(course.index_number);
        }


    }

}