package com.mak.classportal.modales;

import java.util.ArrayList;

public class DaysData {

    String day;

    ArrayList<StudentData> appointmentData=new ArrayList<>();

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<StudentData> getAppointmentData() {
        return appointmentData;
    }

    public void setAppointmentData(ArrayList<StudentData> appointmentData) {
        this.appointmentData = appointmentData;
    }

}
