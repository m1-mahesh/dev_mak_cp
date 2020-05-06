package com.nikvay.drnitingroup;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.fragment.NoticeStepOneFragment;
import com.nikvay.drnitingroup.fragment.NoticeStepTwoFragment;
import com.nikvay.drnitingroup.modales.StudentClass;
import com.nikvay.drnitingroup.swap_plugin.CustomViewPager;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NewNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Calendar c;
    boolean isCamera;
    ImageView imageView;
    String picturePath = "";
    AppSingleTone appSingleTone;
    CustomViewPager mViewPager;
    ArrayList<StudentClass> classes = new ArrayList<>();
    SharedPreferences sharedPreferences;
    UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice1);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_notice);
        mViewPager =  findViewById(R.id.pager);
        mViewPager.setPagingEnabled(false);
        ((Button) findViewById(R.id.saveButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.prevButton)).setOnClickListener(this);

        getClassDivision();
        /*txtDate = findViewById(R.id.date_edit_text);

        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);

        c = Calendar.getInstance();*/

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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date_edit_text:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.time_edit_text:
                break;
            case R.id.saveButton:
                if (NoticeStepOneFragment.selectedOption == 0){
                    SelectStudents.selectedStudents.clear();
                    FinaliseNotice.NOTICE_TYPE = Constant.NOTICE_TYPE_ALL;
                    startActivityForResult(new Intent(this, FinaliseNotice.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }else if (NoticeStepOneFragment.selectedOption == 1) {
                    if (mViewPager.getCurrentItem() == 0)
                        mViewPager.setCurrentItem(1, true);
                    else {
                        if (!NoticeStepTwoFragment.selectedClass.equals("")) {
                            if (NoticeStepTwoFragment.selectedDivisions.size() > 0) {
                                SelectStudents.selectedStudents.clear();
                                FinaliseNotice.NOTICE_TYPE = Constant.NOTICE_TYPE_DIVISION;
                                FinaliseNotice.CLASS_ID = NoticeStepTwoFragment.selectedClass;
                                startActivityForResult(new Intent(this, FinaliseNotice.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                            } else {
                                showToast("Please Select Division(S)");
                            }
                        } else {
                            showToast("Please Select Class");
                        }
                    }
                }else showToast("Please Select Notice For What");
                break;
            case R.id.prevButton:
                mViewPager.setCurrentItem(0,true);
                break;

        }

    }
    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            if (position == 0) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("classData", classes);
                NoticeStepOneFragment oneStep = new NoticeStepOneFragment();
                oneStep.setArguments(bundle);
                return oneStep;
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("classData", classes);
                NoticeStepTwoFragment oneStep = new NoticeStepTwoFragment();
                oneStep.setArguments(bundle);
                return oneStep;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
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

            mViewPager.setAdapter(new SamplePagerAdapter(
                    getSupportFragmentManager()));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE) {
            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
            finish();
        }
    }
}
