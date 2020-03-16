package com.nikvay.drnitingroup.modales;

import java.util.HashMap;
import java.util.Map;

public class Question {
    public String questionId;
    public String question;
    public String correctAns;
    public int marks;
    public String status;
    public String imageUrl;
    public String selectedAns = null;
    public boolean isChecked;
    public Map<String,String> options = new HashMap<>();

    public String getCorrectAns() {
        return correctAns;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSelectedAns() {
        return selectedAns;
    }

    public void setSelectedAns(String selectedAns) {
        this.selectedAns = selectedAns;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, String> getOptions() {
        return options;
    }


    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
