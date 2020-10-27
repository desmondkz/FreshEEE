package com.ntu.fresheee;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String fullName, email;
    public List<Course> timetable = new ArrayList<>();

    public User() {

    }

    public User(String fullName, String email) {

        this.fullName = fullName;
        this.email = email;
    }
}
