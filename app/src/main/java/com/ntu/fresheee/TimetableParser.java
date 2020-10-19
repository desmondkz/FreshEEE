package com.ntu.fresheee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TimetableParser {

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

        for (String row : rows) {
            String[] columns = row.split("\\t");

            if (columns.length == 1) {
//                System.out.println("EMPTY ROW");
            } else if (columns.length == 15) {
                currentCourse = new Course();
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

//                System.out.println("NEW COURSE WITH TIMETABLE: " + newCourse.title);
//                System.out.println("NEW " + newClassSlot.type + " WITH TIMETABLE FOR " + newCourse.title);

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
}
