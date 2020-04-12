package com.nikvay.drnitingroup.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.TestsList;
import com.nikvay.drnitingroup.adapter.ScheduledTestsAdapter;
import com.nikvay.drnitingroup.modales.TestData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class ActiveTestsTabFragment extends Fragment {

    private ListAdapter mAdapter;
    ArrayList<TestData> allClassData = new ArrayList<>();
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_test_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(
                R.id.activeTests);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences =  getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        getTestList();


        return view;
    }
    void parseTestList(JSONObject jsonObject){
        try {
            allClassData.clear();
            Log.e("", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("online_test_list");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                TestData testData =new TestData();
                testData.setTestClass(object.getString("class_name"));
                testData.setId(object.getString("id"));
                testData.setTestTitle(object.getString("test_name"));
                testData.setTestDate(object.getString("test_date"));
                testData.setTestTime(object.getString("test_time"));
                try {
                    testData.wrongMarks = object.getInt("wrong_ans_marks");
                    testData.correctMarks = object.getInt("correct_ans_marks");
                }catch (Exception e){e.printStackTrace();}
                if(object.has("instruction_array"))
                    testData.instructionArray = object.getJSONArray("instruction_array");
                testData.setDescription(object.getString("test_description"));
                testData.setDuration(object.getString("test_time_in_mints"));
                testData.setClassName(object.getString("class_name"));
                testData.setClassId(object.getString("class_id"));
                testData.totalQuestions = object.getInt("num_of_question");
                testData.endTime = object.getString("test_end_time");
                testData.setTestStatus("Pending");
                if (userSession.isStudent()) {
                    if (object.has("total_marks") && !object.getString("total_marks").equals("null"))
                        testData.setTotalMarks(object.getInt("total_marks"));
                    if (object.has("result_data") && !object.getJSONObject("result_data").getString("total_marks_recived").equals("null"))
                        testData.setGainMarks(object.getJSONObject("result_data").getInt("total_marks_recived"));
                    testData.isTestAttempt = object.getBoolean("isTestAttempt");

                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat ff= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
                        Date date = dateFormat.parse(testData.getTestDate());
                        Date current = ff.parse(new Date().toString());
                        current = dateFormat.parse(dateFormat.format(current));

                        if (!testData.isTestAttempt && date.equals(current))
                            allClassData.add(testData);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat ff= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
                        Date date = dateFormat.parse(testData.getTestDate());
                        Date current = ff.parse(new Date().toString());
                        current = dateFormat.parse(dateFormat.format(current));

                        if (date.equals(current))
                            allClassData.add(testData);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            boolean viewResultForTeacher = false;
            if (userSession.isTeacher())
                viewResultForTeacher = true;
            ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(getContext(), allClassData, viewResultForTeacher, userSession, Constant.TAB_INDEX_0);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter1);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userSession!=null&&appSingleTone!=null)
            getTestList();
    }

    public void getTestList() {

        try {
            String url = null;
            if (userSession.isStudent())
                url = appSingleTone.testList;
            else
                url = appSingleTone.teacherTestList;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            Log.e("Org id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", TestsList.CLASS_ID);
            executeAPI.addPostParam("division_id",TestsList.DIVISION_ID);
            if (userSession.isTeacher())
                executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            else
                executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestList(result);
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
