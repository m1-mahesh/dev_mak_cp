package com.mak.classportal.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.NewTestActivity;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.TestsList;
import com.mak.classportal.adapter.ScheduledTestsAdapter;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class AttemptedTestsTabFragment extends Fragment {

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
        FloatingActionButton fab = view.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                NewTestActivity.isTest = true;
//                NewTestActivity.classId = TestsList.CLASS_ID;
//                getActivity().startActivity(new Intent(getContext(), NewTestActivity.class));
//                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

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
                testData.setInstruction(object.getString("test_instructions"));
                testData.setDescription(object.getString("test_description"));
                testData.setDuration(object.getString("test_time_in_mints"));
                testData.setTestStatus("Pending");
                allClassData.add(testData);
            }
            ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(getContext(), allClassData, false, userSession, true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter1);
        }catch (JSONException e){
            e.printStackTrace();
        }
        for (int i=0;i<5;i++){

        }
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
