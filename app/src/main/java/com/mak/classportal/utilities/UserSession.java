package com.mak.classportal.utilities;

import android.content.SharedPreferences;

/**
 * Created by ambrosial on 15/6/17.
 */

public class UserSession {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor  editor;
    static final String STUDENT_ROLE="Student";
    static final String TEACHER_ROLE="Teacher";
    static final String ADMIN_ROLE="Admin";

    public UserSession(SharedPreferences sharedPreferences, SharedPreferences.Editor editor){
        this.sharedPreferences=sharedPreferences;
        this.editor=editor;
    }
    public void setAttribute(String key, String value){
        editor.putString(key,value);
        editor.commit();
    }

    public void setLong(String key, long value){
        editor.putLong(key,value);
        editor.commit();
    }

    public String getAttribute(String key){
        return  sharedPreferences.getString(key,"");
    }

    public long getLong(String key){
        return  sharedPreferences.getLong(key,0);
    }

    public int getInt(String key){
        return  sharedPreferences.getInt(key,0);
    }

    public void setInt(String key, int value){
        editor.putInt(key,value);
        editor.commit();
    }
    public boolean getBoolean(String key){
        return  sharedPreferences.getBoolean(key,false);
    }

    public void setBoolean(String key, boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }
    public boolean isStudent(){
        if (this.getAttribute("userRole").equals(STUDENT_ROLE))
            return true;
        else return false;
    }
    public boolean isTeacher(){
        if (this.getAttribute("userRole").equals(TEACHER_ROLE))
            return true;
        else return false;
    }
    public void userLogout(){
        editor.clear();
        editor.commit();
    }
}
