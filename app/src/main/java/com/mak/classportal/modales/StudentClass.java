package com.mak.classportal.modales;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class StudentClass implements Parcelable {
    public String id;
    public String name;
    public String url;
    public Map<String, String> divisions = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getDivisions(){
        return divisions;
    }
    public void addDivision(String id, String name){
        divisions.put(id, name);
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StudentClass){
            StudentClass c = (StudentClass )obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
