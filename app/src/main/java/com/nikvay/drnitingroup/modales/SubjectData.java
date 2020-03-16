package com.nikvay.drnitingroup.modales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubjectData {
    public String id;
    public String name;
    public String url;
    public String classId;
    public String divisionId;
    public Map<String, String> chapters = new HashMap<>();
    public Map<String, String> divisions = new HashMap<>();
    ArrayList<SubjectData> chaptersList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public void setId(String id) {
        this.id = id;
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

    public void addChapter(String id, String name){
        chapters.put(id, name);
    }

    public Map<String, String> getChapters(){
        return chapters;
    }
    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StudentClass){
            SubjectData c = (SubjectData ) obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }

        return false;
    }

    public ArrayList<SubjectData> getChaptersList() {
        return chaptersList;
    }

    public void setChaptersList(ArrayList<SubjectData> chaptersList) {
        this.chaptersList = chaptersList;
    }
}
