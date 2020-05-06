package com.nikvay.drnitingroup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.InstructionsAd;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FinaliseTest extends AppCompatActivity implements View.OnClickListener {

    EditText txtDate, txtTime, titleET, durationET, wrongMarksET, correctMarksET, txtEndTime;
    Calendar c;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;
    RecyclerView mRecyclerView;
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
        txtEndTime = findViewById(R.id.end_time_edit_text);
        titleET = findViewById(R.id.title_edit_text);
        durationET = findViewById(R.id.duration_text);
        wrongMarksET = findViewById(R.id.total_marks_text);
        correctMarksET = findViewById(R.id.correct_marks_text);
        mRecyclerView = findViewById(R.id.instructionList);
        txtTime.setOnClickListener(this);
        txtEndTime.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);
        txtDate.setTag(txtTime.getKeyListener());
        txtTime.setKeyListener(null);
        c = Calendar.getInstance();
        findViewById(R.id.saveButton).setOnClickListener(this);
        filterResult();
        getInstructions();
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
    void filterResult(){
        for (int i=0;i<ViewTestQuestions.selectedQ.size(); i++){
            Question q = ViewTestQuestions.selectedQ.get(i);
            jsonArray.put(q.getQuestionId());
//            totalMarks+=Integer.parseInt(correctMarksET.getText().toString());
        }
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
        if (correctMarksET.getText().toString().equals("")) {
            showToast("Please Enter Correct Answer Marks");
            return false;
        }
        if (wrongMarksET.getText().toString().equals("")) {
            showToast("Please Enter Wrong Answer Marks");
            return false;
        }
        if (durationET.getText().toString().equals("")) {
            showToast("Please Enter Test Duration in Minutes");
            return false;
        }

        if (selectedInstructions.size()==0) {
            showToast("Please Select instructions");
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
                                monthOfYear ++;
                                String month = monthOfYear<10?"0"+monthOfYear:""+monthOfYear;
                                String day = dayOfMonth<10?"0"+dayOfMonth:""+dayOfMonth;
                                txtDate.setText(year + "-" + month  + "-" + day);

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
                                String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                                if (hourOfDay == 0) {
                                    hourOfDay += 12;
                                    am_pm = "AM";
                                }
                                else if (hourOfDay == 12) {
                                    am_pm = "PM";
                                }
                                else if (hourOfDay > 12) {
                                    hourOfDay -= 12;
                                    am_pm = "PM";
                                }
                                else {
                                    am_pm = "AM";
                                }

                                String hr = hourOfDay<10?"0"+hourOfDay:""+hourOfDay;
                                String minuteStr = minute<10?"0"+minute:""+minute;

                                txtTime.setText(hr + ":" + minuteStr+":"+am_pm);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
                break;
            case R.id.end_time_edit_text:
                Log.e("End","Time");
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                                if (hourOfDay == 0) {
                                    hourOfDay += 12;
                                    am_pm = "AM";
                                }
                                else if (hourOfDay == 12) {
                                    am_pm = "PM";
                                }
                                else if (hourOfDay > 12) {
                                    hourOfDay -= 12;
                                    am_pm = "PM";
                                }
                                else {
                                    am_pm = "AM";
                                }

                                String hr = hourOfDay<10?"0"+hourOfDay:""+hourOfDay;
                                String minuteStr = minute<10?"0"+minute:""+minute;

                                txtEndTime.setText(hr + ":" + minuteStr+":"+am_pm);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog1.show();
                break;
            case R.id.saveButton:
                //showToast("New Test Created Successfully");
                if (isValidated())
                    createNewTest();
                //finish();
                break;

        }

    }


    public int timeDiff(String startTime, String endTime) {
        int hoursDiff = 0;
        SimpleDateFormat format = new SimpleDateFormat("hh:mma");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(startTime);
            date2 = format.parse(endTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date2.getTime() - date1.getTime();
        hoursDiff = (int) difference / (1000 * 60 * 60);
        return hoursDiff;
    }

    public void createNewTest() {

        try {
            String url = appSingleTone.createTest;
            JSONArray jsonArray1 = new JSONArray();
            for (Map.Entry<String, String> entry : SelectQuestionsActivity.subjectData.divisions.entrySet()) {
                String key = entry.getKey();
                jsonArray1.put(key);
            }
            JSONArray instructionArray = new JSONArray();
            for (Map.Entry<String, Boolean> entry : selectedInstructions.entrySet()) {
                String key = entry.getKey();
                instructionArray.put(key);
            }
            totalMarks = (jsonArray.length() * Integer.parseInt(correctMarksET.getText().toString()));
            Log.e("Array:", jsonArray.toString());
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", SelectQuestionsActivity.subjectData.getClassId());
            executeAPI.addPostParam("division_id", jsonArray1.toString());
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("test_name", titleET.getText().toString());
            executeAPI.addPostParam("test_description", titleET.getText().toString());
            executeAPI.addPostParam("test_time_in_mints", durationET.getText().toString());
            executeAPI.addPostParam("test_date", txtDate.getText().toString());
            executeAPI.addPostParam("test_time", txtTime.getText().toString());
            executeAPI.addPostParam("test_end_time", txtEndTime.getText().toString());
            executeAPI.addPostParam("test_instructions", instructionArray.toString());
            executeAPI.addPostParam("wrong_ans_marks", wrongMarksET.getText().toString());
            executeAPI.addPostParam("correct_ans_marks", correctMarksET.getText().toString());
            executeAPI.addPostParam("total_marks", ""+totalMarks);
            executeAPI.addPostParam("subject_id", SelectQuestionsActivity.subjectData.getId());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("New Test Created");
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
    int totalMarks = 0;
    public void addQuestion(String test_id) {

        try {
            String url = appSingleTone.addTestQuestions;
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
//                            showToast("New Test Created");
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
            InstructionsAd adapter1 = new InstructionsAd(this, instructions, true, false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void getInstructions() {

        try {
            String url = appSingleTone.getTestInstructionList;

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

}
