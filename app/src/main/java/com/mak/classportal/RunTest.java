package com.mak.classportal;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.swap_plugin.SwipeStack;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RunTest extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {

    static int step = 0;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    private Button mButtonLeft, mButtonRight;
    static ArrayList<Question> mData = new ArrayList<>();
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    public static TestData testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone =  new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (step == 0) {
            setContentView(R.layout.activity_run_test_first);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    step = 1;
                    RunTest.mData = mData;
                    startActivity(new Intent(RunTest.this, RunTest.class));
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                    finish();
                }
            }, 8000);
            getTestQuestions();

        } else {
            setContentView(R.layout.activity_run_test);
            mSwipeStack = findViewById(R.id.swipeStack);
            mButtonLeft = findViewById(R.id.buttonSwipeLeft);
            mButtonRight = findViewById(R.id.buttonSwipeRight);

            mButtonLeft.setOnClickListener(this);
            mButtonRight.setOnClickListener(this);

            mAdapter = new SwipeStackAdapter(mData);
            mSwipeStack.setAdapter(mAdapter);
            mSwipeStack.setListener(this);

        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
            selectedOption = preOption;
        } else if (v.equals(mButtonRight)) {
            if (!selectedOption.equals(""))
                mSwipeStack.swipeTopViewToRight();
            else showToast("Please Select Option");
        } /*else if (v.equals(mFab)) {
            mData.add(getString(R.string.dummy_fab));
            mAdapter.notifyDataSetChanged();
        }*/
    }

    String selectedOption = "";
    String preOption = "";

    @Override
    public void onViewSwipedToRight(int position) {
        Question swipedElement = mAdapter.getItem(position);
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
    void parseTestQuestions(JSONObject jsonObject){
        mData.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("online_test_question_list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("questions"));
                question.options.add(object.getString("optionA"));
                question.options.add(object.getString("optionB"));
                question.options.add(object.getString("optionC"));
                question.options.add(object.getString("optionD"));
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
            String url = appSingleTone.questionList;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            Log.e("Org id", ""+userSession.getInt("org_id"));
            executeAPI.addPostParam("org_id", ""+userSession.getInt("org_id"));
            executeAPI.addPostParam("class_id", userSession.getAttribute("class_id"));
            executeAPI.addPostParam("division_id","1");
            executeAPI.addPostParam("chapter_id","1");
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
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public class SwipeStackAdapter extends BaseAdapter {

        View view;
        private List<Question> mData;

        public SwipeStackAdapter(List<Question> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Question getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.question_card, parent, false);
            }
            TextView textViewCard = view.findViewById(R.id.textViewCard);
            Question question = mData.get(position);
            textViewCard.setText(question.getQuestion());
            final RadioButton one = view.findViewById(R.id.one);
            final RadioButton two = view.findViewById(R.id.two);
            final RadioButton three = view.findViewById(R.id.three);
            final RadioButton four = view.findViewById(R.id.four);
            one.setText(question.getOptions().get(0));
            two.setText(question.getOptions().get(1));
            three.setText(question.getOptions().get(2));
            four.setText(question.getOptions().get(3));
            RadioGroup optionGroup = view.findViewById(R.id.optionView);
            optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.one:
                            one.setBackgroundResource(R.drawable.redio_selected);
                            two.setBackgroundResource(R.drawable.layout_border);
                            three.setBackgroundResource(R.drawable.layout_border);
                            four.setBackgroundResource(R.drawable.layout_border);
                            selectedOption = question.getOptions().get(0);
                            break;
                        case R.id.two:
                            one.setBackgroundResource(R.drawable.layout_border);
                            two.setBackgroundResource(R.drawable.redio_selected);
                            three.setBackgroundResource(R.drawable.layout_border);
                            four.setBackgroundResource(R.drawable.layout_border);
                            selectedOption = question.getOptions().get(1);
                            break;
                        case R.id.three:
                            one.setBackgroundResource(R.drawable.layout_border);
                            two.setBackgroundResource(R.drawable.layout_border);
                            three.setBackgroundResource(R.drawable.redio_selected);
                            four.setBackgroundResource(R.drawable.layout_border);
                            selectedOption = question.getOptions().get(2);
                            break;
                        case R.id.four:
                            one.setBackgroundResource(R.drawable.layout_border);
                            two.setBackgroundResource(R.drawable.layout_border);
                            three.setBackgroundResource(R.drawable.layout_border);
                            four.setBackgroundResource(R.drawable.redio_selected);
                            selectedOption = question.getOptions().get(3);
                            break;
                    }
                }
            });

            return view;
        }
    }
}
