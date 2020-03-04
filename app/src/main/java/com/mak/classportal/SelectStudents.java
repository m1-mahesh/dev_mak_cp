package com.mak.classportal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.StudentListAdapter;
import com.mak.classportal.fragment.NoticeStepTwoFragment;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.modales.StudentData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectStudents extends AppCompatActivity {

    ArrayList<StudentData> students = new ArrayList<>();
    TextView topTitle;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    public static ArrayList<String> selectedStudents = new ArrayList<>();
    public static String classId="", divisionId="";
    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_students);
        topTitle = findViewById(R.id.tvTitle);
        topTitle.setText("Select Student(s)");
        appSingleTone = new AppSingleTone(this);
        selectedStudents.clear();
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        mRecyclerView = findViewById(R.id.studentList);
        mRecyclerView.setHasFixedSize(true);
        getStudents();

    }
    public void saveStudents(View view){
        FinaliseNotice.NOTICE_TYPE = Constant.NOTICE_TYPE_STUDENTS;
        NoticeStepTwoFragment.selectedDivisions.clear();
        startActivityForResult(new Intent(this, FinaliseNotice.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
    }

    void prepareStudents(JSONArray apiResponse){
        try {
            students.clear();
            SelectQuestionsActivity.divisions.clear();
            StudentData aClass1 = new StudentData();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentData studentData = new StudentData();
                studentData.setId(classObj.getString("student_id"));
                studentData.setName(classObj.getString("name"));
                students.add(studentData);
            }
            StudentListAdapter adapter1 = new StudentListAdapter(this,students, false);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    void showToast(String toastText){
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }
    public void getStudents() {

        try {
            String url = appSingleTone.studentListByDivision;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", classId);
            executeAPI.addPostParam("division_id", divisionId);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("student_list")) {
                            JSONArray object = result.getJSONArray("student_list");
                            prepareStudents(object);
                        } else {
                            showToast("Something went wrong, Please try again later");
                        }
                    } catch (JSONException e) {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE) {
            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
            finish();
        }
    }
}
