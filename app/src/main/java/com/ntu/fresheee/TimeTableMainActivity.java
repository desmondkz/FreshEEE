package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.text.WordUtils;

public class TimeTableMainActivity extends AppCompatActivity {

    private LinearLayout courseLinearLayout;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_main);

        dialog = new Dialog(TimeTableMainActivity.this);

        courseLinearLayout = (LinearLayout) findViewById(R.id.course_linear_layout);

        String pasteTimetable = getIntent().getStringExtra("pasteTimetable");
        System.out.println(pasteTimetable);
        TimetableParser timetableParser = new TimetableParser();
        timetableParser.buildTimetable(pasteTimetable);

        for(ListUtils.EnumeratedItem<Course> courseEnumeratedItem : ListUtils.enumerate(timetableParser.courses)) {
            Course course = courseEnumeratedItem.item;
            int index = courseEnumeratedItem.index;

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
        }
    }
}