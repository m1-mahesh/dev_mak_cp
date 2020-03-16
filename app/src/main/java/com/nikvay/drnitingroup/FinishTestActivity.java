package com.nikvay.drnitingroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FinishTestActivity extends AppCompatActivity {

    public static ArrayList<Question> mData = new ArrayList<>();
    TextView testTitle, attemptedQ, durationText;
    int totalQ = 0, totalAttemptedQ = 0;
    JSONObject resultObject;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_test);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        testTitle = findViewById(R.id.testTitle);
        attemptedQ = findViewById(R.id.attemptedQ);
        durationText = findViewById(R.id.testDuration);

        Log.e("Recoreded ", "" + mData.size());
        findViewById(R.id.finishButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuickTestResult.mData = mData;
                QuickTestResult.isQuick = true;
                startActivity(new Intent(FinishTestActivity.this, QuickTestResult.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                finish();
                finish();
            }
        });
        calculateTime();
        filterResult();
        initialiseValue();
        postTestData();
    }

    int totalMarks = 0;
    void filterResult() {
        try {
            resultObject = new JSONObject();
            totalQ = mData.size();
            for (int i = 0; i < mData.size(); i++) {
                Question question = mData.get(i);
                if (question.getSelectedAns() != null) {
                    totalAttemptedQ++;
                    if (question.getSelectedAns()!=null&&question.getSelectedAns().equals(question.getCorrectAns())){
                       totalMarks += question.getMarks();
                    }
                    resultObject.put(question.getQuestionId(), question.getSelectedAns());
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    void initialiseValue() {
        attemptedQ.setText("Attempted QuestionS: " + totalAttemptedQ + "/" + totalQ);
        if (RunTest.testData != null)
            testTitle.setText(RunTest.testData.getTestTitle());
        else
            testTitle.setText("");
    }
    String startTimeStr ="00:00:00";
    String endTimeStr ="00:00:00";
    void calculateTime() {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date());
        long difference = endTime.getTime().getTime()  - Constant.startTime.getTime().getTime();
        long hours = (difference / 3600000) % 24;
        long minutes = (difference / 60000) % 60;
        long seconds = (difference / 1000) % 60;
        hours = (hours < 0 ? -hours : hours);
        String h = ""+hours,m=""+minutes,s=""+seconds;
        if (hours<10)
            h = "0"+hours;
        if (minutes<10)
            m = "0"+minutes;
        if (seconds<10)
            s = "0"+seconds;


        durationText.setText("Duration: " + h + ":"+m+":"+s);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        startTimeStr=sdf.format(Constant.startTime.getTime());
        endTimeStr=sdf.format(endTime.getTime());
    }
    public void postTestData() {

        try {
            String url = appSingleTone.submitTest;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("test_id", RunTest.testData.getId());
            executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("start_time", startTimeStr);
            executeAPI.addPostParam("end_time", endTimeStr);
            executeAPI.addPostParam("question_answer_array", resultObject.toString());
            executeAPI.addPostParam("total_marks_recived", ""+totalMarks);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
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
