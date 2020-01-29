package com.mak.classportal;

import androidx.annotation.Nullable;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.QuestionListAdapter;
import com.mak.classportal.adapter.SubjectListAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.SubjectData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectQuestionsActivity extends AppCompatActivity {

    public static String SUBJECT_ID = "";
    public static String CHAPTER_ID = "";
    public static String CLASS_ID = "";
    public static int INDEX = 0;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView, mRecyclerView1;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    public static HashMap<String, ArrayList<Question>> chapterQuestions = new HashMap<>();
    TextView totalQText, viewText;
    public static SubjectData subjectData;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (INDEX == 0) {
            prepareData();
            setContentView(R.layout.fragment_chapters);
            mRecyclerView = findViewById(R.id.chapterList);
            totalQText = findViewById(R.id.questionText);
            viewText = findViewById(R.id.viewText);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_CHAPTER);
            mRecyclerView.setHasFixedSize(true);
            SubjectListAdapter adapter1 = new SubjectListAdapter(this,allClassData);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(adapter1);
        }else{
            setContentView(R.layout.activity_select_questions);
            mRecyclerView = findViewById(R.id.questionList);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView.setHasFixedSize(true);
            if(chapterQuestions.containsKey(CHAPTER_ID)){
                QuestionListAdapter.CHAPTER_ID = CHAPTER_ID;
                QuestionListAdapter adapter1 = new QuestionListAdapter(this,chapterQuestions.get(CHAPTER_ID), false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mRecyclerView.setAdapter(adapter1);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.notifyDataSetChanged();
                    }
                });
            }else {
                getChapterQuestions();
            }

            findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveQuestion();
                }
            });
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
    void prepareData(){
        allClassData = new ArrayList<>();
        if (subjectData!=null) {
            for (Map.Entry<String, String> entry : subjectData.getChapters().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                HomeMenu aClass = new HomeMenu();
                aClass.setName(value);
                aClass.setId(key);
                allClassData.add(aClass);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (totalQText!=null)
            totalQText.setText("Total Questions: "+ selectedQ.size());

    }
    static ArrayList<Question> selectedQ = new ArrayList<>();
    public void saveQuestion(){
        showToast("Questions Added");
        selectedQ.clear();
        for (Map.Entry<String,ArrayList<Question>> entry : chapterQuestions.entrySet()){
            for (int j=0;j<entry.getValue().size(); j++){
                Question q=entry.getValue().get(j);
                if(q.isChecked)
                    selectedQ.add(q);
            }
        }
        finish();
    }
    public void viewQuestion(View view){
        if (selectedQ.size()>0) {
            ViewTestQuestions.selectedQ = selectedQ;
            startActivityForResult(new Intent(SelectQuestionsActivity.this, ViewTestQuestions.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
            overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE){
            SelectQuestionsActivity.chapterQuestions.clear();
            SelectQuestionsActivity.subjectData = null;
            finish();
        }
    }

    public static ArrayList<Question> mData = new ArrayList<>();


    void parseTestQuestions(JSONObject jsonObject) {
        if (!chapterQuestions.containsKey(CHAPTER_ID)) {
            chapterQuestions.put(CHAPTER_ID, new ArrayList<>());
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("online_test_question_list");
            for (int i = 0; i < jsonArray.length(); i++) {
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
                chapterQuestions.get(CHAPTER_ID).add(question);
            }
            QuestionListAdapter.CHAPTER_ID = CHAPTER_ID;
            QuestionListAdapter adapter1 = new QuestionListAdapter(this,chapterQuestions.get(CHAPTER_ID), false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter1.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getChapterQuestions() {

        try {
            String url = appSingleTone.questionList;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", subjectData.getClassId());
            executeAPI.addPostParam("division_id", subjectData.getDivisionId());
            executeAPI.addPostParam("chapter_id", CHAPTER_ID);
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
}
