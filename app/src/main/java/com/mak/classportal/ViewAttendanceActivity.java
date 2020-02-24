package com.mak.classportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.StudentAttendanceTabAd;
import com.mak.classportal.modales.DaysData;
import com.mak.classportal.modales.StudentData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ViewAttendanceActivity extends AppCompatActivity {

    public static ArrayList<DaysData> attendanceList = new ArrayList<>();
    public static String CLASS_ID = "11", DIVISION_ID = "7", DIVISION_NAME = "", CLASS_NAME = "";
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    Typeface opensanssemibold;
    Calendar c = Calendar.getInstance();
    ArrayList<StudentData> studentDataList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView dateInputEditText;
    ImageView imageView;
    LinearLayout notFoundView;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        appSingleTone = new AppSingleTone(this);
        opensanssemibold = ResourcesCompat.getFont(this, R.font.opensanssemibold);
        dateInputEditText = findViewById(R.id.dateInputEdit);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        imageView = findViewById(R.id.selectDateImage);
        recyclerView = findViewById(R.id.attendanceView);
        notFoundView = findViewById(R.id.notFoundView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(null, true);
            }
        });

        //getClassAttendance("2020-02-19");
    }

    void openDialog() {
        final Dialog dialog = new Dialog(ViewAttendanceActivity.this, R.style.Dialog);
        dialog.setContentView(R.layout.attendance_date);
        dialog.setTitle("Attendance Date");
        c = Calendar.getInstance();
        // set the custom dialog components - text, image and button
        EditText editText = dialog.findViewById(R.id.dateInput);

        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button okButton = dialog.findViewById(R.id.okButton);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    checkAttendanceStatus(editText.getText().toString());
                    dialog.dismiss();
                }else showToast("Please Select Date");

            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(editText, false);
            }
        });

        dialog.show();
    }

    void datePicker(EditText editText, boolean isSync) {
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ViewAttendanceActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        monthOfYear++;
                        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                        String dateStr = year + "-" + month + "-" + day;
                        if (editText != null)
                            editText.setText(dateStr);
                        else dateInputEditText.setText(dateStr);
                        if (isSync) {
                            getClassAttendance(dateStr);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    void parseTestList(JSONObject jsonObject) {
        try {
            studentDataList.clear();
            Log.e("", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("student_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                StudentData studentData = new StudentData();
                studentData.setId(object.getString("student_id"));
                studentData.setName(object.getString("name"));
                studentData.setAttendanceStatus(object.getInt("attendance_status"));
                studentDataList.add(studentData);
            }
            Collections.sort(studentDataList, new Comparator<StudentData>() {
                @Override
                public int compare(StudentData o1, StudentData o2) {
                    int first = Integer.parseInt(o1.getId());
                    int second = Integer.parseInt(o2.getId());
                    if (first > second) {
                        return 1;
                    } else if (first < second) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            StudentAttendanceTabAd adapter1 = new StudentAttendanceTabAd(ViewAttendanceActivity.this, studentDataList);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            recyclerView.setAdapter(adapter1);
            if (studentDataList.size() == 0)
                notFoundView.setVisibility(View.VISIBLE);
            else notFoundView.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getClassAttendance(String date) {

        try {
            String url = appSingleTone.attendanceByClass;
            ExecuteAPI executeAPI = new ExecuteAPI(ViewAttendanceActivity.this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("date", date);
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id", DIVISION_ID);
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

    public void checkAttendanceStatus(String date) {

        try {
            String url = appSingleTone.attendanceStatusByClass;
            ExecuteAPI executeAPI = new ExecuteAPI(ViewAttendanceActivity.this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("date", date);
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("division_id", DIVISION_ID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    if (result.has("attendance_status")){
                        try {
                            if (result.getInt("attendance_status") == 0){
                                TakeAttendance.CLASS_ID = CLASS_ID;
                                TakeAttendance.DIVISION_ID = DIVISION_ID;
                                TakeAttendance.DIVISION_NAME = DIVISION_NAME;
                                TakeAttendance.CLASS_NAME = CLASS_NAME;
                                TakeAttendance.ATTENDANCE_DATE = date;
                                startActivity(new Intent(ViewAttendanceActivity.this, TakeAttendance.class));
                                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                            }else showToast("Already Attendance Data Submitted For "+ date);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else showToast("Something went wrong, Please try again");
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
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    void showToast(String toastText){
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(tostLayout);
        toast.show();
    }
}
