package com.ntu.fresheee;

import java.io.Serializable;

public class ClassSlot implements Serializable {
    public String type;
    public String group;
    public String day;
    public String time;
    public String venue;
    public String remark;

    public ClassSlot() {

    }

    public ClassSlot(String type,  String group,  String day,  String time,
                  String venue,  String remark) {

        this.type = type;
        this.group = group;
        this.day = day;
        this.time = time;
        this.venue = venue;
        this.remark = remark;
    }
}
