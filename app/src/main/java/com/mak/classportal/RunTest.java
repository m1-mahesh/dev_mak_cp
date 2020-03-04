package com.mak.classportal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.swap_plugin.CustomPagerAdapter;
import com.mak.classportal.swap_plugin.CustomViewPager;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunTest extends AppCompatActivity implements View.OnClickListener {

    public static TestData testData;
    static int step = 0;
    static ArrayList<Question> mData = new ArrayList<>();
    TextView customToast, questionCount;
    LayoutInflater inflater;
    View tostLayout;
    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    TextView countDownText;
    String selectedOption = "";
    String preOption = "";
    int questionCountInt = 0;
    CustomViewPager viewPager;
    CountDownTimer timerCD;
    private Button mButtonLeft, mButtonRight;
    Button mSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (step == 0) {
            setContentView(R.layout.activity_run_test_first);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    step = 1;
                    RunTest.mData = mData;
                    RunTest.testData = testData;
                    startActivity(new Intent(RunTest.this, RunTest.class));
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                    finish();
                }
            }, 8000);
            getTestQuestions();

        } else {
            setContentView(R.layout.activity_run_test);
            countDownText = findViewById(R.id.countDownText);
            viewPager = findViewById(R.id.viewPager);
            viewPager.setAdapter(new CustomPagerAdapter(this, mData));
            viewPager.setPagingEnabled(false);
            mButtonLeft = findViewById(R.id.buttonSwipeLeft);
            mButtonRight = findViewById(R.id.buttonSwipeRight);
            questionCount = findViewById(R.id.questionCount);
            mSkipButton = findViewById(R.id.skipButton);
            mButtonLeft.setOnClickListener(this);
            mButtonRight.setOnClickListener(this);
            mSkipButton.setOnClickListener(this);


            startTimer(TimeUnit.MINUTES.toMillis(Integer.parseInt(testData.getDuration())));

        }
    }

    private void startTimer(long noOfMinutes) {
        Constant.startTime = Calendar.getInstance();
        Constant.startTime.setTime(new Date());

        timerCD = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countDownText.setText(hms);//set text
            }

            public void onFinish() {
                countDownText.setText("TIME'S UP!!"); //On finish change timer text
                FinishTestActivity.mData = CustomPagerAdapter.mData;
                startActivity(new Intent(RunTest.this, FinishTestActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                showToast("Test Completed...");
                finish();
            }
        }.start();
        questionCount.setText("01/" + mData.size());
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else if (v.equals(mButtonRight) || v.equals(mSkipButton)) {
            Log.e("--", "" + viewPager.getCurrentItem());
            if (mData.size() == viewPager.getCurrentItem() + 1) {
                confirmDialog();
            }else if (mData.size() == viewPager.getCurrentItem() ){
                mSkipButton.setVisibility(View.GONE);
            }
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

        }
        questionCount.setText((viewPager.getCurrentItem() + 1) + "/" + mData.size());
    }

    private void confirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Do you want to submit test?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FinishTestActivity.mData = CustomPagerAdapter.mData;
                        startActivity(new Intent(RunTest.this, FinishTestActivity.class));
                        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                        timerCD.cancel();
                        showToast("Test Completed...");
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

   /* @Override
    public void onViewSwipedToRight(int position) {
        Question swipedElement = mAdapter.getItem(position);
        swipedElement.setSelectedAns(selectedOption);
        FinishTestActivity.userTestData.put(swipedElement.getQuestionId(), selectedOption);
        preOption = selectedOption;
        selectedOption = "";

    }

    @Override
    public void onViewSwipedToLeft(int position) {
        Question swipedElement = mAdapter.getItem(position);
    }

    @Override
    public void onStackEmpty() {
        //Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RunTest.this, FinishTestActivity.class));
        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        showToast("Test Completed...");
        finish();
    }*/

    void showToast(String toastText) {
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(tostLayout);
        toast.show();
    }

    void parseTestQuestions(JSONObject jsonObject) {
        mData.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("online_test_question_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("questions"));
                JSONArray options = object.getJSONArray("options");
                for(int k=0;k<options.length();k++){
                   JSONObject op = options.getJSONObject(k);
                    question.options.put(op.getString("option_id"),op.getString("option_value"));
                }
                question.setCorrectAns(object.getString("answer_id"));
                question.setMarks(object.getInt("questions_marks"));
                question.setStatus(object.getString("status"));
                question.setImageUrl(object.getString("image"));
                mData.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTestQuestions() {

        try {
            String url = appSingleTone.attendTestQ;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("test_id", testData.getId());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestQuestions(result);
                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(false);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }


}
