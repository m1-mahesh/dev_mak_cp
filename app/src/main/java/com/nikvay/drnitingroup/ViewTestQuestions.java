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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.ViewQuestionListAd;
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
    public static boolean IS_VIEW_CREATED_TEST = false;

    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static String TEST_ID;
    AppSingleTone appSingleTone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (IS_VIEW_CREATED_TEST){
            setContentView(R.layout.activity_quick_result);
            appSingleTone = new AppSingleTone(this);
            mRecyclerView1 = findViewById(R.id.scheduledTest);
            ((TextView)findViewById(R.id.tvTitle)).setText("Questions");
            sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
            userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
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
            findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.IS_PAPER)
                        startActivity(new Intent(ViewTestQuestions.this, FinalisePaperActivity.class));
                    else
                        startActivityForResult(new Intent(ViewTestQuestions.this, FinaliseTest.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.final_q);
            mRecyclerView1 = findViewById(R.id.questionList);
            mRecyclerView1.setHasFixedSize(true);
            ViewQuestionListAd adapter1 = new ViewQuestionListAd(this, selectedQ, true);
            mRecyclerView1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

            mRecyclerView1.setAdapter(adapter1);
            ((Button) findViewById(R.id.saveButton)).setText("Proceed");
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
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
                for(int k=0;k<options.length();k++){
                    JSONObject op = options.getJSONObject(k);
                    question.options.put(op.getString("option_id"),op.getString("option_value"));
                }
                question.setCorrectAns(object.getString("answer_id"));
                question.setMarks(object.getInt("questions_marks"));
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

}
