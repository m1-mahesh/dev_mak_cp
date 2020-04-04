package com.nikvay.drnitingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.InstructionsAd;
import com.nikvay.drnitingroup.fragment.PaperListFragment;
import com.nikvay.drnitingroup.modales.HomeMenu;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.StudentClass;
import com.nikvay.drnitingroup.modales.SubjectData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NewPaperActivity extends AppCompatActivity implements View.OnClickListener {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    AppCompatSpinner selectClass, selectSubject;
    TextView classText;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;
    ArrayList<StudentClass> classes = new ArrayList<>();
    ArrayList<SubjectData> subjects = new ArrayList<>();
    String selectedClass = "", selectedSubject = "";
    CheckBox easyCheckBox, mediumCheckBox, hardCheckBox;
    RecyclerView mRecyclerView;
    EditText txtDate, txtTime, titleET, durationET, totalMarksET;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_paper);

        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        appSingleTone = new AppSingleTone(this);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_paper);
        ((Button) findViewById(R.id.saveButton)).setText("Next");
        mRecyclerView = findViewById(R.id.instructionList);
        classText = findViewById(R.id.classText);
        selectClass = findViewById(R.id.selectClass);
        selectSubject = findViewById(R.id.selectSubject);
        easyCheckBox = findViewById(R.id.easy);
        mediumCheckBox = findViewById(R.id.medium);
        hardCheckBox = findViewById(R.id.hard);
        txtDate = findViewById(R.id.date_edit_text);
//        txtTime = findViewById(R.id.time_edit_text);
        titleET = findViewById(R.id.title_edit_text);
        durationET = findViewById(R.id.duration_text);
        totalMarksET = findViewById(R.id.total_marks_text);
//        txtTime.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);
        txtDate.setTag(txtDate.getKeyListener());
//        txtTime.setKeyListener(null);
        getClassDivision();
        getInstructions();
    }

    void spinnerImplementation(boolean isClass) {
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
                        selectSubject.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            ArrayAdapter<SubjectData> spinnerAdapter = new ArrayAdapter<>(NewPaperActivity.this, android.R.layout.simple_spinner_item, subjects);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectSubject.setAdapter(spinnerAdapter);
            selectSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectData swt = (SubjectData) parent.getItemAtPosition(position);
                    selectedSubject = swt.getId();
                    if (!selectedSubject.equals("")) {
                        swt.setClassId(selectedClass);
                        SelectQuestionsActivity.subjectData = swt;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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
        if (!isValidated())
            return;
        else {
            createNewPaper();
        }
    }

    void prepareClassData(JSONArray apiResponse) {
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
                for (int j = 0; j < classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                    SelectQuestionsActivity.divisions.put(obj.getString("division_id"), obj.getString("division_name"));
                }
                classes.add(aClass);
            }
            spinnerImplementation(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void prepareSubjectChapter(JSONArray apiResponse) {
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
                for (int j = 0; j < classObj.getJSONArray("chapter_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("chapter_list").getJSONObject(j);
                    aClass.addChapter(obj.getString("id"), obj.getString("chapter_name"));
                }
                Log.e("", aClass.id);
                subjects.add(aClass);
            }
            spinnerImplementation(false);
        } catch (JSONException e) {
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
    public static Map<String, Boolean> selectedInstructions = new HashMap<>();
    ArrayList<NoticeData> instructions = new ArrayList<>();
    void parseInstructions(JSONArray jsonArray){

        try {
            instructions.clear();
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                NoticeData notice = new NoticeData();
                notice.setId(object.getString("id"));
                notice.setTitle(object.getString("title"));
                instructions.add(notice);
            }
            Collections.sort(instructions, new Comparator<NoticeData>() {
                @Override
                public int compare(NoticeData o1, NoticeData o2) {
                    try {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            InstructionsAd adapter1 = new InstructionsAd(this, instructions);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
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

    public void getInstructions() {

        try {
            String url = appSingleTone.getInstructionList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.getInt("error_code") == 200) {
                            JSONArray object = result.getJSONArray("instruction_list");
                            parseInstructions(object);
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
    Map<String, Boolean> levels = new HashMap<>();
    Calendar c = Calendar.getInstance();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easy:
                levels.put("1", easyCheckBox.isChecked());
                break;
            case R.id.medium:
                levels.put("2", mediumCheckBox.isChecked());
                break;
            case R.id.hard:
                levels.put("3", hardCheckBox.isChecked());
                break;
            case R.id.date_edit_text:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear ++;
                                String month = monthOfYear<10?"0"+monthOfYear:""+monthOfYear;
                                String day = dayOfMonth<10?"0"+dayOfMonth:""+dayOfMonth;
                                txtDate.setText(year + "-" + month  + "-" + day);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    boolean isValidated() {

        if (titleET.getText().toString().equals("")) {
            showToast("Please Enter Title");
            return false;
        }
        if (selectClass.equals("")) {
            showToast("Please Select Class");
            return false;
        }
        if (txtDate.getText().toString().equals("")) {
            showToast("Please Select Date");
            return false;
        }

        if (durationET.getText().toString().equals("")) {
            showToast("Please Enter Duration in Hours");
            return false;
        }

        if (totalMarksET.getText().toString().equals("")) {
            showToast("Please Enter Total Marks");
            return false;
        }

        if (totalMarksET.getText().toString().equals("")) {
            showToast("Please Enter Total Marks");
            return false;
        }

        if (selectedSubject.equals("")) {
            showToast("Please Select Subject");
            return false;
        }

        if (selectedInstructions.size()==0) {
            showToast("Please Paper instructions");
            return false;
        }
        return true;
    }

    public void createNewPaper() {
        try {
            String url = appSingleTone.createNewPaper;
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, Boolean> entry : selectedInstructions.entrySet()) {
                String key = entry.getKey();
                jsonArray.put(key);
            }
            Log.e("Array:", jsonArray.toString());
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", selectedClass);
            executeAPI.addPostParam("subject_id", selectedSubject);
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("title", titleET.getText().toString());
            executeAPI.addPostParam("total_marks", totalMarksET.getText().toString());
            executeAPI.addPostParam("exam_time_hr", durationET.getText().toString());
            executeAPI.addPostParam("exam_date", txtDate.getText().toString());
            executeAPI.addPostParam("instruction_array", jsonArray.toString());

            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("New Paper Created");
                            PaperListFragment.IS_ADD = true;
                            //startActivity(new Intent(NewPaperActivity.this, FinalisePaperActivity.class));
                            //overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
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
