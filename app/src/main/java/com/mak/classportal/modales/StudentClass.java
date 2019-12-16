package com.mak.classportal.modales;

public class StudentClass {
    public String name;
    public String url;
    public String[] devisions = new String[4];

    public StudentClass(){
        devisions[0] = "A";
        devisions[1] = "B";
        devisions[2] = "C";
        devisions[3] = "D";
    }
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
}
