package com.mak.classportal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.BoardAd;
import com.mak.classportal.adapter.QuestionListAdapter;
import com.mak.classportal.adapter.TestResults;
import com.mak.classportal.modales.BoardData;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuickTestResult extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static ArrayList<Question> mData = new ArrayList<>();
    public static boolean isQuick = false;
    public static String TEST_ID;
    AppSingleTone appSingleTone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_result);
        appSingleTone = new AppSingleTone(this);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Result");
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (isQuick) {
            TestResults adapter1 = new TestResults(this, mData, userSession, false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(adapter1);
        }else {
                getResult();
        }
        ((ImageButton) findViewById(R.id.closeBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.clear();
                finish();
            }
        });

    }

    void parseTestQuestions(JSONObject jsonObject) {
        try {
            mData.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("questions"));
                question.setCorrectAns(object.getString("correct_answer"));
                question.setMarks(object.getInt("questions_marks"));
                question.setSelectedAns(object.getString("given_answer"));
                mData.add(question);
            }
            TestResults adapter1 = new TestResults(this, mData, userSession, true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(adapter1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getResult() {

        try {
            String url = appSingleTone.testResult;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("student_id", userSession.getAttribute("org_id"));
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

}
