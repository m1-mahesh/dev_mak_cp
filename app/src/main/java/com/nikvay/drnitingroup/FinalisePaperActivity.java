package com.nikvay.drnitingroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.permission.PermissionsActivity;
import com.nikvay.drnitingroup.permission.PermissionsChecker;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.FileUtils;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.nikvay.drnitingroup.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.nikvay.drnitingroup.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class FinalisePaperActivity extends AppCompatActivity {

    PermissionsChecker checker;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    AppSingleTone appSingleTone;
    EditText titleText, totalMarks;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    static public Question paperData;
    static public boolean IS_UPDATE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalise_paper);
        appSingleTone = new AppSingleTone(this);
        checker = new PermissionsChecker(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        titleText = findViewById(R.id.title_edit_text);
        totalMarks = findViewById(R.id.total_marks_text);
        if (IS_UPDATE){
            ((TextView)findViewById(R.id.tvTitle)).setText("Update Section");
            titleText.setText(paperData.getQuestion());
            totalMarks.setText(paperData.getAnswerDescription());
        }
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }

        ((Button) findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {
                    if (!IS_UPDATE) {
                        SelectQuestionsActivity.INDEX = 0;
                        SelectQuestionsActivity.apiData.totalMarks = totalMarks.getText().toString();
                        SelectQuestionsActivity.apiData.title = titleText.getText().toString();
                        Constant.IS_PAPER = true;
                        startActivityForResult(new Intent(FinalisePaperActivity.this, SelectQuestionsActivity.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                    }else {
                        updatePaper();
                    }
                }
            }
        });
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
    boolean isValidated() {
        if (titleText.getText().toString().equals("")) {
            showToast("Please Enter Heading");
            return false;
        }
        if (totalMarks.getText().toString().equals("")) {
            showToast("Please Enter Total Marks");
            return false;
        }
        return true;
    }
    public void updatePaper() {

        try {
            String url = appSingleTone.editPaperSection;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", paperData.paperID);
            executeAPI.addPostParam("heading_id", paperData.getQuestionId());
            executeAPI.addPostParam("question_heading", titleText.getText().toString());
            executeAPI.addPostParam("marks", totalMarks.getText().toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("Updated...");
                            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE){
            SelectQuestionsActivity.chapterQuestions.clear();
            SelectQuestionsActivity.subjectData = null;
            SelectQuestionsActivity.apiData = null;
            finish();
        }
    }
}
