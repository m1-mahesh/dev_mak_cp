package com.nikvay.drnitingroup.modales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Question {
    public String questionId, paperID;
    public String question;
    public String correctAns;
    public int marks;
    public String status;
    public String imageUrl;
    public String selectedAns = null;
    public String answerDescription = null;
    public boolean isChecked;
    public int level = -1;
    public ArrayList<Question> sectionData = new ArrayList<>();
//    public Map<String,String> options = new HashMap<>();
    public JSONArray options;
    public String getCorrectAns() {
        return correctAns;
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

    public JSONArray getOptions() {
        return options;
    }

    public boolean icContainKey(String option){
        try{
            for(int i=0;i<options.length();i++){
                JSONObject object = options.getJSONObject(i);
                if(object.getString("option_id").equals(option))
                    return true;
            }
        }catch (JSONException e){e.printStackTrace();}
        return false;
    }
    public String getOptionOfId(String id){
        try{
            for(int i=0;i<options.length();i++){
                JSONObject object = options.getJSONObject(i);
                if(object.getString("option_id").equals(id))
                    return object.getString("option_value");
            }
        }catch (JSONException e){e.printStackTrace();}
        return "";
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

    public String getAnswerDescription(){
        return this.answerDescription;
    }
    public void setAnswerDescription(String answerDescription){
        this.answerDescription = answerDescription;
    }
}
