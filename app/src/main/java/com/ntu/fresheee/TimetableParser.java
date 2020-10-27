package com.ntu.fresheee;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class TimetableParser implements Serializable{

    public String pasteTimetable;
    public Course currentCourse;
    public List<Course> courses = new ArrayList<>();

    public TimetableParser() {

    }

    private static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void buildTimetable(String pasteTimetable) {

        this.pasteTimetable = pasteTimetable;
//        System.out.println(pasteTimetable);

        String[] splits = pasteTimetable.split("Remark|TOTAL ");
        String timetable = splits[1];
        String[] rows = timetable.split("\\r?\\n");
        List<String> courseColours = Arrays.asList("#845EC2", "#D65DB1", "#FF6F91", "#FF9671", "#FFC75F", "#F9F871", "#2C73D2", "#008F7A", "#00C9A7");

        for (String row : rows) {
            String[] columns = row.split("\\t");

            if (columns.length == 1) {
//                System.out.println("EMPTY ROW");
            } else if (columns.length == 15) {
                currentCourse = new Course();
                currentCourse.color = courseColours.get(courses.size());
                courses.add(currentCourse);
                currentCourse.course_code = columns[0];
                currentCourse.title = columns[1];
                currentCourse.au = (tryParse(columns[2]) == null) ? -1 : Integer.parseInt(columns[2]);
                currentCourse.course_type = columns[3];
                currentCourse.su_grade_option = columns[4];
                currentCourse.ger_type = columns[5];
                currentCourse.index_number = (tryParse(columns[6]) == null) ? -1 : Integer.parseInt(columns[6]);
                currentCourse.status = columns[7];

                ClassSlot newClassSlot = new ClassSlot();
                currentCourse.classSlots.add(newClassSlot);
                newClassSlot.type = columns[9];
                newClassSlot.group = columns[10];
                newClassSlot.day = columns[11];
                newClassSlot.time = columns[12];
                newClassSlot.venue = columns[13];
                newClassSlot.remark = columns[14];

            } else if (columns.length == 6) {
                ClassSlot newClassSlot = new ClassSlot();
                currentCourse.classSlots.add(newClassSlot);
                newClassSlot.type = columns[0];
                newClassSlot.group = columns[1];
                newClassSlot.day = columns[2];
                newClassSlot.time = columns[3];
                newClassSlot.venue = columns[4];
                newClassSlot.remark = columns[5];
            } else {
//                System.out.println("UNKNOWN CASE");
            }
        }
    }

    public void buildTimetableFromFirebase(String userID) {
        DatabaseReference timetableReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("timetable");

        timetableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot timetableSnapshot: snapshot.getChildren()) {
                    Course course = timetableSnapshot.getValue(Course.class);
                    courses.add(course);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }
        });
    }
}
