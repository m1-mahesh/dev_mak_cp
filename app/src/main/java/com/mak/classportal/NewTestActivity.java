package com.mak.classportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.modales.SubjectData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewTestActivity extends AppCompatActivity {

    public static boolean isTest = false;
    public static String classId = "";
    public static String CLASS_NAME = "";
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    AppCompatSpinner selectClass, selectSubject, selectLevel;
    GridLayout divisionsView;
    TextView classText, selectTxt;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;
    ArrayList<StudentClass> classes = new ArrayList<>();
    ArrayList<SubjectData> subjects = new ArrayList<>();
    public Map<String, String> selectedDivisions = new HashMap<>();
    String selectedClass = "", selectedSubject = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tests);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        appSingleTone = new AppSingleTone(this);
        if (isTest)
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_test);
        else
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_paper);
        ((Button) findViewById(R.id.saveButton)).setText("Next");

        classText = findViewById(R.id.classText);
        selectClass = findViewById(R.id.selectClass);
        selectSubject = findViewById(R.id.selectSubject);
        selectLevel = findViewById(R.id.selectLevel);
        classText.setText(CLASS_NAME);
        selectTxt = findViewById(R.id.selectTxt);
        divisionsView = findViewById(R.id.divisionsLayout);

        if (isTest) {
            selectLevel.setVisibility(View.GONE);
            classText.setVisibility(View.GONE);

        } else {
            classText.setVisibility(View.GONE);

        }
        getClassDivision();
    }
    void spinnerImplementation(boolean isClass){
        if (isClass) {
            ArrayAdapter<StudentClass> adapter = new ArrayAdapter<StudentClass>(this, android.R.layout.simple_spinner_dropdown_item, classes);
            selectClass.setAdapter(adapter);
            selectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StudentClass sClass = (StudentClass) parent.getSelectedItem();
                    selectedClass = sClass.getId();
                    if (!selectedClass.equals("")) {
                        getSubjectChapters();
                        selectTxt.setVisibility(View.VISIBLE);
                        divisionsView.setVisibility(View.VISIBLE);
                        selectSubject.setVisibility(View.VISIBLE);
                    }
                    divisionsView.removeAllViews();

                    for (Map.Entry<String, String> entry : sClass.getDivisions().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        CheckBox checkBox = new CheckBox(NewTestActivity.this);
                        checkBox.setText(value);
                        params.setMargins(10,10,0,0);
                        checkBox.setTypeface(ResourcesCompat.getFont(NewTestActivity.this, R.font.proximanovaregular));
                        checkBox.setLayoutParams(params);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    selectedDivisions.put(key, value);
                                else selectedDivisions.remove(key);

                            }
                        });
                        divisionsView.addView(checkBox);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            ArrayAdapter<SubjectData> spinnerAdapter = new ArrayAdapter<>(NewTestActivity.this, android.R.layout.simple_spinner_item, subjects);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectSubject.setAdapter(spinnerAdapter);
            selectSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectData swt = (SubjectData) parent.getItemAtPosition(position);
                    selectedSubject = swt.getId();
                    if (!selectedSubject.equals("") && selectedDivisions.size()>0) {
                        swt.setClassId(selectedClass);
                        swt.divisions = selectedDivisions;
                        SelectQuestionsActivity.subjectData = swt;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
    private static class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String string, Object tag) {
            this.string = string;
            this.tag = tag;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    void showToast(String toastText) {
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }

    public void next(View view) {
        SelectQuestionsActivity.CLASS_ID = "";
        SelectQuestionsActivity.SUBJECT_ID = "";
        SelectQuestionsActivity.INDEX = 0;
        if (selectedClass.equals(""))
            showToast("Please Select Class");
        else if(selectedDivisions.size() == 0)
            showToast("Please Select Division");
        else if(selectedSubject.equals(""))
            showToast("Please Select Subject");
        else {
            SelectQuestionsActivity.selectedQ.clear();
            startActivity(new Intent(this, SelectQuestionsActivity.class));
            overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            finish();
        }
    }
    void prepareClassData(JSONArray apiResponse){
        try {
            classes.clear();
            SelectQuestionsActivity.divisions.clear();
            StudentClass aClass1 = new StudentClass();
            aClass1.setName("Select Class");
            aClass1.setId("");
            classes.add(aClass1);
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentClass aClass = new StudentClass();
                aClass.setId(classObj.getString("class_id"));
                aClass.setName(classObj.getString("class_name"));
                for (int j =0; j< classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                    SelectQuestionsActivity.divisions.put(obj.getString("division_id"), obj.getString("division_name"));
                }
                classes.add(aClass);
            }
            spinnerImplementation(true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    void prepareSubjectChapter(JSONArray apiResponse){
        try {
            subjects.clear();
            SubjectData sub = new SubjectData();
            sub.setName("Select Subject");
            sub.setId("");
            subjects.add(sub);
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                SubjectData aClass = new SubjectData();
                aClass.setId(classObj.getString("id"));
                aClass.setName(classObj.getString("subject_name"));
                for (int j =0; j< classObj.getJSONArray("chapter_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("chapter_list").getJSONObject(j);
                    aClass.addChapter(obj.getString("id"), obj.getString("chapter_name"));
                }
                Log.e("",aClass.id);
                subjects.add(aClass);
            }
            spinnerImplementation(false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getClassDivision() {

        try {
            String url = appSingleTone.classDivisionList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("class_list")) {
                            JSONArray object = result.getJSONArray("class_list");
                            prepareClassData(object);
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

    public void getSubjectChapters() {

        try {
            String url = appSingleTone.subjectChapterList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", selectedClass);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("subject_list")) {
                            JSONArray object = result.getJSONArray("subject_list");
                            prepareSubjectChapter(object);
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
}
