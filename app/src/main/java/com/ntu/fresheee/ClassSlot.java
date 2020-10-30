package com.ntu.fresheee;

import java.io.Serializable;

public class ClassSlot implements Serializable {
    public String type;
    public String group;
    public String day;
    public String time;
    public String venue;
    public String remark;
    public String teachingweek;
    public int startHour;
    public int startMin;
    public int endHour;
    public int endMin;
    public int intWeekDay;


    public ClassSlot() {

    }

    public ClassSlot(String type,  String group,  String day,  String time,
                  String venue,  String remark, String teachingweek, int startHour, int startMin,
                     int endHour, int endMin, int intWeekDay) {

        this.type = type;
        this.group = group;
        this.day = day;
        this.time = time;
        this.venue = venue;
        this.remark = remark;
        this.teachingweek = teachingweek;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
        this.intWeekDay = intWeekDay;

    }
}
