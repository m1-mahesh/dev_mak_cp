package com.mak.classportal.modales;

import java.util.ArrayList;

public class DaysData {

    String day;

    ArrayList<StudentData> attendanceData =new ArrayList<>();

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<StudentData> getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(ArrayList<StudentData> attendanceData) {
        this.attendanceData = attendanceData;
    }

}
