package com.mak.classportal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class FinaliseTest extends AppCompatActivity implements View.OnClickListener {

    EditText txtDate, txtTime, titleET, durationET, totalMarksET, instructionET;
    Calendar c;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;
    private int mYear, mMonth, mDay, mHour, mMinute;
    JSONArray jsonArray = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalise_test);

        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.finalise_test);
        txtDate = findViewById(R.id.date_edit_text);
        txtTime = findViewById(R.id.time_edit_text);
        titleET = findViewById(R.id.title_edit_text);
        durationET = findViewById(R.id.duration_text);
        totalMarksET = findViewById(R.id.total_marks_text);
        instructionET = findViewById(R.id.instructionET);
        txtTime.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);
        txtDate.setTag(txtTime.getKeyListener());
        txtTime.setKeyListener(null);
        c = Calendar.getInstance();
        findViewById(R.id.saveButton).setOnClickListener(this);
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

    boolean isValidated() {
        if (titleET.getText().toString().equals("")) {
            showToast("Please Enter Test Title");
            return false;
        }
        if (txtDate.getText().toString().equals("")) {
            showToast("Please Select Date For Test");
            return false;
        }
        if (txtTime.getText().toString().equals("")) {
            showToast("Please Select Time For Test");
            return false;
        }
        if (durationET.getText().toString().equals("")) {
            showToast("Please Enter Test Duration in Minutes");
            return false;
        }
        if (totalMarksET.getText().toString().equals("")) {
            showToast("Please Enter Test Total Marks");
            return false;
        }
        if (instructionET.getText().toString().equals("")) {
            showToast("Please Enter Test Instructions");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_edit_text:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.time_edit_text:
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;
            case R.id.saveButton:
                //showToast("New Test Created Successfully");
                if (isValidated())
                    createNewTest();
                //finish();
                break;

        }

    }

    //    teacher_id,org_id,class_id,division_id,test_name,test_description,test_time_in_mints,test_date,test_time,test_instructions
    public void createNewTest() {

        try {
            String url = appSingleTone.createTest;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", SelectQuestionsActivity.subjectData.getClassId());
            executeAPI.addPostParam("division_id", SelectQuestionsActivity.subjectData.getDivisionId());
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("test_name", titleET.getText().toString());
            executeAPI.addPostParam("test_description", titleET.getText().toString());
            executeAPI.addPostParam("test_time_in_mints", durationET.getText().toString());
            executeAPI.addPostParam("test_date", txtDate.getText().toString());
            executeAPI.addPostParam("test_time", txtTime.getText().toString());
            executeAPI.addPostParam("test_instructions", instructionET.getText().toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("Created");
                            addQuestion(result.getString("test_id"));
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
    public void addQuestion(String test_id) {

        try {
            String url = appSingleTone.addTestQuestions;
            for (int i=0;i<ViewTestQuestions.selectedQ.size(); i++){
                jsonArray.put(ViewTestQuestions.selectedQ.get(i).getQuestionId());
            }
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("test_id", test_id);
            executeAPI.addPostParam("question_id_array", jsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("Created");
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
