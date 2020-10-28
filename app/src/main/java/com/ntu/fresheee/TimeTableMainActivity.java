package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.text.WordUtils;
import com.github.tlaabs.timetableview.TimetableView;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.BuildConfig;
import com.github.tlaabs.timetableview.HighlightMode;
import com.github.tlaabs.timetableview.SaveManager;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Sticker;
import com.github.tlaabs.timetableview.*;

public class TimeTableMainActivity extends AppCompatActivity {

    private LinearLayout courseLinearLayout, classSlotLinearLayout;
    Dialog dialog;
    private String userID;
    private DatabaseReference reference;
    private TimetableView timetableView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_main);

        getSupportActionBar().setTitle("My Timetable");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(TimeTableMainActivity.this);

        courseLinearLayout = (LinearLayout) findViewById(R.id.course_linear_layout);

        TimetableParser timetableParser = (TimetableParser) getIntent().getSerializableExtra("timetableParser");

        timetableView = (TimetableView) findViewById(R.id.weekview_timetable);



//        schedule.setClassPlace("TR+96");
//        schedule.setDay(1);
//        schedule.setStartTime(new Time(7,30));
//        schedule.setEndTime(new Time(15,30));
//        schedules.add(schedule);
//        timetableView.add(schedules);

        for (Course course:timetableParser.courses) {
            ArrayList<Schedule> schedules = new ArrayList<Schedule>();
            for (ClassSlot classSlot: course.classSlots) {
                Schedule schedule = new Schedule();
                schedule.setClassTitle(course.course_code + "\n" + classSlot.type);
                schedule.setClassPlace(classSlot.venue);
                schedule.setDay(classSlot.intWeekDay);
                schedule.setStartTime(new Time(classSlot.startHour,classSlot.startMin));
                schedule.setEndTime(new Time(classSlot.endHour,classSlot.endMin));
                schedules.add(schedule);
            }

            View currentCourseCardView = View.inflate(TimeTableMainActivity.this, R.layout.course_card, null);

            ((TextView) currentCourseCardView.findViewById(R.id.course_title)).setText(course.title);
            ((TextView) currentCourseCardView.findViewById(R.id.course_title)).setTextColor(Color.parseColor(course.color));
            ((TextView) currentCourseCardView.findViewById(R.id.course_code)).setText(course.course_code);
            ((TextView) currentCourseCardView.findViewById(R.id.course_index_number)).setText(course.index_number == -1 ? "" : Integer.toString(course.index_number));
            ((TextView) currentCourseCardView.findViewById(R.id.course_au)).setText("(" + Integer.toString(course.au) + "AU)");

            currentCourseCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View coursePopupDialog = View.inflate(TimeTableMainActivity.this, R.layout.course_popup_dialog, null);
                    ((TextView) coursePopupDialog.findViewById(R.id.popup_course_title)).setText(WordUtils.capitalizeFully(course.title));
                    ((TextView) coursePopupDialog.findViewById(R.id.popup_course_code)).setText(course.course_code);
                    ((TextView) coursePopupDialog.findViewById(R.id.popup_course_index_number)).setText(course.index_number == -1 ? "N.A." : Integer.toString(course.index_number));
                    ((TextView) coursePopupDialog.findViewById(R.id.popup_course_au)).setText(Integer.toString(course.au));

                    classSlotLinearLayout = (LinearLayout) coursePopupDialog.findViewById(R.id.class_slot_linear_layout);

                    if (course.classSlots.size() > 0) {
                        for (ClassSlot classSlot:course.classSlots) {
                            View classSlotRelativeLayout = View.inflate(TimeTableMainActivity.this, R.layout.class_slot, null);
                            ((TextView) classSlotRelativeLayout.findViewById(R.id.class_type)).setText(classSlot.type);
                            ((TextView) classSlotRelativeLayout.findViewById(R.id.class_day)).setText(classSlot.day);
                            ((TextView) classSlotRelativeLayout.findViewById(R.id.class_time)).setText(classSlot.time);
                            ((TextView) classSlotRelativeLayout.findViewById(R.id.class_venue)).setText(classSlot.venue);
                            classSlotLinearLayout.addView(classSlotRelativeLayout);
                        }
                    }


                    dialog.setContentView(coursePopupDialog);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.popup_course_background));
                    }
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    dialog.show();
                }
            });

            courseLinearLayout.addView(currentCourseCardView);
            timetableView.add(schedules);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Confirm Delete?");
                builder.setMessage("You will need to generate your timetable again.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference("Users");
                        userID = fbuser.getUid();

                        //Delete user's timetable
                        reference.child(userID).child("timetable").setValue(null);

                        startActivity(new Intent(TimeTableMainActivity.this, TimeTableIntroActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}