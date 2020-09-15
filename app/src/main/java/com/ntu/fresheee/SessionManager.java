package com.ntu.fresheee;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    //Initialize variable
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Create constructor
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("com.ntu.fresheee.users", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    //Create set login method
    public void setLogin(boolean login) {
        editor.putBoolean("keyLogin", login);
        editor.commit();
    }

    //Create get login method
    public boolean getLogin() {
        return sharedPreferences.getBoolean("keyLogin", false);
    }

}
