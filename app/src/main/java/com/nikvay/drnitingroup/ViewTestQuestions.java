package com.nikvay.drnitingroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.ViewQuestionListAd;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewTestQuestions extends AppCompatActivity {

    RecyclerView mRecyclerView1;
    public static ArrayList<Question> selectedQ = new ArrayList<>();
    public static NoticeData apiData;
    public static boolean IS_VIEW_CREATED_TEST = false;
    public static boolean IS_TEST = false;

    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static String TEST_ID;
    AppSingleTone appSingleTone;
    RelativeLayout levelOfQuestionView;
    TextView questionLevelText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (IS_VIEW_CREATED_TEST){
            setContentView(R.layout.activity_quick_result);
            mRecyclerView1 = findViewById(R.id.scheduledTest);
            ((TextView)findViewById(R.id.tvTitle)).setText("Questions");
            mRecyclerView1.setHasFixedSize(true);
            getTestQuestions();
            ((ImageButton) findViewById(R.id.closeBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedQ.clear();
                    finish();
                }
            });
        }else {
            setContentView(R.layout.activity_select_questions);
            levelOfQuestionView = findViewById(R.id.levelOfQuestionView);
            questionLevelText = findViewById(R.id.questionLevelText);
            findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.IS_PAPER) {
                        if (Constant.ADD_Q_IN_PAPER)
                            editAddQuestion(apiData.getId());
                        else
                            addQuestion(apiData.getId());
                    }else
                        startActivityForResult(new Intent(ViewTestQuestions.this, FinaliseTest.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.final_q);
            mRecyclerView1 = findViewById(R.id.questionList);
            parseQuestionLevelWise();
            questionLevelText.setText(" Easy: "+easyCount+" Medium: "+mediumCount+" Hard: "+hardCount);
            mRecyclerView1.setHasFixedSize(true);
            ViewQuestionListAd adapter1 = new ViewQuestionListAd(this, selectedQ, true);
            mRecyclerView1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            levelOfQuestionView.setVisibility(View.VISIBLE);
            mRecyclerView1.setAdapter(adapter1);
            ((Button) findViewById(R.id.saveButton)).setText("Proceed");
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        }
    }
    int mediumCount = 0, hardCount = 0, easyCount = 0;
    void parseQuestionLevelWise(){

        for (int i=0;i<selectedQ.size();i++){
            Question question = selectedQ.get(i);
            if (question.level == 1)
                easyCount ++;
            else if (question.level == 2)
                mediumCount ++;
            else if (question.level == 3)
                hardCount ++;
        }
    }
    void parseTestQuestions(JSONObject jsonObject) {
        try {
            selectedQ.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("online_test_question_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("questions"));
                JSONArray options = object.getJSONArray("options");
                question.options = options;
                /*for(int k=0;k<options.length();k++){
                    JSONObject op = options.getJSONObject(k);
                    question.options.put(op.getString("option_id"),op.getString("option_value"));
                }*/
                question.setCorrectAns(object.getString("answer_id"));
                question.setStatus(object.getString("status"));
                question.setImageUrl(object.getString("image"));
                selectedQ.add(question);
            }
            ViewQuestionListAd adapter1 = new ViewQuestionListAd(this, selectedQ, true);
            mRecyclerView1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mRecyclerView1.setAdapter(adapter1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getTestQuestions() {

        try {
            String url = appSingleTone.attendTestQ;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("test_id", TEST_ID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestQuestions(result);
                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE) {
            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
            finish();
        }
    }
    public void addQuestion(String test_id) {
        try {
            String url = appSingleTone.addPaperQuestions;
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<selectedQ.size();i++){
                jsonArray.put(selectedQ.get(i).getQuestionId());
            }
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", apiData.getId());
            executeAPI.addPostParam("question_heading", apiData.getTitle());
            executeAPI.addPostParam("marks", apiData.totalMarks);
            executeAPI.addPostParam("question_id_array", jsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            SelectQuestionsActivity.chapterQuestions.clear();
                            SelectQuestionsActivity.subjectData = null;
                            ViewTestQuestions.selectedQ.clear();
                            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void editAddQuestion(String test_id) {
        try {
            String url = appSingleTone.editPaperAddQuestions;
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<selectedQ.size();i++){
                jsonArray.put(selectedQ.get(i).getQuestionId());
            }
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", apiData.getId());
            executeAPI.addPostParam("heading_id", Constant.headQuestion.getQuestionId());
            executeAPI.addPostParam("question_id_array", jsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            SelectQuestionsActivity.chapterQuestions.clear();
                            SelectQuestionsActivity.subjectData = null;
                            ViewTestQuestions.selectedQ.clear();
                            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
