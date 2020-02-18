package com.mak.classportal;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.StudentListAdapter;
import com.mak.classportal.modales.StudentData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TakeAttendance extends AppCompatActivity {

    public static String CLASS_ID = "";
    public static String CLASS_NAME = "";
    public static String DIVISION_ID = "";
    public static String DIVISION_NAME = "";



    ArrayList<StudentData> students = new ArrayList<>();
    TextView topTitle;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static HashMap<String, String> studentAttendance = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        topTitle = findViewById(R.id.tvTitle);
        topTitle.setText("Class: "+CLASS_NAME+"("+ DIVISION_NAME+")");
        mRecyclerView = findViewById(R.id.studentList);
        mRecyclerView.setHasFixedSize(true);
        studentAttendance.clear();
        getStudents();

    }

    void prepareStudents(JSONArray apiResponse){
        try {
            students.clear();
            SelectQuestionsActivity.divisions.clear();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentData studentData = new StudentData();
                studentData.setId(classObj.getString("student_id"));
                studentData.setName(classObj.getString("name"));
                students.add(studentData);
            }
            StudentListAdapter adapter1 = new StudentListAdapter(this,students, true);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getStudents() {

        try {
            String url = appSingleTone.studentListByDivision;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id", DIVISION_ID);
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

    public void submitAttendance(View view){
        if (studentAttendance.size()>0){
            submitAttendance();
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

    public void submitAttendance() {

        try {
            String url = appSingleTone.submitClassAttendance;
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : studentAttendance.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                jsonObject.put(key, value);
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat ff= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
            Date current = ff.parse(new Date().toString());

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id", DIVISION_ID);
            executeAPI.addPostParam("attendances_marked_by_teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("attendance_date", dateFormat.format(current));
            executeAPI.addPostParam("attendance_data_array", jsonObject.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("Data Submitted");
                            finish();
                        }
                    }catch (JSONException e){
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
