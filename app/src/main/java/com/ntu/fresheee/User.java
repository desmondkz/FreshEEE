package com.ntu.fresheee;

public class User {

    public String email, fullname;

    public User(){

    }

    public User(String fullname, String email) {
//      this is class to store user information
        this.fullname = fullname;
        this.email = email;
    }
}
