package com.ntu.fresheee;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course  implements Serializable{

    public String course_code;
    public String title;
    public int au;
    public String course_type;
    public String su_grade_option;
    public String ger_type;
    public int index_number;
    public String status;
    public List<ClassSlot> classSlots = new ArrayList<ClassSlot>();
    public String color;

    public Course() {

    }

    public Course(String course_code,  String title,  int au,  String course_type,
                  String su_grade_option,  String ger_type,  int index_number,
                  String status, String color) {

        this.course_code = course_code;
        this.title = title;
        this.au = au;
        this.course_type = course_type;
        this.su_grade_option = su_grade_option;
        this.ger_type = ger_type;
        this.index_number = index_number;
        this.status = status;
        this.color = color;

    }

}
