package com.ntu.fresheee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TimetableParser {

    public String pasteTimetable;
    public Course currentCourse;

    public TimetableParser() {

    }

    public void buildTimetable(String pasteTimetable) {
        this.pasteTimetable = pasteTimetable;
//        System.out.println(pasteTimetable);

        String[] splits = pasteTimetable.split("Remark|TOTAL ");
        String timetable = splits[1];
        String[] rows = timetable.split("\\r?\\n");

        for (String row : rows) {
            String[] columns = row.split("\\t");

            if (columns.length == 1) {
                System.out.println("EMPTY ROW");
            } else if (columns.length == 15) {
                currentCourse = new Course();
                currentCourse.course_code = columns[0];
                currentCourse.title = columns[1];
                currentCourse.au = Integer.parseInt(columns[2]);
                currentCourse.course_type = columns[4];
                currentCourse.su_grade_option = columns[5];
                currentCourse.ger_type = columns[6];
                currentCourse.index_number = Integer.parseInt(columns[7]);
                currentCourse.status = columns[8];

                ClassSlot newClassSlot = new ClassSlot();
                newClassSlot.type = columns[10];
                newClassSlot.group = columns[11];
                newClassSlot.day = columns[12];
                newClassSlot.time = columns[13];
                newClassSlot.venue = columns[14];
                newClassSlot.remark = columns[15];
                currentCourse.classSlots.add(newClassSlot);

//                System.out.println("NEW COURSE WITH TIMETABLE: " + newCourse.title);
//                System.out.println("NEW " + newClassSlot.type + " WITH TIMETABLE FOR " + newCourse.title);

            } else if (columns.length == 6) {
                ClassSlot newClassSlot = new ClassSlot();
                newClassSlot.type = columns[0];
                newClassSlot.group = columns[1];
                newClassSlot.day = columns[2];
                newClassSlot.time = columns[3];
                newClassSlot.venue = columns[4];
                newClassSlot.remark = columns[5];

                currentCourse.classSlots.add(newClassSlot);
            } else {
                System.out.println("UNKNOWN CASE");
            }

        }

    }
}
